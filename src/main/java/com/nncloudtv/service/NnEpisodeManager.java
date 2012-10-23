package com.nncloudtv.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnEpisodeDao;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.TitleCard;

public class NnEpisodeManager {
    
    protected static final Logger log = Logger.getLogger(NnEpisodeManager.class.getName());
    
    private NnEpisodeDao dao = new NnEpisodeDao();
    
    public NnEpisode findById(long id) {
        return dao.findById(id);
    }
    
    public NnEpisode save(NnEpisode episode) {
        
        Date now = new Date();
        
        episode.setUpdateDate(now);
        
        return dao.save(episode);
        
    }
    
    public List<NnEpisode> save(List<NnEpisode> episodes) {
        
        Date now = new Date();
        
        for (NnEpisode episode : episodes) {
            episode.setUpdateDate(now);
        }
        
        return dao.saveAll(episodes);
    }
    
    public NnEpisode save(NnEpisode episode, boolean rerun) {
    
        // rerun - to make episode on top again and public
        if (rerun) {
            
            episode.setPublishDate(new Date());
            episode.setPublic(true);
            episode.setSeq(0);
            save(episode);
            
            reorderChannelEpisodes(episode.getChannelId());
        }
        
        return save(episode);
    }
    
    public NnEpisode findByAdId(long adId) {
        return dao.findByAdId(adId);
    }
    
    public List<NnEpisode> findByChannelId(long channelId) {
    
        return dao.findByChannelId(channelId);
    }
    
    public void populateEpisodesSeq(List<NnEpisode> episodes) {
        
        if (episodes == null) {
            return;
        }
        
        for (NnEpisode episode : episodes) {
            
            episode.setSeq(getEpisodeSeq(episode));
        }
    }
    
    public int getEpisodeSeq(NnEpisode episode) {
        
        if (episode == null) {
            return 0;
        }
        
        if (episode.getSeq() == 0) {
            NnProgramManager programMngr = new NnProgramManager();
            NnProgram program = programMngr.findOneByEpisodeId(episode.getId());
            if (program == null) {
                return 0;
            }
            return program.getSeqInt();
        } else {
            return episode.getSeq();
        }
        
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
    
    public List<NnEpisode> findByChannelIdSorted(long channelId) {
        
        List<NnEpisode> episodes = findByChannelId(channelId);
        populateEpisodesSeq(episodes);
        Collections.sort(episodes, getEpisodeSeqComparator());
        
        return episodes;
    }
    
    public void reorderChannelEpisodes(long channelId) {
        
        List<NnEpisode> episodes = findByChannelIdSorted(channelId);
        
        for (int i = 0; i < episodes.size(); i++) {
            
            episodes.get(i).setSeq(i + 1);
        }
        
        save(episodes);
    }
    
    public void delete(NnEpisode episode) {
    
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
        List<TitleCard> titleCards = null;
        
        int totalDuration = 0;
        
        for (NnProgram program : programs) {
            
            int startTime = program.getStartTimeInt();
            int endTime = program.getEndTimeInt();
            
            int delta = 0;
            if (startTime == 0 && endTime == 0) {
                delta = program.getDurationInt();
            } else {
                delta = (endTime > startTime) ? (endTime - startTime) : 0;
            }
            totalDuration += delta;
            
            delta = 0;
            titleCards = titleCardMngr.findByProgramId(program.getId());
            for (TitleCard titleCard : titleCards) {
                delta += titleCard.getDurationInt();
            }
            totalDuration += delta;
        }
        
        return totalDuration;
    }
}
