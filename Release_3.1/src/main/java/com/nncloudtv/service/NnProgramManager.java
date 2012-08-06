package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnProgramDao;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.QueueFactory;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnChannelAutosharing;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.SnsAuth;
import com.nncloudtv.web.json.facebook.FBPost;

@Service
public class NnProgramManager {
	
	protected static final Logger log = Logger.getLogger(NnProgramManager.class.getName());
	private static MessageSource messageSource = new ClassPathXmlApplicationContext("locale.xml");
	
	private NnProgramDao programDao = new NnProgramDao();
	
	public void create(NnChannel channel, NnProgram program) {		
		Date now = new Date();
		program.setCreateDate(now);
		program.setUpdateDate(now);
		program.setChannelId(channel.getId());
		programDao.save(program);
		this.processCache(channel.getId());
		
		//!!!!! clean plus "hook, auto share to facebook"
		//set channel count
		int count = channel.getProgramCnt() + 1;
		channel.setProgramCnt(count);
		NnChannelManager channelMngr = new NnChannelManager();
		channelMngr.save(channel);

		//if the channel's original programCount is zero, its count will not be in the category, adding it now.
		if (count == 1) {
			CategoryManager categoryMngr = new CategoryManager();
			System.out.println("mso program manager, channel create, addChannelCount");
			categoryMngr.addChannelCounter(channel);
		}
		
		// hook, auto share to facebook
		AutosharingService sharingService = new AutosharingService();
		SnsAuthManager snsMngr = new SnsAuthManager();
		FBPost fbPost = new FBPost(program.getName(), program.getIntro(), program.getImageUrl());
		MsoManager msoMngr = new MsoManager();
		String url = "http://" + MsoConfigManager.getServerDomain() + "/view?channel=" + channel.getId() + "&episode=" + program.getId();
		fbPost.setLink(url);
		log.info("share link: " + url);
		if (program.getComment() != null) {
			fbPost.setMessage(program.getComment());
		}
		
		List<NnChannelAutosharing> channelAutosharings = sharingService.findByChannelAndType(channel.getId(), SnsAuth.TYPE_FACEBOOK);
		log.info("FB autosharing count = " + channelAutosharings.size());
		for (NnChannelAutosharing autosharing : channelAutosharings) {
			SnsAuth snsAuth = snsMngr.findFacebookAuthByMso(autosharing.getMsoId());
			Mso mso = msoMngr.findById(autosharing.getMsoId());
			if (mso.getLang() != null && mso.getLang().equals("en")) {
				fbPost.setCaption(messageSource.getMessage("cms.autosharing.episode_added", null, Locale.ENGLISH));
			} else {
				fbPost.setCaption(messageSource.getMessage("cms.autosharing.episode_added", null, Locale.TRADITIONAL_CHINESE));
			}
			if (snsAuth != null && snsAuth.isEnabled()) {
				if (autosharing.getTarget() != null && autosharing.getParameter() != null) {
					fbPost.setFacebookId(autosharing.getTarget());
					fbPost.setAccessToken(autosharing.getParameter());
				} else {
					fbPost.setFacebookId(snsAuth.getToken());
					fbPost.setAccessToken(snsAuth.getSecret());
				}
				QueueFactory.add("/CMSAPI/postToFacebook", fbPost);
			}
		}
		
		// hook, auto share to twitter
		channelAutosharings = sharingService.findByChannelAndType(channel.getId(), SnsAuth.TYPE_TWITTER);
		log.info("twitter autosharing count = " + channelAutosharings.size());
		for (NnChannelAutosharing autosharing : channelAutosharings) {
			SnsAuth snsAuth = snsMngr.findTitterAuthByMsoId(autosharing.getMsoId());
			Mso mso = msoMngr.findById(autosharing.getMsoId());
			if (mso.getLang() != null && mso.getLang().equals("en")) {
				fbPost.setCaption(messageSource.getMessage("cms.autosharing.episode_added", null, Locale.ENGLISH));
			} else {
				fbPost.setCaption(messageSource.getMessage("cms.autosharing.episode_added", null, Locale.TRADITIONAL_CHINESE));
			}
			if (snsAuth != null && snsAuth.isEnabled()) {
				
				fbPost.setFacebookId(snsAuth.getToken());
				fbPost.setAccessToken(snsAuth.getSecret());
				
				QueueFactory.add("/CMSAPI/postToTwitter", fbPost);
			}
		}
	} 

	/**
	 * Save programs massively, and keep updateDate untouched
	 * 
	 * why? processCache() takes too much time when saving individually
	 * 
	 * @param programs
	 * @return programs
	 */
	public List<NnProgram> save(List<NnProgram> programs) {
		
		long channelId = 0;
		
		for (NnProgram program : programs) {
			
			Date now = new Date();
			if (program.getCreateDate() == null)
				program.setCreateDate(now);
			if (program.getUpdateDate() == null) {
				program.setUpdateDate(now);
			}
			
			program = programDao.save(program);
			
			if (channelId != program.getChannelId()) {
				channelId = program.getChannelId();
				processCache(channelId);
			}
		}
		
		return programs;
	}
	
	public NnProgram save(NnProgram program) {
		Date now = new Date();
		if (program.getCreateDate() == null)
			program.setCreateDate(now);
		program.setUpdateDate(now); // NOTE: a trying to modify program update time (from admin) will be omitted by this, use "untouched" save() instread
		program = programDao.save(program);
		this.processCache(program.getChannelId());
		return program;
	}

	public void delete(NnProgram program) {
		this.processCache(program.getChannelId());		
		programDao.delete(program);		
	}
	
	public List<NnProgram> findPlayerProgramsByChannel(long channelId) {
		List<NnProgram> programs = new ArrayList<NnProgram>();
		NnChannel c = new NnChannelManager().findById(channelId);
		if (c == null)
			return programs;
		programs = programDao.findPlayerProgramsByChannel(c);
		return programs;
	}	

	public String findPlayerProgramInfoByChannel(long channelId) {
		String cacheKey = "nnprogram(" + channelId + ")";
		String result = (String)CacheFactory.get(cacheKey);
		if (CacheFactory.isRunning && result != null) { 
			log.info("<<<<< retrieve program info from cache >>>>>");
			return result;
		}		
		
		log.info("nothing in the cache");		
		List<NnProgram> programs = this.findPlayerProgramsByChannel(channelId);
		log.info("channel id:" + channelId + "; program size:" + programs.size());
		String str = this.composeProgramInfoStr(programs);
		if (CacheFactory.isRunning) { 
			CacheFactory.set(cacheKey, str);
		}
		return str;
	}	
	
	/**
	 * find playerAPI's programInfo string
	 * @param channelId system channel id
	 * @param sidx start index
	 * @param limit number of records
	 * @return program info string 
	 */
	public String findPlayerProgramInfoByChannel(long channelId, long sidx, long limit) {
		String result = this.findPlayerProgramInfoByChannel(channelId);
		return this.composeLimitProgramInfoStr(result, sidx, limit);
	}	
	
	public List<NnProgram> findPlayerProgramsByChannels(List<Long>channelIds) {
		log.info("requested channelIds size:" + channelIds.size());
		List<NnProgram> programs = new ArrayList<NnProgram>();
		log.info("remaining channel size not in the cache:" + channelIds.size());
		if (channelIds.size() > 0) {
			List<NnProgram> list = programDao.findPlayerProgramsByChannels(channelIds);
			programs.addAll(list);
		}
		return programs;
	}

	/**
	 * Get a position of an episode in a channel.
	 * Works only for fixed sorting channel such as maplestage channel or 9x9 channel.
	 *  
	 * @param player program info string
	 * @param programId program key
	 * @return program id position
	 */
	public int getEpisodeIndex(String input, String programId) {
		String[] lines = input.split("\n");
		int index = 0;
		for (int i=0; i<lines.length; i++) {
			String[] tabs = lines[i].split("\t");
			if (tabs[1].equals(programId)) {
				index = i+1;
				i = lines.length + 1;
			}
		}		
		return index;
	}
	
	public short getContentType(NnProgram program) {
		if (program.getAudioFileUrl() != null)
			return NnProgram.CONTENTTYPE_RADIO;
		if (program.getFileUrl().contains("youtube.com")) 
			return NnProgram.CONTENTTYPE_YOUTUBE; 		
		return NnProgram.CONTENTTYPE_DIRECTLINK; 	
	}
	
	private String composeLimitProgramInfoStr(String input, long sidx, long limit) {
		if (sidx == 0 && limit == 0)
			return input;
		String[] lines = input.split("\n");
		String result = "";
		long start = sidx - 1;
		long end = start + limit;
		for (int i=0; i<lines.length; i++) {
			if (i>=start && i<end) {
				result += lines[i] + "\n";
			}
			if (i > end) {
				return result;
			}
		}		
		return result;
	}
	
	public String composeProgramInfoStr(List<NnProgram> programs) {		
		String output = "";		
		String regexCache = "^(http|https)://(9x9cache.s3.amazonaws.com|s3.amazonaws.com/9x9cache)";
		String regexPod = "^(http|https)://(9x9pod.s3.amazonaws.com|s3.amazonaws.com/9x9pod)";
		String cache = "http://cache.9x9.tv";
		String pod = "http://pod.9x9.tv";
		for (NnProgram p : programs) {
			//file urls
			String url1 = p.getFileUrl();
			String url2 = ""; //not used for now
			String url3 = ""; //not used for now
			String url4 = p.getAudioFileUrl();
			String imageUrl = p.getImageUrl();
			String imageLargeUrl = p.getImageLargeUrl();
			if (imageUrl == null) {imageUrl = "";}
			if (imageLargeUrl == null) {imageLargeUrl = "";}
			//!!!!
			//if (config.getValue().equals(MsoConfig.CDN_AKAMAI)) {
			    if (url1 != null) {
					url1 = url1.replaceFirst(regexCache, cache);
					url1 = url1.replaceAll(regexPod, pod);
			    }
				url2 = url2.replaceFirst(regexCache, cache);
				url2 = url2.replaceAll(regexPod, pod);
				url3 = url3.replaceFirst(regexCache, cache);
				url3 = url3.replaceAll(regexPod, pod);
				if (url4 != null) {
					url4 = url4.replaceFirst(regexCache, cache);
					url4 = url4.replaceAll(regexPod, pod);
				}
				if (imageUrl != null) {
					imageUrl = imageUrl.replaceFirst(regexCache, cache);
					imageUrl = imageUrl.replaceAll(regexPod, pod);
				}
				if (imageLargeUrl != null) {
					imageLargeUrl = imageLargeUrl.replaceFirst(regexCache, cache);
					imageLargeUrl = imageLargeUrl.replaceAll(regexPod, pod);
				}
			//}
					
			//intro
			String intro = p.getIntro();			
			if (intro != null) {
				int introLenth = (intro.length() > 256 ? 256 : intro.length()); 
				intro = intro.replaceAll("\\s", " ");				
				intro = intro.substring(0, introLenth);
			}
			
			//the rest
			String[] ori = {String.valueOf(p.getChannelId()), 
					        String.valueOf(p.getId()), 
					        p.getName(), 
					        intro,
					        String.valueOf(p.getContentType()), 
					        p.getDuration(),
					        imageUrl,
					        imageLargeUrl,
					        url1, 
					        url2, 
					        url3, 
					        url4,			
					        String.valueOf(p.getUpdateDate().getTime()),
					        p.getComment()};
			output = output + NnStringUtil.getDelimitedStr(ori);
			output = output.replaceAll("null", "");
			output = output + "\n";
		}
		return output;		
	}
	
	
	public NnProgram findByStorageId(String storageId) {
		return programDao.findByStorageId(storageId);
	}

	public NnProgram findById(long id) {
		NnProgram program = programDao.findById(id);
		return program;
	}

	public List<NnProgram> findByChannel(long channelId) {
		return programDao.findByChannel(channelId);
	}
	
	public List<NnProgram> findSubscribedPrograms(NnUser user) {
		NnUserSubscribeManager subService = new NnUserSubscribeManager();			
		List<NnChannel> channels = subService.findSubscribedChannels(user);
		List<NnProgram> programs = new ArrayList<NnProgram>();
		List<Long> channelIds = new ArrayList<Long>();
		for (NnChannel c : channels) {
			channelIds.add(c.getId());
		}
		programs = this.findPlayerProgramsByChannels(channelIds);
		return programs;
	}
	
	public String processCache(long channelId) {
		List<NnProgram> programs = this.findPlayerProgramsByChannel(channelId);
		log.info("channel id:" + channelId + "; program size:" + programs.size());
		String cacheKey = this.getCacheKey(channelId);
		String str = this.composeProgramInfoStr(programs); 
		CacheFactory.set(cacheKey, str);
		return str;
	}	
	
	public String retrieveCache(String key) {
		log.info("cache key:" + key);
		String value = (String)CacheFactory.get(key);
		return value;
	}
	
	//example: nnprogram(channel_id)
	public String getCacheKey(long channelId) {
		String str = "nnprogram(" + channelId + ")"; 
		return str;
	}
	
	public int total() {
		return programDao.total();
	}
	
	public int total(String filter) {
		return programDao.total(filter);
	}
	
	public List<NnProgram> list(int page, int limit, String sidx, String sord) {
		return programDao.list(page, limit, sidx, sord);
	}
	
	public List<NnProgram> list(int page, int limit, String sidx, String sord, String filter) {
		return programDao.list(page, limit, sidx, sord, filter);
	}
}
