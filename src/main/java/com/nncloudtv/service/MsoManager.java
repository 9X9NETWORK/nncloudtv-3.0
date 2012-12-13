package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.MsoDao;
import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;

@Service
public class MsoManager {

    protected static final Logger log = Logger.getLogger(MsoManager.class.getName());
    
    private MsoDao msoDao = new MsoDao();    
    
    public int addMsoVisitCounter(boolean readOnly) {        
        String counterName = "9x9" + "BrandInfo";
        CounterFactory factory = new CounterFactory();
        ShardedCounter counter = factory.getOrCreateCounter(counterName);
        if (!readOnly) {
            counter.increment();
        }
        return counter.getCount();
    }
    
    // only 9x9 mso will be stored in cache
    public Mso save(Mso mso) {
        if (this.findByName(mso.getName()) != null) { 
            return null;
        }
        Date now = new Date();        
        if (mso.getCreateDate() == null)
            mso.setCreateDate(now);
        mso.setUpdateDate(now);
        msoDao.save(mso);
        if (mso.getType() == Mso.TYPE_NN)
            this.processCache();
        return mso;
    }
    
    public void processCache() {
        this.getBrandInfoCache(true);
    }
    
    public Mso findNNMso() {
        List<Mso> list = this.findByType(Mso.TYPE_NN);
        System.out.println("list.size:" + list.size());
        return list.get(0);
    }
    
    public String[] getBrandInfoCache(boolean cacheReset) {
        String cacheKey = "brandInfo";
        String[] result = {""};
        try {
            String[] cached = (String[]) CacheFactory.get(cacheKey);
            if (cached != null) {
                log.info("get brandInfo from cache:" + cached.length);
                return cached;
            }
        } catch (Exception e) {
            log.info("memcache error");
        }
        log.info("brand info not from cache");
        Mso mso = this.findNNMso();
        if (mso == null) {return null; }
        MsoConfigManager configMngr = new MsoConfigManager();
        
        //general setting
        result[0] += PlayerApiService.assembleKeyValue("key", String.valueOf(mso.getId()));
        result[0] += PlayerApiService.assembleKeyValue("name", mso.getName());
        result[0] += PlayerApiService.assembleKeyValue("title", mso.getTitle());        
        result[0] += PlayerApiService.assembleKeyValue("logoUrl", mso.getLogoUrl());
        result[0] += PlayerApiService.assembleKeyValue("jingleUrl", mso.getJingleUrl());
        result[0] += PlayerApiService.assembleKeyValue("preferredLangCode", mso.getLang());
        result[0] += PlayerApiService.assembleKeyValue("jingleUrl", mso.getJingleUrl());
        List<MsoConfig> list = configMngr.findByMso(mso);
        //config        
        for (MsoConfig c : list) {
            System.out.println(c.getItem() + ";" + c.getValue());
            if (c.getItem().equals(MsoConfig.DEBUG))
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.DEBUG, c.getValue());
            if (c.getItem().equals(MsoConfig.FBTOKEN))
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.FBTOKEN, c.getValue());
            if (c.getItem().equals(MsoConfig.RO)) {
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.RO, c.getValue());
            }            
            if (c.getItem().equals(MsoConfig.FORCE_UPGRADE)) {
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.FORCE_UPGRADE, c.getValue());
            }            
            if (c.getItem().equals(MsoConfig.UPGRADE_MSG)) {
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.UPGRADE_MSG, c.getValue());
            }            
        }
        if (CacheFactory.isRunning) { 
            CacheFactory.set(cacheKey, result);
        }        
        return result;        
    }
            
    public List<Mso> findByType(short type) {
        return msoDao.findByType(type);
    }

    public Mso findByName(String name) {
        if (name == null) {return null;}
        Mso mso = msoDao.findByName(name);
        return mso;
    }
            
    public Mso findById(long id) {
        return msoDao.findById(id);
    }
    
    public List<Mso> findAll() {
        return msoDao.findAll();
    }
    
    public List<Mso> list(int page, int limit, String sidx, String sord) {
        return msoDao.list(page, limit, sidx, sord);
    }
    
    public List<Mso> list(int page, int limit, String sidx, String sord, String filter) {
        return msoDao.list(page, limit, sidx, sord, filter);
    }
    
    public int total() {
        return msoDao.total();
    }
    
    public int total(String filter) {
        return msoDao.total(filter);
    }
    
}
