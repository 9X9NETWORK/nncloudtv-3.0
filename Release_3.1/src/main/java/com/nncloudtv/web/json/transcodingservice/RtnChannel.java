package com.nncloudtv.web.json.transcodingservice;

import java.io.Serializable;

/**
 * Receive Channel information from Transcoding Service 
 */
public class RtnChannel implements Serializable{

	private static final long serialVersionUID = -1003729993905988235L;
	
	private String action;
	private String key; //channel key
	private String title;
	private String description;
	private String contentType;
	private String pubDate;
	private String lastUpdateTime;
	private String image;
	private String errorCode;
	private String errorReason;
	
	public static String ACTION_UPDATE_CHANNEL = "updateChannel";
		
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorReason() {
		return errorReason;
	}
	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}	
    public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	} 	
	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("key: " + key + ";");
        buffer.append("title: " + title + ";");
        buffer.append("pubDate: " + pubDate + ";");
        buffer.append("errorCode: " + errorCode + ";");
        buffer.append("errorReason: " + errorReason + ";");
        return buffer.toString();
    }
}
