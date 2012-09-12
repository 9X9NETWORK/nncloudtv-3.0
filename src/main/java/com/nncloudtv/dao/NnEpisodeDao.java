package com.nncloudtv.dao;

import java.util.logging.Logger;

import com.nncloudtv.model.NnEpisode;

public class NnEpisodeDao extends GenericDao<NnEpisode> {
    protected static final Logger log = Logger.getLogger(NnEpisodeDao.class.getName());
    
    public NnEpisodeDao() {
        super(NnEpisode.class);
    }
        
}
