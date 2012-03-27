package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import net.spy.memcached.MemcachedClient;

import com.nncloudtv.dao.MsoConfigDao;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;

public class MsoConfigManager {

	private MsoConfigDao configDao = new MsoConfigDao();
    protected static final Logger log = Logger.getLogger(MsoConfigManager.class.getName());
    
	private static final String EXTERNAL_ROOT_PATH = "http://9x9ui.s3.amazonaws.com/tprd-moveout"; // where to place this config ?
	private static final String S3_UPLOAD_BUCKET = "9x9tmp"; // where to place this config ?
	
	static public String getS3UploadBucket() {
		return S3_UPLOAD_BUCKET;
	}
	
	static public String getExternalRootPath() {
		return EXTERNAL_ROOT_PATH;
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
		MemcachedClient cache = CacheFactory.get();		
		if (!cacheReset && cache != null) {
			String result = (String)cache.get(cacheKey);
			if (result != null){
				log.info("value from cache: key=" + cacheKey + "value=" + result);
				return NnStringUtil.stringToBool(result);
			}			
		}
		boolean value = false;
		MsoConfig config = new MsoConfigDao().findByItem(key);
		if (config != null) {
			if (cache != null) {
				cache.set(cacheKey, CacheFactory.EXP_DEFAULT, config.getValue());
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

