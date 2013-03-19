package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnUserChannelSortingDao;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserChannelSorting;

public class NnUserChannelSortingManager {

    protected static final Logger log = Logger.getLogger(NnUserChannelSortingManager.class.getName());
    
    private NnUserChannelSortingDao sortingDao = new NnUserChannelSortingDao();    
        
    public NnUserChannelSorting save(NnUser user, NnUserChannelSorting sorting) {
        NnUserChannelSorting existed = this.findByUserAndChannel(user, sorting.getChannelId());
        Date now = new Date();
        if (existed == null) {
            existed = sorting;
            existed.setCreateDate(now);
        } else {
            existed.setSort(sorting.getSort());
        }
        existed.setUpdateDate(now);
        return sortingDao.save(user, existed);        
    }        
    
    public NnUserChannelSorting findByUserAndChannel(NnUser user, long channelId) {        
        return sortingDao.findByUserAndChannel(user, channelId);
    }
    
    public List<NnUserChannelSorting> findByUser(NnUser user) {        
        return sortingDao.findByUser(user);
    }
    
}
