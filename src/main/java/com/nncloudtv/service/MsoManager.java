package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.MsoDao;
import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.StoreListing;
import com.nncloudtv.model.SysTagMap;

@Service
public class MsoManager {

    protected static final Logger log = Logger.getLogger(MsoManager.class.getName());
    
    private MsoDao msoDao = new MsoDao();    

    public Mso findOneByName(String name) {
        if (name == null)
            return this.findNNMso(); //most of the situation
        Mso mso = this.findByName(name);
        if (mso == null)
            return this.findNNMso(); 
        return mso;
    }
    
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
        
        // avoid save mso which name is duplicate with other, except itself
        Mso origin = this.findByName(mso.getName());
        if (origin != null && origin.getId() != mso.getId()) { 
            return null;
        }
        
        Date now = new Date();        
        if (mso.getCreateDate() == null)
            mso.setCreateDate(now);
        mso.setUpdateDate(now);
        msoDao.save(mso);
        /*
        if (mso.getType() == Mso.TYPE_NN)
            this.processCache();
            */
        return mso;
    }
    /*
    public void processCache() {
        this.getBrandInfoCache(true);
    }
    */
    
    public Mso findNNMso() {
        List<Mso> list = this.findByType(Mso.TYPE_NN);
        return list.get(0);
    }
    
    public String[] getBrandInfoCache(Mso mso) {
        if (mso == null) {return null; }
        String[] result = {""};
        String cacheKey = "brandInfo(" + mso.getName() + ")";
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

    public Mso getByNameFromCache(String name) {
        if (name == null) {return null;}
        String cacheKey = "mso(" + name + ")";
        try {
            Mso cached = (Mso) CacheFactory.get(cacheKey);
            if (cached != null) {
                log.info("get mso object from cache:" + cached.getId());
                return cached;
            }
        } catch (Exception e) {
            log.info("memcache error");
        }        
        log.info("NOT get mso object from cache:" + name);
        Mso mso = msoDao.findByName(name);
        if (CacheFactory.isRunning) { 
            CacheFactory.set(cacheKey, mso);
        }         
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
    
    /** indicate which brands that channel can play on, means channel is in the brand's store */
    public List<Mso> getValidBrands(Long channelId) {
        
        if (channelId == null) {
            return new ArrayList<Mso>();
        }
        
        List<Mso> validMsos = new ArrayList<Mso>();
        validMsos.add(findNNMso()); // channel is always playable on 9x9
        
        SysTagMapManager sysTagMapMngr = new SysTagMapManager();
        SysTagMap sysTagMap = sysTagMapMngr.findSysTagMap((long) 1, channelId); // TODO : categoryId = 1 is hard coded
        if (sysTagMap == null) { // the channel not in the 9x9 store
            return validMsos;
        }
        
        StoreListingManager storeListingMngr = new StoreListingManager();
        List<StoreListing> blackList = storeListingMngr.getBlackList(channelId);
        Map<Long, Long> blackListMap = new TreeMap<Long, Long>();
        if (blackList != null && blackList.size() > 0) {
            for (StoreListing item : blackList) {
                blackListMap.put(item.getMsoId(), item.getMsoId());
            }
        }
        
        List<Mso> msos = findByType(Mso.TYPE_MSO);
        for (Mso mso : msos) {
            if (blackListMap.containsKey(mso.getId())) {
                // channel is not playable on this brand
            } else {
                validMsos.add(mso);
            }
        }
        
        return validMsos;
    }
    
    /** indicate channel can or can't play on target brand */
    public boolean isValidBrand(Long channelId, Mso mso) {
        
        if (channelId == null || mso == null) {
            return false;
        }
        
        List<Mso> validMsos = getValidBrands(channelId);
        for (Mso validMso : validMsos) {
            if (validMso.getName().equals(mso.getName())) {
                return true;
            }
        }
        
        return false;
    }
    
}
