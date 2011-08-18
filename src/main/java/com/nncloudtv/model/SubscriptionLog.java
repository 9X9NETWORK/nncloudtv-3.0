package com.nncloudtv.model;

import java.io.Serializable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Subscription count
 */
@SuppressWarnings("serial")
@PersistenceCapable(table="subscription_log", detachable="true")
public class SubscriptionLog implements Serializable {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	@Column(name="mso_id")
	private long msoId;
	
	@Persistent
	@Column(name="channel_id")
	private long channelId;

	@Persistent
	@Column(name="set_id")
	private long setId;
	
	@Persistent
	private int count;

	public SubscriptionLog(long msoId, long channelId) {
		this.msoId = msoId;
		this.channelId = channelId;
		this.count = 1;
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

	public long getChannelId() {
		return channelId;
	}

	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getSetId() {
		return setId;
	}

	public void setSetId(long setId) {
		this.setId = setId;
	}		
}
