package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;
import javax.jdo.annotations.*;

/**
 * Programs(shows) under a MsoChannel.
 */
@PersistenceCapable(table="nnprogram", detachable="true")
public class NnProgram implements Serializable {
	private static final long serialVersionUID = 5553891672235566066L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	@Column(name="channel_id")
	private long channelId;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String name;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String intro;
	
	@Persistent
	@Column(name="image_url", jdbcType="VARCHAR", length=255)
	private String imageUrl;
	
	@Persistent
	@Column(name="image_large_url", jdbcType="VARCHAR", length=255)
	private String imageLargeUrl;
	
	@Persistent
	@Column(name="mpeg_file_url", jdbcType="VARCHAR", length=255)
	private String mpeg4FileUrl;
	
	@Persistent
	@Column(name="webm_file_url", jdbcType="VARCHAR", length=255)
	private String webMFileUrl;				
	
	@Persistent
	@Column(name="other_file_url", jdbcType="VARCHAR", length=255)
	private String otherFileUrl;
	
	@Persistent
	@Column(name="audio_file_url", jdbcType="VARCHAR", length=255)
	private String audioFileUrl;
	
	@Persistent
	@Column(name="storage_id", jdbcType="VARCHAR", length=255)
	private String storageId; // id of where the file physically stores, from transcoding service
	
	@Persistent
	@Column(name="error_code", jdbcType="VARCHAR", length=255)
	private String errorCode;

	@Persistent
	private short status;
	//general
	public static short STATUS_OK = 0;
	public static short STATUS_ERROR = 1;
	//quality
	public static short STATUS_BAD_QUALITY = 101;	
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String duration;
		
	@Persistent
	@Column(name="is_public")
	private boolean isPublic; 
	
	@Persistent
	private short type;
	public static short TYPE_VIDEO = 1;
	public static short TYPE_AUDIO = 2;	
		
	@Persistent
	@Column(name="create_date")
	private Date createDate;
		
	@Persistent
	@Column(name="update_date")
	private Date updateDate;

	@Persistent
	@Column(name="pub_date")
	private Date pubDate; //the value from original publisher, such as podcast, or youtube
	
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

	public String getWebMFileUrl() {
		return webMFileUrl;
	}

	public void setWebMFileUrl(String webMFileUrl) {
		this.webMFileUrl = webMFileUrl;
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

	public String getMpeg4FileUrl() {
		return mpeg4FileUrl;
	}

	public void setMpeg4FileUrl(String mpeg4FileUrl) {
		this.mpeg4FileUrl = mpeg4FileUrl;
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

	public String getOtherFileUrl() {
		return otherFileUrl;
	}

	public void setOtherFileUrl(String otherFileUrl) {
		this.otherFileUrl = otherFileUrl;
	}

	public String getAudioFileUrl() {
		return audioFileUrl;
	}

	public void setAudioFileUrl(String audioFileUrl) {
		this.audioFileUrl = audioFileUrl;
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

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

}
