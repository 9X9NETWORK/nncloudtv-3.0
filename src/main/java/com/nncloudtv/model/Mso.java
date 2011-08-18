package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.*;

@PersistenceCapable(table="mso", detachable="true")
public class Mso implements Serializable {

	private static final long serialVersionUID = 352047930355952392L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
		
	@Persistent 
	@Column(jdbcType="VARCHAR", length=255)
	private String name;
	public static String NAME_9X9 = "9x9";
	public static String NAME_5F = "5f";

	@Persistent
	@Column(jdbcType="VARCHAR", length=255)	
	private String title;
		
	@Persistent 
	@Column(jdbcType="VARCHAR", length=255)
	private String intro;
		
	@Persistent
	@Column(name="logo_url", jdbcType="VARCHAR", length=255) 	
	private String logoUrl;
		
	@Persistent
	@Column(name="jingle_url", jdbcType="VARCHAR", length=255)
	private String jingleUrl;
	
	@Persistent
	@Column(name="contact_email", jdbcType="VARCHAR", length=255)
	private String contactEmail;
	
	@Persistent
	private short type;
	public static final short TYPE_NN = 1; //default mso, must have and must have ONLY one
	public static final short TYPE_MSO = 2;
		
	@Persistent
	@Column(name="lang_code", jdbcType="VARCHAR", length=4)
	public String langCode;
		
	@Persistent
	private int shard; //what shard it goes to, mainly for user partition
	                   //currently user partition is determined mainly by ip since we keep one mso for now.
	                   //default is 1, asia tw, cn, hk is 2
	@Persistent
	@Column(name="create_date")
	private Date createDate;
	
	@Persistent
	@Column(name="update_date")
	private Date updateDate;

	public Mso(String name, String intro, String contactEmail, short type) {
		this.name = name;
		this.intro = intro;
		this.contactEmail = contactEmail;
		this.type = type;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getJingleUrl() {
		return jingleUrl;
	}

	public void setJingleUrl(String jingleUrl) {
		this.jingleUrl = jingleUrl;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public int getShard() {
		return shard;
	}

	public void setShard(int shard) {
		this.shard = shard;
	}	
}
