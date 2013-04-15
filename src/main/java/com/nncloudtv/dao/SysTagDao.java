package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
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

}
