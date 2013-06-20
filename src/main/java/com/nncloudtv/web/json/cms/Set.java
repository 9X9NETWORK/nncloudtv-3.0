package com.nncloudtv.web.json.cms;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Set implements Serializable {
    
    /**
     * eclipse generated
     */
    private static final long serialVersionUID = -4777307952253124679L;

    private long id;
    
    private long msoId;
    
    private long displayId;
    
    private int channelCnt;
    
    private String lang;
    
    private short seq;
    
    private String tag;
    
    private String name;
    
    private short sortingType;
    
    public String toString() {
        return new ToStringBuilder(this).
            append("id", id).
            append("msoId", msoId).
            append("channelCnt", channelCnt).
            append("lang", lang).
            append("seq", seq).
            append("tag", tag).
            append("name", name).
            append("sortingType", sortingType).
            toString();
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

    public int getChannelCnt() {
        return channelCnt;
    }

    public void setChannelCnt(int channelCnt) {
        this.channelCnt = channelCnt;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getSortingType() {
        return sortingType;
    }

    public void setSortingType(short sortingType) {
        this.sortingType = sortingType;
    }

    public long getDisplayId() {
        return displayId;
    }

    public void setDisplayId(long displayId) {
        this.displayId = displayId;
    }
    
}
