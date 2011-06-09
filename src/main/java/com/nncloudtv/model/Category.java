package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/** 
 * Category is created by MSO. Each channel belongs to at least a category. 
 */
@PersistenceCapable(table="category", detachable="true")
public class Category implements Serializable {

	private static final long serialVersionUID = -3641562781732198821L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	@Column(name="parent_id")
	private long parentId; 
	
	@Persistent
	@Column(name="mso_id")
	private long msoId;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String name;
	
	@Persistent
	@Column(name="is_public")
	private boolean isPublic;
	
	@Persistent
	@Column(name="channel_count")
	private int channelCount;
	
	@Persistent
	@Column(name="create_date")
	private Date createDate;
	
	@Persistent
	@Column(name="update_date")
	private Date updateDate;
	
	public Category(String name, boolean isPublic, long msoId) {
		this.name = name;
		this.isPublic = isPublic;
		this.msoId= msoId;
	}	
	
	public Category() {}	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMsoId() {
		return msoId;
	}

	public void setMsoId(long msoId) {
		this.msoId = msoId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
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
	
	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("name: " + name + ";");
        buffer.append("msoId: " + msoId + ";");
        buffer.append("channelCount: " + channelCount + ";");
        return buffer.toString();
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public long getParentId() {
		return parentId;
	}
	
}
