package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.PoiCampaign;

public class PoiCampaignDao extends GenericDao<PoiCampaign> {
    
    protected static final Logger log = Logger.getLogger(PoiCampaignDao.class.getName());
    
    public PoiCampaignDao() {
        super(PoiCampaign.class);
    }
    
    public List<PoiCampaign> findByUserId(long userId) {
        
        List<PoiCampaign> detached = new ArrayList<PoiCampaign>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        try {
            Query query = pm.newQuery(PoiCampaign.class);
            query.setFilter("userId == userIdParam");
            query.declareParameters("long userIdParam");
            //query.setOrdering("startDate desc");
            @SuppressWarnings("unchecked")
            List<PoiCampaign> results = (List<PoiCampaign>) query.execute(userId);
            if (results != null && results.size() > 0) {
                detached = (List<PoiCampaign>) pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        
        return detached;
    }

}
