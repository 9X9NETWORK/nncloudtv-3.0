package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.CategoryChannelDao;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryChannel;
import com.nncloudtv.model.NnChannel;

public class CategoryChannelManager {
	
	protected static final Logger log = Logger.getLogger(CategoryChannelManager.class.getName());
	
	private CategoryChannelDao ccDao = new CategoryChannelDao();		
	
	public void create(CategoryChannel cc) {
		Date now = new Date();
		cc.setCreateDate(now);
		cc.setUpdateDate(now);
		ccDao.save(cc);
	}	

	public void delete(CategoryChannel cc) {
		CategoryManager categoryMngr = new CategoryManager();
		Category category = categoryMngr.findById(cc.getCategoryId()); 
		ccDao.delete(cc);
		category.setChannelCount(category.getChannelCount()-1);
		categoryMngr.save(category);
	}
	
	public void deleteChannelCategory(NnChannel channel, List<Category> categories) {
		if (channel == null) {return;}
		if (categories.size() == 0) {return;}
				
		for (Category c : categories) {	
			CategoryChannel cc = this.findByCategoryIdAndChannelId(c.getId(), channel.getId());
			if (cc != null) {
				this.delete(cc);
			}
		}		
	}
	
	public List<CategoryChannel> findAllByCategoryId(long categoryId) {
		List<CategoryChannel> ccs = ccDao.findAllByCategoryId(categoryId);
		log.info("findByCategoryKey(): found " + ccs.size() + " with id " + categoryId);
		return ccs;
	}
	
	public List<CategoryChannel> findAllByChannelId(long channelId) {
		List<CategoryChannel> ccs = ccDao.findAllByChannelId(channelId);
		return ccs;
	}

	public CategoryChannel findByCategoryIdAndChannelId(long categoryId, long channelId) {
		return ccDao.findByCategoryIdAndChannelId(categoryId, channelId);
	}
	
	public CategoryChannel findById(long id) {
		return ccDao.findById(id);
	}
	
}
