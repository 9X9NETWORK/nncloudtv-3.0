package com.nncloudtv.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.MsoConfigDao;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;

@Service
public class MsoConfigManager {
    
    private MsoConfigDao configDao = new MsoConfigDao();
    protected static final Logger log = Logger.getLogger(MsoConfigManager.class.getName());
    
    static public String getS3UploadBucket() {
        Properties properties = new Properties();
        String result = "";
        try {
            properties.load(MsoConfigManager.class.getClassLoader().getResourceAsStream("aws.properties"));
            result = properties.getProperty("s3_upload_bucket");
        } catch (IOException e) {
            NnLogUtil.logException(e);
        }
        return result;
    }
    
    static public String getPiwikDomain() {
        Properties properties = new Properties();
        String result = "";
        try {
            properties.load(MsoConfigManager.class.getClassLoader().getResourceAsStream("piwik.properties"));
            result = properties.getProperty("piwik_server");
        } catch (IOException e) {
            NnLogUtil.logException(e);
        }
        return result;
    }
    
    static public String getServerDomain() {
        Properties properties = new Properties();
        String result = "";
        try {
            properties.load(MsoConfigManager.class.getClassLoader().getResourceAsStream("sns.properties"));
            result = properties.getProperty("server_domain");
        } catch (IOException e) {
            NnLogUtil.logException(e);
        }
        return result;
    }
    
    static public String getAutoshareFacebookApptoken() {
        Properties properties = new Properties();
        String result = "";
        try {
            properties.load(MsoConfigManager.class.getClassLoader().getResourceAsStream("sns.properties"));
            result = properties.getProperty("autoshare_facebook_apptoken");
        } catch (IOException e) {
            NnLogUtil.logException(e);
        }
        return result;
    }
    
    static public String getExternalRootPath() {
        Properties properties = new Properties();
        String result = "";
        try {
            properties.load(MsoConfigManager.class.getClassLoader().getResourceAsStream("aws.properties"));
            result = properties.getProperty("static_file_root_path");
        } catch (IOException e) {
            NnLogUtil.logException(e);
        }
        return result;
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
            if (CacheFactory.isRunning) {
                CacheFactory.set(cacheKey, config.getValue());
            }
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

}

