package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Poi;
import com.nncloudtv.model.PoiPoint;

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
    
    public List<Poi> findCurrentByChannel(long channelId) {
        List<Poi> detached = new ArrayList<Poi>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        /*
        select * 
          from poi a1  
        inner join  
       (select distinct pp.id
        from poi_point pp, poi poi 
       where pp.active = true
         and pp.targetId = 1 
         and poi.pointId = pp.id
         and pp.type = 3 
         and poi.pointId = pp.id   
         and now() > poi.startDate 
         and now() < poi.endDate) a2
            on a1.id=a2.id         
        */
        try {
            String sql = "select * " + 
                          " from poi a1 " +   
                         " inner join " +  
                        "(select distinct pp.id " +
                          " from poi_point pp, poi poi " + 
                         " where pp.active = true " +
                           " and pp.targetId = " + channelId +
                           " and poi.pointId = pp.id " +
                           " and pp.type = " + PoiPoint.TYPE_CHANNEL +
                           " and poi.pointId = pp.id" +   
                           " and now() > poi.startDate " +
                           " and now() < poi.endDate) a2" +
                             " on a1.id=a2.id";
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(PoiPoint.class);
            @SuppressWarnings("unchecked")
            List<Poi> results = (List<Poi>) query.execute();            
            detached = (List<Poi>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        } 
        return detached;                            
    }

    public List<Poi> findCurrentByProgram(long programId) {
        List<Poi> detached = new ArrayList<Poi>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        /*
        select * 
          from poi a1  
        inner join  
       (select distinct pp.id
        from poi_point pp, poi poi 
       where pp.active = true
         and pp.targetId = 1 
         and poi.pointId = pp.id
         and pp.type = 5 
         and poi.pointId = pp.id   
         and now() > poi.startDate 
         and now() < poi.endDate) a2
            on a1.id=a2.id         
        */
        try {
            String sql = "select * " + 
                          " from poi a1 " +   
                         " inner join " +  
                        "(select distinct pp.id " +
                          " from poi_point pp, poi poi " + 
                         " where pp.active = true " +
                           " and pp.targetId = " + programId +
                           " and poi.pointId = pp.id " +
                           " and pp.type = " + PoiPoint.TYPE_SUBEPISODE +
                           " and poi.pointId = pp.id" +   
                           " and now() > poi.startDate " +
                           " and now() < poi.endDate) a2" +
                             " on a1.id=a2.id";
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(PoiPoint.class);
            @SuppressWarnings("unchecked")
            List<Poi> results = (List<Poi>) query.execute();            
            detached = (List<Poi>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        } 
        return detached;                                    
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