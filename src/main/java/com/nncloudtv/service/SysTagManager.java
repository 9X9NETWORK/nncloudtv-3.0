package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.SysTagDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;

@Service
public class SysTagManager {
    
    protected static final Logger log = Logger.getLogger(SysTagManager.class.getName());
    
    private SysTagDao dao = new SysTagDao();

    public SysTag findById(long id) {
        return dao.findById(id);
    }
    
    public SysTag findById(Long id) {
        if(id == null) {
            return null;
        }
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
    
    /** call when Mso is going to delete **/
    public void deleteByMsoId(Long msoId) {
        // delete sysTags, sysTagDisplays, sysTagMaps
    }
    
    public List<SysTag> findByMsoIdAndType(Long msoId, short type) {
        
        if (msoId == null) {
            return new ArrayList<SysTag>();
        }
        
        return dao.findByMsoIdAndType(msoId, type);
    }

    public long findPlayerChannelsCountById(long id, String lang, long msoId) {
        long size = dao.findPlayerChannelsCountById(id, lang, msoId);        
        return size;
    }
    
    //player channels means status=true and isPublic=true
    //lang: if lang is null, then don't filter sphere
    //sort: if order = SysTag.SEQ, order by seq, otherwise order by updateDate
    //msoId: if msoId =0, do store_listing black list
    public List<NnChannel> findPlayerChannelsById(long id, String lang, int start, int count, short sort, long msoId) {
        List<NnChannel> channels = dao.findPlayerChannelsById(id, lang, false, start, count, sort, msoId);
        return channels;
    }
    
    public List<NnChannel> findPlayerChannelsById(long id, String lang, short sort, long msoId) {
        List<NnChannel> channels = dao.findPlayerChannelsById(id, lang, false, 0, 0, sort, msoId);
        return channels;
    }

    //find channels for dayparting
    public List<NnChannel> findPlayerChannelsById(long id, String lang, boolean rand, long msoId) {
        List<NnChannel> channels = dao.findPlayerChannelsById(id, lang, true, 0, 0, SysTag.SORT_DATE, 0);
        return channels;
    }
    
    public short convertDashboardType(long systagId) {
        SysTag tag = this.findById(systagId);
        if (tag == null)
            return 99;
        if (tag.getType() == SysTag.TYPE_DAYPARTING)
            return 0;
        if (tag.getType() == SysTag.TYPE_ACCOUNT)
            return 2;
        if (tag.getType() == SysTag.TYPE_SUBSCRIPTION)
            return 1;
        return 0;
            
    }
    
    /** indicate input value is in model SysTag's sorting table or not */
    public boolean isValidSortingType(Short sortingType) {
        
        if (sortingType == null) {
            return false;
        }
        if (sortingType == SysTag.SORT_SEQ) {
            return true;
        }
        if (sortingType == SysTag.SORT_DATE) {
            return true;
        }
        
        return false;
    }
    
}
