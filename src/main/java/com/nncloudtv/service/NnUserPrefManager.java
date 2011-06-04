package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnUserPrefDao;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserPref;

@Service
public class NnUserPrefManager {
	
	protected static final Logger log = Logger.getLogger(NnUserPrefManager.class.getName());
		
	private NnUserPrefDao nnUserPrefDao = new NnUserPrefDao();
	
	public void create(NnUser user, NnUserPref pref) { 
		pref.setCreateDate(new Date());
		nnUserPrefDao.save(user, pref);
	}

	public NnUserPref save(NnUser user, NnUserPref pref) {
		pref.setUpdateDate(new Date());
		return nnUserPrefDao.save(user, pref);
	}

	public List<NnUserPref> findByUser(NnUser user) {
		return nnUserPrefDao.findByUser(user);
	}

	public NnUserPref findByUserAndItem(NnUser user, String item) {		
		return nnUserPrefDao.findByUserAndItem(user, item);
	}
	
}
