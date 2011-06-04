package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;

public class NnProgramDao extends GenericDao<NnProgram> {
	
	protected static final Logger log = Logger.getLogger(NnProgramDao.class.getName());	
		
	public NnProgramDao() {
		super(NnProgram.class);
	}
	
	public NnProgram save(NnProgram program) {
		if (program == null) {return null;}		
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			pm.makePersistent(program);
			program = pm.detachCopy(program);
		} finally {
			pm.close();
		}
		return program;
	}
	
	public void delete(NnProgram program) {
		if (program == null) return;
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			pm.deletePersistent(program);
		} finally {
			pm.close();
		}		
	}

	public NnProgram findByStorageId(String storageId) {
		NnProgram detached = null;
		PersistenceManager pm = PMF.getContent().getPersistenceManager();		
		try {
			Query query = pm.newQuery(NnProgram.class);
			query.setFilter("storageId == '" + storageId + "'");
			@SuppressWarnings("unchecked")
			List<NnProgram> results = (List<NnProgram>) query.execute();
			if (results.size() > 0) {
				detached = pm.detachCopy(results.get(0));
			}
		} finally {
			pm.close();
		}
		return detached;
	}
	
	/**
	 * Good: reference findGoodProgramsByChannelId  
	 */
	public List<NnProgram> findGoodProgramsByChannelIds(List<Long> channelIds) {
		List<NnProgram> good = new ArrayList<NnProgram>();
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			Query q = pm.newQuery(NnProgram.class, ":p.contains(channelId)");
			q.setOrdering("channelId asc, pubDate desc");
			@SuppressWarnings("unchecked")
			List<NnProgram> programs = ((List<NnProgram>) q.execute(channelIds));		
			good = (List<NnProgram>) pm.detachCopyAll(programs);
			for (NnProgram p : programs) {
				  if (p.isPublic() && p.getStatus() != NnProgram.STATUS_OK && p.getType() == NnProgram.TYPE_VIDEO) {
					  good.add(p);
				  }			
			}
		} finally {
			pm.close();
		}
		return good;
	}
		
	/**
	 * Good: is Public, is STATUS_OK, is TYPE_VIDEO
	 */
	public List<NnProgram> findGoodProgramsByChannelId(long channelId) {
		List<NnProgram> detached = new ArrayList<NnProgram>();
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			Query q = pm.newQuery(NnProgram.class);
			q.setFilter("channelId == channelIdParam && status == statusParam && type == typeParam");
			q.declareParameters("long channelIdParam, short statusParam, short typeParam");
			q.setOrdering("pubDate desc");
			@SuppressWarnings("unchecked")
			List<NnProgram> programs = (List<NnProgram>)q.execute(channelId, NnProgram.STATUS_OK, NnProgram.TYPE_VIDEO);
			detached = (List<NnProgram>)pm.detachCopyAll(programs);
		} finally {
			pm.close();
		}
		return detached;
	}
	
	public List<NnProgram> findAllByChannelId(long channelId) {
		List<NnProgram> detached = new ArrayList<NnProgram>();
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			Query q = pm.newQuery(NnProgram.class);
			q.setFilter("channelId == channelIdParam");
			q.declareParameters("long channelIdParam");
			q.setOrdering("pubDate desc");
			@SuppressWarnings("unchecked")
			List<NnProgram> programs = (List<NnProgram>)q.execute(channelId);		
			detached = (List<NnProgram>)pm.detachCopyAll(programs);
		} finally {
			pm.close();
		}
		return detached;
	}	
	
	public NnProgram findById(long id) {
		NnProgram program = null;
		if (id == 0) return program;
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			program = pm.getObjectById(NnProgram.class, id);
			program = pm.detachCopy(program);
		} catch (JDOObjectNotFoundException e) {
		} finally {
			pm.close();
		}
		return program;		
	}	
	
	public int findAndDeleteProgramsOlderThanMax(long channelId) {
		List<NnProgram> list = new ArrayList<NnProgram>();
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			Query q = pm.newQuery(NnProgram.class);
			q.setOrdering("pubDate asc");
			q.setFilter("channelId == channelIdParam");
			q.declareParameters("long channelIdParam");			
			@SuppressWarnings("unchecked")
			List<NnProgram> programs = (List<NnProgram>) q.execute(channelId);			
			if (programs.size() > NnChannel.MAX_CHANNEL_SIZE) {
				int over = programs.size() - NnChannel.MAX_CHANNEL_SIZE;
				list = programs.subList(0, over);				
				log.info("Channel id, original size, delete size:" + channelId + "," + programs.size() + "," + list.size());
				pm.deletePersistentAll(list);
			}
			return programs.size() - list.size(); 
			
		} finally {
			pm.close();
		}		
	}	
	
	public NnProgram findOldestByChannelId(long channelId) {
		NnProgram oldest = null;
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			Query q = pm.newQuery(NnProgram.class);
			q.setFilter("channelId == channelIdParam");
			q.declareParameters("long channelIdParam");			
			q.setOrdering("pubDate asc");
			q.setRange(0, 1);
			@SuppressWarnings("unchecked")
			List<NnProgram> programs = (List<NnProgram>) q.execute(channelId);
			if (programs.size() > 0) {
				oldest = programs.get(0);
				oldest = pm.detachCopy(oldest);
			}		
		} finally {
			pm.close();
		}
		return oldest;		
	}	
}
