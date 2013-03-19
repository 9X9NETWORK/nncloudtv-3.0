package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "nnuser_library", detachable = "true")
public class NnUserLibrary implements Serializable {
    private static final long serialVersionUID = -5963982247560373939L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent
    @Column(jdbcType = "VARCHAR", length = 25)
    private String userIdStr; // format: shard-userId, example: 1-1

    @Persistent
    @Column(jdbcType = "VARCHAR", length = 255)
    private String name;

    @Persistent
    @Column(jdbcType = "VARCHAR", length = 244)
    private String fileUrl;

    @Persistent
    @Column(jdbcType = "VARCHAR", length = 255)
    private String imageUrl;

    @Persistent
    private short type;
    public static final short TYPE_UPLOADS = 1;
    public static final short TYPE_YOUTUBE = 2;

    @Persistent
    private short status; // correspond to NnProgram status

    @Persistent
    private Date updateDate;

    public NnUserLibrary(String name, String fileUrl, short type) {
        this.name = name;
        this.fileUrl = fileUrl;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserIdStr() {
        return userIdStr;
    }
    
    public long getUserId() {
        return getUserId(userIdStr);
    }
    
    public long getUserId(String userId) {
        if (userId == null)
            return 0;
        String[] splits = userId.split("-");
        if (splits.length > 1)
            return Short.parseShort(splits[1]);
        else
            return Short.parseShort(userId);        
    }

    public void setUserIdStr(String userIdStr) {
        this.userIdStr = userIdStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }
}