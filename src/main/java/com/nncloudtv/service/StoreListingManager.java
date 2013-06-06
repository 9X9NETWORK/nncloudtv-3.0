package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.StoreListingDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.StoreListing;

@Service
public class StoreListingManager {
    
    protected static final Logger log = Logger.getLogger(StoreListingManager.class.getName());
    
    private StoreListingDao dao = new StoreListingDao();
    private SysTagManager sysTagMngr;
    private NnChannelManager channelMngr;
    
    @Autowired
    public StoreListingManager(SysTagManager sysTagMngr, NnChannelManager channelMngr) {
        this.sysTagMngr = sysTagMngr;
        this.channelMngr = channelMngr;
    }
    
    public StoreListingManager() {
        this.sysTagMngr = new SysTagManager();
        this.channelMngr = new NnChannelManager();
    }
    
    /** return channels that have channelIds whom are in the mso store */
    public List<Long> findByChannelIdsAndMsoId(List<Long> channelIds, Long msoId) {
        
        if (channelIds == null || channelIds.size() == 0 || msoId == null) {
            return new ArrayList<Long>();
        }
        
        List<Long> storeMso = findByMsoId(msoId);
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
        
        /*
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
        */
        
        return results;
    }
    
    public void addChannelsToBlackList(List<Long> channelIds, Long msoId) {
        
        if (channelIds == null || msoId == null || channelIds.size() == 0) {
            return ;
        }
        
        List<NnChannel> channels = channelMngr.findByIds(channelIds);
        if (channels == null || channels.size() == 0) {
            return ;
        }
        
        List<Long> channelIdList = new ArrayList<Long>();
        for (NnChannel channel : channels) {
            channelIdList.add(channel.getId());
        }
        
        // find if already exist, update updateDate
        List<StoreListing> storeListing = dao.findByChannelIdsAndMsoId(channelIdList, msoId);
        if (storeListing != null && storeListing.size() > 0) {
            saveAll(storeListing);
            
            for (StoreListing item : storeListing) {
                if (channelIdList.contains(item.getChannelId())) {
                    channelIdList.remove(item.getChannelId());
                }
            }
        }
        if (channelIdList.size() == 0) {
            return ;
        }
        
        // save if not exist in db
        StoreListing item = null;
        List<StoreListing> newStoreListing = new ArrayList<StoreListing>();
        for (Long channelId : channelIdList) {
            item = new StoreListing(channelId, msoId);
            newStoreListing.add(item);
        }
        
        saveAll(newStoreListing);
    }
    
    public void removeChannelsFromBlackList(List<Long> channelIds, Long msoId) {
        
        if (channelIds == null || msoId == null || channelIds.size() == 0) {
            return ;
        }
        
        List<NnChannel> channels = channelMngr.findByIds(channelIds);
        if (channels == null || channels.size() == 0) {
            return ;
        }
        
        List<Long> channelIdList = new ArrayList<Long>();
        for (NnChannel channel : channels) {
            channelIdList.add(channel.getId());
        }
        
        List<StoreListing> storeListing = dao.findByChannelIdsAndMsoId(channelIdList, msoId);
        if (storeListing != null && storeListing.size() > 0) {
            dao.deleteAll(storeListing);
        }
        
    }
    
    private List<StoreListing> saveAll(List<StoreListing> storeListing) {
        
        if (storeListing == null) {
            return null;
        }
        if (storeListing.size() == 0) {
            return storeListing;
        }
        
        Date now = new Date();
        for (StoreListing item : storeListing) {
            item.setUpdateDate(now);
        }
        
        storeListing = dao.saveAll(storeListing);
        
        return storeListing;
    }
    
    /** call when NnChannel is going to delete **/
    public void deleteByChannelId(Long channelId) {
        
    }
    
    /** call when Mso is going to delete **/
    public void deleteByMsoId(Long msoId) {
        
    }
    
    /** mso store = 9x9 store - blackList, use categoryId to find partial store */
    public List<Long> findByCategoryIdAndMsoId(Long categoryId, Long msoId) {
        
        if (msoId == null || categoryId == null) {
            return new ArrayList<Long>();
        }
        List<StoreListing> blackList = dao.findByMsoId(msoId);
        
        // this categoryId = sysTagId, it should belong to 9x9, the 9x9's category
        List<NnChannel> store9x9 = sysTagMngr.findStoreChannelsById(categoryId);
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
    
    /** mso store = 9x9 store - blackList */
    public List<Long> findByMsoId(Long msoId) {
        
        if (msoId == null) {
            return new ArrayList<Long>();
        }
        List<StoreListing> blackList = dao.findByMsoId(msoId);
        
        List<NnChannel> store9x9 = sysTagMngr.findStoreChannels();
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
    
    public List<StoreListing> getBlackList(Long channelId) {
        
        if (channelId == null) {
            return new ArrayList<StoreListing>();
        }
        
        List<StoreListing> results = dao.findByChannelId(channelId);
        return results;
    }

}
