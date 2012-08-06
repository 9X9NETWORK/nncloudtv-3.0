package com.nncloudtv.model;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;

import com.nncloudtv.lib.NnNetUtil;

@PersistenceCapable(table="user_invite", detachable="true")
public class UserInvite {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long id;

	@Persistent
	private short shard;
	
	@Persistent
	private long userId;

	@Persistent
	private long inviteeId;
	
	@Persistent
	private long channelId;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String inviteeEmail;		
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String inviteeName;
	
	@Persistent
	@Column(jdbcType="VARCHAR", length=255)
	private String inviteToken;

	@Persistent
	private short status;
	public static short STATUS_REJECTED = 0;
	public static short STATUS_ACCEPTED = 1;
	public static short STATUS_PENDING = 2;
	
	@Persistent
	private Date createDate;

	@Persistent
	private Date updateDate;
	
	public UserInvite(short shard, long userId, 
			          String inviteToken, long channelId,
			          String inviteeEmail, String inviteeName) {
		this.shard = shard;
		this.userId = userId;
		this.inviteToken = inviteToken;
		this.channelId = channelId;
		this.inviteeEmail = inviteeEmail;
		this.inviteeName = inviteeName;
		this.status = UserInvite.STATUS_PENDING;
		Date now = new Date();		
		this.createDate = now;
		this.updateDate = now;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public short getShard() {
		return shard;
	}

	public void setShard(short shard) {
		this.shard = shard;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getInviteToken() {
		return inviteToken;
	}

	public void setInviteToken(String inviteToken) {
		this.inviteToken = inviteToken;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}		

	public static String generateToken() {
		String time = String.valueOf(new Date().getTime());
		String random = RandomStringUtils.randomAlphabetic(10);
		String result = time + random;
		result = RandomStringUtils.random(15, 0, 15, true, true, result.toCharArray());
		return result;
	}
	
	public static String getInviteContent(NnUser user, String token, String toName, String fromName, HttpServletRequest req) {
		String content = "Hello " + toName + "\n\n";
		String urlRoot = NnNetUtil.getUrlRoot(req);
		content += user.getName() + " invited to watch his/her channel.\n";
		content += "Click link " + urlRoot + "/share/invite/" + token + " to join " + user.getName();
		return content;
	}
	
	public static String getInviteSubject() {
		return "You've been invited to join 9x9";
	}

	public static String getNotifySubject(String channelName) {
		return "New things to watch";
		//return "The channel " + channelName + " has new updates";
	}

	public static String getNotifyContent(String channelName) {
		return "The channel " + channelName + " has new updates. Check it out on your flipr.";
	}
	
	public long getChannelId() {
		return channelId;
	}

	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}

	public String getInviteeEmail() {
		return inviteeEmail;
	}

	public void setInviteeEmail(String inviteeEmail) {
		this.inviteeEmail = inviteeEmail;
	}

	public String getInviteeName() {
		return inviteeName;
	}

	public void setInviteeName(String inviteeName) {
		this.inviteeName = inviteeName;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public long getInviteeId() {
		return inviteeId;
	}

	public void setInviteeId(long inviteeId) {
		this.inviteeId = inviteeId;
	}	
}
