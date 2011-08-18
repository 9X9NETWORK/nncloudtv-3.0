package com.nncloudtv.dao;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.AuthLib;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.NnUser;

public class NnUserDao extends GenericDao<NnUser> {

	protected static final Logger log = Logger.getLogger(NnUserDao.class.getName());
	
	public NnUserDao() {
		super(NnUser.class);
	}

	//use either shard or token to determine partition, default shard 1 if nothing
	private PersistenceManager getPersistenceManager(short shard, String token) {
		if (shard != 0) {
			if (shard != 1)
				return PMF.getNnUser2().getPersistenceManager();
			else 
				return PMF.getNnUser1().getPersistenceManager();
		}
		if (token != null) {
			if (token.contains("1-")) {
				return PMF.getNnUser1().getPersistenceManager();
			} else {
				return PMF.getNnUser2().getPersistenceManager();
			}
		}		
		return PMF.getNnUser1().getPersistenceManager();
	}
			
	public NnUser save(NnUser user) {
		if (user == null) {return null;}
		PersistenceManager pm = this.getPersistenceManager(user.getShard(), user.getToken());
		try {
			pm.makePersistent(user);
			user = pm.detachCopy(user);
		} finally {
			pm.close();
		}
		return user;
	}
	
	public NnUser findAuthenticatedUser(String email, String password, short shard) {
		NnUser user = null;
		PersistenceManager pm = this.getPersistenceManager(shard, null);
		try {
			Query query = pm.newQuery(NnUser.class);
			query.setFilter("email == emailParam");
			query.declareParameters("String emailParam");				
			@SuppressWarnings("unchecked")
			List<NnUser> results = (List<NnUser>) query.execute(email);
			if (results.size() > 0) {
				user = results.get(0);		
				byte[] proposedDigest = AuthLib.passwordDigest(password, user.getSalt());
				if (!Arrays.equals(user.getCryptedPassword(), proposedDigest)) {				
					user = null;
				}
			}
			user = pm.detachCopy(user);
		} finally {
			pm.close();
		}
		return user;		
	}
		 
	public NnUser findByToken(String token) {
		NnUser user = null;
		PersistenceManager pm = this.getPersistenceManager((short)0, token);
		try {
			Query query = pm.newQuery(NnUser.class);
			query.setFilter("token == tokenParam");
			query.declareParameters("String tokenParam");		
			@SuppressWarnings("unchecked")
			List<NnUser> results = (List<NnUser>) query.execute(token);
			if (results.size() > 0) {
				user = results.get(0);			
			}
			user = pm.detachCopy(user);
		} finally {
			pm.close();
		}
		return user;				
	}
	
	public NnUser findByEmail(String email, short shard) {
		NnUser user = null;
		PersistenceManager pm = this.getPersistenceManager(shard, null);
		try {
			Query query = pm.newQuery(NnUser.class);
			query.setFilter("email == emailParam");
			query.declareParameters("String emailParam");		
			@SuppressWarnings("unchecked")
			List<NnUser> results = (List<NnUser>) query.execute(email);
			if (results.size() > 0) {
				user = results.get(0);			
			}
			user = pm.detachCopy(user);
		} finally {
			pm.close();
		}
		return user;				
	}	
}
