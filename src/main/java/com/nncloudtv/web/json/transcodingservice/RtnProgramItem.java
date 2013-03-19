package com.nncloudtv.web.json.transcodingservice;

/**
 * Part of RtnProgram.
 * Receive Program information from Transcoding Service 
 */
public class RtnProgramItem {
    private String itemId;
    private String title;
    private String description;    
    private String pubDate;
    private String webm;
    private String mp4;
    private String other;
    private String audio;
    private String duration;
    private String thumbnail;
    private String thumbnailLarge;
    private String type;
    private String sortId;
    private String subSortId;
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getPubDate() {
        return pubDate;
    }
    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getWebm() {
        return webm;
    }
    public void setWebm(String webm) {
        this.webm = webm;
    }
    public String getMp4() {
        return mp4;
    }
    public void setMp4(String mp4) {
        this.mp4 = mp4;
    }
    public String getOther() {
        return other;
    }
    public void setOther(String other) {
        this.other = other;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getAudio() {
        return audio;
    }
    public void setAudio(String audio) {
        this.audio = audio;
    }
    public String getThumbnailLarge() {
        return thumbnailLarge;
    }
    public void setThumbnailLarge(String thumbnailLarge) {
        this.thumbnailLarge = thumbnailLarge;
    }    
    public String getSortId() {
        return sortId;
    }
    public void setSortId(String sortId) {
        this.sortId = sortId;
    }    
    public String getSubSortId() {
        return subSortId;
    }
    public void setSubSortId(String subSortId) {
        this.subSortId = subSortId;
    }
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("itemId: " + itemId + ";");
        buffer.append("pubDate: " + pubDate + ";");
        return buffer.toString();
    } 
    
}