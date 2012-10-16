package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;
import javax.jdo.annotations.*;

import com.nncloudtv.lib.NnStringUtil;

/**
 * Programs under a NnChannel.
 * 
 * Terminology: 
 * Program: aka NnProgram. where video file is stored.
 * Episode: aka NnEpisode. Only 9x9 programs has "episode". It is "super-program", store each sub-episode's metadata.    
 */
@PersistenceCapable(table="nnprogram", detachable="true")
public class NnProgram implements Serializable {
    private static final long serialVersionUID = 5553891672235566066L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
    
    @Persistent
    private long channelId;

    @Persistent
    private long episodeId;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String name;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=500)
    private String comment;
    
    @Persistent
    private short contentType;
    public static final short CONTENTTYPE_DIRECTLINK = 0;
    public static final short CONTENTTYPE_YOUTUBE = 1;
    public static final short CONTENTTYPE_SCRIPT = 2;
    public static final short CONTENTTYPE_RADIO = 3;
    public static final short CONTENTTYPE_REFERENCE = 4;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String intro;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String imageUrl;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String fileUrl;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String audioFileUrl;
    
    /**
     * used in 2 places:
     * 1. from channel parsing service, it's where the physical file stores, to avoid duplication.
     * 2. for "favorite" feature, 
     *    used when the program type is "reference". in this case, the original programId will be stored
     *    if a file url is stored, the original channel id is stored.  
     */
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String storageId;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String errorCode;

    @Persistent
    private short status;
    //general
    public static short STATUS_OK = 0;
    public static short STATUS_ERROR = 1;
    public static short STATUS_NEEDS_REVIEWED = 2;
    //quality
    public static short STATUS_BAD_QUALITY = 101;    
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String duration;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String startTime;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String endTime;
    
    @Persistent
    private boolean isPublic; 

    //used by maplestage channels, 9x9 channels, youtube special sorting channels
    //please not it is a string instead of digit, make 1 00000001, 8 digits total 
    @Persistent
    @Column(jdbcType="VARCHAR", length=8)
    private String seq;

    //used with seq
    @Persistent
    @Column(jdbcType="VARCHAR", length=8)    
    private String subSeq;
    
    @Persistent
    private Date createDate;
        
    @Persistent
    private Date updateDate;
    
    @Persistent
    private Date publishDate;

    @NotPersistent
    private int cntView;
    
    public NnProgram(long channelId, String name, String intro, String imageUrl) {    
        this.channelId = channelId;
        this.name = name;
        this.intro = intro;
        this.imageUrl = imageUrl;
        Date now = new Date();
        this.createDate = now;
        this.updateDate = now;
        this.publishDate = now;
        this.isPublic = true;
        this.status = NnProgram.STATUS_OK;
    }
    
    public NnProgram(long channelId, long episodeId, String name, String intro,
            String imageUrl) {
    
        this.channelId = channelId;
        this.episodeId = episodeId;
        this.name = name;
        this.intro = intro;
        this.imageUrl = imageUrl;
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

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        if (name != null)
            name = NnStringUtil.revertHtml(name);
        return name;
    }

    public String getPlayerName() {
    	String name = this.getName(); 
        if (name != null) {        	
           name = name.replace("|", "\\|");
       	   name = name.replaceAll("\\s", " ");
        }
        return name;    	
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getIntro() {
        if (intro != null)
            intro = NnStringUtil.revertHtml(intro);
        return intro;
    }
    
    public String getPlayerIntro() {
    	String intro = this.getIntro(); 
        if (intro != null) {
            int len = (intro.length() > 256 ? 256 : intro.length()); 
        	intro = intro.replaceAll("\\s", " ");                
        	intro = intro.substring(0, len);           
        }
        return name;    	
    	
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public boolean isPublic() {
        return isPublic;
    }
    
    public boolean getIsPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public int getDurationInt() {
        
        if (this.duration == null) {
            return 0;
        }
        
        int duration = 0;
        try {
            duration = Integer.valueOf(this.duration);
        } catch (NumberFormatException e) {
        }
        return duration;
    }
    
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDuration(short duration) {
        this.duration = String.valueOf(duration);
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {        
        this.status = status;
    }

    public String getStorageId() {
        return storageId;
    }

    //used in favorite program, to reference the real 9x9 program (maplestage, youtube channel do not apply here)
    /*
    public String getReferenceStorageId() {
        return this.getChannelId() + ";" + "00000000";
    }

    //compatibility with old scheme. 
    public String getReferenceStorageIdOldScheme() {
        return this.getChannelId() + ";" + "00000001";        
    }
    */
    
    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getAudioFileUrl() {
        return audioFileUrl;
    }

    public void setAudioFileUrl(String audioFileUrl) {
        this.audioFileUrl = audioFileUrl;
    }

    public String getComment() {
        if (comment != null)
            comment = NnStringUtil.revertHtml(comment);
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public short getContentType() {
        return contentType;
    }

    public void setContentType(short contentType) {
        this.contentType = contentType;
    }
    
    public int getSeqInt() {
        
        if (this.seq == null) {
            return 0;
        }
        
        short seq = 0;
        try {
            seq = Short.valueOf(this.seq);
        } catch (NumberFormatException e) {
        }
        return seq;
    }
    
    public String getSeq() {
        return seq;
    }
    
    public void setSeq(int seq) {
        this.seq = NnStringUtil.seqToStr(seq);
    }
    
    public void setSeq(String seq) {
        this.seq = seq;
    }
    
    public int getSubSeqInt() {
        
        if (this.subSeq == null) {
            return 0;
        }
        
        short subSeq = 0;
        try {
            subSeq = Short.valueOf(this.subSeq);
        } catch (NumberFormatException e) {
        }
        return subSeq;
    }
    
    public String getSubSeq() {
        return subSeq;
    }
    
    public void setSubSeq(int subSeq) {
        this.subSeq = NnStringUtil.seqToStr(subSeq);
    }
    
    public void setSubSeq(String subSeq) {
        this.subSeq = subSeq;
    }

    public void setStartTime(int startTime) {
        this.startTime = String.format("%d", startTime);
    }
    
    public void setEndTime(int endTime) {
        this.endTime = String.format("%d", endTime);
    }
    
    public String getStartTime() {
        return startTime;
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
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
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
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(long episodeId) {
        this.episodeId = episodeId;
    }

    public long getStorageIdInt() {
        
        if (storageId == null) {
            return 0;
        }
        
        Long id = null;
        try {
            id = Long.valueOf(storageId.replace("e", ""));
        } catch (NumberFormatException e) {
        }
        
        return (id == null) ? 0 : id;
    }

    public int getCntView() {
    
        return cntView;
    }
    
    public void setCntView(int cntView) {
        this.cntView = cntView;
    }
}
