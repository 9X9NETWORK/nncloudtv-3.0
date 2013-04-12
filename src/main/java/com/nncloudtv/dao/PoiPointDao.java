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
            String sql = "select * " +
                           "from poi " +
                         " where programId in (select id from Poi where channelId=" + channelId + ")" +
                         " order by startTime" ; 
                        
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

    public List<PoiPoint> findByProgram(long programId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        List<PoiPoint> results = new ArrayList<PoiPoint>();
        try {
            Query query = pm.newQuery(PoiPoint.class);
            query.setFilter("programId == programIdParam");
            query.declareParameters("long programIdParam");
            //query.setOrdering("startTime asc");
            @SuppressWarnings("unchecked")
            List<PoiPoint> pois = (List<PoiPoint>) query.execute(programId);
            results = (List<PoiPoint>) pm.detachCopyAll(pois);
        } finally {
            pm.close();
        }
        return results;
    }
    
}
