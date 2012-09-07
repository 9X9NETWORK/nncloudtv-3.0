package com.nncloudtv.dao;

import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Shallow;

public class ShallowDao {
    protected static final Logger log = Logger.getLogger(ShallowDao.class.getName());
    
    public Shallow findByChannelId(long chId) {
        PersistenceManager pm = PMF.getRecommend().getPersistenceManager();
        Shallow detached = null;
        try {
            Shallow shallow = (Shallow)pm.getObjectById(Shallow.class, chId);
            detached = (Shallow)pm.detachCopy(shallow);
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return detached;        
    }    

}
