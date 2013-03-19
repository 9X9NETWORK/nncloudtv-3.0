package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryMap;

public class CategoryDao extends GenericDao<Category> {
        
    public CategoryDao() {
        super(Category.class);
    }

    public long findChannelSize(long categoryId) {
    	PersistenceManager pm = PMF.getContent().getPersistenceManager();
    	long size = 0;
    	try {
        	String sql = "select count(*) " +
        				   "from category_map " +
        				  "where categoryId = " + categoryId;    				   
        	Query query = pm.newQuery("javax.jdo.query.SQL", sql);
        	@SuppressWarnings("rawtypes")
    		List results = (List) query.execute();
        	size = (Long)results.iterator().next();
        } finally {
            pm.close();
        }
    	return size;
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
    
    public List<Category> findPlayerCategories(String lang) {
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
    
    public List<CategoryMap> listCategoryMap(int page, int limit, String filter) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        /*
        select * 
          from category_map
         where categoryId = 1
           and channelId in (select id from nnchannel where isPublic=true and status=0)
         limit 10, 20
        */
        List<CategoryMap> detached = new ArrayList<CategoryMap>();
        try {
            String sql = "select * " +
                           "from category_map ";
            if (filter != null && filter != "")
               sql += " where " + filter + " and ";
            else
               sql += " where ";
            sql += " channelId in " +
                      "(select id from nnchannel where isPublic=true and status=0)";
            if (limit > 0) {
                int start = (page-1) * limit;
                sql += " limit " + start + "," + limit;
            }
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(CategoryMap.class);
            @SuppressWarnings("unchecked")
            List<CategoryMap> results = (List<CategoryMap>) query.execute();
            detached = (List<CategoryMap>)pm.detachCopyAll(results);
        } finally {            
            pm.close();
        } 
        return detached;
        
    }
        
    public List<CategoryMap> findMap(long id) {        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<CategoryMap> detached = new ArrayList<CategoryMap>();
        try {
            Query query = pm.newQuery(CategoryMap.class);
            query.setFilter("categoryId == categoryIdParam");
            query.declareParameters("long categoryIdParam");
            @SuppressWarnings("unchecked")
            List<CategoryMap> results = (List<CategoryMap>) query.execute(id);
            detached = (List<CategoryMap>)pm.detachCopyAll(results);            
        } catch (JDOObjectNotFoundException e) {
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
