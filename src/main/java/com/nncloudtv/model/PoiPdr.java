package com.nncloudtv.model;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="poi_pdr", detachable="true")
public class PoiPdr {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
    
    @Persistent
    private long userId; //if a device has associated user account, not always

    @Persistent
    private long msoId;

    @Persistent
    private long eventId; //if a device has associated user account, not always

    @Persistent
    private long poiId; //if a device has associated user account, not always
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=200)
    private String select;
    
    @Persistent
    private Date updateDate;

    @Persistent
    private Date scheduledDate;
    
    public PoiPdr(long userId, long msoId, long poiId, long eventId, String select) {
        this.userId = userId;
        this.msoId = msoId;
        this.poiId = poiId;
        this.eventId = eventId;
        this.select = select;
    }
    
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

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


    public long getMsoId() {
        return msoId;
    }

    public void setMsoId(long msoId) {
        this.msoId = msoId;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getPoiId() {
        return poiId;
    }

    public void setPoiId(long poiId) {
        this.poiId = poiId;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
}
