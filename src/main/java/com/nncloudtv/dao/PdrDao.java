package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnDevice;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.Pdr;

public class PdrDao {

	public Pdr save(Pdr pdr) {
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			pm.makePersistent(pdr);
			pdr = pm.detachCopy(pdr);
		} finally {
			pm.close();
		}
		return pdr;
	}
	
	public List<Pdr> findByUserId(long userId) {
		List<Pdr> detached = new ArrayList<Pdr>();
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query q = pm.newQuery(Pdr.class);
			q.setFilter("userId == userIdParam");
			q.declareParameters("long userIdParam");
			@SuppressWarnings("unchecked")
			List<Pdr> results = (List<Pdr>)q.execute(userId);
			detached = (List<Pdr>)pm.detachCopyAll(results);
		} finally {
			pm.close();
		}
		return detached;
		
	}

	@SuppressWarnings("unchecked")
	public List<Pdr> findDebugging(
			NnUser user, NnDevice device, String session,
			String ip, Date since) {
		List<Pdr> detached = new ArrayList<Pdr>();
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		List<Pdr> results = new ArrayList<Pdr>();
		try {
			Query q = pm.newQuery(Pdr.class);
			if (user != null && session != null) {
				q.setFilter("userId == userIdParam && session == sessionParam");
				q.declareParameters("long userIdParam, String sessionParam");
				results = (List<Pdr>)q.execute(user.getId(), session);
			} else if (device != null && session != null) {
				q.setFilter("deviceToken == deviceIdParam && session == sessionParam");
				q.declareParameters("long userIdParam, String sessionParam");
				results = (List<Pdr>)q.execute(device.getId(), session);
			} else if (user != null) {
				q.setFilter("userId == userIdParam");
				q.declareParameters("long userIdParam");
				results = (List<Pdr>)q.execute(user.getId());				
			} else if (device != null) {
				q.setFilter("deviceId == deviceIdParam");
				q.declareParameters("long deviceIdParam");
				results = (List<Pdr>)q.execute(device.getId());				
			} else if (ip != null) {
				q.declareImports("import java.util.Date");
				q.setFilter("ip == ipParam && createDate > createDateParam");
				q.declareParameters("String ipParam, Date createDateParam");
				results = (List<Pdr>)q.execute(ip, since);
			}
			q.setRange(0,199);
			detached = (List<Pdr>)pm.detachCopyAll(results);
		} finally {
			pm.close();
		}
		return detached;		
	}
	
}
