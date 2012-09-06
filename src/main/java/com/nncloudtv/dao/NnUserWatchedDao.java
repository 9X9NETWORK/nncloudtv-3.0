package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
    public NnUserWatched findByUserTokenAndChannel(String token, long channelId) {
        PersistenceManager pm = NnUserDao.getPersistenceManager((short)0, token);
        NnUserWatched watched = null;
        try {
            Query q = pm.newQuery(NnUserWatched.class);
            q.setFilter("userToken == userTokenParam && channelId== channelIdParam");
            q.declareParameters("String userTokenParam, long channelIdParam");
            List<NnUserWatched> results = (List<NnUserWatched>) q.execute(token, channelId);
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
    public List<NnUserWatched> findByUserToken(String token) {
        PersistenceManager pm = NnUserDao.getPersistenceManager((short)0, token);
        ArrayList<NnUserWatched> results = new ArrayList<NnUserWatched>();
        try {
            Query q = pm.newQuery(NnUserWatched.class);
            q.setFilter("userToken == userTokenParam");
            q.declareParameters("String userTokenParam");
            results.addAll((List<NnUserWatched>)q.execute(token));
            results = (ArrayList<NnUserWatched>) pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return results;        
    }
        
    
    /*
    @SuppressWarnings("unchecked")
    public List<NnUserWatched> findAllByUser(long userId, String userToken) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        ArrayList<NnUserWatched> results = new ArrayList<NnUserWatched>();
        
        try {
            Query query = pm.newQuery(NnUserWatched.class);
            if (userId != 0) {
                query.setFilter("userId == userIdParam");
                query.declareParameters("long userIdParam");
                query.setOrdering("createDate DESC");
                results.addAll((List<NnUserWatched>)query.execute(userId));
            } else {
                query.setFilter("userToken == userTokenParam");
                query.declareParameters("String userTokenParam");                
                results.addAll((List<NnUserWatched>)query.execute(userToken));
            }
            results = (ArrayList<NnUserWatched>) pm.detachCopyAll(results);
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return results;    
    }

    */
}