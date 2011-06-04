//http://www.datanucleus.org/products/accessplatform_1_0/guides/eclipse/index.html
//http://www.datanucleus.org/products/accessplatform/guides/eclipse/index.html#preferences_enhancer
package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Pdr raw information, for future use
 */
@PersistenceCapable(table="pdr_raw", detachable="true")
public class PdrRaw implements Serializable {	
	private static final long serialVersionUID = 7348543186240756490L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	@Column(name="user_id")
	private long userId;
	
	@Persistent
	@Column(name="session_id", jdbcType="VARCHAR", length=255)
	private String sessionId;

	@Persistent
	@Column(name="time_delta")
	private long timeDelta;
	
	@Column(name="verb", jdbcType="VARCHAR", length=255)
	@Persistent
	private String verb;
	public static String VERB_WATCH = "w";
	
	@Persistent
	@Column(name="info", jdbcType="VARCHAR", length=255)
	private String info;

	@Persistent
	@Column(name="create_date")
	private Date createDate;
	
	@Persistent
	@Column(name="update_date")
	private Date updateDate;
	
	public PdrRaw (long userId, String sessionId, long timeDelta, String verb, String info) {
		this.sessionId = sessionId;
		this.timeDelta = timeDelta;
		this.userId = userId;
		this.verb = verb;
		this.info = info;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
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

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public long getTimeDelta() {
		return timeDelta;
	}

	public void setTimeDelta(long timeDelta) {
		this.timeDelta = timeDelta;
	}
		
}
