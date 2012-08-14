package com.nncloudtv.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.logging.Logger;

import com.nncloudtv.model.NnChannel;
import com.nncloudtv.service.MsoConfigManager;
import com.nncloudtv.service.NnChannelManager;

public class PiwikLib {

	protected static final Logger log = Logger.getLogger(PiwikLib.class.getName());
	
	private static Boolean isNoPiwik() {
		Properties properties = new Properties();
		Boolean result = true;
		try {
			properties.load(NnChannelManager.class.getClassLoader().getResourceAsStream("piwik.properties"));
			String noPiwik = properties.getProperty("no_piwik");
			if (noPiwik.equalsIgnoreCase("0")) {
				result = false;
			}
		} catch (IOException e) {
			NnLogUtil.logException(e);
		}		
		return result;
	}
	
	//generate api like http://piwik.dev.9x9.tv/index.php?jsoncallback=jsonp1316424512664&method=SitesManager.getSitesIdFromSiteUrl&url=http%3A%2F%2Fcms.9x9.tv%2F9x9&module=API&format=JSON&token_auth=23ed70e585b18033d7150f917232d1f4 
	public static String createPiwikSite(long channelId) {
		if (isNoPiwik()) {
			log.info("no piwik");
			return null;
		}
		if (channelId == 0)
			return null;
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel c = null;
		if (channelId != 0) {
			c = channelMngr.findById(channelId);
			if (c == null) {
				log.info("querying for empty channel");
				return null;
			}
			if (c.getPiwik() != null && c.getPiwik().length() > 0) {
				log.info("not creating anything new:" + c.getId());
				return c.getPiwik();
			}
		}
		
		String urlRoot = "http://" + MsoConfigManager.getServerDomain() + "/";
		String piwikHost = "http://" + MsoConfigManager.getPiwikDomain();
		String contentUrl = urlRoot + "?";
		String siteName = "";
		if (channelId != 0) {
			contentUrl += "ch=" + channelId;
			siteName = "ch" + String.valueOf(channelId);
		}
		
		String urlStr = piwikHost + "/index.php?";
		try {
			urlStr += "jsoncallback=jsonp1316430986921";
			urlStr += "&method=SitesManager.addSite";
			urlStr += "&module=API";
			urlStr += "&format=JSON";
			urlStr += "&token_auth=23ed70e585b18033d7150f917232d1f4"; 
			urlStr += "&urls=" + URLEncoder.encode(contentUrl, "UTF-8");
			urlStr += "&siteName=" + URLEncoder.encode(siteName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
		//urlStr = "http://piwik.teltel.com/index.php?jsoncallback=jsonp1316424512664&method=SitesManager.getSitesIdFromSiteUrl&url=http%3A%2F%2Fcms.9x9.tv%2F9x9&module=API&format=JSON&token_auth=23ed70e585b18033d7150f917232d1f4";
		//HTTP GET
		URL url;
        String idsite = "";
		try {
			url = new URL(urlStr);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);	      
	        BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));;
	        String line = null;
	        //line = "jsonp1316430986921({\"value\":18})";
	        while ((line = rd.readLine()) != null) {
	        	if (line.contains("value")) {
	        		log.info("line:" + line);	       
	        		idsite = line.substring(line.indexOf("value\":")+7, line.indexOf("})"));
	        	}
	        }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String id = idsite;
		if (c != null) {
			c.setPiwik(id);
			channelMngr.save(c);
		}
		
		return idsite;
	}
		
}
