package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnContent;

public class NnContentDao extends GenericDao<NnContent> {

    public NnContentDao() {
        super(NnContent.class);
    }
    
    public NnContent findByItemAndLang(String item, String lang) {
        NnContent content = null;
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        try {
            Query query = pm.newQuery(NnContent.class);
            query.setFilter("item == itemParam && lang == langParam");
            query.declareParameters("String itemParam, String langParam");
            @SuppressWarnings("unchecked")
            List<NnContent> results = (List<NnContent>) query.execute(item, lang);
            if (results.size() > 0) {        
                content = (NnContent) pm.detachCopy(results.get(0));
            }
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return content;
    }

    public List<NnContent> findAll() {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnContent> detached = new ArrayList<NnContent>();
        try {
            Query query = pm.newQuery(NnContent.class);
            @SuppressWarnings("unchecked")
            List<NnContent> results = (List<NnContent>) query.execute();
            detached = (List<NnContent>)pm.detachCopyAll(results);
        } finally {
            pm.close();
        }
        return detached;        
    }
}
