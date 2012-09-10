package com.nncloudtv.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnAd;

public class NnAdDao extends GenericDao<NnAd> {
    
    protected static final Logger log = Logger.getLogger(NnAdDao.class.getName());
    
    public NnAdDao() {
        super(NnAd.class);
    }
    
    public NnAd findByProgramId(long programId) {
        NnAd detached = null;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            Query query = pm.newQuery(NnAd.class);
            query.setFilter("programId == programIdParam");
            query.declareParameters("long programIdParam");
            @SuppressWarnings("unchecked")
            List<NnAd> ads = (List<NnAd>)query.execute(programId);
            if (ads.size() > 0)
                detached = pm.detachCopy(ads.get(0));
        } finally {
            pm.close();
        }
        return detached;
    }
    
}
