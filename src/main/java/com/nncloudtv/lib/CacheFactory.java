package com.nncloudtv.lib;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
        
        if (!CacheFactory.isRunning) {
            log.warning("cache is not running");
            return null;
        }
        CacheFactory.isRunning = false;
        
        MemcachedClient cache = CacheFactory.getClient();
        if (cache == null) {
            log.info("cache [" + key + "] --> miss");
            return null;
        }
        
        Object obj = null;
        Future<Object> future = cache.asyncGet(key);
        try {
            obj = future.get(2, TimeUnit.SECONDS); // Asynchronously 
        } catch (OperationTimeoutException e) {
            log.severe("get OperationTimeoutException");
        } catch (NullPointerException e) {
            log.severe("memcache not found");
        } catch (Exception e) {
            log.severe("get Exception");
            e.printStackTrace();
        } finally {
            cache.shutdown(); 
            future.cancel(false);
            CacheFactory.isRunning = true;
        }
        log.info("cache [" + key + "] --> hit");
        return obj;
    }

    public static void set(String key, Object obj) {
        
        if (!CacheFactory.isRunning) {
            log.warning("cache is not running");
            return;
        }
        CacheFactory.isRunning = false;
        
        MemcachedClient cache = CacheFactory.getClient();
        if (cache == null) return;
        
        try {
            cache.set(key, CacheFactory.EXP_DEFAULT, obj);
        } catch (OperationTimeoutException e) {
            log.severe("memcache OperationTimeoutException");
        } catch (NullPointerException e) {
            log.severe("memcache not found");
        } catch (Exception e) {
            log.severe("get Exception");
            e.printStackTrace();
        } finally {
            cache.shutdown();
            CacheFactory.isRunning = true;
        }
        log.info("cache [" + key + "] --> set");
    }    

    public static void delete(String key) {
        
        //if (!CacheFactory.isRunning) {
        //    log.warning("cache is not running");
        //    return;
        //}
        CacheFactory.isRunning = false;
        
        MemcachedClient cache = CacheFactory.getClient();
        if (cache == null) return;
        
        try {
            cache.delete(key).get();
        } catch (OperationTimeoutException e) {
            log.severe("get OperationTimeoutException");
        } catch (Exception e) {
            log.severe("get Exception");
            e.printStackTrace();
        } finally {
            cache.shutdown();            
        //    CacheFactory.isRunning = true;
        }
        log.info("cache [" + key + "] --> delete");
    }    
    
}
