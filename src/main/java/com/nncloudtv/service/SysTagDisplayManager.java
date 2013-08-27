package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.SysTagDisplayDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;

@Service
public class SysTagDisplayManager {
    
    protected static final Logger log = Logger.getLogger(SysTagDisplayManager.class.getName());
    
    private SysTagDisplayDao dao = new SysTagDisplayDao();
    
    public List<SysTagDisplay> findPlayerCategories(String lang, long msoId) {
        return dao.findPlayerCategories(lang, msoId);       
    }

    public List<SysTagDisplay> findPlayerCategoriesAll(String lang, long msoId) {
        List<SysTagDisplay> categories = new ArrayList<SysTagDisplay>();
        if (msoId != 1) {        	
        	categories.addAll(this.findPlayerCategories(lang, msoId));
        }
        categories.addAll(this.findPlayerCategories(lang, 1));
        return dao.findPlayerCategories(lang, msoId);       
    }

    public List<SysTagDisplay> findRecommendedSets(String lang, long msoId) {
        List<SysTagDisplay> sets = dao.findRecommendedSets(lang, msoId, SysTag.TYPE_SET);
        log.info("recommended size:" + sets.size());        
        return sets;
    }

    public List<SysTagDisplay> find33RecommendedSets(String lang, long msoId) {
        List<SysTagDisplay> sets = dao.findRecommendedSets(lang, msoId, SysTag.TYPE_33SET);
        log.info("33 recommended size:" + sets.size());        
        return sets;
    }

    public SysTagDisplay findDayparting(short baseTime, String lang, long msoId) {
        List<SysTagDisplay> sets = dao.findDayparting(baseTime, lang, msoId);
        if (sets.size() > 0)
            return sets.get(0);            
        return null;
    } 

    public SysTagDisplay findFrontpage(long msoId, short type, String lang) {
        List<SysTagDisplay> sets = dao.findFrontpage(msoId, type, lang);
        if (sets.size() > 0)
            return sets.get(0);            
        return null;
    } 

    public SysTagDisplay findPrevious(long msoId, String lang, SysTagDisplay dayparting) {
        List<SysTagDisplay> display = dao.findFrontpage(msoId, SysTag.TYPE_PREVIOUS, lang);
        if (display.size() > 0) {
            SysTag systag = new SysTagManager().findById(dayparting.getSystagId());
            if (systag != null ) { 
                long systagId = Long.parseLong(systag.getAttr());
                display.get(0).setSystagId(systagId);
                SysTagDisplay previousDisplay = this.findBySysTagIdAndLang(systagId, lang);                
                display.get(0).setImageUrl(previousDisplay.getImageUrl());
                log.info("previous systag id set:" + systagId);
            }
            log.info("previous systag id:" + display.get(0).getSystagId());
            return display.get(0);
        }
        return null;
    } 
    
    public SysTagDisplay findById(long id) {
        return dao.findById(id);
    }
    
    public SysTagDisplay findBySysTagIdAndLang(Long sysTagId, String lang) {
        if (sysTagId == null) {
            return null;
        }
        return dao.findBySysTagIdAndLang(sysTagId, lang);
    }
    
    /** if multiple display, only pick one, for now using in Set */
    public SysTagDisplay findBySysTagId(Long sysTagId) {
        if (sysTagId == null) {
            return null;
        }
        return dao.findBySysTagId(sysTagId);
    }
    
    public List<SysTagDisplay> findAllBySysTagId(Long sysTagId) {
        if (sysTagId == null) {
            return new ArrayList<SysTagDisplay>();
        }
        return dao.findAllBySysTagId(sysTagId);
    }

    public SysTagDisplay findByName(String name, long msoId) {
        return dao.findByName(name);
    }
        
    public SysTagDisplay save(SysTagDisplay sysTagDisplay) {
        
        if (sysTagDisplay == null) {
            return null;
        }
        
        Date now = new Date();
        sysTagDisplay.setUpdateDate(now);
        
        sysTagDisplay = dao.save(sysTagDisplay);
        
        return sysTagDisplay;
    }
    
    public void delete(SysTagDisplay sysTagDisplay) {
        if (sysTagDisplay == null) {
            return ;
        }
        dao.delete(sysTagDisplay);
    }
    
    public void deleteAll(List<SysTagDisplay> sysTagDisplays) {
        if (sysTagDisplays == null || sysTagDisplays.size() < 1) {
            return ;
        }
        dao.deleteAll(sysTagDisplays);
    }
    
    public void addChannelCounter(NnChannel channel) {
        
    }

}
