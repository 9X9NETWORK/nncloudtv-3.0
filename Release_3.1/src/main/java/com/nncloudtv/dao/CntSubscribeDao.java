package com.nncloudtv.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.CntSubscribe;

public class CntSubscribeDao {
	public CntSubscribe save(CntSubscribe log) {		
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			pm.makePersistent(log);
			log = pm.detachCopy(log);
		} finally {
			pm.close();
		}
		return log;
	}
		
	public int findTotalCountByChannel(long channelId) {		
		int totalCount = 0;
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query q = pm.newQuery(CntSubscribe.class);
			q.setFilter("channelId == channelIdParam");
			q.declareParameters("long channelIdParam");
			@SuppressWarnings("unchecked")
			List<CntSubscribe> subs = (List<CntSubscribe>)q.execute(channelId);
			for (CntSubscribe s : subs)
				totalCount += s.getCnt();
		} finally {
			pm.close();
		}
		return totalCount;
	}

	public int findTotalCountBySet(long setId) {		
		int totalCount = 0;
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query q = pm.newQuery(CntSubscribe.class);
			q.setFilter("setId == setIdParam");
			q.declareParameters("long setIdParam");
			@SuppressWarnings("unchecked")
			List<CntSubscribe> subs = (List<CntSubscribe>)q.execute(setId);
			for (CntSubscribe s : subs)
				totalCount += s.getCnt();
		} finally {
			pm.close();
		}
		return totalCount;
	}
	
	public CntSubscribe findByChannel(long channelId) {
		CntSubscribe s = null;
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query q = pm.newQuery(CntSubscribe.class);
			q.setFilter("channelId == channelIdParam");
			q.declareParameters("long channelIdParam");
			@SuppressWarnings("unchecked")
			List<CntSubscribe> subs = (List<CntSubscribe>)q.execute(channelId);
			if (subs.size() > 0) {
				s = subs.get(0);
				s = pm.detachCopy(s);
			}
		} finally {
			pm.close();
		}
		return s;		
	}		
	
}
