package com.nncloudtv.web.json.transcodingservice;

import java.io.Serializable;

public class Program implements Serializable {
	private static final long serialVersionUID = 4283777238540646610L;
	
	private String audio;
	private String mp4;
	private String other;
	private String webm;

	public Program(String audio, String mp4, String other, String webm) {
		this.audio = audio;
		this.mp4 = mp4;
		this.other = other;
		this.webm = webm;
	}
	
	public String getWebm() {
		return webm;
	}
	public void setWebm(String webm) {
		this.webm = webm;
	}
	public String getMp4() {
		return mp4;
	}
	public void setMp4(String mp4) {
		this.mp4 = mp4;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public String getAudio() {
		return audio;
	}
	public void setAudio(String audio) {
		this.audio = audio;
	}		
}
