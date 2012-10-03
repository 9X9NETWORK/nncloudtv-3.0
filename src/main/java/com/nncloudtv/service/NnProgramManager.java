package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        int seq = episodeMngr.getEpisodeSeq(episode);
        
        program.setSeq(seq);
        program = save(program);
        
        // first program join episode
        if (seq == 0) {
            
            episodeMngr.reorderChannelEpisodes(episode.getChannelId());
            
        }
        
        // non-specified sub-position
        if (program.getSubSeqInt() == 0) {
            reorderEpisodePrograms(episode.getId());
        }
        
        // update episode duration
        episode.setDuration(episodeMngr.calculateEpisodeDuration(episode));
        episodeMngr.save(episode);
        
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
        
        if (recalculateEpisodeDuration) {
            
            NnEpisodeManager episodeMngr = new NnEpisodeManager();
            
            NnEpisode episode = episodeMngr.findById(program.getEpisodeId());
            if (episode != null) {
                episode.setDuration(episodeMngr.calculateEpisodeDuration(episode));
                episodeMngr.save(episode);
            }
            
        }
        
        return program;
    }
    
    public void delete(NnProgram program) {
    
        long episodeId = program.getEpisodeId();
        
        dao.delete(program);
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode != null) {
            
            episode.setDuration(episodeMngr.calculateEpisodeDuration(episode));
            episodeMngr.save(episode);
        }
        
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
    
    public List<NnProgram> findPlayerProgramsByChannel(long channelId) {
        List<NnProgram> programs = new ArrayList<NnProgram>();
        NnChannel c = new NnChannelManager().findById(channelId);
        if (c == null)
            return programs;
        programs = dao.findPlayerProgramsByChannel(c);
        return programs;
    }    

    public String findPlayerProgramInfoByChannel(long channelId) {
        String cacheKey = "nnprogram(" + channelId + ")";
        String result = (String)CacheFactory.get(cacheKey);
        if (CacheFactory.isRunning && result != null) { 
            return result;
        }        
        
        List<NnProgram> programs = this.findPlayerProgramsByChannel(channelId);
        log.info("channel id:" + channelId + "; program size:" + programs.size());
        String str = this.composeProgramInfo(programs);
        if (CacheFactory.isRunning) { 
            CacheFactory.set(cacheKey, str);
        }
        return str;
    }    
    
    /**
     * find playerAPI's programInfo string
     * @param channelId system channel id
     * @param sidx start index
     * @param limit number of records
     * @return program info string 
     */
    public String findPlayerProgramInfoByChannel(long channelId, long sidx, long limit) {
        String result = this.findPlayerProgramInfoByChannel(channelId);
        return this.composeLimitProgramInfoStr(result, sidx, limit);
    }    
    
    public List<NnProgram> findPlayerProgramsByChannels(List<Long>channelIds) {
        log.info("requested channelIds size:" + channelIds.size());
        List<NnProgram> programs = new ArrayList<NnProgram>();
        log.info("remaining channel size not in the cache:" + channelIds.size());
        if (channelIds.size() > 0) {
            List<NnProgram> list = dao.findPlayerProgramsByChannels(channelIds);
            programs.addAll(list);
        }
        return programs;
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
            
    public List<NnProgram> findRealPrograms(String storageId) {
        List<NnProgram> programs = new ArrayList<NnProgram>();
        if (storageId == null)
            return programs;
        storageId = storageId.replace("e", "");
        programs = dao.findProgramsByEpisode(Long.parseLong(storageId));
        log.info("find reference's real programs size:" + programs.size());        
        return programs;        
    }
    
    @SuppressWarnings("rawtypes")
    public String processSubEpisode(List<NnProgram> programs) {
        if (programs.size() == 0)
            return "";
        long cId = programs.get(0).getChannelId();
        //put programs in the map, key is seq, all programs with the same seq will be lumped to a list
        Map<String, List<NnProgram>> map = new TreeMap<String, List<NnProgram>>();                
        String result = "";
        for (NnProgram p : programs) {
            if (p.getEpisodeId() == 0) {
                result += this.composeProgramInfoStr(p);
            }
            List<NnProgram> list = map.get(p.getSeq());
            list = (list == null) ? new ArrayList<NnProgram>() : list;
            list.add(p);
            map.put(p.getSeq(), list);
        }
        if (result.length() > 0) {
            log.info("noted: old 9x9 program not migrated");
            return result;
        }
        //episode in map, nnprogram retrieves episode based on episodeId in nnprogram
        List<NnEpisode> episodes = new NnEpisodeManager().findByChannelId(cId);
        Map<Long, NnEpisode> episodeMap = new HashMap<Long, NnEpisode>();
        for (NnEpisode e : episodes) {
            episodeMap.put(e.getId(), e);
        }
        //title card in map, nnprogram retrieves title card based on program id and type 
        List<TitleCard> cards = new TitleCardDao().findByChannel(cId); //order by channel id and program id
        HashMap<String, TitleCard> cardMap = new HashMap<String, TitleCard>();        
        for (TitleCard c : cards) {
            String key = String.valueOf(c.getProgramId() + ";" + c.getType());
            cardMap.put(key, c);
        }
        Iterator<Entry<String, List<NnProgram>>> it = map.entrySet().iterator();
        while (it.hasNext()) { //each entry is an episode (of programs)
            Map.Entry pairs = (Map.Entry)it.next();
            @SuppressWarnings("unchecked")
            List<NnProgram> list = (List<NnProgram>) pairs.getValue();
            if (list.size() > 0) {
                String videoUrl = "";
                String name = "";
                String imageUrl = "";
                String intro = "";                        
                String card = "";
                NnProgram one = list.get(0);
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
                videoUrl = videoUrl.replaceFirst("\\|", "");
                name = name.replaceFirst("\\|", "");
                imageUrl = imageUrl.replaceFirst("\\|", "");
                intro = intro.replaceFirst("\\|", "");
                result += composeEpisodeInfoStr(episodeMap.get(one.getEpisodeId()), name, intro, imageUrl, videoUrl, card);
            }
            it.remove();
        }
        return result;
    }

    public String composeProgramInfo(List<NnProgram> programs) {
        if (programs.size() == 0)
            return "";        
        String result = "";
        NnProgram original = programs.get(0);
        NnChannel c = new NnChannelManager().findById(original.getChannelId());
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
                System.out.println("<<<< program type >>>>" + p.getContentType());
                if (p.getContentType() == NnProgram.CONTENTTYPE_REFERENCE) {
                    List<NnProgram> favorite = this.findRealPrograms(p.getStorageId());
                    String favoriteStr = this.processSubEpisode(favorite);
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
                } else {
                    result += this.composeProgramInfoStr(p);
                }
            }
            return result;
        }
        //only 9x9 channels hits here
        result += this.processSubEpisode(programs);
        return result;
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
    
    public List<NnProgram> findSubscribedPrograms(NnUser user) {
        NnUserSubscribeManager subService = new NnUserSubscribeManager();            
        List<NnChannel> channels = subService.findSubscribedChannels(user);
        List<NnProgram> programs = new ArrayList<NnProgram>();
        List<Long> channelIds = new ArrayList<Long>();
        for (NnChannel c : channels) {
            channelIds.add(c.getId());
        }
        programs = this.findPlayerProgramsByChannels(channelIds);
        return programs;
    }
    
    public String processCache(long channelId) {
        List<NnProgram> programs = this.findPlayerProgramsByChannel(channelId);
        log.info("channel id:" + channelId + "; program size:" + programs.size());
        String cacheKey = this.getCacheKey(channelId);
        String str = this.composeProgramInfo(programs); 
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
    
    public void reorderEpisodePrograms(long episodeId) {
        
        List<NnProgram> programs = findByEpisodeId(episodeId);
        
        Collections.sort(programs, getProgramSeqComparator());
        
        log.info("programs.size() = " + programs.size());
        
        for (int i = 0; i < programs.size(); i++) {
            programs.get(i).setSubSeq(i + 1);
        }
        
        save(programs);
        
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
    
    public String composeEpisodeInfoStr(NnEpisode e, String name, String intro, String imageUrl, String videoUrl, String card) {
        String output = "";        
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
    
}
