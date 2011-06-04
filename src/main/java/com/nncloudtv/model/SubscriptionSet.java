package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * User's set subscriptions. 
 */
@PersistenceCapable(table="subscription_set", detachable = "true")
public class SubscriptionSet implements Serializable {
	
	private static final long serialVersionUID = -8270766676514088749L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
		
	@Persistent
	@Column(name="user_id")
	private long userId;
	
	@Persistent
	@Column(name="set_id")
	private long setId;
	
	@Persistent
	@Column(name="set_name", jdbcType="VARCHAR", length=255)
	private String setName;

	@Persistent
	@Column(name="set_image_url", jdbcType="VARCHAR", length=255)
	private String setImageUrl;
	
	@Persistent
	@Column(name="seq")
	private short seq;
	
	@Persistent
	private int status;
	public static final int STATUS_FREE = 0;
	public static final int STATUS_OCCUPIED = 1;
	
	@Persistent
	@Column(name="create_date")
	private Date createDate;
	
	@Persistent
	@Column(name="update_date")
	private Date updateDate;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getUserId() {
		return userId;
	}

	public long getSetId() {
		return setId;
	}

	public void setSetId(long setId) {
		this.setId = setId;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public short getSeq() {
		return seq;
	}

	public void setSeq(short seq) {
		this.seq = seq;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
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

	public String getSetImageUrl() {
		return setImageUrl;
	}

	public void setSetImageUrl(String setImageUrl) {
		this.setImageUrl = setImageUrl;
	}
		
}
