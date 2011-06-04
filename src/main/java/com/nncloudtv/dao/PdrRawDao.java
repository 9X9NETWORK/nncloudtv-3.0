package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.PdrRaw;

public class PdrRawDao {

	public PdrRaw save(PdrRaw pdr) {
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			pm.makePersistent(pdr);
			pdr = pm.detachCopy(pdr);
		} finally {
			pm.close();
		}
		return pdr;
	}
	
	public List<PdrRaw> findByUserId(long userId) {
		List<PdrRaw> detached = new ArrayList<PdrRaw>();
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query q = pm.newQuery(PdrRaw.class);
			q.setFilter("userId == userIdParam");
			q.declareParameters("long userIdParam");
			@SuppressWarnings("unchecked")
			List<PdrRaw> results = (List<PdrRaw>)q.execute(userId);
			detached = (List<PdrRaw>)pm.detachCopyAll(results);
		} finally {
			pm.close();
		}
		return detached;
		
	}

}
