package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.CategoryDao;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryMap;
import com.nncloudtv.model.NnChannel;

@Service
public class CategoryManager {
	
	protected static final Logger log = Logger.getLogger(CategoryManager.class.getName());
	private CategoryDao dao = new CategoryDao();
		
	public Category save(Category category) {
		Date now = new Date();
		category.setUpdateDate(now);		
		category = dao.save(category);
		return category;
	}
			
	
	public List<Category> findCategoriesByIdStr(String categoryIds) {
		List<Long> categoryIdList = new ArrayList<Long>();	
		String[] arr = categoryIds.split(",");
		for (int i=0; i<arr.length; i++) { categoryIdList.add(Long.parseLong(arr[i])); }
		List<Category> categories = this.findAllByIds(categoryIdList);
		return categories;		
	}
			
	public void createChannelRelated(NnChannel channel, List<Category> categories) {
		//create CategoryChannel
		this.addChannelCounter(channel);
	}
	
	public void addChannelCounter(NnChannel channel) {						
	}
			
	public Category findByName(String name) {
		return dao.findByName(name);
	}	

	public Category findById(long id) {
		return dao.findById(id);
	}

	//player=true returns only public and success channels
	public List<NnChannel> findChannels(long id, boolean player) {
		List<CategoryMap> map = dao.findMap(id);
		List<NnChannel> channels = new ArrayList<NnChannel>();
		NnChannelManager chMngr = new NnChannelManager();		
		for (CategoryMap m : map) {			
			NnChannel c = chMngr.findById(m.getChannelId());
			if (c != null) {
				if ((!player) || (player && c.isPublic() && c.getStatus() == NnChannel.STATUS_SUCCESS)) {
					channels.add(c);
				}
			}
		}
		return channels;
	}
	
	public List<Category> findPlayerCategories(String lang) {
		return dao.findPlayerCategories(lang);
	}

	public List<Category> findPublicCategories(boolean isPublic) {
		return dao.findPublicCategories(isPublic);
	}
	
	public List<Category> findAllByIds(List<Long> ids) {
		 return dao.findAllByIds(ids);
	}
	
	public Category findByLangAndSeq(String lang, short seq) {
		return dao.findByLangAndSeq(lang, seq);
	}

	//sort by seq
	public List<Category> findByLang(String lang) {
		return dao.findByLang(lang);
	}
	
	public List<Category> findAll() {
		List<Category> categories = dao.findAll();
		return categories;
	}
	
	public List<Category> list(int page, int limit, String sidx, String sord) {
		return dao.list(page, limit, sidx, sord);
	}
	
	public List<Category> list(int page, int limit, String sidx, String sord, String filter) {
		return dao.list(page, limit, sidx, sord, filter);
	}
	
	public int total() {
		return dao.total();
	}
	
	public int total(String filter) {
		return dao.total(filter);
	}
		
	public void delete(Category c) {
		dao.delete(c);
	}

	public void saveAll(List<Category> categories) {
		dao.saveAll(categories);
	}
		
}
