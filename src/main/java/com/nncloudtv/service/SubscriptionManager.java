package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nncloudtv.dao.SubscriptionDao;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.Subscription;
import com.nncloudtv.model.SubscriptionLog;
import com.nncloudtv.model.SubscriptionSet;

@Service
public class SubscriptionManager {		

	protected static final Logger log = Logger.getLogger(SubscriptionManager.class.getName());
	
	SubscriptionDao subDao = new SubscriptionDao(); 

	public List<Subscription> findAllByUser(NnUser user) {
		return subDao.findAllByUser(user);
	}
	
	//make sure your channel has seq and type set
	public boolean subscribeChannel(NnUser user, NnChannel channel) {
		// !!! if channel does not have seq and type set, return error, 
		Subscription existed = subDao.findByUserAndChannelId(user, channel.getId());
		if (existed != null) {
			log.info("user trying to subscribe a channel that has been subscribed." + channel.getId());
			return false;
		}
		Subscription s = new Subscription(user.getId(), channel.getId(), channel.getSeq(), channel.getType());
		Date now = new Date();
		s.setCreateDate(now);
		s.setUpdateDate(now);
		subDao.save(user, s);
		//!!!! move out, don't care
		SubscriptionLogManager sublogMngr = new SubscriptionLogManager();
		SubscriptionLog sublog = sublogMngr.findByMsoIdAndChannelId(user.getMsoId(), channel.getId());
		if (sublog == null) {
			sublog = new SubscriptionLog(user.getMsoId(), channel.getId());
		} else {
			sublog.setCount(sublog.getCount()+1);
		}
		sublogMngr.save(sublog);		
		return true;
	}
	
	@Transactional
	public boolean subscribeSet(NnUser user, SubscriptionSet subSet, List<NnChannel> channels) {

		SubscriptionSetManager subSetMngr = new SubscriptionSetManager();
		subSetMngr.create(user, subSet);
		
		for (NnChannel c : channels) {
			Subscription existed = subDao.findByUserAndChannelId(user, c.getId());
			if (existed == null) {
				Subscription sub = new Subscription(user.getId(), c.getId(), c.getSeq(), c.getType());
				Date now = new Date();
				sub.setCreateDate(now);
				sub.setUpdateDate(now);
				subDao.save(user, sub);
			}
		}
		return true;
	}
	
	public Subscription findByUserAndChannelId(NnUser user, long channelId) {
		Subscription s = subDao.findByUserAndChannelId(user, channelId);
		return s;
	}
	
	public void unsubscribeChannel(NnUser user, Subscription s) {
		if (s != null) { subDao.delete(user, s); }
	}

	/**
	 * @param msoId used if interested in finding subscription count, pass 0 if not.  	      	    
	 */
	public List<NnChannel> findSubscribedChannels(NnUser user, Mso mso) {
		List<Subscription> subs = subDao.findAllByUser(user);
		List<NnChannel> channels = new ArrayList<NnChannel>();
		NnChannelManager channelMngr = new NnChannelManager();
		SubscriptionLogManager sublogMngr = new SubscriptionLogManager();
		for (Subscription s : subs) {
			NnChannel c = channelMngr.findById(s.getChannelId()); //!!!
			if ( c!= null) {
				c.setSeq(s.getSeq());
				c.setType(s.getType());
				if (mso != null) {
					SubscriptionLog sublog = sublogMngr.findByMsoIdAndChannelId(mso.getId(), c.getId());			
					if (sublog != null) {c.setSubscriptionCount(sublog.getCount());}
				}			
				channels.add(c);
			}
		}
		return channels;			 
	}

	//move from seq1 to seq2
	public boolean moveSeq(NnUser user, int seq1, int seq2) {						
		Subscription sub = subDao.findByUserAndSeq(user, seq1);
		if (sub == null) {return false;}
		sub.setSeq(seq2);
		subDao.save(user, sub);
		return true;
	}
	
	/*
	public List<Subscription> list(int page, int limit, String sidx, String sord) {
		return subDao.list(page, limit, sidx, sord);
	}
	
	public List<Subscription> list(int page, int limit, String sidx, String sord, String filter) {
		return subDao.list(page, limit, sidx, sord, filter);
	}
	
	public int total() {
		return subDao.total();
	}
	
	public int total(String filter) {
		return subDao.total(filter);
	}
	*/
}
