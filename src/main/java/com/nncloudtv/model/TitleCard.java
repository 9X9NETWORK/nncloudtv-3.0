package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * sub-episode's title card 
 */
@PersistenceCapable(table="title_card", detachable = "true")
public class TitleCard implements Serializable {    
    
    private static final long serialVersionUID = 1276804191557423541L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
    
    @Persistent
    private long channelId;

    @Persistent
    @Column(jdbcType="VARCHAR", length=8)    
    private String seq; //from nnprogram: channelId + seq, example: 4300000004 (43: channelId, seq: 00000004)    

    @Persistent
    @Column(jdbcType="VARCHAR", length=8)
    private String subSeq; //subSeq should correspond with nnprogram's subSeq

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String duration;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String message;

    @Persistent
    @Column(jdbcType="VARCHAR", length=20)
    private String size; // font size

    @Persistent
    @Column(jdbcType="VARCHAR", length=20)
    private String color; // font color

    @Persistent
    @Column(jdbcType="VARCHAR", length=20)
    private String effect;

    @Persistent
    @Column(jdbcType="VARCHAR", length=20)
    private String align;

    @Persistent
    @Column(jdbcType="VARCHAR", length=20)
    private String bgColor;

    @Persistent
    @Column(jdbcType="VARCHAR", length=20)
    private String style;

    @Persistent
    private short type; //0 begin, 1 end    
    public static short TYPE_BEGIN = 0;
    public static short TYPE_END = 1;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String bgImage;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=500)
    private String playerSyntax;

    @Persistent
    private Date updateDate;
    
    public TitleCard(long channelId, int seq, int subSeq, short type) {
        this.channelId = channelId;
        this.setSeq(seq);
        this.setSubSeq(subSeq);
        this.type = type;
        Date now = new Date();        
        this.updateDate = now;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public String getPlayerSyntax() {
        return playerSyntax;
    }

    public void setPlayerSyntax(String playerSyntax) {
        this.playerSyntax = playerSyntax;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
    
    public void setSeq(int seq) {
        this.seq = String.format("%08d", seq);
    }

    public String getSubSeq() {
        return subSeq;
    }

    public void setSubSeq(String subSeq) {
        this.subSeq = subSeq;
    }
    
    public void setSubSeq(int subSeq) {
        this.subSeq = String.format("%08d", subSeq);
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

}
