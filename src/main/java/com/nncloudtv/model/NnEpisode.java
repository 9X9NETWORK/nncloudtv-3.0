package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.service.CounterFactory;

@PersistenceCapable(table="nnepisode", detachable="true")
public class NnEpisode implements Serializable {
    private static final long serialVersionUID = -2365225197711392350L;

    protected static final Logger log = Logger.getLogger(NnEpisode.class.getName());
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    @Persistent
    private long channelId;   
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String name;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String imageUrl;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String intro;

    @Persistent
    private long adId;   

    @Persistent
    private boolean isPublic; 
    
    @Persistent
    private Date scheduleDate;
    
    @Persistent
    private Date publishDate;
        
    @Persistent
    private Date updateDate;
    
    @NotPersistent
    private int cntView;
    
    @NotPersistent
    private String playbackUrl;
    
    @Persistent
    private int duration;
    
    @Persistent
    private int seq;
    
    public NnEpisode(long channelId) {
        this.channelId = channelId;
        Date now = new Date();
        this.updateDate = now;
        this.publishDate = now;
    } 
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public long getAdId() {
        return adId;
    }

    public void setAdId(long adId) {
        this.adId = adId;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
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

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

    public int getCntView() {    
        try {
            //v_ch10514_e21688
            String name = "v_ch" + channelId + "_e" + id;        
            String result = (String)CacheFactory.get(name);
            if (result != null) {
                return Integer.parseInt(result);
            }
            log.info("cnt view not in the cache:" + name);
            CounterFactory factory = new CounterFactory();
            cntView = factory.getCount(name);
            if (CacheFactory.isRunning)
                CacheFactory.set(name, String.valueOf(cntView));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;            
        }
        return cntView;
    }

    /*
    public void setCntView(int cntView) {    
        this.cntView = cntView;
    }
    */

    public int getDuration() {
    
        return duration;
    }

    public void setDuration(int duration) {
    
        this.duration = duration;
    }

    public String getPlaybackUrl() {
    
        return playbackUrl;
    }

    public void setPlaybackUrl(String playbackUrl) {
    
        this.playbackUrl = playbackUrl;
    }

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
     
}
