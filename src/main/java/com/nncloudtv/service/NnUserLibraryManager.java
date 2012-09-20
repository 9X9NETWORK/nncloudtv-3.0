package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnUserLibraryDao;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserLibrary;

@Service
public class NnUserLibraryManager {
    
    protected static final Logger log = Logger.getLogger(NnUserLibraryManager.class.getName());
    
    private NnUserLibraryDao libDao = new NnUserLibraryDao();
    
    public NnUserLibrary save(NnUserLibrary lib) {
        if (lib == null) {return null;}
        Date now = new Date();
        lib.setUpdateDate(now);        
        return libDao.save(lib);
    }
    
    public NnUserLibrary findById(Long id) {
        return libDao.findById(id);
    }
    
    // sorted by updateDate
    public List<NnUserLibrary> findByUserAndType(NnUser user, Short type) {
        return libDao.findByUserIdStrAndType(user.getIdStr(), type);
    }
    
    public void delete(NnUserLibrary lib) {
        libDao.delete(lib);
    }
    
    public NnUserLibrary findByUserAndTypeAndFileUrl(NnUser user, short type,
            String fileUrl) {        
        return libDao.findByUserIdStrAndTypeAndFileUrl(user.getIdStr(), type, fileUrl);
        
    }
    
    public List<NnUserLibrary> list(long page, long rows, String sidx, String sord, String filter) {
        return libDao.list(page, rows, sidx, sord, filter);
    }
}
