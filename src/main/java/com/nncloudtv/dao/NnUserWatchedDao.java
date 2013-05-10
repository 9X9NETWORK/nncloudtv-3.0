package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserWatched;

public class NnUserWatchedDao extends GenericDao<NnUserWatched>{

    protected static final Logger log = Logger.getLogger(NnUserWatchedDao.class.getName());
    
    public NnUserWatchedDao() {
        super(NnUserWatched.class);
    }

    public NnUserWatched save(NnUser user, NnUserWatched watched) {
        if (user == null) {return null;}
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        try {
            pm.makePersistent(watched);
            watched = pm.detachCopy(watched);
        } catch (JDODataStoreException e){
            e.printStackTrace();
        } finally {
            pm.close();
        }
        return watched;
    }

    public void delete(NnUser user, NnUserWatched watched) {
        if (watched == null) return;
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        try {
            pm.deletePersistent(watched);
        } finally {
            pm.close();
        }
    }

    @SuppressWarnings("unchecked")
    public NnUserWatched findByUserAndChannel(NnUser user, long channelId) {
        PersistenceManager pm = NnUserDao.getPersistenceManager((short)0, user.getToken());
        NnUserWatched watched = null;
        try {
            Query q = pm.newQuery(NnUserWatched.class);
            q.setFilter("userId == userIdParam && channelId== channelIdParam && msoId == msoIdParam");
            q.declareParameters("long userIdParam, long channelIdParam, long msoIdParam");
            List<NnUserWatched> results = (List<NnUserWatched>) q.execute(user.getId(), channelId, user.getMsoId());
            if (results.size() > 0) {
                watched = results.get(0);        
            }
            watched = pm.detachCopy(watched);
        } finally {
            pm.close();
        }
        return watched;        
    }
    
    @SuppressWarnings("unchecked")
    public List<NnUserWatched> findHistory(NnUser user) {
        PersistenceManager pm = NnUserDao.getPersistenceManager((short)0, user.getToken());
        //userPersistenceManager pm = PMF.getNnUser1().getPersistenceManager();
        ArrayList<NnUserWatched> results = new ArrayList<NnUserWatched>();
        try {
            Query q = pm.newQuery(NnUserWatched.class);
            q.setFilter("userId == userIdParam && msoId == msoIdParam");
            q.declareParameters("long userIdParam, long msoIdParam");
            q.setOrdering("updateDate desc");
            results.addAll((List<NnUserWatched>)q.execute(user.getId(), user.getMsoId()));
            results = (ArrayList<NnUserWatched>) pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return results;        
    }            
        
}