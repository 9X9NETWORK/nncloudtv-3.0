package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * To relate Category and Set
 */
@PersistenceCapable(table="category_map", detachable = "true")
public class CategoryMap implements Serializable {

    private static final long serialVersionUID = 7301130494786796666L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
    
    @Persistent
    private long categoryId;
    
    @Persistent
    private long channelId;
        
    @Persistent
    private Date updateDate;

    public CategoryMap(long categoryId, long channelId) {
        this.categoryId = categoryId;
        this.channelId = channelId;
        this.updateDate = new Date();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }
}
