package com.nncloudtv.model;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="endpoint", detachable="true")
public class EndPoint {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String token; //each device has a unique token
    
    @Persistent
    private long userId; //if a device has associated user account, not always

    @Persistent
    private long msoId;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private short vendor; //not really used for now, to identify device type
    public static short VENDOR_UNDEFINED = 0;
    public static short VENDOR_GCM = 1;
    public static short VENDOR_APNS = 2;
    public static short VENDOR_SMS = 3;

    @Persistent
    private Date createDate;
    
    @Persistent
    private Date updateDate;

    public EndPoint(long userId, long msoId, String token, short vendor) {
        this.userId = userId;
        this.msoId = msoId;
        this.token = token;
        this.vendor = vendor;
    }
    
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getMsoId() {
        return msoId;
    }

    public void setMsoId(long msoId) {
        this.msoId = msoId;
    }

    public short getVendor() {
        return vendor;
    }

    public void setVendor(short vendor) {
        this.vendor = vendor;
    }
    
}
