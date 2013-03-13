package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.PoiMap;

public class PoiMapDao extends GenericDao<PoiMap> {
    
    public PoiMapDao() {
        super(PoiMap.class);
    }
    
    public List<PoiMap> findByPoiId(long poiId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        List<PoiMap> results = new ArrayList<PoiMap>();
        try {
            Query query = pm.newQuery(PoiMap.class);
            query.setFilter("poiId == poiIdParam");
            query.declareParameters("long poiIdParam");
            query.setOrdering("updateDate desc");
            @SuppressWarnings("unchecked")
            List<PoiMap> poiMaps = (List<PoiMap>) query.execute(poiId);
            results = (List<PoiMap>) pm.detachCopyAll(poiMaps);
        } finally {
            pm.close();
        }
        return results;
    }

}