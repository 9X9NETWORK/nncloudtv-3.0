package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * 9x9 user preference, stored in key/value pair
 */
@PersistenceCapable(table="nnuser_pref", detachable="true")
public class NnUserPref implements Serializable {    
    private static final long serialVersionUID = -708171304411630395L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
        
    @Persistent
    private long userId;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String item;
    public static final String FB_USER_ID = "fb-user-id";
    public static final String FB_TOKEN = "fb-token";  //accessToken
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String value;
    
    public NnUserPref(NnUser user, String item, String value) {
        this.userId = user.getId();
        this.item = item;
        this.value = value;
    }

    @Persistent
    private Date createDate;
    
    @Persistent
    private Date updateDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }    
}
