package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnChannel;

/** put all sql operations in this dao used by store management */
public class StoreDao {
    
    protected static final Logger log = Logger.getLogger(StoreDao.class.getName());
    
    /** get channels from official store's category */
    public List<NnChannel> getStoreChannelsFromCategory(long categoryId, List<String> spheres) { // TODO : can't promise categoryId is true
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        
        try {
            String filter = "";
            if (spheres != null && spheres.size() > 0) {
                //filter = "sphere = '" + lang + "' or c.sphere =";
                filter = " and ( c.sphere = '" + LangTable.OTHER + "'";
                for (String sphere : spheres) {
                    filter = filter + " or c.sphere = '" + sphere + "'";
                }
                filter = filter + " )";
            }
            
            String sorting = " order by c.updateDate desc";
            String sql = "select * from nnchannel a1 " +
                         " inner join " +
                       " (select distinct c.id " +
                          " from systag_display d, systag_map m, nnchannel c " +
                         " where d.systagId = " + categoryId +
                           " and d.systagId = m.systagId " +
                           " and c.id = m.channelId " +
                           " and c.isPublic = true" +
                           " and c.contentType != " + NnChannel.CONTENTTYPE_FAVORITE +
                           " and c.status = " + NnChannel.STATUS_SUCCESS +
                           filter +
                           sorting +
                           ") a2 on a1.id=a2.id";
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnChannel.class);
            @SuppressWarnings("unchecked")
            List<NnChannel> results = (List<NnChannel>) q.execute(); 
            if (results != null && results.size() > 0) {
                detached = (List<NnChannel>)pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        return detached;                
    }
    
    /** get channels from official store */
    public List<NnChannel> getStoreChannels(List<String> spheres) {
        
        PersistenceManager pm = PMF.getContent().getPersistenceManager();
        List<NnChannel> detached = new ArrayList<NnChannel>();
        
        try {
            String filter = "";
            if (spheres != null && spheres.size() > 0) {
                //filter = "sphere = '" + lang + "' or c.sphere =";
                filter = " and ( sphere = '" + LangTable.OTHER + "'";
                for (String sphere : spheres) {
                    filter = filter + " or sphere = '" + sphere + "'";
                }
                filter = filter + " )";
            }
            
            String sql = "select * from nnchannel where isPublic = true" +
                           " and status = " + NnChannel.STATUS_SUCCESS +
                           " and contentType != " + NnChannel.CONTENTTYPE_FAVORITE +
                           filter +
                           " order by updateDate desc";
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnChannel.class);
            @SuppressWarnings("unchecked")
            List<NnChannel> results = (List<NnChannel>) q.execute(); 
            if (results != null && results.size() > 0) {
                detached = (List<NnChannel>)pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        return detached;                
    }

}
