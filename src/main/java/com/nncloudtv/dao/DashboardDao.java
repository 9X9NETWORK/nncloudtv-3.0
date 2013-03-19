package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Dashboard;

public class DashboardDao  extends GenericDao<Dashboard> {
    
    public DashboardDao() {
        super(Dashboard.class);
    }

    public List<Dashboard> findFrontpage(short baseTime) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<Dashboard> detached = new ArrayList<Dashboard>();
        try {
            String sql = "select * from dashboard " +
            		     " where ((timeStart != 0 or timeEnd != 0) and timeEnd > " + baseTime + " and timeStart <= " + baseTime + ")" +
            		     " or (timeStart = 0 and timeEnd = 0) order by seq";
            log.info("Sql=" + sql);
            //String sql = "select * from dashboard order by seq";
            //select * from dashboard order by seq, timeStart;
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(Dashboard.class);
            @SuppressWarnings("unchecked")
            List<Dashboard> results = (List<Dashboard>) q.execute();
            detached = (List<Dashboard>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;        
    } 
       
    /*
    public List<Dashboard> findFrontpage(short baseTime) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<Dashboard> detached = new ArrayList<Dashboard>();
        try {
            Query q = pm.newQuery(Dashboard.class);
            @SuppressWarnings("unchecked")
            List<Dashboard> items = (List<Dashboard>) q.execute();
            detached = (List<Dashboard>)pm.detachCopyAll(items);
        } finally {
            pm.close();
        }
        return detached;        
    } 
    */
    
    
}
