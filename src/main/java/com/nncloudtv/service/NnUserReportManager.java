package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnUserReportDao;
import com.nncloudtv.model.NnDevice;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserReport;

public class NnUserReportManager {
    protected static final Logger log = Logger.getLogger(NnUserReportManager.class.getName());
    
    private NnUserReportDao reportDao = new NnUserReportDao();
    
    public NnUserReport save(NnUser user, NnDevice device, String session, String type, String item, String comment) {
        NnUserReport report = new NnUserReport(user, device, session, type, item, comment);
        report.setCreateDate(new Date());
        reportDao.save(report);
        return report;
    }
    
    public List<NnUserReport> findAll() {
        return reportDao.findAll(); 
    }
    
    public List<NnUserReport> findSince(Date since) {
        return reportDao.findSince(since);
    }
    
    public List<NnUserReport> findByUser(String token) {
        return reportDao.findByUser(token);
    }

    public List<NnUserReport> findByUserSince(String token, Date since) {
        return reportDao.findByUserSince(token, since);
    }
    
}

