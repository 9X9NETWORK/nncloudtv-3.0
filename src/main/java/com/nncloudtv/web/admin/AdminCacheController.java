package com.nncloudtv.web.admin;

import java.util.logging.Logger;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.datastore.DataStoreCache;

import net.spy.memcached.MemcachedClient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.PMF;

/**  
 * Including memcache flush and db flush
 */
@Controller
@RequestMapping("admin/cache")
public class AdminCacheController {

	protected static final Logger log = Logger.getLogger(AdminCacheController.class.getName());
	
	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		NnLogUtil.logException(e);
		return "error/exception";				
	}

	//delete cache with key
	@RequestMapping("delete")
	public ResponseEntity<String> delete(@RequestParam(value="key", required=false)String key) {
		MemcachedClient cache = CacheFactory.get();
		cache.delete(key);
		cache.shutdown();
		return NnNetUtil.textReturn("cache delete:" + key);
	}
	
	//cache flush
	@RequestMapping("flush")
	public ResponseEntity<String> flush() {
		MemcachedClient cache = CacheFactory.get();
		cache.flush();
		cache.shutdown();
		return NnNetUtil.textReturn("flush");
	}

	@RequestMapping("dbevict")
	public ResponseEntity<String> dbevict(@RequestParam int pm) {
		String output = "dbevict:fail";
		PersistenceManagerFactory pmf = null; 
		DataStoreCache cache = null;
		switch (pm) {
		  case 1:  
				pmf = PMF.getContent();
				cache = pmf.getDataStoreCache();
				break;
		  case 2:
				pmf = PMF.getAnalytics();
				cache = pmf.getDataStoreCache();
				break;
		  case 3:
				pmf = PMF.getNnUser1();
				cache = pmf.getDataStoreCache();
				break;
		  case 4:
				pmf = PMF.getNnUser2();
				cache = pmf.getDataStoreCache();
				break;
		}
		if (cache != null) {
			cache.evictAll();
			output = "dbevict:success";
		}
		return NnNetUtil.textReturn(output);
	}
	
}
