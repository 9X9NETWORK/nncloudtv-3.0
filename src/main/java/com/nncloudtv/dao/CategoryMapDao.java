package com.nncloudtv.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.CategoryMap;

public class CategoryMapDao extends GenericDao<CategoryMap> {
    
    protected static final Logger log = Logger.getLogger(NnUserLibraryDao.class.getName());
    
    public CategoryMapDao() {
        super(CategoryMap.class);
    }
    
    public CategoryMap findByChannelId(long channelId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        CategoryMap categoryMap = null;
        try {
            
            Query query = pm.newQuery(CategoryMap.class);
            query.setFilter("channelId == channelIdParam");
            query.declareParameters("long userIdStrParam");
            @SuppressWarnings("unchecked")
            List<CategoryMap> categoryMaps = (List<CategoryMap>) query
                    .execute(channelId);
            if (categoryMaps.size() > 0) {
                categoryMap = pm.detachCopy(categoryMaps.get(0));
            }
        } finally {
            pm.close();
        }
        return categoryMap;
    }

}
