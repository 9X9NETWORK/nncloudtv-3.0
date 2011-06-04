package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.Subscription;

//public class SubscriptionDao extends GenericDao<Subscription>{
public class SubscriptionDao{
	
	protected static final Logger log = Logger.getLogger(SubscriptionDao.class.getName());
	
	/*
	public SubscriptionDao() {
		super(Subscription.class);
	}
	*/

	private PersistenceManager getPersistenceManager(NnUser user) {
		if (user.getSharing() == 1) {
			return PMF.getNnUser1().getPersistenceManager();
		} else {
			return PMF.getNnUser2().getPersistenceManager();
		}
	}
	
	public Subscription save(NnUser user, Subscription sub) {
		if (sub == null) {return null;}
		PersistenceManager pm = this.getPersistenceManager(user);
		try {
			System.out.println("<<<<<<<<<<< make all my way here????? ");
			pm.makePersistent(sub);
			sub = pm.detachCopy(sub);
		} finally {
			pm.close();
		}
		return sub;
	}		

	public void saveAll(NnUser user, List<Subscription> subs) {
		Date now = new Date();
		for (Subscription s : subs) {
			s.setUpdateDate(now);			
		}
		PersistenceManager pm = this.getPersistenceManager(user);
		try {
			pm.makePersistentAll(subs);
		} finally {
			pm.close();
		}
	}
	
	public void delete(NnUser user, Subscription sub) {
		if (sub != null) {
			PersistenceManager pm = this.getPersistenceManager(user);
			try {
				pm.deletePersistent(sub);
			} finally {
				pm.close();
			}
		}
	}
	
	public Subscription findByUserAndSeq(NnUser user, int seq) {
		Subscription s = null;
		PersistenceManager pm = this.getPersistenceManager(user);
		try {
			Query q = pm.newQuery(Subscription.class);
			q.setFilter("userId == userIdParam && seq == seqParam");
			q.declareParameters("long userIdParam, int seqParam");
			q.setOrdering("seq asc");
			@SuppressWarnings("unchecked")
			List<Subscription> subs = (List<Subscription>)q.execute(user.getId(), seq);
			if (subs.size() > 0) {
				s = subs.get(0);
				s = pm.detachCopy(s);
			}
		} finally {
			pm.close();
		}
		return s;
	}	 
	
	public Subscription findByUserAndChannelId(NnUser user, long channelId) {
		PersistenceManager pm = this.getPersistenceManager(user);
		Subscription s = null;
		try {
			Query q = pm.newQuery(Subscription.class);
			q.setFilter("userId == userIdParam && channelId== channelIdParam");
			q.setOrdering("seq asc");
			q.declareParameters("long userIdParam, long channelIdParam");
			@SuppressWarnings("unchecked")
			List<Subscription> subs = (List<Subscription>)q.execute(user.getId(), channelId);
			if (subs.size() > 0) {
				s = subs.get(0);
				s = pm.detachCopy(s);
			}
		} finally {
			pm.close();
		}
		return s;
	}
	
	public List<Subscription> findAllByUser(NnUser user) {
		PersistenceManager pm = this.getPersistenceManager(user);
		List<Subscription> detached = new ArrayList<Subscription>();
		try {
			Query q = pm.newQuery(Subscription.class);
			q.setFilter("userId == userIdParam");
			q.declareParameters("long userIdParam");
			q.setOrdering("seq asc");
			@SuppressWarnings("unchecked")
			List<Subscription> subscriptions = (List<Subscription>)q.execute(user.getId());
			detached = (List<Subscription>)pm.detachCopyAll(subscriptions);
		} finally {
			pm.close();
		}
		return detached;
	}	 
		
	
}
