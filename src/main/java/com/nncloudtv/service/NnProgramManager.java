package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnProgramDao;
import com.nncloudtv.dao.TitleCardDao;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.TitleCard;

@Service
public class NnProgramManager {
    
    protected static final Logger log = Logger.getLogger(NnProgramManager.class.getName());
    
    private NnProgramDao dao = new NnProgramDao();
    
    public void create(NnChannel channel, NnProgram program) {        
        Date now = new Date();
        program.setCreateDate(now);
        program.setUpdateDate(now);
        program.setChannelId(channel.getId());
        dao.save(program);
        this.processCache(channel.getId());
        
        //!!!!! clean plus "hook, auto share to facebook"
        //set channel count
        int count = channel.getProgramCnt() + 1;
        channel.setProgramCnt(count);
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
        Date now = new Date();
        if (program.getCreateDate() == null)
            program.setCreateDate(now);
        program.setUpdateDate(now); // NOTE: a trying to modify program update time (from admin) will be omitted by this, use "untouched" save() instread
        program = dao.save(program);
        this.processCache(program.getChannelId());
        return program;
    }

    public void delete(NnProgram program) {
        dao.delete(program);        
        this.processCache(program.getChannelId());        
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
    
    public String subEpisode() {
        return "";
    }
    
    public List<NnProgram> findRealPrograms(String storageId) {
        List<NnProgram> programs = new ArrayList<NnProgram>();
        String[] info = storageId.split(";");
        if (info.length > 0) {
            long channelId = Long.parseLong(info[0]);
            String seq = info[1];
            programs = dao.findByChannelAndSeq(channelId, seq);
        }
        return programs;        
    }
    
    public String processSubEpisode(List<NnProgram> programs) {
        String result = "";
        NnProgram original = programs.get(0);        
        long cid = programs.get(0).getChannelId();
        List<TitleCard> cards = new TitleCardDao().findByChannel(cid); //order by channel id and entry id
        System.out.println("--- title card size ---" + cards.size());
        String seq = programs.get(0).getSeq();
        int cardIdx = 0;
        String videoUrl = "";
        for (int i=0; i<programs.size(); i++) {
           NnProgram p = programs.get(i);
           if (p.getContentType() == NnProgram.CONTENTTYPE_REFERENCE) {               
               List<NnProgram> reference = this.findRealPrograms(p.getStorageId());
               System.out.println("reference size:" + reference.size());
               if (reference.size() > 0) { //keep the stored program id, for deletion
                   for (NnProgram ref : reference) {
                       ref.setId(p.getId());
                   }
                   result += this.processSubEpisode(reference);
               }
           }
           if (p.getContentType() != NnProgram.CONTENTTYPE_REFERENCE) {
               if (p.getSeq() == null || p.getSubSeq() == null) {
                   result += composeProgramInfoStr(p, p.getFileUrl(), null);
               } else { 
                   //it's another program
                  if (!p.getSeq().equals(seq) || (i == programs.size()-1)) {
                     String card = "";
                     String titleSeq = original.getSeq();
                     for (int j=cardIdx; j<cards.size(); j++) {
                        if (cards.get(j).getSeq().equals(titleSeq)) {
                           card += cards.get(j).getPlayerSyntax() + "%0A--%0A";
                           cardIdx++;
                        }
                     }
                     videoUrl = videoUrl.replaceFirst("\\|", "");
                     result += composeProgramInfoStr(original, videoUrl, card); 
                     //reset
                     seq = p.getSeq();
                     videoUrl = "";
                     card = "";
                     original = p;
                  } else {
                      //format: videoUrl;startTime;endTime|videoUrl;startTime;endTime
                      //each video is separated by "|"
                      videoUrl += "|" + p.getFileUrl();
                      if (p.getStartTime() != null)
                         videoUrl += ";" + p.getStartTime();
                      else
                         videoUrl += ";" + ""; 
                      if (p.getEndTime() != null)
                         videoUrl += ";" + p.getEndTime();
                      else
                         videoUrl += ";" + "";                  
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
                result += this.composeProgramInfoStr(p, null, null);
            }
            return result;
        }
        result += this.processSubEpisode(programs);
        return result;
    }

    public String composeProgramInfoStr(NnProgram p, String videoUrl, String card) {
        String output = "";        
        String regexCache = "^(http|https)://(9x9cache.s3.amazonaws.com|s3.amazonaws.com/9x9cache)";
        String regexPod = "^(http|https)://(9x9pod.s3.amazonaws.com|s3.amazonaws.com/9x9pod)";
        String cache = "http://cache.9x9.tv";
        String pod = "http://pod.9x9.tv";
        String url1 = p.getFileUrl();
        String url2 = ""; //not used for now
        String url3 = ""; //not used for now
        String url4 = p.getAudioFileUrl();
        String imageUrl = p.getImageUrl();
        String imageLargeUrl = p.getImageUrl();
        if (imageUrl == null) {imageUrl = "";}
        if (imageLargeUrl == null) {imageLargeUrl = "";}
        //!!!!
        //if (config.getValue().equals(MsoConfig.CDN_AKAMAI)) {
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
        //}
                
        //intro
        String intro = p.getIntro();            
        if (intro != null) {
            int introLenth = (intro.length() > 256 ? 256 : intro.length()); 
            intro = intro.replaceAll("\\s", " ");                
            intro = intro.substring(0, introLenth);
        }
        
        if (videoUrl != null)
            url1 = videoUrl;
        //the rest
        String[] ori = {String.valueOf(p.getChannelId()), 
                        String.valueOf(p.getId()), 
                        p.getName(), 
                        intro,
                        String.valueOf(p.getContentType()), 
                        p.getDuration(),
                        imageUrl,
                        imageLargeUrl,
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

    public List<NnProgram> findByChannel(long channelId) {
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
}
