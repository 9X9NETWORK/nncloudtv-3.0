package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnUserReport;

public class NnUserReportDao extends GenericDao<NnUserReport>{
    protected static final Logger log = Logger.getLogger(NnUserReportDao.class.getName());
    
    public NnUserReportDao() {
        super(NnUserReport.class);
    }

    public List<NnUserReport> findAll() {
        List<NnUserReport> detached = new ArrayList<NnUserReport>();
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        try {
            Query query = pm.newQuery(NnUserReport.class);        
            @SuppressWarnings("unchecked")
            List<NnUserReport> results = (List<NnUserReport>) query.execute();
            detached = (List<NnUserReport>)pm.detachCopyAll(results);            
        } finally {
            pm.close();
        }
        return detached;                
    }

    
    public NnUserReport save(NnUserReport report) {
        if (report == null) {return null;}
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        try {
            pm.makePersistent(report);            
            report = pm.detachCopy(report);
        } finally {
            pm.close();
        }
        return report;
    }
    
    public List<NnUserReport> findSince(Date since) {
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        List<NnUserReport> detached = new ArrayList<NnUserReport>();
        try {
            Query query = pm.newQuery(NnUserReport.class);
            query.setFilter("createDate > createDateParam");
            query.declareImports("import java.util.Date");
            query.declareParameters("Date createDateParam");             
             
            @SuppressWarnings("unchecked")
            List<NnUserReport> results = (List<NnUserReport>) query.execute(since);
            detached = (List<NnUserReport>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;        
    }
    
    public List<NnUserReport> findByUser(String token) {
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        List<NnUserReport> detached = new ArrayList<NnUserReport>();
        try {
            Query query = pm.newQuery(NnUserReport.class);
            query.setFilter("userToken == tokenParam");
            query.declareParameters("String tokenParam");                
            @SuppressWarnings("unchecked")
            List<NnUserReport> results = (List<NnUserReport>) query.execute(token);
            detached = (List<NnUserReport>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;        
    }

    public List<NnUserReport> findByUserSince(String token, Date since) {
        PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
        List<NnUserReport> detached = new ArrayList<NnUserReport>();
        try {
            Query query = pm.newQuery(NnUserReport.class);
            query.declareImports("import java.util.Date");
            query.setFilter("userToken == tokenParam && createDate > dateParam");
            query.declareParameters("String tokenParam, Date dateParam");                
            @SuppressWarnings("unchecked")
            List<NnUserReport> results = (List<NnUserReport>) query.execute(token, since);
            detached = (List<NnUserReport>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;        
    }
    
}

