package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.CategoryToNnSetDao;
import com.nncloudtv.dao.NnSetToNnChannelDao;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryToNnSet;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnSetToNnChannel;

@Service
public class CmsApiService {
	protected static final Logger log = Logger.getLogger(CmsApiService.class.getName());
	
	private NnSetManager setMngr = new NnSetManager();
	private ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
	private CategoryManager catMngr = new CategoryManager();
	
	public NnSet getDefaultNnSet(long msoId) {
		List<NnSet> ownedNnSets = ownershipMngr.findOwnedSetsByMso(msoId);
		if (ownedNnSets.size() > 0) {
			return ownedNnSets.get(0);
		}
		return null;
	}
 
	public List<NnChannel> findChannelsBySet(long setId) {
		return setMngr.findChannelsById(setId);
	}
		
	//!!! move to category_manager
	public List<CategoryToNnSet> whichCToSContainingTheSet(long setId) {
		CategoryToNnSetDao dao = new CategoryToNnSetDao();
		List<Category> sysCategories = catMngr.findPublicCategories(true);
		List<Long> categoryIds = new ArrayList<Long>();
		for (Category category : sysCategories) {
			categoryIds.add(category.getId());
		}
		List<CategoryToNnSet> results = new ArrayList<CategoryToNnSet>();
		for (long categoryId : categoryIds) {
			CategoryToNnSet cToN = dao.findByCategoryAndSet(categoryId, setId);
			if (cToN != null)
				results.add(cToN);
		}
		return results;
	}
	
	public Category whichSystemCategoryContainingTheSet(long setId) {		
		CategoryToNnSetDao dao = new CategoryToNnSetDao();
		List<Category> sysCategories = catMngr.findPublicCategories(true);
		
		for (Category category : sysCategories) {
			CategoryToNnSet ccs = dao.findByCategoryAndSet(category.getId(), setId);
			if (ccs != null) {
				return catMngr.findById(ccs.getCategoryId());
			}
		}
		
		return null;
	}
	
	/*
	public List<Category> whichSystemCategoriesContainingTheChannel(long channelId) {		
		List<CategoryChannel> ccs = this.whichCCContainingTheChannel(channelId);		
		logger.info("ccs count = " + ccs.size());		
		List<Long> categoryIds = new ArrayList<Long>();
		for (CategoryChannel cc : ccs) {
			categoryIds.add(cc.getCategoryId());
		}
		
		return catMngr.findAllByIds(categoryIds);
	}	
	public List<CategoryChannel> whichCCContainingTheChannel(Long channelId) {
		List<Category> sysCategories = catMngr.findAllSystemCategories();
		List<Long> categoryIds = new ArrayList<Long>();
		for (Category category : sysCategories) {
			categoryIds.add(category.getId());
		}
		
		return ccMngr.findByChannelIdAndCategoryIds(channelId, categoryIds);
	}
	*/
	
	public List<NnSet> whichSystemNnSetsContainingThisChannel(Long channelId) {			
		NnSetManager setMngr = new NnSetManager();
		List<NnSetToNnChannel> list = this.whichSystemCSCContainingThisChannel(channelId);
		List<Long> ids = new ArrayList<Long>();
		for (NnSetToNnChannel sToC : list) {
			ids.add(sToC.getSetId());
		}
		return setMngr.findByIds(ids);
	}
	
	public List<NnSetToNnChannel> whichSystemCSCContainingThisChannel(long channelId) {
		NnSetToNnChannelDao sToCDao = new NnSetToNnChannelDao(); 
		List<NnSetToNnChannel> results = new ArrayList<NnSetToNnChannel>();		
		List<NnSetToNnChannel> cscs = sToCDao.findByChannel(channelId);
		List<NnSet> systemSets = setMngr.findAllSystemSets();
		List<Long> systemSetIds = new ArrayList<Long>();
		for (NnSet set : systemSets) {
			systemSetIds.add(set.getId());
		}
		for (NnSetToNnChannel sToC : cscs) {
			if (systemSetIds.contains(sToC.getSetId())) {
				results.add(sToC);
			}
		}
		
		return results;
	}
}
