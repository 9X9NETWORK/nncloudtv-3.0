package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.*;

/**
 * 9x9 Channel
 */
@PersistenceCapable(table="nnchannel", detachable="true")
public class NnChannel implements Serializable {
	private static final long serialVersionUID = 6138621615980949044L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	@Column(name="user_id")
	private long userId;
		
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
	@Column(name="is_public")
	private boolean isPublic;
	
	@Persistent
	@Column(name="lang_code", jdbcType="VARCHAR", length=255)
	private String langCode;
	
	@Persistent
	private short rating;
	
	@Persistent
	private short license;
	
	@Persistent
	private short advertsing;

	@Persistent
	@Column(name="program_count")
	private int programCount;
	
	@Persistent
	@Column(name="source_url", jdbcType="VARCHAR", length=255)
	private String sourceUrl;

	@NotPersistent
	private short type; //Use with MsoIpg and Subscription, to define attributes such as MsoIpg.TYPE_READONLY

	@Persistent
	@Column(name="content_type")
	public short contentType;
	
	@Persistent
	private short status;
				
	//enforce transcoding, could be used to assign special formats or bit rates
	//currently 0 is no, 1 is yes
	@Persistent
	@Column(name="enforce_transcoding")
	private short enforceTranscoding; 
		
	//value mostly passing from transcoding service
	@Persistent
	@Column(name="error_reason", jdbcType="VARCHAR", length=255)
	private String errorReason;
		
	@NotPersistent
	private int seq; //use with subscription, to specify sequence in IPG. 
		
	@NotPersistent	
	private int subscriptionCount;
	
	@Persistent
	@Column(name="create_date") 
	private Date createDate;
		
	@Persistent
	@Column(name="update_date")
	private Date updateDate;
			
	@Persistent
	@Column(name="transcoding_update_date", jdbcType="VARCHAR", length=255)
	private String transcodingUpdateDate; //timestamps from transcoding server	
		
	public static short MAX_CHANNEL_SIZE = 50;
	/* content */
	public static final short CONTENTTYPE_SYSTEM = 1;
	public static final short CONTENTTYPE_PODCAST = 2;
	public static final short CONTENTTYPE_YOUTUBE_CHANNEL = 3;
	public static final short CONTENTTYPE_YOUTUBE_PLAYLIST = 4;
	public static final short CONTENTTYPE_FACEBOOK = 5;
	
	/* status */
	//general
	public static final short STATUS_SUCCESS = 0;
	public static final short STATUS_ERROR = 1;
	public static final short STATUS_PROCESSING = 2;
	public static final short STATUS_WAIT_FOR_APPROVAL = 3;
	//invalid
	public static final short STATUS_INVALID_FORMAT = 51;
	public static final short STATUS_URL_NOT_FOUND = 53;
	//quality
	public static final short STATUS_NO_VALID_EPISODE = 100;
	public static final short STATUS_BAD_QUALITY = 101;
	//internal
	public static final short STATUS_TRANSCODING_DB_ERROR = 1000;
	public static final short STATUS_NNVMSO_JSON_ERROR = 1001;
		
	public NnChannel(String name, String intro, String imageUrl, long userId) {
		this.name = name;
		this.intro = intro;
		this.imageUrl = imageUrl;
		this.userId = userId;
	}
	
	public NnChannel(String sourceUrl, long userId) {
		this.sourceUrl = sourceUrl;
		this.userId = userId;
	}
		
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	public Date getCreateDate() {
		return createDate;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public int getProgramCount() {
		return programCount;
	}

	public void setProgramCount(int count) {
		this.programCount = count;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public short getContentType() {
		return contentType;
	}

	public void setContentType(short contentType) {
		this.contentType = contentType;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	public short getRating() {
		return rating;
	}

	public void setRating(short rating) {
		this.rating = rating;
	}

	public short getLicense() {
		return license;
	}

	public void setLicense(short license) {
		this.license = license;
	}

	public short getAdvertsing() {
		return advertsing;
	}

	public void setAdvertsing(short advertsing) {
		this.advertsing = advertsing;
	}

	public int getSubscriptionCount() {
		return subscriptionCount;
	}

	public void setSubscriptionCount(int subscriptionCount) {
		this.subscriptionCount = subscriptionCount;
	}

	public void setTranscodingUpdateDate(String transcodingUpdateDate) {
		this.transcodingUpdateDate = transcodingUpdateDate;
	}

	public short getEnforceTranscoding() {
		return enforceTranscoding;
	}

	public void setEnforceTranscoding(short enforceTranscoding) {
		this.enforceTranscoding = enforceTranscoding;
	}

	public String getTranscodingUpdateDate() {
		return transcodingUpdateDate;
	}
	
}
