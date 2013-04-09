package com.nncloudtv.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserProfile;

public class NnUserProfileDao extends GenericDao<NnUserProfile> {

    protected static final Logger log = Logger.getLogger(NnUserProfileDao.class.getName());
    
    public NnUserProfileDao() {
        super(NnUserProfile.class);
    }

    public NnUserProfile findByUser(NnUser user) {
        if (user == null)
            return null;
        log.info("user id:" + user.getId() + ";mso id:" + user.getMsoId());
        NnUserProfile detached = null;
        PersistenceManager pm = NnUserDao.getPersistenceManager(NnUser.SHARD_DEFAULT, null);
        try {
            String sql = "select * " +
                          " from nnuser_profile " + 
                         " where msoId = " + user.getMsoId() +   
                           " and userId = " + user.getId();
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnUserProfile.class);
            @SuppressWarnings("unchecked")
            List<NnUserProfile> results = (List<NnUserProfile>) q.execute();
            if (results.size() > 0) {
                detached = (NnUserProfile)pm.detachCopy(results.get(0));
            }            
        } finally {
            pm.close();
        }
        return detached;
    }

    public NnUserProfile save(NnUser user, NnUserProfile profile) {
        if (profile == null) {return null;}
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        try {
            pm.makePersistent(profile);
            profile = pm.detachCopy(profile);
        } finally {
            pm.close();
        }
        return profile;
    }
    
}
