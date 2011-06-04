package com.nncloudtv.dao;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.AuthLib;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnUser;

public class NnUserDao extends GenericDao<NnUser> {

	protected static final Logger log = Logger.getLogger(NnUserDao.class.getName());
	
	public NnUserDao() {
		super(NnUser.class);
	}

	//!!! and all the pref, subscription, etc
	private PersistenceManager getPersistenceManager(Mso mso, String token) {
		if (mso != null) {
			if (mso.getSharding() == 1) {
				return PMF.getNnUser1().getPersistenceManager();
			} else {
				return PMF.getNnUser2().getPersistenceManager();
			}
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
		PersistenceManager pm = this.getPersistenceManager(null, user.getToken());
		try {
			pm.makePersistent(user);
			user = pm.detachCopy(user);
		} finally {
			pm.close();
		}
		return user;
	}
	
	// return MSO only
	@SuppressWarnings("unchecked")
	public NnUser findAuthenticatedMsoUser(String email, String password, Mso mso) {
		NnUser user = null;
		PersistenceManager pm = this.getPersistenceManager(mso, null);
		try {
			Query query = pm.newQuery(NnUser.class);
			query.setFilter("email == emailParam && msoId == msoIdParam && type == typeParam");
			query.declareParameters("String emailParam, long msoIdParam, short typeParam");				
			List<NnUser> results = (List<NnUser>) query.execute(email, mso.getId(), NnUser.TYPE_3X3);
			if (results.size() > 0) {
				user = results.get(0);		
			} else {
				// make 9x9 and 5F login as possible
				results = (List<NnUser>) query.execute(email,  mso.getId(), NnUser.TYPE_NN);
				if (results.size() > 0) {
					user = results.get(0);
				} else {
					results = (List<NnUser>) query.execute(email,  mso.getId(), NnUser.TYPE_TBC);
					if (results.size() > 0)
						user = results.get(0);
				}
			}
			if (user != null) {
				user = pm.detachCopy(user);
				byte[] proposedDigest = AuthLib.passwordDigest(password, user.getSalt());
				if (!Arrays.equals(user.getCryptedPassword(), proposedDigest)) {				
					user = null;
				}
			}
		} finally {
			pm.close();
		}
		return user;		
	}
	
	public NnUser findAuthenticatedUser(String email, String password, Mso mso) {
		NnUser user = null;
		PersistenceManager pm = this.getPersistenceManager(mso, null);
		try {
			Query query = pm.newQuery(NnUser.class);
			query.setFilter("email == emailParam && msoId == msoIdParam");
			query.declareParameters("String emailParam, long msoIdParam");				
			@SuppressWarnings("unchecked")
			List<NnUser> results = (List<NnUser>) query.execute(email, mso.getId());
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
		 
	public NnUser findByTokenAndMso(String token, Mso mso) {
		NnUser user = null;
		PersistenceManager pm = this.getPersistenceManager(null, token);
		try {
			Query query = pm.newQuery(NnUser.class);
			query.setFilter("token == tokenParam && msoId == msoIdParam");
			query.declareParameters("String tokenParam, long msoIdParam");		
			@SuppressWarnings("unchecked")
			List<NnUser> results = (List<NnUser>) query.execute(token, mso.getId());
			if (results.size() > 0) {
				user = results.get(0);			
			}
			user = pm.detachCopy(user);
		} finally {
			pm.close();
		}
		return user;				
	}

	public NnUser findByEmailAndMso(String email, Mso mso) {
		NnUser user = null;
		PersistenceManager pm = this.getPersistenceManager(mso, null);
		try {
			Query query = pm.newQuery(NnUser.class);
			query.setFilter("email == emailParam && msoId == msoIdParam");
			query.declareParameters("String emailParam, long msoIdParam");		
			@SuppressWarnings("unchecked")
			List<NnUser> results = (List<NnUser>) query.execute(email, mso.getId());
			if (results.size() > 0) {
				user = results.get(0);			
			}
			user = pm.detachCopy(user);
		} finally {
			pm.close();
		}
		return user;				
	}
	
	/*
	public List<NnUser> findByType(short type) {
		List<NnUser> detached = new ArrayList<NnUser>();
		PersistenceManager pm = PMF.get(NnUser.class).getPersistenceManager();
		try {
			Query query = pm.newQuery(NnUser.class);
			query.setFilter("type == " + type);	
			@SuppressWarnings("unchecked")
			List<NnUser> users = (List<NnUser>) query.execute(type);
			detached = (List<NnUser>)pm.detachCopyAll(users);
		} finally {
			pm.close();
		}
		return detached;		
	}
	*/
}
