package com.nncloudtv.web.json.cms;

import java.io.Serializable;

public class Category implements Serializable {
    
    private static final long serialVersionUID = 6689668608352509726L;
    
    private long id;
    
    private String name;
    
    private int cntChannel;
    
    private String lang;
    
    private short seq;
    
    private long msoId;
    
    private String enName;
    
    private String zhName;

    public String getName() {
    
        return name;
    }

    public void setName(String name) {
    
        this.name = name;
    }

    public int getCntChannel() {
    
        return cntChannel;
    }

    public void setCntChannel(int cntChannel) {
    
        this.cntChannel = cntChannel;
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

    public short getSeq() {
    
        return seq;
    }

    public void setSeq(short seq) {
    
        this.seq = seq;
    }

    public long getMsoId() {
    
        return msoId;
    }

    public void setMsoId(long msoId) {
    
        this.msoId = msoId;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

}
