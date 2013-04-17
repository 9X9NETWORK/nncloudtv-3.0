package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

//creative
@PersistenceCapable(table="poi_event", detachable="true")
public class PoiEvent implements Serializable {
    private static final long serialVersionUID = -1261189136283925861L;
        
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent
    private long userId; //will be replaced by profileId
    
    @Persistent
    private long msoId; //will be replaced by profileId

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String name;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String notifyMsg;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String notifyScheduler; //timestamp list, separate by comma    
    
    @Persistent
    private short type;
    public static final short TYPE_HYPERCHANNEL = 1;

    @Persistent
    @Column(jdbcType="VARCHAR", length=2000)
    private String context;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String message; //response message   

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

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getHyperChannelText() {
        if (context != null) {
            String[] splits = context.split("\\|");            
            if (splits.length > 1)
                return splits[1];
        }
        return null;
    }

    public String getHyperChannelLink() {
        if (context != null) {
            String[] splits = context.split("\\|");            
            if (splits.length > 1)
                return splits[0];
        }
        return null;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMsoId() {
        return msoId;
    }

    public void setMsoId(long msoId) {
        this.msoId = msoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotifyMsg() {
        return notifyMsg;
    }

    public void setNotifyMsg(String notifyMsg) {
        this.notifyMsg = notifyMsg;
    }

    public String getNotifyScheduler() {
        return notifyScheduler;
    }

    public void setNotifyScheduler(String notifyScheduler) {
        this.notifyScheduler = notifyScheduler;
    }
    
}
