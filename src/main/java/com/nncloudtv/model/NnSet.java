package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
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
	@Column(name="mso_id")
	private long msoId; // don't use this, please refer to class ContentOwnership
	
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
	@Column(name="default_url", jdbcType="VARCHAR", length=255)
	private String defaultUrl;
	
	@Persistent
	@Column(name="beautiful_url", jdbcType="VARCHAR", length=255)
	private String beautifulUrl;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String tag;
	
	@Persistent
	@Column(name="create_date")
	private Date createDate;
		
	@Persistent
	@Column(name="update_date")
	private Date updateDate;
	
	public NnSet(long msoId, String name, String intro, boolean isPublic) {
		this.msoId = msoId;
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
	
	public String getDefaultUrl() {
		return defaultUrl;
	}
	
	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
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
	
	public long getMsoId() {
		return msoId;
	}
	
	public void setMsoId(long msoId) {
		this.msoId = msoId;
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
		
}
