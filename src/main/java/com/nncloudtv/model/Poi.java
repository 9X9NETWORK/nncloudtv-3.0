package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

//ad units
@PersistenceCapable(table="poi", detachable="true")
public class Poi implements Serializable {
    private static final long serialVersionUID = 7003434361311530790L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
    
    @Persistent
    private long programId;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String name;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String intro;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String startTime;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String endTime;

    @Persistent
    @Column(jdbcType="VARCHAR", length=500)
    private String tag;

    @Persistent
    private Date createDate;
        
    @Persistent
    private Date updateDate;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProgramId() {
        return programId;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public int getStartTimeInt() {
        
        if (startTime == null) {
            return 0;
        }
        
        int startTimeInt = 0;
        try {
            startTimeInt = Integer.valueOf(startTime);
        } catch (NumberFormatException e) {
        }
        return startTimeInt;
    }
    
    public void setStartTime(int startTime) {
        this.startTime = String.format("%d", startTime);
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public int getEndTimeInt() {
        
        if (endTime == null) {
            return 0;
        }
        
        int endTimeInt = 0;
        try {
            endTimeInt = Integer.valueOf(endTime);
        } catch (NumberFormatException e) {
        }
        return endTimeInt;
    }
    
    public void setEndTime(int endTime) {
        this.endTime = String.format("%d", endTime);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
    
}
