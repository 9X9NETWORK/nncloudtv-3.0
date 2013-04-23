package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.PoiCampaignDao;
import com.nncloudtv.dao.PoiDao;
import com.nncloudtv.model.Poi;
import com.nncloudtv.model.PoiCampaign;

@Service
public class PoiCampaignManager {
    
    protected static final Logger log = Logger.getLogger(PoiCampaignManager.class.getName());
    
    private PoiCampaignDao poiCampaignDao = new PoiCampaignDao();
    private PoiDao poiDao = new PoiDao();
    
    public List<PoiCampaign> findByUserId(Long userId) {
        
        if (userId == null) {
            return new ArrayList<PoiCampaign>();
        }
        
        List<PoiCampaign> results = poiCampaignDao.findByUserId(userId);
        if (results == null) {
            return new ArrayList<PoiCampaign>();
        }
        
        return results;
    }
    
    public PoiCampaign save(PoiCampaign campaign) {
        
        if (campaign == null) {
            return null;
        }
        
        Date now = new Date();
        if (campaign.getCreateDate() == null) {
            campaign.setCreateDate(now);
        }
        campaign.setUpdateDate(now);
        
        PoiCampaign result = poiCampaignDao.save(campaign);
        
        return result;
    }
    
    public List<Poi> findPoisByCampaignId(Long campaignId) {
        
        if (campaignId == null) {
            return new ArrayList<Poi>();
        }
        
        return new ArrayList<Poi>();
    }
    
    /** temporary function for spring-1 */
    public List<Poi> findPoisByPointId(Long pointId) { // TODO : rewrite when AD's cms is ready
        
        if (pointId == null) {
            return new ArrayList<Poi>();
        }
        
        List<Poi> results = poiDao.findByPointId(pointId);
        if (results == null) {
            return new ArrayList<Poi>();
        }
        
        return results;
    }
    
    public PoiCampaign findCompaignById(Long compaignId) {
        if (compaignId == null) {
            return null;
        }
        return poiCampaignDao.findById(compaignId);
    }

}
