package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Category and MsoChannel establish connections through CategoryChannel  
 */
@PersistenceCapable(table="category_channel", detachable="true")
public class CategoryChannel implements Serializable {

	private static final long serialVersionUID = -3245839522361848219L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	@Persistent
	@Column(name="category_id")
	private long categoryId;
	
	@Persistent
	@Column(name="channel_id")
	private long channelId;

	@Persistent
	@Column(name="create_date")
	private Date createDate;
	
	@Persistent
	@Column(name="update_date")
	private Date updateDate;

	public CategoryChannel(long categoryId, long channelId) {
		this.categoryId = categoryId;
		this.channelId = channelId;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
