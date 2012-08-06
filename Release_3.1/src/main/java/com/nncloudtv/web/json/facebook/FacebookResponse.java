package com.nncloudtv.web.json.facebook;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FacebookResponse implements Serializable {
	private static final long serialVersionUID = -930385136285315982L;
	
	private List<FacebookPage> data;
	private FacebookError error;
	private Map<String,String> paging;
	public Map<String, String> getPaging() {
		return paging;
	}
	public void setPaging(Map<String, String> paging) {
		this.paging = paging;
	}
	public List<FacebookPage> getData() {
		return data;
	}
	public void setData(List<FacebookPage> data) {
		this.data = data;
	}
	public FacebookError getError() {
		return error;
	}
	public void setError(FacebookError error) {
		this.error = error;
	}
}
