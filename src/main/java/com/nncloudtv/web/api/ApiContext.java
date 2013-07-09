package com.nncloudtv.web.api;

import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

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
        
        mso = msoMngr.getByNameFromCache(httpReqest.getParameter(ApiContext.PARAM_MSO));
        if (mso == null) {
            mso = msoMngr.getByNameFromCache(Mso.NAME_9X9);;
        }
        
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
        log.info("language = " + language.getLanguage() + "; mso = " + mso.getName() + "; version = " + version + "; root = " + root);
    }
    
    public Boolean isProductionSite() {
        
        if (root == null || root.isEmpty()) {
            
            return false;
            
        } else if (root.matches(ApiContext.PRODUCTION_SITE_URL_REGEX)) {
            
            return true;
            
        } else {
            
            String domain = root.replaceAll("^http(s)?:\\/\\/", "");
            String[] split = domain.split("\\.");
            if (split.length > 2) {
                log.info("sub-domain = " + split[0]);
                Mso subdomain = msoMngr.findByName(split[0]);
                if (subdomain != null)
                    return true;
            }
        }
        
        return false;
    }
}
