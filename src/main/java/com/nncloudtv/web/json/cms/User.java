package com.nncloudtv.web.json.cms;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    
    /**
     * eclipse generated
     */
    private static final long serialVersionUID = 2819895395719371672L;

    private long id;
    
    private Date createDate;
    
    private Date updateDate;
    
    private String userEmail;
    
    private boolean fbUser;
    
    private String name;
    
    private String intro;
    
    private String imageUrl;
    
    private String lang;
    
    private String profileUrl;
    
    private short shard;
    
    private String sphere;
    
    private short type;
    
    private int cntSubscribe;
    
    private int cntChannel;
    
    private int cntFollower;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isFbUser() {
        return fbUser;
    }

    public void setFbUser(boolean fbUser) {
        this.fbUser = fbUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public short getShard() {
        return shard;
    }

    public void setShard(short shard) {
        this.shard = shard;
    }

    public String getSphere() {
        return sphere;
    }

    public void setSphere(String sphere) {
        this.sphere = sphere;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public int getCntSubscribe() {
        return cntSubscribe;
    }

    public void setCntSubscribe(int cntSubscribe) {
        this.cntSubscribe = cntSubscribe;
    }

    public int getCntChannel() {
        return cntChannel;
    }

    public void setCntChannel(int cntChannel) {
        this.cntChannel = cntChannel;
    }

    public int getCntFollower() {
        return cntFollower;
    }

    public void setCntFollower(int cntFollower) {
        this.cntFollower = cntFollower;
    }

}
