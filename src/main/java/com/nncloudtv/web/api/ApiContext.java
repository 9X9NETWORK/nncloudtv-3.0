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
    
    public final static String PRODUCTION_SITE_URL = "http://www.9x9.tv";
    
    public final static String HEADER_USER_AGENT = "user-agent";
    
    public final static String PARAM_MSO = "mso";
    public final static String PARAM_LANG = "lang";
    public final static String PARAM_SPHERE = "shpere";
    public final static String PARAM_VERSION = "v";
    
    public NnChannelManager channelMngr;
    public NnUserManager userMngr;
    public MsoManager msoMngr;
    
    public HttpServletRequest httpReqest;
    public Mso mso;
    public Locale language;
    public Long version;
    public String root;
    
    protected static final Logger log = Logger.getLogger(ApiContext.class.getName());
    
    public ApiContext(HttpServletRequest req) {
    
        this.httpReqest = req;
        
        log.info("user agent = " + req.getHeader(ApiContext.HEADER_USER_AGENT));
        NnNetUtil.logUrl(req);
        
        channelMngr = new NnChannelManager();
        userMngr = new NnUserManager();
        msoMngr = new MsoManager();
        
        mso = this.msoMngr.getByNameFromCache(this.httpReqest.getParameter(ApiContext.PARAM_MSO));
        if (mso == null) {
            mso = this.msoMngr.getByNameFromCache(Mso.NAME_9X9);;
        }
        log.info("mso entrance = " + mso.getId());
        
        String lang = this.httpReqest.getParameter(ApiContext.PARAM_LANG);
        if (LangTable.isValidLanguage(lang)) {
            this.language = LangTable.getLocale(lang);
        } else {
            this.language = Locale.ENGLISH; // TODO: from http request
        }
        log.info("language = " + this.language.getLanguage());
        
        this.version = Long.parseLong("31");
        String versionStr = this.httpReqest.getParameter(PARAM_VERSION);
        if (versionStr != null) {
            try {
                this.version = Long.parseLong(versionStr);
            } catch (NumberFormatException e) {
            }
        }
        log.info("version = " + this.version);
        
        this.root = NnNetUtil.getUrlRoot(this.httpReqest);
        log.info("root = " + this.root);
    }
    
    public Boolean isProductionSite() {
        
        if (this.root == null) {
            
            return false;
            
        } else if (this.root.equals(ApiContext.PRODUCTION_SITE_URL)) {
            
            return true;
            
        } else {
            
            String domain = this.root.replaceAll("^http(s)?:\\/\\/", "");
            String[] split = domain.split("\\.");
            if (split.length > 2) {
                log.info("sub-domain = " + split[0]);
                Mso subdomain = this.msoMngr.findByName(split[0]);
                if (subdomain != null)
                    return true;
            }
            
        }
        
        return false;
    }
}
