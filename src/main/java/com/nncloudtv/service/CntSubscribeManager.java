package com.nncloudtv.service;

import java.util.logging.Logger;

import com.nncloudtv.dao.CntSubscribeDao;
import com.nncloudtv.model.CntView;

public class CntSubscribeManager {
	
	protected static final Logger log = Logger.getLogger(NnUserSubscribeManager.class.getName());
	
	CntSubscribeDao cntDao = new CntSubscribeDao(); 
	
	public void create(CntView log) {
		this.save(log);
	}
	public CntView save(CntView log) {
		return cntDao.save(log);
	}
		
	public int findTotalCountByChannel(long channelId) {
		return cntDao.findTotalCountByChannel(channelId);
	}

	public int findTotalCountBySet(long setId) {
		return cntDao.findTotalCountBySet(setId);
	}
	
	public CntView findByChannel(long channelId) {
		return cntDao.findByChannel(channelId);
	}
	
}
