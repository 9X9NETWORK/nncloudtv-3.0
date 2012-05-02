package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnSetToNnChannelDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnSetToNnChannel;

@Service
public class NnSetToNnChannelManager {
	protected static final Logger log = Logger.getLogger(NnSetToNnChannelManager.class.getName());
	
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
	
	public void deleteAll(List<NnSetToNnChannel> list) {
		dao.deleteAll(list);
	}
	
	public void deleteChannelSet(NnChannel channel, List<NnSet> sets) {
		if (channel == null) {return;}
		if (sets.size() == 0) {return;}
				
		for (NnSet s : sets) {	
			NnSetToNnChannel cs = this.findBySetAndChannel(s.getId(), channel.getId());
			if (cs != null) {
				this.delete(cs);
			}
		}		
	}
	
	public NnSetToNnChannel save(NnSetToNnChannel sc) {
		sc.setUpdateDate(new Date());
		dao.save(sc);
		return sc;
	}
	
	public void saveAll(List<NnSetToNnChannel> list) {
		for(NnSetToNnChannel sc : list) {
			sc.setUpdateDate(new Date());
		}
		dao.saveAll(list);
		//sc.setUpdateDate(new Date());
		//dao.save(sc);
		//return sc;
	}
	
	public NnSetToNnChannel findById(long id) {
		return dao.findById(id);
	}
	
	public List<NnSetToNnChannel> findBySet(long setId) {
		return dao.findBySet(setId);
	}
	
	public List<NnSetToNnChannel> findByChannel(long channelId) {
		return dao.findByChannel(channelId);
	}

	public NnSetToNnChannel findBySetAndChannel(long setId, long channelId) {
		return dao.findBySetAndChannel(setId, channelId);
	}
	
	public NnSetToNnChannel findBySetAndSeq(long setId, short seq) {
		return dao.findBySetAndSeq(setId, seq);
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
	
	public int total() {
		return dao.total();
	}
	
	public int total(String filter) {
		return dao.total(filter);
	}
	
	public List<NnSetToNnChannel> list(int page, int limit, String sidx, String sord) {
		return dao.list(page, limit, sidx, sord);
	}
	
	public List<NnSetToNnChannel> list(int page, int limit, String sidx, String sord, String filter) {
		return dao.list(page, limit, sidx, sord, filter);
	}
	
}
