package com.nncloudtv.web.json.transcodingservice;

import java.io.Serializable;

/**
 * Pass RSS feed to Transcoding Service
 */
public class PostUrl implements Serializable {

	private static final long serialVersionUID = 8382838010042182091L;
	
	private String key; //channel key
	private String rss; //rss feed
	private String callback; //callback host name
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public String getRss() {
		return rss;
	}
	public void setRss(String rss) {
		this.rss = rss;
	}

	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}
	
}
