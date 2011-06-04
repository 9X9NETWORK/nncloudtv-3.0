package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/** 
 * User's IPG. Currently is used for FB.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Ipg implements Serializable {	
	private static final long serialVersionUID = 104915710055479694L;

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
	@Column(name="program_id")
	private long programId;
	
	@Persistent
	@Column(name="program_id_str", jdbcType="VARCHAR", length=255)
	private String programIdStr; //for programs not storing in our system, rather in youtube or other sources
	
	@Persistent
	@Column(name="create_date")
	private Date createDate;
	
	@Persistent
	@Column(name="update_date")
	private Date updateDate;
	
	@Persistent(serialized = "true")
	private Hashtable<Integer,Long> channels;
		
	public Hashtable<Integer, Long> getChannels() {
		return channels;
	}

	public void setChannels(Hashtable<Integer, Long> channels) {
		this.channels = channels;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}
	
	public Date getCreateDate() {
		return this.createDate;
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

	public long getProgramId() {
		return programId;
	}

	public void setProgramId(long programId) {
		this.programId = programId;
	}

	public String getProgramIdStr() {
		return programIdStr;
	}

	public void setProgramIdStr(String programIdStr) {
		this.programIdStr = programIdStr;
	}	
	
}
