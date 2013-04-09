package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnSet;

public class NnSetDao extends GenericDao<NnSet> {
    
    protected static final Logger log = Logger.getLogger(NnSetDao.class.getName());
    
    public NnSetDao() {
        super(NnSet.class);
    }
    
    public List<NnSet> findFeatured(String lang, long msoId) {        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnSet> detached = new ArrayList<NnSet>(); 
        try {
            Query q = pm.newQuery(NnSet.class);
            q.setFilter("featured == featuredParam && lang == langParam && msoId == msoIdParam");
            q.declareParameters("boolean featuredParam, String langParam, Long msoIdParam");
            q.setOrdering("seq asc");
            @SuppressWarnings("unchecked")
            List<NnSet> sets = (List<NnSet>) q.execute(true, lang, msoId);
            detached = (List<NnSet>)pm.detachCopyAll(sets);
        } finally {
            pm.close();
        }
        return detached;
    }   
    
    public NnSet findByName(String name, long msoId) {      
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        NnSet detached = null;      
        try {
            Query query = pm.newQuery(NnSet.class);
            query.setFilter("name == nameParam && msoId == msoIdParam");
            query.declareParameters("String nameParam, long msoIdParam");
            @SuppressWarnings("unchecked")
            List<NnSet> channelNnSets = (List<NnSet>) query.execute(name, msoId);
            if (channelNnSets.size() > 0) {
                detached = pm.detachCopy(channelNnSets.get(0));
            }
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }        
        return detached;
    }
    
}
