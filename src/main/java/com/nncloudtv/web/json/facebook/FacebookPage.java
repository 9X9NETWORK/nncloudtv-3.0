package com.nncloudtv.web.json.facebook;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FacebookPage implements Serializable {
    private static final long serialVersionUID = 5360642433865767444L;
    
    private String name;
    private String access_token;
    private String category;
    private List<Map<String, String>> category_list;
    private String id;
    private List<String> perms;
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
    public List<String> getPerms() {
        return perms;
    }
    public void setPerms(List<String> perms) {
        this.perms = perms;
    }
    public List<Map<String, String>> getCategory_list() {
        return category_list;
    }
    public void setCategory_list(List<Map<String, String>> category_list) {
        this.category_list = category_list;
    }
}
