package com.nncloudtv.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.web.json.transcodingservice.PostResponse;
import com.nncloudtv.web.json.transcodingservice.PostUrl;
import com.nncloudtv.web.json.transcodingservice.RtnChannel;
import com.nncloudtv.web.json.transcodingservice.RtnProgram;
import com.nncloudtv.web.json.transcodingservice.RtnProgramItem;

@Service
public class DepotService {
	protected static final Logger log = Logger.getLogger(DepotService.class.getName());	
	
	public PostResponse handleException (Exception e) {
		PostResponse resp = new PostResponse(String.valueOf(NnStatusCode.ERROR), "Error");		
		if (e.getClass().equals(NoSuchMessageException.class)) {			
			resp.setErrorCode(String.valueOf(NnStatusCode.OUTPUT_NO_MSG_DEFINED));
			resp.setErrorReason("oops, system does not define this error msg");						
		}
		NnLogUtil.logException((Exception) e);
		return resp;
	}	
	
	private short convertStatus(short tranStatus) {
		switch (tranStatus) {
		  case 0: 
			  return NnChannel.STATUS_SUCCESS;
		  case 1:
			  return NnChannel.STATUS_NNVMSO_JSON_ERROR;
		  case 2:
			  return NnChannel.STATUS_INVALID_FORMAT;
		  case 4:
			  return NnChannel.STATUS_TRANSCODING_DB_ERROR;
		  case 5:
			  return NnChannel.STATUS_NO_VALID_EPISODE;
		  case 6: 
			  return NnChannel.STATUS_URL_NOT_FOUND;
		  case 7:
			  return NnChannel.STATUS_INVALID_FORMAT;
		}
		return NnChannel.STATUS_SUCCESS;				
	}
	
	public PostResponse updateChannel(RtnChannel podcast) {
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel channel = channelMngr.findById(Long.parseLong(podcast.getKey()));
		if (channel == null) {
			return new PostResponse(String.valueOf(NnStatusCode.CHANNEL_INVALID), "CHANNEL_INVALID");
		}
		if (!podcast.getErrorCode().equals(String.valueOf(NnChannel.STATUS_SUCCESS))) {
			channel.setPublic(false);
			channel.setStatus(this.convertStatus(Short.valueOf(podcast.getErrorCode())));
			channel.setErrorReason(podcast.getErrorReason());
			channelMngr.save(channel);
			return new PostResponse(String.valueOf(NnStatusCode.SUCCESS), "SUCCESS"); 
		}
		String name = podcast.getTitle();
		if (channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL || 
			channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_PLAYLIST) {			
			channel.setOriName(name);
		} else {
			channel.setName(name);
		}		
		String intro = podcast.getDescription();
		if (intro!= null && intro.length() > 500) {
			intro = intro.substring(0, 499);
		}
		if (intro != null) { intro = intro.replaceAll("\\s", " ");}
		if (podcast.getContentType() != null) {
			channel.setContentType(Short.parseShort(podcast.getContentType()));
		}		
		channel.setIntro(intro);
		channel.setImageUrl(podcast.getImage());
		if (channel.getStatus() == NnChannel.STATUS_PROCESSING) {			
			channel.setStatus(NnChannel.STATUS_WAIT_FOR_APPROVAL);
		}
		if (channel.getStatus() != NnChannel.STATUS_PROCESSING && channel.getStatus() != NnChannel.STATUS_WAIT_FOR_APPROVAL) {
			channel.setStatus(this.convertStatus(Short.valueOf(podcast.getErrorCode())));
		}
		
		if (podcast.getLastUpdateTime() != null)
			channel.setTranscodingUpdateDate(podcast.getLastUpdateTime());
		channel.setPublic(true);
		channel.setErrorReason("");
		
		channelMngr.save(channel);
		return new PostResponse(String.valueOf(NnStatusCode.SUCCESS), "SUCCESS");
	}

	public String[] getPodcastInfo(String urlStr) {
        URL url;
        String podcastInfo[] = new String[3];
        boolean retry = true; //the "good" podcastUrl oftentimes comes after a redirect, giving it twice chance. 
        int counter = 1;
        while (retry) {
			try {
				//HTTP GET
				url = new URL(urlStr);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoOutput(true);
		        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
		        	log.info("podcast GET response not ok!" + connection.getResponseCode());	        	
		        }
		        String returnUrl = connection.getURL().toString();
		        if (returnUrl.equals(urlStr)) {
		        	retry = false;
		        } else {
		        	counter++;
		        	retry = counter > 2 ? false : true;
		        }
		        podcastInfo[0] = Integer.toString(connection.getResponseCode());
		        podcastInfo[1] = connection.getHeaderField("content-type");
		        podcastInfo[2] = connection.getURL().toString();
			} catch (Exception e) {
				retry = false;
				podcastInfo[0] = "400";
			}
        }
		return podcastInfo;
	}
					
	public PostResponse updateProgram(RtnProgram rtnProgram) {
		NnProgramManager programMngr = new NnProgramManager();
		NnChannelManager channelMngr = new NnChannelManager();			
		RtnProgramItem item = rtnProgram.getItems()[0]; //for now there's only one item
		log.info("updateProgramViaTranscodingService(): " + item.toString());
		if (!Pattern.matches("^\\d*$", rtnProgram.getKey())) {
			return new PostResponse(String.valueOf(NnStatusCode.CHANNEL_INVALID), "channel invalid");
		}
		NnChannel channel = channelMngr.findById(Long.parseLong(rtnProgram.getKey()));		
		if (channel == null) {
			return new PostResponse(String.valueOf(NnStatusCode.CHANNEL_INVALID), "channel invalid");
		}
				
		if (channel.isPublic() != true && channel.getStatus() == NnChannel.STATUS_SUCCESS) {
			channel.setPublic(true);
			channelMngr.save(channel);
		}
		
		NnProgram program = programMngr.findByStorageId(item.getItemId());
		boolean isNew = false;
		if (program == null) {
			isNew = true;
			program = new NnProgram(channel.getId(), "", "", "");
		}
		if (isNew) {
			if (item.getTitle() != null) { program.setName(item.getTitle()); }					

			String intro = item.getDescription();
			if (intro != null && intro.length() > 500) {
				intro = item.getDescription().substring(0, 499);
			}
			if (intro != null) {intro.replaceAll("\\s", " ");}
			program.setIntro(intro);
			
			if (item.getThumbnail()!= null) {
				program.setImageUrl(item.getThumbnail());
			} else {
				program.setImageUrl(channel.getImageUrl());
			}		
			
			if (item.getThumbnail()!= null) {
				program.setImageLargeUrl(item.getThumbnailLarge());
			} else {
				program.setImageUrl(channel.getImageUrl());
			}		
		}
		program.setStorageId(item.getItemId());			
		if (item.getOther() != null)
			program.setFileUrl(item.getOther());
		if (item.getDuration() != null)
			program.setDuration(item.getDuration());
		program.setPublic(true);
		if (item.getSortId() != null)
			program.setSeq(item.getSortId());
		if (item.getSubSortId() != null)
			program.setSubSeq(item.getSubSortId());
		
		if (isNew) {
			programMngr.create(channel, program);
		} else {
			programMngr.save(program);
		}
		return new PostResponse(String.valueOf(NnStatusCode.SUCCESS), "SUCCESS");
	}

	public void submitToTranscodingService(long channelId, String sourceUrl, HttpServletRequest req) {		
		PostUrl postUrl = new PostUrl();
		postUrl.setKey(String.valueOf(channelId));
		postUrl.setRss(sourceUrl);	
		String[] transcodingEnv = this.getTranscodingEnv(req);
		String transcodingServer = transcodingEnv[0] + "podcatcher.php";
		String callbackUrl = transcodingEnv[1];
		String devel = transcodingEnv[2];		
		postUrl.setCallback(callbackUrl);
		if (!devel.equals("1")) {
			NnNetUtil.urlPostWithJson(transcodingServer, postUrl);			
		}
	}
	
	public Properties getTranscodingServerPro() {
		Properties pro = new Properties();
		try {
			pro.load(NnChannelManager.class.getClassLoader().getResourceAsStream("transcoding.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pro;
	}
	
	public String[] getTranscodingEnv(HttpServletRequest req) {
		//get environment
		Properties pro = getTranscodingServerPro();
		String url = NnNetUtil.getUrlRoot(req);
		String env = "office";
		String callback_env = "_callback";
		if (url.contains("alpha.9x9.tv")) {
			env = "alpha";
		} else if (url.contains("dev") && url.contains("9x9.tv")){
			env = "dev";
		} else if (url.contains("9x9.tv")) {				   
			env = "prod";
		}
		log.info("submit to server " + env);
		callback_env = env + callback_env;
		
		//set transcoding server and callback urls
		String transcodingServer = pro.getProperty(env);		
		String callbackServer = url;
		if (pro.getProperty(callback_env).length() > 0) {
			callbackServer = pro.getProperty(callback_env);
		}
		String devel = pro.getProperty("devel");
		return new String[]{transcodingServer, callbackServer, devel};
	}
	
}
