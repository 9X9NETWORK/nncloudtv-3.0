package com.nncloudtv.web.json.cms;

import java.io.Serializable;
import java.util.Date;

public class UserFavorite implements Serializable {
    
    private static final long serialVersionUID = -6689668608352509726L;
    
    private long channelId;
    
    private String imageUrl;
    
    private String name;
    
    private int duration;
    
    private Date publishDate;
    
    private int cntView;
    
    private boolean isPublic;
    
    private String playbackUrl;

    public long getChannelId() {
    
        return channelId;
    }

    public void setChannelId(long channelId) {
    
        this.channelId = channelId;
    }

    public String getImageUrl() {
    
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
    
        this.imageUrl = imageUrl;
    }

    public String getName() {
    
        return name;
    }

    public void setName(String name) {
    
        this.name = name;
    }

    public int getDuration() {
    
        return duration;
    }

    public void setDuration(int duration) {
    
        this.duration = duration;
    }

    public Date getPublishDate() {
    
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
    
        this.publishDate = publishDate;
    }

    public int getCntView() {
    
        return cntView;
    }

    public void setCntView(int cntView) {
    
        this.cntView = cntView;
    }

    public boolean isPublic() {
    
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
    
        this.isPublic = isPublic;
    }

    public String getPlaybackUrl() {
    
        return playbackUrl;
    }

    public void setPlaybackUrl(String playbackUrl) {
    
        this.playbackUrl = playbackUrl;
    }
    
}
