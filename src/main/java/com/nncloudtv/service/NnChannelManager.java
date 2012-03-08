package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.QueueMessage;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CntSubscribe;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSet;

@Service
public class NnChannelManager {

	protected static final Logger log = Logger.getLogger(NnChannelManager.class.getName());
	
	private NnChannelDao channelDao = new NnChannelDao();
	
	public NnChannel create(String sourceUrl, HttpServletRequest req) {
		if (sourceUrl == null) 
			return null;
		if (this.verifyUrl(sourceUrl) == null) 
			return null;
		NnChannel channel = this.findBySourceUrl(sourceUrl);		
		if (channel != null)
			return channel; 
		channel = new NnChannel(sourceUrl);
		channel.setContentType(this.getContentTypeByUrl(sourceUrl));
		if (channel.getContentType() == NnChannel.CONTENTTYPE_FACEBOOK) {
			FacebookLib lib = new FacebookLib();
			String[] info = lib.getFanpageInfo(sourceUrl);
			channel.setName(info[0]);
			channel.setImageUrl(info[1]);
			channel.setStatus(NnChannel.STATUS_SUCCESS);			
		} else {
			channel.setImageUrl(NnChannel.PROCESSING_IMAGE_URL);
			channel.setName("Processing");
			channel.setStatus(NnChannel.STATUS_PROCESSING);
			if (channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL) {
				String url = channel.getSourceUrl();
				String name = YouTubeLib.getYouTubeChannelName(url);
				log.info("youtube: " + name);
				Map<String, String> info = YouTubeLib.getYouTubeEntry(name, true);
				if (!info.get("status").equals(String.valueOf(NnStatusCode.SUCCESS)))
					return null;
				if (info.get("title") != null)
					channel.setName(info.get("title"));
				if (info.get("description") != null)
					channel.setIntro(info.get("description"));
				if (info.get("thumbnail") != null)
					channel.setImageUrl(info.get("thumbnail"));
			} else if (channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_PLAYLIST) {
				String url = channel.getSourceUrl();
				String name = YouTubeLib.getYouTubeChannelName(url);
				log.info("playlist: " + name);
				Map<String, String> info = YouTubeLib.getYouTubeEntry(name, false);
				if (!info.get("status").equals(String.valueOf(NnStatusCode.SUCCESS)))
					return null;
				if (info.get("title") != null)
					channel.setName(info.get("title"));
				if (info.get("description") != null)
					channel.setIntro(info.get("description"));
				if (info.get("thumbnail") != null)
					channel.setImageUrl(info.get("thumbnail"));
			}
			
		}
		channel.setPublic(false);
		this.save(channel);
		return channel;
	}
	
	public NnChannel save(NnChannel channel) {
		NnChannel original = channelDao.findById(channel.getId());
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
		channel = channelDao.save(channel);
		NnChannel[] channels = {original, channel};
		if (MsoConfigManager.isQueueEnabled(true)) {
	        new QueueMessage().fanout("localhost",QueueMessage.CHANNEL_CREATE_RELATED, channels);
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
	
	public static List<NnChannel> search(String queryStr) {
		return NnChannelDao.searchChannelEntries(queryStr);		
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
		return channelDao.findBySourceUrl(url);
	}
	
	public NnChannel findById(long id) {
		NnChannel channel = channelDao.findById(id);
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

	public List<NnChannel> findAllByChannelIds(List<Long> channelIds) {
		List<NnChannel> channels = new ArrayList<NnChannel>();
		for (Long id : channelIds) {
			NnChannel channel = this.findById(id);
			if (channel != null) channels.add(channel);
		}
		return channels;		
	}

	public String verifyUrl(String url) {
		if (url == null) return null;
		TranscodingService tranService = new TranscodingService();
		if (!url.contains("youtube.com")) {
			if (url.contains("facebook.com")) {
				return url;
			}
			String podcastInfo[] = tranService.getPodcastInfo(url);			
			if (!podcastInfo[0].equals("200") || !podcastInfo[1].contains("xml")) {
				log.info("invalid url:" + url);		
				return null;
			}
		} else {
			//url = YouTubeLib.formatCheck(url);
		}
		return url;
	}

	public static short getDefaultSorting(NnChannel c) {
		short sorting = NnChannel.SORT_NEWEST_TO_OLDEST; 
		if (c.getContentType() == NnChannel.CONTENTTYPE_MAPLE_SOAP || 
			c.getContentType() == NnChannel.CONTENTTYPE_MAPLE_VARIETY)
			sorting = NnChannel.SORT_MAPEL;
		return sorting;
	}
	
}
