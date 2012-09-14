package com.nncloudtv.service;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.CategoryMapDao;
import com.nncloudtv.model.CategoryMap;

@Service
public class CategoryMapManager {
    
    protected static final Logger log = Logger.getLogger(NnUserLibraryManager.class.getName());
    
    private CategoryMapDao catMapDao = new CategoryMapDao();
    
    public CategoryMap findByChannelId(Long channelId) {       
        CategoryMap categoryMap = catMapDao.findByChannelId(channelId);  
        return categoryMap;
    }

}
