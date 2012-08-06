package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnChannelAutosharing;

public class NnChannelAutosharingDao extends GenericDao<NnChannelAutosharing> {
	
	public NnChannelAutosharingDao() {
		super(NnChannelAutosharing.class);
	}
	
	public boolean isChannelAutosharedByMso(long msoId, long channelId, short type) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		boolean result = false;
		
		try {
			Query query = pm.newQuery(NnChannelAutosharing.class);
			query.setFilter("msoId == msoIdParam");
			query.setFilter("channelId == channelIdParam");
			query.setFilter("type == typeParam");
			query.declareParameters("long msoIdParam");
			query.declareParameters("long channelIdParam");
			query.declareParameters("short typeParam");
			@SuppressWarnings("unchecked")
			List<NnChannelAutosharing> list = (List<NnChannelAutosharing>) query.execute(msoId, channelId, type);
			if (list.size() > 0)
				result = true;
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		
		return result;
	}
	
	public List<NnChannelAutosharing> findByChannel(long channelId) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnChannelAutosharing> results = new ArrayList<NnChannelAutosharing>();
		
		try {
			Query query = pm.newQuery(NnChannelAutosharing.class);
			query.setFilter("channelId == channelIdParam");
			query.declareParameters("long channelIdParam");
			@SuppressWarnings("unchecked")
			List<NnChannelAutosharing> list = (List<NnChannelAutosharing>) query.execute(channelId);
			if (list.size() > 0)
				results = (List<NnChannelAutosharing>) pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return results;
	}
	
	public List<NnChannelAutosharing> findByChannelAndMso(long channelId, long msoId) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnChannelAutosharing> results = new ArrayList<NnChannelAutosharing>();
		
		try {
			Query query = pm.newQuery(NnChannelAutosharing.class);
			query.setFilter("channelId == channelIdParam && msoId == msoIdParam");
			query.declareParameters("long channelIdParam, long msoIdParam");
			@SuppressWarnings("unchecked")
			List<NnChannelAutosharing> list = (List<NnChannelAutosharing>) query.execute(channelId, msoId);
			if (list.size() > 0)
				results = (List<NnChannelAutosharing>) pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return results;
	}

	public NnChannelAutosharing findChannelAutosharing(long msoId, long channelId, short type) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnChannelAutosharing result = null;
		
		try {
			Query query = pm.newQuery(NnChannelAutosharing.class);
			query.setFilter("msoId == msoIdParam && channelId == channelIdParam && type == typeParam");
			query.declareParameters("long msoIdParam, long channelIdParam, short typeParam");
			@SuppressWarnings("unchecked")
			List<NnChannelAutosharing> list = (List<NnChannelAutosharing>) query.execute(msoId, channelId, type);
			if (list.size() > 0)
				result = pm.detachCopy(list.get(0));
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		
		return result;
	}
	
	public List<NnChannelAutosharing> findByChannelAndType(long channelId, short type) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnChannelAutosharing> results = new ArrayList<NnChannelAutosharing>();
		
		try {
			Query query = pm.newQuery(NnChannelAutosharing.class);
			query.setFilter("channelId == channelIdParam && type == typeParam");
			query.declareParameters("long channelIdParam, short typeParam");
			@SuppressWarnings("unchecked")
			List<NnChannelAutosharing> list = (List<NnChannelAutosharing>) query.execute(channelId, type);
			if (list.size() > 0)
				results = (List<NnChannelAutosharing>) pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return results;
	}
	
	public List<NnChannelAutosharing> findChannelsByMso(long msoId) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnChannelAutosharing> results = new ArrayList<NnChannelAutosharing>();
		
		try {
			Query query = pm.newQuery(NnChannelAutosharing.class);
			query.setFilter("msoId == msoIdParam");
			query.declareParameters("long msoIdParam");
			@SuppressWarnings("unchecked")
			List<NnChannelAutosharing> list = (List<NnChannelAutosharing>) query.execute(msoId);
			if (list.size() > 0)
				results = (List<NnChannelAutosharing>) pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return results;
	}
	
	public List<NnChannelAutosharing> findChannelsByMsoAndType(long msoId, short type) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnChannelAutosharing> results = new ArrayList<NnChannelAutosharing>();
		
		try {
			Query query = pm.newQuery(NnChannelAutosharing.class);
			query.setFilter("msoId == msoIdParam && type == typeParam");
			query.declareParameters("long msoIdParam, short typeParam");
			@SuppressWarnings("unchecked")
			List<NnChannelAutosharing> list = (List<NnChannelAutosharing>) query.execute(msoId, type);
			if (list.size() > 0)
				results = (List<NnChannelAutosharing>) pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return results;
	}
}
