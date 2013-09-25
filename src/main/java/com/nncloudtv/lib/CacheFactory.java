package com.nncloudtv.lib;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nncloudtv.service.MsoConfigManager;

import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;
import net.spy.memcached.internal.CheckedOperationTimeoutException;

public class CacheFactory {
    
    protected static final Logger log = Logger.getLogger(CacheFactory.class.getName());
    
    public static final int EXP_DEFAULT = 2592000;
    public static final int PORT_DEFAULT = 11211;
    public static final int ASYNC_CACHE_TIMEOUT = 2000; // milliseconds
    public static final int HEALTH_CHECK_INTERVAL = 100000; // milliseconds
    public static final String ERROR = "ERROR";
    
    public static boolean isRunning = true;
    private static long lastCheck = 0;
    private static List<InetSocketAddress> memcacheServers = null;
    
    private static boolean checkServer(InetSocketAddress addr) {
        
        String key = "loop_test(" + new Date().getTime() + ")";
        log.info("key = " + key);
        boolean alive = false;
        
        MemcachedClient cache = null;
        Future<Object> future = null;
        long now = new Date().getTime();
        try {
            cache = new MemcachedClient(addr);
            cache.set(key, EXP_DEFAULT, addr);
            future = cache.asyncGet(key);
            if (future.get(ASYNC_CACHE_TIMEOUT, TimeUnit.MILLISECONDS) != null) {
                alive = true;
            }
        } catch (NullPointerException e) {
            log.warning(e.getMessage());
        } catch (InterruptedException e) {
            log.warning(e.getMessage());
        } catch (ExecutionException e) {
            log.warning(e.getMessage());
        } catch (TimeoutException e) {
            log.warning(e.getMessage());
        } catch (IOException e) {
            log.warning(e.getMessage());
        } finally {
            long delta = new Date().getTime() - now;
            log.info("it takes " + delta + " milliseconds");
            if (cache != null)
                cache.shutdown();
            if (future != null)
                future.cancel(false);
        }
        if (alive)
            log.info("memcache server " + addr + " is alive");
        return alive;
    }
    
    public static MemcachedClient getClient() {
        
        return getClient(false);
    }
    
    private static MemcachedClient getClient(boolean reconfig) {
        
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SunLogger"); 
        Logger.getLogger("net.spy.memcached").setLevel(Level.SEVERE);
        
        try {
            // check & rebuild available server list
            if (reconfig) {
                
                String serverStr = MsoConfigManager.getMemcacheServer();
                List<InetSocketAddress> checkedServers = new ArrayList<InetSocketAddress>();
                log.info("memcache server = " + serverStr);
                String[] serverList = serverStr.split(",");
                for (String server : serverList) {
                    
                    InetSocketAddress addr = new InetSocketAddress(server, PORT_DEFAULT);
                    if (checkServer(addr)) {
                        checkedServers.add(addr);
                    }
                }
                memcacheServers = checkedServers;
                isRunning = (memcacheServers == null || memcacheServers.isEmpty()) ? false : true;
                if (!isRunning)
                    log.severe("no available memcache server");
            }
            return isRunning ? new MemcachedClient(new BinaryConnectionFactory(), memcacheServers) : null;
            
        } catch (NullPointerException e) {
            log.severe("memcache is missing");
        } catch (IOException e) {
            log.severe("memcache io exception");
        } catch (Exception e) {
            log.severe("memcache exception");
            e.printStackTrace();
        }
        return null;
    }
    
    public static Object get(String key) {
        
        if (key == null || key.isEmpty()) return null;
        boolean reconfig = false;
        long now = new Date().getTime();
        if (now - lastCheck > HEALTH_CHECK_INTERVAL) {
            // check point
            lastCheck = now;
            reconfig = true;
        } else if (!isRunning) {
            // cache is temporarily not running
            return null;
        }
        MemcachedClient cache = getClient(reconfig);
        if (cache == null) return null;
        
        Object obj = null;
        Future<Object> future = null;
        try {
            future = cache.asyncGet(key);
            obj = future.get(ASYNC_CACHE_TIMEOUT, TimeUnit.MILLISECONDS); // Asynchronously 
        } catch (CheckedOperationTimeoutException e) {
            log.warning("get CheckedOperationTimeoutException");
        } catch (OperationTimeoutException e) {
            log.severe("get OperationTimeoutException");
        } catch (NullPointerException e) {
            log.warning("there is no future");
        } catch (Exception e) {
            log.severe("get Exception");
            e.printStackTrace();
        } finally {
            cache.shutdown(ASYNC_CACHE_TIMEOUT, TimeUnit.MILLISECONDS);
            if (future != null)
                future.cancel(false);
        }
        if (reconfig)
            log.info("memcache reconfig costs " + (new Date().getTime() - now) + " milliseconds");
        if (obj == null)
            log.info("cache [" + key + "] --> missed");
        return obj;
    }
    
    public static Object set(String key, Object obj) {
        
        if (!isRunning || key == null || key.isEmpty()) return null;
        long now = new Date().getTime();
        MemcachedClient cache = getClient();
        if (cache == null) return null;
        
        Future<Object> future = null;
        Object retObj = null;
        try {
            cache.set(key, EXP_DEFAULT, obj);
            future = cache.asyncGet(key);
            retObj = future.get(ASYNC_CACHE_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (CheckedOperationTimeoutException e){
            log.warning("get CheckedOperationTimeoutException");
        } catch (OperationTimeoutException e) {
            log.severe("memcache OperationTimeoutException");
        } catch (NullPointerException e) {
            log.warning("there is no future");
        } catch (Exception e) {
            log.severe("get Exception");
            e.printStackTrace();
        } finally {
            cache.shutdown(ASYNC_CACHE_TIMEOUT, TimeUnit.MILLISECONDS);
            if (future != null)
                future.cancel(false);
        }
        log.info("save operation costs " + (new Date().getTime() - now) + " milliseconds");
        if (retObj == null)
            log.info("cache [" + key + "] --> not saved");
        else
            log.info("cache [" + key + "] --> saved");
        return retObj;
    }    
    
    public static void delete(String key) {
        
        boolean isDeleted = false;
        if (!isRunning || key == null || key.isEmpty()) return;
        long now = new Date().getTime();
        MemcachedClient cache = getClient();
        if (cache == null) return;
        
        try {
            cache.delete(key).get(ASYNC_CACHE_TIMEOUT, TimeUnit.MILLISECONDS);
            isDeleted = true;
        } catch (CheckedOperationTimeoutException e){
            log.warning("get CheckedOperationTimeoutException");
        } catch (OperationTimeoutException e) {
            log.severe("memcache OperationTimeoutException");
        } catch (NullPointerException e) {
            log.warning("there is no future");
        } catch (Exception e) {
            log.severe("get Exception");
            e.printStackTrace();
        } finally {
            cache.shutdown(ASYNC_CACHE_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        log.info("delete operation costs " + (new Date().getTime() - now) + " milliseconds");
        if (isDeleted) {
            log.info("cache [" + key + "] --> deleted");
        } else {
            log.info("cache [" + key + "] --> not deleted");
        }
    }
}
