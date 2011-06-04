package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnSetDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnSetChannel;

@Service
public class NnSetManager {
	
	protected static final Logger logger = Logger.getLogger(NnSetManager.class.getName());
	
	private NnSetDao setDao = new NnSetDao();
	
	public void create(NnSet channelSet, List<NnChannel> channels) {
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		
		if (this.findByName(channelSet.getName()) != null) {
			logger.warning("channelSet already exists, name: " + channelSet.getName());
			//return;
		}
		Date now = new Date();
		channelSet.setCreateDate(now);
		channelSet.setUpdateDate(now);
		
		setDao.save(channelSet);
		
		for (NnChannel channel : channels) {
			NnSetChannel csc = new NnSetChannel(channelSet.getId(), channel.getId(), channel.getSeq());
			cscMngr.create(csc);
		}
	}
	
	public NnSet findBybeautifulUrl(String url) {
		return setDao.findByBeautifulUrl(url);
	}

	public NnSet save(NnSet set) {	
		//NOTE check name existence if needed
		set.setUpdateDate(new Date());
		setDao.save(set);
		
		return set;
	}
	
	private Object findByName(String name) {
		return setDao.findByName(name.trim().toLowerCase());
	}
	
	public List<NnChannel> findChannelsById(long setId) {
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		NnChannelManager channelMngr = new NnChannelManager();		
		List<NnSetChannel> scs = cscMngr.findBySetId(setId); 
		ArrayList<NnChannel> results = new ArrayList<NnChannel>();
		
		for (NnSetChannel sc : scs) {
			NnChannel channel = channelMngr.findById(sc.getChannelId());
			if (channel != null) {
				channel.setSeq(sc.getSeq());
				results.add(channel);
			}
		}
		return results;
	}

	public NnSet findById(long channelSetId) {
		return setDao.findById(channelSetId);
	}
	
}
