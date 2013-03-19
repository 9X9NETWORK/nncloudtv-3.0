package com.nncloudtv.dao;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.ContentOwnership;

public class ContentOwnershipDao extends GenericDao<ContentOwnership> {
    
    protected static final Logger log = Logger.getLogger(ContentOwnershipDao.class.getName());
    
    public ContentOwnershipDao() {
        super(ContentOwnership.class);
    }
    
    public List<ContentOwnership> findByMsoIdAndContentType(long msoId, short contentType) {
        
        PersistenceManager pm = PMF.get(ContentOwnership.class).getPersistenceManager();
        List<ContentOwnership> detached = new ArrayList<ContentOwnership>();
        
        log.info("msoId = " + msoId + ", contentType = " + contentType);
        
        try {
            Query query = pm.newQuery(ContentOwnership.class);
            query.setFilter("msoId == msoIdParam && contentType == contentTypeParam");
            query.declareParameters("long msoIdParam, short contentTypeParam");
            @SuppressWarnings("unchecked")
            List<ContentOwnership> list = (List<ContentOwnership>)query.execute(msoId, contentType);
            if (list.size() > 0)
                detached = (List<ContentOwnership>)pm.detachCopyAll(list);
            log.info("found = " + list.size());
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        
        return detached;
    }

    public List<ContentOwnership> findAllByMsoId(long msoId) {        
        PersistenceManager pm = PMF.get(ContentOwnership.class).getPersistenceManager();
        List<ContentOwnership> detached = new ArrayList<ContentOwnership>();
        
        try {
            Query query = pm.newQuery(ContentOwnership.class);
            query.setFilter("msoId == msoIdParam");
            query.declareParameters("long msoIdParam");
            @SuppressWarnings("unchecked")
            List<ContentOwnership> list = (List<ContentOwnership>)query.execute(msoId);
            if (list.size() > 0)
                detached = (List<ContentOwnership>)pm.detachCopyAll(list);
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        
        return detached;
    }
    
    public ContentOwnership findByMsoIdAndChannelId(long msoId, long channelId) {        
        PersistenceManager pm = PMF.get(ContentOwnership.class).getPersistenceManager();
        ContentOwnership ownership = null;        
        try {
            Query query = pm.newQuery(ContentOwnership.class);
            query.setFilter("msoId == msoIdParam && contentType == contentTypeParam && contentId == contentIdParam");
            query.declareParameters("long msoIdParam, short contentTypeParam, long contentIdParam");
            @SuppressWarnings("unchecked")
            List<ContentOwnership> list = (List<ContentOwnership>)query.execute(msoId, ContentOwnership.TYPE_CHANNEL, channelId);
            if (list.size() > 0)
                ownership = pm.detachCopy(list.get(0));
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        
        return ownership;
    }
}
