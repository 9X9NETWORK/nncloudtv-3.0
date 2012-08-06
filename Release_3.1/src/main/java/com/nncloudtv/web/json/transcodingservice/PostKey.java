package com.nncloudtv.web.json.transcodingservice;

import java.io.Serializable;

/**
 * Pass Channel key and program key to Transcoding Service
 */
public class PostKey implements Serializable {

	private static final long serialVersionUID = -1989278891340544589L;
	
	private String key;
	private String itemKey;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getItemKey() {
		return itemKey;
	}
	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}		
}
