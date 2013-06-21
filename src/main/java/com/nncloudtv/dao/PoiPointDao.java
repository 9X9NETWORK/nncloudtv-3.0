package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.PoiPoint;

public class PoiPointDao extends GenericDao<PoiPoint> {

    protected static final Logger log = Logger.getLogger(PoiPointDao.class.getName());
    
    public PoiPointDao() {
        super(PoiPoint.class);
    }
    
    public List<PoiPoint> findByChannel(long channelId) {
        
        List<PoiPoint> detached = new ArrayList<PoiPoint>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        try {
            String sql = "select * from poi_point where targetId = " + channelId +
                           " and type = " + PoiPoint.TYPE_CHANNEL;
                        
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(PoiPoint.class);
            @SuppressWarnings("unchecked")
            List<PoiPoint> results = (List<PoiPoint>) query.execute();
            if (results != null && results.size() > 0) {
                detached = (List<PoiPoint>)pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        
        return detached;        
    }
    
    public List<PoiPoint> findByProgram(long programId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<PoiPoint> results = new ArrayList<PoiPoint>();
        
        try {
            String sql = "select * from poi_point where targetId = " + programId +
                           " and type = " + PoiPoint.TYPE_SUBEPISODE;
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(PoiPoint.class);
            @SuppressWarnings("unchecked")
            List<PoiPoint> points = (List<PoiPoint>) query.execute();
            if (points != null && points.size() > 0) {
                results = (List<PoiPoint>) pm.detachCopyAll(points);
            }
        } finally {
            pm.close();
        }
        
        return results;
    }

    //current: poi_point is active and poi is within date range
    public List<PoiPoint> findCurrentByChannel(long channelId) {
        List<PoiPoint> detached = new ArrayList<PoiPoint>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        /*
        select * 
          from poi_point a1  
        inner join  
       (select distinct pp.id
        from poi_point pp, poi poi, poi_event e 
       where pp.active = true
         and pp.targetId = 1 
         and poi.eventId = e.id
         and poi.pointId = pp.id
         and pp.type = 3 
         and poi.pointId = pp.id   
         and now() > poi.startDate 
         and now() < poi.endDate) a2
            on a1.id=a2.id         
        */
        try {
            String sql = "select * " + 
                          " from poi_point a1 " +   
                         " inner join " +  
                        "(select distinct pp.id " +
                          " from poi_point pp, poi poi, poi_event e " + 
                         " where pp.active = true " +
                           " and pp.targetId = " + channelId +
                           " and poi.eventId = e.id " +
                           " and poi.pointId = pp.id " +
                           " and pp.type = " + PoiPoint.TYPE_CHANNEL +
                           " and poi.pointId = pp.id ) a2 " +   
                           //" and now() > poi.startDate " +
                           //" and now() < poi.endDate) a2" +
                             " on a1.id=a2.id";
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(PoiPoint.class);
            @SuppressWarnings("unchecked")
            List<PoiPoint> results = (List<PoiPoint>) query.execute();            
            detached = (List<PoiPoint>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        } 
        return detached;                            
    }
    
    //current: poi_point is active and poi is within date range
    public List<PoiPoint> findCurrentByProgram(long programId) {
        List<PoiPoint> detached = new ArrayList<PoiPoint>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        /*
        select * 
          from poi_point a1  
        inner join  
       (select distinct pp.id
        from poi_point pp, poi poi, poi_event e 
       where pp.active = true
         and pp.targetId = 1 
         and poi.eventId = e.id
         and poi.pointId = pp.id
         and pp.type = 5 
         and poi.pointId = pp.id   
         and now() > poi.startDate 
         and now() < poi.endDate) a2
            on a1.id=a2.id         
        */
        try {
            String sql = "select * " + 
                          " from poi_point a1 " +   
                         " inner join " +  
                        "(select distinct pp.id " +
                          " from poi_point pp, poi poi, poi_event e " + 
                         " where pp.active = true " +
                           " and pp.targetId = " + programId +
                           " and poi.eventId = e.id " +
                           " and poi.pointId = pp.id " +
                           " and pp.type = " + PoiPoint.TYPE_SUBEPISODE +
                           " and poi.pointId = pp.id) a2 " +   
                           //" and now() > poi.startDate " +
                           //" and now() < poi.endDate) a2" +
                             " on a1.id=a2.id";
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(PoiPoint.class);
            @SuppressWarnings("unchecked")
            List<PoiPoint> results = (List<PoiPoint>) query.execute();            
            detached = (List<PoiPoint>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        } 
        return detached;                            
    }
    
}
