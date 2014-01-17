package com.nncloudtv.model;

import java.io.Serializable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="app", detachable="true")
public class App  implements Serializable {
	
    private static final long serialVersionUID = -1574784862238151019L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

   @Persistent
    private long msoId; //maybe different mso wants different
	
	@Persistent
    public short type;
    public static final short TYPE_IOS = 1;
    public static final short TYPE_ANDROID = 2;

    @Persistent
    @Column(jdbcType="VARCHAR", length=500)
	private String name;
	
    @Persistent
    @Column(jdbcType="VARCHAR", length=500)
	private String intro;

    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String imageUrl; 

	@Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String storeUrl;
	
    @Persistent
    @Column(jdbcType="VARCHAR", length=5)
	private String sphere;
	
    @Persistent    
	private boolean featured;    

    @Persistent
	private int position1; //featured position. to begin with, use position 1 only. 
		
    @Persistent
    private int position2; //general list position

	public long getMsoId() {
		return msoId;
	}

	public void setMsoId(long msoId) {
		this.msoId = msoId;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getStoreUrl() {
		return storeUrl;
	}

	public void setStoreUrl(String storeUrl) {
		this.storeUrl = storeUrl;
	}

	public String getSphere() {
		return sphere;
	}

	public void setSphere(String sphere) {
		this.sphere = sphere;
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

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public int getPosition1() {
		return position1;
	}

	public void setPosition1(int position1) {
		this.position1 = position1;
	}

	public int getPosition2() {
		return position2;
	}

	public void setPosition2(int position2) {
		this.position2 = position2;
	}
	    
}
