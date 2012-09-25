package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.TitleCard;

public class TitleCardDao extends GenericDao<TitleCard> {
    
    protected static final Logger log = Logger.getLogger(TitleCardDao.class.getName());
    
    public TitleCardDao() {
        super(TitleCard.class);
    }    
        
    public List<TitleCard> findByChannel(long channelId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<TitleCard> detached = new ArrayList<TitleCard>(); 
        try {
            Query q = pm.newQuery(TitleCard.class);
            q.setFilter("channelId == channelIdParam");
            q.declareParameters("long channelIdParam");
            q.setOrdering("programId");
            @SuppressWarnings("unchecked")
            List<TitleCard> cards = (List<TitleCard>) q.execute(channelId);
            detached = (List<TitleCard>)pm.detachCopyAll(cards);
        } finally {
            pm.close();
        }
        return detached;
    }    
    
    public List<TitleCard> findByProgramId(long programId) {
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<TitleCard> detached = new ArrayList<TitleCard>(); 
        try {
            Query q = pm.newQuery(TitleCard.class);
            q.setFilter("programId == " + programId);
            @SuppressWarnings("unchecked")
            List<TitleCard> cards = (List<TitleCard>) q.execute(programId);
            detached = (List<TitleCard>) pm.detachCopyAll(cards);
        } finally {
            pm.close();
        }
        return detached;
    }
    
    public TitleCard findByProgramIdAndType(long programId, short type) {
    
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        TitleCard result = null;
        try {
            Query query = pm.newQuery(TitleCard.class);
            query.setFilter("programId == programIdParam && type == typeParam");
            query.declareParameters("long programId, short typeParam");
            @SuppressWarnings("unchecked")
            List<TitleCard> titleCards = (List<TitleCard>) query.execute(
                    programId, type);
            if (titleCards.size() > 0) {
                result = pm.detachCopy(titleCards.get(0));
            }
        } finally {
            pm.close();
        }
        return result;
    }

}
