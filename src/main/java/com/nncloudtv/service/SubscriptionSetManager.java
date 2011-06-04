package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.SubscriptionSetDao;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.SubscriptionSet;

@Service
public class SubscriptionSetManager {
	
	protected static final Logger logger = Logger.getLogger(SubscriptionSetManager.class.getName());
	
	private SubscriptionSetDao subSetDao = new SubscriptionSetDao();
	
	public void create(NnUser user, SubscriptionSet subSet) {
		Date now = new Date();
		subSet.setCreateDate(now);
		subSet.setUpdateDate(now);
		subSetDao.save(user, subSet);
	}
	
	public SubscriptionSet save(NnUser user, SubscriptionSet subSet) {
		subSet.setUpdateDate(new Date());
		subSet = subSetDao.save(user, subSet);
		return subSet;
	}
	
	public List<SubscriptionSet> findByUser(NnUser user) {
		return subSetDao.findByUser(user);
	}
	
	public SubscriptionSet findByUserAndSeq(NnUser user, short seq) {
		return subSetDao.findByUserAndSeq(user, seq);
	}	
}
