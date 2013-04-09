package com.nncloudtv.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnSetDao;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSet;

@Service
public class NnSetManager {
    
    protected static final Logger log = Logger.getLogger(NnSetManager.class.getName());
    
    private NnSetDao dao = new NnSetDao();        
    
    public List<NnSet> findFeatured(String lang, long msoId) {
        return dao.findFeatured(lang, msoId);
    }

    public NnSet findById(long setId) {
        return dao.findById(setId);
    }    

    public NnSet findByName(String name, long msoId) {
        if (name == null)
            return null;
        return dao.findByName(name.trim(), msoId);
    }
    
    public List<NnChannel> findChannels(NnSet set, Mso mso) {
        if (set == null)
            return null;
        String stackName = TagManager.assembleStackName(set.getName(), set.getLang(), mso.getName());
        List<NnChannel> channels = new TagManager().findChannelsByTag(stackName, true);
        return channels;
    }    
}
