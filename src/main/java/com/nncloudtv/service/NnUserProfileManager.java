package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnUserProfileDao;
import com.nncloudtv.model.Mso;
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
    
    public List<NnUserProfile> findByUserId(Long userId) {
        
        if (userId == null) {
            return new ArrayList<NnUserProfile>();
        }
        
        List<NnUserProfile> results = dao.findByUserId(userId);
        if (results == null) {
            return new ArrayList<NnUserProfile>();
        }
        return results;
    }
    
    public NnUserProfile save(NnUser user, NnUserProfile profile) {
        if (profile == null)
            return null;
        return dao.save(user, profile);
    }
    
    public Set<NnUserProfile> search(String keyword, int start, int limit) {
        
        return dao.search(keyword, start, limit);
        
    }
    
    /** return if this user has super priv to access PCS */
    public NnUserProfile pickSuperProfile(Long userId) {
        
        if (userId == null) {
            return null;
        }
        
        NnUserProfile target = null;
        List<NnUserProfile> profiles = findByUserId(userId);
        if (profiles == null || profiles.size() == 0) {
            return null;
        } else {
            for (NnUserProfile profile : profiles) {
                if (profile.getPriv() != null && profile.getPriv().startsWith("111")) { // logic hard coded
                    if (target == null) {
                        target = profile;
                    } else {
                        // multiple assigned 
                        target = profile;
                        log.warning("this userId : " + userId + " has multiple super profile and this func cant choose approriate one");
                    }
                }
            }
        }
        
        return target;
    }
    
}
