package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnDevice;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.Pdr;

public class PdrDao {

    protected static final Logger log = Logger.getLogger(PdrDao.class.getName());
    
    /*
    private PersistenceManager getPersistenceManager() {
        PersistenceManager pm = null;
        try {
            pm = PMF.getAnalytics().getPersistenceManager();
        } catch (Throwable t) {            
            if (t.getCause() instanceof JDOFatalDataStoreException) {
                t.printStackTrace();
                MsoConfigManager.enableEmergencyRO();
                try {
                    pm = PMF.getAnalyticsSlave().getPersistenceManager();
                } catch (Throwable t1) { 
                }
            }    
        }
        return pm;        
    }
    */
    
    public Pdr save(Pdr pdr) {        
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        if (pm == null) return null;
        try {
            pm.makePersistent(pdr);
            pdr = pm.detachCopy(pdr);
        } finally {
            pm.close();
        }
        return pdr;
    }
    
    public List<Pdr> findByUserId(long userId) {
        List<Pdr> detached = new ArrayList<Pdr>();
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        try {
            Query q = pm.newQuery(Pdr.class);
            q.setFilter("userId == userIdParam");
            q.declareParameters("long userIdParam");
            @SuppressWarnings("unchecked")
            List<Pdr> results = (List<Pdr>)q.execute(userId);
            detached = (List<Pdr>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;
        
    }

    /*
    PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
    try {
        Query query = pm.newQuery(NnUserReport.class);        
        @SuppressWarnings("unchecked")
        List<NnUserReport> results = (List<NnUserReport>) query.execute();
        detached = (List<NnUserReport>)pm.detachCopyAll(results);            
    } finally {
        pm.close();
    }
    */
            
    @SuppressWarnings("unchecked")
    public List<Pdr> findDebugging(
            NnUser user, NnDevice device, String session,
            String ip, Date since) {
        List<Pdr> detached = new ArrayList<Pdr>();
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        List<Pdr> results = new ArrayList<Pdr>();
        try {
            Query q = pm.newQuery(Pdr.class);
            if (user != null && session != null) {
                log.info("user:" + user.getId() + ";session:" + session);
                q.setFilter("userId == userIdParam && session == sessionParam");
                q.declareParameters("long userIdParam, String sessionParam");
                results = (List<Pdr>)q.execute(user.getId(), session);
            } else if (device != null && session != null) {
                log.info("device:" + device.getId() + ";session:" + session);
                q.setFilter("deviceToken == deviceIdParam && session == sessionParam");
                q.declareParameters("long userIdParam, String sessionParam");
                results = (List<Pdr>)q.execute(device.getId(), session);
            } else if (user != null) {
                log.info("user:" + user.getId());
                q.setFilter("userId == userIdParam");
                q.declareParameters("long userIdParam");
                results.addAll((List<Pdr>)q.execute(user.getId()));
                log.info("size:" + results.size());
            } else if (device != null) {
                log.info("device:" + device.getId());
                q.setFilter("deviceId == deviceIdParam");
                q.declareParameters("long deviceIdParam");
                results = (List<Pdr>)q.execute(device.getId());                
            } else if (ip != null) {
                log.info("ip:" + ip);
                q.declareImports("import java.util.Date");
                q.setFilter("ip == ipParam && createDate > createDateParam");
                q.declareParameters("String ipParam, Date createDateParam");
                results = (List<Pdr>)q.execute(ip, since);
            }
            q.setRange(0,199);
            detached = (List<Pdr>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;        
    }
    
}
