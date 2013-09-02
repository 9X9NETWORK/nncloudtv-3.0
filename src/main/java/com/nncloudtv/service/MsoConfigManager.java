package com.nncloudtv.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.MsoConfigDao;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.SysTag;

@Service
public class MsoConfigManager {
    
    private MsoConfigDao configDao = new MsoConfigDao();
    protected static final Logger log = Logger.getLogger(MsoConfigManager.class.getName());
    
    static String getProperty(String propertyFile, String propertyName) {
        
        Properties properties = new Properties();
        String result = null;
        log.info("to get property " + propertyName + " from " + propertyFile);
        try {
            properties.load(MsoConfigManager.class.getClassLoader().getResourceAsStream(propertyFile));
            result = properties.getProperty(propertyName);
        } catch (IOException e) {
            NnLogUtil.logException(e);
        }
        return result;
    }
    
    static public String getS3UploadBucket() {
        
        return getProperty("aws.properties", "s3_upload_bucket");
    }
    
    static public String getPiwikDomain() {
        
        return getProperty("piwik.properties", "piwik_server");
    }
    
    static public String getServerDomain() {
        
        return getProperty("sns.properties", "server_domain");
    }
    
    static public String getFacebookAppToken() {
        
        return getProperty("sns.properties", "facebook_app_token");
    }
    
    static public String getFacebookClientId() {
        
        return getProperty("sns.properties", "facebook_client_id");
    }
    
    static public String getFacebookClientSecret() {
        
        return getProperty("sns.properties", "facebook_client_secret");
    }
    
    static public String getExternalRootPath() {
        
        return getProperty("aws.properties", "static_file_root_path");
    }
    
    public MsoConfig create(MsoConfig config) {
        Date now = new Date();
        config.setCreateDate(now);
        config.setUpdateDate(now);
        return configDao.save(config);
    }
    
    public MsoConfig save(Mso mso, MsoConfig config) {
        config.setUpdateDate(new Date());
        if (mso.getType() == Mso.TYPE_NN) {
            this.processCache(config);
        }
        return configDao.save(config);
    }

    public void processCache(MsoConfig config) {
        isInReadonlyMode(true);
        isQueueEnabled(true);
    }

    public static boolean getBooleanValueFromCache(String key, boolean cacheReset) {
        String cacheKey = "msoconfig(" + key + ")";
        try {        
            String result = (String)CacheFactory.get(cacheKey);        
            if (result != null){
                log.info("value from cache: key=" + cacheKey + "value=" + result);            
                return NnStringUtil.stringToBool(result);
            }            
        } catch (Exception e) {
            log.info("memcache error");
        }
        boolean value = false;
        MsoConfig config = new MsoConfigDao().findByItem(key);
        if (config != null) {
            CacheFactory.set(cacheKey, config.getValue());
            value = NnStringUtil.stringToBool(config.getValue());
        }
        return value;
    }
        
    public static boolean isInReadonlyMode(boolean cacheReset) {
        return MsoConfigManager.getBooleanValueFromCache(MsoConfig.RO, cacheReset);
    }
        
    public static boolean isQueueEnabled(boolean cacheReset) {
        boolean status = MsoConfigManager.getBooleanValueFromCache(MsoConfig.QUEUED, cacheReset);     
        return status;     
    }
    
    public List<MsoConfig> findByMso(Mso mso) {
        return configDao.findByMso(mso);
    }
            
    public MsoConfig findByMsoAndItem(Mso mso, String item) {
        return configDao.findByMsoAndItem(mso.getId(), item);
    }
    
    public MsoConfig findByItem(String item) {
        return configDao.findByItem(item);
    }
    
    /** parse supportedRegion to list of sphere that mso can supported */
    public static List<String> parseSupportedRegion(String supportedRegion) {
        
        if (supportedRegion == null) {
            return new ArrayList<String>();
        }
        
        List<String> spheres = new ArrayList<String>();
        String[] pairs = supportedRegion.split(";");
        for (String pair : pairs) {
            String[] values = pair.split(" ");
            if (values[0].equals(LangTable.LANG_EN)) {
                spheres.add(LangTable.LANG_EN);
            }
            if (values[0].equals(LangTable.LANG_ZH)) {
                spheres.add(LangTable.LANG_ZH);
            }
            if (values[0].equals(LangTable.OTHER)) {
                spheres.add(LangTable.OTHER);
            }
        }
        
        return spheres;
    }
    
    public static List<String> parseSystemCategoryMask(String systemCategoryMask) {
        
        if (systemCategoryMask == null) {
            return new ArrayList<String>();
        }
        
        String[] systemCategoryLocks = systemCategoryMask.split(",");
        if (systemCategoryLocks.length == 1 && systemCategoryLocks[0].equals("")) {
            return new ArrayList<String>();
        }
        
        List<String> results = new ArrayList<String>();
        for (String systemCategoryLock : systemCategoryLocks) {
            results.add(systemCategoryLock);
        }
        
        return results;
    }
    
    public static String composeSystemCategoryMask(List<String> systemCategoryLocks) {
        
        if (systemCategoryLocks == null || systemCategoryLocks.size() < 1) {
            return "";
        }
        
        String systemCategoryMask = StringUtils.join(systemCategoryLocks, ",");
        return systemCategoryMask;
    }
    
    public static List<String> verifySystemCategoryLocks(List<String> systemCategoryLocks) {
        
        if (systemCategoryLocks == null || systemCategoryLocks.size() < 1) {
            return new ArrayList<String>();
        }
        
        // populate System's CategoryIds
        MsoManager msoMngr = new MsoManager();
        SysTagManager sysTagMngr = new SysTagManager();
        List<SysTag> systemCategories = sysTagMngr.findByMsoIdAndType(msoMngr.findNNMso().getId(), SysTag.TYPE_CATEGORY);
        Map<Long, Long> systemCategoryIds = new TreeMap<Long, Long>();
        for (SysTag systemCategory : systemCategories) {
            systemCategoryIds.put(systemCategory.getId(), systemCategory.getId());
        }
        
        List<String> verifiedLocks = new ArrayList<String>();
        for (String lock : systemCategoryLocks) {
            
            Long categoryId = null;
            try {
                categoryId = Long.valueOf(lock);
            } catch (NumberFormatException e) {
                // special lock for lock all System Category
                if (lock.equals(MsoConfig.DISABLE_ALL_SYSTEM_CATEGORY)) {
                    verifiedLocks.add(MsoConfig.DISABLE_ALL_SYSTEM_CATEGORY);
                }
            }
            
            if (categoryId != null) {
                if (systemCategoryIds.containsKey(categoryId)) {
                    verifiedLocks.add(lock);
                }
            }
        }
        
        return verifiedLocks;
    }

}

