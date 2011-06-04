package com.nncloudtv.lib;

import java.io.IOException;
import java.net.InetSocketAddress;

import net.spy.memcached.MemcachedClient;

public class CacheFactory {
	private static MemcachedClient cache = null;
	
	public static MemcachedClient get() {		
		try {
			cache = new MemcachedClient(new InetSocketAddress("localhost", 11211));				    
		} catch (IOException e) {	 
		   e.printStackTrace();
		}
		
		return cache;
	}	
	
}
