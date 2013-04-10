package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.StoreListingDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.StoreListing;

@Service
public class StoreListingManager {
    
    protected static final Logger log = Logger.getLogger(StoreListingManager.class.getName());
    
    private StoreListingDao dao;
    private NnChannelManager channelMngr;
    
    @Autowired
    public StoreListingManager(StoreListingDao dao, NnChannelManager channelMngr) {
        this.dao = dao;
        this.channelMngr = channelMngr;
    }
    
    public List<NnChannel> findByChannelIdsAndMsoId(List<Long> channelIds, Long msoId) {
        
        if (channelIds == null || channelIds.size() == 0 || msoId == null) {
            return new ArrayList<NnChannel>();
        }
        
        List<StoreListing> storeListing = dao.findByChannelIdsAndMsoId(channelIds, msoId);
        if ((storeListing == null) || (storeListing.size() == 0)) {
            return new ArrayList<NnChannel>();
        }
        
        List<Long> channelIdList = new ArrayList<Long>();
        for (StoreListing item : storeListing) {
            channelIdList.add(item.getChannelId());
        }
        List<NnChannel> results = channelMngr.findByIds(channelIdList);
        if (results == null) {
            return new ArrayList<NnChannel>();
        }
        
        return results;
    }
    
    public List<NnChannel> findByPaging(long page, long rows, Long msoId) {
        
        if (page <= 0 || rows <= 0 || msoId == null) {
            return new ArrayList<NnChannel>();
        }
        
        String filter = "msoId == " + msoId;
        List<StoreListing> storeListing = dao.list(page, rows, "updateDate", "desc", filter);
        if ((storeListing == null) || (storeListing.size() == 0)) {
            return new ArrayList<NnChannel>();
        }
        
        List<Long> channelIdList = new ArrayList<Long>();
        for (StoreListing item : storeListing) {
            channelIdList.add(item.getChannelId());
        }
        List<NnChannel> results = channelMngr.findByIds(channelIdList);
        if (results == null) {
            return new ArrayList<NnChannel>();
        }
        
        return results;
    }

}
