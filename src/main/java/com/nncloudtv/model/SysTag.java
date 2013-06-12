package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

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
    public static final short TYPE_CATEGORY = 1;
    public static final short TYPE_SET = 2;
    public static final short TYPE_DAYPARTING = 3;
    public static final short TYPE_PREVIOUS = 4;    
    public static final short TYPE_SUBSCRIPTION = 5;
    public static final short TYPE_ACCOUNT = 6;
    public static final short TYPE_33SET = 7;
    
    @Persistent
    private short sorting;
    public static final short SORT_SEQ = 1; //default
    public static final short SORT_DATE = 2;
    
    @Persistent
    private short seq;
    
    @Persistent
    private boolean featured; //for set

    @Persistent
    private short timeStart; //for dayparting

    @Persistent
    private short timeEnd; //for dayparting

    @Persistent
    @Column(jdbcType="VARCHAR", length=10)    
    private String attr;
    public static final short ATTR_APP_STACK = 0;
    public static final short ATTR_APP_SUBSCRIPTION = 1;
    public static final short ATTR_APP_ACCOUNT = 2;
    public static final short ATTR_APP_CHANNEL = 3;
    public static final short ATTR_APP_DIR = 4;
    public static final short ATTR_APP_SEARCH = 5;
    
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

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
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

    public short getSorting() {
        return sorting;
    }

    public void setSorting(short sorting) {
        this.sorting = sorting;
    }
    
}
