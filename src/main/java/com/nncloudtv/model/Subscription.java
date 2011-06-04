package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * User's channel subscriptions. 
 */
@PersistenceCapable(detachable="true")
public class Subscription implements Serializable {

	private static final long serialVersionUID = -8947021127329404786L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	@Persistent
	@Column(name="user_id")
	private long userId;
	
	@Persistent
	@Column(name="channel_id")
	private long channelId;
	
	@Persistent
	private int seq;
	
	@Persistent
	private short type; //The value derived from msoIpg

	@Persistent
	@Column(name="create_date")
	private Date createDate;
	
	@Persistent
	@Column(name="udpate_date")
	private Date updateDate;
	
	public Subscription(long userId, long channelId, int seq, short type) {
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

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
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