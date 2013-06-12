package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;

public class SysTagDisplayDao extends GenericDao<SysTagDisplay> {

    protected static final Logger log = Logger.getLogger(SysTagDisplayDao.class.getName());
    
    public SysTagDisplayDao() {
        super(SysTagDisplay.class);
    }    

    public List<SysTagDisplay> findFrontpage(long msoId, short type, String lang) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<SysTagDisplay> detached = new ArrayList<SysTagDisplay>();
        try {
            /*
            select * 
              from systag_display a1   
            inner join 
            (select d.id  
               from systag s, systag_display d  
              where s.msoId = 1 
                and s.type = 4 
                and d.lang = 'zh'
                and s.id = d.systagId
               order by s.seq) a2 
            on a1.id=a2.id
            */
            String sql = " select * from systag_display a1 " +
                         "  inner join " +
                         "(select d.id " + 
                           " from systag s, systag_display d " +
                         " where s.msoId = " + msoId + 
                           " and s.type = " + type +
                           " and d.lang = '" + lang + "'" +
                           " and s.id = d.systagId " +                           
                         " order by s.seq) a2" +
                         " on a1.id=a2.id";
            log.info("Sql=" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(SysTagDisplay.class);
            @SuppressWarnings("unchecked")
            List<SysTagDisplay> results = (List<SysTagDisplay>) q.execute();
            detached = (List<SysTagDisplay>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;                
        
    }
    
    public List<SysTagDisplay> findDayparting(short baseTime, String lang, long msoId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<SysTagDisplay> detached = new ArrayList<SysTagDisplay>();
        try {
            /*
            select * 
              from systag_display a1   
            inner join 
            (select d.id  
               from systag s, systag_display d  
              where s.msoId = 1 
                and type = 3 
                and lang = 'en'
                and s.id = d.systagId  
                and (((s.timeStart != 0 or s.timeEnd != 0) and s.timeEnd > 3 and s.timeStart <= 3) or (s.timeStart = 0 and s.timeEnd = 0))  
               order by s.seq) a2 
            on a1.id=a2.id
            */
            String sql = " select * from systag_display a1 " +
                         "  inner join " +
                         "(select d.id " + 
                           " from systag s, systag_display d " +
                         " where s.msoId = " + msoId + 
                           " and type = " + SysTag.TYPE_DAYPARTING +
                           " and lang = '" + lang + "'" +
                           " and s.id = d.systagId " +                           
                           " and (((s.timeStart != 0 or s.timeEnd != 0) and s.timeEnd > " + baseTime + " and s.timeStart <= " + baseTime + ")" +
                                 " or (s.timeStart = 0 and s.timeEnd = 0)) " + 
                         " order by s.seq) a2" +
                         " on a1.id=a2.id";
            log.info("Sql=" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(SysTagDisplay.class);
            @SuppressWarnings("unchecked")
            List<SysTagDisplay> results = (List<SysTagDisplay>) q.execute();
            detached = (List<SysTagDisplay>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;                
    }
            
    public List<SysTagDisplay> findRecommendedSets(String lang, long msoId, short type) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<SysTagDisplay> detached = new ArrayList<SysTagDisplay>();
        try {
            /*
            select * 
              from systag_display a1  
            inner join 
            (select d.*  
               from systag s, systag_display d  
              where s.msoId = 1 
                and s.type = 2 
                and s.id = d.systagId 
                and featured = true  
                and d.lang='en') a2 
            on a1.id=a2.id
            */
            String sql = " select * from systag_display a1 " +
                         " inner join " +
                         "(select d.id " + 
                          " from systag s, systag_display d " +
                         " where s.msoId = " + msoId + "" +
                           " and s.type = " + type + 
                           " and s.id = d.systagId " +
                           " and featured = true " +
                           " and d.lang='" + lang + "') a2" +
                           " on a1.id=a2.id";
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(SysTagDisplay.class);
            @SuppressWarnings("unchecked")
            List<SysTagDisplay> results = (List<SysTagDisplay>) q.execute();            
            detached = (List<SysTagDisplay>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;                
    }
    
    public List<SysTagDisplay> findPlayerCategories(String lang, long msoId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<SysTagDisplay> detached = new ArrayList<SysTagDisplay>();
        try {
            String sql = " select * from systag_display a1 " +
                         " inner join " +
                         "(select d.id " + 
                          " from systag s, systag_display d " +
                         " where s.msoId = " + msoId + "" +
                           " and s.type = " + SysTag.TYPE_CATEGORY + 
                           " and s.id = d.systagId " +
                           " and d.lang='" + lang + "') a2" +
                           " on a1.id=a2.id";
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(SysTagDisplay.class);
            @SuppressWarnings("unchecked")
            List<SysTagDisplay> results = (List<SysTagDisplay>) q.execute();            
            detached = (List<SysTagDisplay>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;        
    }    

    //will need mso in the future to avoid name conflicts
    public SysTagDisplay findByName(String name) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        SysTagDisplay detached = null;
        try {
            Query query = pm.newQuery(SysTagDisplay.class);
            query.setFilter("name == " + NnStringUtil.escapedQuote(name));
            @SuppressWarnings("unchecked")
            List<SysTagDisplay> results = (List<SysTagDisplay>) query.execute();            
            if (results.size() > 0) {
                detached = pm.detachCopy(results.get(0));
            }
        } finally {
            pm.close();
        }
        return detached;
    }
    
    public SysTagDisplay findBySysTagId(long sysTagId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        SysTagDisplay detached = null;
        try {
            Query query = pm.newQuery(SysTagDisplay.class);
            query.setFilter("systagId == " + sysTagId);
            @SuppressWarnings("unchecked")
            List<SysTagDisplay> results = (List<SysTagDisplay>) query.execute();            
            if (results.size() > 0) {
                detached = pm.detachCopy(results.get(0));
            }
        } finally {
            pm.close();
        }
        return detached;
    }
    
    public SysTagDisplay findBySysTagIdAndLang(Long sysTagId, String lang) {
    
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        SysTagDisplay detached = null;
        try {
            Query query = pm.newQuery(SysTagDisplay.class);
            query.setFilter("systagId == sysTagIdParam && lang == langParam");
            query.declareParameters("long sysTagIdParam, String langParam");
            @SuppressWarnings("unchecked")
            List<SysTagDisplay> results = (List<SysTagDisplay>) query.execute(sysTagId, lang);
            if (results.size() > 0) {
                detached = pm.detachCopy(results.get(0));
            }
        } finally {
            pm.close();
        }
        return detached;
    }
}
