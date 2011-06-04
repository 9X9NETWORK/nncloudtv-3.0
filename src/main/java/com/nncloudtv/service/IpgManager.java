package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.IpgDao;
import com.nncloudtv.model.Ipg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.Subscription;

public class IpgManager {

	protected static final Logger log = Logger.getLogger(IpgManager.class.getName());
	
	private IpgDao ipgDao = new IpgDao();	
	
	public void create(Ipg ipg, NnUser user) {
		Date now = new Date();
		ipg.setUpdateDate(now);
		ipg.setCreateDate(now);

		Hashtable<Integer,Long> channels = new Hashtable<Integer, Long>();
		SubscriptionManager subMngr = new SubscriptionManager();
		List<Subscription> subscriptions = subMngr.findAllByUser(user);		
		int size = subscriptions.size();
		for (int i = 0; i < size; i++) {
			Subscription s = subscriptions.get(i);
			channels.put(new Integer(s.getSeq()), s.getChannelId());
		}
		ipg.setChannels(channels);
		log.info("new IPG size: " + channels.size());		
		ipgDao.save(ipg);
	}	
	
	public Ipg save(Ipg ipg) {
		return ipgDao.save(ipg);
	}	
	
	public Ipg findById(Long id) {
		return ipgDao.findById(id);
	}	
	
	public List<Ipg> findByUserId(long userId) {
		return ipgDao.findByUserId(userId);
	}
	
	public List<NnProgram> findIpgPrograms(Ipg ipg) {
		Hashtable<Integer,Long> hashTable = ipg.getChannels();
		NnProgramManager programMngr = new NnProgramManager();
		List<Long> channelIds = new ArrayList<Long>();		
		int i = 0;		
		for (Enumeration<Integer> e = hashTable.keys(); e.hasMoreElements(); i++) {
			Integer grid = (Integer)e.nextElement();
			channelIds.add((long)hashTable.get(grid));
		}		
		return programMngr.findGoodProgramsByChannelIds(channelIds);
	}
			
	public List<NnChannel> findIpgChannels(Ipg ipg) {
		Hashtable<Integer,Long> hash = ipg.getChannels();
		List<NnChannel> channels = new ArrayList<NnChannel>();
		NnChannelManager msoChannelMngr = new NnChannelManager();
		NnChannel c;
		for (Enumeration<Integer> e = hash.keys(); e.hasMoreElements();) {
			Integer grid = (Integer)e.nextElement();
			c = msoChannelMngr.findById(hash.get(grid));
			if (c != null) {
				c.setSeq(grid.intValue());
				channels.add(c);
			}
		}
		return channels;
	}
	
}