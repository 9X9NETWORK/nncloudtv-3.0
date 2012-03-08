package com.nncloudtv.web.json.facebook;

public class FacebookPage {
	private String name;
	private String access_token;
	private String category;
	private String id;
	public String getName() {
	    return name;
    }
	public void setName(String name) {
	    this.name = name;
    }
	public String getAccess_token() {
	    return access_token;
    }
	public void setAccess_token(String access_token) {
	    this.access_token = access_token;
    }
	public String getCategory() {
	    return category;
    }
	public void setCategory(String category) {
	    this.category = category;
    }
	public String getId() {
	    return id;
    }
	public void setId(String id) {
	    this.id = id;
    }
}
