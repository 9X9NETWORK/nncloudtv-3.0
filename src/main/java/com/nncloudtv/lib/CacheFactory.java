package com.nncloudtv.lib;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import net.spy.memcached.MemcachedClient;

public class CacheFactory {
	
	protected static final Logger log = Logger.getLogger(CacheFactory.class.getName());
	
	private static MemcachedClient cache = null;
	public static int EXP_DEFAULT = 2592000;
	public static int PORT_DEFAULT = 11211;
	
	public static MemcachedClient get() {		
		try {
			//TODO load it from memcache.properties, but make sure the property input stream is closed
			cache = new MemcachedClient(new InetSocketAddress("localhost", CacheFactory.PORT_DEFAULT));		
		} catch (IOException e) {	 
		   log.severe("memcache io exception");
		   NnLogUtil.logException(e);
		} catch (Exception e) {
			log.severe("memcache exception");
			NnLogUtil.logException(e);
		}
		
		return cache;
	}	
	
}
