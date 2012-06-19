package com.nncloudtv.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.nncloudtv.dao.MsoConfigDao;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;

public class MsoConfigManager {
	
	private MsoConfigDao configDao = new MsoConfigDao();
	protected static final Logger log = Logger.getLogger(MsoConfigManager.class.getName());
	
	static public String getS3UploadBucket() {
		Properties properties = new Properties();
		String result = "";
		try {
			properties.load(MsoConfigManager.class.getClassLoader().getResourceAsStream("aws.properties"));
			result = properties.getProperty("s3_upload_bucket");
		} catch (IOException e) {
			NnLogUtil.logException(e);
		}
		return result;
	}
	
	static public String getPiwikDomain() {
		Properties properties = new Properties();
		String result = "";
		try {
			properties.load(MsoConfigManager.class.getClassLoader().getResourceAsStream("piwik.properties"));
			result = properties.getProperty("piwik_server");
		} catch (IOException e) {
			NnLogUtil.logException(e);
		}
		return result;
	}
	
	static public String getServerDomain() {
		Properties properties = new Properties();
		String result = "";
		try {
			properties.load(MsoConfigManager.class.getClassLoader().getResourceAsStream("sns.properties"));
			result = properties.getProperty("server_domain");
		} catch (IOException e) {
			NnLogUtil.logException(e);
		}
		return result;
	}
	
	static public String getExternalRootPath() {
		Properties properties = new Properties();
		String result = "";
		try {
			properties.load(MsoConfigManager.class.getClassLoader().getResourceAsStream("aws.properties"));
			result = properties.getProperty("static_file_root_path");
		} catch (IOException e) {
			NnLogUtil.logException(e);
		}
		return result;
	}
	
	public MsoConfig create(MsoConfig config) {
		Date now = new Date();
		config.setCreateDate(now);
		config.setUpdateDate(now);
		return configDao.save(config);
	}
	
	public MsoConfig save(Mso mso, MsoConfig config) {
		config.setUpdateDate(new Date());
		if (mso.getType() == Mso.TYPE_NN) {
			this.processCache(config);
		}
		return configDao.save(config);
	}

	public void processCache(MsoConfig config) {
		isInReadonlyMode(true);
		isQueueEnabled(true);
	}

	public static boolean getBooleanValueFromCache(String key, boolean cacheReset) {
		String cacheKey = "msoconfig(" + key + ")";
		String result = (String)CacheFactory.get(cacheKey);		
		if (!cacheReset && CacheFactory.isRunning) {
			if (result != null){
				log.info("value from cache: key=" + cacheKey + "value=" + result);			
				return NnStringUtil.stringToBool(result);
			}			
		}
		boolean value = false;
		MsoConfig config = new MsoConfigDao().findByItem(key);
		if (config != null) {
			if (CacheFactory.isRunning) {
				CacheFactory.set(cacheKey, config.getValue());
			}
			value = NnStringUtil.stringToBool(config.getValue());
		}
		return value;
	}
		
	public static boolean isInReadonlyMode(boolean cacheReset) {
		return MsoConfigManager.getBooleanValueFromCache(MsoConfig.RO, cacheReset);
	}
		
	public static boolean isQueueEnabled(boolean cacheReset) {
		boolean status = MsoConfigManager.getBooleanValueFromCache(MsoConfig.QUEUED, cacheReset);	 
		return status; 	
	}
	
	public List<MsoConfig> findByMso(Mso mso) {
		return configDao.findByMso(mso);
	}
			
	public MsoConfig findByMsoAndItem(Mso mso, String item) {
		return configDao.findByMsoAndItem(mso.getId(), item);
	}
	
	public MsoConfig findByItem(String item) {
		return configDao.findByItem(item);
	}

	/*
	public void processCache(MsoConfig config) {
		MemcachedClient cache = CacheFactory.get();
		String cacheKey = this.getCacheKey(config.getItem());
		if (cache != null) { 
			cache.set(cacheKey, CacheFactory.EXP_DEFAULT, config.getValue());
		}
	}
	
	//only 9x9 mso's config is cached
	public MsoConfig retrieveCache(String item) {
		MemcachedClient cache = CacheFactory.get();
		MsoConfig config = null;
		if (cache != null) { 
			config = (MsoConfig)cache.get(item);
		}
		return config;
	}

	//mso_config(item), example mso_config(debug) 
	public String getCacheKey(String item) {
		return "mso_config(" + item + ")";
	}
	*/
}

