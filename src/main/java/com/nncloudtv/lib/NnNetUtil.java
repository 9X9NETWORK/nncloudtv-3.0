package com.nncloudtv.lib;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class NnNetUtil {
	
	protected final static Logger log = Logger.getLogger(NnNetUtil.class.getName());
		
	public static ResponseEntity<String> textReturn(String output) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf("text/plain;charset=utf-8"));
		return new ResponseEntity<String>(output, headers, HttpStatus.OK);		
	}	

	public static ResponseEntity<String> htmlReturn(String output) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf("text/html;charset=utf-8"));			                                                    
		return new ResponseEntity<String>(output, headers, HttpStatus.OK);
	}	
	
	public static String getUrlRoot(HttpServletRequest req) {
		String url = req.getRequestURL().toString();
	    Pattern p = Pattern.compile("(^http://.*?)/(.*)");	    	    
	    Matcher m = p.matcher(url);
	    String host = "";
	    if (m.find()) {
	    	host = m.group(1);
	    }
		return host;
	}

	public static void urlPostWithJson(String urlStr, Object obj) {
        URL url;
		try {
			url = new URL(urlStr);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.writeValue(writer, obj);
	        log.info("url fetch-json:" + mapper.writeValueAsString(obj));	        
	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {	        	
	        	log.info("response not ok!" + connection.getResponseCode());
	        }
	        writer.close();	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
