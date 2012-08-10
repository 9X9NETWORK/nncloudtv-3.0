package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.dao.NnProgramDao;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.PiwikLib;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CntSubscribe;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnUser;

@Service
public class NnChannelManager {

	protected static final Logger log = Logger.getLogger(NnChannelManager.class.getName());
	
	private NnChannelDao dao = new NnChannelDao();
	
	public NnChannel create(String sourceUrl, String name, HttpServletRequest req) {
		if (sourceUrl == null) 
			return null;
		String url = this.verifyUrl(sourceUrl);
		log.info("valid url=" + url);
		if (url == null) 
			return null;
		
		NnChannel channel = this.findBySourceUrl(url);		
		if (channel != null) {
			log.info("submit a duplicate channel:" + channel.getId());
			return channel; 
		}
		channel = new NnChannel(url);
		channel.setContentType(this.getContentTypeByUrl(url));
		log.info("new channel contentType:" + channel.getContentType());
		if (channel.getContentType() == NnChannel.CONTENTTYPE_FACEBOOK) {
			FacebookLib lib = new FacebookLib();
			String[] info = lib.getFanpageInfo(url);
			channel.setName(info[0]);
			channel.setImageUrl(info[1]);
			channel.setStatus(NnChannel.STATUS_SUCCESS);			
		} else {
			channel.setImageUrl(NnChannel.IMAGE_PROCESSING_URL);
			channel.setName("Processing");
			channel.setStatus(NnChannel.STATUS_PROCESSING);
			if (channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL ||
				channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_PLAYLIST) {
				Map<String, String> info = null;
				String youtubeName = YouTubeLib.getYouTubeChannelName(url);
				if (channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL) {
					info = YouTubeLib.getYouTubeEntry(youtubeName, true);
				} else {
					info = YouTubeLib.getYouTubeEntry(youtubeName, false);
				}
				if (!info.get("status").equals(String.valueOf(NnStatusCode.SUCCESS)))
					return null;
				if (name != null)
					channel.setName(name);
				String oriName = info.get("title");
				if (info.get("title") != null) {
					channel.setOriName(oriName);
					if (name == null)
						channel.setName(oriName);
				}
				if (info.get("description") != null)
					channel.setIntro(info.get("description"));
				if (info.get("thumbnail") != null)
					channel.setImageUrl(info.get("thumbnail"));
			}			
		}
		channel.setPublic(false);
		channel = this.save(channel);
		if (channel.getContentType() == NnChannel.CONTENTTYPE_MAPLE_SOAP ||
			channel.getContentType() == NnChannel.CONTENTTYPE_MAPLE_VARIETY ||
			channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_SPECIAL_SORTING) {
			new DepotService().submitToTranscodingService(channel.getId(), channel.getSourceUrl(), req);								
		}
		
		// piwik
		if (channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL || channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_PLAYLIST) {
			
			PiwikLib.createPiwikSite(0, channel.getId());
		}
		
		return channel;
	}

	public void saveFavorite(NnUser user, String fileUrl, String name, String imageUrl, short type) {
		NnChannel c = dao.findByUserId(user.getId());
		if (c == null) {
			c = new NnChannel("'s Favorite", "", "");
			c.setContentType(NnChannel.CONTENTTYPE_YOUR_FARORITE);
			dao.save(c);
		}
		NnProgramDao pDao = new NnProgramDao();
		NnProgram p = pDao.findFavorite(c.getId(), fileUrl);
		if (p == null) {
			p = new NnProgram(name, "", imageUrl, type);
			p.setFileUrl(fileUrl);
			pDao.save(p);
		}
	}																																																																																																																																																																																																																																																																																																																																																															
	
	public NnChannel save(NnChannel channel) {
		NnChannel original = dao.findById(channel.getId());
		Date now = new Date();
		if (channel.getCreateDate() == null)
			channel.setCreateDate(now);
		channel.setUpdateDate(now);		
		if (channel.getIntro() != null) {
			channel.setIntro(channel.getIntro().replaceAll("\n", ""));
			channel.setIntro(channel.getIntro().replaceAll("\t", " "));
			if (channel.getIntro().length() > 500)
				channel.getIntro().substring(0, 499);
		}
		if (channel.getName() != null) {
			channel.setName(channel.getName().replaceAll("\n", ""));
			channel.setName(channel.getName().replaceAll("\t", " "));
		}
		channel = dao.save(channel);
		NnChannel[] channels = {original, channel};
		if (MsoConfigManager.isQueueEnabled(true)) {
	        //new QueueMessage().fanout("localhost",QueueMessage.CHANNEL_CUD_RELATED, channels);
		} else {
			this.processChannelRelatedCounter(channels);
		}
		return channel;
	}		
	
	public void processChannelRelatedCounter(NnChannel[] channels) {
		NnChannel original = channels[0];
		NnChannel channel = channels[1];
		if (original == null && channel.getStatus() == NnChannel.STATUS_SUCCESS && channel.isPublic()) {
			NnSetManager setMngr = new NnSetManager();
			CategoryManager catMngr = new CategoryManager();
			List<NnSet> sets = setMngr.findSetsByChannel(channel.getId());
			List<Category> categories = catMngr.findBySets(sets);
			for (NnSet set : sets) {
				set.setChannelCnt(set.getChannelCnt()+1);
			}
			for (Category c : categories) {
				c.setChannelCnt(c.getChannelCnt()+1);
			}
		}
	}
		
	public static List<NnChannel> search(String queryStr, boolean all) {
		return NnChannelDao.search(queryStr, all);		
	}
	
	/**
	 * No deletion so we can keep track of blacklist urls 
	 */
	public void delete(NnChannel channel) {
	}		
	
	//the url has to be verified(verifyUrl) first
	public short getContentTypeByUrl(String url) {
		short type = NnChannel.CONTENTTYPE_PODCAST;
		if (url.contains("http://www.youtube.com"))
			type = NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL;
		if (url.contains("http://www.youtube.com/view_play_list?p="))
			type = NnChannel.CONTENTTYPE_YOUTUBE_PLAYLIST;
		if (url.contains("facebook.com")) 
			type = NnChannel.CONTENTTYPE_FACEBOOK;
		if (url.contains("http://www.maplestage.net/show"))
			type = NnChannel.CONTENTTYPE_MAPLE_VARIETY;
		if (url.contains("http://www.maplestage.net/drama"))
			type = NnChannel.CONTENTTYPE_MAPLE_SOAP;
		return type;
	}		
			
	public boolean isCounterQualified(NnChannel channel) {
		boolean qualified = false;
		if (channel.getStatus() == NnChannel.STATUS_SUCCESS &&
			channel.getProgramCnt() > 0 &&
			channel.isPublic()) {
			qualified = true;
		}
		return qualified;
	}

	public NnChannel findBySourceUrl(String url) {
		if (url == null) {return null;}
		return dao.findBySourceUrl(url);
	}
	
	public NnChannel findById(long id) {
		NnChannel channel = dao.findById(id);
		return channel;
	}

	public List<NnChannel> findMsoDefaultChannels(long msoId, boolean needSubscriptionCnt) {		
		//find msoIpg
		MsoIpgManager msoIpgMngr = new MsoIpgManager();
		CntSubscribeManager cntMngr = new CntSubscribeManager();		
		List<MsoIpg>msoIpg = msoIpgMngr.findAllByMsoId(msoId);
		//retrieve channels
		List<NnChannel> channels = new ArrayList<NnChannel>();
		for (MsoIpg i : msoIpg) {
			NnChannel channel = this.findById(i.getChannelId());
			if (channel != null) {
				channel.setType(i.getType());
				channel.setSeq(i.getSeq());
				if (needSubscriptionCnt) {
					CntSubscribe cnt = cntMngr.findByChannel(channel.getId());
					channel.setSubscriptionCnt(cnt.getCnt());
				}
				channels.add(channel);
			}
		}
		return channels;
	}	
	
	//!!! different channel might have program count == 0
	public List<NnChannel> findGoodChannelsByCategoryId(long categoryId) {
		//channels within a category
		//CategoryChannelManager ccMngr = new CategoryChannelManager();
		List<NnChannel> channels = new ArrayList<NnChannel>();
		/*
		List<CategoryChannel> ccs = (List<CategoryChannel>) ccMngr.findAllByCategoryId(categoryId);

		//retrieve channels
		List<NnChannel> channels = new ArrayList<NnChannel>();
		for (CategoryChannel cc : ccs) {
			NnChannel channel = this.findById(cc.getChannelId());
			if (channel != null && 
				channel.getStatus() == NnChannel.STATUS_SUCCESS &&  
				channel.isPublic()) { 
				channels.add(channel);
			}
		}				
		*/
		return channels; 
	}

	public List<NnChannel> findByType(short type) {
		return dao.findByType(type);		
	}
	
	public List<NnChannel> findMaples() {
		List<NnChannel> variety = this.findByType(NnChannel.CONTENTTYPE_MAPLE_VARIETY);
		List<NnChannel> soap = this.findByType(NnChannel.CONTENTTYPE_MAPLE_SOAP);
		List<NnChannel> channels = new ArrayList<NnChannel>();
		channels.addAll(variety);
		channels.addAll(soap);
		return channels;
	}
	
	public List<NnChannel> findFeatured() {
		List<NnChannel> channels = new NnSetManager().findChannelsById(3);
		return channels;
	}
	
	public List<NnChannel> findTrending() {
		List<NnChannel> channels = new NnSetManager().findChannelsById(4);		
		return channels;
	}

	public List<NnChannel> findHot() {
		List<NnChannel> channels = new NnSetManager().findChannelsById(5);		
		return channels;
	}

	public List<NnChannel> findRecommended() {
		List<NnChannel> channels = new NnSetManager().findChannelsById(6);		
		return channels;
	}
	
	public List<NnChannel> findByChannelIds(List<Long> channelIds) {
		List<NnChannel> channels = new ArrayList<NnChannel>();
		for (Long id : channelIds) {
			NnChannel channel = this.findById(id);
			if (channel != null) channels.add(channel);
		}
		return channels;		
	}
	
	public List<NnChannel> findByStatus(short status) {
		List<NnChannel> channels = dao.findAllByStatus(status);		
		return channels;
	}
	
	public List<NnChannel> findAll() {
		return dao.findAll();
	}
	
	public List<NnChannel> list(int page, int limit, String sidx, String sord) {
		return dao.list(page, limit, sidx, sord);
	}
	
	public List<NnChannel> list(int page, int limit, String sidx, String sord, String filter) {
		return dao.list(page, limit, sidx, sord, filter);
	}
	
	public int total() {
		return dao.total();
	}
	
	public int total(String filter) {
		return dao.total(filter);
	}

	public String verifyUrl(String url) {
		if (url == null) return null;
		if (!url.contains("http://") && !url.contains("https://"))
			return null;		
		if (url.contains("youtube.com")) {
			return YouTubeLib.formatCheck(url);
		} else if (url.contains("facebook.com")) {
			return url;
		} else if (url.contains("www.maplestage.net")) {
		//} else if (url.contains("www.maplestage.net") && !url.contains("9x9.tv")) {
			return url;
		}
		return null;
	}
	
	public static short getDefaultSorting(NnChannel c) {
		short sorting = NnChannel.SORT_NEWEST_TO_OLDEST; 
		if (c.getContentType() == NnChannel.CONTENTTYPE_MAPLE_SOAP || 
			c.getContentType() == NnChannel.CONTENTTYPE_MAPLE_VARIETY || 
			c.getContentType() == NnChannel.CONTENTTYPE_MIXED)
			sorting = NnChannel.SORT_DESIGNATED;
		return sorting;
	}
	
	public List<NnChannel> findUnUniqueSourceUrl() {
		List<NnChannel> channels = this.findAll();
		HashSet<String> set = new HashSet<String>();
		List<NnChannel> bad = new ArrayList<NnChannel>();
		for (NnChannel c : channels) {
			if (!set.contains(c.getSourceUrl())) {
				set.add(c.getSourceUrl());
			} else {
				log.info("duplicate source url:" + c.getSourceUrl());
				bad.add(c);
			}
		}
		return bad;
	}
	
	/** unmark if needed
	public void calibrateProgramCnt(NnChannel channel) {
		
		NnProgramManager programMngr = new NnProgramManager();
		List<NnProgram> programs = programMngr.findByChannel(channel.getId());
		int counter = 0;
		for (NnProgram program : programs) {
			if (program.getStatus() == NnProgram.STATUS_OK) {
				counter++;
			}
		}
		// not save yet
		channel.setProgramCnt(counter);
		log.info("programCnt: " + counter);
	} **/
}
