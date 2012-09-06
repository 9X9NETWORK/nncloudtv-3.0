package com.nncloudtv.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.CntView;

public class CntSubscribeDao {
    public CntView save(CntView log) {        
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        try {
            pm.makePersistent(log);
            log = pm.detachCopy(log);
        } finally {
            pm.close();
        }
        return log;
    }
        
    public int findTotalCountByChannel(long channelId) {        
        int totalCount = 0;
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        try {
            Query q = pm.newQuery(CntView.class);
            q.setFilter("channelId == channelIdParam");
            q.declareParameters("long channelIdParam");
            @SuppressWarnings("unchecked")
            List<CntView> subs = (List<CntView>)q.execute(channelId);
            for (CntView s : subs)
                totalCount += s.getCnt();
        } finally {
            pm.close();
        }
        return totalCount;
    }

    public int findTotalCountBySet(long setId) {        
        int totalCount = 0;
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        try {
            Query q = pm.newQuery(CntView.class);
            q.setFilter("setId == setIdParam");
            q.declareParameters("long setIdParam");
            @SuppressWarnings("unchecked")
            List<CntView> subs = (List<CntView>)q.execute(setId);
            for (CntView s : subs)
                totalCount += s.getCnt();
        } finally {
            pm.close();
        }
        return totalCount;
    }
    
    public CntView findByChannel(long channelId) {
        CntView s = null;
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        try {
            Query q = pm.newQuery(CntView.class);
            q.setFilter("channelId == channelIdParam");
            q.declareParameters("long channelIdParam");
            @SuppressWarnings("unchecked")
            List<CntView> subs = (List<CntView>)q.execute(channelId);
            if (subs.size() > 0) {
                s = subs.get(0);
                s = pm.detachCopy(s);
            }
        } finally {
            pm.close();
        }
        return s;        
    }        
    
}
