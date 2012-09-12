package com.nncloudtv.service;

import java.util.Date;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnAdDao;
import com.nncloudtv.model.NnAd;

public class NnAdManager {
    
    protected static final Logger log = Logger.getLogger(NnAdManager.class.getName());
    
    private NnAdDao dao = new NnAdDao();
    
    public NnAd findByProgramId(long programId) {
        return dao.findByProgramId(programId);
    }
    
    public void delete(NnAd nnad) {
        dao.delete(nnad);
    }
    
    public NnAd save(NnAd nnad) {
        
        Date now = new Date();
        
        if (nnad.getCreateDate() == null) {
            nnad.setCreateDate(now);
        }
        nnad.setUpdateDate(now);
        
        return dao.save(nnad);
    }
    
}
