package com.nncloudtv.model;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/** 
 * For website's dynamic content, example would be entries in FAQ.
 * Used as key/value pair.
 */
@PersistenceCapable(table="nncontent", detachable = "true")
public class NnContent {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent
    private long msoId;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String item;
    
    @Persistent
    @Column(jdbcType="LONGVARCHAR", length=100000)
    private String value;

    @Persistent
    @Column(jdbcType="VARCHAR", length=5)
    private String lang;
    
    @Persistent
    private Date createDate;
    
    @Persistent
    private Date updateDate;

    public NnContent() {}
    public NnContent(String item, String value, String lang, long msoId) {
        this.item = item;
        this.value = value;
        this.lang = lang;
        this.msoId = msoId;
    }    

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public long getMsoId() {
        return msoId;
    }
    
    public void setMsoId(long msoId) {
        this.msoId = msoId;
    }
    
}
