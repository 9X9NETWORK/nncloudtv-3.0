package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * To relate Set and Channel
 */
@PersistenceCapable(table="nnset_channel", detachable = "true")
public class NnSetChannel implements Serializable {

	private static final long serialVersionUID = -6253012826484625104L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	@Column(name="channel_id")
	private long channelId;
	
	@Persistent
	@Column(name="set_id")
	private long setId;
	
	@Persistent
	private int seq;
	
	@Persistent
	@Column(name="create_date")
	private Date createDate;
	
	@Persistent
	@Column(name="udpate_date")
	private Date updateDate;

	public NnSetChannel(long setId, long channelId, int seq) {
		this.setId = setId;
		this.channelId = channelId;
		this.seq = seq;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSetId() {
		return setId;
	}

	public void setSetId(long setId) {
		this.setId = setId;
	}

	public long getChannelId() {
		return channelId;
	}

	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}
	
}
