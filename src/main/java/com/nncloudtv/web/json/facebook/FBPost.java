package com.nncloudtv.web.json.facebook;

import java.io.Serializable;

public class FBPost implements Serializable {
    private static final long serialVersionUID = -3608860240829468589L;
    
    private String message;
    private String picture;
    private String link;
    private String name;
    private String caption;
    private String description;
    private String facebookId;
    private String accessToken;
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public FBPost(String name, String description, String picture) {
        this.name = name;
        this.description = description;
        this.picture = picture;
    }
    public FBPost() {
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public String getPicture() {
        return picture;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getLink() {
        return link;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }
    public String getCaption() {
        return caption;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
    public String getFacebookId() {
        return facebookId;
    }
    @Override
    public String toString() {
        return "FBPost [message=" + message + ", picture=" + picture
                + ", link=" + link + ", name=" + name + ", caption=" + caption
                + ", description=" + description + ", facebookId=" + facebookId
                + "]";
    }
}
