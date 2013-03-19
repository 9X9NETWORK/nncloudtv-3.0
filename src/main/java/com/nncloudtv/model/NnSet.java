package com.nncloudtv.model;

import java.io.Serializable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * 9x9 Channel Set
 */
@PersistenceCapable(table="nnset", detachable="true")
public class NnSet implements Serializable {
    
    private static final long serialVersionUID = 6138621615980949044L;
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long msoId;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String name;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String imageUrl;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=5)
    private String lang;

    @Persistent
    private short seq; 
    
    @Persistent
    private int cntChannel;

    @Persistent
    private boolean featured;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getCntChannel() {
        return cntChannel;
    }

    public void setCntChannel(int cntChannel) {
        this.cntChannel = cntChannel;
    }

    public long getMsoId() {
        return msoId;
    }

    public void setMsoId(long msoId) {
        this.msoId = msoId;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }
    
}
