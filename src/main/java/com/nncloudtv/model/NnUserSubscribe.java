package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * User's channel subscriptions. 
 */
@PersistenceCapable(table="nnuser_subscribe", detachable="true")
public class NnUserSubscribe implements Serializable {

    private static final long serialVersionUID = -8947021127329404786L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent
    private long userId;
    
    @Persistent
    private long channelId;
    
    @Persistent
    private short seq; //from 1-81
    
    @Persistent
    private short type; //The value derived from msoIpg

    @Persistent
    private Date createDate;
    
    @Persistent
    private Date updateDate;
    
    public NnUserSubscribe(long userId, long channelId, short seq, short type) {
        this.userId = userId;
        this.channelId= channelId;
        this.seq = seq;
        this.type = type;
    }    

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

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }
}