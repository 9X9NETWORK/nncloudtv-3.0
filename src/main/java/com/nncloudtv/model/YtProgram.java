package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Used by android device, essentially simplified version of nnprogram. Will see how it goes, maybe will be merged to nnprogram somehow.
 * They are data crawled from YouTube.
 */
@PersistenceCapable(table="ytprogram", detachable="true")
public class YtProgram implements Serializable {
    
    private static final long serialVersionUID = 3029235937585901713L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
        
    @Persistent
    private long channelId;
    
    @Persistent 
    @Column(jdbcType="VARCHAR", length=255)
    private String ytUserName;

    @Persistent 
    @Column(jdbcType="VARCHAR", length=255)
    private String ytVideoId;

    @Persistent 
    @Column(jdbcType="VARCHAR", length=255)
    private String name;

    @Persistent 
    @Column(jdbcType="VARCHAR", length=255)
    private String duration;
    
    @Persistent 
    @Column(jdbcType="VARCHAR", length=255)
    private String imageUrl;

    @Persistent 
    @Column(jdbcType="VARCHAR", length=255)
    private String intro;
    
    @Persistent
    private Date crawlDate;

    @Persistent
    private Date updateDate;
    
    public YtProgram() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getYtUserName() {
        return ytUserName;
    }

    public void setYtUserName(String ytUserName) {
        this.ytUserName = ytUserName;
    }

    public String getYtVideoId() {
        return ytVideoId;
    }

    public void setYtVideoId(String ytVideoId) {
        this.ytVideoId = ytVideoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Date getCrawlDate() {
        return crawlDate;
    }

    public void setCrawlDate(Date crawlDate) {
        this.crawlDate = crawlDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
        
}
