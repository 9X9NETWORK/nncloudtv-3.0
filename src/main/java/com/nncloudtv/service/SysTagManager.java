package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.SysTagDao;
import com.nncloudtv.dao.SysTagMapDao;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagMap;
import com.nncloudtv.web.json.cms.Category;

@Service
public class SysTagManager {
    
    protected static final Logger log = Logger.getLogger(SysTagManager.class.getName());
    
    private SysTagDao dao = new SysTagDao();

    public SysTag findById(long id) {
        return dao.findById(id);
    }
    
    public SysTag save(SysTag sysTag) {
        
        if (sysTag == null) {
            return null;
        }
        
        Date now = new Date();
        sysTag.setUpdateDate(now);
        if (sysTag.getCreateDate() == null) {
            sysTag.setCreateDate(now);
        }
        
        sysTag = dao.save(sysTag);
        
        return sysTag;
    }
    
    public void delete(SysTag sysTag) {
        if (sysTag == null) {
            return ;
        }
        dao.delete(sysTag);
    }
    
    public SysTag findById(Long id) {
        if(id == null) {
            return null;
        }
        return dao.findById(id);
    }
    
    public List<SysTag> findSetsByMsoId(Long msoId) {
        
        if (msoId == null) {
            return new ArrayList<SysTag>();
        }
        
        List<SysTag> results = dao.findSetsByMsoId(msoId);
        if (results == null) {
            return new ArrayList<SysTag>();
        }
        
        return results;
    }
    
    /** call when Mso is going to delete **/
    public void deleteByMsoId(Long msoId) {
        // delete sysTags, sysTagDisplays, sysTagMaps
    }

    //player channels means status=true and isPublic=true    
    public List<NnChannel> findPlayerChannelsById(long id) {
        List<NnChannel> channels = dao.findPlayerChannelsById(id);
        return channels;
    }

    public void setupChannelCategory(Long categoryId, Long channelId) {
    
        SysTagMapDao mapDao = new SysTagMapDao();
        
        List<SysTagMap> tagMaps = mapDao.findCategoryMapsByChannelId(channelId);
        mapDao.deleteAll(tagMaps);
        mapDao.save(new SysTagMap(categoryId, channelId));
    }
    
    public Comparator<Category> getCategoryComparator(String field) {
        
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

    public List<SysTag> findCategoriesByChannelId(long channelId) {
    
        SysTagDao tagDao = new SysTagDao();
        return tagDao.findCategoriesByChannelId(channelId);
    }
}
