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
    
}
