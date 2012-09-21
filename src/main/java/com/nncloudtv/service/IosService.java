package com.nncloudtv.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.web.api.NnStatusCode;

@Service
public class IosService {
	
    protected static final Logger log = Logger.getLogger(IosService.class.getName());
    private static final String urlRoot = "http://s3.amazonaws.com/9x9ui/war/v2/ios/";
    
    public String category(String id, String lang, boolean flatten) {    	
    	String filename = "category_en";
        if (lang != null && lang.equals(LangTable.LANG_ZH)) {
        	filename = "category_zh";
        }
        if (id != null) {
        	filename = "category_" + id;
        	if (id.contains("s"))
        		filename = id;
        }
    	String url = urlRoot + filename;
    	String result = NnNetUtil.urlGet(url);
    	if (result == null)
    		return new PlayerApiService().assembleMsgs(NnStatusCode.INPUT_BAD, null);
    	return result;
    }
    
	public String setInfo(String id, String beautifulUrl) {
		String filename = id;
    	String url = urlRoot + "s" + filename;
    	String result = NnNetUtil.urlGet(url);
    	if (result == null)
    		return new PlayerApiService().assembleMsgs(NnStatusCode.INPUT_BAD, null);		
    	return result;
	}
	
	public String composeChannelLineup(List<NnChannel> channels) {
		String result = "";
		//TODO remove the "more" image url
		for (NnChannel c : channels) {
    		String name = c.getName();
    		if (name != null)
    			name = name.replaceAll("\n", " ").replaceAll("\t", " ");		
    		String intro = c.getIntro();		
    		if (intro != null)
    			intro = intro.replaceAll("\n", " ").replaceAll("\t", " ");
    		String imageUrl = c.getPlayerPrefImageUrl();	
    		String youtubeId = "";
    		if (c.getSourceUrl() != null && c.getSourceUrl().contains("http://www.youtube.com"))
    			youtubeId = YouTubeLib.getYouTubeChannelName(c.getSourceUrl());
    		if (c.getContentType() == NnChannel.CONTENTTYPE_FACEBOOK) 
    			youtubeId = c.getSourceUrl();
    		String[] ori = {Integer.toString(c.getSeq()),				         
    					    String.valueOf(c.getId()),
    					    name,
    					    intro,
    					    imageUrl,
    					    String.valueOf(c.getCntEpisode()),
    					    String.valueOf(c.getType()),
    					    String.valueOf(c.getStatus()),
    					    String.valueOf(c.getContentType()),
    					    youtubeId,
    					    NnChannelManager.convertEpochToTime(c.getTranscodingUpdateDate(), c.getUpdateDate()),
    					    String.valueOf(c.getSorting()),
    					    c.getPiwik(),
    					    String.valueOf(c.getRecentlyWatchedProgram()),
    					    c.getOriName(),
    					    String.valueOf(c.getCntSubscribe()),
    					    };
    		result += NnStringUtil.getDelimitedStr(ori) + "\n";
		}
		return result;
	}
	
}
