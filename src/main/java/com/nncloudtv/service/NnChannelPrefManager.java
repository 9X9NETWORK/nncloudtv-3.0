package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelPrefDao;
import com.nncloudtv.model.NnChannelPref;

@Service
public class NnChannelPrefManager {
	
	protected static final Logger log = Logger
	        .getLogger(NnChannelPrefManager.class.getName());
	
	private NnChannelPrefDao prefDao = new NnChannelPrefDao();
	
	public NnChannelPref save(NnChannelPref pref) {
		Date now = new Date();
		if (pref.getCreateDate() == null)
			pref.setCreateDate(now);
		pref.setUpdateDate(now);
		return prefDao.save(pref);
	}
	
	public List<NnChannelPref> findByChannelId(long channelId) {
		return prefDao.findByChannelId(channelId);
	}
	
	public NnChannelPref findByChannelIdAndItem(long channelId, String item) {
		return prefDao.findByChannelIdAndItem(channelId, item);
	}
	
	public void delete(NnChannelPref pref) {
		prefDao.delete(pref);
	}
}
