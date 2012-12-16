package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnChannel;

public class NnChannelDao extends GenericDao<NnChannel> {
    
    protected static final Logger log = Logger.getLogger(NnChannelDao.class.getName());
    
    public NnChannelDao() {
        super(NnChannel.class);
    }    
        
    public List<NnChannel> findByType(short type) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>(); 
        try {
            Query q = pm.newQuery(NnChannel.class);
            q.setFilter("contentType == contentTypeParam");
            q.declareParameters("short contentTypeParam");
            @SuppressWarnings("unchecked")
            List<NnChannel> channels = (List<NnChannel>) q.execute(type);
            detached = (List<NnChannel>)pm.detachCopyAll(channels);
        } finally {
            pm.close();
        }
        return detached;
    }    
    
    public NnChannel save(NnChannel channel) {
        if (channel == null) {return null;}
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            pm.makePersistent(channel);            
            channel = pm.detachCopy(channel);
        } finally {
            pm.close();
        }
        return channel;
    }

    //find good channels, for all needs to be extended
    public List<NnChannel> findChannelsByTag(String name) {        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        try {
            /*
            String sql = "select id from nnchannel " +
                         " where id in " +
                          " (select channelId from tag_map " +
                            " where tagId = (select id from tag where name='" + name + "')) " +
                         "order by rand() limit 9";
            */
            String sql = "select * from nnchannel where id in ( " + 
                            "select distinct map.channelId " + 
                               "from ytprogram yt, tag_map map " + 
                              "where yt.channelId = map.channelId " +
                                "and map.tagId = (select id from tag where name= '" + name + "')) " +
                                "order by rand() limit 9;";
            log.info("Sql=" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnChannel.class);
            @SuppressWarnings("unchecked")
            List<NnChannel> results = (List<NnChannel>) q.execute();
            detached = (List<NnChannel>)pm.detachCopyAll(results);
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return detached;        
    }
    
    public NnChannel findById(long id) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();        
        NnChannel channel = null;
        try {
            channel = pm.getObjectById(NnChannel.class, id);
            channel = pm.detachCopy(channel);
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();            
        }
        return channel;        
    }    

    //replaced with Apache Lucene
    @SuppressWarnings("unchecked")
    public static List<NnChannel> search(String queryStr, boolean all, int start, int count) {        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        try {
            String sql = 
                "select * from nnchannel " +
                 "where (lower(name) like lower(\"%" + queryStr + "%\")" +
                    "|| lower(intro) like lower(\"%" + queryStr + "%\"))";
            if (!all) {
                sql += " and (status = " + NnChannel.STATUS_SUCCESS + " or status = " + NnChannel.STATUS_WAIT_FOR_APPROVAL + ")";
                sql += " and isPublic = true";
            }
            sql += " limit 9";        
            log.info("Sql=" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnChannel.class);
            List<NnChannel> results = (List<NnChannel>) q.execute();
            detached = (List<NnChannel>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }

        return detached;     
    }
    
    public List<NnChannel> findAll() {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        try {
            Query query = pm.newQuery(NnChannel.class);
            @SuppressWarnings("unchecked")
            List<NnChannel> results = (List<NnChannel>) query.execute();
            detached = (List<NnChannel>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;
    }
        
    public List<NnChannel> findAllByStatus(short status) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>(); 
        try {
            Query q = pm.newQuery(NnChannel.class);
            q.setFilter("status == statusParam");
            q.declareParameters("short statusParam");
            q.setOrdering("createDate asc");
            @SuppressWarnings("unchecked")
            List<NnChannel> channels = (List<NnChannel>) q.execute(status);
            detached = (List<NnChannel>)pm.detachCopyAll(channels);
        } finally {
            pm.close();
        }
        return detached;
    }    

    //select id from nnchannel where poolType > 10 order by rand() limit 10;
    public List<NnChannel> findSpecial(short type, String lang, int limit) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>(); 
        try {
            String sql = "select * " +
                          " from nnchannel " + 
            		     " where poolType >= " + type + 
                           " and (lang = '" + lang + " 'or lang = 'other')" + 
                           " order by rand()";
            if (limit > 0) 
                sql += " limit " + limit;
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnChannel.class);
            @SuppressWarnings("unchecked")
            List<NnChannel> channels = (List<NnChannel>) q.execute(type);
            detached = (List<NnChannel>)pm.detachCopyAll(channels);
        } finally {
            pm.close();
        }
        return detached;
    }
    
    @SuppressWarnings("unchecked")
    public List<NnChannel> findByUser(String userIdStr, int limit, boolean isAll) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> channels = new ArrayList<NnChannel>(); 
        try {
            if (isAll) {
                Query q = pm.newQuery(NnChannel.class);
                q.setOrdering("seq asc, contentType asc");
                q.setFilter("userIdStr == userIdStrParam");
                q.declareParameters("String userIdStrParam");
                if (limit != 0)
                    q.setRange(0, limit);            
                channels = (List<NnChannel>) q.execute(userIdStr);
            } else {                
                String sql = 
                    "select * from nnchannel " +
                     "where  userIdStr = '" + userIdStr + "' " +
                       " and isPublic=true " +
                       " and (status=0 or status=3) " +
                       " order by seq, contentType "; 
                if (limit != 0)
                    sql += " limit " + limit;
                log.info("Sql=" + sql);
                Query q= pm.newQuery("javax.jdo.query.SQL", sql);
                q.setClass(NnChannel.class);
                channels = (List<NnChannel>) q.execute();                                
            }
            
            channels = (List<NnChannel>)pm.detachCopyAll(channels);
        } finally {
            pm.close();
        }
        return channels;
    }    
    
    public NnChannel findBySourceUrl(String url) {
        if (url == null) {return null;}
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        NnChannel channel = null;
        try {
            Query q = pm.newQuery(NnChannel.class);
            q.setFilter("sourceUrl== sourceUrlParam");
            q.declareParameters("String sourceUrlParam");
            @SuppressWarnings("unchecked")
            //List<NnChannel> channels = (List<NnChannel>) q.execute(url.toLowerCase());
            List<NnChannel> channels = (List<NnChannel>) q.execute(url);
            if (channels.size() > 0) {
                channel = pm.detachCopy(channels.get(0));
            }
        } finally {
            pm.close();
        }
        return channel;                
    }        

    public NnChannel findFavorite(String userIdStr) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        NnChannel channel = null;
        try {
            Query q = pm.newQuery(NnChannel.class);
            q.setFilter("userIdStr == userIdStrParam && contentType == contentTypeParam");
            q.declareParameters("String userIdStrParam, short contentTypeParam");
            q.setOrdering("cntSubscribe");
            @SuppressWarnings("unchecked")
            List<NnChannel> channels = (List<NnChannel>) q.execute(userIdStr, NnChannel.CONTENTTYPE_FAVORITE);
            if (channels.size() > 0) {
                channel = pm.detachCopy(channels.get(0));
            }
        } finally {
            pm.close();
        }
        return channel;                
    }        

    @SuppressWarnings("unchecked")
    public List<NnChannel> findByIds(List<Long> ids) {
        List<NnChannel> channels = new ArrayList<NnChannel>();
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            Query q = pm.newQuery(NnChannel.class, ":p.contains(id)");
            q.setOrdering("updateDate desc");
            channels = ((List<NnChannel>) q.execute(ids));        
            channels = (List<NnChannel>) pm.detachCopyAll(channels);
        } finally {
            pm.close();
        }
        return channels;
    }
    
    
}
