package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.CategoryDao;
import com.nncloudtv.dao.CategoryToNnSetDao;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryToNnSet;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSet;

@Service
public class CategoryManager {
	
	protected static final Logger log = Logger.getLogger(CategoryManager.class.getName());
	private CategoryDao categoryDao = new CategoryDao();
	private CategoryToNnSetDao cToNDao = new CategoryToNnSetDao();
	
	public void create(Category category) {		
		if (this.findByName(category.getName()) == null) {
			Date now = new Date();
			category.setCreateDate(now);
			category.setUpdateDate(now);
			categoryDao.save(category);
		}
	}
	
	public Category save(Category category) {
		category.setUpdateDate(new Date());		
		category = categoryDao.save(category);
		return category;
	}

	private CategoryToNnSet findByCategoryAndSet(Category c, NnSet s) {
		return cToNDao.findByCategoryAndSet(c.getId(), s.getId());
	}
	
	public void addSets(Category c, List<NnSet> sets) {
		Date now = new Date();
		for (NnSet s : sets) {
			if (this.findByCategoryAndSet(c, s) != null) {
				CategoryToNnSet cs = new CategoryToNnSet(
					c.getId(), 
					s.getId()
				);
				s.setUpdateDate(now);
				s.setCreateDate(now);
				cToNDao.save(cs);
			}
		}
	}

	public void changeChannelCntBySet(NnSet set, int cnt) {
		List<NnSet> sets = new ArrayList<NnSet>();
		sets.add(set);
		List<Category> categories = this.findBySets(sets);
		for (Category c : categories) {
			c.setChannelCnt(c.getChannelCnt() + cnt);
			categoryDao.save(c);
		}		
	}

	public List<Category> findBySets(List<NnSet> sets) {
		CategoryToNnSetDao dao = new CategoryToNnSetDao();
		List<CategoryToNnSet> list = new ArrayList<CategoryToNnSet>();
		List<Category> categories = new ArrayList<Category>();
		for (NnSet set : sets) {
			list.addAll(dao.findBySet(set.getId()));
		}		
		for (CategoryToNnSet cToS : list) {
			Category c = this.findById(cToS.getCategoryId());
			if (c != null) {
				categories.add(c);
			}
		}
		return categories;
	}
	
	public List<NnSet> findSetsByCategory(long categoryId, boolean isPublic) {
		CategoryToNnSetDao dao = new CategoryToNnSetDao();
		List<CategoryToNnSet> list = dao.findByCategory(categoryId);
		List<Long> ids = new ArrayList<Long>();
		for (CategoryToNnSet cToN : list) {
			ids.add(cToN.getSetId());
		}
		NnSetManager setMngr = new NnSetManager();
		List<NnSet> sets = new ArrayList<NnSet>();
		if (isPublic) {
			sets.addAll(setMngr.findPublicByIds(ids));
		} else {
			sets.addAll(setMngr.findByIds(ids));
		}
		return sets;
	}
	public boolean moveSet(long fromCategoryId, long toCategoryId, long setId) {
		CategoryToNnSetDao dao = new CategoryToNnSetDao();
		CategoryToNnSet cToS = dao.findByCategoryAndSet(fromCategoryId, setId);
		if (cToS == null)
			return false;
		cToS.setCategoryId(toCategoryId);
		dao.save(cToS);
		return true;		
	}
	
	public boolean deleteSet(long categoryId, long setId) {
		CategoryToNnSetDao dao = new CategoryToNnSetDao();
		CategoryToNnSet found = dao.findByCategoryAndSet(categoryId, setId);
		if (found == null)
			return false;
		dao.delete(found);
		return true;
	}
		
	public void createChannelRelated(NnChannel channel, List<Category> categories) {
		//create CategoryChannel
		this.addChannelCounter(channel);
	}
	
	public void addChannelCounter(NnChannel channel) {						
	}
	
		
	public List<NnSet> findPlayerSetsByCategory(long categoryId) {
		NnSetManager setMngr = new NnSetManager();
		CategoryToNnSetDao csDao = new CategoryToNnSetDao();
		List<CategoryToNnSet> list = csDao.findByCategory(categoryId);
		List<NnSet> sets = new ArrayList<NnSet>();
		for (CategoryToNnSet cs : list) {
			NnSet set = setMngr.findById(cs.getSetId());
			if (set != null) {
				if (set.isPublic()) {
					sets.add(set);
				}
			}
		}
		return sets;		
	}
	
	public Category findByName(String name) {
		return categoryDao.findByName(name);
	}	

	public Category findById(long id) {
		return categoryDao.findById(id);
	}

	public List<Category> findPlayerCategories(long parentId, String lang) {
		return categoryDao.findPlayerCategories(parentId, lang);
	}

	public List<Category> findPublicCategories(boolean isPublic) {
		return categoryDao.findPublicCategories(isPublic);
	}
	
	public List<Category> findAllByIds(List<Long> ids) {
		 return categoryDao.findAllByIds(ids);
	}	
					
	public List<Category> findAll() {
		List<Category> categories = categoryDao.findAll();
		return categories;
	}
	
	public List<Category> list(int page, int limit, String sidx, String sord) {
		return categoryDao.list(page, limit, sidx, sord);
	}
	
	public List<Category> list(int page, int limit, String sidx, String sord, String filter) {
		return categoryDao.list(page, limit, sidx, sord, filter);
	}
	
	public int total() {
		return categoryDao.total();
	}
	
	public int total(String filter) {
		return categoryDao.total(filter);
	}
	
}
