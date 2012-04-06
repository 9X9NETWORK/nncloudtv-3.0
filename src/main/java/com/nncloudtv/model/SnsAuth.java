package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.nncloudtv.web.json.facebook.FacebookPage;

@PersistenceCapable(table="sns_auth", detachable = "true")
public class SnsAuth implements Serializable {
	
	private static final long serialVersionUID = 1001080049969508799L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	private long msoId;
	
	@Persistent
	private short type;
	public static final short TYPE_FACEBOOK = 1;
	public static final short TYPE_TWITTER = 2;
	public static final short TYPE_PLURK = 3;
	public static final short TYPE_SINA = 4;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String token;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String secret;
	
	@Persistent
	private boolean enabled;
	
	@Persistent
	private Date createDate;
	
	@NotPersistent
	private List<FacebookPage> pages;
	
	public SnsAuth(long msoId, short type, String token) {
		this.msoId = msoId;
		this.type = type;
		this.token = token;
	}
	
	public void setMsoId(long msoId) {
		this.msoId = msoId;
	}

	public long getMsoId() {
		return msoId;
	}

	public void setType(short type) {
		this.type = type;
	}

	public short getType() {
		return type;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getSecret() {
		return secret;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public List<FacebookPage> getPages() {
		return pages;
	}

	public void setPages(List<FacebookPage> pages) {
		this.pages = pages;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
