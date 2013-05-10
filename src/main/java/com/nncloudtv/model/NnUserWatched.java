package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/** 
 * Store user's last watched program of each channel
 */
@PersistenceCapable(table="nnuser_watched", detachable="true")
public class NnUserWatched implements Serializable {

    private static final long serialVersionUID = 8903328950575923098L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
    
    @Persistent
    private long userId;

    @Persistent
    private long msoId;
    
    @Persistent
    private long channelId;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String program; //it can be a 9x9 program id or youtube program id (not number)
    
    @Persistent
    private Date updateDate;
    
    public NnUserWatched(NnUser user, long channelId, String program) {
        this.msoId = user.getMsoId();
        this.userId = user.getId();
        this.channelId = channelId;
        this.program = program;
    }
    
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

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
    
}
