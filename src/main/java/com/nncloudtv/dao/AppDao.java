package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.App;

public class AppDao extends GenericDao<App> {
    protected static final Logger log = Logger.getLogger(AppDao.class.getName());
    
    public AppDao() {
        super(App.class);
    }

    public List<App> findAllBySphere(String sphere) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<App> detached = new ArrayList<App>(); 
        try {
            Query q = pm.newQuery(App.class);
            q.setFilter("sphere == sphereParam && featured == featuredParam");
            q.declareParameters("String sphereParam, boolean featuredParam");
            q.setOrdering("position1 asc");
            @SuppressWarnings("unchecked")
            List<App> apps = (List<App>) q.execute(sphere, false);            
            detached = (List<App>)pm.detachCopyAll(apps);
        } finally {
            pm.close();
        }
        return detached;
    }    

    public List<App> findFeaturedBySphere(String sphere) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<App> detached = new ArrayList<App>(); 
        try {
            Query q = pm.newQuery(App.class);
            q.setFilter("sphere == sphereParam && featured == featuredParam");
            q.declareParameters("String sphereParam, boolean featuredParam");
            q.setOrdering("position1 asc");
            @SuppressWarnings("unchecked")
            List<App> apps = (List<App>) q.execute(sphere, true);            
            detached = (List<App>)pm.detachCopyAll(apps);
        } finally {
            pm.close();
        }
        return detached;
    }    
    
}
