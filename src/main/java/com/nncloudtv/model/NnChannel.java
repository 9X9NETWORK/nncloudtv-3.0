package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.nncloudtv.lib.YouTubeLib;

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
	@Column(name="faux_name", jdbcType="VARCHAR", length=255)
	private String fauxName; //instead the original podcast/youtube name, we make up something 
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String intro;

	@Persistent
	@Column(name="faux_intro", jdbcType="VARCHAR", length=255)
	private String fauxIntro; //instead the original podcast/youtube description, we make up something 
	
	@Persistent
	@Column(name="image_url", jdbcType="VARCHAR", length=255)
	private String imageUrl; 
			
	@Persistent
	@Column(name="is_public")
	private boolean isPublic;
	
	@Persistent
	@Column(name="lang_code", jdbcType="VARCHAR", length=4)
	private String langCode;
	
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
	public static final short CONTENTTYPE_SYSTEM = 1;
	public static final short CONTENTTYPE_PODCAST = 2;
	public static final short CONTENTTYPE_YOUTUBE_CHANNEL = 3;
	public static final short CONTENTTYPE_YOUTUBE_PLAYLIST = 4;
	public static final short CONTENTTYPE_FACEBOOK = 5;
	public static final short CONTENTTYPE_MIXED = 6;
	public static final short CONTENTTYPE_SLIDE = 7;	
	
	@Persistent
	private short status;
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

	public String getPlayerPrefIntro() {
		if (getFauxIntro() != null && getFauxIntro().length() > 0) {
			return getFauxName();
		}
		return intro;
	}
		
	public String getPlayerPrefName() {
		if (getFauxName() != null && getFauxName().length() > 0) {
			return getFauxName();
		}
		return name;		
	}
	
	public String getPlayerPrefSource() {
		if (getSourceUrl() != null && getSourceUrl().contains("http://www.youtube.com"))
			return YouTubeLib.getYouTubeChannelName(getSourceUrl());		
		if (getContentType() == NnChannel.CONTENTTYPE_FACEBOOK)
			return getSourceUrl();
		return "";
	}
	
	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		if (intro != null) {
			intro = intro.replaceAll("\n", " ");
			intro = intro.replaceAll("\t", " ");
			int introLenth = (intro.length() > 256 ? 256 : intro.length()); 
			intro = intro.substring(0, introLenth);
		}
		this.intro = intro;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getPlayerPrefImageUrl() {
		String imageUrl = getImageUrl();
		if ((getStatus() == NnChannel.STATUS_ERROR) || 
		    (getStatus() != NnChannel.STATUS_WAIT_FOR_APPROVAL &&
			 getStatus() != NnChannel.STATUS_SUCCESS && 
			 getStatus() != NnChannel.STATUS_PROCESSING)) {	
			imageUrl = "http://9x9ui.s3.amazonaws.com/9x9playerV65/images/error.png";
		} 
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

	public String getFauxName() {
		return fauxName;
	}

	public void setFauxName(String fauxName) {
		this.fauxName = fauxName;
	}

	public String getFauxIntro() {
		return fauxIntro;
	}

	public void setFauxIntro(String fauxIntro) {
		this.fauxIntro = fauxIntro;
	}
	
}
