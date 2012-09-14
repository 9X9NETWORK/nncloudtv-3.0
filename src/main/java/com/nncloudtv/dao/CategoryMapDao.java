package com.nncloudtv.dao;

import java.util.ArrayList;
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
    
    public List<CategoryMap> findByChannelId(long channelId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        List<CategoryMap> results = new ArrayList<CategoryMap>();
        try {
            Query query = pm.newQuery(CategoryMap.class);
            query.setFilter("channelId == channelIdParam");
            query.declareParameters("long channelIdParam");
            query.setOrdering("updateDate desc");
            @SuppressWarnings("unchecked")
            List<CategoryMap> categoryMaps = (List<CategoryMap>) query.execute(channelId);
            results = (List<CategoryMap>) pm.detachCopyAll(categoryMaps);
        } finally {
            pm.close();
        }
        return results;
    }

}
