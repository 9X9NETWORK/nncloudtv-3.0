package com.nncloudtv.service;

import org.springframework.stereotype.Service;

@Service
public class CmsApiService {
    /*
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
    */
}
