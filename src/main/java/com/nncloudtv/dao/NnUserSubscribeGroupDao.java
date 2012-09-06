package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserSubscribeGroup;

public class NnUserSubscribeGroupDao extends GenericDao<NnUserSubscribeGroup> {
    
    protected static final Logger log = Logger.getLogger(NnUserSubscribeGroupDao.class.getName());
    
    public NnUserSubscribeGroupDao() {
        super(NnUserSubscribeGroup.class);
    }
    
    public NnUserSubscribeGroup save(NnUser user, NnUserSubscribeGroup subSet) {
        if (subSet == null) {return null;}
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        try {
            pm.makePersistent(subSet);
            subSet = pm.detachCopy(subSet);
        } finally {
            pm.close();
        }
        return subSet;
    }    
    
    public List<NnUserSubscribeGroup> findByUser(NnUser user) {        
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        ArrayList<NnUserSubscribeGroup> detached = new ArrayList<NnUserSubscribeGroup>();        
        try {
            Query query = pm.newQuery(NnUserSubscribeGroup.class);
            query.setFilter("userId == userIdParam");
            query.declareParameters("long userIdParam");
            @SuppressWarnings("unchecked")
            List<NnUserSubscribeGroup> tmp = (List<NnUserSubscribeGroup>)query.execute(user.getId());
            detached = (ArrayList<NnUserSubscribeGroup>) pm.detachCopyAll(tmp);
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return detached;
    }

    public NnUserSubscribeGroup findByUserAndSeq(NnUser user, short seq) {        
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        NnUserSubscribeGroup detached = null;        
        try {
            Query query = pm.newQuery(NnUserSubscribeGroup.class);
            query.setFilter("userId == userIdParam && seq == seqParam");
            query.declareParameters("long userIdParam, short seqParam");
            @SuppressWarnings("unchecked")
            List<NnUserSubscribeGroup> result = (List<NnUserSubscribeGroup>)query.execute(user.getId(), seq);
            if (result.size() > 0) {
                detached = pm.detachCopy(result.get(0));
            } 
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return detached;
    }
    
    public void delete(NnUser user, NnUserSubscribeGroup group) {
        if (group == null) return;
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        try {
            pm.deletePersistent(group);
        } finally {
            pm.close();
        }
    }
    
}
