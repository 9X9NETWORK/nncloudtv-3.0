package com.nncloudtv.service;

import java.util.ArrayList;
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
    
    public void save(NnEpisode episode) {
        
        Date now = new Date();
        
        episode.setUpdateDate(now);
        
        dao.save(episode);
        
    }
    
    public NnEpisode findByAdId(long adId) {
        return dao.findByAdId(adId);
    }
    
    public List<NnEpisode> findByChannelId(long channelId) {
    
        return dao.findByChannelId(channelId);
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
    
    public Comparator<NnEpisode> getEpisodeSeqComparator() {
        
        class NnEpisodeSeqComparator implements Comparator<NnEpisode> {
            
            public int compare(NnEpisode episode1, NnEpisode episode2) {
                
                int seq1 = (episode1.getSeq() == 0) ? getEpisodeSeq(episode1) : episode1.getSeq();
                int seq2 = (episode2.getSeq() == 0) ? getEpisodeSeq(episode2) : episode2.getSeq();
                
                return (seq1 - seq2);
                
            }
        }
        
        return new NnEpisodeSeqComparator();
    }
}
