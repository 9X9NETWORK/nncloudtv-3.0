package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.QueueMessage;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryChannel;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.SubscriptionLog;

@Service
public class NnChannelManager {

	protected static final Logger log = Logger.getLogger(NnChannelManager.class.getName());
	
	private NnChannelDao channelDao = new NnChannelDao();
	
	/**
	 * @@@IMPORTANT 
	 * setProgramCount will be done automatically in MsoProgramManager when a program is added.
	 * If necessary to manually change programCount, please do with caution.
	 * 
	 * @@@IMPORTANT
	 * sourceURL is not supposed to be duplicated. Duplication check is your responsibility.   
	 */
	public void create(NnChannel channel, List<Category> categories) {
		Date now = new Date();		
		channel.setCreateDate(now);
		channel.setUpdateDate(now);
		channelDao.save(channel);

		Object[] obj = {channel, categories};
		new QueueMessage().fanout("localhost", QueueMessage.CHANNEL_CREATE_RELATED, obj);
	}	
	
	/**
	 * There's chance category's channelCounter is wrong, but so far the chance is small: 
	 * 1. when channel from public to non-public
	 * 2. when channel from status success to non-success
	 * Currently the counter is mainly dealt in MsoProgramManager.create() 
	 * Will need to fix it in transaction
	 *       
	 */
	public NnChannel save(NnChannel channel) {
		channel = channelDao.save(channel);
		return channel;
	}		
	
	/**
	 * No deletion so we can keep track of blacklist urls 
	 */
	public void delete(NnChannel channel) {
	}
		
	public NnChannel createChannelFromUrl(String sourceUrl, NnUser user, List<Category> categories, HttpServletRequest req) {
		//!!!
		if (sourceUrl == null) {return null;}
		NnChannel channel = new NnChannel(sourceUrl, user.getId());
		channel.setContentType(this.getContentTypeByUrl(sourceUrl));
		if (channel.getContentType() == NnChannel.CONTENTTYPE_FACEBOOK) {
			FacebookLib lib = new FacebookLib();
			String[] info = lib.getFanpageInfo(sourceUrl);
			channel.setName(info[0]);
			channel.setImageUrl(info[1]);
			channel.setStatus(NnChannel.STATUS_SUCCESS);			
		} else {
			channel.setImageUrl("/WEB-INF/../images/processing.png");
			channel.setName("Processing");
			channel.setStatus(NnChannel.STATUS_PROCESSING);
		}
		channel.setUserId(user.getId());
		channel.setPublic(false);
		this.create(channel, categories);
		//<<< queue
		if (req != null) {
			if (channel.getContentType() != NnChannel.CONTENTTYPE_FACEBOOK) { //!!!
				TranscodingService tranService = new TranscodingService();
				tranService.submitToTranscodingService(channel.getId(), sourceUrl, req);
			}
		}
		return channel;
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
		return type;
	}		
			
	public boolean isCounterQualified(NnChannel channel) {
		boolean qualified = false;
		if (channel.getStatus() == NnChannel.STATUS_SUCCESS &&
			channel.getProgramCount() > 0 &&
			channel.isPublic()) {
			qualified = true;
		}
		return qualified;
	}

	public NnChannel findBySourceUrlSearch(String url) {
		if (url == null) {return null;}
		return channelDao.findBySourceUrlSearch(url.toLowerCase());
	}
	
	public NnChannel findById(long id) {
		NnChannel channel = channelDao.findById(id);
		return channel;
	}

	public List<NnChannel> findMsoDefaultChannels(long msoId, boolean needSubscriptionCnt) {		
		//find msoIpg
		MsoIpgManager msoIpgMngr = new MsoIpgManager();
		SubscriptionLogManager sublogMngr = new SubscriptionLogManager();		
		List<MsoIpg>msoIpg = msoIpgMngr.findAllByMsoId(msoId);		
		System.out.println("<<<<<< msoIpg retrival: >>>>>>" + msoIpg.size());
		//retrieve channels
		List<NnChannel> channels = new ArrayList<NnChannel>();
		for (MsoIpg i : msoIpg) {
			NnChannel channel = this.findById(i.getChannelId());
			if (channel != null) {
				channel.setType(i.getType());
				channel.setSeq(i.getSeq());
				if (needSubscriptionCnt) {
					SubscriptionLog sublog = sublogMngr.findByMsoIdAndChannelId(msoId, channel.getId());
					channel.setSubscriptionCount(sublog.getCount());
				}
				channels.add(channel);
			}
		}
		return channels;
	}	
	
	//!!! different channel might have program count == 0
	public List<NnChannel> findGoodChannelsByCategoryId(long categoryId) {
		//channels within a category
		CategoryChannelManager ccMngr = new CategoryChannelManager();
		CategoryManager categoryMngr = new CategoryManager();
		SubscriptionLogManager sublogMngr = new SubscriptionLogManager();
		List<CategoryChannel> ccs = (List<CategoryChannel>) ccMngr.findAllByCategoryId(categoryId);

		//retrieve channels
		List<NnChannel> channels = new ArrayList<NnChannel>();
		for (CategoryChannel cc : ccs) {
			NnChannel channel = this.findById(cc.getChannelId());
			if (channel != null && 
				channel.getStatus() == NnChannel.STATUS_SUCCESS &&  
				channel.isPublic()) { 
				//category is used to find this channel's mso, then find corresponding subscription count
				Category category  = categoryMngr.findById(cc.getCategoryId());
				if (category != null) {
					SubscriptionLog sublog = sublogMngr.findByMsoIdAndChannelId(category.getMsoId(), channel.getId());			
				    if (sublog != null) {channel.setSubscriptionCount(sublog.getCount());}
				}
				channels.add(channel);
			}
		}				
		
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

	//!!!
	public String verifyUrl(String url) {
		if (url == null) return null;
		TranscodingService tranService = new TranscodingService();
		if (!url.contains("youtube.com")) {
			if (url.contains("deimos3.apple.com")) { //temp fix for demo
				return url;
			}
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
	
}
