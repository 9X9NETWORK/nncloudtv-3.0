package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnChannelPref;

public class NnChannelPrefDao extends GenericDao<NnChannelPref> {

	protected static final Logger log = Logger.getLogger(NnChannelPref.class.getName());
	
	public NnChannelPrefDao() {
		super(NnChannelPref.class);
	}	
	
	public List<NnChannelPref> findByChannelId(long channelId) {
		
		List<NnChannelPref> prefList = new ArrayList<NnChannelPref>();
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		
		try {
			Query query = pm.newQuery(NnChannelPref.class);
			query.setFilter("channelId == channelIdParam");
			query.declareParameters("long channelIdParam");
			@SuppressWarnings("unchecked")
			List<NnChannelPref> results = (List<NnChannelPref>) query
			        .execute(channelId);
			prefList = (List<NnChannelPref>) pm.detachCopyAll(results);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return prefList;
	}
	
	public NnChannelPref findByChannelIdAndItem(long channelId, String item) {
		
		NnChannelPref pref = null;
		PersistenceManager pm = PMF.getNnUser1().getPersistenceManager();
		
		try {
			Query query = pm.newQuery(NnChannelPref.class);
			query.setFilter("channelId == channelIdParam && item == itemParam");
			query.declareParameters("long channelIdParam, String itemParam");
			@SuppressWarnings("unchecked")
			List<NnChannelPref> results = (List<NnChannelPref>) query.execute(channelId, item);
			if (results.size() > 0) {		
				pref = (NnChannelPref) pm.detachCopy(results.get(0));
			}
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return pref;
	}
	
}
