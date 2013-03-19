package com.nncloudtv.service;

import java.util.logging.Logger;

import com.nncloudtv.dao.NnUserProfileDao;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserProfile;

public class NnUserProfileManager {

    protected static final Logger log = Logger.getLogger(NnUserProfileManager.class.getName());
    
    private NnUserProfileDao dao = new NnUserProfileDao();
    
    public NnUserProfile findByUser(NnUser user) {
        if (user == null)
            return null;
        return dao.findByUser(user);
    }
    
    public NnUserProfile save(NnUser user, NnUserProfile profile) {
        if (profile == null)
            return null;
        return dao.save(user, profile);
    }
    
}
