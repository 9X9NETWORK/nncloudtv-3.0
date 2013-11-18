package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.model.SysTagMap;
import com.nncloudtv.web.json.cms.Category;
import com.nncloudtv.web.json.cms.Set;

@Service
public class ApiMsoService {
    
    protected static final Logger log = Logger.getLogger(ApiMsoService.class.getName());
    
    private SetService setService;
    private SysTagManager sysTagMngr;
    private SysTagDisplayManager sysTagDisplayMngr;
    private SysTagMapManager sysTagMapMngr;
    private NnChannelManager channelMngr;
    private StoreService storeService;
    private StoreListingManager storeListingMngr;
    private MsoManager msoMngr;
    private CategoryService categoryService;
    private MsoConfigManager msoConfigMngr;
    
    @Autowired
    public ApiMsoService(SetService setService, SysTagManager sysTagMngr,
                            SysTagDisplayManager sysTagDisplayMngr, SysTagMapManager sysTagMapMngr,
                            NnChannelManager channelMngr, StoreService storeService,
                            StoreListingManager storeListingMngr, MsoManager msoMngr,
                            CategoryService categoryService, MsoConfigManager msoConfigMngr) {
        this.setService = setService;
        this.sysTagMngr = sysTagMngr;
        this.sysTagDisplayMngr = sysTagDisplayMngr;
        this.sysTagMapMngr = sysTagMapMngr;
        this.channelMngr = channelMngr;
        this.storeService = storeService;
        this.storeListingMngr = storeListingMngr;
        this.msoMngr = msoMngr;
        this.categoryService = categoryService;
        this.msoConfigMngr = msoConfigMngr;
    }
    
    /** service for ApiMso.msoSets
     *  get Sets that belong to target Mso
     *  @param msoId required, Mso's Id
     *  @param lang optional, filter for Set's lang
     *  @return list of Sets */
    public List<Set> msoSets(Long msoId, String lang) {
        
        if (msoId == null) {
            return new ArrayList<Set>();
        }
        
        List<Set> results = null;
        if (lang != null) {
            results = setService.findByMsoIdAndLang(msoId, lang);
        } else {
            results = setService.findByMsoId(msoId);
        }
        
        if (results == null) {
            return new ArrayList<Set>();
        }
        return results;
    }
    
    public Set msoSetCreate(Long msoId, Short seq, String tag, String name, Short sortingType) {
        
        if (msoId == null) {
            return null;
        }
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            return null;
        }
        
        Set newSet = new Set();
        newSet.setMsoId(msoId);
        newSet.setName(name);
        if (seq != null) {
            newSet.setSeq(seq);
        }
        if (sortingType != null) {
            newSet.setSortingType(sortingType);
        }
        if (tag != null) {
            newSet.setTag(tag);
        }
        
        String lang = LangTable.LANG_EN; // default
        MsoConfig supportedRegion = msoConfigMngr.findByMsoAndItem(mso, MsoConfig.SUPPORTED_REGION);
        if (supportedRegion != null && supportedRegion.getValue() != null) {
            List<String> spheres = MsoConfigManager.parseSupportedRegion(supportedRegion.getValue());
            if (spheres != null && spheres.isEmpty() == false) {
                lang = spheres.get(0);
            }
        }
        newSet.setLang(lang);
        
        Set savedSet = setService.create(newSet);
        
        return savedSet;
    }
    
    /** service for ApiMso.set
     *  get Set by given Set's ID
     *  @param setId required, SysTag's Id with SysTag's type = Set
     *  @return object Set or null if not exist */
    public Set set(Long setId) {
        
        if (setId == null) {
            return null;
        }
        
        return setService.findById(setId);
    }
    
    /** service for ApiMso.setUpdate
     *  update object Set
     *  @param setId required, SysTag's Id with SysTag's type = Set
     *  @param name optional, Set's name save in SysTagDisplay's name
     *  @param seq optional, Set's seq save in SysTag's seq
     *  @param tag optional, Set's tag save in SysTagDisplay's popularTag
     *  @param sortingType optional, Set's sortingType save in SysTag's sorting
     *  @return object Set or null if not exist */
    public Set setUpdate(Long setId, String name, Short seq, String tag, Short sortingType) {
        
        if (setId == null) {
            return null;
        }
        SysTag set = sysTagMngr.findById(setId);
        if (set == null) {
            return null;
        }
        SysTagDisplay setMeta = sysTagDisplayMngr.findBySysTagId(set.getId());
        if (setMeta == null) {
            log.warning("invalid structure : SysTag's Id=" + set.getId() + " exist but not found any of SysTagDisPlay");
            return null;
        }
        
        if (name != null) {
            setMeta.setName(name);
        }
        if (seq != null) {
            set.setSeq(seq);
        }
        if (tag != null) {
            setMeta.setPopularTag(tag);
        }
        if (sortingType != null) {
            set.setSorting(sortingType);
        }
        // automated update cntChannel
        List<SysTagMap> channels = sysTagMapMngr.findBySysTagId(set.getId());
        setMeta.setCntChannel(channels.size());
        
        if (seq != null || sortingType != null) {
            set = sysTagMngr.save(set);
        }
        setMeta = sysTagDisplayMngr.save(setMeta);
        
        return setService.composeSet(set, setMeta);
    }
    
    public void setDelete(Long setId) {
        
        if (setId == null) {
            return ;
        }
        
        setService.delete(setId);
    }
    
    /** service for ApiMso.setChannels
     *  get Channels from Set
     *  @param setId required, SysTag's Id with SysTag's type = Set
     *  @return list of Channels */
    public List<NnChannel> setChannels(Long setId) {
        
        if (setId == null) {
            return new ArrayList<NnChannel>();
        }
        
        SysTag set = sysTagMngr.findById(setId);
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            return new ArrayList<NnChannel>();
        }
        
        List<NnChannel> results = null;
        if (set.getSorting() == SysTag.SORT_SEQ) {
            results = setService.getChannelsOrderBySeq(set.getId());
        }
        if (set.getSorting() == SysTag.SORT_DATE) {
            results = setService.getChannelsOrderByUpdateTime(set.getId());
        }
        
        if (results == null) {
            return new ArrayList<NnChannel>();
        }
        
        results = channelMngr.responseNormalization(results);
        /*
        if (results.size() > 0) { // dependence with front end use case
            channelMngr.populateMoreImageUrl(results.get(0));
        }
        */
        return results;
    }
    
    /** service for ApiMso.setChannelAdd
     *  add Channel to Set
     *  @param setId required, SysTag's Id with SysTag's type = Set
     *  @param channelId required, Channel's Id
     *  @param timeStart optional, set a period start that Channel appear in the Set
     *  @param timeEnd optional, set a period end that Channel appear in the Set
     *  @param alwaysOnTop optional, put this Channel in the head when Channels sorting by update time get from Set */
    public void setChannelAdd(Long setId, Long channelId, Short timeStart, Short timeEnd, Boolean alwaysOnTop) {
        
        if (setId == null || channelId == null) {
            return ;
        }
        
        setService.addChannelToSet(setId, channelId, timeStart, timeEnd, alwaysOnTop);
    }
    
    /** service for ApiMso.setChannelRemove
     *  remove Channel from Set
     *  @param setId required, SysTag's Id with SysTag's type = Set
     *  @param channelId required, Channel's Id */
    public void setChannelRemove(Long setId, Long channelId) {
        
        if (setId == null || channelId == null) {
            return ;
        }
        
        SysTagMap sysTagMap = sysTagMapMngr.findBySysTagIdAndChannelId(setId, channelId);
        if (sysTagMap == null) {
            // do nothing
        } else {
            sysTagMapMngr.delete(sysTagMap);
        }
    }
    
    /** service for ApiMso.setChannelsSorting
     *  sort Channels that in the Set
     *  @param setId required, SysTag's Id with SysTag's type = Set
     *  @param sortedChannels required, the Channel Ids from Set to be sorted */
    public void setChannelsSorting(Long setId, List<Long> sortedChannels) {
        
        if (setId == null) {
            return ;
        }
        
        if (sortedChannels == null) {
            sysTagMapMngr.reorderSysTagMaps(setId);
            return ;
        }
        
        // it must same as setChannels's result, order by seq asc
        List<SysTagMap> setChannels = sysTagMapMngr.findBySysTagId(setId);
        
        List<Long> oldSequence = new ArrayList<Long>();
        List<Long> remainder = new ArrayList<Long>();
        for (SysTagMap channel : setChannels) {
            oldSequence.add(channel.getChannelId());
            remainder.add(channel.getChannelId());
        }
        
        List<SysTagMap> newSequence = new ArrayList<SysTagMap>();
        for (Long channelId : sortedChannels) {
            int index = oldSequence.indexOf(channelId);
            if (index > -1) {
                newSequence.add(setChannels.get(index));
                remainder.remove(channelId);
            }
        }
        
        // add remainder channels to the end of list
        for (Long channelId : remainder) {
            int index = oldSequence.indexOf(channelId);
            if (index > -1) {
                newSequence.add(setChannels.get(index));
            }
        }
        
        int seq = 1;
        for (SysTagMap channel : newSequence) {
            channel.setSeq((short) seq);
            seq++;
        }
        
        sysTagMapMngr.saveAll(newSequence);
    }
    
    /** service for ApiMso.storeChannels
     *  get Channel's IDs from Mso's store
     *  @param msoId required, the Mso's Id
     *  @param channelIds optional, check if these Channel IDs are in the Mso's store
     *  @param categoryId optional, the official Category's ID, get channels from Mso's store's Category
     *  @return list of Channel's IDs */
    public List<Long> storeChannels(Long msoId, java.util.Set<Long> channelIds, Long categoryId) {
        
        if (msoId == null) {
            return new ArrayList<Long>();
        }
        
        List<Long> results = null;
        if (channelIds != null) {
            results = storeService.checkChannelIdsInMsoStore(channelIds, msoId);
        } else if (categoryId != null) {
            results = storeService.getChannelIdsFromMsoStoreCategory(categoryId, msoId);
        } else {
            results = storeService.getChannelIdsFromMsoStore(msoId);
        }
        
        if (results == null) {
            return new ArrayList<Long>();
        }
        return results;
    }
    
    /** service for ApiMso.storeChannelRemove
     *  remove Channels from Mso's store
     *  @param msoId required, the Mso's Id
     *  @param channelIds required, the Channel's IDs */
    public void storeChannelRemove(Long msoId, List<Long> channelIds) {
        
        if (msoId == null || channelIds == null) {
            return ;
        }
        storeListingMngr.addChannelsToBlackList(channelIds, msoId);
    }
    
    /** service for ApiMso.storeChannelAdd
     *  add Channels to Mso's store
     *  @param msoId required, the Mso's Id
     *  @param channelIds required, the Channel's IDs */
    public void storeChannelAdd(Long msoId, List<Long> channelIds) {
        
        if (msoId == null || channelIds == null) {
            return ;
        }
        storeListingMngr.removeChannelsFromBlackList(channelIds, msoId);
    }
    
    /** service for ApiMso.mso
     *  get Mso by given Mso's ID
     *  @param msoId required, the Mso's Id
     *  @return object Mso or null if not exist */
    public Mso mso(Long msoId) {
        
        if (msoId == null) {
            return null;
        }
        
        Mso mso = msoMngr.findByIdWithSupportedRegion(msoId);
        if (mso == null) {
            return null;
        }
        
        mso.setMaxSets(Mso.MAXSETS_DEFAULT);
        MsoConfig maxSets = msoConfigMngr.findByMsoAndItem(mso, MsoConfig.MAX_SETS);
        if (maxSets != null && maxSets.getValue() != null && maxSets.getValue().isEmpty() == false) {
            try {
                mso.setMaxSets(Short.valueOf(maxSets.getValue()));
            } catch (NumberFormatException e) {
            }
        }
        
        mso.setMaxChPerSet(Mso.MAXCHPERSET_DEFAULT);
        MsoConfig maxChPerSet = msoConfigMngr.findByMsoAndItem(mso, MsoConfig.MAX_CH_PER_SET);
        if (maxChPerSet != null && maxChPerSet.getValue() != null && maxChPerSet.getValue().isEmpty() == false) {
            try {
                mso.setMaxChPerSet(Short.valueOf(maxChPerSet.getValue()));
            } catch (NumberFormatException e) {
            }
        }
        
        return mso;
    }
    
    /** service for ApiMso.msoUpdate
     *  update object Mso
     *  @param msoId required, the Mso's Id
     *  @param title optional, Mso's title
     *  @param logoUrl optional, Mso's logoUrl
     *  @return object Mso or null if not exist */
    public Mso msoUpdate(Long msoId, String title, String logoUrl) {
        
        if (msoId == null) {
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            return null;
        }
        
        if (title != null) {
            mso.setTitle(title);
        }
        if (logoUrl != null) {
            mso.setLogoUrl(logoUrl);
        }
        
        if (title != null || logoUrl != null) {
            Mso savedMso = msoMngr.save(mso);
            return savedMso;
        } else {
            return mso;
        }
    }
    
    /**
     * service for ApiMso.msoCategories
     * Get MSO promotion Categories.
     * @param msoId required, the Mso's Id
     * @return list of Categories
     */
    public List<Category> msoCategories(Long msoId) {
        
        if (msoId == null) {
            return new ArrayList<Category>();
        }
        
        List<Category> results = categoryService.findByMsoId(msoId);
        
        return results;
    }
    
    /**
     * service for ApiMso.msoCategoryCreate
     * Create MSO promotion Category
     * @param msoId required, the Mso's Id
     * @param seq optional, sequence compare with other promotion Category
     * @param zhName optional, zhong wen name for display
     * @param enName optional, english name for display
     * @return promotion Category
     */
    public Category msoCategoryCreate(Long msoId, Short seq, String zhName, String enName) {
        
        if (msoId == null) {
            return null;
        }
        
        Category newCategory = new Category();
        newCategory.setMsoId(msoId);
        if (seq != null) {
            newCategory.setSeq(seq);
        }
        if (zhName != null) {
            newCategory.setZhName(zhName);
        }
        if (enName != null) {
            newCategory.setEnName(enName);
        }
        
        // create
        Category savedCategory = categoryService.create(newCategory);
        
        return savedCategory;
    }
    
    /**
     * service for ApiMso.category
     * Get promotion Category, don't use this get System Category.
     * @param categoryId required, Category ID
     * @return promotion Category
     */
    public Category category(Long categoryId) {
        
        if (categoryId == null) {
            return null;
        }
        
        Category result = categoryService.findById(categoryId);
        
        return result;
    }
    
    /**
     * service for ApiMso.categoryUpdate
     * Update promotion Category.
     * @param categoryId required, Category ID
     * @param seq optional, sequence compare with other promotion Category
     * @param zhName optional, zhong wen name for display
     * @param enName optional, english name for display
     * @return promotion Category
     */
    public Category categoryUpdate(Long categoryId, Short seq, String zhName, String enName) {
        
        if (categoryId == null) {
            return null;
        }
        
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            return null;
        }
        if (seq != null) {
            category.setSeq(seq);
        }
        if (zhName != null) {
            category.setZhName(zhName);
        }
        if (enName != null) {
            category.setEnName(enName);
        }
        category.setCntChannel(categoryService.getCntChannel(category.getId()));
        
        Category savedCategory = categoryService.save(category);
        
        return savedCategory;
    }
    
    /**
     * service for ApiMso.categoryDelete
     * Delete promotion Category.
     * @param categoryId required, Category ID
     */
    public void categoryDelete(Long categoryId) {
        
        if (categoryId == null) {
            return ;
        }
        
        categoryService.delete(categoryId);
    }
    
    /**
     * service for ApiMso.categoryChannels
     * Get Channels from promotion Category.
     * @param categoryId required, Category ID
     * @return list of Channels
     */
    public List<NnChannel> categoryChannels(Long categoryId) {
        
        if (categoryId == null) {
            return new ArrayList<NnChannel>();
        }
        
        List<NnChannel> results = categoryService.getChannelsOrderByUpdateTime(categoryId);
        if (results == null) {
            return new ArrayList<NnChannel>();
        }
        
        results = channelMngr.responseNormalization(results);
        
        return results;
    }
    
    /**
     * service for ApiMso.categoryChannelAdd
     * Add Channel to promotion Category.
     * @param category required, Category ID
     * @param channelIds optional, list of IDs that Channels to be added to promotion Category
     * @param channelId optional, ID that Channel to be added to promotion Category
     * @param seq optional, follow with channelId, indicate specify sequence of Channel in promotion Category
     * @param alwaysOnTop optional, follow with channelId, indicate Channel is set on top in the list of Channels from promotion Category
     */
    public void categoryChannelAdd(Category category, List<Long> channelIds, Long channelId, Short seq, Boolean alwaysOnTop) {
        
        if (category == null || category.getId() == 0) {
            return ;
        }
        
        if (channelIds != null) {
            
            if (channelIds.size() < 1) {
                return ;
            }
            
            List<Long> verifiedChannelIds = msoMngr.getPlayableChannels(channelIds, category.getMsoId());
            categoryService.addChannelsToCategory(category.getId(), verifiedChannelIds);
            
        } else if (channelId != null) {
            
            if (msoMngr.isPlayableChannel(channelId, category.getMsoId()) == true) {
                categoryService.addChannelToCategory(category.getId(), channelId, seq, alwaysOnTop);
            }
        }
    }
    
    /**
     * service for ApiMso.categoryChannelRemove
     * Remove Channel from promotion Category.
     * @param categoryId required, Category ID
     * @param channelIds required, list of IDs that Channels to be removed from promotion Category
     */
    public void categoryChannelRemove(Long categoryId, List<Long> channelIds) {
        
        if (categoryId == null || channelIds == null || channelIds.size() < 1) {
            return ;
        }
        
        categoryService.removeChannelsFromCategory(categoryId, channelIds);
    }
    
    /**
     * service for ApiMso.msoSystemCategoryLocks
     * Get system Category locks from MSO.
     * @param msoId required, the Mso's Id
     * @return the locks indicate system Category should hide or not in MSO's player
     */
    public List<String> msoSystemCategoryLocks(Long msoId) {
        
        if (msoId == null) {
            return new ArrayList<String>();
        }
        
        List<String> results = storeService.getStoreCategoryLocks(msoId);
        return results;
    }
    
    /**
     * service for ApiMso.msoSystemCategoryLocksUpdate
     * Update system Category locks from MSO, overwrite previous one.
     * @param msoId required, the Mso's Id
     * @param systemCategoryLocks required, the locks indicate system Category should hide or not in MSO's player
     * @return the locks indicate system Category should hide or not in MSO's player
     */
    public List<String> msoSystemCategoryLocksUpdate(Long msoId, List<String> systemCategoryLocks) {
        
        if (msoId == null) {
            return new ArrayList<String>();
        }
        
        List<String> results = storeService.setStoreCategoryLocks(msoId, systemCategoryLocks);
        return results;
    }

}
