package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;

public class MsoConfigDao {
    
    public MsoConfig save(MsoConfig config) {
        if (config == null) {return null;}
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            pm.makePersistent(config);
            config = pm.detachCopy(config);
        } finally {
            pm.close();
        }
        return config;
    }
    
    public MsoConfig findByMsoAndItem(long msoId, String item) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        MsoConfig config = null;
        try {
            Query query = pm.newQuery(MsoConfig.class);
            query.setFilter("msoId == msoIdParam && item == itemParam");        
            query.declareParameters("long msoIdParam" + ", String itemParam");                
            @SuppressWarnings("unchecked")
            List<MsoConfig> results = (List<MsoConfig>) query.execute(msoId, item);
            if (results.size() > 0) {
                config = results.get(0);
                config = pm.detachCopy(config);
            }
        } finally {
            pm.close();
        }
        return config;        
    }

    public MsoConfig findByItem(String item) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        MsoConfig config = null;
        try {
            Query query = pm.newQuery(MsoConfig.class);
            query.setFilter("item == itemParam");        
            query.declareParameters("String itemParam");
            @SuppressWarnings("unchecked")
            List<MsoConfig> results = (List<MsoConfig>) query.execute(item);
            if (results.size() > 0) {
                config = results.get(0);
                config = pm.detachCopy(config);
            }
        } finally {
            pm.close();
        }
        return config;        
    }

    public List<MsoConfig> findByMso(Mso mso) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();

        List<MsoConfig> detached = new ArrayList<MsoConfig>();
        try {
            Query query = pm.newQuery(MsoConfig.class);
            query.setFilter("msoId == msoIdParam");        
            query.declareParameters("long msoIdParam");                
            @SuppressWarnings("unchecked")
            List<MsoConfig> results = (List<MsoConfig>) query.execute(mso.getId());
            detached = (List<MsoConfig>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;        
    }
    
}
