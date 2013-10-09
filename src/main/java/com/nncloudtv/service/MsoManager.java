package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.MsoDao;
import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.NnChannel;

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
    
    public long addMsoVisitCounter(boolean readOnly) {        
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
    
    public static boolean isNNMso(Mso mso) {
    	if (mso == null)
    		return false;
    	if (mso.getId() == 1)
    		return true;
    	return false;
    }
    
    public String[] getBrandInfoCache(Mso mso, String os) {
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
        boolean regionSet = false;
        boolean videoSet = false;
        String videoMatchItem = MsoConfig.VIDEO + "-" + os;
        for (MsoConfig c : list) {
            System.out.println(c.getItem() + ";" + c.getValue());
            if (c.getItem().equals(MsoConfig.DEBUG))
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.DEBUG, c.getValue());
            if (c.getItem().equals(MsoConfig.FBTOKEN))
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.FBTOKEN, c.getValue());
            if (c.getItem().equals(MsoConfig.RO)) {
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.RO, c.getValue());
            }            
            if (c.getItem().equals(MsoConfig.SUPPORTED_REGION)) {
            	result[0] += PlayerApiService.assembleKeyValue(MsoConfig.SUPPORTED_REGION, c.getValue());
            	regionSet = true;
            }
            if (c.getItem().equals(MsoConfig.FORCE_UPGRADE)) {
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.FORCE_UPGRADE, c.getValue());
            }            
            if (c.getItem().equals(MsoConfig.UPGRADE_MSG)) {
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.UPGRADE_MSG, c.getValue());
            }    
            if (c.getItem().equals(videoMatchItem)) {
                result[0] += PlayerApiService.assembleKeyValue(MsoConfig.VIDEO, c.getValue());
                videoSet = true;
            }            
        }
        if (regionSet == false) {
        	result[0] += PlayerApiService.assembleKeyValue(MsoConfig.SUPPORTED_REGION, "en US;zh 台灣");
        }
        if (videoSet == false) {
        	result[0] += PlayerApiService.assembleKeyValue(MsoConfig.VIDEO, "en w-YkGyubqcA;zh w-YkGyubqcA");
        }
        CacheFactory.set(cacheKey, result);
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
        if (name == null || name.isEmpty()) {return null;}
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
        CacheFactory.set(cacheKey, mso);
        return mso;
    }
    
    public Mso findById(long id) {
        return msoDao.findById(id);
    }
    
    /** rewrite method findById, populate supportedRegion information */
    public Mso findByIdWithSupportedRegion(long id) {
        Mso mso = msoDao.findById(id);
        if (mso == null) {
            return null;
        }
        
        MsoConfigManager configMngr = new MsoConfigManager();
        MsoConfig config = configMngr.findByMsoAndItem(mso, MsoConfig.SUPPORTED_REGION);
        if (config == null) {
            mso.setSupportedRegion(null);
        } else {
            mso.setSupportedRegion(config.getValue());
        }
        
        return mso;
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
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            return new ArrayList<Mso>();
        }
        
        List<Mso> validMsos = new ArrayList<Mso>();
        validMsos.add(findNNMso()); // channel is always valid for brand 9x9
        
        if (channel.getStatus() == NnChannel.STATUS_SUCCESS &&
                channel.getContentType() != NnChannel.CONTENTTYPE_FAVORITE &&
                channel.isPublic() == true) {
            // the channel is in the official store
        } else {
            return validMsos;
        }
        
        MsoConfigManager configMngr = new MsoConfigManager();
        MsoConfig supportedRegion = null;
        List<String> spheres = null;
        List<Mso> msos = findByType(Mso.TYPE_MSO);
        for (Mso mso : msos) {
            
            supportedRegion = configMngr.findByMsoAndItem(mso, MsoConfig.SUPPORTED_REGION); // TODO : sql in the for loop
            if (supportedRegion == null) {
                validMsos.add(mso); // mso support all region
            } else {
                spheres = MsoConfigManager.parseSupportedRegion(supportedRegion.getValue());
                spheres.add(LangTable.OTHER);
                for (String sphere : spheres) {
                    if (sphere.equals(channel.getSphere())) { // this channel's sphere that MSO supported
                        validMsos.add(mso);
                        break;
                    }
                    // if not hit any of sphere, channel is not playable on this MSO, is not valid brand.
                }
            }
        }
        
        return validMsos;
    }
    
    /* indicate channel can or can't play on target brand
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
    } */
    
    /** indicate channel can or can't set brand for target MSO,
     *  9x9 is always a valid brand for auto-sharing even channel is not playable */
    public boolean isValidBrand(Long channelId, Mso mso) {
        
        if (channelId == null || mso == null) {
            return false;
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            return false;
        }
        
        // 9x9 always valid
        if (mso.getType() == Mso.TYPE_NN) {
            return true;
        }
        
        // official store check
        if (channel.getStatus() == NnChannel.STATUS_SUCCESS &&
                channel.getContentType() != NnChannel.CONTENTTYPE_FAVORITE &&
                channel.isPublic() == true) {
            // the channel is in official store
        } else {
            return false; // the channel is not in official store
        }
        
        // support region check
        MsoConfigManager configMngr = new MsoConfigManager();
        MsoConfig supportedRegion = configMngr.findByMsoAndItem(mso, MsoConfig.SUPPORTED_REGION);
        if (supportedRegion == null) {
            return true; // Mso's region support all sphere
        } else {
            List<String> spheres = MsoConfigManager.parseSupportedRegion(supportedRegion.getValue());
            spheres.add(LangTable.OTHER);
            for (String sphere : spheres) {
                if (sphere.equals(channel.getSphere())) { // Mso's region support channel's sphere
                    return true;
                }
            }
            return false; // Mso's region not support channel's sphere
        }
    }
    
    /** Get playable channels on target MSO.
     *  The basic definition of playable should same as NnChannelDao.getStoreChannels. */
    public List<Long> getPlayableChannels(List<Long> channelIds, Long msoId) {
        
        if (channelIds == null || channelIds.size() < 1 || msoId == null) {
            return new ArrayList<Long>();
        }
        
        Mso mso = findByIdWithSupportedRegion(msoId);
        if (mso == null) {
            return new ArrayList<Long>();
        }
        
        List<String> supportSpheres;
        if (mso.getSupportedRegion() == null) {
            supportSpheres = null; // means support all sphere
        } else {
            supportSpheres = MsoConfigManager.parseSupportedRegion(mso.getSupportedRegion());
            supportSpheres.add(LangTable.OTHER);
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        List<NnChannel> channels = channelMngr.findByIds(channelIds);
        if (channels == null || channels.size() < 1) {
            return new ArrayList<Long>();
        }
        
        List<Long> results = new ArrayList<Long>();
        for (NnChannel channel : channels) {
            
            // official store check
            if (channel.getStatus() == NnChannel.STATUS_SUCCESS &&
                    channel.getContentType() != NnChannel.CONTENTTYPE_FAVORITE &&
                    channel.isPublic() == true) {
                // the channel is in official store
            } else {
                continue; // the channel is not in official store
            }
            
            // MSO support region check
            if (supportSpheres == null) { // Mso's region support all sphere
                results.add(channel.getId());
            } else {
                for (String sphere : supportSpheres) {
                    if (sphere.equals(channel.getSphere())) { // Mso's region support channel's sphere
                        results.add(channel.getId());
                        break;
                    }
                }
            }
        }
        
        return results;
    }
    
    public boolean isPlayableChannel(Long channelId, Long msoId) {
        
        if (channelId == null || msoId==null) {
            return false;
        }
        
        List<Long> unverifiedChannel = new ArrayList<Long>();
        unverifiedChannel.add(channelId);
        
        List<Long> verifiedChannel = getPlayableChannels(unverifiedChannel, msoId);
        if (verifiedChannel != null && verifiedChannel.isEmpty() == false) {
            return true;
        }
        return false;
    }
    
}
