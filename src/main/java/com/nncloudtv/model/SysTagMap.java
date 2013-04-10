package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="systag_map", detachable="true")
public class SysTagMap implements Serializable {
    private static final long serialVersionUID = 6301796133250702476L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent
    private long sysTagId;

    @Persistent
    private long channelId;
    
    @Persistent
    private short seq;
    
    @Persistent
    private short timeStart;

    @Persistent
    private short timeEnd;

    @Persistent
    @Column(jdbcType="VARCHAR", length=10)    
    private String attr;

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

    public long getSysTagId() {
        return sysTagId;
    }

    public void setSysTagId(long sysTagId) {
        this.sysTagId = sysTagId;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public short getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(short timeStart) {
        this.timeStart = timeStart;
    }

    public short getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(short timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
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
