package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;

public class SysTagDao extends GenericDao<SysTag> {

    protected static final Logger log = Logger.getLogger(SysTagDao.class.getName());
    
    public SysTagDao() {
        super(SysTag.class);
    }
    
    public List<SysTag> findSetsByMsoId(long msoId) {        
        List<SysTag> detached = new ArrayList<SysTag>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();        
        try {
            String sql = " select * from systag where msoId = " + msoId +
                           " and type = " + SysTag.TYPE_SET +
                           " order by seq asc";
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(SysTag.class);
            @SuppressWarnings("unchecked")
            List<SysTag> results = (List<SysTag>) q.execute();            
            detached = (List<SysTag>) pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        
        return detached;
    }
    
    //player channels means status=true and isPublic=true
    public List<NnChannel> findPlayerChannelsById(long id, String lang, boolean limitRows, int start, int count) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        try {
            /*
            select * 
              from nnchannel a1  
            inner join ( 
             select distinct c.id  
               from systag_display d, systag_map m, nnchannel c  
              where d.systagId = 56 
                and d.systagId = m.systagId 
                and c.id = m.channelId
                and c.isPublic = true  
                and c.status = 0                
                and (c.lang = 'en' or c.lang = 'other')
                order by c.updateDate desc
                limit 3, 5                
              ) a2 on a1.id=a2.id
            */            
            String str = " order by c.updateDate desc";
            if (limitRows)
                str = " order by rand() limit 9";
            if (start > 0 && count > 0) {
                str += " limit " + start + ", " + count;
            }
            String sql = "select * from nnchannel a1 " +
                         " inner join " + 
                       " (select distinct c.id " + 
                          " from systag_display d, systag_map m, nnchannel c " +
                         " where d.systagId = " + id + 
                           " and d.systagId = m.systagId " +                           
                           " and c.id = m.channelId " +
                           " and c.isPublic = true" +
                           " and c.contentType != " + NnChannel.CONTENTTYPE_FAVORITE +
                           " and c.status = " + NnChannel.STATUS_SUCCESS +
                           " and (c.lang = '" + lang + "' or c.lang = 'other')" +
                           str +
                           ") a2 on a1.id=a2.id";
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnChannel.class);
            @SuppressWarnings("unchecked")
            List<NnChannel> results = (List<NnChannel>) q.execute();            
            detached = (List<NnChannel>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;                
    }

    public List<NnChannel> findSetChannelsById(long id, boolean limitRows, int page, int limit) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        try {
            /*
            select * 
              from nnchannel a1  
            inner join ( 
             select distinct c.id  
               from systag_display d, systag_map m, nnchannel c  
              where d.systagId = 56 
                and d.systagId = m.systagId 
                and c.id = m.channelId
                and c.isPublic = true  
                and c.status = 0                
                and (c.lang = 'en' or c.lang = 'other')
                order by c.updateDate desc
                limit 3, 5                
              ) a2 on a1.id=a2.id
            */            
            String str = " order by c.updateDate desc";
            if (limitRows)
                str = " order by rand() limit 9";
            if (limit > 0 && page > 0) {
                int start = (page-1) * limit;                
                str += " limit " + start + ", " + limit;
            }
            String sql = "select * from nnchannel a1 " +
                         " inner join " + 
                       " (select distinct c.id " + 
                          " from systag_display d, systag_map m, nnchannel c " +
                         " where d.systagId = " + id + 
                           " and d.systagId = m.systagId " +                           
                           " and c.id = m.channelId " +
                           " and c.isPublic = true" +
                           " and c.contentType != " + NnChannel.CONTENTTYPE_FAVORITE +
                           str +
                           ") a2 on a1.id=a2.id";
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnChannel.class);
            @SuppressWarnings("unchecked")
            List<NnChannel> results = (List<NnChannel>) q.execute();            
            detached = (List<NnChannel>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;                
    }
    
    
    /** twin whit findPlayerChannelsById but lang independent */
    public List<NnChannel> findStoreChannelsById(long sysTagId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        try {  
            String str = " order by c.updateDate desc";
            String sql = "select * from nnchannel a1 " +
                         " inner join " +
                       " (select distinct c.id " +
                          " from systag_display d, systag_map m, nnchannel c " +
                         " where d.systagId = " + sysTagId +
                           " and d.systagId = m.systagId " +
                           " and c.id = m.channelId " +
                           " and c.isPublic = true" +
                           " and c.status = " + NnChannel.STATUS_SUCCESS +
                           str +
                           ") a2 on a1.id=a2.id";
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnChannel.class);
            @SuppressWarnings("unchecked")
            List<NnChannel> results = (List<NnChannel>) q.execute(); 
            if (results != null && results.size() > 0) {
                detached = (List<NnChannel>)pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        return detached;                
    }
    
    /** find all channels in the store */
    public List<NnChannel> findStoreChannels() {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        try {
            String sql = "select * from nnchannel where isPublic = true" +
                           " and status = " + NnChannel.STATUS_SUCCESS +
                           " order by updateDate desc";
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnChannel.class);
            @SuppressWarnings("unchecked")
            List<NnChannel> results = (List<NnChannel>) q.execute(); 
            if (results != null && results.size() > 0) {
                detached = (List<NnChannel>)pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        return detached;                
    }
    
    public List<SysTag> findCategoriesByChannelId(long channelId) {
    
        return sql("select * from systag where type = 1 and id in (select systagId from systag_map where channelId = " + channelId + ")");
    }
}
