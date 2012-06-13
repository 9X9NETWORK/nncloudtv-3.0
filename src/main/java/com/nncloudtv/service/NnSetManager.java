package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnSetDao;
import com.nncloudtv.dao.NnSetToNnChannelDao;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnSetToNnChannel;

@Service
public class NnSetManager {
	
	protected static final Logger log = Logger.getLogger(NnSetManager.class.getName());
	
	private NnSetDao setDao = new NnSetDao();
	
	public NnSet save(NnSet set) {	
		Date now = new Date();
		if (set.getCreateDate() == null)			
			set.setCreateDate(now);
		set.setUpdateDate(now);
		setDao.save(set);
		
		return set;
	}
	
	public void saveAll(List<NnSet> sets) {
		for(NnSet set : sets) {
			save(set);
		}
	}
	
	public void delete(NnSet cs) {
		setDao.delete(cs);
	}
	
	public NnSet create(NnSet set, List<NnChannel> channels) {		
		if (this.findByName(set.getName()) != null) {
			log.warning("set already exists, name: " + set.getName());
			return null;
		}
		Date now = new Date();
		set.setCreateDate(now);
		set.setUpdateDate(now);		
		setDao.save(set);
		this.addChannels(set, channels);
		return set;
	}

	public void deleteChannel(NnSet set, NnChannel channel) {
		NnSetToNnChannelDao dao = new NnSetToNnChannelDao();
		NnSetToNnChannel sToC = dao.findBySetAndChannel(set.getId(), channel.getId());
		if (dao.findBySetAndChannel(set.getId(), channel.getId()) != null) {
			dao.delete(sToC);
		}
		if (channel.getStatus() == NnChannel.STATUS_SUCCESS && 
			channel.isPublic() && 
			MsoConfigManager.isQueueEnabled(false)) {
			this.processChannelRelatedCounter(set, -1);
		}
	}
	
	public void processChannelRelatedCounter(NnSet set, int cnt) {
		int count = set.getChannelCnt() + cnt;
		if (count > 0) {
			//change set counter			
			set.setChannelCnt(count);
			setDao.save(set);
			//change category counter
			CategoryManager catMngr = new CategoryManager();
			catMngr.changeChannelCntBySet(set, cnt);
		}
	}
	
	public void editChannel(long setId, long channelId, short seq) {
		System.out.println("seq:" + seq);
		NnSetToNnChannelDao dao = new NnSetToNnChannelDao();
		NnSetToNnChannel sToC1 = dao.findBySetAndChannel(setId, channelId);
		NnSetToNnChannel sToC2 = dao.findBySetAndSeq(setId, sToC1.getSeq());
		if (sToC1 != null && sToC2 != null) {
			short seq1 = sToC1.getSeq();
			sToC1.setSeq(seq);
			sToC2.setSeq(seq1);
			dao.save(sToC1);
			dao.save(sToC2);
		}
		if (sToC1 != null && sToC2 == null) {
			sToC1.setSeq(seq);
			dao.save(sToC1);
		}
	}
	
	public void addChannels(NnSet set, List<NnChannel> channels) {
		if (channels == null || channels.size() == 0)
			return;
		NnSetToNnChannelDao dao = new NnSetToNnChannelDao();
		int newChCnt = 0;
		for (NnChannel channel : channels) {
			if (dao.findBySetAndChannel(set.getId(), channel.getId()) == null) {
				NnSetToNnChannel sToC = new NnSetToNnChannel(set.getId(), channel.getId(), (short)0);				
				dao.save(sToC);
				if (channel.getStatus() == NnChannel.STATUS_SUCCESS && channel.isPublic()) {
					newChCnt++;
				}				
			}
		}		
		if (set.isFeatured() && channels.size() == 1) {
			NnSetToNnChannel sToC = dao.findBySetAndChannel(set.getId(), channels.get(0).getId());
			List<NnSetToNnChannel> list = dao.findBySet(set);
			List<NnSetToNnChannel> toBeSaved = new ArrayList<NnSetToNnChannel>();
			short seq = channels.get(0).getSeq();
			for (int i=seq; i<list.size(); i++) {
				NnSetToNnChannel r = list.get(i);
				r.setSeq((short)(i+1));
				toBeSaved.add(r);
			}
			sToC.setSeq(channels.get(0).getSeq());
			toBeSaved.add(sToC);
			dao.saveAll(toBeSaved);	
		}		
		Object[] obj = new Object[2];
		if (newChCnt > 0) {
			if (MsoConfigManager.isQueueEnabled(false)) {
				obj[0] = set;
				obj[1] = newChCnt;
		        //new QueueMessage().fanout("localhost",QueueMessage.SET_CUD_RELATED, obj);
			} else {
				this.processChannelRelatedCounter(set, newChCnt);
				log.info("process channel related");
			}
		}
	}

	public List<NnSet> findSetsByChannel(long channelId) {
		NnSetToNnChannelDao dao = new NnSetToNnChannelDao();
		List<NnSetToNnChannel> list = dao.findByChannel(channelId);
		List<NnSet> result = new ArrayList<NnSet>();
		for (NnSetToNnChannel sToC : list) {
			NnSet set = setDao.findById(sToC.getSetId());
			if (set != null)
				result.add(set);
		}
		return result;
	}
	
	public List<NnSetToNnChannel> findNnSetToNnChannelsBySet(long setId) {
		NnSetToNnChannelDao dao = new NnSetToNnChannelDao();
		return dao.findBySet(setId);
	}
	
	public List<NnSet> findFeaturedSets(String lang) {
		return setDao.findFeaturedSets(lang);
	}
		
	public NnSet findBybeautifulUrl(String url) {
		return setDao.findByBeautifulUrl(url);
	}
	
	public List<NnSet> findByLang(String lang) {
		return setDao.findByLang(lang);
	}
	
	public NnSet findByLangAndSeq(String lang, String seq) {
		return setDao.findByLangAndSeq(lang, Short.parseShort(seq));
	}
	
	public NnSet findByName(String name) {
		return setDao.findByName(name.trim());
	}
	
	public List<NnSet> findAll() {
		return setDao.findAll();
	}

	//!!!
	public List<NnSet> findByIds(List<Long> ids) {
		List<NnSet> results = new ArrayList<NnSet>();
		for (Long id : ids) {
			NnSet s = this.findById(id);
			if (s != null) results.add(s);
		}
		return results;
	}
	
	public List<NnSet> findPublicByIds(List<Long> ids) {
		List<NnSet> results = new ArrayList<NnSet>();
		List<NnSet> candidates = this.findByIds(ids);
		for (NnSet set : candidates) {
			if (set.isPublic()) {
				results.add(set);
			}
		}
		return results;
	}

	public NnSet findById(long setId) {
		return setDao.findById(setId);
	}

	//!!! cache
	public List<NnSet> findAllSystemSets() {		
		List<NnSet> results = new ArrayList<NnSet>();
		ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
		String cacheIdString = "System.NnSets(sortby=lang)";
		@SuppressWarnings("unchecked")
		List<NnSet> cached = (List<NnSet>)CacheFactory.get(cacheIdString);		
		if (cached != null && cached.size() > 0) {
			log.info("get system sets from cache");
			return cached;
		}
		MsoManager msoMngr = new MsoManager();
		Mso nnMso = msoMngr.findNNMso();		
		results = ownershipMngr.findOwnedSetsByMso(nnMso.getId());
		return results;
	}

	public List<NnChannel> findPlayerChannels(NnSet set) {
		NnSetToNnChannelDao dao = new NnSetToNnChannelDao();
		NnChannelManager channelMngr = new NnChannelManager();		
		List<NnSetToNnChannel> relations = dao.findBySet(set);
		ArrayList<NnChannel> results = new ArrayList<NnChannel>();
		
		int i=1;
		for (NnSetToNnChannel sToC : relations) {
			NnChannel c = channelMngr.findById(sToC.getChannelId());			
			if (c != null) {
				if (c.getStatus() == NnChannel.STATUS_SUCCESS && c.isPublic()) {
					if (set.isFeatured())
						c.setSeq(sToC.getSeq());
					else 
						c.setSeq((short)i++);
					results.add(c);
				}
			}
		}
		return results;
	}
	
	public List<NnChannel> findChannels(NnSet set) {
		NnSetToNnChannelDao sToCDao = new NnSetToNnChannelDao();
		NnChannelManager channelMngr = new NnChannelManager();		
		List<NnSetToNnChannel> list = sToCDao.findBySet(set); 
		ArrayList<NnChannel> results = new ArrayList<NnChannel>();		
		for (NnSetToNnChannel nToC : list) {
			NnChannel channel = channelMngr.findById(nToC.getChannelId());
			if (channel != null) {
				channel.setSeq(nToC.getSeq());
				results.add(channel);
			}
		}
		return results;
	}
	
	public List<NnChannel> findChannelsById(long setId) {
		NnSetToNnChannelDao sToCDao = new NnSetToNnChannelDao();
		NnChannelManager channelMngr = new NnChannelManager();		
		List<NnSetToNnChannel> list = sToCDao.findBySet(setId); 
		ArrayList<NnChannel> results = new ArrayList<NnChannel>();
		
		for (NnSetToNnChannel nToC : list) {
			NnChannel channel = channelMngr.findById(nToC.getChannelId());
			if (channel != null) {
				channel.setSeq(nToC.getSeq());
				results.add(channel);
			}
		}
		return results;
	}
	
	public List<NnChannel> findPublicChannelsById(long setId) {
		NnSetToNnChannelDao sToCDao = new NnSetToNnChannelDao();
		NnChannelManager channelMngr = new NnChannelManager();		
		List<NnSetToNnChannel> list = sToCDao.findBySet(setId); 
		ArrayList<NnChannel> results = new ArrayList<NnChannel>();
		
		for (NnSetToNnChannel nToC : list) {
			NnChannel channel = channelMngr.findById(nToC.getChannelId());
			if (channel != null && channel.getStatus() == NnChannel.STATUS_SUCCESS && channel.getProgramCnt() > 0 && channel.isPublic()) {
				channel.setSeq(nToC.getSeq());
				results.add(channel);
			}
		}
		return results;
	}
	
	public int total() {
		return setDao.total();
	}
	
	public int total(String filter) {
		return setDao.total(filter);
	}
	
	public List<NnSet> list(int page, int limit, String sidx, String sord) {
		return setDao.list(page, limit, sidx, sord);
	}
	
	public List<NnSet> list(int page, int limit, String sidx, String sord, String filter) {
		return setDao.list(page, limit, sidx, sord, filter);
	}
	
}
