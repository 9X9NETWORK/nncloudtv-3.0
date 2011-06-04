package com.nncloudtv.web.admin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import net.spy.memcached.MemcachedClient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.service.MsoManager;

@Controller
@RequestMapping("admin/cache")
public class AdminCacheController {

	protected static final Logger log = Logger.getLogger(AdminCacheController.class.getName());
	
	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		NnLogUtil.logException(e);
		return "error/exception";				
	}

	@RequestMapping("spy_set")
	public ResponseEntity<String> spy_set() {
		String output = "No Cache";
		MemcachedClient c;
		try {
			c = new MemcachedClient(
				    new InetSocketAddress("localhost", 11211));
			c.set("hello", 3600, "9x9");
			output = (String)c.get("hello");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NnNetUtil.textReturn(output);
	}
	
	@RequestMapping("spy_get")
	public ResponseEntity<String> spy_get() {
		String output = "No Cache";
		MemcachedClient c;
		try {
			c = new MemcachedClient(
				    new InetSocketAddress("localhost", 11211));
			output = (String)c.get("hello");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return NnNetUtil.textReturn(output);
	}

	@RequestMapping("client_set")
	public ResponseEntity<String> client_set() {
		Mso mso = new MsoManager().findByName("9x9");
		MemcachedClient cache = CacheFactory.get();
		if (cache != null) 
			cache.set(MsoManager.getCacheKey(mso.getName()), 3600, mso);
		return NnNetUtil.textReturn("OK");
	}

	@RequestMapping("client_get")
	public ResponseEntity<String> client_get() {
		Mso mso = new MsoManager().findByName("9x9");
		MemcachedClient cache = CacheFactory.get();
		String result = "no cache found";
		if (cache != null) {
			Mso cachedMso = (Mso)cache.get(MsoManager.getCacheKey(mso.getName()));
			result = cachedMso.getLogoUrl();
		}
		return NnNetUtil.textReturn(result);
	}
	
}
