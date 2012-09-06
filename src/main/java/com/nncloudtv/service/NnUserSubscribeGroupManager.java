package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnUserSubscribeGroupDao;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserSubscribeGroup;

@Service
public class NnUserSubscribeGroupManager {
    
    protected static final Logger log = Logger.getLogger(NnUserSubscribeGroupManager.class.getName());
    
    private NnUserSubscribeGroupDao groupDao = new NnUserSubscribeGroupDao();
    
    public void create(NnUser user, NnUserSubscribeGroup group) {
        Date now = new Date();
        group.setCreateDate(now);
        group.setUpdateDate(now);
        groupDao.save(user, group);
    }
    
    public NnUserSubscribeGroup save(NnUser user, NnUserSubscribeGroup group) {
        group.setUpdateDate(new Date());
        group = groupDao.save(user, group);        
        return group;
    }
    
    public List<NnUserSubscribeGroup> findByUser(NnUser user) {
        return groupDao.findByUser(user);
    }
    
    public NnUserSubscribeGroup findByUserAndSeq(NnUser user, short seq) {
        return groupDao.findByUserAndSeq(user, seq);
    }
    
    public void delete(NnUser user, NnUserSubscribeGroup group) {
        groupDao.delete(user, group);
    }
    
}
