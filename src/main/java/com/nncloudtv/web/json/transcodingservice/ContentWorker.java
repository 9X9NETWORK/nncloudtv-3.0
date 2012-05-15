package com.nncloudtv.web.json.transcodingservice;

import java.io.IOException;
import java.io.Serializable;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class ContentWorker implements Serializable {

	private static final long serialVersionUID = -8187551206678593605L;
	
	private long id;
	private String imageUrl;
	private String videoUrl;
	private String prefix; 
	private boolean flag; //auto_generated_logo
	private String callback;
	private String errorCode;
	private String errorReason;	
	
	public ContentWorker() {}
	public ContentWorker(long id, String imageUrl, String videoUrl, String prefix, boolean flag) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.videoUrl = videoUrl;
		this.prefix = prefix;
		this.flag = flag;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}	
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("id: " + id + ";");
        buffer.append("videoUrl: " + videoUrl + ";");
        buffer.append("prefix: " + prefix + ";");
        buffer.append("imageUrl: " + imageUrl + ";");
        buffer.append("flag: " + flag + ";");
        buffer.append("errorCode:" + errorCode + ";");
        buffer.append("errorReason:" + errorReason + ";");
        return buffer.toString();		
	}
	
	public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
		try {
			json = mapper.writeValueAsString(this);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return json;
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
	
}
