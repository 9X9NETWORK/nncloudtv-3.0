package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * "title card" 
 */

@PersistenceCapable(table="nnprogram_card", detachable = "true")
public class TitleCard implements Serializable {	
	
	private static final long serialVersionUID = 1276804191557423541L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;
	
	@Persistent
	private long channelId;

	@Persistent
	private long programId;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String duration;
		
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String message;

	@Persistent
	@Column(jdbcType="VARCHAR", length=10)
	private String color;

	@Persistent
	@Column(jdbcType="VARCHAR", length=10)
	private String style;
	
	@Persistent
	private short type; //0 begin, 1 end

	@Persistent
	@Column(jdbcType="VARCHAR", length=500)
	private String playerSyntax;

	@Persistent
	private Date updateDate;
	
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getChannelId() {
		return channelId;
	}

	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}

	public long getProgramId() {
		return programId;
	}

	public void setProgramId(long programId) {
		this.programId = programId;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public String getPlayerSyntax() {
		return playerSyntax;
	}

	public void setPlayerSyntax(String playerSyntax) {
		this.playerSyntax = playerSyntax;
	}	
}
