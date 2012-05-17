package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * To relate Set and Channel
 */
@PersistenceCapable(table="nnset_to_nnchannel", detachable = "true")
public class NnSetToNnChannel implements Serializable {

	private static final long serialVersionUID = -6253012826484625104L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	private long channelId;
	
	@Persistent
	private long setId;
	
	@Persistent
	private short seq;
	
	@Persistent
	private Date createDate;
	
	@Persistent
	private Date updateDate;

	public NnSetToNnChannel(long setId, long channelId, short seq) {
		this.setId = setId;
		this.channelId = channelId;
		this.seq = seq;
		Date now = new Date();
		this.createDate = now;
		this.updateDate = now;
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

	public short getSeq() {
		return seq;
	}

	public void setSeq(short seq) {
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
