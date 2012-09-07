package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Used for shallow recommendation.
 * Data is from recommendation engine
 */
@PersistenceCapable(table="deep", detachable="true")
public class Deep implements Serializable{    

    private static final long serialVersionUID = 96881541165589061L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Persistent
    private long userId;

    @Persistent
    private short shard;

    //channel ids, separated by comma
    @Column(jdbcType="VARCHAR", length=1024)
    private String recommendIds;

    @Persistent
    @Column(jdbcType="VARCHAR", length=10)
    private String lang;
    
    @Persistent
    private Date updateDate;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public short getShard() {
        return shard;
    }

    public void setShard(short shard) {
        this.shard = shard;
    }

    public String getRecommendIds() {
        return recommendIds;
    }

    public void setRecommendIds(String recommendIds) {
        this.recommendIds = recommendIds;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
