package com.nncloudtv.web.json.facebook;

import java.io.Serializable;

public class FacebookMe implements Serializable {

    private static final long serialVersionUID = -314492580933897069L;

    private String id;
    private String name;
    private String link;
    private String username;
    private String birthday;
    private String gender;
    private String email;
    private String imageUrl;
    private String locale;
    private String updated_time;
    private short status;
    public static short STATUS_SUCCESS = 0;
    public static short STATUS_ERROR = 1;
    
    public FacebookMe() {
        status = STATUS_SUCCESS;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUpdated_time() {
        return updated_time;
    }
    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public short getStatus() {
        return status;
    }
    public void setStatus(short status) {
        this.status = status;
    }
    public String toString() {
        String str = "id:" + id + "\t";
        str += "email:" + email + "\t";
        str += "name:" + name + "\t"; 
        str += "birthday:" + birthday + "\t";
        str += "locale:" + locale + "\t";
        str += "gender:" + gender + "\t";
        return str;
    }    
}
