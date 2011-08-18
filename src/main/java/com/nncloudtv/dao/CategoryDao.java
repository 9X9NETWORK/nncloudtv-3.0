package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOFatalException;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Category;

public class CategoryDao extends GenericDao<Category> {
		
	public CategoryDao() {
		super(Category.class);
	}
	
	public Category save(Category category) {
		if (category == null) {return null;}		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			pm.makePersistent(category);
			category = pm.detachCopy(category);
		} finally {
			pm.close();
		}
		return category;
	}

	public void delete(Category category) {
		throw new JDOFatalException();
	}
	
	public Category findByName(String name) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		Category detached = null;
		try {
			Query query = pm.newQuery(Category.class);
			query.setFilter("name == " + NnStringUtil.escapedQuote(name));
			@SuppressWarnings("unchecked")
			List<Category> results = (List<Category>) query.execute();			
			if (results.size() > 0) {
				detached = pm.detachCopy(results.get(0));
			}
		} finally {
			pm.close();
		}
		return detached;
	}

	public List<Category> findIpgCategoryByMsoId(long msoId) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<Category> detached = new ArrayList<Category>();
		try {
			Query q = pm.newQuery(Category.class);
			q.setFilter("msoId == msoIdParam && isIpg == isIpgParam");
			q.declareParameters("long msoIdParam, boolean isIpgParam");
			q.setOrdering("name");
			@SuppressWarnings("unchecked")
			List<Category> categories = (List<Category>)q.execute(msoId, true);
			detached = (List<Category>)pm.detachCopyAll(categories);
		} finally {
			pm.close();			
		}
		return detached;
	}
	
	public List<Category> findAllByMsoId(long msoId) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<Category> detached = new ArrayList<Category>();
		try {
			Query q = pm.newQuery(Category.class);
			q.setFilter("msoId == msoIdParam");
			q.declareParameters("long msoIdParam");
			q.setOrdering("name");
			@SuppressWarnings("unchecked")
			List<Category> categories = (List<Category>)q.execute(msoId);
			detached = (List<Category>)pm.detachCopyAll(categories);
		} finally {
			pm.close();			
		}
		return detached;		
	}
	
	public List<Category> findAll() {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<Category> detached = new ArrayList<Category>();
		try {
			String query = "select from " + Category.class.getName() + " order by createDate";
			@SuppressWarnings("unchecked")
			List<Category> categories = (List<Category>) pm.newQuery(query).execute();
			detached = (List<Category>)pm.detachCopyAll(categories);
		} finally {
			pm.close();
		}
		return detached;				
	}
	
	public Category findById(long id) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		Category category = null;
		try { 
			category = pm.getObjectById(Category.class, id);
			category = pm.detachCopy(category);
		} catch (JDOObjectNotFoundException e) {			
		} finally {			
			pm.close();
		}
		return category;		
	}

	public List<Category> findAllInIpg(long msoId) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<Category> detached = new ArrayList<Category>();
		try {
			Query q = pm.newQuery(Category.class);
			q.setFilter("msoId == msoIdParam && isIpg == isIpgParam");
			q.declareParameters("long msoIdParam, boolean isIpgParam");
			q.setOrdering("seq asc");
			@SuppressWarnings("unchecked")
			List<Category> categories = (List<Category>)q.execute(msoId, true);
			detached = (List<Category>)pm.detachCopyAll(categories);
		} finally {
			pm.close();			
		}
		return detached;		
	}
	
	//!!! contains query
	public List<Category> findAllByIds(List<Long> ids) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<Category> categories= new ArrayList<Category>();
		try {
			for (long id : ids) {
				Category c = this.findById(id); 
				if (c != null) {
					categories.add(c);
				}
			}
		} finally {
			pm.close();		
		}
		return categories;
	}
			
}
