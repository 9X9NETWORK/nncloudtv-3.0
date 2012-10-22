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
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.TitleCard;

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
        this.processCache(channel.getId());
        
        //!!!!! clean plus "hook, auto share to facebook"
        //set channel count
        int count = channel.getCntEpisode() + 1;
        channel.setCntEpisode(count);
        NnChannelManager channelMngr = new NnChannelManager();
        channelMngr.save(channel);

        //if the channel's original programCount is zero, its count will not be in the category, adding it now.
        if (count == 1) {
            CategoryManager categoryMngr = new CategoryManager();
            categoryMngr.addChannelCounter(channel);
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
            processCache(channelId);
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
        
        this.processCache(program.getChannelId());
        
        return program;
    }
    
    public NnProgram save(NnProgram program, boolean recalculateEpisodeDuration) {
            
        if (program == null) {
            return program;
        }
        
        program = save(program);
                
        //episode.setDuration(0); // set 0 to notify episode get operation to recalculate duration.        
        
        return program;
    }
    
    public void delete(NnProgram program) {
        long cId = program.getChannelId();
        dao.delete(program);
        this.processCache(cId);
    }
    
    public void delete(List<NnProgram> programs) {
    
        if (programs == null || programs.size() == 0) {
            return;
        }
        
        List<Long> channelIds = new ArrayList<Long>();
        
        for (NnProgram program : programs) {
            
            if (channelIds.indexOf(program.getChannelId()) < 0) {
                channelIds.add(program.getChannelId());
            }
        }
        
        dao.deleteAll(programs);
        
        log.info("channel count = " + channelIds.size());
        for (Long channelId : channelIds) {
            processCache(channelId);
        }
    }
    
    public NnProgram findByChannelAndStorageId(long channelId, String storageId) {
        return dao.findByChannelAndStorageId(channelId, storageId);
    }
    
    public NnProgram findByChannelAndFileUrl(long channelId, String fileUrl) {
        return dao.findByChannelAndFileUrl(channelId, fileUrl);
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

    public List<NnProgram> findByChannelId(long channelId) {
        return dao.findByChannel(channelId);
    }
    
    public List<NnProgram> findByChannelIdAndSeq(long channelId, Short seq) {
        return dao.findByChannelAndSeq(channelId, NnStringUtil.seqToStr(seq));
    }
        
    public String processCache(long channelId) {
        String cacheKey = this.getCacheKey(channelId);
        String str = findPlayerProgramInfoByChannel(channelId);
        CacheFactory.set(cacheKey, str);
        return str;
    }    
    
    public String retrieveCache(String key) {
        log.info("cache key:" + key);
        String value = (String)CacheFactory.get(key);
        return value;
    }
    
    //example: nnprogram(channel_id)
    public String getCacheKey(long channelId) {
        String str = "nnprogram(" + channelId + ")"; 
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
    
    public NnProgram findOneByEpisodeId(long episodeId) {
        
        return dao.findProgramByEpisode(episodeId);
    }
    
    public void reorderEpisodePrograms(long episodeId) {
        
        List<NnProgram> programs = findByEpisodeId(episodeId);
        
        Collections.sort(programs, getProgramSeqComparator());
        
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
        //default, seq and subSeq
        class NnProgramSeqComparator implements Comparator<NnProgram> {            
            public int compare(NnProgram program1, NnProgram program2) {                
                int seq1 = program1.getSeqInt();
                int seq2 = program2.getSeqInt();
                int subSeq1 = program1.getSubSeqInt();
                int subSeq2 = program2.getSubSeqInt();                
                return (seq1 == seq2) ? (subSeq1 - subSeq2) : (seq1 - seq2);                
            }
        }        
        return new NnProgramSeqComparator();       
    }
    
    public Comparator<NnProgram> getProgramSeqComparator() {
        
        class NnProgramSeqComparator implements Comparator<NnProgram> {
            
            public int compare(NnProgram program1, NnProgram program2) {
                
                int seq1 = program1.getSeqInt();
                int seq2 = program2.getSeqInt();
                int subSeq1 = program1.getSubSeqInt();
                int subSeq2 = program2.getSubSeqInt();
                
                return (seq1 == seq2) ? (subSeq1 - subSeq2) : (seq1 - seq2);
                
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
     * @param sidx start index
     * @param limit number of records
     * @return program info string 
     */
    public String findPlayerProgramInfoByChannel(long channelId, long sidx, long limit) {
        String result = this.findPlayerProgramInfoByChannel(channelId);
        return this.composeLimitProgramInfoStr(result, sidx, limit);
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
        String cacheKey = "nnprogram(" + channelId + ")";
//        String result = (String)CacheFactory.get(cacheKey);
//        if (CacheFactory.isRunning && result != null) { 
//            return result;
//        }        
        NnChannel c = new NnChannelManager().findById(channelId);
        if (c == null)
            return "";
        String output = this.assembleProgramInfo(c);
        if (CacheFactory.isRunning) { 
            CacheFactory.set(cacheKey, output);
        }
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
            List<NnEpisode> episodes = new NnEpisodeManager().findPlayerEpisodes(c.getId());
            List<NnProgram> programs = this.findPlayerNnProgramsByChannel(c.getId());
            output = this.composeNnProgramInfo(c, episodes, programs);
        } else {
            List<NnProgram> programs = this.findPlayerProgramsByChannel(c.getId());
            log.info("channel id:" + c.getId() + "; program size:" + programs.size());
            output = this.composeProgramInfo(c, programs);
        }
        return output;
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
    public String composeNnProgramInfo(NnChannel channel, List<NnEpisode> episodes, List<NnProgram> programs) {
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
        for (NnEpisode e : episodes) {
            List<NnProgram> list = (List<NnProgram>) map.get(e.getId());
            Collections.sort(list, getProgramSeqComparator());
            if (list == null)
                log.info("episode:" + e.getId() + " have no programs");
            if (list != null && list.size() > 0) {
                String videoUrl = "|";
                String name = e.getName();
                String imageUrl = e.getImageUrl();
                String intro = e.getIntro();                        
                String card = "";
                int i=1;
                for (NnProgram p : list) { //sub-episodes
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
                    videoUrl += "|" + p.getFileUrl();
                    videoUrl += (p.getStartTime() != null) ? ";" + p.getStartTime() : ";";   
                    videoUrl += (p.getEndTime() != null) ? ";" + p.getEndTime() : ";";                                        
                    name += "|" + p.getPlayerName();
                    imageUrl += "|" + p.getImageUrl();
                    intro += "|" + p.getPlayerIntro();
                    i++;
                }
                /*
                videoUrl = videoUrl.replaceFirst("\\|", "");
                name = name.replaceFirst("\\|", "");
                imageUrl = imageUrl.replaceFirst("\\|", "");
                intro = intro.replaceFirst("\\|", "");
                */
                System.out.println("name:" + name);
                result += composeEpisodeInfoStr(e, name, intro, imageUrl, videoUrl, card);
            }
        }
        return result;
    }    
    
    private String removePlayerUnwanted(String value) {
        if (value == null) return value;        
        value = value.replaceAll("\\s", " ");
        return value;
    }
    
    public String composeEpisodeInfoStr(NnEpisode e, String name, String intro, String imageUrl, String videoUrl, String card) {
        name = this.removePlayerUnwanted(name);
        intro = this.removePlayerUnwanted(intro);
        String output = "";
        System.out.println("--before compose---" + name);
        String[] ori = {String.valueOf(e.getChannelId()), 
                        "e" + String.valueOf(e.getId()), 
                        name, 
                        intro,
                        "1", //content type, more accurate should be piped
                        String.valueOf(e.getDuration()),
                        imageUrl,
                        "", //imageLargeUrl
                        videoUrl,
                        "", //url2
                        "", //url3
                        "", //url4           
                        String.valueOf(e.getUpdateDate().getTime()),
                        "",
                        card};
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
                        String.valueOf(p.getUpdateDate().getTime()),
                        p.getComment(),
                        ""}; //card
        output = output + NnStringUtil.getDelimitedStr(ori);
        output = output.replaceAll("null", "");
        output = output + "\n";
        return output;
    }        
    
}
