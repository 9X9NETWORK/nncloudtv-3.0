package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.servlet.http.HttpServletRequest;

import com.nncloudtv.service.NnUserManager;

@PersistenceCapable(table="nnuser_profile", detachable="true")
public class NnUserProfile implements Serializable {
    private static final long serialVersionUID = -3477922988272107801L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
        
    @Persistent
    private long userId;

    @Persistent
    private long msoId; //which mso a user belongs to
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String name;    
    
    @Persistent
    private String dob; //for now it's year
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String intro;
            
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String imageUrl;
    public static String IMAGE_URL_DEFAULT = "https://s3.amazonaws.com/9x9ui/war/v2/images/profile_default101.png";

    @Persistent
    @Column(jdbcType="VARCHAR", length=5)
    private String sphere; //content region, used with LangTable

    @Persistent
    @Column(jdbcType="VARCHAR", length=5)
    private String lang; //ui language, used with LangTable

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String profileUrl; //curator url
    
    @Persistent
    private short gender; //0 (f) or 1(m) or 2(not specified)

    @Persistent
    private boolean featured; 
    
    @Persistent
    private int cntSubscribe; //the number of channels the user subscribes
    
    @Persistent
    private int cntChannel; //the number of channels the user creates
    
    @Persistent
    private int cntFollower; //the number of users who subscribe to this user's channels
    
    @Persistent
    private Date createDate;
    
    @Persistent
    private Date updateDate;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=6)
    private String priv; // indicate pcs read write delete and ccs read write delete

    public long getMsoId() {
        return msoId;
    }

    public void setMsoId(long msoId) {
        this.msoId = msoId;
    }

    public String getImageUrl() {
        if (imageUrl == null)
            return NnUserProfile.IMAGE_URL_DEFAULT;
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public NnUserProfile() {       
    }
    
    public NnUserProfile(HttpServletRequest req) {
        String sphere = NnUserManager.findLocaleByHttpRequest(req);
        this.sphere = sphere;
        this.lang = sphere;
        this.updateDate = new Date();
    }

    public NnUserProfile(long userId, long msoId) {
        this.userId = userId;
        this.msoId = msoId;
        Date now = new Date();
        this.createDate = now;
        this.updateDate = now;
    }
        
    public NnUserProfile(long msoId, String name, String sphere, String lang, String dob) {
        this.name = name;
        this.msoId = msoId;
        if (sphere != null && sphere.length() > 0)
            this.sphere = sphere;
        if (lang != null && lang.length() > 0)
            this.lang = lang;
        if (dob != null && dob.length() > 0)
            this.dob = dob;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSphere() {
        return sphere;
    }

    public void setSphere(String sphere) {
        if (sphere != null && sphere.contains("_"))
            sphere = sphere.substring(0, 2);
        this.sphere = sphere;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        if (lang != null & lang.length() > 2)
            lang = lang.substring(0, 2);
        this.lang = lang;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public short getGender() {
        return gender;
    }

    public void setGender(short gender) {
        this.gender = gender;
    }
    
    public void setGender(String gender) {
        if (gender == null || gender.length() == 0)
            this.gender = 2;
        else if (gender.startsWith("f"))
            this.gender = 0;
        else
            this.gender = 1;
    }    

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
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

    public String getBrandUrl() {
        if (profileUrl != null && profileUrl.matches("[a-zA-Z].+")) {
            return "~" + profileUrl;
        }
        return profileUrl;
    }

    public String getPriv() {
        return priv;
    }

    public void setPriv(String priv) {
        this.priv = priv;
    }

}
