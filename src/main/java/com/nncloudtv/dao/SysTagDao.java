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
    public List<NnChannel> findPlayerChannelsById(long id) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        try {
            /*
            select * 
              from nnchannel a1  
            inner join 
            (select c.*  
               from systag_display d, systag_map m, nnchannel c  
              where d.systagId = 56 
                and d.systagId = m.systagId 
                and c.id = m.channelId  
                and c.status = 0 
                and c.isPublic = true) a2 
            on a1.id=a2.id
            */            
            String sql = "select * from nnchannel a1 " +
                         " inner join " + 
                       " (select c.* " + 
                          " from systag_display d, systag_map m, nnchannel c " +
                         " where d.systagId = " + id + 
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
    
}
