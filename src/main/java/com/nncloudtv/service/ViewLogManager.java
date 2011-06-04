package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.ViewLogDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.ViewLog;

public class ViewLogManager {

	protected static final Logger log = Logger.getLogger(ViewLogManager.class.getName());
	
	private ViewLogDao watchedDao= new ViewLogDao();
					
	public ViewLog save(ViewLog watched) {
		Date now = new Date();
		if (watched.getCreateDate() == null) {watched.setCreateDate(now);}
		watched.setUpdateDate(now);
		return watchedDao.save(watched);
	}
	
	public ViewLog findByUserIdAndChannelId(long userId, long channelId) {
		return watchedDao.findByUserIdAndChannelId(userId, channelId);
	}
	
	public List<ViewLog> findAllByUserId(List<Long> channelIds) {
		return watchedDao.findAllByUserId(channelIds);
	}
	
	public void processPdr(String pdrLine, long userId) {
		if (userId == 0) {return;}
		
		NnChannelManager channelMngr = new NnChannelManager();				
		try {
			String[] data = pdrLine.split("\t");		
			long channelId = Long.parseLong(data[2]);
			NnChannel c = channelMngr.findById(channelId);
			ViewLog watched = this.findByUserIdAndChannelId(userId, c.getId());
			
			if (c != null) {
				for (int i=3; i< data.length; i++) {
					long programId = Long.parseLong(data[i]);
					if (watched == null) {
						watched = new ViewLog(userId, c.getId());
						watched.getPrograms().add(programId);					
					} else {							
					    if (!watched.getPrograms().contains(programId)) {
					    	watched.getPrograms().add(programId);
					    	this.save(watched);
					    }
					}
					this.save(watched);					
				}
			}
		} catch (Exception e) {
			log.info("exception catpures: " + e.getClass());
		}
	}
	
}
