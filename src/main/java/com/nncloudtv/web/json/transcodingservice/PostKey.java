package com.nncloudtv.web.json.transcodingservice;

/**
 * Pass Channel key and program key to Transcoding Service
 */
public class PostKey {

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
