package com.nncloudtv.lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

public class NnUserShardingLib {
	protected static final Logger log = Logger.getLogger(NnUserShardingLib.class.getName());	

	public static int findShardingByReq(HttpServletRequest req) {
		String ip = req.getRemoteAddr();
		log.info("findLocaleByHttpRequest() ip is " + ip);
	    String country = "";
		try {
			URL url = new URL("http://brussels.teltel.com/geoip/?ip=" + ip);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);
	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	        	log.info("findLocaleByHttpRequest() IP service returns error:" + connection.getResponseCode());	        	
	        }
	        BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));;
	        if (rd.readLine()!=null) {country = rd.readLine().toLowerCase();} //assuming one line	        
		} catch (Exception e) {
			NnLogUtil.logException(e);
		}
		if (country.equals("tw") || country.equals("cn") || country.equals("hk")) {
			return 1;
		} else if (country.equals("")) {
			return 2;
		}
		return 2;
	}
	
	public static int findShardingByUserToken(String token) {
		return 1;
	}

}
