package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Poi;

public class PoiDao extends GenericDao<Poi> {
    
    public PoiDao() {
        super(Poi.class);
    }
    
    public List<Poi> findByPointId(long pointId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        List<Poi> results = new ArrayList<Poi>();
        try {
            Query query = pm.newQuery(Poi.class);
            query.setFilter("pointId == pointIdParam");
            query.declareParameters("long pointIdParam");
            query.setOrdering("updateDate desc");
            @SuppressWarnings("unchecked")
            List<Poi> poiMaps = (List<Poi>) query.execute(pointId);
            results = (List<Poi>) pm.detachCopyAll(poiMaps);
        } finally {
            pm.close();
        }
        return results;
    }

}