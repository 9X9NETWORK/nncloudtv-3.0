package com.nncloudtv.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import javax.jdo.annotations.*;

import com.nncloudtv.lib.AuthLib;

/**
 * 9x9 User accounts
 */
@PersistenceCapable(table="nnuser", detachable="true")
public class NnUser implements Serializable {
        
    private static final long serialVersionUID = -708171304411630395L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;
        
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String token;  //each user has a unique token, could be a access_token from fb as well
    
    @Persistent
    private short shard; //which shard a user belongs to
    public static short SHARD_DEFAULT = 1;
    public static short SHARD_CHINESE = 2;
        
    @Persistent
    private long msoId; //which mso a user belongs to
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String email; //unique key
    
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
    public static String IMAGE_URL_DEFAULT = "http://www3.ul.ie/llcc/cals/english/nopic.jpg"; //TEMP

    @NotPersistent
    private String password;
    
    @Persistent
    private byte[] cryptedPassword;

    @Persistent
    private byte[] salt;
    
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
    private Date createDate;
    
    @Persistent
    private Date updateDate;
    
    @Persistent
    private short type; //user's type
    public static short TYPE_ADMIN = 1;        // Administrator (abandoned)
    public static short TYPE_TBC = 2;          // a.k.a. Target Brand-name Customer (abandoned)
    public static short TYPE_TCO = 3;          // a.k.a. Target Content Owner (abandoned)
    public static short TYPE_USER = 4;         
    public static short TYPE_NN = 5;           // default user, must have and only one
    public static short TYPE_3X3 = 6;          // Taiwan partner
    public static short TYPE_ENTERPRISE = 7;   // U.S. partner
    public static short TYPE_FAKE_YOUTUBE = 8; //create fake account based on youtube users
    
    public static String GUEST_EMAIL = "guest@9x9.com";
    public static String GUEST_NAME = "Guest";
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String fbId;

    @Persistent
    private long expires;
    
    @Persistent
    private boolean featured; 
    
    //used for testing without changing the program logic
    //isTemp set to true means it can be wiped out
    @Persistent
    private boolean isTemp; 

    @Persistent
    private int cntSubscribe;
    
    @Persistent
    private int cntChannel;
    
    @Persistent
    private int cntFollower;
    
    public NnUser(String email, String password, String name, short type) {
        this.email = email;
        this.salt = AuthLib.generateSalt();
        this.cryptedPassword= AuthLib.encryptPassword(password, this.getSalt());
        this.name = name;
        this.type = type;
    }

    //for facebook
    public NnUser(String email, String name, String fbId, String fbToken) {
        this.email = email;
        this.name = name;
        this.fbId = fbId;
        this.token = fbToken;
    }
    
    public NnUser(String email, String password, String name, short type, long msoId) {
        this.email = email;
        this.salt = AuthLib.generateSalt();
        this.cryptedPassword= AuthLib.encryptPassword(password, this.getSalt());
        this.name = name;
        this.type = type;
        this.msoId = msoId;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdStr() {
        return shard + "-" + this.getId();
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getCryptedPassword() {
        return cryptedPassword;
    }

    public void setCryptedPassword(byte[] cryptedPassword) {
        this.cryptedPassword = cryptedPassword;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getImageUrl() {
        if (imageUrl == null)
            return NnUser.IMAGE_URL_DEFAULT;
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getMsoId() {
        return msoId;
    }

    public void setMsoId(long msoId) {
        this.msoId = msoId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public short getShard() {
        return shard;
    }

    public void setShard(short shard) {
        this.shard = shard;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSphere() {
        return sphere;
    }

    public void setSphere(String sphere) {
        if (sphere != null && sphere.contains("_"))
            sphere = sphere.substring(0, 2);
        this.sphere = sphere;
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

    public void setProfileUrl() {
        Random r = new Random();
        int num = r.nextInt(99999);
        String formatted = String.format("%05d", num);                
        if (name != null)
            this.profileUrl = formatted + "-" + name;
        else
            this.profileUrl = formatted;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean isTemp) {
        this.isTemp = isTemp;
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

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }
    
}
