package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.StoreListing;

public class StoreListingDao extends GenericDao<StoreListing> {
    
    protected static final Logger log = Logger.getLogger(StoreListingDao.class.getName());
    
    public StoreListingDao() {
        super(StoreListing.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<StoreListing> findByChannelIdsAndMsoId(List<Long> channelIds, long msoId) {
        
        List<StoreListing> results = new ArrayList<StoreListing>();
        List<StoreListing> results2 = new ArrayList<StoreListing>();
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            Query q = pm.newQuery(StoreListing.class, ":p.contains(channelId)");
            //q.setFilter("msoId == " + msoId);
            q.setOrdering("updateDate desc");
            results = ((List<StoreListing>) q.execute(channelIds));        
            results = (List<StoreListing>) pm.detachCopyAll(results);
            
        } finally {
            pm.close();
        }
        
        for (StoreListing item : results) {
            if (item.getMsoId() == msoId) {
                results2.add(item);
            }
        }
        
        return results2;
    }
    
    public StoreListing findByChannelIdAndMsoId(long channelId, long msoId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        StoreListing result = null;
        try {
            Query q = pm.newQuery(StoreListing.class);
            q.setFilter("channelId == channelIdParam && msoId == msoIdParam");
            q.declareParameters("long channelIdParam, long msoIdParam");
            @SuppressWarnings("unchecked")
            List<StoreListing> results = (List<StoreListing>) q.execute(channelId, msoId);
            if (results.size() > 0) {
                result = pm.detachCopy(results.get(0));
            }
        } finally {
            pm.close();
        }
        return result;
    }
    
    public List<StoreListing> findByMsoId(long msoId) {
        
        List<StoreListing> detached = new ArrayList<StoreListing>();
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            Query query = pm.newQuery(StoreListing.class);
            query.setFilter("msoId == msoIdParam");
            query.declareParameters("long msoIdParam");
            @SuppressWarnings("unchecked")
            List<StoreListing> results = (List<StoreListing>)query.execute(msoId);
            if (results.size() > 0) {
                detached = (List<StoreListing>) pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        
        return detached;
    }
    
    public List<StoreListing> findByChannelId(long channelId) {
        
        List<StoreListing> detached = new ArrayList<StoreListing>();
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            Query query = pm.newQuery(StoreListing.class);
            query.setFilter("channelId == channelIdParam");
            query.declareParameters("long channelIdParam");
            @SuppressWarnings("unchecked")
            List<StoreListing> results = (List<StoreListing>)query.execute(channelId);
            if (results.size() > 0) {
                detached = (List<StoreListing>) pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        
        return detached;
    }
    
}
