package com.nncloudtv.web.api;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Joiner;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnUserManager;


public class ApiContext {
    
    public final static String PRODUCTION_SITE_URL_REGEX = "^http(s)?:\\/\\/(www\\.)?9x9\\.tv$";
    public final static String DEFAULT_VERSION = "31";
    public final static String HEADER_USER_AGENT = "user-agent";
    public final static String HEADER_REFERRER = "referer";
    
    public final static String PARAM_MSO = "mso";
    public final static String PARAM_LANG = "lang";
    public final static String PARAM_SPHERE = "shpere";
    public final static String PARAM_VERSION = "v";
    
    NnChannelManager channelMngr;
    NnUserManager userMngr;
    MsoManager msoMngr;
    
    HttpServletRequest httpReqest;
    Locale language;
    Long version;
    String root;
    Mso mso;
    
    public Mso getMso() {
    
        return mso;
    }
    
    protected static final Logger log = Logger.getLogger(ApiContext.class.getName());
    
    public ApiContext(HttpServletRequest req) {
    
        httpReqest = req;
        log.info("user agent = " + req.getHeader(ApiContext.HEADER_USER_AGENT));
        NnNetUtil.logUrl(req);
        
        channelMngr = new NnChannelManager();
        userMngr = new NnUserManager();
        msoMngr = new MsoManager();
        
        String lang = httpReqest.getParameter(ApiContext.PARAM_LANG);
        if (LangTable.isValidLanguage(lang)) {
            language = LangTable.getLocale(lang);
        } else {
            language = Locale.ENGLISH; // TODO: from http request
        }
        
        version = Long.parseLong(ApiContext.DEFAULT_VERSION);
        String versionStr = httpReqest.getParameter(PARAM_VERSION);
        if (versionStr != null) {
            try {
                version = Long.parseLong(versionStr);
            } catch (NumberFormatException e) {
            }
        }
        
        root = NnNetUtil.getUrlRoot(httpReqest);
        
        mso = msoMngr.getByNameFromCache(httpReqest.getParameter(ApiContext.PARAM_MSO));
        if (mso == null) {
            String domain = root.replaceAll("^http(s)?:\\/\\/", "");
            String[] split = domain.split("\\.");
            if (split.length > 2) {
                log.info("sub-domain = " + split[0]);
                mso = msoMngr.findByName(split[0]);
            }
            if (mso == null)
                mso = msoMngr.getByNameFromCache(Mso.NAME_9X9);
        }
        
        log.info("language = " + language.getLanguage() + "; mso = " + mso.getName() + "; version = " + version + "; root = " + root);
    }
    
    public Boolean isProductionSite() {
        
        if (root == null || root.isEmpty()) {
            
            return false;
            
        } else if (root.matches(ApiContext.PRODUCTION_SITE_URL_REGEX)) {
            
            return true;
            
        } else {
            
            String domain = root.replaceAll("^http(s)?:\\/\\/", "");
            String[] splits = domain.split("\\.");
            if (splits.length == 3) {
                String subdomain = splits[0];
                log.info("subdomain = " + subdomain);
                if (msoMngr.findByName(subdomain) != null)
                    return true;
            }
        }
        
        return false;
    }
    
    public String getAppDomain() {
        
        String domain = root.replaceAll("^http(s)?:\\/\\/(www\\.)?", "");
        log.info("domain = " + domain);
        List<String> splits = Arrays.asList(domain.split("\\."));
        
        if (splits.size() < 3)
            return MsoManager.isNNMso(mso) ? "www." + domain : mso.getName() + "." + domain;
        
        log.info("subdomain = " + splits.get(0));
        if (msoMngr.findByName(splits.get(0)) != null) {
            
            splits.remove(0);
        }
        String remain = Joiner.on(".").join(splits);
        
        return MsoManager.isNNMso(mso) ? (splits.size() < 3 ? "www." + remain : remain) : mso.getName() + "." + remain;
    }
}
