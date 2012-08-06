package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<Category> findPlayerCategories(long parentId, String lang) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<Category> detached = new ArrayList<Category>();
		try {
			Query query = pm.newQuery(Category.class);
			query.setFilter("lang == langParam && parentId == parentIdParam && isPublic == isPublicParam");
			query.declareParameters("String langParam, long parentIdParam, boolean isPublicParam");
			query.setOrdering("seq");
			@SuppressWarnings("unchecked")
			List<Category> results = (List<Category>) query.execute(lang, parentId, true);			
			detached = (List<Category>)pm.detachCopyAll(results);
		} finally {
			pm.close();
		}
		return detached;		
	}	

	public List<Category> findPublicCategories(boolean isPublic) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<Category> detached = new ArrayList<Category>();
		try {
			Query query = pm.newQuery(Category.class);
			query.setFilter("isPublic == isPublicParam");
			query.declareParameters("boolean isPublicParam");
			query.setOrdering("lang asc, seq asc");
			@SuppressWarnings("unchecked")
			List<Category> results = (List<Category>) query.execute(isPublic);			
			detached = (List<Category>)pm.detachCopyAll(results);
		} finally {
			pm.close();
		}
		return detached;		
	}	
	
	public List<Category> findAll() {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<Category> detached = new ArrayList<Category>();
		try {
			Query query = pm.newQuery(Category.class);
			@SuppressWarnings("unchecked")
			List<Category> results = (List<Category>) query.execute();
			detached = (List<Category>)pm.detachCopyAll(results);
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
	
	public Category findByLangAndSeq(String lang, short seq) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		Category detached = null;		
		try {
			Query query = pm.newQuery(Category.class);
			query.setFilter("lang == langParam && seq == seqParam");
			query.declareParameters("String langParam, int seqParam");
			@SuppressWarnings("unchecked")
			List<Category> categories = (List<Category>) query.execute(lang, seq);
			if (categories.size() > 0) {
				detached = pm.detachCopy(categories.get(0));
			}
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		
		return detached;		
	}

	public List<Category> findByLang(String lang) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<Category> detached = new ArrayList<Category>();		
		try {
			Query query = pm.newQuery(Category.class);
			query.setFilter("lang == langParam");
			query.declareParameters("String langParam");
			query.setOrdering("seq");
			@SuppressWarnings("unchecked")
			List<Category> results = (List<Category>) query.execute(lang);
			detached = (List<Category>)pm.detachCopyAll(results);			
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		
		return detached;		
	}
	
}
