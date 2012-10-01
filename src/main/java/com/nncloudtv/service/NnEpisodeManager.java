package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnEpisodeDao;
import com.nncloudtv.lib.NnStringUtil;
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
    
    public NnEpisode save(NnEpisode episode, boolean rerun) {
    
        // rerun: to make episode on top again and public
        if (rerun) {
            
            NnProgramManager programMngr = new NnProgramManager();
            List<NnProgram> programs = programMngr.findByEpisodeId(episode.getId());
            
            for (NnProgram program : programs) {
                program.setSeq(0);
            }
            programMngr.save(programs);
            
            episode.setPublishDate(new Date());
            episode.setIsPublic(rerun);
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
    
        NnProgramManager programMngr = new NnProgramManager();
        List<NnEpisode> episodes = findByChannelIdSorted(channelId);
        List<NnEpisode> removes = new ArrayList<NnEpisode>();
        
        // remove empty episode which will not be sorted
        for (NnEpisode episode : episodes) {
            
            if (episode.getSeq() == 0 && getProgramCnt(episode) == 0) {
                removes.add(episode);
            }
        }
        episodes.remove(removes);
        
        List<NnProgram> reorderedPrograms = new ArrayList<NnProgram>(); 
        for (int i = 0; i < episodes.size(); i++) {
            
            List<NnProgram> programs = programMngr.findByEpisodeId(episodes.get(i).getId());
            
            for (NnProgram program : programs) {
                
                program.setSeq(i + 1);
            }
            reorderedPrograms.addAll(programs);
        }
        programMngr.save(reorderedPrograms);
    }
    
    public void delete(NnEpisode episode) {
    
        dao.delete(episode);
    }
    
    public List<NnEpisode> list(long page, long rows, String sidx, String sord,
            String filter) {
    
        return dao.list(page, rows, sidx, sord, filter);
    }
    
    public int calculateEpisodeDuration(NnEpisode episode) {
    
        NnProgramManager programMngr = new NnProgramManager();
        List<NnProgram> programs = programMngr.findByEpisodeId(episode.getId());
        
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
        }
        
        return totalDuration;
    }
    
    public String getEpisodePlaybackUrl(NnEpisode episode) {
    
        return NnStringUtil.getPlaybackUrl(episode.getChannelId(), episode.getId());
    }
}
