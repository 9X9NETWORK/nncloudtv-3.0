package com.nncloudtv.model;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/** 
 * User's problem reporting. 
 */
@PersistenceCapable(table="nnuser_report", detachable="true")
public class NnUserReport {

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
    private String type;
    public static String TYPE_PROBLEM = "problem";
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=1000)
    private String comment; //use with item as key/value pair
    
    //session defined by the player, it's the same as PdrRaw session. 
    //to associate user's report and our logging data.
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String session;
    
    @Persistent
    private Date createDate;

    public NnUserReport() {}
    
    public NnUserReport(NnUser user, NnDevice device, String session, String type, String comment) {
        if (user != null) {
            this.userId = user.getId();
            this.userToken = user.getToken();
        }
        if (device != null) {
            this.deviceId = device.getId();
            this.deviceToken = device.getToken();
        }
        this.session = session;
        this.comment = comment;
        if (type == null)
            this.setType(NnUserReport.TYPE_PROBLEM);
        else
            this.setType(type);
    }    
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getSession() {
        return session;
    }
    public void setSession(String session) {
        this.session = session;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }    
}
