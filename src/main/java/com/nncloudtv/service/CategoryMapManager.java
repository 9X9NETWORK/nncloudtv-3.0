package com.nncloudtv.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.CategoryMapDao;
import com.nncloudtv.model.CategoryMap;

@Service
public class CategoryMapManager {
    
    protected static final Logger log = Logger.getLogger(NnUserLibraryManager.class.getName());
    
    private CategoryMapDao catMapDao = new CategoryMapDao();
    
    public List<CategoryMap> findByChannelId(Long channelId) {       
        List<CategoryMap> categoryMaps = catMapDao.findByChannelId(channelId);  
        return categoryMaps;
    }
    
    public void create(CategoryMap categoryMap) {
        
        List<CategoryMap> categoryMaps = catMapDao.findByChannelId(categoryMap.getChannelId());
        int i;
        for (i=0; i<categoryMaps.size(); i++) {
            if (categoryMaps.get(i).getCategoryId()==categoryMap.getCategoryId()) {
                this.save(categoryMaps.get(i)); // the database already has one, only change the updateDate.
                break;
            }
        }
        if (i==categoryMaps.size()) {
            this.save(categoryMap); // not found in database, newly created.
        }
    }
    
    public void save(CategoryMap categoryMap) {
        catMapDao.save(categoryMap);
    }
    
}
