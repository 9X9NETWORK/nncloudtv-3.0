package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnDeviceDao;
import com.nncloudtv.model.NnDevice;
import com.nncloudtv.model.NnUser;

@Service
public class NnDeviceManager {
	protected static final Logger log = Logger.getLogger(NnDeviceManager.class.getName());
	
	private NnDeviceDao deviceDao = new NnDeviceDao();
	private HttpServletRequest req;
			
	public HttpServletRequest getReq() { return req; }
	public void setReq(HttpServletRequest req) { this.req = req;}

	public void save(NnDevice device) {
		device.setUpdateDate(new Date());
		deviceDao.save(device);
	}
	
	//can create device based on user, or device
	public NnDevice create(NnDevice device, NnUser user, String type) {
		if (device != null && user != null) {
			NnDevice existed = this.findByTokenAndUser(device.getToken(), user); 
			if (existed != null)
				return existed;
		}
		
		if (device == null)
			device = new NnDevice();
		if (device.getToken() == null && user == null)
			device.setToken(NnUserManager.generateToken(NnUserManager.getShardByLocale(req)));		
		if (user != null) {
			device.setUserId(user.getId());
			device.setShard(user.getShard()); //for future reference
		}				
		
		device.setType(type);
		Date now = new Date();
		if (device.getCreateDate() == null)
			device.setCreateDate(now);
		device.setUpdateDate(now);
		deviceDao.save(device);		
		return device;
	}
	
	public NnDevice findByTokenAndUser(String token, NnUser user) {
		return deviceDao.findByTokenAndUser(token, user);
	}

	//find a device that's not associated with any user account, which is user id = 0
	public NnDevice findDeviceOpenToken(String token) {
		return deviceDao.findDeviceOpenToken(token);
	}
	
	public List<NnDevice> findByToken(String token) {
		return deviceDao.findByToken(token);
	}

	public List<NnDevice> findByUser(NnUser user) {
		return deviceDao.findByUser(user);
	}
	
	public NnDevice addUser(String token, NnUser user) {		
		List<NnDevice> devices = this.findByToken(token);		
		if (devices.size() == 0)
			return null;
		NnDevice existed = this.findDeviceOpenToken(token);
		if (existed != null) {
			existed.setUserId(user.getId());			
			deviceDao.save(existed);
			return existed;
		}
		existed = this.findByTokenAndUser(token, user);
		if (existed != null) {
			return existed;
		}		
		NnDevice device = new NnDevice();
		device.setToken(devices.get(0).getToken());
		this.create(device, user, null);
		return device;
	}	
	
	public void delete(NnDevice device) {
		deviceDao.delete(device);
	}
	
	public boolean removeUser(String token, NnUser user) {
		List<NnDevice> devices = this.findByToken(token);
		if (devices.size() == 0)
			return false;
		NnDevice existed = this.findByTokenAndUser(token, user);
		if (existed == null)
			return false;
		this.delete(existed);
		return true;
	}
}
