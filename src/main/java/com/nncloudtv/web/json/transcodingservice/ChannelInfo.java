package com.nncloudtv.web.json.transcodingservice;

import java.io.Serializable;
import java.util.List;

import com.nncloudtv.web.api.NnStatusCode;

public class ChannelInfo implements Serializable {

	private static final long serialVersionUID = -3971482679688470754L;
	
	private String errorCode;
	private String errorReason;
	private String callBack;
	private String paging;
	private String totalPage;
	private List<Channel> channels;

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

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
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

	public String toString() {
		return "channel info";
	}
}
