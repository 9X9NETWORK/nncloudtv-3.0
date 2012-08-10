package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnChannel;

public class NnChannelDao extends GenericDao<NnChannel> {
	
	protected static final Logger log = Logger.getLogger(MsoDao.class.getName());
	
	public NnChannelDao() {
		super(NnChannel.class);
	}	
		
	public List<NnChannel> findByType(short type) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnChannel> detached = new ArrayList<NnChannel>(); 
		try {
			Query q = pm.newQuery(NnChannel.class);
			q.setFilter("contentType == contentTypeParam");
			q.declareParameters("short contentTypeParam");
			@SuppressWarnings("unchecked")
			List<NnChannel> channels = (List<NnChannel>) q.execute(type);
			detached = (List<NnChannel>)pm.detachCopyAll(channels);
		} finally {
			pm.close();
		}
		return detached;
	}	
	
	public NnChannel save(NnChannel channel) {
		if (channel == null) {return null;}
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			pm.makePersistent(channel);			
			channel = pm.detachCopy(channel);
		} finally {
			pm.close();
		}
		return channel;
	}
	
	public NnChannel findById(long id) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();		
		NnChannel channel = null;
		try {
			channel = pm.getObjectById(NnChannel.class, id);
			channel = pm.detachCopy(channel);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();			
		}
		return channel;		
	}	

	//replaced with Apache Lucene
	@SuppressWarnings("unchecked")
	public static List<NnChannel> search(String queryStr, boolean all) {		
	    PersistenceManager pm = PMF.getContent().getPersistenceManager();
	    String sql = "select * from nnchannel " +
	     "where (lower(name) like lower('%" + queryStr + "%')" +   
	     "|| lower(intro) like lower('%" + queryStr + "%'))";
	    if (!all) {
	    	sql += " and status = " + NnChannel.STATUS_SUCCESS;
	    }

//	    String sql = "select * from nnchannel " +
//	     "where status = " + NnChannel.STATUS_SUCCESS + 
//	     " and " + 
//	     "(lower(name) like lower('%" + queryStr + "%')" + 
//	     "|| lower(intro) like lower('%" + queryStr + "%'))";
	    
	    log.info("Sql=" + sql);
	    Query q= pm.newQuery("javax.jdo.query.SQL", sql);
	    q.setClass(NnChannel.class);
	    List<NnChannel> results = (List<NnChannel>) q.execute();
	    
		return results; 	
	}
	
	public List<NnChannel> findAll() {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnChannel> detached = new ArrayList<NnChannel>();
		try {
			Query query = pm.newQuery(NnChannel.class);
			@SuppressWarnings("unchecked")
			List<NnChannel> results = (List<NnChannel>) query.execute();
			detached = (List<NnChannel>)pm.detachCopyAll(results);
		} finally {
			pm.close();
		}
		return detached;
	}
		
	public List<NnChannel> findAllByStatus(short status) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnChannel> detached = new ArrayList<NnChannel>(); 
		try {
			Query q = pm.newQuery(NnChannel.class);
			q.setFilter("status == statusParam");
			q.declareParameters("short statusParam");
			q.setOrdering("createDate asc");
			@SuppressWarnings("unchecked")
			List<NnChannel> channels = (List<NnChannel>) q.execute(status);
			detached = (List<NnChannel>)pm.detachCopyAll(channels);
		} finally {
			pm.close();
		}
		return detached;
	}	
	
	public NnChannel findBySourceUrl(String url) {
		if (url == null) {return null;}
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnChannel channel = null;
		try {
			Query q = pm.newQuery(NnChannel.class);
			q.setFilter("sourceUrl== sourceUrlParam");
			q.declareParameters("String sourceUrlParam");
			@SuppressWarnings("unchecked")
			//List<NnChannel> channels = (List<NnChannel>) q.execute(url.toLowerCase());
			List<NnChannel> channels = (List<NnChannel>) q.execute(url);
			if (channels.size() > 0) {
				channel = pm.detachCopy(channels.get(0));
			}
		} finally {
			pm.close();
		}
		return channel;				
	}		

	public NnChannel findByUserId(long userId) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnChannel channel = null;
		try {
			Query q = pm.newQuery(NnChannel.class);
			q.setFilter("userId == userIdParam");
			q.declareParameters("long userIdParam");
			@SuppressWarnings("unchecked")
			List<NnChannel> channels = (List<NnChannel>) q.execute(userId);
			if (channels.size() > 0) {
				channel = pm.detachCopy(channels.get(0));
			}
		} finally {
			pm.close();
		}
		return channel;				
	}		
	
	
}
