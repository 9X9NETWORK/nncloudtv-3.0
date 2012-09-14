package com.nncloudtv.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnEpisode;

public class NnEpisodeDao extends GenericDao<NnEpisode> {
    protected static final Logger log = Logger.getLogger(NnEpisodeDao.class.getName());
    
    public NnEpisodeDao() {
        super(NnEpisode.class);
    }
    
    public NnEpisode findByAdId(long adId) {
        
        NnEpisode detached = null;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        try {
            Query query = pm.newQuery(NnEpisode.class);
            query.setFilter("adId == adIdParam");
            query.declareParameters("long adIdParam");
            @SuppressWarnings("unchecked")
            List<NnEpisode> episodes = (List<NnEpisode>)query.execute(adId);
            if (episodes.size() > 0) {
                detached = (NnEpisode)pm.detachCopy(episodes.get(0));
            }
        } finally {
            pm.close();
        }
        return detached;
    }
}
