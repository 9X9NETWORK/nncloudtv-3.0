package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.SubscriptionGroupDao;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.SubscriptionGroup;

@Service
public class SubscriptionGroupManager {
	
	protected static final Logger logger = Logger.getLogger(SubscriptionGroupManager.class.getName());
	
	private SubscriptionGroupDao subGroupDao = new SubscriptionGroupDao();
	
	public void create(NnUser user, SubscriptionGroup subGroup) {
		Date now = new Date();
		subGroup.setCreateDate(now);
		subGroup.setUpdateDate(now);
		subGroupDao.save(user, subGroup);
	}
	
	public SubscriptionGroup save(NnUser user, SubscriptionGroup subGroup) {
		subGroup.setUpdateDate(new Date());
		subGroup = subGroupDao.save(user, subGroup);		
		return subGroup;
	}
	
	public List<SubscriptionGroup> findByUser(NnUser user) {
		return subGroupDao.findByUser(user);
	}
	
	public SubscriptionGroup findByUserAndSeq(NnUser user, short seq) {
		return subGroupDao.findByUserAndSeq(user, seq);
	}
	
	public SubscriptionGroup findByUserAndItemId(NnUser user, long itemId) {
		return subGroupDao.findByUserAndItemId(user, itemId);
	}	
	
}
