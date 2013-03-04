package com.nncloudtv.service;

import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.PoiDao;
import com.nncloudtv.model.Poi;

public class PoiManager {
    protected static final Logger log = Logger.getLogger(PoiManager.class.getName());
    
    private PoiDao dao = new PoiDao();

    public List<Poi> findByChannel(long channelId) {
        return dao.findByChannel(channelId);
    }
    
    public List<Poi> findByProgram(long programId) {
        return dao.findByProgram(programId);
    }
    
}
