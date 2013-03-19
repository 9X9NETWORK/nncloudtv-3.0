package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserPref;

public class NnUserPrefDao extends GenericDao<NnUserPref> {

    protected static final Logger log = Logger.getLogger(NnUserPref.class.getName());
    
    public NnUserPrefDao() {
        super(NnUserPref.class);
    }    
        
    private PersistenceManager getPersistenceManager(NnUser user) {
        if (user.getShard() == 1) {
            return PMF.getNnUser1().getPersistenceManager();
        } else {
            return PMF.getNnUser2().getPersistenceManager();
        }
    }
    
    public NnUserPref save(NnUser user, NnUserPref pref) {
        if (pref == null) {return null;}
        PersistenceManager pm = this.getPersistenceManager(user);
        try {
            pm.makePersistent(pref);
            pref = pm.detachCopy(pref);
        } finally {
            pm.close();
        }
        return pref;
    }

    public List<NnUserPref> findByUser(NnUser user) {
        List<NnUserPref> pref = new ArrayList<NnUserPref>();
        PersistenceManager pm = this.getPersistenceManager(user);
        try {
            Query query = pm.newQuery(NnUserPref.class);
            query.setFilter("userId == userIdParam && msoId == msoIdParam");
            query.declareParameters("long userIdParam, long msoIdParam");        
            @SuppressWarnings("unchecked")
            List<NnUserPref> results = (List<NnUserPref>) query.execute(user.getId(), user.getMsoId());
            pref = (List<NnUserPref>) pm.detachCopyAll(results);
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return pref;        
    }    

    public NnUserPref findByUserAndItem(NnUser user, String item) {
        NnUserPref pref = null;
        PersistenceManager pm = PMF.getNnUser1().getPersistenceManager();
        try {
            Query query = pm.newQuery(NnUserPref.class);
            query.setFilter("userId == userIdParam && item == itemParam && msoId == msoIdParam");
            query.declareParameters("long userIdParam, String itemParam, long msoIdParam");
            @SuppressWarnings("unchecked")
            List<NnUserPref> results = (List<NnUserPref>) query.execute(user.getId(), item, user.getMsoId() );
            if (results.size() > 0) {        
                pref = (NnUserPref) pm.detachCopy(results.get(0));
            }
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return pref;
    }

    public void delete(NnUser user, NnUserPref pref) {        
        if (pref == null) return;                    
        
        PersistenceManager pm = this.getPersistenceManager(user);        
        try {
            pm.deletePersistent(pref);
        } finally {
            pm.close();
        }
        
    }
    
}
