package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnSetToNnChannelDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSetToNnChannel;

@Service
public class NnSetChannelManager {
	protected static final Logger log = Logger.getLogger(NnSetChannelManager.class.getName());
	
	NnSetToNnChannelDao dao = new NnSetToNnChannelDao();
	
	public void create(NnSetToNnChannel sc) {
		Date now = new Date();
		sc.setCreateDate(now);
		sc.setUpdateDate(now);
		dao.save(sc);
	}
	
	public void delete(NnSetToNnChannel sc) {
		dao.delete(sc);
	}
	
	public NnSetToNnChannel save(NnSetToNnChannel sc) {
		sc.setUpdateDate(new Date());
		dao.save(sc);
		return sc;
	}
	
	public List<NnSetToNnChannel> findBySet(long setId) {
		return dao.findBySet(setId);
	}

	public NnSetToNnChannel findBySetAndChannel(long setId, long channelId) {
		return dao.findBySetAndChannel(setId, channelId);
	}	
	
	//move from seq1 to seq2
	public boolean moveSeq(long setId, short seq1, short seq2) {
		NnSetToNnChannel sc1 = dao.findBySetAndSeq(setId, seq1);
		NnSetToNnChannel sc2 = dao.findBySetAndSeq(setId, seq2);
		if (sc1 == null) {return false;}
		sc1.setSeq(seq2);
		this.save(sc1);
		if (sc2 != null) {
			sc2.setSeq(seq1);
			this.save(sc2);
		}
		return true;
	}
	
	public void addChannel(long setId, NnChannel channel) {
		NnSetToNnChannel sc = dao.findBySetAndSeq(setId, channel.getSeq());
		if (sc == null) {
			sc = new NnSetToNnChannel(setId, channel.getId(), channel.getSeq());
			this.create(sc);
		} else {
			sc.setChannelId(channel.getId());
			this.save(sc);
		}
	}
	
	public void removeChannel(long setId, short seq) {
		NnSetToNnChannel csc = dao.findBySetAndSeq(setId, seq);
		if (csc != null)
			this.delete(csc);
	}
	
	
}
