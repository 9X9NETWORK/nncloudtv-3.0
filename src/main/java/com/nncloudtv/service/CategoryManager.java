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
		
	public Category save(Category category) {
		Date now = new Date();
		if (category.getCreateDate() == null)
			category.setCreateDate(now);
		category.setUpdateDate(now);		
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
	
	public void addSet(Category c, NnSet set) {
		Date now = new Date();
		if (this.findByCategoryAndSet(c, set) == null) {
			CategoryToNnSet cs = new CategoryToNnSet(
				c.getId(), 
				set.getId()
			);
			cs.setUpdateDate(now);
			cs.setCreateDate(now);
			cToNDao.save(cs);
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
	
	public List<Category> findBySet(NnSet set) {
		CategoryToNnSetDao dao = new CategoryToNnSetDao();
		List<CategoryToNnSet> list = new ArrayList<CategoryToNnSet>();
		List<Category> categories = new ArrayList<Category>();
		
		list.addAll(dao.findBySet(set.getId()));
				
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
	
	public List<Category> findCategoriesByIdStr(String categoryIds) {
		List<Long> categoryIdList = new ArrayList<Long>();	
		String[] arr = categoryIds.split(",");
		for (int i=0; i<arr.length; i++) { categoryIdList.add(Long.parseLong(arr[i])); }
		List<Category> categories = this.findAllByIds(categoryIdList);
		return categories;		
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
		
	
	/*
	public void deleteCatToSetBySetId(long setId) {
		deleteAllCatToSet(this.findCatToSetBySetId(setId));
		for(Category category : categories) {
			deleteSet(category.getId(), setId);
		}
	}
*/

	/*
	public void deleteCatToSetByCatId(long categoryId) {
		deleteAllCatToSet(this.findCatToSetByCategoryId(categoryId));
	}
	*/
		
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
			if (set != null && set.isPublic()) {
				sets.add(set);
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
	
	public Category findByLangAndSeq(String lang, short seq) {
		return categoryDao.findByLangAndSeq(lang, seq);
	}

	//sort by seq
	public List<Category> findByLang(String lang) {
		return categoryDao.findByLang(lang);
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
	
	public List<CategoryToNnSet> listCatToSet(int page, int limit, String sidx, String sord) {
		return cToNDao.list(page, limit, sidx, sord);
	}
	
	public List<CategoryToNnSet> listCatToSet(int page, int limit, String sidx, String sord, String filter) {
		return cToNDao.list(page, limit, sidx, sord, filter);
	}
	
	public int total() {
		return categoryDao.total();
	}
	
	public int total(String filter) {
		return categoryDao.total(filter);
	}
	
	public int totalCatToSet() {
		return cToNDao.total();
	}
	
	public int totalCatToSet(String filter) {
		return cToNDao.total(filter);
	}
	
	public void deleteSets(Category c) {
		List<CategoryToNnSet> list = cToNDao.findByCategory(c.getId());
		cToNDao.deleteAll(list);
	}
	
	public boolean deleteSet(long categoryId, long setId) {
		CategoryToNnSet found = cToNDao.findByCategoryAndSet(categoryId, setId);
		if (found == null)
			return false;
		cToNDao.delete(found);
		return true;
	}
	
	public void delete(Category c) {
		deleteSets(c);
		categoryDao.delete(c);
	}

	public void saveAll(List<Category> categories) {
		categoryDao.saveAll(categories);
	}
		
}
