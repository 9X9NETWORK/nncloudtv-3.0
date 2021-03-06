package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnProgramDao;
import com.nncloudtv.dao.TitleCardDao;
import com.nncloudtv.dao.YtProgramDao;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.PoiEvent;
import com.nncloudtv.model.PoiPoint;
import com.nncloudtv.model.TitleCard;
import com.nncloudtv.model.YtProgram;

@Service
public class NnProgramManager {
    
    protected static final Logger log = Logger.getLogger(NnProgramManager.class
                                              .getName());
    
    private NnProgramDao dao = new NnProgramDao();
    
    public NnProgram create(NnEpisode episode, NnProgram program) {
        
        program = save(program);
        
        // non-specified sub-position
        if (program.getSubSeqInt() == 0) {
            reorderEpisodePrograms(episode.getId());
        }
        
        // episode.setDuration(0); // set 0 to notify episode get operation to recalculate duration.                
        return program;
    }
    
    public void create(NnChannel channel, NnProgram program) {        
        Date now = new Date();
        program.setCreateDate(now);
        program.setUpdateDate(now);
        program.setChannelId(channel.getId());
        dao.save(program);
        resetCache(channel.getId());
        
        //!!!!! clean plus "hook, auto share to facebook"
        //set channel count
        int count = channel.getCntEpisode() + 1;
        channel.setCntEpisode(count);
        NnChannelManager channelMngr = new NnChannelManager();
        channelMngr.save(channel);

        //if the channel's original programCount is zero, its count will not be in the category, adding it now.
        if (count == 1) {
            SysTagDisplayManager displayMngr = new SysTagDisplayManager();
            displayMngr.addChannelCounter(channel);
        }
        this.autoShare(program);
    } 

    /**
     * Save programs massively, and keep updateDate untouched
     * why? processCache() takes too much time when saving individually
     * 
     * @param programs
     * @return programs
     */
    public List<NnProgram> save(List<NnProgram> programs) {
        
        List<Long> channelIds = new ArrayList<Long>();
        
        for (NnProgram program : programs) {            
            Date now = new Date();
            if (program.getCreateDate() == null)
                program.setCreateDate(now);
            if (program.getUpdateDate() == null) {
                program.setUpdateDate(now);
            }            
            
            if (channelIds.indexOf(program.getChannelId()) < 0) {
                channelIds.add(program.getChannelId());
            }
        }
        
        programs = dao.saveAll(programs);
        
        log.info("channel count = " + channelIds.size());
        for (Long channelId : channelIds) {
            resetCache(channelId);
        }
        
        return programs;
    }
    
    public NnProgram save(NnProgram program) {
        
        if (program == null) {
            return program;
        }
        
        Date now = new Date();
        
        if (program.getCreateDate() == null)
            program.setCreateDate(now);
        
        program.setUpdateDate(now);
        program = dao.save(program);
        
        resetCache(program.getChannelId());
        
        return program;
    }
    
    public void delete(NnProgram program) {
        
        if (program == null) {
            return;
        }
        
        // delete titleCards
        TitleCardManager titleCardMngr = new TitleCardManager();
        List<TitleCard> titleCards = titleCardMngr.findByProgramId(program.getId());
        titleCardMngr.delete(titleCards);
        
        // delete poiPoints at program level
        PoiPointManager pointMngr = new PoiPointManager();
        List<PoiPoint> points = pointMngr.findByProgram(program.getId());
        if (points != null && points.size() > 0) {
            pointMngr.delete(points);
        }
        
        long cId = program.getChannelId();
        dao.delete(program);
        resetCache(cId);
    }
    
    public void delete(List<NnProgram> programs) {
        
        if (programs == null || programs.size() == 0) {
            return;
        }
        
        // delete titleCards, delete poiPoints at program level
        TitleCardManager titlecardMngr = new TitleCardManager();
        List<TitleCard> titlecards = null;
        List<TitleCard> titlecardDeleteList = new ArrayList<TitleCard>();
        PoiPointManager pointMngr = new PoiPointManager();
        List<PoiPoint> points = null;
        List<PoiPoint> pointDeleteList = new ArrayList<PoiPoint>();
        for (NnProgram program : programs) { // TODO : sql in loop is bad
            titlecards = null;
            points = null;
            
            titlecards = titlecardMngr.findByProgramId(program.getId());
            if (titlecards != null && titlecards.size() > 0) {
                titlecardDeleteList.addAll(titlecards);
            }
            
            points = pointMngr.findByProgram(program.getId());
            if (points != null && points.size() > 0) {
                pointDeleteList.addAll(points);
            }
        }
        titlecardMngr.delete(titlecardDeleteList);
        pointMngr.delete(pointDeleteList);
        
        List<Long> channelIds = new ArrayList<Long>();
        
        for (NnProgram program : programs) {
            
            if (channelIds.indexOf(program.getChannelId()) < 0) {
                channelIds.add(program.getChannelId());
            }
        }
        
        dao.deleteAll(programs);
        
        log.info("channel count = " + channelIds.size());
        for (Long channelId : channelIds) {
            resetCache(channelId);
        }
    }
    
    public NnProgram findByChannelAndStorageId(long channelId, String storageId) {
        return dao.findByChannelAndStorageId(channelId, storageId);
    }
    
    public NnProgram findByChannelAndFileUrl(long channelId, String fileUrl) {
        return dao.findByChannelAndFileUrl(channelId, fileUrl);
    }
    
    public String findLatestProgramInfoByChannels(List<NnChannel> channels) {
        
        YtProgramDao ytDao = new YtProgramDao();
        String output = "";
        
        for (NnChannel c : channels) {
            
            String cacheKey = this.getProgramInfoCacheKey(c.getId());
            String programInfo = (String) CacheFactory.get(cacheKey);
            
            if (programInfo != null) {
                log.info("got programInfo from cache, channelId = " + c.getId());
                if (!programInfo.isEmpty())
                    output += programInfo;
                continue;
            }
            if (c.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL || 
                c.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_PLAYLIST) {
                YtProgram yt = ytDao.findLatestByChannel(c.getId());
                List<YtProgram> programs = new ArrayList<YtProgram>();
                if (yt != null) {
                    programs.add(yt);
                    log.info("find latest yt program:" + yt.getId());
                    programInfo = this.composeYtProgramInfo(c, programs);
                }
            }
            if (c.getContentType() == NnChannel.CONTENTTYPE_MIXED) {
                List<NnEpisode> episodes = new NnEpisodeManager().findPlayerLatestEpisodes(c.getId(), c.getSorting());                
                if (episodes.size() > 0) {
                    log.info("find latest episode id:" + episodes.get(0).getId());
                    List<NnProgram> programs = this.findByEpisodeId(episodes.get(0).getId());
                    programInfo = this.composeNnProgramInfo(c, episodes, programs);
                }
            }
            if (programInfo != null) {
                output += programInfo;
                log.info("save lastProgramInfo to cache, channelId = " + c.getId());
                CacheFactory.set(cacheKey, programInfo);
            } else {
                log.info("save lastProgramInfo to cache, channelId = " + c.getId() + ", though its empty");
                CacheFactory.set(cacheKey, ""); // save an empty string to prevent cache miss
            }
        }        
        return output;
    }
    
    public static String getCntViewCacheName(long channelId, String programId) {
        return "ch" + String.valueOf(channelId) + "-ep" + programId;
    }    

    /**
     * Get a position of an episode in a channel.
     * Works only for fixed sorting channel such as maplestage channel or 9x9 channel.
     *  
     * @param player program info string
     * @param programId program key
     * @return program id position
     */
    public int getEpisodeIndex(String input, String programId) {
        String[] lines = input.split("\n");
        int index = 0;
        for (int i=0; i<lines.length; i++) {
            String[] tabs = lines[i].split("\t");
            if (tabs[1].equals(programId)) {
                index = i+1;
                i = lines.length + 1;
            }
        }        
        return index;
    }
    
    public short getContentType(NnProgram program) {
        if (program.getAudioFileUrl() != null)
            return NnProgram.CONTENTTYPE_RADIO;
        if (program.getFileUrl().contains("youtube.com")) 
            return NnProgram.CONTENTTYPE_YOUTUBE;         
        return NnProgram.CONTENTTYPE_DIRECTLINK;     
    }
    
    
    public NnProgram findByStorageId(String storageId) {
        return dao.findByStorageId(storageId);
    }

    public NnProgram findById(long id) {
        NnProgram program = dao.findById(id);
        return program;
    }

    public List<NnProgram> findByYtVideoId(String videoId) {
        List<NnProgram> programs = dao.findByYtVideoId(videoId);
        return programs;
    }
    
    public List<NnProgram> findByChannelId(long channelId) {
        return dao.findByChannel(channelId);
    }
    
    public List<NnProgram> findByChannelIdAndSeq(long channelId, Short seq) {
        return dao.findByChannelAndSeq(channelId, NnStringUtil.seqToStr(seq));
    }
    
    public void resetCache(long channelId) {        
        log.info("reset program info cache: " + channelId);
        CacheFactory.delete(getCacheKey(channelId));
        CacheFactory.delete(getV31CacheKey(channelId));
        CacheFactory.delete(getProgramInfoCacheKey(channelId));
        CacheFactory.delete(NnChannelManager.getCacheKey(channelId, 32));
        CacheFactory.delete(NnChannelManager.getCacheKey(channelId, 40));
    }
    
    /*
    public void processCache(long channelId) {
        String cacheKey = this.getCacheKey(channelId);        
        NnChannel c = new NnChannelManager().findById(channelId);
        if (c == null)
            return;
        log.info("re-assemble program info cache:" + channelId);
        String output = this.assembleProgramInfo(c);
        if (CacheFactory.isRunning) { 
            CacheFactory.set(cacheKey, output);
        }
    } 
    */   
    
    /*
    public String retrieveCache(String key) {
        log.info("cache key:" + key);
        String value = (String)CacheFactory.get(key);
        return value;
    }
    */
    
    //example: nnprogram(channel_id)
    public String getCacheKey(long channelId) {
        String str = "nnprogram(" + channelId + ")"; 
        return str;
    }
    
    //cache the 1st program of channel
    public String getProgramInfoCacheKey(long channelId) {
        String str = "nnprogram-info(" + channelId + ")";
        return str;
    }
    
    public String getV31CacheKey(long channelId) {
        String str = "nnprogram-v31(" + channelId + ")"; 
        return str;
    }
    
    public int total() {
        return dao.total();
    }
    
    public int total(String filter) {
        return dao.total(filter);
    }
    
    public List<NnProgram> list(int page, int limit, String sidx, String sord) {
        return dao.list(page, limit, sidx, sord);
    }
    
    public List<NnProgram> list(int page, int limit, String sidx, String sord, String filter) {
        return dao.list(page, limit, sidx, sord, filter);
    }
    
    // hook, auto share to facebook
    private void autoShare(NnProgram program) {
        /*
        AutosharingService sharingService = new AutosharingService();
        FBPost fbPost = new FBPost(program.getName(), program.getIntro(), program.getImageUrl());
        MsoManager msoMngr = new MsoManager();
        String url = "http://" + MsoConfigManager.getServerDomain() + "/view?channel=" + channel.getId() + "&episode=" + program.getId();
        fbPost.setLink(url);
        log.info("share link: " + url);
        if (program.getComment() != null) {
            fbPost.setMessage(program.getComment());
        }
        
        log.info("FB autosharing count = " + channelAutosharings.size());
        for (NnChannelAutosharing autosharing : channelAutosharings) {
            Mso mso = msoMngr.findById(autosharing.getMsoId());
            if (mso.getLang() != null && mso.getLang().equals("en")) {
                fbPost.setCaption(messageSource.getMessage("cms.autosharing.episode_added", null, Locale.ENGLISH));
            } else {
                fbPost.setCaption(messageSource.getMessage("cms.autosharing.episode_added", null, Locale.TRADITIONAL_CHINESE));
            }
            if (snsAuth != null && snsAuth.isEnabled()) {
                if (autosharing.getTarget() != null && autosharing.getParameter() != null) {
                    fbPost.setFacebookId(autosharing.getTarget());
                    fbPost.setAccessToken(autosharing.getParameter());
                } else {
                    fbPost.setFacebookId(snsAuth.getToken());
                    fbPost.setAccessToken(snsAuth.getSecret());
                }
                QueueFactory.add("/CMSAPI/postToFacebook", fbPost);
            }
        }
        
        // hook, auto share to twitter
        channelAutosharings = sharingService.findByChannelAndType(channel.getId(), SnsAuth.TYPE_TWITTER);
        log.info("twitter autosharing count = " + channelAutosharings.size());
        for (NnChannelAutosharing autosharing : channelAutosharings) {
            SnsAuth snsAuth = snsMngr.findTitterAuthByMsoId(autosharing.getMsoId());
            Mso mso = msoMngr.findById(autosharing.getMsoId());
            if (mso.getLang() != null && mso.getLang().equals("en")) {
                fbPost.setCaption(messageSource.getMessage("cms.autosharing.episode_added", null, Locale.ENGLISH));
            } else {
                fbPost.setCaption(messageSource.getMessage("cms.autosharing.episode_added", null, Locale.TRADITIONAL_CHINESE));
            }
            if (snsAuth != null && snsAuth.isEnabled()) {
                
                fbPost.setFacebookId(snsAuth.getToken());
                fbPost.setAccessToken(snsAuth.getSecret());
                
                QueueFactory.add("/CMSAPI/postToTwitter", fbPost);
            }
        }
       */
    }
    
    public List<NnProgram> findByEpisodeId(long episodeId) {
        
        if (episodeId == 0) {
            return new ArrayList<NnProgram>();
        }
        
        return dao.findProgramsByEpisode(episodeId); // sorted already
    }
    
    public void reorderEpisodePrograms(long episodeId) {
        
        List<NnProgram> programs = findByEpisodeId(episodeId);
        
        Collections.sort(programs, getProgramComparator("subSeq"));
        
        log.info("programs.size() = " + programs.size());
        
        for (int i = 0; i < programs.size(); i++) {
            programs.get(i).setSubSeq(i + 1);
        }
        
        save(programs);
        
    }
    
    public Comparator<NnProgram> getProgramComparator(String sort) {
        if (sort.equals("updateDate")) {
            class ProgramComparator implements Comparator<NnProgram> {
                public int compare(NnProgram program1, NnProgram program2) {
                    Date d1 = program1.getUpdateDate();
                    Date d2 = program2.getUpdateDate();
                    return d2.compareTo(d1);
                }
            }
            return new ProgramComparator();            
        }
        if (sort.equals("subSeq")) {
            class ProgramComparator implements Comparator<NnProgram> {
                public int compare(NnProgram program1, NnProgram program2) {
                    int subSeq1 = program1.getSubSeqInt();
                    int subSeq2 = program2.getSubSeqInt();
                    return (subSeq1 - subSeq2);
                }
            }
            return new ProgramComparator();
        }
        //default, subSeq
        class NnProgramSeqComparator implements Comparator<NnProgram> {            
            public int compare(NnProgram program1, NnProgram program2) {
                int subSeq1 = program1.getSubSeqInt();
                int subSeq2 = program2.getSubSeqInt();                
                return (subSeq1 - subSeq2);
            }
        }
        return new NnProgramSeqComparator();
    }
    
    public List<NnProgram> getUserFavorites(NnUser user) {
    
        NnChannelManager channelMngr = new NnChannelManager();
        List<NnProgram> empty = new ArrayList<NnProgram>();
        
        List<NnChannel> channelFavorites = channelMngr.findByUser(user, 0, false);
        
        for (NnChannel channel : channelFavorites) {
            
            if (channel.getContentType() == NnChannel.CONTENTTYPE_FAVORITE) {
                
                List<NnProgram> favorites = findByChannelId(channel.getId());
                log.info("favorites count = " + favorites.size());
                return favorites;
            }
        }
        
        log.info("no favorite channel");
        return empty;
    }
            
    /**
     * player programInfo entry for iOS
     * @param channelId system channel id
     * @param episodeIds specified episodes
     * @param sidx start index
     * @param limit number of records
     * @return program info string 
     */
    public String findPlayerProgramInfoByChannel(long channelId, String episodeIds, long sidx, long limit) {
        String result = this.findPlayerProgramInfoByChannel(channelId);
        if (episodeIds != null && !episodeIds.isEmpty()) return composeSpecifiedProgramInfoStr(result, channelId, episodeIds);
        if (channelId == 28087) return result; // weifilm, temporary workaround
        return this.composeLimitProgramInfoStr(result, sidx, limit);
    }    
    
    private String composeSpecifiedProgramInfoStr(String input, long channelId, String episodeIds) {
        
        if (episodeIds == null || episodeIds.isEmpty())
            return input;
        String[] lines = input.split("\n");
        String[] episodeIdArr = episodeIds.split(",");
        String result = "";
            
        for (String episodeId : episodeIdArr) {
                
            String regex = "^" + channelId + "\t" + episodeId + "\t.*";
            log.info("regex = " + regex);
            for (String line : lines) {
                
                if (line.matches(regex)) {
                    result += line + "\n";
                }
            }
        }
        return result;
    }
    //for iOS
    private String composeLimitProgramInfoStr(String input, long sidx, long limit) {
        if (sidx == 0 && limit == 0)
            return input;
        String[] lines = input.split("\n");
        String result = "";
        long start = sidx - 1;
        long end = start + limit;
        for (int i=0; i<lines.length; i++) {
            if (i>=start && i<end) {
                result += lines[i] + "\n";
            }
            if (i > end) {
                return result;
            }
        }        
        return result;
    }
    
    //player programInfo entry
    public String findPlayerProgramInfoByChannel(long channelId) {
        String cacheKey = this.getCacheKey(channelId);
        try {
            String result = (String)CacheFactory.get(cacheKey);
            if (result != null) {
                log.info("cached programInfo, channelId = " + channelId);
                return result;
            } 
        } catch (Exception e) {
            log.info("memcache error");
        }
        NnChannel c = new NnChannelManager().findById(channelId);
        if (c == null)
            return "";
        String output = this.assembleProgramInfo(c);
        log.info("store programInfo, channelId = " + channelId);
        CacheFactory.set(cacheKey, output);
        return output;
    }
    
    //find "good" programs, to find nnchannel type of programs, use findPlayerNnProgramsByChannel
    public List<NnProgram> findPlayerProgramsByChannel(long channelId) {
        List<NnProgram> programs = new ArrayList<NnProgram>();
        NnChannel c = new NnChannelManager().findById(channelId);
        if (c == null)
            return programs;
        programs = dao.findPlayerProgramsByChannel(c); //sort by seq and subSeq
        return programs;
    }    
        
    //find player programs through nnepisode
    public List<NnProgram> findPlayerNnProgramsByChannel(long channelId) {
        return dao.findPlayerNnProgramsByChannel(channelId); //sort by episode seq and nnprogram subSeq
    }
    
    //for favorite 9x9 program, find the referenced data
    //return value: obj[0] = NnEpisode; obj[1] = List<NnProgram>
    public Object[] findFavoriteReferenced(String storageId) {
        Object[] obj = new Object[2];
        List<NnProgram> programs = new ArrayList<NnProgram>();
        if (storageId == null)
            return null;
        storageId = storageId.replace("e", "");
        NnEpisode episode = new NnEpisodeManager().findById(Long.parseLong(storageId));
        if (episode == null)
            return null;
        if (episode.isPublic() == true) { //TODO and some other conditions?
            programs = dao.findProgramsByEpisode(Long.parseLong(storageId));
            obj[0] = episode;
            obj[1] = programs;
            log.info("find favorite reference's real programs size:" + programs.size());            
        }           
        return obj;
    }
                
    //based on channel type, assemble programInfo string
    public String assembleProgramInfo(NnChannel c) {
        String output = "";        
        if (c.getContentType() == NnChannel.CONTENTTYPE_MIXED){
            List<NnEpisode> episodes = new NnEpisodeManager().findPlayerEpisodes(c.getId(), c.getSorting());
            List<NnProgram> programs = this.findPlayerNnProgramsByChannel(c.getId());
            output = this.composeNnProgramInfo(c, episodes, programs);
        } else {
            List<NnProgram> programs = this.findPlayerProgramsByChannel(c.getId());
            log.info("channel id:" + c.getId() + "; program size:" + programs.size());
            output = this.composeProgramInfo(c, programs);
        }
        return output;
    }
    
    public String composeYtProgramInfo(NnChannel c, List<YtProgram> programs) {
        if (programs.size() == 0)
            return "";        
        String result = "";
        if (c == null) return null;
        for (YtProgram p : programs) {
            result += this.composeYtProgramInfoStr(p);
        }
        return result;
    }
    
    //compose programInfo for non 9x9 and non favorite channel
    public String composeProgramInfo(NnChannel c, List<NnProgram> programs) {
        if (programs.size() == 0)
            return "";        
        String result = "";
        //NnProgram original = programs.get(0);
        //NnChannel c = new NnChannelManager().findById(original.getChannelId());
        if (c == null) return null;
        if (c.getContentType() != NnChannel.CONTENTTYPE_MIXED &&
            c.getContentType() != NnChannel.CONTENTTYPE_FAVORITE) {
            for (NnProgram p : programs) {
                result += this.composeProgramInfoStr(p);
            }
            return result;
        }
        if (c.getContentType() == NnChannel.CONTENTTYPE_FAVORITE) {
            for (NnProgram p : programs) {
                if (p.getContentType() == NnProgram.CONTENTTYPE_REFERENCE) {
                    Object[] obj = this.findFavoriteReferenced(p.getStorageId());
                    if (obj != null) {
                        List<NnEpisode> epList = new ArrayList<NnEpisode>();
                        epList.add((NnEpisode)obj[0]);
                        @SuppressWarnings("unchecked")
                        List<NnProgram> referencePrograms = (List<NnProgram>)obj[1];
                        log.info("reference program size:" + referencePrograms.size());
                        String favoriteStr = this.composeNnProgramInfo(c, epList, referencePrograms);
                        String[] lines = favoriteStr.split("\n");
                        for (String line : lines) {
                            //replace with favorite's own channel id and program id
                            Pattern pattern = Pattern.compile("\t.*?\t");
                            Matcher m = pattern.matcher(line);
                            if (m.find()) {
                                line = m.replaceFirst("\t" + String.valueOf(p.getId()) + "\t");
                            }
                            pattern = Pattern.compile(".*?\t");
                            m = pattern.matcher(line);
                            if (m.find()) {
                                line = m.replaceFirst(p.getChannelId() + "\t");
                            }                        
                            result += line + "\n";
                        }
                    }                    
                } else {
                    result += this.composeProgramInfoStr(p);
                }
            }
        }
        return result;
    }
    
    //compose nnchannel programs and favorite channels, otherwise use composeProgramInfo
    public String composeNnProgramInfo(NnChannel channel, List<NnEpisode> episodes, 
                                       List<NnProgram> programs) {
        if (episodes.size() == 0 || programs.size() == 0)
            return "";
        String result = "";
        Map<Long, List<NnProgram>> map = new TreeMap<Long, List<NnProgram>>();                
        for (NnProgram p : programs) {
            List<NnProgram> list = map.get(p.getEpisodeId());
            list = (list == null) ? new ArrayList<NnProgram>() : list;
            list.add(p);
            map.put(p.getEpisodeId(), list);
        }      
        //title card in map, nnprogram retrieves title card based on program id and type 
        List<TitleCard> cards = new TitleCardDao().findByChannel(channel.getId()); //order by channel id and program id
        HashMap<String, TitleCard> cardMap = new HashMap<String, TitleCard>();        
        for (TitleCard c : cards) {
            String key = String.valueOf(c.getProgramId() + ";" + c.getType());
            cardMap.put(key, c);
        }
        
        //find all the programs, and find its event
        //episode number;text;link;start time;end time|episode number;link;text;start time;end time
        //List<Poi> pois = new PoiPointManager().findByChannel(channel.getId()); //find all the programs        
        //List<PoiEvent> events = new PoiEventManager().findByChannel(channel.getId());
        PoiPointManager pointMngr = new PoiPointManager();
        PoiEventManager eventMngr = new PoiEventManager();
        for (NnEpisode e : episodes) {
            List<NnProgram> list = (List<NnProgram>) map.get(e.getId());
            if (list == null)
                log.info("episode:" + e.getId() + " have no programs");
            if (list != null && list.size() > 0) {
                Collections.sort(list, getProgramComparator("subSeq"));
                String videoUrl = "";
                String duration = String.valueOf(e.getDuration());
                String name = getNotPipedProgramInfoData(e.getName());
                String imageUrl = e.getImageUrl();
                String imageLargeUrl = e.getImageUrl();
                String intro = getNotPipedProgramInfoData(e.getIntro());
                String card = "";
                String contentType = "";
                int i=1;                
                String poiStr = "";
                for (NnProgram p : list) { //sub-episodes
                    List<PoiPoint> points = pointMngr.findCurrentByProgram(p.getId());
                    //List<Poi> pois = pointMngr.findCurrentPoiByProgram(p.getId());                    
                    log.info("points size:" + points.size());                    
                    List<PoiEvent> events = new ArrayList<PoiEvent>();
                    for (PoiPoint point : points) {
                        PoiEvent event = eventMngr.findByPoint(point.getId());
                        events.add(event);
                    }
                    if (points.size() != events.size()) {
                        log.info("Bad!!! should not continue.");
                        points.clear();
                    }
                    for (int j=0; j<points.size(); j++) {
                        PoiPoint point = points.get(j);
                        PoiEvent event = events.get(j);
                        //Poi poi = pois.get(j);
                        String context = NnStringUtil.urlencode(event.getContext());
                        //String poiStrHere = i + ";" + poi.getId() + ";" + point.getStartTime() + ";" + point.getEndTime() + ";" + event.getType() + ";" + context + "|";
                        String poiStrHere = i + ";" + point.getStartTime() + ";" + point.getEndTime() + ";" + event.getType() + ";" + context + "|";
                        log.info("poi output:" + poiStrHere);
                        poiStr += poiStrHere;
                    }
//                    for (PoiPoint point : points) {
//                        PoiEvent event = eventMngr.findByPoint(point.getId());
//                        String poiStrHere = i + ";" + event.getHyperChannelText() + ";" + event.getHyperChannelLink() + ";" + poi.getStartTime() + ";" + poi.getEndTime() + "|";
//                        log.info("poiStrHere:" + poiStrHere);                        
//                        poiStr += poiStrHere;                        
//                    }                    
                    String cardKey1 = String.valueOf(p.getId() + ";" + TitleCard.TYPE_BEGIN); 
                    String cardKey2 = String.valueOf(p.getId() + ";" + TitleCard.TYPE_END);
                    if (p.getSubSeq() != null && p.getSubSeq().length() > 0) {
                        if (cardMap.containsKey(cardKey1)) {
                            card += "subepisode" + "%3A%20" + i + "%0A";
                            card += cardMap.get(cardKey1).getPlayerSyntax() + "%0A--%0A";
                            cardMap.remove(cardKey1);
                        }
                        if (cardMap.containsKey(cardKey2)) {
                            card += "subepisode" + "%3A%20" + i + "%0A";
                            card += cardMap.get(cardKey2).getPlayerSyntax() + "%0A--%0A";
                            cardMap.remove(cardKey2);                            
                        }
                    }
                    if (p.getStartTime() != null && p.getStartTime().equals("0") &&
                        p.getEndTime() != null && p.getEndTime().equals("0")) {
                        p.setStartTime("");
                        p.setEndTime("");
                    }
                    String f1 = p.getFileUrl();
                    if (p.getAudioFileUrl() != null)
                        f1 = p.getAudioFileUrl();
                    //log.info("f1:" + f1);
                    videoUrl += "|" + f1;
                    String d1 = (p.getStartTime() != null) ? ";" + p.getStartTime() : ";";
                    String d2 = (p.getEndTime() != null) ? ";" + p.getEndTime() : ";";
                    videoUrl += d1;
                    videoUrl += d2;
                    //log.info("video url :" + videoUrl);
                    name += "|" + p.getPlayerName();
                    imageUrl += "|" + p.getImageUrl();
                    imageLargeUrl += "|" + p.getImageLargeUrl();
                    intro += "|" + p.getPlayerIntro();
                    duration += "|" + p.getDurationInt();
                    contentType += "|" + p.getContentType();
                    i++;
                }
                /*
                videoUrl = videoUrl.replaceFirst("\\|", "");
                name = name.replaceFirst("\\|", "");
                imageUrl = imageUrl.replaceFirst("\\|", "");
                intro = intro.replaceFirst("\\|", "");
                */
                poiStr = poiStr.replaceAll("\\|$", "");                
                result += composeEpisodeInfoStr(e, name, intro, imageUrl, imageLargeUrl, videoUrl, duration, card, contentType, poiStr);
            }
        }
        return result;
    }    
    
    private String removePlayerUnwanted(String value) {
        if (value == null) return value;        
        value = value.replaceAll("\\s", " ");
        return value;
    }
    
    //TODO make all piped string coming through here, actually process all player string here
    private String getNotPipedProgramInfoData(String str) {
        if (str == null) return null;
        str = str.replaceAll("\\|", "\\\\|");
        return str;
    }

    public String composeEpisodeInfoStr(NnEpisode e, 
             String name, String intro, 
             String imageUrl, String imageLargeUrl, String videoUrl, 
             String duration, String card,
             String contentType, String poiStr) {
        //zero file to play            
        name = this.removePlayerUnwanted(name);
        intro = this.removePlayerUnwanted(intro);
        String output = "";
        if (e.getPublishDate() == null)
        	e.setPublishDate(new Date()); //should not happen, just in case
        String[] ori = {String.valueOf(e.getChannelId()), 
                        "e" + String.valueOf(e.getId()), 
                        name, 
                        intro,
                        contentType,
                        duration,
                        imageUrl,
                        imageLargeUrl, //imageLargeUrl
                        videoUrl,
                        "", //url2
                        "", //url3
                        "", //audio file           
                        String.valueOf(e.getPublishDate().getTime()),
                        "", //comment
                        card,
                        poiStr};
                
        output = output + NnStringUtil.getDelimitedStr(ori);
        output = output.replaceAll("null", "");
        
        output = output + "\n";
        return output;
    }        

    public String composeProgramInfoStr(NnProgram p) {
        String output = "";        
        String regexCache = "^(http|https)://(9x9cache.s3.amazonaws.com|s3.amazonaws.com/9x9cache)";
        String regexPod = "^(http|https)://(9x9pod.s3.amazonaws.com|s3.amazonaws.com/9x9pod)";
        String cache = "http://cache.9x9.tv";
        String pod = "http://pod.9x9.tv";
        String url1 = p.getFileUrl();
        String imageUrl = p.getImageUrl();
        if (url1 != null) {
            url1 = url1.replaceFirst(regexCache, cache);
            url1 = url1.replaceAll(regexPod, pod);
        }
        if (imageUrl != null) {
            imageUrl = imageUrl.replaceFirst(regexCache, cache);
            imageUrl = imageUrl.replaceAll(regexPod, pod);
        }
        if (p.getPublishDate() == null)
        	p.setPublishDate(new Date()); //should not happen, just in case

        String[] ori = {String.valueOf(p.getChannelId()), 
                        String.valueOf(p.getId()), 
                        p.getPlayerName(), 
                        p.getPlayerIntro(),
                        String.valueOf(p.getContentType()), 
                        p.getDuration(),
                        imageUrl,
                        "",
                        url1, //video url
                        "", //file type 2 
                        "", //file type 3
                        p.getAudioFileUrl(), //audio file            
                        String.valueOf(p.getPublishDate().getTime()),
                        p.getComment(),
                        ""}; //card
        output = output + NnStringUtil.getDelimitedStr(ori);
        output = output.replaceAll("null", "");
        output = output + "\n";
        return output;
    }        

    public String composeYtProgramInfoStr(YtProgram p) {
        String output = "";
        
        String url1 = "";
        if (p.getYtVideoId() != null)
            url1 = "http://www.youtube.com/watch?v=" + p.getYtVideoId();
        String[] ori = {String.valueOf(p.getChannelId()), 
                        String.valueOf(p.getId()), 
                        p.getPlayerName(), 
                        p.getPlayerIntro(),
                        String.valueOf(NnProgram.CONTENTTYPE_YOUTUBE), 
                        p.getDuration(),
                        p.getImageUrl(),
                        "",
                        url1, //video url
                        "", //file type 2 
                        "", //file type 3
                        "", //audio file            
                        String.valueOf(p.getUpdateDate().getTime()),
                        "", //comment
                        ""}; //card
        output = output + NnStringUtil.getDelimitedStr(ori);
        output = output.replaceAll("null", "");
        output = output + "\n";
        return output;
    }        
    
    // TODO change to isPoiPointCollision
    /*
    public boolean isPoiCollision(NnProgram program, int startTime, int endTime) {
        List<PoiPoint> pois = new PoiPointManager().findByProgramId(program.getId());
        for (PoiPoint poi : pois) {
            if (startTime > poi.getStartTimeInt() || endTime < poi.getEndTimeInt()) {
                return true;
            }
        }
        return false;
    }
    */
}
