package com.nncloudtv.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Deep;

public class DeepDao {
    protected static final Logger log = Logger.getLogger(ShallowDao.class.getName());
    
    public Deep findByUser(short shard, long userId) {
        PersistenceManager pm = PMF.getRecommend().getPersistenceManager();
        Deep deep = null; 
        try {
            Query q = pm.newQuery(Deep.class);
            q.setFilter("shard == shardParam && userId == userIdParam");
            q.declareParameters("short shardParam, long userIdParam");
            @SuppressWarnings("unchecked")
            List<Deep> list = (List<Deep>) q.execute(shard, userId);
            if (list.size() > 0) {
                deep = list.get(0);        
                deep = pm.detachCopy(deep);
            }
        } finally {
            pm.close();
        }
        return deep;
    }

}
