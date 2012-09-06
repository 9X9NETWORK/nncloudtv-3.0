package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnDevice;
import com.nncloudtv.model.NnUser;

public class NnDeviceDao extends GenericDao<NnDevice> {

    protected static final Logger log = Logger.getLogger(NnDeviceDao.class.getName());
    
    public NnDeviceDao() {
        super(NnDevice.class);
    }
    
    public List<NnDevice> findByUser(NnUser user) {
        List<NnDevice> detached = new ArrayList<NnDevice>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            Query q = pm.newQuery(NnDevice.class);
            q.setFilter("userId == userIdParam");
            q.declareParameters("long userIdParam");
            @SuppressWarnings("unchecked")
            List<NnDevice> results = (List<NnDevice>)q.execute(user.getId());
            detached = (List<NnDevice>)pm.detachCopyAll(results); 
        } finally {
            pm.close();
        }
        return detached;        
    }
    
    public List<NnDevice> findByToken(String token) {
        List<NnDevice> detached = new ArrayList<NnDevice>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager(); 
        try {
            Query q = pm.newQuery(NnDevice.class);
            q.setFilter("token == tokenParam"); 
            q.declareParameters("String tokenParam");
            @SuppressWarnings("unchecked")
            List<NnDevice> results = (List<NnDevice>)q.execute(token);
            detached = (List<NnDevice>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;        
    }

    @SuppressWarnings("unchecked")
    public NnDevice findByTokenAndUser(String token, NnUser user) {
        NnDevice device = null;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            Query query = pm.newQuery(NnDevice.class);
            query.setFilter("token == tokenParam && userId == userIdParam");
            query.declareParameters("String tokenParam, long userIdParam");        
            List<NnDevice> results = (List<NnDevice>) query.execute(token, user.getId());
            if (results.size() > 0) {
                device = results.get(0);
            }
            
            device = pm.detachCopy(device);
        } finally {
            pm.close();
        }
        return device;
    }

    @SuppressWarnings("unchecked")
    public NnDevice findDeviceOpenToken(String token) {
        NnDevice device = null;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();        
        try {
            Query query = pm.newQuery(NnDevice.class);
            query.setFilter("token == tokenParam && userId == userIdParam");
            query.declareParameters("String tokenParam, long userIdParam");        
            List<NnDevice> results = (List<NnDevice>) query.execute(token, 0);
            if (results.size() > 0) {
                device = results.get(0);            
            }            
            device = pm.detachCopy(device);
        } finally {
            pm.close();
        }
        return device;
    }
    
}
