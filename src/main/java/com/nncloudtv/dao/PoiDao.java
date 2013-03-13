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
        
    public List<Poi> findByChannel(long channelId) {
        List<Poi> detached = new ArrayList<Poi>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            String sql = "select * " +
                           "from poi " +
                         " where programId in (select id from Poi where channelId=" + channelId + ")" +
                         " order by startTime" ; 
                        
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(Poi.class);
            @SuppressWarnings("unchecked")
            List<Poi> results = (List<Poi>) query.execute();
            detached = (List<Poi>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        } 
        return detached;        
    }

    public List<Poi> findByProgram(long programId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        List<Poi> results = new ArrayList<Poi>();
        try {
            Query query = pm.newQuery(Poi.class);
            query.setFilter("programId == programIdParam");
            query.declareParameters("long programIdParam");
            query.setOrdering("startTime asc");
            @SuppressWarnings("unchecked")
            List<Poi> pois = (List<Poi>) query.execute(programId);
            results = (List<Poi>) pm.detachCopyAll(pois);
        } finally {
            pm.close();
        }
        return results;
    }
    
}
