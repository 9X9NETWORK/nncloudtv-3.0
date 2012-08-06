package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.SnsAuthDao;
import com.nncloudtv.model.SnsAuth;

@Service
public class SnsAuthManager {
	
	protected static final Logger log = Logger.getLogger(SnsAuthManager.class.getName());
	
	private SnsAuthDao authDao = new SnsAuthDao();
	
	public void create(SnsAuth auth) {
		auth.setCreateDate(new Date());
		auth.setEnabled(true);
		authDao.save(auth);
	}
	
	public void save(SnsAuth auth) {
		authDao.save(auth);
	}
	
	public void delete(SnsAuth auth) {
		authDao.delete(auth);
	}
	
	public List<SnsAuth> findByMso(long msoId) {
		return authDao.findByMso(msoId);
	}
	
	public SnsAuth findFacebookAuthByMso(long msoId) {
		return authDao.findByMsoAndType(msoId, SnsAuth.TYPE_FACEBOOK);
	}
	
	public SnsAuth findTitterAuthByMsoId(long msoId) {
		return authDao.findByMsoAndType(msoId, SnsAuth.TYPE_TWITTER);
	}
	
	public SnsAuth findPlurkAuthByMsoId(long msoId) {
		return authDao.findByMsoAndType(msoId, SnsAuth.TYPE_PLURK);
	}
	
	public SnsAuth findSinaAuthByMsoId(long msoId) {
		return authDao.findByMsoAndType(msoId, SnsAuth.TYPE_SINA);
	}
	
	public SnsAuth findMsoIdAndType(Long msoId, Short type) {
		return authDao.findByMsoAndType(msoId, type);
	}
}
