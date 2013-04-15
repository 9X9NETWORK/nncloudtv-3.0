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
        return dao.findRecommendedSets(lang, msoId);
    }
    
    public SysTagDisplay findById(long id) {
        return dao.findById(id);
    }
    
    public SysTagDisplay findBySysTagId(Long sysTagId) {
        if (sysTagId == null) {
            return null;
        }
        return dao.findBySysTagId(sysTagId);
    }

    public SysTagDisplay findByName(String name, long msoId) {
        return dao.findByName(name);
    }
    
    public List<NnChannel> findChannelsById(long displayId) {
        List<NnChannel> channels = dao.findChannelsById(displayId);
        return channels;
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

}
