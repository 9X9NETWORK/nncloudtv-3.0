package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.CategoryDao;
import com.nncloudtv.dao.CategoryMapDao;
import com.nncloudtv.dao.TagDao;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryMap;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.Tag;
import com.nncloudtv.model.TagMap;

@Service
public class CategoryManager {
    
    protected static final Logger log = Logger.getLogger(CategoryManager.class.getName());
    private CategoryDao dao = new CategoryDao();
    private CategoryMapDao mapDao = new CategoryMapDao();
    
    public CategoryMap save(CategoryMap map) {
        Date now = new Date();
        map.setUpdateDate(now);
        map = mapDao.save(map);
        return map;
    }
    
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

    public long findChannelSize(long categoryId) {
    	return dao.findChannelSize(categoryId);
    }
    
    public List<NnChannel> listChannels(int page, int limit, long categoryId) {
        List<CategoryMap> list = dao.listCategoryMap(page, limit, "categoryId = " + categoryId);
        List<Long> ids = new ArrayList<Long>();
    	for (CategoryMap m :list) {
    	    ids.add(m.getChannelId());
    	}
    	List<NnChannel> channels = new NnChannelManager().findByIds(ids);
    	return channels;
    }
        
    
    //find channels under certain category id with the tag
    public List<NnChannel> findChannelsByTag(long id, boolean player, String tagStr) {
        List<NnChannel> channels = this.findChannels(id, player); //TODO change to listChannels

        TagDao tagDao = new TagDao();
        Tag tag = tagDao.findByName(tagStr);
        HashSet<Long> set = new HashSet<Long>();
        List<NnChannel> matched = new ArrayList<NnChannel>();
        if (tag != null) {
            List<TagMap> map = tagDao.findMapByTag(tag.getId());
            for (TagMap m : map) {
                set.add(m.getChannelId());
            }
            for (NnChannel c : channels) {
                if (set.contains(c.getId()))
                    matched.add(c);
            }            
        }
        log.info("category matched channels:" + channels.size() + ";tag matched channels:" + matched.size());
        return matched;
    }
    
    public List<CategoryMap> findMapByChannelId(long channelId) {
        
        return mapDao.findByChannelId(channelId);
    }
    
    public List<Category> findByChannelId(long channelId) {
        
        List<CategoryMap> maps = mapDao.findByChannelId(channelId);
        List<Category> categories = new ArrayList<Category>();
        
        for (CategoryMap map : maps) {
            Category cat = dao.findById(map.getCategoryId());
            if (cat != null) {
                categories.add(cat);
            }
        }
        
        Collections.sort(categories, getCategorySeqComparator());
        
        return categories;
    }
    
    public Comparator<Category> getCategorySeqComparator() {
        
        class CategorySeqComparator implements Comparator<Category> {
            public int compare(Category category1, Category category2) {
                int seq1 = category1.getSeq();
                if (category1.getLang() != null
                        && category1.getLang().equalsIgnoreCase(
                                LangTable.LANG_EN)) {
                    seq1 -= 100;
                }
                int seq2 = category2.getSeq();
                if (category2.getLang() != null
                        && category2.getLang().equalsIgnoreCase(
                                LangTable.LANG_EN)) {
                    seq2 -= 100;
                }
                return (seq1 - seq2);
            }
        }
        
        return new CategorySeqComparator();
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
    
    public void delete(CategoryMap map) {
    
        mapDao.delete(map);
    }
    
    public void delete(List<CategoryMap> cats) {
        
        for (CategoryMap cat : cats) {
            delete(cat);
        }
    }
    
    public void saveAll(List<Category> categories) {
        dao.saveAll(categories);
    }
    
    /**
     * for zh to find en, and for en to find zh
     * 
     * @param category
     * @return
     */
    public Category findTwin(Category category) {
    
        if (category == null) {
            return null;
        }
        
        String lang = "en";
        if (category.getLang() != null
                && category.getLang().equalsIgnoreCase(LangTable.LANG_EN)) {
            lang = "zh";
        }
        
        return findByLangAndSeq(lang, category.getSeq());
    }
}
