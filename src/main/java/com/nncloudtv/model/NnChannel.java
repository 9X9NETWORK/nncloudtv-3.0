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
 * a Channel
 */
@PersistenceCapable(table="nnchannel", detachable="true")
public class NnChannel implements Serializable {
	private static final long serialVersionUID = 6138621615980949044L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
			
	@Persistent
	@Column(jdbcType="VARCHAR", length=500)
	private String name; 

	@Persistent
	@Column(jdbcType="VARCHAR", length=500)
	private String oriName; //instead the original podcast/youtube name, we make up something 
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=500)
	private String intro;

	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String imageUrl; 
	public static String PROCESSING_IMAGE_URL = "http://s3.amazonaws.com/9x9ui/images/processing.png";
	public static String ERROR_IMAGE_URL = "http://9x9ui.s3.amazonaws.com/9x9playerV65/images/error.png";
	public static String FB_IMAGE_URL = "https://s3.amazonaws.com/9x9ui/images/facebook-icon.gif";
	
	@Persistent
	private boolean isPublic;
		
	@Persistent
	private int programCnt;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=500)
	private String sourceUrl;

	@NotPersistent
	private short type; //Use with MsoIpg and Subscription, to define attributes such as MsoIpg.TYPE_READONLY

	@Persistent
	@Column(jdbcType="VARCHAR", length=500)
	private String tag;
	
	@Persistent
	public short contentType;
	public static final short CONTENTTYPE_SYSTEM = 1;
	public static final short CONTENTTYPE_PODCAST = 2;
	public static final short CONTENTTYPE_YOUTUBE_CHANNEL = 3;
	public static final short CONTENTTYPE_YOUTUBE_PLAYLIST = 4;
	public static final short CONTENTTYPE_FACEBOOK = 5;
	public static final short CONTENTTYPE_MIXED = 6;
	public static final short CONTENTTYPE_SLIDE = 7;
	public static final short CONTENTTYPE_MAPLE_VARIETY = 8;
	public static final short CONTENTTYPE_MAPLE_SOAP = 9;
	public static final short CONTENTTYPE_YOUTUBE_SPECIAL_SORTING = 10;
	
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String piwik;
	
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
							
	//value mostly passing from transcoding service
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String errorReason;
		
	@NotPersistent
	private short seq; //use with subscription, to specify sequence in IPG. 
		
	public static final short SORT_NEWEST_TO_OLDEST = 1; //default
	public static final short SORT_OLDEST_TO_NEWEST = 2;
	public static final short SORT_MAPEL = 3;
	@Persistent
	private short sorting;

	@NotPersistent
	private String recentlyWatchedProgram;  

	@NotPersistent	
	private int subscriptionCnt;
	
	@Persistent 
	private Date createDate;
		
	@Persistent
	private Date updateDate;
			
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String transcodingUpdateDate; //timestamps from transcoding server			
		
	public NnChannel(String name, String intro, String imageUrl) {
		this.name = name;
		this.intro = intro;
		this.imageUrl = imageUrl;
	}
	
	public NnChannel(String sourceUrl) {
		this.sourceUrl = sourceUrl;
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
			imageUrl = ERROR_IMAGE_URL;
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

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public int getProgramCnt() {
		return programCnt;
	}

	public void setProgramCnt(int cnt) {
		this.programCnt = cnt;
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

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public short getSeq() {
		return seq;
	}

	public void setSeq(short seq) {
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

	public int getSubscriptionCnt() {
		return subscriptionCnt;
	}

	public void setSubscriptionCnt(int subscriptionCnt) {
		this.subscriptionCnt = subscriptionCnt;
	}

	public void setTranscodingUpdateDate(String transcodingUpdateDate) {
		this.transcodingUpdateDate = transcodingUpdateDate;
	}

	public String getTranscodingUpdateDate() {
		return transcodingUpdateDate;
	}

	public String getOriName() {
		return oriName;
	}

	public void setOriName(String oriName) {
		this.oriName = oriName;
	}

	public short getSorting() {
		return sorting;
	}

	public void setSorting(short sorting) {
		this.sorting = sorting;
	}

	public String getRecentlyWatchedProgram() {
		return recentlyWatchedProgram;
	}

	public void setRecentlyWatchedProgram(String recentlyWatchedProgram) {
		this.recentlyWatchedProgram = recentlyWatchedProgram;
	}

	public String getPiwik() {
		return piwik;
	}

	public void setPiwik(String piwik) {
		this.piwik = piwik;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
}
