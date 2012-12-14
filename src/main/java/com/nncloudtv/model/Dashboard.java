package com.nncloudtv.model;

import java.io.Serializable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="dashboard", detachable="true")
public class Dashboard implements Serializable {
        
    private static final long serialVersionUID = 6684875835085885553L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String name;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String stackName;
    
    @Persistent
    private short timeStart;

    @Persistent
    private short timeEnd;
    
    @Persistent
    private short opened;

    @Persistent
    private short type;
    
    @Persistent
    private short seq;
        
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public short getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(short timeStart) {
        this.timeStart = timeStart;
    }

    public short getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(short timeEnd) {
        this.timeEnd = timeEnd;
    }

    public short getOpened() {
        return opened;
    }

    public void setOpened(short opened) {
        this.opened = opened;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public String getStackName() {
        return stackName;
    }

    public void setStackName(String stackName) {
        this.stackName = stackName;
    }
        
}
