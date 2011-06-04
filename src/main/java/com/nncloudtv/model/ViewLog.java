package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="view_log", detachable="true")
public class ViewLog implements Serializable {

	private static final long serialVersionUID = -4611831552437881824L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	@Persistent
	@Column(name="user_id")
	private long userId;
	
	@Persistent
	@Column(name="channel_id")
	private long channelId;
	
	@Persistent(serialized = "true")
	private HashSet<Long> programs = new HashSet<Long>();

	@Persistent
	private Date createDate;
	
	@Persistent
	private Date updateDate;
	
	public ViewLog(long userId, long channelId) {
		this.userId = userId;
		this.channelId = channelId;
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

	public HashSet<Long> getPrograms() {
		return programs;
	}

	public void setPrograms(HashSet<Long> programs) {
		this.programs = programs;
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
