package com.nncloudtv.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.SubscriptionLog;

public class SubscriptionLogDao {
	public SubscriptionLog save(SubscriptionLog log) {		
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			pm.makePersistent(log);
			log = pm.detachCopy(log);
		} finally {
			pm.close();
		}
		return log;
	}
	
	public SubscriptionLog findByMsoIdAndChannelId(long msoId, long channelId) {
		SubscriptionLog s = null;
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query q = pm.newQuery(SubscriptionLog.class);
			q.setFilter("msoId == msoIdParam && channelId== channelIdParam");
			q.declareParameters("long msoIdParam, long channelIdParam");
			@SuppressWarnings("unchecked")
			List<SubscriptionLog> subs = (List<SubscriptionLog>)q.execute(msoId, channelId);
			if (subs.size() > 0) {
				s = subs.get(0);
				s = pm.detachCopy(s);
			}
		} finally {
			pm.close();
		}
		return s;		
	}
	
	public int findTotalCountByChannelId(long channelId) {
		
		int totalCount = 0;
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query q = pm.newQuery(SubscriptionLog.class);
			q.setFilter("channelId == channelIdParam");
			q.declareParameters("long channelIdParam");
			@SuppressWarnings("unchecked")
			List<SubscriptionLog> subs = (List<SubscriptionLog>)q.execute(channelId);
			for (SubscriptionLog s : subs)
				totalCount += s.getCount();
		} finally {
			pm.close();
		}
		return totalCount;
	}
	
	public SubscriptionLog findByChannelId(long channelId) {
		SubscriptionLog s = null;
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query q = pm.newQuery(SubscriptionLog.class);
			q.setFilter("channelId == channelIdParam");
			q.declareParameters("long channelIdParam");
			@SuppressWarnings("unchecked")
			List<SubscriptionLog> subs = (List<SubscriptionLog>)q.execute(channelId);
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
