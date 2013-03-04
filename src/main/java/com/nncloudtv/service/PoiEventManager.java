package com.nncloudtv.service;

import java.util.logging.Logger;

import com.nncloudtv.dao.PoiEventDao;
import com.nncloudtv.model.PoiEvent;

public class PoiEventManager {
    protected static final Logger log = Logger.getLogger(PoiEventManager.class.getName());
    
    private PoiEventDao dao = new PoiEventDao();

    public PoiEvent findByPoi(long poiId) {
        return dao.findByPoi(poiId);
    }
    
}
