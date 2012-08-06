package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnUserWatchedDao;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserWatched;

public class NnUserWatchedManager {

	protected static final Logger log = Logger.getLogger(NnUserWatchedManager.class.getName());
	
	private NnUserWatchedDao watchedDao= new NnUserWatchedDao();
					
	public NnUserWatched save(NnUser user, NnUserWatched watched) {
		Date now = new Date();
		NnUserWatched existed = this.findByUserTokenAndChannel(user.getToken(), watched.getChannelId());
		if (existed != null) {
			existed.setProgram(watched.getProgram());
			watched = existed;
		}
		if (watched.getCreateDate() == null) {watched.setCreateDate(now);}
		watched.setUpdateDate(now);		
		return watchedDao.save(user, watched);
	}
	
	public NnUserWatched findByUserTokenAndChannel(String token, long channelId) {
		return watchedDao.findByUserTokenAndChannel(token, channelId);
	}

	public void delete(NnUser user, NnUserWatched watched) {
		watchedDao.delete(user, watched);
	}
		
	public List<NnUserWatched> findByUserToken(String token) {
		return watchedDao.findByUserToken(token);
	}
}
