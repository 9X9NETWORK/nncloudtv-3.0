package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserSubscribe;
import com.nncloudtv.model.Position;

public class NnUserSubscribeDao extends GenericDao<NnUserSubscribe>{
    
    protected static final Logger log = Logger.getLogger(NnUserSubscribeDao.class.getName());
    
    public NnUserSubscribeDao() {
        super(NnUserSubscribe.class);
    }
    
    public NnUserSubscribe save(NnUser user, NnUserSubscribe sub) {
        if (sub == null) {return null;}
        log.info("shard:"+user.getShard() + ";token:" + user.getToken());
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        try {
            pm.makePersistent(sub);
            sub = pm.detachCopy(sub);
        } catch (javax.jdo.JDODataStoreException e) {
            
        } finally {
            pm.close();
        }
        return sub;
    }        

    public void saveAll(NnUser user, List<NnUserSubscribe> subs) {
        Date now = new Date();
        for (NnUserSubscribe s : subs) {
            s.setUpdateDate(now);            
        }
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        try {
            pm.makePersistentAll(subs);
        } finally {
            pm.close();
        }
    }
    
    public void delete(NnUser user, NnUserSubscribe sub) {
        if (sub != null) {
            PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
            try {
                pm.deletePersistent(sub);
            } finally {
                pm.close();
            }
        }
    }
    
    public short findFirstAvailableSpot(NnUser user) {
        PersistenceManager pm = PMF.getNnUser1().getPersistenceManager(); //!problem
        short seq = 0;
        try {
            String sql = 
                "select * from position where seq not in " +
                "  (select seq from nnuser_subscribe where userId = ? and msoId = ?) limit 1; ";

            log.info("Sql=" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);            
            q.setClass(Position.class);
            @SuppressWarnings("unchecked")
            List<Position> list = (List<Position>) q.execute(user.getId(), user.getMsoId());                                
            if (list.size() > 0) {
                seq = list.get(0).getSeq();
            }            
        } finally {
            pm.close();
        }
        return seq;
    }
    
    public NnUserSubscribe findByUserAndSeq(NnUser user, short seq) {
        NnUserSubscribe s = null;
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        try {
            Query q = pm.newQuery(NnUserSubscribe.class);
            q.setFilter("userId == userIdParam && seq == seqParam && msoId == msoIdParam");
            q.declareParameters("long userIdParam, short seqParam, Long msoIdParam");
            q.setOrdering("seq asc");
            @SuppressWarnings("unchecked")
            List<NnUserSubscribe> subs = (List<NnUserSubscribe>)q.execute(user.getId(), seq, user.getMsoId());
            if (subs.size() > 0) {
                s = subs.get(0);
                s = pm.detachCopy(s);
            }
        } finally {
            pm.close();
        }
        return s;
    }     
    
    /*
    public NnUserSubscribe findChannelSubscription(NnUser user, long channelId, short seq) {
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        NnUserSubscribe detached = null;
        try {
            Query q = pm.newQuery(NnUserSubscribe.class);            
            q.setFilter("userId == userIdParam && channelId == channelIdParam && seq == seqParam");
            q.declareParameters("long userIdParam, long channelIdParam, short seqParam");
            q.setOrdering("seq asc");
            @SuppressWarnings("unchecked")
            List<NnUserSubscribe> subs = (List<NnUserSubscribe>)q.execute(user.getId(), channelId, seq);
            if (subs.size() > 0) {
                detached = subs.get(0);
                detached = pm.detachCopy(detached);
            }
        } finally {
            pm.close();
        }
        return detached;
    }
    */

    public NnUserSubscribe findByUserAndChannel(NnUser user, long channelId) {
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        NnUserSubscribe s = null;
        try {
            Query q = pm.newQuery(NnUserSubscribe.class);
            q.setFilter("userId == userIdParam && channelId== channelIdParam && msoId == msoIdParam");
            q.setOrdering("seq asc");
            q.declareParameters("long userIdParam, long channelIdParam, Long msoIdParam");
            @SuppressWarnings("unchecked")
            List<NnUserSubscribe> subs = (List<NnUserSubscribe>)q.execute(user.getId(), channelId, user.getMsoId());
            if (subs.size() > 0) {
                s = subs.get(0);
                s = pm.detachCopy(s);
            }
        } finally {
            pm.close();
        }
        return s;
    }
    
    public List<NnUserSubscribe> findAllByUser(NnUser user) {
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        List<NnUserSubscribe> detached = new ArrayList<NnUserSubscribe>();
        try {
            Query q = pm.newQuery(NnUserSubscribe.class);
            q.setFilter("userId == userIdParam && msoId == msoIdParam");
            q.declareParameters("long userIdParam, Long msoIdParam");
            q.setOrdering("seq asc");
            @SuppressWarnings("unchecked")
            List<NnUserSubscribe> subscriptions = (List<NnUserSubscribe>)q.execute(user.getId(), user.getMsoId());
            detached = (List<NnUserSubscribe>)pm.detachCopyAll(subscriptions);
        } finally {
            pm.close();
        }
        return detached;
    }     
        
    
}
