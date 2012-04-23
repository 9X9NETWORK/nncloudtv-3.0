package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="nnchannel_autosharing", detachable="true")
public class NnChannelAutosharing implements Serializable {
	
	private static final long serialVersionUID = -2644869375639221703L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent	
	private long msoId;
	
	@Persistent	
	private long channelId;
	
	@Persistent
	private short type;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)	
	private String target;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)	
	private String parameter;
	
	@Persistent
	private Date createDate;
	
	public NnChannelAutosharing(long msoId, long channelId, short type) {
		this.msoId = msoId;
		this.channelId = channelId;
		this.type = type;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setMsoId(long msoId) {
		this.msoId = msoId;
	}

	public long getMsoId() {
		return msoId;
	}

	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}

	public long getChannelId() {
		return channelId;
	}

	public void setType(short type) {
		this.type = type;
	}

	public short getType() {
		return type;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getParameter() {
		return parameter;
	}

	public String getTarget() {
	    return target;
    }

	public void setTarget(String target) {
	    this.target = target;
    }
}
