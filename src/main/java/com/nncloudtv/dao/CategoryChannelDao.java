package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.CategoryChannel;

public class CategoryChannelDao extends GenericDao<CategoryChannel> {
	
	protected static final Logger logger = Logger.getLogger(CategoryChannelDao.class.getName());
	
	public CategoryChannelDao() {
		super(CategoryChannel.class);
	}
	
	public CategoryChannel save(CategoryChannel cc) {		
		if (cc == null) {return null;}
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
	    try {
			pm.makePersistent(cc);
			cc= pm.detachCopy(cc);
	    } finally {
	    	pm.close();
	    }
		return cc;
	}
	
	public void delete(CategoryChannel cc) {
		if (cc == null) {return;}
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			pm.deletePersistent(cc);
		} finally {
			pm.close();
		}
	}
	
	public CategoryChannel findById(long id) {
		CategoryChannel cc = null;
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			cc = pm.getObjectById(CategoryChannel.class, id);
			cc = pm.detachCopy(cc);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();			
		}
		return cc;		
	}	
	
	public List<CategoryChannel> findAllByCategoryId(long categoryId) {
		List<CategoryChannel> detached = new ArrayList<CategoryChannel>();
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			Query q = pm.newQuery(CategoryChannel.class);
			q.setFilter("categoryId == categoryIdParam");
			q.declareParameters("long categoryIdParam");
			@SuppressWarnings("unchecked")
			List<CategoryChannel> ccs = (List<CategoryChannel>)q.execute(categoryId);
			detached = (List<CategoryChannel>)pm.detachCopyAll(ccs);
		} finally {
			pm.close();
		}
		return detached;
	}	

	public List<CategoryChannel> findAllByChannelId(long channelId) {
		List<CategoryChannel> detached = new ArrayList<CategoryChannel>();
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			Query q = pm.newQuery(CategoryChannel.class);
			q.setFilter("channelId == channelIdParam");
			q.declareParameters("long channelIdParam");
			@SuppressWarnings("unchecked")
			List<CategoryChannel> ccs = (List<CategoryChannel>)q.execute(channelId);
			detached = (List<CategoryChannel>)pm.detachCopyAll(ccs);
		} finally {
			pm.close();
		}
		return detached;
	}	
	
	public CategoryChannel findByCategoryIdAndChannelId(long categoryId, long channelId) {
		CategoryChannel cc = null;
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			Query q = pm.newQuery(CategoryChannel.class);
			q.setFilter("categoryId == categoryIdParam && channelId == channelIdParam");
			q.declareParameters("long categoryIdParam, long channelIdParam");
			@SuppressWarnings("unchecked")
			List<CategoryChannel> ccs= (List<CategoryChannel>)q.execute(categoryId, channelId);
			if (ccs.size() > 0) {
				cc = ccs.get(0);
				cc = pm.detachCopy(cc);
			}
		} finally {
			pm.close();
		}
		return cc;
	}
		
}
