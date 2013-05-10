package com.nncloudtv.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.EndPoint;

public class EndPointDao {

    protected static final Logger log = Logger.getLogger(EndPointDao.class.getName());

    public EndPoint save(EndPoint endpoint) {
        if (endpoint == null) {return null;}
        PersistenceManager pm = PMF.getNnUser1().getPersistenceManager();
        try {
            pm.makePersistent(endpoint);
            endpoint = pm.detachCopy(endpoint);
        } finally {
            pm.close();
        }
        return endpoint;
    }
    
    public void delete(EndPoint endpoint) {
        if (endpoint == null) return;
        PersistenceManager pm = PMF.getNnUser1().getPersistenceManager();
        try {
            pm.deletePersistent(endpoint);
        } finally {
            pm.close();
        }
    }
    
    public EndPoint findByEndPoint(long userId, long msoId, String token, short vendor) {
        PersistenceManager pm = PMF.getNnUser1().getPersistenceManager();
        EndPoint detached = null;
        try {
            String sql = "select * from endpoint " + 
                         " where userId = " + userId +
                           " and msoId = " + msoId +
                           " and token = " + token + 
                           " and vendor = " + vendor;
            log.info("Sql=" + sql);
            
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(EndPoint.class);
            @SuppressWarnings("unchecked")
            List<EndPoint> results = (List<EndPoint>) q.execute();
            if (results.size() > 0)
                detached = pm.detachCopy(results.get(0));
        } finally {
            pm.close();
        }

        return detached;     
        
    }
}
