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
        
    public List<TagMap> findMap(long id) {        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<TagMap> detached = new ArrayList<TagMap>();
        try {
            Query query = pm.newQuery(TagMap.class);
            query.setFilter("tagId == tagIdParam");
            query.declareParameters("long tagIdParam");
            @SuppressWarnings("unchecked")
            List<TagMap> results = (List<TagMap>) query.execute(id);
            detached = (List<TagMap>)pm.detachCopyAll(results);            
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return detached;        
    }
        
}
