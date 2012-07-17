package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.UserInvite;

public class UserInviteDao {
	protected static final Logger log = Logger.getLogger(UserInviteDao.class.getName());
	
	public UserInvite save(UserInvite invite) {
		invite.setUpdateDate(new Date());
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		if (pm == null) return null;
		try {
			pm.makePersistent(invite);
			invite = pm.detachCopy(invite);
		} finally {
			pm.close();
		}
		return invite;
	}

	public List<UserInvite> findSubscribers(long userId, short shard, long channelId) {
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		List<UserInvite> detached = new ArrayList<UserInvite>();
		try {
			Query q = pm.newQuery(UserInvite.class);
			q.setFilter("userId == userIdParam && shard == shardParam && channelId == channelIdParam");
			q.declareParameters("long userIdParam, short shardParam, long channelIdParam");
			@SuppressWarnings("unchecked")
			List<UserInvite> invites = (List<UserInvite>)q.execute(userId, shard, channelId);
			detached = (List<UserInvite>)pm.detachCopyAll(invites);
		} finally {
			pm.close();
		}
		return detached;
		
	}
	
	public List<UserInvite> findByUserAndInvitee(long userId, long inviteeId) {
		List<UserInvite> detached = new ArrayList<UserInvite>();
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query q = pm.newQuery(UserInvite.class);
			q.setFilter("userId == userIdParam && inviteeId == inviteeIdParam");
			q.declareParameters("long userIdParam, long inviteeIdParam");
			@SuppressWarnings("unchecked")
			List<UserInvite> invites = (List<UserInvite>)q.execute(userId, inviteeId);
			log.info("invites size:" + invites.size());
			detached = (List<UserInvite>)pm.detachCopyAll(invites);
		} finally {
			pm.close();
		}
		return detached;
	}	 

	public UserInvite findByToken(String token) {
		UserInvite invite = null;
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query q = pm.newQuery(UserInvite.class);
			q.setFilter("inviteToken == inviteTokenParam");
			q.declareParameters("String inviteTokenParam");
			@SuppressWarnings("unchecked")
			List<UserInvite> results = (List<UserInvite>) q.execute(token);
			if (results.size() > 0) {
				invite = results.get(0);			
			}
			invite = pm.detachCopy(invite);
		} finally {
			pm.close();
		}
		return invite;
		
	}
	
}
