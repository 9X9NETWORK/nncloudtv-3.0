package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Poi;

public class PoiDao extends GenericDao<Poi> {
    
    protected static final Logger log = Logger.getLogger(PoiDao.class.getName());
    
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
            List<Poi> pois = (List<Poi>) query.execute(pointId);
            if (pois != null && pois.size() > 0) {
                results = (List<Poi>) pm.detachCopyAll(pois);
            }
        } finally {
            pm.close();
        }
        return results;
    }
    
    public List<Poi> findByCompaignId(long campaignId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        List<Poi> results = new ArrayList<Poi>();
        try {
            Query query = pm.newQuery(Poi.class);
            query.setFilter("campaignId == campaignIdParam");
            query.declareParameters("long campaignIdParam");
            query.setOrdering("updateDate desc");
            @SuppressWarnings("unchecked")
            List<Poi> pois = (List<Poi>) query.execute(campaignId);
            if (pois != null && pois.size() > 0) {
                results = (List<Poi>) pm.detachCopyAll(pois);
            }
        } finally {
            pm.close();
        }
        return results;
    }

}