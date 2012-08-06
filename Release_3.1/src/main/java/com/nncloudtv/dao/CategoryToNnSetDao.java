package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.CategoryToNnSet;

public class CategoryToNnSetDao extends GenericDao<CategoryToNnSet> {
	
	protected static final Logger log = Logger.getLogger(CategoryToNnSetDao.class.getName());
	
	public CategoryToNnSetDao() {
		super(CategoryToNnSet.class);
	}
	
	public List<CategoryToNnSet> findByCategory(long categoryId) {		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<CategoryToNnSet> detached = new ArrayList<CategoryToNnSet>();
		try {
			Query query = pm.newQuery(CategoryToNnSet.class);
			query.setFilter("categoryId == categoryIdParam");
			query.declareParameters("long categoryIdParam");			
			@SuppressWarnings("unchecked")
			List<CategoryToNnSet> ccs = (List<CategoryToNnSet>)query.execute(categoryId);
			detached = (List<CategoryToNnSet>)pm.detachCopyAll(ccs);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return detached;
	}
	
	public List<CategoryToNnSet> findBySet(long setId) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<CategoryToNnSet> detached = new ArrayList<CategoryToNnSet>();
		try {
			Query query = pm.newQuery(CategoryToNnSet.class);
			query.setFilter("setId == setIdParam");
			query.declareParameters("long setIdParam");
			@SuppressWarnings("unchecked")
			List<CategoryToNnSet> ccs = (List<CategoryToNnSet>)query.execute(setId);
			detached = (List<CategoryToNnSet>)pm.detachCopyAll(ccs);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return detached;
	}

	public CategoryToNnSet findByCategoryAndSet(long categoryId, long setId) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		CategoryToNnSet result = null;
		try {
			Query query = pm.newQuery(CategoryToNnSet.class);
			query.setFilter("categoryId == categoryIdParam && setId == setIdParam");
			query.declareParameters("long categoryIdParam, long setIdParam");
			@SuppressWarnings("unchecked")
			List<CategoryToNnSet> to = (List<CategoryToNnSet>)query.execute(categoryId, setId);
			if (to.size() > 0)
				result = pm.detachCopy(to.get(0));
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return result;
	}
}
