package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/** 
 * Content category, the root of our content directory
 */
@PersistenceCapable(table="category", detachable="true")
public class Category implements Serializable {

	private static final long serialVersionUID = -3641562781732198821L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=5)
	private String lang;	
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String name; //category name
	public static String UNCATEGORIZED = "UNCATEGORIZED";
	
	@Persistent
	private boolean isPublic; //shows only public categories on our directory
	
	@Persistent
	private int channelCnt; //channel count in each category
	
	@Persistent
	private short seq; //sequence shown in the directory
	
	@Persistent
	private Date updateDate;

	public Category() {}	
	
	public Category(String name, boolean isPublic) {
		this.name = name;
		this.isPublic = isPublic;
	}
	
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
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}	
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}	

	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("name: " + name + ";");
        buffer.append("channelCount: " + channelCnt + ";");
        return buffer.toString();
	}

	public short getSeq() {
		return seq;
	}

	public void setSeq(short seq) {
		this.seq = seq;
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
	
}
