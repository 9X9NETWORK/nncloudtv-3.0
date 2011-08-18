package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.SubscriptionGroup;

public class SubscriptionGroupDao extends GenericDao<SubscriptionGroup> {
	
	protected static final Logger logger = Logger.getLogger(SubscriptionGroupDao.class.getName());
	
	public SubscriptionGroupDao() {
		super(SubscriptionGroup.class);
	}
	
	private PersistenceManager getPersistenceManager(NnUser user) {
		if (user.getShard() == 1) {
			return PMF.getNnUser1().getPersistenceManager();
		} else {
			return PMF.getNnUser2().getPersistenceManager();
		}
	}	

	public SubscriptionGroup save(NnUser user, SubscriptionGroup subSet) {
		if (subSet == null) {return null;}
		PersistenceManager pm = this.getPersistenceManager(user);
		try {
			pm.makePersistent(subSet);
			subSet = pm.detachCopy(subSet);
		} finally {
			pm.close();
		}
		return subSet;
	}	
	
	public List<SubscriptionGroup> findByUser(NnUser user) {		
		PersistenceManager pm = this.getPersistenceManager(user);
		ArrayList<SubscriptionGroup> detached = new ArrayList<SubscriptionGroup>();		
		try {
			Query query = pm.newQuery(SubscriptionGroup.class);
			query.setFilter("userId == userIdParam");
			query.declareParameters("long userIdParam");
			@SuppressWarnings("unchecked")
			List<SubscriptionGroup> tmp = (List<SubscriptionGroup>)query.execute(user.getId());
			detached = (ArrayList<SubscriptionGroup>) pm.detachCopyAll(tmp);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return detached;
	}

	public SubscriptionGroup findByUserAndSeq(NnUser user, short seq) {		
		PersistenceManager pm = this.getPersistenceManager(user);
		SubscriptionGroup detached = null;		
		try {
			Query query = pm.newQuery(SubscriptionGroup.class);
			query.setFilter("userId == userIdParam && seq == seqParam");
			query.declareParameters("long userIdParam, short seqParam");
			@SuppressWarnings("unchecked")
			List<SubscriptionGroup> result = (List<SubscriptionGroup>)query.execute(user.getId(), seq);
			if (result.size() > 0) {
				detached = pm.detachCopy(result.get(0));
			} 
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return detached;
	}

	public SubscriptionGroup findByUserAndItemId(NnUser user, long itemId) {		
		PersistenceManager pm = this.getPersistenceManager(user);
		SubscriptionGroup detached = null;		
		try {
			Query query = pm.newQuery(SubscriptionGroup.class);
			query.setFilter("userId == userIdParam && itemId == itemIdParam");
			query.declareParameters("long userIdParam, long itemIdParam");
			@SuppressWarnings("unchecked")
			List<SubscriptionGroup> result = (List<SubscriptionGroup>)query.execute(user.getId(), itemId);
			if (result.size() > 0) {
				detached = pm.detachCopy(result.get(0));
			} 
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return detached;
	}
	
}
