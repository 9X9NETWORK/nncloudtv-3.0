package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnSetChannel;

public class NnSetChannelDao extends GenericDao<NnSetChannel> {
	
	protected static final Logger logger = Logger.getLogger(NnSetChannel.class.getName());
	
	public NnSetChannelDao() {
		super(NnSetChannel.class);
	}
	
	public List<NnSetChannel> findBySetId(long setId) {	
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		List<NnSetChannel> detached = new ArrayList<NnSetChannel>();
		
		try {
			Query query = pm.newQuery(NnSetChannel.class);
			query.setFilter("setId == setIdParam");
			query.declareParameters("long setIdParam");
			@SuppressWarnings("unchecked")
			List<NnSetChannel> list = (List<NnSetChannel>)query.execute(setId);
			detached = (List<NnSetChannel>)pm.detachCopyAll(list);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return detached;
	}
	
	public NnSetChannel findBySetIdAndSeq(long setId, int seq) {
		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		NnSetChannel result = null;
		
		try {
			Query query = pm.newQuery(NnSetChannel.class);
			query.setFilter("setId == setIdParam && seq == seqParam");
			query.declareParameters("long setIdParam, int seqParam");
			@SuppressWarnings("unchecked")
			List<NnSetChannel> list = (List<NnSetChannel>)query.execute(setId, seq);
			if (list.size() > 0)
				result = pm.detachCopy(list.get(0));
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return result;
	}
}
