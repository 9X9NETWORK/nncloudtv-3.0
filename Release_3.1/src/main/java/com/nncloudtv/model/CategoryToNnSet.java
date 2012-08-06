package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * To relate Category and Set
 */
@PersistenceCapable(table="category_to_nnset", detachable = "true")
public class CategoryToNnSet implements Serializable {

	private static final long serialVersionUID = -6253012826484625104L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	private long categoryId;
	
	@Persistent
	private long setId;
	
	@Persistent
	private Date createDate;
	
	@Persistent
	private Date updateDate;

	public CategoryToNnSet(long categoryId, long setId) {
		this.setId = setId;
		this.categoryId = categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSetId() {
		return setId;
	}

	public void setSetId(long setId) {
		this.setId = setId;
	}	
}
