package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.nncloudtv.dao.NnEpisodeDao;
import com.nncloudtv.lib.QueueFactory;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnChannelPref;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.TitleCard;
import com.nncloudtv.web.json.facebook.FBPost;

public class NnEpisodeManager {
    
    protected static final Logger log = Logger.getLogger(NnEpisodeManager.class.getName());
    
    private NnEpisodeDao dao = new NnEpisodeDao();
    
    public NnEpisode findById(long id) {
        return dao.findById(id);
    }
    
    public NnEpisode save(NnEpisode episode) {
        
        Date now = new Date();
        
        episode.setUpdateDate(now);
        
        new NnProgramManager().resetCache(episode.getChannelId());
        
        return dao.save(episode);
        
    }
    
    public NnEpisode create(NnEpisode episode) {
        autoShare(episode);
        return save(episode);
    }
    
    public List<NnEpisode> save(List<NnEpisode> episodes) {
        
        Date now = new Date();
        List<Long> channelIds = new ArrayList<Long>();
        
        for (NnEpisode episode : episodes) {
            episode.setUpdateDate(now);
            
            if (channelIds.indexOf(episode.getChannelId()) < 0) {
                channelIds.add(episode.getChannelId());
            }
        }
        
        NnProgramManager programMngr = new NnProgramManager();
        
        log.info("channel count = " + channelIds.size());
        for (Long channelId : channelIds) {
            programMngr.resetCache(channelId);
        }
        
        return dao.saveAll(episodes);
    }
    
    public NnEpisode save(NnEpisode episode, boolean rerun) {
    
        // rerun - to make episode on top again and public
        if (rerun) {
            
            log.info("rerun!");
            
            episode.setPublishDate(new Date());
            episode.setPublic(true);
            episode.setSeq(0);
            save(episode);
            
            reorderChannelEpisodes(episode.getChannelId());
            
            return episode;
        }
        
        log.info("publishDate = " + episode.getPublishDate());
        
        return save(episode);
    }
    
    public NnEpisode findByAdId(long adId) {
        return dao.findByAdId(adId);
    }
    
    public List<NnEpisode> findByChannelId(long channelId) {
    
        return dao.findByChannelId(channelId);
    }
    
    public int getProgramCnt(NnEpisode episode) {
        
        if (episode == null) {
            return 0;
        }
        
        NnProgramManager programMngr = new NnProgramManager();
        
        List<NnProgram> programs = programMngr.findByEpisodeId(episode.getId());
        
        return programs.size();
    }
    
    public Comparator<NnEpisode> getEpisodePublicSeqComparator() {
    
        class NnEpisodePublicSeqComparator implements Comparator<NnEpisode> {
            
            public int compare(NnEpisode episode1, NnEpisode episode2) {
                
                if (episode1.isPublic() == episode2.isPublic()) {
                    
                    return (episode1.getSeq() - episode2.getSeq());
                    
                } else if (episode1.isPublic() == false) {
                    
                    return -1;
                            
                }
                
                return 1;
            }
        }
        
        return new NnEpisodePublicSeqComparator();
    }
    
    public Comparator<NnEpisode> getEpisodeSeqComparator() {
        
        class NnEpisodeSeqComparator implements Comparator<NnEpisode> {
            
            public int compare(NnEpisode episode1, NnEpisode episode2) {
                
                return (episode1.getSeq() - episode2.getSeq());
                
            }
        }
        
        return new NnEpisodeSeqComparator();
    }
    
    public void reorderChannelEpisodes(long channelId) {
        
        List<NnEpisode> episodes = findByChannelId(channelId);
        Collections.sort(episodes, getEpisodeSeqComparator());
        
        for (int i = 0; i < episodes.size(); i++) {
            
            episodes.get(i).setSeq(i + 1);
        }
        
        save(episodes);
    }
    
    public void delete(NnEpisode episode) {
    
        new NnProgramManager().resetCache(episode.getChannelId());
        dao.delete(episode);
    }
    
    public List<NnEpisode> list(long page, long rows, String sidx, String sord,
            String filter) {
    
        return dao.list(page, rows, sidx, sord, filter);
    }

    public List<NnEpisode> findPlayerEpisodes(long channelId) {
        return dao.findPlayerEpisode(channelId);
    }
    
    public int calculateEpisodeDuration(NnEpisode episode) {
    
        NnProgramManager programMngr = new NnProgramManager();
        TitleCardManager titleCardMngr = new TitleCardManager();
        List<NnProgram> programs = programMngr.findByEpisodeId(episode.getId());
        List<TitleCard> titleCards = titleCardMngr.findByEpisodeId(episode.getId());
        
        int totalDuration = 0;
        
        for (NnProgram program : programs) {
            totalDuration += program.getDurationInt();
        }
        
        for (TitleCard titleCard : titleCards) {
            totalDuration += titleCard.getDurationInt();
        }
        
        return totalDuration;
    }
    
    // hook, auto share to facebook
    private void autoShare(NnEpisode episode) {
        
        FBPost fbPost = new FBPost(episode.getName(), episode.getIntro(), episode.getImageUrl());
        String url = "http://" + MsoConfigManager.getServerDomain() + "/#!ch=" + episode.getChannelId() + "!ep=e" + episode.getId();
        fbPost.setLink(url);
        log.info("share link: " + url);
        
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(episode.getChannelId());
        if (channel == null) {
            return ;
        }
        
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findById(channel.getUserId());
        if (user == null) {
            return ;
        }
        
        MessageSource messageSource = new ClassPathXmlApplicationContext("locale.xml");
        if (user.getLang().equals("zh")) {
            //fbPost.setCaption("Published an episode on 9x9.tv");
            fbPost.setCaption(messageSource.getMessage("cms.autosharing.episode_added", null, Locale.TRADITIONAL_CHINESE));
        } else {
            //fbPost.setCaption("已在9x9.tv發佈節目");
            fbPost.setCaption(messageSource.getMessage("cms.autosharing.episode_added", null, Locale.ENGLISH));
        }
        
        NnChannelPrefManager prefMngr = new NnChannelPrefManager();
        List<NnChannelPref> prefList = prefMngr.findByChannelIdAndItem(episode.getChannelId(), NnChannelPref.FB_AUTOSHARE);
        String facebookId, accessToken;
        String[] parsedObj;
        
        for (NnChannelPref pref : prefList) {
            parsedObj = prefMngr.parseFacebookAutoshare(pref.getValue());
            if (parsedObj == null) {
                continue;
            }
            facebookId = parsedObj[0];
            accessToken = parsedObj[1];
            fbPost.setFacebookId(facebookId);
            fbPost.setAccessToken(accessToken);
            
            QueueFactory.add("/fb/postToFacebook", fbPost);
        }
        
    }
    
}
