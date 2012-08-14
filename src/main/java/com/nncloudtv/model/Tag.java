package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.*;

/**
 * tag
 */
@PersistenceCapable(table="tag", detachable="true")
public class Tag implements Serializable {
	private static final long serialVersionUID = 2748807403878352371L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
		
	@Persistent 
	@Column(jdbcType="VARCHAR", length=255)
	private String name;

	
	@Persistent
	private Date updateDate;

	public Tag(String name) {
		this.name = name;
		this.updateDate = new Date();
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}
