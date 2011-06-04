package com.nncloudtv.dao;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Ipg;

public class IpgDao {

	protected static final Logger log = Logger.getLogger(IpgDao.class.getName());
		
	public Ipg save(Ipg ipg) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			pm.makePersistent(ipg);
			ipg = pm.detachCopy(ipg);
		} finally {
			pm.close();
		}
		return ipg;
	}	
	
	public Ipg findById(Long id) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		Ipg ipg = null, detached = null;
		try {
			ipg = (Ipg)pm.getObjectById(Ipg.class, id);
			DateFormat df = DateFormat.getDateInstance();
			log.info("ipg channel count: " + ipg.getChannels().size() + ";owner: " + ipg.getUserId() + ";ipg date: " + df.format(ipg.getCreateDate())); 
			detached = (Ipg)pm.detachCopy(ipg);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();			
		}
		return detached;
	}	
	
	public List<Ipg> findByUserId(long userId) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<Ipg> detached = new ArrayList<Ipg>();
		try {
			Query query = pm.newQuery(Ipg.class);
			query.setFilter("userId == userIdParam");
			query.declareParameters("long userIdParam");
			@SuppressWarnings("unchecked")
			List<Ipg> results = (List<Ipg>)query.execute(userId);
			log.info("ipg count = " + results.size());
			detached = (List<Ipg>)pm.detachCopyAll(results);
		} finally {
			pm.close();
		}
		return detached;
	}	
		
}
