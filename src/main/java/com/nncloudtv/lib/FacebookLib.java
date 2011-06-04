package com.nncloudtv.lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

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
	
}
