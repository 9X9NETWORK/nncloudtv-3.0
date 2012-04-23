package com.nncloudtv.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.nncloudtv.dao.NnGuestDao;
import com.nncloudtv.model.NnGuest;

public class NnGuestManager {

	private NnGuestDao guestDao = new NnGuestDao();
	
	public void save(NnGuest guest, HttpServletRequest req) {
		if (guest.getCreateDate() == null)
			guest.setCreateDate(new Date());
		if (guest.getShard() == 0) {
			short shard = NnUserManager.getShardByLocale(req);
			guest.setShard(shard);
		}
		guestDao.save(guest);
	}

	public void delete(NnGuest guest) {
		guestDao.delete(guest);
	}
	
	public NnGuest findByToken(String token) {
		return guestDao.findByToken(token);
	}
}
