package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

import com.nncloudtv.model.StoreListing;

@Repository
public class StoreListingDao extends GenericDao<StoreListing> {
    
    protected static final Logger log = Logger.getLogger(StoreListingDao.class.getName());
    
    public StoreListingDao() {
        super(StoreListing.class);
    }
    
    public List<StoreListing> findByChannelIdsAndMsoId(List<Long> channelIds, long msoId) {
        
        List<StoreListing> results = new ArrayList<StoreListing>();
        
        return results;
    }
    
}
