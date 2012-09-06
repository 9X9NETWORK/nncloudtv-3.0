package com.nncloudtv.web.api;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnChannelPref;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnChannelPrefManager;

@Controller
@RequestMapping("api")
public class ApiContent extends ApiGeneric {
	
	protected static Logger log = Logger.getLogger(ApiContent.class.getName());
	
	@RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.DELETE)
	public @ResponseBody
	String facebookAutosharingDelete(HttpServletRequest req,
	        HttpServletResponse resp,
	        @PathVariable("channelId") String channelIdStr) {
		
		Long channelId = null;
		try {
			channelId = Long.valueOf(channelIdStr);
		} catch (NumberFormatException e) {
		}
		if (channelId == null) {
			notFound(resp, INVALID_PATH_PARAMETER);
			return null;
		}
		
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel channel = channelMngr.findById(channelId);
		if (channel == null) {
			notFound(resp, "Channel Not Found");
			return null;
		}
		
		NnChannelPref pref = null;
		NnChannelPrefManager prefMngr = new NnChannelPrefManager();
		
		// fbUserId
		pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_USER_ID);
		if (pref != null) {
			prefMngr.delete(pref);
		}
		
		// accessToken
		pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_TOKEN);
		if (pref != null) {
			prefMngr.delete(pref);
		}
		
		return "OK";
	}
	
	@RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.POST)
	public @ResponseBody
	String facebookAutosharingCreate(HttpServletRequest req,
	        HttpServletResponse resp,
	        @PathVariable("channelId") String channelIdStr) {
		
		Long channelId = null;
		try {
			channelId = Long.valueOf(channelIdStr);
		} catch (NumberFormatException e) {
		}
		if (channelId == null) {
			notFound(resp, INVALID_PATH_PARAMETER);
			return null;
		}
		
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel channel = channelMngr.findById(channelId);
		if (channel == null) {
			notFound(resp, "Channel Not Found");
			return null;
		}
		
		String fbUserId = req.getParameter("userId");
		String accessToken = req.getParameter("accessToken");
		if (fbUserId == null || accessToken == null) {
			
			badRequest(resp, MISSING_PARAMETER);
			return null;
		}
		
		NnChannelPref pref = null;
		NnChannelPrefManager prefMngr = new NnChannelPrefManager();
		
		// fbUserId
		pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_USER_ID);
		if (pref != null) {
			pref.setValue(fbUserId);
		} else {
			pref = new NnChannelPref(channel, NnUserPref.FB_USER_ID, fbUserId);
		}
		prefMngr.save(pref);
		
		// accessToken
		pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_TOKEN);
		if (pref != null) {
			pref.setValue(accessToken);
		} else {
			pref = new NnChannelPref(channel, NnUserPref.FB_TOKEN, accessToken);
		}
		prefMngr.save(pref);
		
		return "OK";
	}
	
	@RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.GET)
	public @ResponseBody
	Map<String, Object> facebookAutosharing(HttpServletRequest req, HttpServletResponse resp,
	        @PathVariable("channelId") String channelIdStr) {
		
		Long channelId = null;
		try {
			channelId = Long.valueOf(channelIdStr);
		} catch (NumberFormatException e) {
		}
		if (channelId == null) {
			notFound(resp, INVALID_PATH_PARAMETER);
			return null;
		}
		
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel channel = channelMngr.findById(channelId);
		if (channel == null) {
			notFound(resp, "Channel Not Found");
			return null;
		}
		
		NnChannelPrefManager prefMngr = new NnChannelPrefManager();
		NnChannelPref pref = null;
		Map<String, Object> result = new TreeMap<String, Object>();
		String fbUserId = null;
		String accessToken = null;
		
		// fbUserId
		pref = prefMngr.findByChannelIdAndItem(channelId,NnUserPref.FB_USER_ID);
		if (pref != null) {
			fbUserId = pref.getValue();
			result.put("userId", fbUserId);
		}
		
		// accessToken
		pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_TOKEN);
		if (pref != null) {
			accessToken = pref.getValue();
			result.put("accessToken", accessToken);
		}
		
		if (accessToken != null && fbUserId != null) {
			return result;
		}
		
		return null;
	}
	
}
