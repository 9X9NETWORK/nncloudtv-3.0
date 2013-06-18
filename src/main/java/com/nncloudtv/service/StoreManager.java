package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.StoreDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.StoreListing;

@Service
public class StoreManager {
    
    protected static final Logger log = Logger.getLogger(StoreManager.class.getName());
    
    private StoreDao dao = new StoreDao();
    private StoreListingManager storeListingMngr;
    
    @Autowired
    public StoreManager(StoreListingManager storeListingMngr) {
        this.storeListingMngr = storeListingMngr;
    }
    
    public StoreManager() {
        this.storeListingMngr = new StoreListingManager();
    }
    
    /** output channelIds if input channelIds are in the mso store */
    public List<Long> checkChannelIdsInMsoStore(List<Long> channelIds, Long msoId) {
        
        if (channelIds == null || channelIds.size() == 0 || msoId == null) {
            return new ArrayList<Long>();
        }
        
        List<Long> storeMso = getChannelIdsFromMsoStore(msoId);
        if (storeMso == null || storeMso.size() == 0) {
            return new ArrayList<Long>();
        }
        
        Map<Long, Long> storeMsoMap = new TreeMap<Long, Long>();
        for (Long channelId : storeMso) {
            storeMsoMap.put(channelId, channelId);
        }
        
        List<Long> results = new ArrayList<Long>();
        for (Long channelId : channelIds) {
            if (storeMsoMap.containsKey(channelId)) {
                results.add(channelId);
            }
        }
        
        return results;
    }
    
    /** mso store = official store - mso's blackList */
    public List<Long> getChannelIdsFromMsoStore(Long msoId) {
        
        if (msoId == null) {
            return new ArrayList<Long>();
        }
        List<StoreListing> blackList = storeListingMngr.getBlackListByMsoId(msoId);
        
        List<NnChannel> store9x9 = dao.getStoreChannels();
        if (store9x9 == null || store9x9.size() == 0) {
            return new ArrayList<Long>();
        }
        
        Map<Long, Long> blackListMap = new TreeMap<Long, Long>();
        if (blackList != null && blackList.size() > 0) {
            for (StoreListing item : blackList) {
                blackListMap.put(item.getChannelId(), item.getChannelId());
            }
        }
        
        List<Long> msoStoreChannelIds = new ArrayList<Long>();
        for (NnChannel channel : store9x9) {
            if (blackListMap.containsKey(channel.getId())) {
                // skip
            } else {
                msoStoreChannelIds.add(channel.getId());
            }
        }
        
        return msoStoreChannelIds;
    }
    
    /** mso store's category = official store's category - mso's blackList */
    public List<Long> getChannelIdsFromMsoStoreCategory(Long categoryId, Long msoId) {
        
        if (msoId == null || categoryId == null) {
            return new ArrayList<Long>();
        }
        List<StoreListing> blackList = storeListingMngr.getBlackListByMsoId(msoId);
        
        // this categoryId = sysTagId, it should belong to 9x9, the 9x9's category
        List<NnChannel> store9x9 = dao.getStoreChannelsFromCategory(categoryId);
        if (store9x9 == null || store9x9.size() == 0) {
            return new ArrayList<Long>();
        }
        
        Map<Long, Long> blackListMap = new TreeMap<Long, Long>();
        if (blackList != null && blackList.size() > 0) {
            for (StoreListing item : blackList) {
                blackListMap.put(item.getChannelId(), item.getChannelId());
            }
        }
        
        List<Long> msoStoreChannelIds = new ArrayList<Long>();
        for (NnChannel channel : store9x9) {
            if (blackListMap.containsKey(channel.getId())) {
                // skip
            } else {
                msoStoreChannelIds.add(channel.getId());
            }
        }
        
        return msoStoreChannelIds;
    }
    
    /** get channels from official store's category */
    public List<NnChannel> getStoreChannelsFromCategory(Long categoryId) {
        
        if (categoryId == null) {
            return new ArrayList<NnChannel>();
        }
        
        List<NnChannel> channels = dao.getStoreChannelsFromCategory(categoryId);
        if (channels == null) {
            return new ArrayList<NnChannel>();
        }
        
        return channels;
    }
    
    /** get channels from official store */
    public List<NnChannel> getStoreChannels() {
        
        List<NnChannel> channels = dao.getStoreChannels();
        if (channels == null) {
            return new ArrayList<NnChannel>();
        }
        
        return channels;
    }

}
