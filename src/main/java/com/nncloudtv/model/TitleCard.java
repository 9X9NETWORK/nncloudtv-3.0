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
    private long programId;
    
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
    @Column(jdbcType="VARCHAR", length=20)
    private String weight; // font weight

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
    
    public static final String DEFAULT_MESSAGE  = "My Video";
    public static final String DEFAULT_ALIGN    = "center";
    public static final String DEFAULT_EFFECT   = "none";
    public static final String DEFAULT_DURATION = "7";
    public static final String DEFAULT_SIZE     = "20";
    public static final String DEFAULT_COLOR    = "white";
    public static final String DEFAULT_STYLE    = "normal";
    public static final String DEFAULT_WEIGHT   = "normal";
    public static final String DEFAULT_BG_COLOR = "black";
    
    public TitleCard(long channelId, long programId, short type) {
        
        this.channelId = channelId;
        this.programId = programId;
        this.type = type;
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

    public long getProgramId() {
        return programId;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
    }
    
    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
    
}
