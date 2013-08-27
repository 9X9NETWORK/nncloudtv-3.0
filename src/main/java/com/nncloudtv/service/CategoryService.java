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
    private NnChannelManager channelMngr;
    
    @Autowired
    public CategoryService(SysTagManager sysTagMngr, SysTagDisplayManager sysTagDisplayMngr,
                        SysTagMapManager sysTagMapMngr, NnChannelManager channelMngr) {
        this.sysTagMngr = sysTagMngr;
        this.sysTagDisplayMngr = sysTagDisplayMngr;
        this.sysTagMapMngr = sysTagMapMngr;
        this.channelMngr = channelMngr;
    }
    
    /** build Category from SysTag and SysTagDisplay */
    private Category composeCategory(SysTag category, SysTagDisplay categoryMetadata) {
        
        Category categoryResp = new Category();
        categoryResp.setId(category.getId());
        categoryResp.setLang(categoryMetadata.getLang());
        categoryResp.setMsoId(category.getMsoId());
        categoryResp.setName(NnStringUtil.revertHtml(categoryMetadata.getName()));
        categoryResp.setSeq(category.getSeq());
        
        return categoryResp;
    }
    
    public List<Category> findByMsoId(Long msoId) {
        
        if (msoId == null) {
            return new ArrayList<Category>();
        }
        
        List<SysTag> categories = sysTagMngr.findByMsoIdAndType(msoId, SysTag.TYPE_CATEGORY);
        
        List<Category> results = new ArrayList<Category>();
        for (SysTag category : categories) {
            SysTagDisplay zhCategoryDisplay = sysTagDisplayMngr.findBySysTagIdAndLang(category.getId(), LangTable.LANG_ZH);
            SysTagDisplay enCategoryDisplay = sysTagDisplayMngr.findBySysTagIdAndLang(category.getId(), LangTable.LANG_EN);
            
            // TODO need define Category response
            Category result = composeCategory(category, zhCategoryDisplay);
            results.add(result);
        }
        
        // TODO need define sorting
        
        return results;
    }
    
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
        if (zhCategoryDisplay == null || enCategoryDisplay == null) {
            return null;
        }
        
        // TODO need define Category response
        Category result = composeCategory(category, zhCategoryDisplay);
        
        return result;
    }
    
    public Category create(Category category) {
        
        if (category == null) {
            return null;
        }
        
        SysTag newCategory = new SysTag();
        newCategory.setMsoId(category.getMsoId());
        newCategory.setType(SysTag.TYPE_CATEGORY);
        // TODO other setting
        SysTag savedCategory = sysTagMngr.save(newCategory);
        
        SysTagDisplay newZhCategoryDisplay = new SysTagDisplay();
        newZhCategoryDisplay.setSystagId(savedCategory.getId());
        newZhCategoryDisplay.setLang(LangTable.LANG_ZH);
        // TODO other setting
        SysTagDisplay savedZhCategoryDisplay = sysTagDisplayMngr.save(newZhCategoryDisplay);
        
        SysTagDisplay newEnCategoryDisplay = new SysTagDisplay();
        newEnCategoryDisplay.setSystagId(savedCategory.getId());
        newEnCategoryDisplay.setLang(LangTable.LANG_EN);
        // TODO other setting
        SysTagDisplay savedEnCategoryDisplay = sysTagDisplayMngr.save(newEnCategoryDisplay);
        
        Category result = composeCategory(savedCategory, savedZhCategoryDisplay);
        
        return result;
    }
    
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
        SysTagDisplay enCategoryDisplay = sysTagDisplayMngr.findBySysTagIdAndLang(modifiedCategory.getId(), LangTable.LANG_EN);
        if (zhCategoryDisplay == null || enCategoryDisplay == null) {
            return null;
        }
        
        // TODO modify SysTag category
        SysTag savedCategory = sysTagMngr.save(category);
        
        // TODO modify SysTagDisplay zhCategoryDisplay
        SysTagDisplay savedZhCategoryDisplay = sysTagDisplayMngr.save(zhCategoryDisplay);
        
        // TODO modify SysTagDisplay zhCategoryDisplay
        SysTagDisplay savedEnCategoryDisplay = sysTagDisplayMngr.save(enCategoryDisplay);
        
        // TODO need define Category response
        Category result = composeCategory(savedCategory, savedZhCategoryDisplay);
        
        return result;
    }
    
    public void delete(Long categoryId) {
        
        // delete channels, SysTagMap
        List<SysTagMap>  channels = sysTagMapMngr.findBySysTagId(categoryId);
        if (channels != null && channels.size() > 0) {
            sysTagMapMngr.deleteAll(channels);
        }
        // delete displays, SysTagDisplay
        List<SysTagDisplay> displays = sysTagDisplayMngr.findAllBySysTagId(categoryId);
        if (displays != null && displays.size() > 0) {
            sysTagDisplayMngr.deleteAll(displays);
        }
        // delete category, SysTag
        SysTag category = sysTagMngr.findById(categoryId);
        if (category != null) {
            sysTagMngr.delete(category);
        }
    }
    
    public void addChannelsToCategory(Long categoryId, List<NnChannel> channels) {
        
        if (categoryId == null || channels == null || channels.size() < 1) {
            return ;
        }
        
        List<Long> channelIds = new ArrayList<Long>();
        for (NnChannel channel : channels) {
            channelIds.add(channel.getId());
        }
        
        List<SysTagMap> existChannels = sysTagMapMngr.findBySysTagIdAndChannelIds(categoryId, channelIds);
        Map<Long, Long> existChannelIds = new TreeMap<Long, Long>();
        for (SysTagMap existChannel : existChannels) {
            existChannelIds.put(existChannel.getChannelId(), existChannel.getChannelId());
        }
        
        List<SysTagMap> newChannels = new ArrayList<SysTagMap>();
        for (NnChannel channel : channels) {
            if (existChannelIds.containsKey(channel.getId())) {
                // skip
            } else {
                SysTagMap newChannel = new SysTagMap(categoryId, channel.getId());
                newChannels.add(newChannel);
            }
        }
        
        sysTagMapMngr.saveAll(newChannels);
    }
    
    public void removeChannelsFromCategory(Long categoryId, List<Long> channelIds) {
        
        if (categoryId == null || channelIds == null || channelIds.size() < 1) {
            return ;
        }
        
        /*
        List<Long> channelIds = new ArrayList<Long>();
        for (NnChannel channel : channels) {
            channelIds.add(channel.getId());
        }
        */
        
        List<SysTagMap> existChannels = sysTagMapMngr.findBySysTagIdAndChannelIds(categoryId, channelIds);
        
        sysTagMapMngr.deleteAll(existChannels);
    }
    
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

}
