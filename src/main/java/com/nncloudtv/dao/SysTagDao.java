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
    public List<NnChannel> findPlayerChannelsById(long id, String lang, boolean limitRows, int start, int count, short sort, long msoId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        try {
            /*
            select * 
              from nnchannel a1  
            inner join ( 
             select distinct c.id  
               from systag_display d, systag_map m, nnchannel c  
              where d.systagId = 3 
                and d.systagId = m.systagId 
                and c.id = m.channelId
                and c.isPublic = true  
                and c.status = 0                
                and c.id not in (select channelId from store_listing where msoId=3)
                and (c.sphere = 'en' or c.sphere = 'other')
                order by c.updateDate desc
                limit 3, 5                
              ) a2 on a1.id=a2.id
            */
        	        	
            String orderStr = " order by c.updateDate desc";
            if (sort == SysTag.SORT_SEQ) {
            	orderStr = " order by m.seq ";
            }
            if (limitRows)
                orderStr = " order by rand() limit 9";
            if (start >= 0 && count > 0) {
            	//start = start - 1;
                orderStr += " limit " + start + ", " + count;
            }
            String langStr = "";
            if (lang != null)
            	langStr = " and (c.sphere = '" + lang + "' or c.sphere = 'other')";
            String blackList = "";
            if (msoId != 0) {
            	blackList = " and c.id not in (select channelId from store_listing where msoId=" + msoId + ") ";
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
                           blackList +   
                           langStr + 
                           orderStr +
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

    public long findPlayerChannelsCountById(long id, String lang, long msoId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        long size = 0;
        try {
            /*
             select count(*) 
               from nnchannel a1  inner join  
             (select distinct c.id  
               from systag_display d, systag_map m, nnchannel c  
              where d.systagId = 3 
                and d.systagId = m.systagId 
                and c.id = m.channelId
                and c.isPublic = true  
                and c.status = 0                
                and c.id not in (select channelId from store_listing where msoId=3)
                and (c.sphere = 'zh' or c.sphere = 'other')) a2 on a1.id=a2.id
            */
        	        	
            String langStr = "";
            if (lang != null)
            	langStr = " and (c.sphere = '" + lang + "' or c.sphere = 'other')";
            String blackList = "";
            if (msoId != 0) {
            	blackList = " and c.id not in (select channelId from store_listing where msoId=" + msoId + ") ";
            }
            String sql =  "select count(*) " + 
                           " from nnchannel a1 inner join " + 
            		     "(select distinct c.id " +                         
                          " from systag_display d, systag_map m, nnchannel c " +
                         " where d.systagId = " + id + 
                           " and d.systagId = m.systagId " +                           
                           " and c.id = m.channelId " +
                           " and c.isPublic = true" +
                           " and c.contentType != " + NnChannel.CONTENTTYPE_FAVORITE +
                           " and c.status = " + NnChannel.STATUS_SUCCESS +
                           blackList +   
                           langStr + 
                           " ) a2 on a1.id=a2.id"; 
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            @SuppressWarnings("rawtypes")
            List results = (List) q.execute();
            size = (Long)results.iterator().next();
        } finally {
            pm.close();
        }
        return size;                
    }
    
    public List<SysTag> findCategoriesByChannelId(long channelId, long msoId) {
    
        String query = " select * from systag a1"
                     + " inner join (select s.id from systag_map m, systag s"
                     +             " where s.type = "      + SysTag.TYPE_CATEGORY
                     +               " and s.msoId = "     + msoId
                     +               " and m.channelId = " + channelId
                     +               " and s.id = m.systagId) a2 on a1.id = a2.id";
        
        return sql(query);
    }
}
