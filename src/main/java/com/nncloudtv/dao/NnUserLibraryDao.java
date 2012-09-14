package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnUserLibrary;

public class NnUserLibraryDao extends GenericDao<NnUserLibrary> {
	
	protected static final Logger log = Logger.getLogger(NnUserLibraryDao.class.getName());
	
	public NnUserLibraryDao() {
		super(NnUserLibrary.class);
	}
	
	public NnUserLibrary findByUserIdStr(String userIdStr) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		
		NnUserLibrary lib = null;
		try {
			
			Query query = pm.newQuery(NnUserLibrary.class);
			query.setFilter("userIdStr == userIdStrParam");
			query.declareParameters("String userIdStrParam");
			@SuppressWarnings("unchecked")
			List<NnUserLibrary> libs = (List<NnUserLibrary>) query
			        .execute(userIdStr);
			if (libs.size() > 0) {
				lib = pm.detachCopy(libs.get(0));
			}
		} finally {
			pm.close();
		}
		return lib;
	}
	
	public List<NnUserLibrary> findByUserIdStrAndType(String userIdStr, short type) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		
		List<NnUserLibrary> result = new ArrayList<NnUserLibrary>();
		try {
			Query query = pm.newQuery(NnUserLibrary.class);
			query.setFilter("userIdStr == userIdStrParam && type == typeParam");
			query.declareParameters("String userIdStrParam, short typeParam");
			query.setOrdering("updateDate desc");
			@SuppressWarnings("unchecked")
			List<NnUserLibrary> libs = (List<NnUserLibrary>) query.execute(
			        userIdStr, type);
			result = (List<NnUserLibrary>) pm.detachCopyAll(libs);
		} finally {
			pm.close();
		}
		return result;
	}
	
    public List<NnUserLibrary> findByUserIdStrAndType(String userIdStr, short type, short page, short rows) {

        PersistenceManager pm = PMF.getContent().getPersistenceManager();

        List<NnUserLibrary> result = new ArrayList<NnUserLibrary>();
        try {
            Query query = pm.newQuery(NnUserLibrary.class);
            query.setFilter("userIdStr == userIdStrParam && type == typeParam");
            query.declareParameters("String userIdStrParam, short typeParam");
            query.setOrdering("updateDate desc");
            query.setRange((page - 1) * rows, page * rows);
            @SuppressWarnings("unchecked")
            List<NnUserLibrary> libs = (List<NnUserLibrary>) query.execute(
                    userIdStr, type);
            result = (List<NnUserLibrary>) pm.detachCopyAll(libs);
        } finally {
            pm.close();
        }
        return result;
    }
	
	public NnUserLibrary findByUserIdStrAndTypeAndFileUrl(String userIdStr, short type,
            String fileUrl) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		
		NnUserLibrary result = null;
		try {
			Query query = pm.newQuery(NnUserLibrary.class);
			query.setFilter("userIdStr == userIdStrParam && type == typeParam && fileUrl == fileUrlParam");
			query.declareParameters("String userIdStrParam, short typeParam, String fileUrlParam");
			@SuppressWarnings("unchecked")
			List<NnUserLibrary> libs = (List<NnUserLibrary>) query
			        .execute(userIdStr, type, fileUrl);
			if (libs.size() > 0) {
				result = pm.detachCopy(libs.get(0));
			}
		} finally {
			pm.close();
		}
		return result;
    }
}
