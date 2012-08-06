package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnSetAutosharing;

public class NnSetAutosharingDao extends GenericDao<NnSetAutosharing> {
	
	public NnSetAutosharingDao() {
		super(NnSetAutosharing.class);
	}
	
	public List<NnSetAutosharing> findBySet(long setId) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnSetAutosharing> results = new ArrayList<NnSetAutosharing>();
		
		try {
			Query query = pm.newQuery(NnSetAutosharing.class);
			query.setFilter("setId == setIdParam");
			query.declareParameters("long setIdParam");
			@SuppressWarnings("unchecked")
			List<NnSetAutosharing> list = (List<NnSetAutosharing>) query.execute(setId);
			if (list.size() > 0)
				results = (List<NnSetAutosharing>) pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return results;
	}
	
	public List<NnSetAutosharing> findBySetAndMso(long setId, long msoId) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnSetAutosharing> results = new ArrayList<NnSetAutosharing>();
		
		try {
			Query query = pm.newQuery(NnSetAutosharing.class);
			query.setFilter("setId == setIdParam && msoId == msoIdParam");
			query.declareParameters("long setIdParam, long msoIdParam");
			@SuppressWarnings("unchecked")
			List<NnSetAutosharing> list = (List<NnSetAutosharing>) query.execute(setId, msoId);
			if (list.size() > 0)
				results = (List<NnSetAutosharing>) pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return results;
	}
	
	public NnSetAutosharing findSetAutosharing(long msoId, long setId, short type) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnSetAutosharing result = null;
		
		try {
			Query query = pm.newQuery(NnSetAutosharing.class);
			query.setFilter("msoId == msoIdParam && setId == setIdParam && type == typeParam");
			query.declareParameters("long msoIdParam, long setIdParam, short typeParam");
			@SuppressWarnings("unchecked")
			List<NnSetAutosharing> list = (List<NnSetAutosharing>) query.execute(msoId, setId, type);
			if (list.size() > 0)
				result = pm.detachCopy(list.get(0));
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		
		return result;
	}
	
}
