package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

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
        if (program.getSeqInt() == 0) {
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
        long channelId = 0;        
        for (NnProgram program : programs) {            
            Date now = new Date();
            if (program.getCreateDate() == null)
                program.setCreateDate(now);
            if (program.getUpdateDate() == null) {
                program.setUpdateDate(now);
            }            
            program = dao.save(program);            
            if (channelId != program.getChannelId()) {
                channelId = program.getChannelId();
                processCache(channelId);
            }
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
        long channelId = program.getChannelId();
        
        dao.delete(program);
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode != null) {
            episode.setDuration(episodeMngr.calculateEpisodeDuration(episode));
            episodeMngr.save(episode);
            
            reorderEpisodePrograms(episodeId);
        }
        
        processCache(channelId);
    }
    
    public void delete(List<NnProgram> programs) {
    
        long channelId = 0;
        
        for (NnProgram program : programs) {
            
            long tmpChannelId = program.getChannelId();
            
            dao.delete(program);
            
            if (channelId != tmpChannelId) {
                channelId = tmpChannelId;
                processCache(channelId);
            }
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
            log.info("<<<<< retrieve program info from cache >>>>>");
            return result;
        }        
        
        log.info("nothing in the cache");        
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
        programs = dao.findProgramsByEpisode(Long.parseLong(storageId));
        log.info("find reference's real programs size:" + programs.size());        
        return programs;        
    }
    
    public String processSubEpisode(List<NnProgram> programs) {
        String result = "";
        NnProgram original = programs.get(0);        
        long cid = programs.get(0).getChannelId();
        List<TitleCard> cards = new TitleCardDao().findByChannel(cid); //order by channel id and entry id
        HashMap<String, TitleCard> cardMap = new HashMap<String, TitleCard>();        
        for (TitleCard c : cards) {
            String key = String.valueOf(c.getProgramId() + ";" + c.getType());
            cardMap.put(key, c);
        }
        String seq = programs.get(0).getSeq();
        String videoUrl = "";
        String name = "";
        String imageUrl = "";
        String card = "";
        for (int i=0; i<programs.size(); i++) { 
           NnProgram p = programs.get(i);
           String cardKey1 = String.valueOf(original.getId() + ";" + TitleCard.TYPE_BEGIN); 
           String cardKey2 = String.valueOf(p.getId() + ";" + TitleCard.TYPE_END);
           if (p.getSubSeq() != null && p.getSubSeq().length() > 0) {
               if (cardMap.containsKey(cardKey1)) {
                   card += "subepisode" + "%3A%20" + Long.parseLong(p.getSubSeq()) + "%0A";
                   card += cardMap.get(cardKey1).getPlayerSyntax() + "%0A--%0A";
                   cardMap.remove(cardKey1);
               }
               if (cardMap.containsKey(cardKey2)) {
                   card += "subepisode" + "%3A%20" + Long.parseLong(p.getSubSeq()) + "%0A";
                   card += cardMap.get(cardKey2).getPlayerSyntax() + "%0A--%0A";
                   cardMap.remove(cardKey2);
               }
           }           
           if (p.getContentType() == NnProgram.CONTENTTYPE_REFERENCE) {               
               List<NnProgram> reference = this.findRealPrograms(p.getStorageId());
               System.out.println("reference size:" + reference.size());
               if (reference.size() > 0) {
                   String referenceResult = this.processSubEpisode(reference);
                   referenceResult = referenceResult.replaceFirst(String.valueOf(reference.get(0).getId()), String.valueOf(p.getId()));
                   result += referenceResult;
               }
           } else {
               if (p.getSeq() == null || p.getSubSeq() == null) {
                   result += composeProgramInfoStr(p, null, null, null, null, null);
               } else { 
                   //it's another program
                  if ((!p.getSeq().equals(seq)) || (i == (programs.size()-1))) {
                     videoUrl = videoUrl.replaceFirst("\\|", "");
                     name = name.replaceFirst("\\|", "");
                     imageUrl = imageUrl.replaceFirst("\\|", "");
                     String intro = original.getIntro();
                     NnEpisode episode = new NnEpisodeManager().findById(original.getEpisodeId());
                     if (episode != null)
                         intro = episode.getIntro();
                     if ((i == programs.size()-1) && p.getSeq().equals(seq)) {
                         videoUrl += "|" + p.getFileUrl();
                         if (p.getStartTime() != null)
                            videoUrl += ";" + p.getStartTime();
                         else
                            videoUrl += ";"; 
                         if (p.getEndTime() != null)
                            videoUrl += ";" + p.getEndTime();
                         else
                            videoUrl += ";";         
                         name += "|" + p.getName();
                         imageUrl += "|" + p.getImageUrl();                         
                     }
                     result += composeProgramInfoStr(original, name, intro, imageUrl, videoUrl, card);
                     if ((i == programs.size()-1) && !p.getSeq().equals(seq)) {
                         result += composeProgramInfoStr(p, p.getName(), p.getIntro(), p.getImageUrl(), p.getFileUrl(), card);
                     }
                     //reset
                     seq = p.getSeq();                     
                     name = p.getName() + "|";
                     imageUrl = p.getImageUrl() + "|";
                     videoUrl = p.getFileUrl() + "|";
                     card = "";
                     original = p;
                  } else {
                      //format: videoUrl;startTime;endTime|videoUrl;startTime;endTime
                      //each video is separated by "|"
                      videoUrl += "|" + p.getFileUrl();
                      if (p.getStartTime() != null)
                         videoUrl += ";" + p.getStartTime();
                      else
                         videoUrl += ";"; 
                      if (p.getEndTime() != null)
                         videoUrl += ";" + p.getEndTime();
                      else
                         videoUrl += ";";         
                      name += "|" + p.getName();
                      imageUrl += "|" + p.getImageUrl();
                  }
               }
           }
        }
        return result;
    }
    
    //sub-episode implementation
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
                result += this.composeProgramInfoStr(p, null, null, null, null, null);
            }
            return result;
        }
        result += this.processSubEpisode(programs);
        return result;
    }

    //TODO clean up a little bit
    public String composeProgramInfoStr(NnProgram p, String name, String intro, String imageUrl, String videoUrl, String card) {
        String output = "";        
        String regexCache = "^(http|https)://(9x9cache.s3.amazonaws.com|s3.amazonaws.com/9x9cache)";
        String regexPod = "^(http|https)://(9x9pod.s3.amazonaws.com|s3.amazonaws.com/9x9pod)";
        String cache = "http://cache.9x9.tv";
        String pod = "http://pod.9x9.tv";
        String url1 = p.getFileUrl();
        String url2 = ""; //not used for now
        String url3 = ""; //not used for now
        String url4 = p.getAudioFileUrl();
        if (imageUrl == null)
            imageUrl = p.getImageUrl();
        String imageLargeUrl = p.getImageUrl();
        if (imageUrl == null) {imageUrl = "";}
        if (imageLargeUrl == null) {imageLargeUrl = "";}
        //TODO if (config.getValue().equals(MsoConfig.CDN_AKAMAI)) { 
        if (url1 != null) {
            url1 = url1.replaceFirst(regexCache, cache);
            url1 = url1.replaceAll(regexPod, pod);
        }
        if (imageUrl != null) {
            imageUrl = imageUrl.replaceFirst(regexCache, cache);
            imageUrl = imageUrl.replaceAll(regexPod, pod);
        }
        if (imageLargeUrl != null) {
            imageLargeUrl = imageLargeUrl.replaceFirst(regexCache, cache);
            imageLargeUrl = imageLargeUrl.replaceAll(regexPod, pod);
        }
                
        //intro
        if (intro == null)            
            intro = p.getIntro();            
        if (intro != null) {
            int introLenth = (intro.length() > 256 ? 256 : intro.length()); 
            intro = intro.replaceAll("\\s", " ");                
            intro = intro.substring(0, introLenth);
        }
        
        url1 = (videoUrl != null) ? videoUrl : null;
        if (name == null) {
            name = p.getName();
            if (name != null)
            	name = name.replace("|", "\\|");
        }
        //the rest
        String[] ori = {String.valueOf(p.getChannelId()), 
                        String.valueOf(p.getId()), 
                        name, 
                        intro,
                        String.valueOf(p.getContentType()), 
                        p.getDuration(),
                        imageUrl,
                        "",
                        url1, //video url
                        url2, 
                        url3, 
                        url4,            
                        String.valueOf(p.getUpdateDate().getTime()),
                        p.getComment(),
                        card};
        output = output + NnStringUtil.getDelimitedStr(ori);
        output = output.replaceAll("null", "");
        output = output + "\n";
        return output;
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
        
        List<NnChannel> channelFavorites = channelMngr.findByUserAndHisFavorite(user, 0);
        if (channelFavorites.size() == 0) {
            return empty;
        }
        
        NnChannel channelFavorite = channelFavorites.get(0);
        if (channelFavorite.getContentType() != NnChannel.CONTENTTYPE_FAVORITE) {
            return empty;
        }
        
        return findByChannelId(channelFavorite.getId());
    }
}

