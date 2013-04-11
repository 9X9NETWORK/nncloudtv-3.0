package com.nncloudtv.service;

import java.util.List;

import com.nncloudtv.dao.SysTagDisplayDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTagDisplay;

public class SysTagDisplayManager {
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

    public SysTagDisplay findByName(String name, long msoId) {
        return dao.findByName(name);
    }
    
    public List<NnChannel> findChannelsById(long displayId) {
        List<NnChannel> channels = dao.findChannelsById(displayId);
        return channels;
    }

}
