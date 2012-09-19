package com.nncloudtv.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnEpisodeDao;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;

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
        
        NnProgramManager programMngr = new NnProgramManager();
        
        List<NnProgram> programs = programMngr.findByEpisodeId(episode.getId());
        if (programs.size() == 0) {
            return 0;
        }
        
        return programs.get(0).getSeqInt();
        
    }
    
    public int getProgramCnt(NnEpisode episode) {
        
        if (episode == null) {
            return 0;
        }
        
        NnProgramManager programMngr = new NnProgramManager();
        
        List<NnProgram> programs = programMngr.findByEpisodeId(episode.getId());
        
        return programs.size();
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
    
        NnProgramManager programMngr = new NnProgramManager();
        List<NnEpisode> episodes = findByChannelIdSorted(channelId);
        
        // remove empty episode
        for (NnEpisode episode : episodes) {
            
            if (episode.getSeq() == 0 && getProgramCnt(episode) == 0) {
                episodes.remove(episode);
            }
        }
        
        for (int i = 0; i < episodes.size(); i++) {
            
            List<NnProgram> programs = programMngr.findByEpisodeId(episodes.get(i).getId());
            
            for (NnProgram program : programs) {
                
                program.setSeq(i + 1);
            }
            programMngr.save(programs);
        }
        
    }
}
