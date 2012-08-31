package com.nncloudtv.lib;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.nncloudtv.model.Pdr;

public final class PMF {

	//non-asia	
	private static final PersistenceManagerFactory pmfInstanceNnUser1 = 
		JDOHelper.getPersistenceManagerFactory("datanucleus_nnuser1.properties");
	//asia
	private static final PersistenceManagerFactory pmfInstanceNnUser2 = 
		JDOHelper.getPersistenceManagerFactory("datanucleus_nnuser2.properties");
	//others
	private static final PersistenceManagerFactory pmfInstanceContent = 
		JDOHelper.getPersistenceManagerFactory("datanucleus_content.properties");		
	private static final PersistenceManagerFactory pmfInstanceAnalytics = 
		JDOHelper.getPersistenceManagerFactory("datanucleus_analytics.properties");		                                            
	private static final PersistenceManagerFactory pmfInstanceRecommend = 
		JDOHelper.getPersistenceManagerFactory("datanucleus_recommend.properties");		                                            
	
	private PMF() {}
	
	public static PersistenceManagerFactory get(@SuppressWarnings("rawtypes") Class c) {
		if (c.equals(Pdr.class)) {
			return PMF.getAnalytics();
		}
		//!!! if NnUser, Subscription, SubscriptionSet, throw exception
		return PMF.getContent();
	}
	
	public static PersistenceManagerFactory getNnUser1() {
		return pmfInstanceNnUser1;
	}

	public static PersistenceManagerFactory getNnUser2() {
		return pmfInstanceNnUser2;
	}

	public static PersistenceManagerFactory getContent() {
		return pmfInstanceContent;
	}
	
	public static PersistenceManagerFactory getAnalytics() {
		return pmfInstanceAnalytics;
	}

	public static PersistenceManagerFactory getRecommend() {
		return pmfInstanceRecommend;
	}
	
}
