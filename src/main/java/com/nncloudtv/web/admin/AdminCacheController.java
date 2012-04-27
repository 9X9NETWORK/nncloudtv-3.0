package com.nncloudtv.web.admin;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.datastore.DataStoreCache;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;

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
		MemcachedClient cache = CacheFactory.getClient();
		try {
			cache.delete(key).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		cache.shutdown(); //unsure
		return NnNetUtil.textReturn("cache delete:" + key);
	}

	@RequestMapping("get")
	public ResponseEntity<String> get(@RequestParam(value="key", required=false)String key) {
		MemcachedClient cache = CacheFactory.getClient();
		String value = "";
		if (cache != null) { 
			value = (String)cache.get(key);		
			cache.shutdown();
		}
		return NnNetUtil.textReturn("cache get:" + value);
	}

	@RequestMapping("set")
	public ResponseEntity<String> set(
			@RequestParam(value="key", required=false)String key,
			@RequestParam(value="value", required=false)String value) {
		MemcachedClient cache = null;
		try {
			cache = CacheFactory.getClient();
		} catch (OperationTimeoutException e) {
			log.info("memcache down");
		}
		String setValue = "";
		if (cache != null) { 
			cache.set(key, CacheFactory.EXP_DEFAULT, value);
			setValue = (String)cache.get(key);
			cache.shutdown();
		}
		return NnNetUtil.textReturn("cache get:" + setValue);
	}
	
	//cache flush
	@RequestMapping("flush")
	public ResponseEntity<String> flush() {
		MemcachedClient cache = CacheFactory.getClient();
		if (cache != null) {
			try {
				cache.flush().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			cache.shutdown();
		}
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
