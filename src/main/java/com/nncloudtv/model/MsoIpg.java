package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/** 
 * Mso's default IPG, defining what channel is shown on the ipg
 */
@PersistenceCapable(table="mso_ipg", detachable="true")
public class MsoIpg  implements Serializable {
	private static final long serialVersionUID = -1577337108738062176L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
		
	@Persistent
	@Column(name="mso_id")
	private long msoId; 

	@Persistent 
	@Column(name="channel_id")
	private long channelId;
	
	public static short TYPE_GENERAL = 1;
	public static short TYPE_READONLY = 2;
	@Persistent	
	private short type;
	
	@Persistent
	private int seq;

	@Persistent
	private short start;
	
	@Persistent
	@Column(name="create_date")
	private Date createDate;
	
	@Persistent
	@Column(name="update_date")
	private Date updateDate;
	
	public MsoIpg(long msoId, long channelId, int seq, short type) {
		this.msoId = msoId;
		this.channelId = channelId;
		this.seq = seq;
		this.type = type;
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

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
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

	public short getStart() {
		return start;
	}

	public void setStart(short start) {
		this.start = start;
	}
	
}
