package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;
import javax.jdo.annotations.*;

import com.nncloudtv.lib.NnStringUtil;

/**
 * Programs(shows) under a NnChannel.
 */
@PersistenceCapable(table="nnprogram", detachable="true")
public class NnProgram implements Serializable {
	private static final long serialVersionUID = 5553891672235566066L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	private long channelId;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String name;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=500)
	private String comment;
	
	@Persistent
	private short contentType;
	public static final short CONTENTTYPE_DIRECTLINK = 0;
	public static final short CONTENTTYPE_YOUTUBE = 1;
	public static final short CONTENTTYPE_SCRIPT = 2;
	public static final short CONTENTTYPE_RADIO = 3;
	
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String intro;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String imageUrl;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String imageLargeUrl; //used for radio programs
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String fileUrl;

	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String audioFileUrl;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String storageId; // id of where the file physically stores, from channel parsing service
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String errorCode;

	@Persistent
	private short status;
	//general
	public static short STATUS_OK = 0;
	public static short STATUS_ERROR = 1;
	public static short STATUS_NEEDS_REVIEWED = 2;
	//quality
	public static short STATUS_BAD_QUALITY = 101;	
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String duration;
		
	@Persistent
	private boolean isPublic; 
	
	@Persistent
	private short type;
	public static short TYPE_VIDEO = 1;
	public static short TYPE_AUDIO = 2;	

	//used by maplestage channels, 9x9 channels, youtube special sorting channels
	//please not it is a string instead of digit, make 1 00000001, 8 digits total 
	@Persistent
	@Column(jdbcType="VARCHAR", length=8)
	private String seq;

	//used with seq
	@Persistent
	@Column(jdbcType="VARCHAR", length=8)	
	private String subSeq;
	
	@Persistent
	private Date createDate;
		
	@Persistent
	private Date updateDate;
	
	public NnProgram(String name, String intro, String imageUrl, short type) {
		this.name = name;
		this.intro = intro;
		this.imageUrl = imageUrl;
		this.type = type;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getChannelId() {
		return channelId;
	}

	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}

	public String getName() {
		if (name != null)
			name = NnStringUtil.revertHtml(name);
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
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

	public String getIntro() {
		if (intro != null)
			intro = NnStringUtil.revertHtml(intro);
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {		
		this.status = status;
	}

	public String getImageLargeUrl() {
		return imageLargeUrl;
	}

	public void setImageLargeUrl(String imageLargeUrl) {
		this.imageLargeUrl = imageLargeUrl;
	}

	public String getStorageId() {
		return storageId;
	}

	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getAudioFileUrl() {
		return audioFileUrl;
	}

	public void setAudioFileUrl(String audioFileUrl) {
		this.audioFileUrl = audioFileUrl;
	}

	public String getComment() {
		if (comment != null)
			comment = NnStringUtil.revertHtml(comment);
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public short getContentType() {
		return contentType;
	}

	public void setContentType(short contentType) {
		this.contentType = contentType;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getSubSeq() {
		return subSeq;
	}

	public void setSubSeq(String subSeq) {
		this.subSeq = subSeq;
	}
	
}
