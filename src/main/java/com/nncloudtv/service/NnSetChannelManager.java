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

	public NnSetToNnChannel findBySetAndChannel(long channelSetId, long channelId) {
		return dao.findBySetAndChannel(channelSetId, channelId);
	}	
	
	//move from seq1 to seq2
	public boolean moveSeq(long setId, int seq1, int seq2) {
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
	
	public void addChannel(long channelSetId, NnChannel channel) {
		NnSetToNnChannel sc = dao.findBySetAndSeq(channelSetId, channel.getSeq());
		if (sc == null) {
			sc = new NnSetToNnChannel(channelSetId, channel.getId(), channel.getSeq());
			this.create(sc);
		} else {
			sc.setChannelId(channel.getId());
			this.save(sc);
		}
	}
	
	public void removeChannel(long channelSetId, int seq) {
		NnSetToNnChannel csc = dao.findBySetAndSeq(channelSetId, seq);
		if (csc != null)
			this.delete(csc);
	}
	
	
}
