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
import com.nncloudtv.model.NnSet;
import com.nncloudtv.service.MsoConfigManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnSetManager;

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
	
	public static String createPiwikSite(long setId, long channelId) {
		if (isNoPiwik()) {
			log.info("no piwik");
			return null;
		}
		if (setId == 0 && channelId == 0)
			return null;
		NnSetManager setMngr = new NnSetManager();
		NnChannelManager channelMngr = new NnChannelManager();
		NnSet set = null;
		NnChannel c = null;
		if (setId != 0) {
			set = setMngr.findById(setId);
			if (set == null) {
				log.info("querying for empty channel set");
				return null;
			}
			if (set.getPiwik() != null && set.getPiwik().length() > 0) {
				log.info("not creating anything new:" + set.getId());
				return set.getPiwik();
			}
		}
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
		} else if (setId != 0) {
			contentUrl += "set=" + setId;
			siteName = "set" + String.valueOf(setId);
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
		} else if (set != null) {		
			set.setPiwik(id);
			setMngr.save(set);
		}
		
		return idsite;
		////http://piwik.dev.9x9.tv/index.php?jsoncallback=jsonp1316424512664&method=SitesManager.getSitesIdFromSiteUrl&url=http%3A%2F%2Fcms.9x9.tv%2F9x9&module=API&format=JSON&token_auth=23ed70e585b18033d7150f917232d1f4		
	}
	
	//urlRoot example, "http://qa.9x9.tv"
	// DEPRECATED !!!
	public static String createPiwikSite(long setId, long channelId, String urlRoot) {
		if (isNoPiwik()) {
			log.info("no piwik");
			return null;
		}
		log.info("setID:" + setId + ";urlRoot:" + urlRoot);
		if (setId == 0 && channelId == 0)
			return null;
		NnSetManager setMngr = new NnSetManager();
		NnChannelManager channelMngr = new NnChannelManager();
		NnSet set = null;
		NnChannel c = null;
		if (setId != 0) {
			set = setMngr.findById(setId);
			if (set == null) {
				log.info("querying for empty channel set");
				return null;
			}
			if (set.getPiwik() != null && set.getPiwik().length() > 0) {
				log.info("not creating anything new:" + set.getId());
				return set.getPiwik();
			}
		}
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
			
		String site = "";
		if (urlRoot.contains("demo")) {
			site = ".demo";
		} else if (urlRoot.contains("alpha") ||  
				   urlRoot.contains("localhost") || 
				   urlRoot.contains("puppy") || 
				   urlRoot.contains("office") ||
				   urlRoot.contains("beta")){
			site = ".dev";
//		} else if (!urlRoot.contains("9x9.tv")) {
//			site = "_garbage_";
		}
		String postHost = "http://piwik" + site + ".9x9.tv";
		if (urlRoot.contains("cms")) {
			postHost = "http://piwik.teltel.com";
		}
		String contentUrl = urlRoot + "?";
		String siteName = "";
		if (channelId != 0) {
			contentUrl += "ch=" + channelId;
			siteName = "ch" + String.valueOf(channelId);
		} else if (setId != 0) {
			contentUrl += "set=" + setId;
			siteName = "set" + String.valueOf(setId);
		}
				
		String urlStr = postHost + "/index.php?";
		urlStr += "jsoncallback=jsonp1316430986921";
		urlStr += "&method=SitesManager.addSite";
		urlStr += "&module=API";
		urlStr += "&format=JSON";
		urlStr += "&token_auth=23ed70e585b18033d7150f917232d1f4"; 
		urlStr += "&urls=" + contentUrl;
		urlStr += "&siteName=" + siteName;
		
		//urlStr = "http://piwik.teltel.com/index.php?jsoncallback=jsonp1316424512664&method=SitesManager.getSitesIdFromSiteUrl&url=http%3A%2F%2Fcms.9x9.tv%2F9x9&module=API&format=JSON&token_auth=23ed70e585b18033d7150f917232d1f4";
		log.info("url:" + urlStr);			
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
		} else if (set != null) {		
			set.setPiwik(id);
			setMngr.save(set);
		}
		
		return idsite;
		////http://piwik.dev.9x9.tv/index.php?jsoncallback=jsonp1316424512664&method=SitesManager.getSitesIdFromSiteUrl&url=http%3A%2F%2Fcms.9x9.tv%2F9x9&module=API&format=JSON&token_auth=23ed70e585b18033d7150f917232d1f4		
	}	
	
}
