package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.*;

/**
 * The ownership between mso and channel/channelset/program
 * 
 * @author louis
 *
 */
@PersistenceCapable(table="content_ownership", detachable = "true")
public class ContentOwnership implements Serializable {
	
	protected static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	@Column(name="mso_id")
	private long msoId;
	
	/**
	 * channelSetId / channelId / programId according to contentType
	 */
	@Persistent
	@Column(name="content_id")
	private long contentId;
	
	@Persistent
	@Column(name="content_type")
	private short contentType;
	public static final short TYPE_CHANNELSET = 1;
	public static final short TYPE_CHANNEL = 2;
	public static final short TYPE_PROGRAM = 3;
	
	@Persistent
	@Column(name="create_mode")
	private short createMode;
	public static final short MODE_UPLOAD = 1;
	public static final short MODE_CURATE = 2;
	
	@Persistent
	@Column(name="create_date")
	private Date createDate;
	
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public short getCreateMode() {
		return createMode;
	}
	
	public void setCreateMode(short createMode) {
		this.createMode = createMode;
	}
	
	public short getContentType() {
		return contentType;
	}
	
	public void setContentType(short contentType) {
		this.contentType = contentType;
	}
	
	public long getContentId() {
		return contentId;
	}
	
	public void setContentId(long contentId) {
		this.contentId = contentId;
	}
	
	public long getMsoId() {
		return msoId;
	}
	
	public void setMsoId(long msoId) {
		this.msoId = msoId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
		
}
