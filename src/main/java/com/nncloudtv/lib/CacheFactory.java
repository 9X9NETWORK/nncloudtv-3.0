package com.nncloudtv.lib;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;

public class CacheFactory {
    
    protected static final Logger log = Logger.getLogger(CacheFactory.class.getName());
    
    public static final int EXP_DEFAULT = 2592000;
    public static final int PORT_DEFAULT = 11211;
    public static final String ERROR = "ERROR";
    public static boolean isRunning = true;
    
    public static MemcachedClient getClient() {
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SunLogger"); 
        Logger.getLogger("net.spy.memcached").setLevel(Level.SEVERE);
     
        try {
            Properties properties = new Properties();
            properties.load(CacheFactory.class.getClassLoader().getResourceAsStream("memcache.properties"));
            String server = properties.getProperty("server");
            //log.info("memcache server:" + server);
            return new MemcachedClient(new InetSocketAddress(server, CacheFactory.PORT_DEFAULT));
        } catch (IOException e) {
           log.severe("memcache io exception");
           return null;
        } catch (Exception e) {
           log.severe("memcache exception");
           return null;
        }        
    }    

    public static Object get(String key) {
        MemcachedClient cache = CacheFactory.getClient();
        CacheFactory.isRunning = false;
        Object obj = null;
        try {
            obj = cache.get(key);
            CacheFactory.isRunning = true;
        } catch (OperationTimeoutException e) {
            log.severe("get OperationTimeoutException");
        } catch (NullPointerException e) {
            log.severe("memcache not found");
        } catch (Exception e) {
            log.severe("get Exception");
            e.printStackTrace();
        } finally {
            if (cache != null)
                cache.shutdown();            
        }
        return obj;
    }

    public static Object set(String key, Object obj) {        
        MemcachedClient cache = CacheFactory.getClient();
        CacheFactory.isRunning = false;
        Object myObj = null;
        try {
            cache.set(key, CacheFactory.EXP_DEFAULT, obj);
            myObj = cache.get(key);
            CacheFactory.isRunning = true;
        } catch (OperationTimeoutException e) {
            log.severe("memcache OperationTimeoutException");
        } catch (NullPointerException e) {
            log.severe("memcache not found");
        } catch (Exception e) {
            log.severe("get Exception");
            e.printStackTrace();
        } finally {
            if (cache != null)
                cache.shutdown();
        }
        return myObj;
    }    

    public static Object delete(String key) {        
        MemcachedClient cache = CacheFactory.getClient();
        CacheFactory.isRunning = false;
        Object obj = null;
        try {
            cache.delete(key).get();
            CacheFactory.isRunning = true;
        } catch (OperationTimeoutException e) {
            log.severe("get OperationTimeoutException");
        } catch (Exception e) {
            log.severe("get Exception");
            e.printStackTrace();
        } finally {
            cache.shutdown();            
        }
        return obj;
    }    
    
}
