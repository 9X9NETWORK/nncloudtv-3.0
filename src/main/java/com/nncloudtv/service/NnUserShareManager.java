package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnUserShareDao;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserShare;

public class NnUserShareManager {

	protected static final Logger log = Logger.getLogger(NnUserShareManager.class.getName());
	
	private NnUserShareDao shareDao = new NnUserShareDao();	
	
	public void create(NnUserShare share) {
		Date now = new Date();
		share.setUpdateDate(now);
		share.setCreateDate(now);
		shareDao.save(share);
	}	
	
	public NnUserShare save(NnUserShare share) {
		return shareDao.save(share);
	}	
	
	public NnUserShare findById(Long id) {
		return shareDao.findById(id);
	}
	
	public List<NnUserShare> findByUser(NnUser user) {
		return shareDao.findByUser(user);
	}
			
}