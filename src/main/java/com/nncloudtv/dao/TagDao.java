package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Tag;
import com.nncloudtv.model.TagMap;

public class TagDao extends GenericDao<Tag> {
        
    public TagDao() {
        super(Tag.class);
    }

    public TagMap saveMap(TagMap tm) {
        if (tm == null) {return null;}
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            pm.makePersistent(tm);
            tm = pm.detachCopy(tm);
        } finally {
            pm.close();
        }
        return tm;
    }
    
    public void deleteTagMap(TagMap map) {
        if (map == null) return;
        PersistenceManager pm = PMF.get(map.getClass()).getPersistenceManager();
        try {
            pm.deletePersistent(map);
        } finally {
            pm.close();
        }
    }
    
    public Tag findByName(String name) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        Tag detached = null;
        try {
            Query query = pm.newQuery(Tag.class);
            query.setFilter("name == " + NnStringUtil.escapedQuote(name));
            @SuppressWarnings("unchecked")
            List<Tag> results = (List<Tag>) query.execute();            
            if (results.size() > 0) {
                detached = pm.detachCopy(results.get(0));
            }
        } finally {
            pm.close();
        }
        return detached;
    }
        
    public List<TagMap> findMapByChannel(long chId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<TagMap> detached = new ArrayList<TagMap>();
        try {
            Query query = pm.newQuery(TagMap.class);
            query.setFilter("channelId == channelIdParam");
            query.declareParameters("long channelIdParam");
            @SuppressWarnings("unchecked")
            List<TagMap> results = (List<TagMap>) query.execute(chId);
            detached = (List<TagMap>)pm.detachCopyAll(results);            
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return detached;            	
    }
    
    public List<TagMap> findMapByTag(long tagId) {        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<TagMap> detached = new ArrayList<TagMap>();
        try {
            Query query = pm.newQuery(TagMap.class);
            query.setFilter("tagId == tagIdParam");
            query.declareParameters("long tagIdParam");
            @SuppressWarnings("unchecked")
            List<TagMap> results = (List<TagMap>) query.execute(tagId);
            detached = (List<TagMap>)pm.detachCopyAll(results);            
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return detached;        
    }

    public TagMap findMapByTagAndChannel(long tagId, long channelId) {        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        TagMap detached = null;
        try {
            Query query = pm.newQuery(TagMap.class);
            query.setFilter("tagId == tagIdParam && channelId == channelIdParam");
            query.declareParameters("long tagIdParam, long channelIdParam");
            @SuppressWarnings("unchecked")
            List<TagMap> results = (List<TagMap>) query.execute(tagId, channelId);
            if (results.size() > 0)
                detached = pm.detachCopy(results.get(0));            
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return detached;        
    }
    
}
