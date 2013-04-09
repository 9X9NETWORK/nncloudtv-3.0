package com.nncloudtv.model;

import java.io.Serializable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="systag", detachable="true")
public class SysTag implements Serializable {
    private static final long serialVersionUID = 6838197745642387197L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent
    private long msoId;

    @Persistent
    private short type;

    @Persistent
    private short seq;
    
    @Persistent
    private int cntChannel;

    @Persistent
    @Column(jdbcType="VARCHAR", length=500)
    private String popularTag; //sequence shown in the directory

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

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public int getCntChannel() {
        return cntChannel;
    }

    public void setCntChannel(int cntChannel) {
        this.cntChannel = cntChannel;
    }

    public String getPopularTag() {
        return popularTag;
    }

    public void setPopularTag(String popularTag) {
        this.popularTag = popularTag;
    }
    
}
