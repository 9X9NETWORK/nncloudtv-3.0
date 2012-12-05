package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;

public class NnProgramDao extends GenericDao<NnProgram> {
    
    protected static final Logger log = Logger.getLogger(NnProgramDao.class.getName());    
        
    public NnProgramDao() {
        super(NnProgram.class);
    }
    
    public NnProgram save(NnProgram program) {
        if (program == null) {return null;}        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            pm.makePersistent(program);
            program = pm.detachCopy(program);
        } finally {
            pm.close();
        }
        return program;
    }
    
    public void delete(NnProgram program) {
        if (program == null) return;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            pm.deletePersistent(program);
        } finally {
            pm.close();
        }        
    }
    
    public NnProgram findByChannelAndFileUrl(long channelId, String fileUrl) {
        NnProgram detached = null;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();        
        try {
            Query query = pm.newQuery(NnProgram.class);
            query.setFilter("channelId == " + channelId + " & fileUrl == '" + fileUrl + "'");
            @SuppressWarnings("unchecked")
            List<NnProgram> results = (List<NnProgram>) query.execute(channelId, fileUrl);
            if (results.size() > 0) {
                detached = pm.detachCopy(results.get(0));
            }
        } finally {
            pm.close();
        }
        return detached;
    }
    
    public NnProgram findByStorageId(String storageId) {
        NnProgram detached = null;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();        
        try {
            Query query = pm.newQuery(NnProgram.class);
            query.setFilter("storageId == '" + storageId + "'");
            @SuppressWarnings("unchecked")
            List<NnProgram> results = (List<NnProgram>) query.execute();
            if (results.size() > 0) {
                detached = pm.detachCopy(results.get(0));
            }
        } finally {
            pm.close();
        }
        return detached;
    }

    //IMPORTANT: applies to 9x9 channel only. otherwise ordering could be wrong
    public List<NnProgram> findByChannelAndSeq(long channelId, String seq) {
        
        List<NnProgram> detached = new ArrayList<NnProgram>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        
        try {
            Query query = pm.newQuery(NnProgram.class);
            query.setFilter("channelId == channelIdParam && seq == seqParam");
            query.declareParameters("long channelIdParam, String seqParam");
            query.setOrdering("seq asc, subSeq asc");
            @SuppressWarnings("unchecked")
            List<NnProgram> results = (List<NnProgram>) query.execute(
                    channelId, seq);
            detached = (List<NnProgram>) pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;
    }

    public List<NnProgram> findProgramByChannelByChannelAndSeq(long channelId, String seq) {
        List<NnProgram> detached = new ArrayList<NnProgram>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();        
        try {
            Query query = pm.newQuery(NnProgram.class);
            query.setFilter("channelId == " + channelId + " & seq == '" + seq + "'");
            log.info("ordering by seq, subSeq asc");

            @SuppressWarnings("unchecked")
            List<NnProgram> results = (List<NnProgram>) query.execute(channelId, seq);
            detached = (List<NnProgram>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;        
    }
    
    public NnProgram findByChannelAndStorageId(long channelId, String storageId) {
        NnProgram detached = null;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();        
        try {
            Query query = pm.newQuery(NnProgram.class);
            query.setFilter("channelId == " + channelId + " && storageId == '" + storageId + "'");
            @SuppressWarnings("unchecked")
            List<NnProgram> results = (List<NnProgram>) query.execute();
            if (results.size() > 0) {
                detached = pm.detachCopy(results.get(0));
            }
        } finally {
            pm.close();
        }
        return detached;
    }
    
    
    public NnProgram findFavorite(long channelId, String fileUrl) {
        NnProgram detached = null;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();        
        try {
            Query query = pm.newQuery(NnProgram.class);
            query.setFilter("channelId == " + channelId + " && fileUrl == '" + fileUrl + "'");
            @SuppressWarnings("unchecked")
            List<NnProgram> results = (List<NnProgram>) query.execute();
            if (results.size() > 0) {
                detached = pm.detachCopy(results.get(0));
            }
        } finally {
            pm.close();
        }
        return detached;
    }
    
    public List<NnProgram> findPlayerProgramsByChannels(List<Long> channelIds) {
        List<NnProgram> good = new ArrayList<NnProgram>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            Query q = pm.newQuery(NnProgram.class, ":p.contains(channelId)");
            q.setOrdering("channelId asc");
            @SuppressWarnings("unchecked")
            List<NnProgram> programs = ((List<NnProgram>) q.execute(channelIds));        
            good = (List<NnProgram>) pm.detachCopyAll(programs);
            for (NnProgram p : programs) {
                  if (p.isPublic() && p.getStatus() != NnProgram.STATUS_OK) {
                      good.add(p);
                  }            
            }
        } finally {
            pm.close();
        }
        return good;
    }
        
    public List<NnProgram> findPlayerProgramsByChannel(NnChannel c) {
        List<NnProgram> detached = new ArrayList<NnProgram>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            String ordering = "seq asc, subSeq asc"; 
            if (c.getContentType() == NnChannel.CONTENTTYPE_MAPLE_SOAP) {
                ordering = "seq asc, subSeq asc"; 
            } else if (c.getContentType() == NnChannel.CONTENTTYPE_MAPLE_VARIETY) {
                ordering = "seq desc, subSeq asc";    
            } else if (c.getContentType() == NnChannel.CONTENTTYPE_MIXED) {
                ordering = "seq asc, subSeq asc";
            } else if (c.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_SPECIAL_SORTING) {
                ordering = "seq desc, subSeq asc";                
            } else {
                ordering = "updateDate desc";
            }
            log.info("ordering:" + ordering);
            String sql = "select * " +
                           "from nnprogram " +
                          "where channelId = " + c.getId() + " " + 
                            "and isPublic = true " +
                            "and status != " + NnProgram.STATUS_ERROR + " " +
             "order by " + ordering;

            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnProgram.class);
            @SuppressWarnings("unchecked")
            List<NnProgram> programs = (List<NnProgram>) q.execute();
            detached = (List<NnProgram>)pm.detachCopyAll(programs);            
        } finally {
            pm.close();
        }
        return detached;
    }

    public List<NnProgram> findByChannel(long channelId) {
        
        log.info("find by channelId = " + channelId);
        
        List<NnProgram> detached = new ArrayList<NnProgram>();
        
        if (channelId == 0) {
            return detached;
        }
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            Query q = pm.newQuery(NnProgram.class);
            q.setFilter("channelId == channelIdParam");
            q.declareParameters("long channelIdParam");
            q.setOrdering("seq, subSeq asc");
            @SuppressWarnings("unchecked")
            List<NnProgram> programs = (List<NnProgram>)q.execute(channelId);        
            detached = (List<NnProgram>)pm.detachCopyAll(programs);
        } finally {
            pm.close();
        }
        return detached;
    }    
    
    public NnProgram findById(long id) {
        NnProgram program = null;
        if (id == 0) return program;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            program = pm.getObjectById(NnProgram.class, id);
            program = pm.detachCopy(program);
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return program;        
    }    

    public List<NnProgram> findPlayerNnProgramsByChannel(long channelId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnProgram> detached = new ArrayList<NnProgram>();
        try {
            String sql = "select * " +
                           "from nnprogram " +
                         " where episodeId in " +
                          "(select id from nnepisode where channelId = " + channelId + " and isPublic=true order by seq) " +
                          " and status = 0 " + 
                          "order by seq, subSeq"; //TODO, order by seq is wrong
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(NnProgram.class);
            @SuppressWarnings("unchecked")
            List<NnProgram> results = (List<NnProgram>) query.execute();
            detached = (List<NnProgram>)pm.detachCopyAll(results);
        } finally {            
            pm.close();
        } 
        return detached;
        
    }
    
    
    //based on one program id to find all sub-episodes belong to the same episode
    //use scenario: such as "reference" lookup
    public List<NnProgram> findProgramsByEpisode(long episodeId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnProgram> detached = new ArrayList<NnProgram>(); 
        try {
            String sql = "select id " +
            		       "from nnprogram " +
            		      "where episodeId = " + episodeId + " " +
            		      "order by subSeq"; // seq is not be maintain anymore
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnProgram.class);
            @SuppressWarnings("unchecked")
            List<NnProgram> programs = (List<NnProgram>) q.execute();
            detached = (List<NnProgram>)pm.detachCopyAll(programs);
        } finally {
            pm.close();
        }
        return detached;        
    }
    
    public List<NnProgram> findByChannels(List<NnChannel> channels) {
        List<NnProgram> detached = new ArrayList<NnProgram>();
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
                           "from nnprogram " +
                         " where channelId in (" + ids + ") " + 
                         " order by updateDate desc limit 50";
            log.info("sql:" + sql);
            Query query = pm.newQuery("javax.jdo.query.SQL", sql);
            query.setClass(NnProgram.class);
            @SuppressWarnings("unchecked")
            List<NnProgram> results = (List<NnProgram>) query.execute();
            detached = (List<NnProgram>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        } 
        return detached;        
    }
    
}
