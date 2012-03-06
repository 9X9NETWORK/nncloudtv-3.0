package com.nncloudtv.lib;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.spy.memcached.MemcachedClient;

public class CacheFactory {
	private static MemcachedClient cache = null;
	public static int EXP_DEFAULT = 2592000;
	public static int PORT_DEFAULT = 11211;
	
	public static MemcachedClient get() {		
		try {
			cache = new MemcachedClient(new InetSocketAddress("localhost", CacheFactory.PORT_DEFAULT));				    
		} catch (IOException e) {	 
		   e.printStackTrace();
		}
		
		return cache;
	}	
	
}
