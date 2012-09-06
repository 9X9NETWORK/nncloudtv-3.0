package com.nncloudtv.model;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * 9x9 Device account. It's not necessarily associated with 9x9 User account.
 */
@PersistenceCapable(table="nndevice", detachable="true")
public class NnDevice {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String token; //each device has a unique token
    
    @Persistent
    private long userId; //if a device has associated user account, not always
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String type; //not really used for now, to identify device type

    @Persistent
    private Date createDate;

    @Persistent
    private short shard; //which shard a user belongs to
    
    @Persistent
    private Date updateDate;
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public short getShard() {
        return shard;
    }

    public void setShard(short shard) {
        this.shard = shard;
    }
    
}
