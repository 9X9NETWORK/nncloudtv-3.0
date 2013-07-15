package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.dao.SysTagDao;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.StoreListing;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.model.SysTagMap;
import com.nncloudtv.web.json.cms.Category;

@Service
public class StoreService {
    
    protected static final Logger log = Logger.getLogger(StoreService.class.getName());
    
    private NnChannelDao channelDao = new NnChannelDao();
    private SysTagDao sysTagDao = new SysTagDao();

    private StoreListingManager storeListingMngr;
    private MsoManager msoMngr;
    private SysTagManager sysTagMngr;
    private SysTagDisplayManager sysTagDisplayMngr;
    private SysTagMapManager sysTagMapMngr;
    
    @Autowired
    public StoreService(StoreListingManager storeListingMngr, MsoManager msoMngr,
                            SysTagManager sysTagMngr, SysTagDisplayManager sysTagDisplayMngr,
                            SysTagMapManager sysTagMapMngr) {
        this.storeListingMngr = storeListingMngr;
        this.msoMngr = msoMngr;
        this.sysTagMngr = sysTagMngr;
        this.sysTagDisplayMngr = sysTagDisplayMngr;
        this.sysTagMapMngr = sysTagMapMngr;
    }
    
    public StoreService() {
        this.storeListingMngr = new StoreListingManager();
        this.msoMngr = new MsoManager();
        this.sysTagMngr = new SysTagManager();
        this.sysTagDisplayMngr = new SysTagDisplayManager();
        this.sysTagMapMngr = new SysTagMapManager();
    }
    
    /** build Category from SysTag and SysTagDisplay */
    private Category composeCategory(SysTag category, SysTagDisplay categoryMeta) {
        
        Category categoryResp = new Category();
        categoryResp.setId(category.getId());
        categoryResp.setLang(categoryMeta.getLang());
        categoryResp.setMsoId(category.getMsoId());
        categoryResp.setName(categoryMeta.getName());
        categoryResp.setSeq(category.getSeq());
        
        return categoryResp;
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
        List<NnChannel> store9x9 = getStoreChannels(spheres);
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
        List<NnChannel> store9x9 = getStoreChannelsFromCategory(categoryId, spheres);
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
        // TODO : can't promise categoryId is true
        
        List<NnChannel> channels = channelDao.getStoreChannelsFromCategory(categoryId, spheres);
        if (channels == null) {
            return new ArrayList<NnChannel>();
        }
        
        return channels;
    }
    
    /** get channels from official store */
    public List<NnChannel> getStoreChannels(List<String> spheres) {
        
        List<NnChannel> channels = channelDao.getStoreChannels(spheres);
        if (channels == null) {
            return new ArrayList<NnChannel>();
        }
        
        return channels;
    }
    
    /** indicate input Id is 9x9's CategoryId or not */
    public boolean isNnCategory(Long categoryId) {
        
        if (categoryId == null) {
            return false;
        }
        
        SysTag category = sysTagMngr.findById(categoryId);
        if (category == null) {
            return false;
        }
        Mso mso9x9 = msoMngr.findNNMso();
        if (category.getMsoId() == mso9x9.getId() && category.getType() == SysTag.TYPE_CATEGORY) {
            return true;
        }
        
        return false;
    }
    
    /** find CategoryIds where Channel is in those Categories */
    public List<Long> findCategoryIdsByChannelId(long channelId, long msoId) {
        
        List<SysTag> sysTags = sysTagDao.findCategoriesByChannelId(channelId, msoId);
        if (sysTags == null || sysTags.size() == 0) {
            return new ArrayList<Long>();
        }
        
        List<Long> categoryIds = new ArrayList<Long>();
        for (SysTag sysTag : sysTags) {
            categoryIds.add(sysTag.getId());
        }
        return categoryIds;
    }
    
    private Comparator<Category> getCategoryComparator(String field) {
        
        class CategorySeqComparator implements Comparator<Category> {
            public int compare(Category category1, Category category2) {
                int seq1 = category1.getSeq();
                if (category1.getLang() != null
                        && category1.getLang().equalsIgnoreCase(
                                LangTable.LANG_EN)) {
                    seq1 -= 100;
                }
                int seq2 = category2.getSeq();
                if (category2.getLang() != null
                        && category2.getLang().equalsIgnoreCase(
                                LangTable.LANG_EN)) {
                    seq2 -= 100;
                }
                return (seq1 - seq2);
            }
        }
        
        return new CategorySeqComparator();
    }
    
    /** get Categories from official store */
    public List<Category> getStoreCategories(String lang) {
        
        if (lang == null) {
            return new ArrayList<Category>();
        }
        
        List<SysTagDisplay> categoryMetas = sysTagDisplayMngr.findPlayerCategories(lang, msoMngr.findNNMso().getId());
        if (categoryMetas == null || categoryMetas.size() == 0) {
            return new ArrayList<Category>();
        }
        
        List<Category> results = new ArrayList<Category>();
        Category result = null;
        for (SysTagDisplay categoryMeta : categoryMetas) {
            
            SysTag category = sysTagMngr.findById(categoryMeta.getSystagId());
            
            if (category != null) {
                result = composeCategory(category, categoryMeta);
                results.add(result);
            }
        }
        
        Collections.sort(results, getCategoryComparator("seq"));
        
        return results;
    }
    
    /** set up which official Category that Channel belongs to, Channel will belongs to only one official Category */
    public void setupChannelCategory(Long categoryId, Long channelId) {
        
        if (categoryId == null || channelId == null) {
            return ;
        }
        
        Mso nnMso = msoMngr.findNNMso();
        List<SysTagMap> tagMaps = sysTagMapMngr.findCategoryMapsByChannelId(channelId, nnMso.getId());
        sysTagMapMngr.deleteAll(tagMaps);
        sysTagMapMngr.save(new SysTagMap(categoryId, channelId));
    }
    
    /** service for ApiMso.storeChannels */
    public List<Long> storeChannels(Long msoId, java.util.Set<Long> channelIds, Long categoryId) {
        
        if (msoId == null) {
            return new ArrayList<Long>();
        }
        
        List<Long> results = null;
        if (channelIds != null) {
            results = checkChannelIdsInMsoStore(channelIds, msoId);
        } else if (categoryId != null) {
            results = getChannelIdsFromMsoStoreCategory(categoryId, msoId);
        } else {
            results = getChannelIdsFromMsoStore(msoId);
        }
        
        if (results == null) {
            return new ArrayList<Long>();
        }
        return results;
    }
    
    /** service for ApiMso.storeChannelRemove */
    public void storeChannelRemove(Long msoId, List<Long> channelIds) {
        
        if (msoId == null || channelIds == null) {
            return ;
        }
        storeListingMngr.addChannelsToBlackList(channelIds, msoId);
    }
    
    /** service for ApiMso.storeChannelAdd */
    public void storeChannelAdd(Long msoId, List<Long> channelIds) {
        
        if (msoId == null || channelIds == null) {
            return ;
        }
        storeListingMngr.removeChannelsFromBlackList(channelIds, msoId);
    }

}
