package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.SysTagDisplayDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTagDisplay;

@Service
public class SysTagDisplayManager {
    
    protected static final Logger log = Logger.getLogger(SysTagDisplayManager.class.getName());
    
    private SysTagDisplayDao dao = new SysTagDisplayDao();
    
    public List<SysTagDisplay> findPlayerCategories(String lang, long msoId) {
        return dao.findPlayerCategories(lang, msoId);       
    }

    public List<SysTagDisplay> findRecommendedSets(String lang, long msoId) {
        List<SysTagDisplay> sets = dao.findRecommendedSets(lang, msoId);
        log.info("recommended size:" + sets.size());        
        return sets;
    }

    public List<SysTagDisplay> findDayparting(short baseTime, String lang, long msoId) {
        List<SysTagDisplay> sets = dao.findDayparting(baseTime, lang, msoId);
        log.info("dayparting size:" + sets.size());
        return sets;
    } 

    public List<SysTagDisplay> findFrontpage(long msoId, short type, String lang) {
        List<SysTagDisplay> sets = dao.findFrontpage(msoId, type, lang);
        log.info("frontpage size:" + sets.size());
        return sets;
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
    
    public void addChannelCounter(NnChannel channel) {
        
    }

    /** used by SET */
    public void plusCntChannel(Long sysTagId, int num) {
        
        if (sysTagId == null || num < 1) {
            return ;
        }
        
        SysTagDisplay sysTagDisplay = findBySysTagId(sysTagId);
        if (sysTagDisplay == null) {
            return ;
        }
        
        int updateCntChannel = sysTagDisplay.getCntChannel() + num;
        sysTagDisplay.setCntChannel(updateCntChannel);
        
        dao.save(sysTagDisplay);
    }
    
    /** used by SET */
    public void minusCntChannel(Long sysTagId, int num) {
        
        if (sysTagId == null || num < 1) {
            return ;
        }
        
        SysTagDisplay sysTagDisplay = findBySysTagId(sysTagId);
        if (sysTagDisplay == null) {
            return ;
        }
        
        int updateCntChannel = sysTagDisplay.getCntChannel() - num;
        if (updateCntChannel < 0) {
            sysTagDisplay.setCntChannel(0);
        } else {
            sysTagDisplay.setCntChannel(updateCntChannel);
        }
        
        dao.save(sysTagDisplay);
    }

}
