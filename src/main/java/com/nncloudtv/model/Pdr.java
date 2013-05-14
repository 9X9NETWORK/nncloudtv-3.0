package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="pdr", detachable="true")
public class Pdr implements Serializable {    
    
    private static final long serialVersionUID = 1064168991300530081L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
    
    @Persistent
    private long userId;

    //when looking up NnUser, token and userId should find the same user 
    //for easier lookup        
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String userToken;
    
    @Persistent
    private long deviceId;
    
    //when looking up NnDevice, token and deviceId should find the same device 
    //for easier lookup        
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String deviceToken;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String session;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String ip;
        
    @Persistent
    @Column(jdbcType="LONGVARCHAR", length=100000)
    private String detail;    

    @Persistent
    private Date updateDate;
    
    public Pdr(long userId, String session, String detail) {
        this.userId = userId;
        this.session = session;
        this.detail = detail;
    }
    
    public Pdr (NnUser user, NnDevice device, String session, String detail) {
        this.session = session;
        if (user != null) {
            this.userId = user.getId();
            this.userToken = user.getToken();            
        }
        if (device != null) {
            this.deviceId = device.getId();
            this.deviceToken = device.getToken();
        }
        this.detail = detail;
    }
    
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
