package com.nncloudtv.service;

import java.util.Date;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnAdDao;
import com.nncloudtv.model.NnAd;
import com.nncloudtv.model.NnEpisode;

public class NnAdManager {
    
    protected static final Logger log = Logger.getLogger(NnAdManager.class.getName());
    
    private NnAdDao dao = new NnAdDao();
    
    public NnAd findByEpisode(NnEpisode episode) {
        
        if (episode == null) {
            return null;
        }
        
        long adId = episode.getAdId();
        
        if (adId == 0) {
            return null;
        }
        
        return dao.findById(adId);
        
    }
    
    public void delete(NnAd nnad) {
        
        if (nnad == null) {
            return;
        }
        
        dao.delete(nnad);
    }
    
    public NnAd save(NnAd nnad, NnEpisode episode) {
        
        if (nnad == null || episode == null) {
            return null;
        }
        
        Date now = new Date();
        
        if (nnad.getCreateDate() == null) {
            nnad.setCreateDate(now);
        }
        
        nnad.setUpdateDate(now);
        nnad = dao.save(nnad);
        
        NnAd origAd = findByEpisode(episode);
        if (origAd != null && origAd.getId() != nnad.getId()) {
            
            delete(origAd);
        }
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        episode.setAdId(nnad.getId());
        episodeMngr.save(episode);
        
        return nnad;
    }
    
}
