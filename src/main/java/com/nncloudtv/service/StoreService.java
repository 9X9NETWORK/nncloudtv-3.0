package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.StoreDao;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.StoreListing;

@Service
public class StoreService {
    
    protected static final Logger log = Logger.getLogger(StoreService.class.getName());
    
    private StoreDao dao = new StoreDao();

    private StoreListingManager storeListingMngr;
    private MsoManager msoMngr;
    
    @Autowired
    public StoreService(StoreListingManager storeListingMngr, MsoManager msoMngr) {
        this.storeListingMngr = storeListingMngr;
        this.msoMngr = msoMngr;
    }
    
    public StoreService() {
        this.storeListingMngr = new StoreListingManager();
        this.msoMngr = new MsoManager();
    }
    
    /** output channelIds if input channelIds are in the mso store */
    public List<Long> checkChannelIdsInMsoStore(java.util.Set<Long> channelIds, Long msoId) {
        
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
        
        Mso mso = msoMngr.findByIdWithSupportedRegion(msoId);
        if (mso == null) {
            return new ArrayList<Long>();
        }
        List<String> spheres;
        if (mso.getSupportedRegion() == null) {
            spheres = null;
        } else {
            spheres = MsoConfigManager.parseSupportedRegion(mso.getSupportedRegion());
        }
        List<NnChannel> store9x9 = dao.getStoreChannels(spheres);
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
        
        // TODO : check this categoryId = sysTagId, it should belong to 9x9, the 9x9's category
        Mso mso = msoMngr.findByIdWithSupportedRegion(msoId);
        if (mso == null) {
            return new ArrayList<Long>();
        }
        List<String> spheres;
        if (mso.getSupportedRegion() == null) {
            spheres = null;
        } else {
            spheres = MsoConfigManager.parseSupportedRegion(mso.getSupportedRegion());
        }
        List<NnChannel> store9x9 = dao.getStoreChannelsFromCategory(categoryId, spheres);
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
    public List<NnChannel> getStoreChannelsFromCategory(Long categoryId, List<String> spheres) {
        
        if (categoryId == null) {
            return new ArrayList<NnChannel>();
        }
        
        List<NnChannel> channels = dao.getStoreChannelsFromCategory(categoryId, spheres);
        if (channels == null) {
            return new ArrayList<NnChannel>();
        }
        
        return channels;
    }
    
    /** get channels from official store */
    public List<NnChannel> getStoreChannels(List<String> spheres) {
        
        List<NnChannel> channels = dao.getStoreChannels(spheres);
        if (channels == null) {
            return new ArrayList<NnChannel>();
        }
        
        return channels;
    }

}
