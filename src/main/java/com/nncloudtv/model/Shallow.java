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
@PersistenceCapable(table="shallow", detachable="true")
public class Shallow implements Serializable{    

    private static final long serialVersionUID = -6605593796839713340L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long channelId;

    //channel ids, separated by comma    
    @Persistent
    @Column(jdbcType="VARCHAR", length=1024)
    private String recommendIds;

    @Persistent
    @Column(jdbcType="VARCHAR", length=10)
    private String lang;
    
    @Persistent
    private Date updateDate;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
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
}
