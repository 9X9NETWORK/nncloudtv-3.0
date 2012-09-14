package com.nncloudtv.service;

import java.util.logging.Logger;

import com.nncloudtv.dao.NnEpisodeDao;
import com.nncloudtv.model.NnEpisode;

public class NnEpisodeManager {
    
    protected static final Logger log = Logger.getLogger(NnEpisodeManager.class.getName());
    
    private NnEpisodeDao dao = new NnEpisodeDao();
    
    public NnEpisode findById(long id) {
        return dao.findById(id);
    }
        
}
