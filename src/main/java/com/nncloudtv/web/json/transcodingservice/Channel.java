package com.nncloudtv.web.json.transcodingservice;

import java.io.Serializable;

public class Channel implements Serializable {
    private static final long serialVersionUID = -626426967884149637L;
    
    private String channelId;
    private String url;
    private String forceEncoding;
    private String lastUpdateTime;
    private String subscriptionCnt;

    public Channel(String channelId, String url, String lastUpdateTime, String forceEncoding, String subscriptionCnt) {
        this.channelId = channelId;
        this.url = url;
        this.lastUpdateTime = lastUpdateTime;
        this.forceEncoding = forceEncoding;
        this.subscriptionCnt = subscriptionCnt;
    }
    
    public String getChannelId() {
        return channelId;
    }
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }
    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getForceEncoding() {
        return forceEncoding;
    }

    public void setForceEncoding(String forceEncoding) {
        this.forceEncoding = forceEncoding;
    }

    public String getSubscriptionCnt() {
        return subscriptionCnt;
    }

    public void setSubscriptionCnt(String subscriptionCnt) {
        this.subscriptionCnt = subscriptionCnt;
    }    
}
