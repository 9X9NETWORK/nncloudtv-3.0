package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.SubscriptionSet;

public class SubscriptionSetDao extends GenericDao<SubscriptionSet> {
	
	protected static final Logger logger = Logger.getLogger(SubscriptionSetDao.class.getName());
	
	public SubscriptionSetDao() {
		super(SubscriptionSet.class);
	}
	
	private PersistenceManager getPersistenceManager(NnUser user) {
		if (user.getSharing() == 1) {
			return PMF.getNnUser1().getPersistenceManager();
		} else {
			return PMF.getNnUser2().getPersistenceManager();
		}
	}	

	public SubscriptionSet save(NnUser user, SubscriptionSet subSet) {
		if (subSet == null) {return null;}
		PersistenceManager pm = this.getPersistenceManager(user);
		try {
			System.out.println("<<<<<<<<<< make to here >>>>>>>>>>>>>");
			pm.makePersistent(subSet);
			subSet = pm.detachCopy(subSet);
		} finally {
			pm.close();
		}
		return subSet;
	}	
	
	public List<SubscriptionSet> findByUser(NnUser user) {		
		PersistenceManager pm = this.getPersistenceManager(user);
		ArrayList<SubscriptionSet> detached = new ArrayList<SubscriptionSet>();		
		try {
			Query query = pm.newQuery(SubscriptionSet.class);
			query.setFilter("userId == userIdParam");
			query.declareParameters("long userIdParam");
			@SuppressWarnings("unchecked")
			List<SubscriptionSet> tmp = (List<SubscriptionSet>)query.execute(user.getId());
			detached = (ArrayList<SubscriptionSet>) pm.detachCopyAll(tmp);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return detached;
	}

	public SubscriptionSet findByUserAndSeq(NnUser user, short seq) {		
		PersistenceManager pm = this.getPersistenceManager(user);
		SubscriptionSet detached = null;		
		try {
			Query query = pm.newQuery(SubscriptionSet.class);
			query.setFilter("userId == userIdParam && seq == seqParam");
			query.declareParameters("long userIdParam, short seqParam");
			@SuppressWarnings("unchecked")
			List<SubscriptionSet> result = (List<SubscriptionSet>)query.execute(user.getId(), seq);
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
