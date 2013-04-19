package com.nncloudtv.web.json.cms;

import java.io.Serializable;

public class Category implements Serializable {
    
    private static final long serialVersionUID = 6689668608352509726L;
    
    private long id;
    
    private String name;
    
    private int cntChannel;
    
    private String lang;
    
    private int seq;
    
    private long msoId;
    

    public String getName() {
    
        return name;
    }

    public void setName(String name) {
    
        this.name = name;
    }

    public int getCntView() {
    
        return cntChannel;
    }

    public void setCntView(int cntView) {
    
        this.cntChannel = cntView;
    }

    public long getId() {
    
        return id;
    }

    public void setId(long id) {
    
        this.id = id;
    }

    public String getLang() {
    
        return lang;
    }

    public void setLang(String lang) {
    
        this.lang = lang;
    }

    public int getSeq() {
    
        return seq;
    }

    public void setSeq(int seq) {
    
        this.seq = seq;
    }

    public long getMsoId() {
    
        return msoId;
    }

    public void setMsoId(long msoId) {
    
        this.msoId = msoId;
    }

}
