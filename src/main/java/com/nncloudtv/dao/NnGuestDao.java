package com.nncloudtv.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.model.NnGuest;

public class NnGuestDao extends GenericDao<NnGuest> {

    public NnGuestDao() {
        super(NnGuest.class);
    }

    public NnGuest save(NnGuest guest) {
        if (guest == null) {return null;}
        PersistenceManager pm = NnUserDao.getPersistenceManager(guest.getShard(), guest.getToken());
        try {
            pm.makePersistent(guest);
            guest = pm.detachCopy(guest);
        } finally {
            pm.close();
        }
        return guest;
    }
    
    public NnGuest findByToken(String token) {
        NnGuest guest = null;
        PersistenceManager pm = NnUserDao.getPersistenceManager((short)0, token);
        try {
            Query query = pm.newQuery(NnGuest.class);
            query.setFilter("token == tokenParam");
            query.declareParameters("String tokenParam");        
            @SuppressWarnings("unchecked")
            List<NnGuest> results = (List<NnGuest>) query.execute(token);
            if (results.size() > 0) {
                guest = results.get(0);            
            }
            guest = pm.detachCopy(guest);
        } finally {
            pm.close();
        }
        return guest;                
    }

    public void delete(NnGuest guest) {
        if (guest == null) return;
        PersistenceManager pm = NnUserDao.getPersistenceManager(guest.getShard(), guest.getToken());
        try {
            pm.deletePersistent(guest);
        } finally {
            pm.close();
        }        
    }    
}
