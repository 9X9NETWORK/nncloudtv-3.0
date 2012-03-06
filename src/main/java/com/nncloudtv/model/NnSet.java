package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * 9x9 Channel Set
 */
@PersistenceCapable(table="nnset", detachable="true")
public class NnSet implements Serializable {
	
	private static final long serialVersionUID = 6138621615980949044L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
		
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String name;
		
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String intro;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String imageUrl;

	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String imageLargeUrl;
	
	@Persistent
	private boolean isPublic;
	
	//it will be used as part of the url for outside to access
	//example, beautifulUrl value abc, user can access such NnSet via http://domain/abc
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String beautifulUrl;  

	@Persistent
	@Column(jdbcType="VARCHAR", length=5)
	private String lang;
	
    @Persistent
    private int channelCnt;
	
  //it's used for recommendation !!! seems not used, in nnsettonnchannel    
	@Persistent
	private short seq; 

	@Persistent
	private boolean featured;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String tag;
	
	@Persistent
	private Date createDate;
		
	@Persistent
	private Date updateDate;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String piwik;
	
	@NotPersistent
	private int subscriptionCnt;
    
	public NnSet(String name, String intro, boolean isPublic) {
		this.name = name;
		this.intro = intro;
		this.isPublic = isPublic;
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
		
	public void setBeautifulUrl(String beautifulUrl) {
		this.beautifulUrl = beautifulUrl;
	}
	
	public String getBeautifulUrl() {
		return beautifulUrl;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
		
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getImageLargeUrl() {
		return imageLargeUrl;
	}

	public void setImageLargeUrl(String imageLargeUrl) {
		this.imageLargeUrl = imageLargeUrl;
	}

	public String getPiwik() {
		return piwik;
	}

	public void setPiwik(String piwik) {
		this.piwik = piwik;
	}

	public int getChannelCnt() {
		return channelCnt;
	}

	public void setChannelCnt(int channelCnt) {
		this.channelCnt = channelCnt;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public short getSeq() {
		return seq;
	}

	public void setSeq(short seq) {
		this.seq = seq;
	}

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public int getSubscriptionCnt() {
		return subscriptionCnt;
	}

	public void setSubscriptionCnt(int subscriptionCnt) {
		this.subscriptionCnt = subscriptionCnt;
	}
	
}
