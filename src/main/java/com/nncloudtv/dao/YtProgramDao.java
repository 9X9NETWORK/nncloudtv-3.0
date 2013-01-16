package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.YtProgram;

public class YtProgramDao extends GenericDao<YtProgram> {
    
    protected static final Logger log = Logger.getLogger(YtProgramDao.class.getName());    
        
    public YtProgramDao() {
        super(YtProgram.class);
    }    

    public List<YtProgram> findByChannels(List<NnChannel> channels) {
        List<YtProgram> detached = new ArrayList<YtProgram>();
        String ids = "";
        for (NnChannel c : channels) {
            ids += "," + String.valueOf(c.getId());
        }
        if (ids.length() == 0) return detached;
        if (ids.length() > 0) ids = ids.replaceFirst(",", "");
        log.info("find in these channels:" + ids);
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            String sql = "select * " +
                           "from ytprogram " +
                         " where channelId in (" + ids + ") " + 
                         " order by updateDate desc limit 50";
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(YtProgram.class);
            @SuppressWarnings("unchecked")
            List<YtProgram> results = (List<YtProgram>) query.execute();
            detached = (List<YtProgram>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        } 
        return detached;        
    }

    public YtProgram findByVideo(String video) { 
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        YtProgram detached = null; 
        try {
            Query q = pm.newQuery(YtProgram.class);
            q.setFilter("ytVideoId == videoParam");
            q.declareParameters("String videoParam");
            @SuppressWarnings("unchecked")
            List<YtProgram> programs = (List<YtProgram>) q.execute(video);
            if (programs.size() > 0) {
                detached = pm.detachCopy(programs.get(0));
            }            
        } finally {
            pm.close();
        }
        return detached;
    }
    
}
