package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;

public class SysTagDisplayDao extends GenericDao<SysTagDisplay> {

    protected static final Logger log = Logger.getLogger(SysTagDisplayDao.class.getName());
    
    public SysTagDisplayDao() {
        super(SysTagDisplay.class);
    }    

    public SysTagDisplay findDisplayById(long id) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();        
        SysTagDisplay display = null;
        try {
            display = pm.getObjectById(SysTagDisplay.class, id);
            display = pm.detachCopy(display);
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();            
        }
        return display;        
    }    
        
    public List<SysTagDisplay> findRecommendedSets(String lang, long msoId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<SysTagDisplay> detached = new ArrayList<SysTagDisplay>();
        try {
            String sql = " select * from systag_display a1 " +
                         " inner join " +
                         "(select d.* " + 
                          " from systag s, systag_display d " +
                         " where s.msoId = " + msoId + "" +
                           " and s.type = " + SysTag.TYPE_SET + 
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
                         "(select d.* " + 
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

    public List<NnChannel> findChannelsById(long id) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        try {
            String sql = "select * from nnchannel a1 " +
                         " inner join " + 
                       " (select c.* " + 
                          " from systag_display d, systag_map m, nnchannel c " +
                         " where d.id = " + id + 
                         " and d.systagId = m.systagId " +                           
                           " and c.id = m.channelId " +
                           " and c.status = " + NnChannel.STATUS_SUCCESS +
                           " and c.isPublic = true) a2" +
                           " on a1.id=a2.id";
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
    
}
