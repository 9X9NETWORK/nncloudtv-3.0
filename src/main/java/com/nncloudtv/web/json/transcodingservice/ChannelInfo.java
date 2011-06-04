package com.nncloudtv.web.json.transcodingservice;

import java.util.List;

import com.nncloudtv.model.NnChannel;
import com.nncloudtv.service.NnStatusCode;

public class ChannelInfo {

	private String errorCode;
	private String errorReason;
	private String callBack;
	private String paging;
	private String totalPage;
	private List<NnChannel> channels;

	public ChannelInfo() {
		this.setErrorCode(String.valueOf(NnStatusCode.ERROR));
		this.setErrorReason("Error");		
		this.paging = "1";
		this.totalPage = "1";
	}

	public String getPaging() {
		return paging;
	}

	public void setPaging(String paging) {
		this.paging = paging;
	}

	public String getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(String totalPage) {
		this.totalPage = totalPage;
	}

	public String getCallBack() {
		return callBack;
	}

	public void setCallBack(String callBack) {
		this.callBack = callBack;
	}

	public List<NnChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<NnChannel> channels) {
		this.channels = channels;
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
