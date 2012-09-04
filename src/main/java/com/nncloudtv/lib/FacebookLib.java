package com.nncloudtv.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.ProtocolException;
import org.codehaus.jackson.map.ObjectMapper;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.google.api.client.json.JsonToken;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.service.MsoConfigManager;
import com.nncloudtv.web.json.facebook.FBPost;
import com.nncloudtv.web.json.facebook.FacebookError;
import com.nncloudtv.web.json.facebook.FacebookPage;
import com.nncloudtv.web.json.facebook.FacebookResponse;

public class FacebookLib {
	protected static final Logger log = Logger.getLogger(FacebookLib.class.getName());

	//!!! rewrite
	public String[] getFanpageInfo(String urlStr) {
		String query = urlStr.replace("www.facebook.com", "graph.facebook.com");
		query = query.replace("https", "http");
        URL url;
        String fbInfo[] = new String[2];
    	String username = "";
    	String picture = "";
    	if (urlStr.contains("pages")) {
	    	int start = urlStr.indexOf("pages/", 1);
	    	query = urlStr.substring(start+6);
	    	start = query.indexOf("/", 1);
	    	if (start > 1)
	    	   query = query.substring(start+1);
	    	   query = "http://graph.facebook.com/" + query;
	    }
        try {
			//HTTP GET
			url = new URL(query);						
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
	        
	        //connection.setDoOutput(true);
	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	        	log.info("podcast GET response not ok!" + connection.getResponseCode());	        	
	        }
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
            	String[] line = inputLine.split(",");
            	for (String l : line) {
            		if (l.contains("name") || l.contains("picture")) {
            			l = l.replaceAll("\"", "");
            			String[] entity = l.split(":");            			
            			if (l.contains("name")) 
            				username = entity[1];
            			if (l.contains("picture")) 
            				picture = entity[1] + entity[2];
            			    picture = picture.replace("http\\/\\/", "http://");
            			    picture = picture.replace("\\/", "/");
            		}            				
            	}
            }
            reader.close();            
	        fbInfo[0] = username;
	        fbInfo[1] = picture; 
		} catch (Exception e) {
			fbInfo[0] = "";
		}
		return fbInfo;
	}
	
	public static void postToFacebook(FBPost fbPost) throws IOException {
		
		MsoConfigManager configMngr = new MsoConfigManager();
		MsoConfig fbConfig = configMngr.findByItem(MsoConfig.REALFBTOKEN);
		String accessToken = null;
		if (fbConfig != null) {
			accessToken = fbConfig.getValue();
		}
		if (fbPost.getAccessToken() != null) {
			accessToken = fbPost.getAccessToken();
		}
		
		URL url = new URL("https://graph.facebook.com/" + fbPost.getFacebookId() + "/feed");
		String post =
			"access_token=" + URLEncoder.encode(accessToken, "US-ASCII") +
			"&picture=" + URLEncoder.encode(fbPost.getPicture(), "US-ASCII") +
			"&name=" + URLEncoder.encode(fbPost.getName(), "UTF-8") +
			"&link=" + URLEncoder.encode(fbPost.getLink(), "US-ASCII")+
			"&caption=" + URLEncoder.encode(fbPost.getCaption(), "UTF-8") +
			"&description=" + URLEncoder.encode(fbPost.getDescription(), "UTF-8");
		if (fbPost.getMessage() != null && !fbPost.getMessage().isEmpty()) {
			post += "&message=" + URLEncoder.encode(fbPost.getMessage(), "UTF-8");
		}
		log.info(post);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(post);
		writer.close();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = reader.readLine();
		reader.close();
		log.info(line);
	}
	
	public static void postToTwitter(FBPost fbPost) throws IOException, TwitterException {
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer("udWzz6YrsaNlbJ18vZ7aCA", "Pf0TdB2QFXWKyphbIdnPG4vZhLVze0cPCxlLkfBwtQ");
		AccessToken accessToken = new AccessToken(fbPost.getFacebookId(), fbPost.getAccessToken());
		twitter.setOAuthAccessToken(accessToken);
		twitter.updateStatus(fbPost.getName()+" "+fbPost.getLink());
	}
	
	
	static public List<FacebookPage> populatePageList(String fbUserId, String accessToken) {
		
		log.info("fbUserId = " + fbUserId + ", accessToken = " + accessToken);
		
		if (fbUserId == null || accessToken == null) {
			return null;
		}
		
		List<FacebookPage> pages = null;
		
		try {
			String fullpath = 
					"https://graph.facebook.com/" + fbUserId + 
					"/accounts?access_token=" + URLEncoder.encode(accessToken, "US-ASCII");
			log.info(fullpath);
			URL url = new URL(fullpath);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			ObjectMapper mapper = new ObjectMapper();
			FacebookResponse response = mapper.readValue(connection.getInputStream(), FacebookResponse.class);
			if (response.getData() != null) {
				pages = response.getData();
				log.info("pages count: " + pages.size());
			} else if (response.getError() != null) {
				FacebookError error = response.getError();
				log.warning("error message: " + error.getMessage());
				log.warning("error type:" + error.getType());
			} else {
				log.warning("neither no data nor error");
			}
		} catch (MalformedURLException e) {
			logException(e);
		} catch (UnsupportedEncodingException e) {
			logException(e);
		} catch (IOException e) {
			logException(e);
		}
		
		return pages;
	}
	
	static private void logException(Exception e) {
		log.warning(e.getClass().getCanonicalName());
		NnLogUtil.logException(e);
	}
}
