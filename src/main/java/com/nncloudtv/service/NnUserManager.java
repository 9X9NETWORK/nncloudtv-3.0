package com.nncloudtv.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.annotations.Transactional;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnUserDao;
import com.nncloudtv.lib.AuthLib;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnUser;

@Service
public class NnUserManager {
	
	protected static final Logger log = Logger.getLogger(NnUserManager.class.getName());
		
	private NnUserDao nnUserDao = new NnUserDao();
	
	//@@@IMPORTANT email duplication is your responsibility
	public void create(NnUser user, HttpServletRequest req, short shard) {
		user.setName(user.getName().replaceAll("\\s", " "));
		user.setEmail(user.getEmail().toLowerCase());
		if (shard == 0)
			shard= this.getShard(req);
		user.setToken(this.generateToken(shard));
		user.setShard(shard);
		Date now = new Date();
		user.setCreateDate(now);
		user.setUpdateDate(now);
		nnUserDao.save(user);
	}

	//default is 1, asia tw, cn, hk is 2
	public short getShard(HttpServletRequest req) {
		String ip = req.getRemoteAddr();
		log.info("findLocaleByHttpRequest() ip is " + ip);
        String country = "";
		try {
			URL url = new URL("http://brussels.teltel.com/geoip/?ip=" + ip);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);
	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	        	log.info("findLocaleByHttpRequest() IP service returns error:" + connection.getResponseCode());	        	
	        }
	        BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));;
	        if (rd.readLine()!=null) {country = rd.readLine().toLowerCase();} //assuming one line	        
		} catch (Exception e) {
			NnLogUtil.logException(e);
		}
        short shard = 1;
		if (country.equals("tw") || country.equals("cn") || country.equals("hk")) {
			shard = 2;
		}		  
		return shard;
	}
	
	public NnUser createGuest(Mso mso, HttpServletRequest req) {
		String password = String.valueOf(("token" + Math.random() + new Date().getTime()).hashCode());
		NnUser guest = new NnUser(NnUser.GUEST_EMAIL, password, NnUser.GUEST_NAME, NnUser.TYPE_USER, mso.getId());
		this.create(guest, req, (short)0);
		return guest;
	}
	
	public NnUser save(NnUser user) {
		if (user.getPassword() != null) {
			user.setSalt(AuthLib.generateSalt());
			user.setCryptedPassword(AuthLib.encryptPassword(user.getPassword(), user.getSalt()));
		}
		user.setEmail(user.getEmail().toLowerCase());
		user.setUpdateDate(new Date());
		return nnUserDao.save(user);
	}

	/**
	 * GAE can only write 5 records a sec, maybe safe enough to do so w/out DB retrieving.
	 * taking the chance to speed up signin (meaning not to consult DB before creating the account).
	 */
	private String generateToken(short shard) {
		if (shard == 0)
			return null;
		String time = String.valueOf(new Date().getTime());
		String random = RandomStringUtils.randomAlphabetic(10);
		String result = time + random;
		result = RandomStringUtils.random(20, 0, 20, true, true, result.toCharArray());
		result = shard + "-" + result;
		return result;
	}	
	
	public NnUser findByEmail(String email, HttpServletRequest req) {
		short shard= this.getShard(req);
		return nnUserDao.findByEmail(email.toLowerCase(), shard);
	}
	
	public NnUser findAuthenticatedUser(String email, String password, HttpServletRequest req) {
		short shard= this.getShard(req); 
		return nnUserDao.findAuthenticatedUser(email.toLowerCase(), password, shard);
	}
	
	/*
	public NnUser findNNUser() {
		List<NnUser> users = nnUserDao.findByType(NnUser.TYPE_NN, );
		if (users.size() > 0) {return nnUserDao.findByType(NnUser.TYPE_NN).get(0); }
		return null;
	}
	
	public List<NnUser> findByType(short type, String token) {
		return nnUserDao.findByType(type, token);
	}
	
	public List<NnUser> findByType(short type, String token) {
		return nnUserDao.findByType(type, token);
	}
		
	public NnUser findAuthenticatedMsoUser(String email, String password, long msoId) {
		return nnUserDao.findAuthenticatedMsoUser(email.toLowerCase(), password, msoId);
	}
				
	public NnUser findByEmailAndMso(String email, Mso mso) {
		return nnUserDao.findByEmailAndMsoId(email.toLowerCase(), mso.getKey().getId());
	}
	
    */

	@Transactional
	public void subscibeDefaultChannels(NnUser user) {
		NnChannelManager channelMngr = new NnChannelManager();		
		List<NnChannel> channels = channelMngr.findMsoDefaultChannels(user.getMsoId(), false);	
		SubscriptionManager subManager = new SubscriptionManager();
		for (NnChannel c : channels) {
			subManager.subscribeChannel(user, c);
		}
		log.info("user " +  user.getId() + "(" + user.getToken() + ") subscribe " + channels.size() + " channels (mso:" + user.getMsoId() + ")");
	}
	
	public NnUser findByToken(String token) {
		return nnUserDao.findByToken(token);
	}
	
	public NnUser findById(long id) {
		return nnUserDao.findById(id);
	}
	
	public List<NnUser> list(int page, int limit, String sidx, String sord) {
		return nnUserDao.list(page, limit, sidx, sord);
	}
	
	public List<NnUser> list(int page, int limit, String sidx, String sord, String filter) {
		return nnUserDao.list(page, limit, sidx, sord, filter);
	}
	
	public int total() {
		return nnUserDao.total();
	}
	
	public int total(String filter) {
		return nnUserDao.total(filter);
	}	
}
