package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.model.SysTagMap;
import com.nncloudtv.web.json.cms.Category;

@Service
public class CategoryService {
    
    protected static final Logger log = Logger.getLogger(CategoryService.class.getName());
    
    private NnChannelDao channelDao = new NnChannelDao();
    
    private SysTagManager sysTagMngr;
    private SysTagDisplayManager sysTagDisplayMngr;
    private SysTagMapManager sysTagMapMngr;
    private ContainerService containerService;
    
    @Autowired
    public CategoryService(SysTagManager sysTagMngr, SysTagDisplayManager sysTagDisplayMngr,
                        SysTagMapManager sysTagMapMngr, ContainerService containerService) {
        this.sysTagMngr = sysTagMngr;
        this.sysTagDisplayMngr = sysTagDisplayMngr;
        this.sysTagMapMngr = sysTagMapMngr;
        this.containerService = containerService;
    }
    
    /** build promotion Category (MSO owned) from SysTag and SysTagDisplay */
    private Category composeCategory(SysTag category, SysTagDisplay zhDisplay, SysTagDisplay enDisplay) {
        
        Category categoryResp = new Category();
        categoryResp.setId(category.getId());
        categoryResp.setLang(zhDisplay.getLang()); // default use zhDisplay
        categoryResp.setMsoId(category.getMsoId());
        categoryResp.setName(zhDisplay.getName()); // default use zhDisplay
        categoryResp.setSeq(category.getSeq());
        categoryResp.setCntChannel(zhDisplay.getCntChannel()); // CntChannel should be equal by both display
        categoryResp.setZhName(zhDisplay.getName());
        categoryResp.setEnName(enDisplay.getName());
        
        return categoryResp;
    }
    
    /** adapt Category to format that CMS API required */
    public static Category normalize(Category category) {
        
        category.setName(NnStringUtil.revertHtml(category.getName()));
        category.setZhName(NnStringUtil.revertHtml(category.getZhName()));
        category.setEnName(NnStringUtil.revertHtml(category.getEnName()));
        
        return category;
    }
    
    /**
     * Find Categories by MSO ID, which they owned by MSO.
     * @param msoId required, MSO ID
     * @return list of Categories
     */
    public List<Category> findByMsoId(Long msoId) {
        
        if (msoId == null) {
            return new ArrayList<Category>();
        }
        
        List<SysTag> categories = sysTagMngr.findByMsoIdAndType(msoId, SysTag.TYPE_CATEGORY);
        
        List<Category> results = new ArrayList<Category>();
        for (SysTag category : categories) {
            SysTagDisplay zhCategoryDisplay = sysTagDisplayMngr.findBySysTagIdAndLang(category.getId(), LangTable.LANG_ZH);
            SysTagDisplay enCategoryDisplay = sysTagDisplayMngr.findBySysTagIdAndLang(category.getId(), LangTable.LANG_EN);
            
            if (zhCategoryDisplay != null && enCategoryDisplay != null) {
                Category result = composeCategory(category, zhCategoryDisplay, enCategoryDisplay);
                results.add(result);
            } else if (enCategoryDisplay != null) {
                Category result = composeCategory(category, enCategoryDisplay, enCategoryDisplay);
                results.add(result);
                log.warning("Category ID=" + category.getId() + " has no matching ZH-display");
            } else if (zhCategoryDisplay != null) {
                Category result = composeCategory(category, zhCategoryDisplay, zhCategoryDisplay);
                results.add(result);
                log.warning("Category ID=" + category.getId() + " has no matching EN-display");
            } else {
                Category result = composeCategory(category, new SysTagDisplay(), new SysTagDisplay());
                results.add(result);
                log.warning("Category ID=" + category.getId() + " has nothing display");
            }
        }
        
        return results;
    }
    
    /**
     * Get Category by Category ID.
     * @param categoryId required, Category ID
     * @return fetched Category
     */
    public Category findById(Long categoryId) {
        
        if (categoryId == null) {
            return null;
        }
        
        SysTag category = sysTagMngr.findById(categoryId);
        if (category == null || category.getType() != SysTag.TYPE_CATEGORY) {
            return null;
        }
        
        SysTagDisplay zhCategoryDisplay = sysTagDisplayMngr.findBySysTagIdAndLang(categoryId, LangTable.LANG_ZH);
        SysTagDisplay enCategoryDisplay = sysTagDisplayMngr.findBySysTagIdAndLang(categoryId, LangTable.LANG_EN);
        
        Category result;
        if (zhCategoryDisplay != null && enCategoryDisplay != null) {
            result = composeCategory(category, zhCategoryDisplay, enCategoryDisplay);
        } else if (enCategoryDisplay != null) {
            result = composeCategory(category, enCategoryDisplay, enCategoryDisplay);
            log.warning("Category ID=" + category.getId() + " has no matching ZH-display");
        } else if (zhCategoryDisplay != null) {
            result = composeCategory(category, zhCategoryDisplay, zhCategoryDisplay);
            log.warning("Category ID=" + category.getId() + " has no matching EN-display");
        } else {
            result = composeCategory(category, new SysTagDisplay(), new SysTagDisplay());
            log.warning("Category ID=" + category.getId() + " has nothing display");
        }
        
        return result;
    }
    
    /**
     * Create a new Category.
     * @param category required, the new Category
     * @return saved Category
     */
    public Category create(Category category) {
        
        if (category == null) {
            return null;
        }
        
        SysTag newCategory = new SysTag();
        newCategory.setMsoId(category.getMsoId());
        newCategory.setType(SysTag.TYPE_CATEGORY);
        newCategory.setSeq(category.getSeq());
        newCategory.setSorting(SysTag.SORT_DATE);
        SysTag savedCategory = sysTagMngr.save(newCategory);
        
        SysTagDisplay newZhCategoryDisplay = new SysTagDisplay();
        newZhCategoryDisplay.setSystagId(savedCategory.getId());
        newZhCategoryDisplay.setLang(LangTable.LANG_ZH);
        newZhCategoryDisplay.setCntChannel(0);
        newZhCategoryDisplay.setName(category.getZhName());
        SysTagDisplay savedZhCategoryDisplay = sysTagDisplayMngr.save(newZhCategoryDisplay);
        
        SysTagDisplay newEnCategoryDisplay = new SysTagDisplay();
        newEnCategoryDisplay.setSystagId(savedCategory.getId());
        newEnCategoryDisplay.setLang(LangTable.LANG_EN);
        newEnCategoryDisplay.setCntChannel(0);
        newEnCategoryDisplay.setName(category.getEnName());
        SysTagDisplay savedEnCategoryDisplay = sysTagDisplayMngr.save(newEnCategoryDisplay);
        
        Category result = composeCategory(savedCategory, savedZhCategoryDisplay, savedEnCategoryDisplay);
        
        return result;
    }
    
    /**
     * Save a modified Category.
     * @param modifiedCategory required, the Category that has modified
     * @return saved Category
     */
    public Category save(Category modifiedCategory) {
        
        if (modifiedCategory == null) {
            return null;
        }
        
        SysTag category = sysTagMngr.findById(modifiedCategory.getId());
        if (category == null || category.getType() != SysTag.TYPE_CATEGORY ||
                category.getMsoId() != modifiedCategory.getMsoId()) {
            return null;
        }
        
        SysTagDisplay zhCategoryDisplay = sysTagDisplayMngr.findBySysTagIdAndLang(modifiedCategory.getId(), LangTable.LANG_ZH);
        if (zhCategoryDisplay == null) {
            // create one
            zhCategoryDisplay = new SysTagDisplay();
            zhCategoryDisplay.setSystagId(category.getId());
            zhCategoryDisplay.setLang(LangTable.LANG_ZH);
            zhCategoryDisplay.setCntChannel(0);
        }
        
        SysTagDisplay enCategoryDisplay = sysTagDisplayMngr.findBySysTagIdAndLang(modifiedCategory.getId(), LangTable.LANG_EN);
        if (enCategoryDisplay == null) {
            // create one
            enCategoryDisplay = new SysTagDisplay();
            enCategoryDisplay.setSystagId(category.getId());
            enCategoryDisplay.setLang(LangTable.LANG_EN);
            enCategoryDisplay.setCntChannel(0);
        }
        
        // modify SysTag category
        category.setSeq(modifiedCategory.getSeq());
        SysTag savedCategory = sysTagMngr.save(category);
        
        // modify SysTagDisplay zhCategoryDisplay
        zhCategoryDisplay.setName(modifiedCategory.getZhName());
        zhCategoryDisplay.setCntChannel(modifiedCategory.getCntChannel());
        SysTagDisplay savedZhCategoryDisplay = sysTagDisplayMngr.save(zhCategoryDisplay);
        
        // modify SysTagDisplay zhCategoryDisplay
        enCategoryDisplay.setName(modifiedCategory.getEnName());
        enCategoryDisplay.setCntChannel(modifiedCategory.getCntChannel());
        SysTagDisplay savedEnCategoryDisplay = sysTagDisplayMngr.save(enCategoryDisplay);
        
        Category result = composeCategory(savedCategory, savedZhCategoryDisplay, savedEnCategoryDisplay);
        
        return result;
    }
    
    /**
     * Delete a Category.
     * @param categoryId required, Category ID
     */
    public void delete(Long categoryId) {
        
        if (categoryId == null) {
            return ;
        }
        
        containerService.delete(categoryId);
    }
    
    /**
     * Add Channels to Category.
     * @param categoryId required, Category ID
     * @param channelIds required, Channel's IDs
     */
    public void addChannelsToCategory(Long categoryId, List<Long> channelIds) {
        
        if (categoryId == null || channelIds == null || channelIds.size() < 1) {
            return ;
        }
        
        List<SysTagMap> existChannels = sysTagMapMngr.findBySysTagIdAndChannelIds(categoryId, channelIds);
        Map<Long, Long> existChannelIds = new TreeMap<Long, Long>();
        for (SysTagMap existChannel : existChannels) {
            existChannelIds.put(existChannel.getChannelId(), existChannel.getChannelId());
        }
        
        List<SysTagMap> newChannels = new ArrayList<SysTagMap>();
        for (Long channelId : channelIds) {
            if (existChannelIds.containsKey(channelId)) {
                // skip
            } else {
                SysTagMap newChannel = new SysTagMap(categoryId, channelId);
                newChannel.setSeq((short) 0);
                newChannel.setTimeStart((short) 0);
                newChannel.setTimeEnd((short) 0);
                newChannel.setAlwaysOnTop(false);
                newChannels.add(newChannel);
            }
        }
        
        sysTagMapMngr.saveAll(newChannels);
    }
    
    /**
     * Add Channel to Category with additional setting.
     * @param categoryId required, Category ID
     * @param channelId required, Channel ID
     * @param seq optional, the sequence of Channel in the Category
     * @param alwaysOnTop optional, indicate the Channel is set on top in Category or not
     */
    public void addChannelToCategory(Long categoryId, Long channelId, Short seq, Boolean alwaysOnTop) {
        
        if (categoryId == null || channelId == null) {
            return ;
        }
        
        containerService.addChannel(categoryId, channelId, null, null, alwaysOnTop, seq);
    }
    
    /**
     * Remove Channels from Category.
     * @param categoryId required, Category ID
     * @param channelIds required, Channel's IDs to be removed
     */
    public void removeChannelsFromCategory(Long categoryId, List<Long> channelIds) {
        
        if (categoryId == null || channelIds == null || channelIds.size() < 1) {
            return ;
        }
        
        List<SysTagMap> existChannels = sysTagMapMngr.findBySysTagIdAndChannelIds(categoryId, channelIds);
        
        sysTagMapMngr.deleteAll(existChannels);
    }
    
    /**
     * Get Channels from Category, Channels are order by update time.
     * @param categoryId required, Category ID
     * @return list of Channels
     */
    public List<NnChannel> getChannelsFromCategory(Long categoryId) {
        
        if (categoryId == null) {
            return new ArrayList<NnChannel>();
        }
        
        List<NnChannel> results = channelDao.getStoreChannelsFromCategory(categoryId, null);
        if (results == null) {
            return new ArrayList<NnChannel>();
        }
        
        return results;
    }
    
    /**
     * Get Channels from Category, Channels are order by update time with set on top feature.
     * @param categoryId required, Category ID
     * @return list of Channels
     */
    public List<NnChannel> getChannelsOrderByUpdateTime(Long categoryId) {
        
        if (categoryId == null) {
            return new ArrayList<NnChannel>();
        }
        
        List<NnChannel> results = containerService.getChannelsOrderByUpdateTime(categoryId);
        if (results == null) {
            return new ArrayList<NnChannel>();
        }
        
        return results;
    }
    
    /**
     * Get total number of Channels in the Category.
     * @param categoryId required, Category ID
     * @return number of Channels
     */
    public int getCntChannel(Long categoryId) {
        
        if (categoryId == null) {
            return 0;
        }
        
        List<SysTagMap> channels = sysTagMapMngr.findBySysTagId(categoryId);
        return channels.size();
    }

}
