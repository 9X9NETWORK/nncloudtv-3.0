package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagMap;

public class SysTagMapDao extends GenericDao<SysTagMap> {
    
    protected static final Logger log = Logger.getLogger(SysTagMapDao.class.getName());
    
    public SysTagMapDao() {
        super(SysTagMap.class);
    }
    
    public SysTagMap findBySysTagIdAndChannelId(long sysTagId, long channelId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        SysTagMap detached = null;
        
        try {
            String sql = " select * from systag_map where sysTagId = " + sysTagId +
                           " and channelId = " + channelId;
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(SysTagMap.class);
            @SuppressWarnings("unchecked")
            List<SysTagMap> results = (List<SysTagMap>) q.execute();
            if (results != null && results.size() > 0) {
                detached = pm.detachCopy(results.get(0));
            }
        } finally {
            pm.close();
        }
        
        return detached;
    }
    
    public List<SysTagMap> findCategoryMapsByChannelId(long channelId, long msoId) {
        
        String query = " select * from systag_map a1"
                     + " inner join (select m.id from systag s, systag_map m"
                     +             " where s.type = "      + SysTag.TYPE_CATEGORY
                     +               " and s.msoId = "     + msoId
                     +               " and m.channelId = " + channelId
                     +               " and s.id = m.systagId) a2 on a1.id = a2.id";
        
        return sql(query);
    }
    
    public List<SysTagMap> findBySysTagId(long sysTagId) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<SysTagMap> detached = new ArrayList<SysTagMap>();
        
        try {
            String sql = " select * from systag_map where sysTagId = " + sysTagId +
                           " order by seq asc";
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(SysTagMap.class);
            @SuppressWarnings("unchecked")
            List<SysTagMap> results = (List<SysTagMap>) q.execute();
            if (results != null && results.size() > 0) {
                detached = (List<SysTagMap>) pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        
        return detached;
    }

}
