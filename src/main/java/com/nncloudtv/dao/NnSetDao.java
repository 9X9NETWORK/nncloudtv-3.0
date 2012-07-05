package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnSet;

public class NnSetDao extends GenericDao<NnSet> {
	
	protected static final Logger log = Logger.getLogger(NnSetDao.class.getName());
	
	public NnSetDao() {
		super(NnSet.class);
	}

	public List<NnSet> findByLang(String lang) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnSet> detached = new ArrayList<NnSet>(); 
		try {
			Query q = pm.newQuery(NnSet.class);
			q.setFilter("lang == langParam");
			q.declareParameters("String langParam");
			q.setOrdering("seq asc");
			@SuppressWarnings("unchecked")
			List<NnSet> sets = (List<NnSet>) q.execute(lang);
			detached = (List<NnSet>)pm.detachCopyAll(sets);
		} finally {
			pm.close();
		}
		return detached;
	}
	
	public NnSet findByLangAndSeq(String lang, short seq) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnSet detached = null;
		
		try {
			Query query = pm.newQuery(NnSet.class);
			query.setFilter("lang == langParam && seq == seqParam");
			query.declareParameters("String langParam, int seqParam");
			@SuppressWarnings("unchecked")
			List<NnSet> channelSets = (List<NnSet>) query.execute(lang, seq);
			if (channelSets.size() > 0) {
				detached = pm.detachCopy(channelSets.get(0));
			}
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		
		return detached;		
	}
	
	public NnSet findById(long id) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnSet cs = null;
		try {
			cs = pm.getObjectById(NnSet.class, id);
			//pm.refresh(cs);
			cs = pm.detachCopy(cs);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();			
		}
		return cs;
	}

	public List<NnSet> findFeaturedSets(String lang) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnSet> detached = new ArrayList<NnSet>(); 
		try {
			Query q = pm.newQuery(NnSet.class);
			q.setFilter("featured == featuredParam && lang == langParam");
			q.declareParameters("boolean featuredParam, String langParam");
			q.setOrdering("seq asc");
			@SuppressWarnings("unchecked")
			List<NnSet> sets = (List<NnSet>) q.execute(true, lang);
			detached = (List<NnSet>)pm.detachCopyAll(sets);
		} finally {
			pm.close();
		}
		return detached;
	}	
	
	public NnSet findByBeautifulUrl(String url) {
		if (url == null)
			return null;
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnSet detached = null;
		
		try {
			Query query = pm.newQuery(NnSet.class);
			query.setFilter("beautifulUrl.toLowerCase() == beautifulUrlParam.toLowerCase()");
			query.declareParameters("String beautifulUrlParam");
			@SuppressWarnings("unchecked")
			List<NnSet> sets = (List<NnSet>) query.execute(url);
			if (sets.size() > 0) {
				detached = pm.detachCopy(sets.get(0));
			}
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		
		return detached;
	}
	
	//!!!lower case
	public NnSet findByName(String name) {		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnSet detached = null;		
		try {
			Query query = pm.newQuery(NnSet.class);
			query.setFilter("name == nameParam");
			query.declareParameters("String nameParam");			
			@SuppressWarnings("unchecked")
			List<NnSet> channelNnSets = (List<NnSet>) query.execute(name);
			if (channelNnSets.size() > 0) {
				detached = pm.detachCopy(channelNnSets.get(0));
			}
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		
		return detached;
	}
}
