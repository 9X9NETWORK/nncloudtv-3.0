package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnSetToNnChannel;

public class NnSetToNnChannelDao extends GenericDao<NnSetToNnChannel> {
	
	protected static final Logger log = Logger.getLogger(NnSetToNnChannel.class.getName());
	
	public NnSetToNnChannelDao() {
		super(NnSetToNnChannel.class);
	}
	
	public NnSetToNnChannel findBySetAndChannel(long setId, long channelId) {		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnSetToNnChannel result = null;		
		try {
			Query query = pm.newQuery(NnSetToNnChannel.class);
			query.setFilter("setId == setIdParam && channelId == channelIdParam");
			query.declareParameters("long setIdParam, long channelIdParam");
			@SuppressWarnings("unchecked")
			List<NnSetToNnChannel> list = (List<NnSetToNnChannel>)query.execute(setId, channelId);
			if (list.size() > 0)
				result = pm.detachCopy(list.get(0));
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return result;
	}
	
	//TODO, should be merged to findBySet(NnSet set), it sets ordering		
	public List<NnSetToNnChannel> findBySet(long setId) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnSetToNnChannel> detached = new ArrayList<NnSetToNnChannel>();
		
		try {
			Query query = pm.newQuery(NnSetToNnChannel.class);
			query.setFilter("setId == setIdParam");
			query.declareParameters("long setIdParam");
			@SuppressWarnings("unchecked")
			List<NnSetToNnChannel> list = (List<NnSetToNnChannel>)query.execute(setId);
			detached = (List<NnSetToNnChannel>)pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return detached;
	}

	public List<NnSetToNnChannel> findBySet(NnSet set) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnSetToNnChannel> detached = new ArrayList<NnSetToNnChannel>();		
		try {
			Query query = pm.newQuery(NnSetToNnChannel.class);
			query.setFilter("setId == setIdParam");
			query.declareParameters("long setIdParam");
			if (set.isFeatured()) {
				log.info("ordering by seq");
				query.setOrdering("seq");
			} else { 
				log.info("ordering by updateDate");
				query.setOrdering("updateDate desc");
			}
			@SuppressWarnings("unchecked")
			List<NnSetToNnChannel> list = (List<NnSetToNnChannel>)query.execute(set.getId());
			detached = (List<NnSetToNnChannel>)pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {	
		} finally {
			pm.close();
		}
		return detached;
	}
	
	public List<NnSetToNnChannel> findByChannel(long channelId) {	
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnSetToNnChannel> detached = new ArrayList<NnSetToNnChannel>();
		
		try {
			Query query = pm.newQuery(NnSetToNnChannel.class);
			query.setFilter("channelId == channelIdParam");
			query.declareParameters("long channelIdParam");
			@SuppressWarnings("unchecked")
			List<NnSetToNnChannel> list = (List<NnSetToNnChannel>)query.execute(channelId);
			System.out.println("<<< dao layer size:" + list.size() + ";setId=" + channelId);
			detached = (List<NnSetToNnChannel>)pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return detached;
	}
		
	public NnSetToNnChannel findBySetAndSeq(long setId, short seq) {		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnSetToNnChannel result = null;
		
		try {
			Query query = pm.newQuery(NnSetToNnChannel.class);
			query.setFilter("setId == setIdParam && seq == seqParam");
			query.declareParameters("long setIdParam, short seqParam");
			@SuppressWarnings("unchecked")
			List<NnSetToNnChannel> list = (List<NnSetToNnChannel>)query.execute(setId, seq);
			if (list.size() > 0)
				result = pm.detachCopy(list.get(0));
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return result;
	}
}
