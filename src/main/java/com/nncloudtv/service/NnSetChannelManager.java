package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnSetChannelDao;
import com.nncloudtv.model.NnSetChannel;
import com.nncloudtv.model.NnChannel;

@Service
public class NnSetChannelManager {
	protected static final Logger logger = Logger.getLogger(NnSetChannelManager.class.getName());
	
	NnSetChannelDao scDao = new NnSetChannelDao();
	
	public void create(NnSetChannel sc) {
		Date now = new Date();
		sc.setCreateDate(now);
		sc.setUpdateDate(now);
		scDao.save(sc);
	}
	
	public void delete(NnSetChannel sc) {
		scDao.delete(sc);
	}
	
	public NnSetChannel save(NnSetChannel sc) {
		sc.setUpdateDate(new Date());
		scDao.save(sc);
		return sc;
	}
	
	public List<NnSetChannel> findBySetId(long setId) {
		return scDao.findBySetId(setId);
	}
	
	//move from seq1 to seq2
	public boolean moveSeq(long setId, int seq1, int seq2) {
		NnSetChannel sc1 = scDao.findBySetIdAndSeq(setId, seq1);
		NnSetChannel sc2 = scDao.findBySetIdAndSeq(setId, seq2);
		if (sc1 == null) {return false;}
		sc1.setSeq(seq2);
		this.save(sc1);
		if (sc2 != null) {
			sc2.setSeq(seq1);
			this.save(sc2);
		}
		return true;
	}
	
	public void addChannel(long channelSetId, NnChannel channel) {
		NnSetChannel sc = scDao.findBySetIdAndSeq(channelSetId, channel.getSeq());
		if (sc == null) {
			sc = new NnSetChannel(channelSetId, channel.getId(), channel.getSeq());
			this.create(sc);
		} else {
			sc.setChannelId(channel.getId());
			this.save(sc);
		}
	}
	
	public void removeChannel(long channelSetId, int seq) {
		NnSetChannel csc = scDao.findBySetIdAndSeq(channelSetId, seq);
		if (csc != null)
			this.delete(csc);
	}
	
	
}
