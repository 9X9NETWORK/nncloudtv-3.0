package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="systag_display", detachable="true")
public class SysTagDisplay implements Serializable {

    private static final long serialVersionUID = 180961281773849098L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent
    private long systagId;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String name;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=5)
    private String lang; //used with LangTable

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String imageUrl;

    @Persistent
    @Column(jdbcType="VARCHAR", length=500)
    private String popularTag; //sequence shown in the directory

    @Persistent
    private int cntChannel;
    
    @Persistent
    private Date updateDate;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSystagId() {
        return systagId;
    }

    public void setSystagId(long systagId) {
        this.systagId = systagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getPopularTag() {
        return popularTag;
    }

    public void setPopularTag(String popularTag) {
        this.popularTag = popularTag;
    }

    public int getCntChannel() {
        return cntChannel;
    }

    public void setCntChannel(int cntChannel) {
        this.cntChannel = cntChannel;
    } 
    
}
