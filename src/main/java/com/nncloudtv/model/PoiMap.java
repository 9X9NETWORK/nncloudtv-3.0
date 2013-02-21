package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="poi_map", detachable = "true")
public class PoiMap implements Serializable {

    private static final long serialVersionUID = -605406672071670966L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
    
    @Persistent
    private long poiId;
    
    @Persistent
    private long eventlId;
        
    @Persistent
    private Date updateDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPoiId() {
        return poiId;
    }

    public void setPoiId(long poiId) {
        this.poiId = poiId;
    }

    public long getEventlId() {
        return eventlId;
    }

    public void setEventlId(long eventlId) {
        this.eventlId = eventlId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

}
