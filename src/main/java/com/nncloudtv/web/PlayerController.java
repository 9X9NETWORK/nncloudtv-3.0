package com.nncloudtv.web;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.service.MsoConfigManager;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.service.PlayerService;
import com.nncloudtv.web.api.ApiContext;

@Controller
@RequestMapping("")
public class PlayerController {

    protected static final Logger log = Logger.getLogger(PlayerController.class.getName());
    
    @ExceptionHandler(Exception.class)
    public String exception(Exception e) {
        NnLogUtil.logException(e);
        return "error/exception";                
    }        
    
    @RequestMapping({"tv","10ft"})
    public String tv(@RequestParam(value="mso",required=false) String mso, 
            HttpServletRequest req, HttpServletResponse resp, Model model,
            @RequestParam(value="channel", required=false) String channel,
            @RequestParam(value="episode", required=false) String episode,
            @RequestParam(value="ch", required=false) String ch,
            @RequestParam(value="ep", required=false) String ep,            
            @RequestParam(value="jsp",required=false) String jsp,
            @RequestParam(value="js",required=false) String js) {
        try {
        	PlayerService service = new PlayerService();
        	if (service.isAndroid(req) || service.isIos(req)) {
        	    return "redirect:/mobile/";
        	}
        	String name = service.getBrandNameByUrl(req, mso);
        	Mso brand = new MsoManager().findOneByName(name);
        	if (brand.getType() == Mso.TYPE_FANAPP) {
        		//below, merge with view
	            log.info("Fan app sharing");
	            MsoConfigManager configMngr = new MsoConfigManager();
	            MsoConfig androidConfig = configMngr.findByMsoAndItem(brand, MsoConfig.STORE_ANDROID);
	            MsoConfig iosConfig = configMngr.findByMsoAndItem(brand, MsoConfig.STORE_IOS);
	        	String androidStoreUrl = "market://details?id=tv.tv9x9.player";
	        	String iosStoreUrl = "https://itunes.apple.com/app/9x9.tv/id443352510?mt=8";        	
	            if (androidConfig != null) {
	            	androidStoreUrl = androidConfig.getValue();
	            }
	            if (iosConfig != null) {
	            	iosStoreUrl = iosConfig.getValue();
	            }
	        	String reportUrl = service.getGAReportUrl(ch, ep, brand.getName());
	        	model.addAttribute("reportUrl", reportUrl);
	        	model.addAttribute("androidStoreUrl", androidStoreUrl);
	        	model.addAttribute("iosStoreUrl", iosStoreUrl);
	        	model.addAttribute("brandName", brand.getName());
	        	return "player/fanapp";
        	}
            ApiContext context = new ApiContext(req);
            model = service.prepareBrand(model, context.getMso().getName(), resp);
            model = service.preparePlayer(model, js, jsp, req);
            if (jsp != null && jsp.length() > 0) {
                return "player/" + jsp;
            }
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);            
        }
        return "player/mini";
    }    
    
    //?_escaped_fragment_=ch=2%26ep=3
    @RequestMapping("/")
    public String index(
            @RequestParam(value="name",required=false) String name,
            @RequestParam(value="jsp",required=false) String jsp,
            @RequestParam(value="js",required=false) String js,
            @RequestParam(value="mso",required=false) String mso,            
            @RequestParam(value="_escaped_fragment_", required=false) String escaped,
            HttpServletRequest req, HttpServletResponse resp, Model model) {
        try {
            PlayerService service = new PlayerService();
            model = service.prepareBrand(model, mso, resp);
            if (escaped != null) {
                model = service.prepareCrawled(model, escaped);
                return "player/crawled";
            }
            model = service.preparePlayer(model, js, jsp, req);
            if (jsp != null && jsp.length() > 0) {
                return "player/" + jsp;
            }        
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return "player/zooatomics";
    }    
    
    /**
     * original url: /durp
     * redirect to:  #!landing=durp  
     */    
    @RequestMapping("{name}")
    public String zooatomics(
            @PathVariable("name") String name,
            @RequestParam(value="jsp",required=false) String jsp,
            @RequestParam(value="js",required=false) String js,
            @RequestParam(value="mso",required=false) String mso,
            HttpServletRequest req, HttpServletResponse resp, Model model) {
        PlayerService service = new PlayerService();
        if (name != null) {
            if (name.matches("[a-zA-Z].+")) {
                NnUser user = new NnUserManager().findByProfileUrl(name, 1);
                if (user != null) {
                    log.info("user enter from curator brand url:" + name);
                    name = "#!" + user.getProfile().getProfileUrl();
                } else {
                    log.info("invalid curator brand url:" + name);
                    name = "";
                }
            }
            String url = service.rewrite(req) + name;
            log.info("redirect url:" + url);
            return "redirect:/" + url;
        }
        //should never hit here, intercept by index
        service.preparePlayer(model, js, jsp, req);
        return "player/zooatomics";
    }
        
    // TODO: may not be used anymore
    @RequestMapping("/redirect/{name}/view")
    public String brandView(
    		        Model model,
    		        @PathVariable("name") String name,
            		@RequestParam(value="ch", required=false) String ch,
                    @RequestParam(value="ep", required=false) String ep,    		
    				HttpServletRequest req, 
    		        HttpServletResponse resp) {    		       
    	String storeUrl = "market://details?id=tv.tv9x9.player";
    	
    	PlayerService service = new PlayerService();
    	String reportUrl = service.getGAReportUrl(ch, ep, name);
    	log.info("reportUrl:" + reportUrl); 
    	
        String fliprStr = service.getFliprUrl(ch, ep, name, req);
        MsoManager msoMngr = new MsoManager();
        
        Mso mso = msoMngr.findByName(name);
        MsoConfig config = new MsoConfigManager().findByMsoAndItem(mso, MsoConfig.STORE_ANDROID);
        if (config != null) {
        	storeUrl = config.getValue();
        }
        /*
    	if (name.equals(Mso.NAME_CTS)) {
    		storeUrl = "market://details?id=tw.com.cts.player";
    	}
    	*/
    	
        model.addAttribute("fliprUrl", fliprStr);    	
    	model.addAttribute("reportUrl", reportUrl);
    	model.addAttribute("storeUrl", storeUrl);
    	model.addAttribute("brandName", mso.getName());
    	if (config != null) {
    		return "player/ios_brand";
    	}
    	/*
        if (name.equals(Mso.NAME_CTS)) {
        	return "player/ios_cts";
        }
        */
        return "player/ios";

    }
    
    /**
     * original url: view?channel=x&episode=y
     * redirect to:  #!ch=x!ep=y  
     */
    @RequestMapping("view")
    public String view(@RequestParam(value="mso",required=false) String msoName, 
                       HttpServletRequest req, HttpServletResponse resp, Model model, 
                       @RequestParam(value="channel", required=false) String channel,
                       @RequestParam(value="episode", required=false) String episode,
                       @RequestParam(value="js",required=false) String js,
                       @RequestParam(value="jsp",required=false) String jsp,
                       @RequestParam(value="ch", required=false) String ch,
                       @RequestParam(value="ep", required=false) String ep) {
        //additional params
        PlayerService service = new PlayerService();
        MsoManager msoMngr = new MsoManager();
        ApiContext context = new ApiContext(req);
        
        Mso mso = msoMngr.getByNameFromCache(msoName);
        if (mso == null) {
            mso = msoMngr.getByNameFromCache(Mso.NAME_9X9);;
        }
        String cid = channel != null ? channel : ch;
        String pid = episode != null ? episode : ep;
                
        if (service.isAndroid(req) || service.isIos(req)) {
            String mobilePromotionUrl = "http://" + context.getAppDomain()
                                      + "/mobile/#/playback/" + cid
                                      + (pid == null ? "" : "/" + pid);
            log.info("mobile promotion url = " + mobilePromotionUrl);
            return "redirect:" + mobilePromotionUrl;
        } else if (mso.getType() == Mso.TYPE_FANAPP) {            
            log.info("Fan app sharing");
            MsoConfig androidConfig = new MsoConfigManager().findByMsoAndItem(mso, MsoConfig.STORE_ANDROID);
            MsoConfig iosConfig = new MsoConfigManager().findByMsoAndItem(mso, MsoConfig.STORE_IOS);
        	String androidStoreUrl = "market://details?id=tv.tv9x9.player";
        	String iosStoreUrl = "https://itunes.apple.com/app/9x9.tv/id443352510?mt=8";        	
            if (androidConfig != null) {
            	androidStoreUrl = androidConfig.getValue();
            }
            if (iosConfig != null) {
            	iosStoreUrl = iosConfig.getValue();
            }
        	String reportUrl = service.getGAReportUrl(ch, ep, mso.getName());
        	model.addAttribute("reportUrl", reportUrl);
        	model.addAttribute("androidStoreUrl", androidStoreUrl);
        	model.addAttribute("iosStoreUrl", iosStoreUrl);
        	model.addAttribute("brandName", mso.getName());
        	return "player/fanapp";
        } else {            
            model = service.prepareBrand(model, mso.getName(), resp);
            model = service.prepareChannel(model, cid, mso.getName(), resp);
            model = service.prepareEpisode(model, pid, mso.getName(), resp);
            
            String playerPromotionUrl = "http://" + context.getAppDomain()
                                      + "/tv#/promotion/" + cid
                                      + (pid == null ? "" : "/" + pid);
            log.info("player promotion url = " + playerPromotionUrl);
            
            String brandSharingUrl = "http://" + context.getAppDomain() + "/view?mso="
                    + context.getMso().getName() + "&ch=" + cid
                    + (pid == null ? "" : "&ep=" + pid);
            log.info("brand sharing url = " + brandSharingUrl);
            
            model.addAttribute("playerPromotionUrl", NnStringUtil.htmlSafeChars(playerPromotionUrl));
            model.addAttribute(PlayerService.META_URL, NnStringUtil.htmlSafeChars(brandSharingUrl));
            
            return "player/crawled";
        }
    }
    
    @RequestMapping("android")
    public String android() {
        log.info("android landing");
        return "player/android";
    }
    
    @RequestMapping("flview")
    public String flview(@RequestParam(value="mso",required=false) String mso, 
                       HttpServletRequest req, HttpServletResponse resp, Model model,                       
                       @RequestParam(value="channel", required=false) String channel,
                       @RequestParam(value="episode", required=false) String episode,
                       @RequestParam(value="js",required=false) String js,
                       @RequestParam(value="jsp",required=false) String jsp,
                       @RequestParam(value="ch", required=false) String ch,
                       @RequestParam(value="ep", required=false) String ep) {
        PlayerService service = new PlayerService();
        try {
            String queryStr = req.getQueryString();
            log.info("query str:" + queryStr);
            if (queryStr != null && queryStr.contains("fb")) {
                log.info("extra stuff from fb" + queryStr);
                String cid = channel != null ? channel : ch;
                String pid = episode != null ? episode : ep;
                boolean isIos = service.isIos(req);
                if (isIos) {
                    //pid = service.findFirstSubepisodeId(pid);
                    String iosStr = service.getFliprUrl(cid, pid, mso, req);
                    model.addAttribute("fliprUrl", iosStr);
                    return "player/ios";
                }
                String str = js != null ? "js=" + js : "";
                str += str.length() != 0 ? "&" : "";
                str += cid != null ? "#!ch=" + cid : "";
                str += cid != null ? "" : "#";
                str += pid != null ? "!ep=" + pid : "";
                log.info("redirect to url:" + str);
                return "redirect:/" + str;
            }
            String cid = channel != null ? channel : ch;
            String pid = episode != null ? episode : ep;
            model = service.prepareBrand(model, mso, resp);
            model = service.preparePlayer(model, js, jsp, req);
            model = service.prepareChannel(model, cid, mso, resp);
            model = service.prepareEpisode(model, pid, mso, resp);
            if (jsp != null && jsp.length() > 0) {
                return "player/" + jsp;
            }        
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return "player/zooatomics";        
    }
    
    @RequestMapping("support")
    public String support() {
        return "general/support";
    }    

    @RequestMapping("tanks")
    public String tanks() {        
        return "player/tanks";
    }
    
    /*
     * used for dns redirect watch dog 
     */
    @RequestMapping("9x9/wd")
    public ResponseEntity<String> watchdog() {        
        return NnNetUtil.textReturn("OK");
    }
    
    /*
    @RequestMapping("flipr")
    public String flipr(HttpServletRequest request,) {        
        Locale locale = request.getLocale();
        if((locale.getCountry()=="TW")||(locale.getCountry()=="CN")) {
            return "redirect:flipr/tw/";
        } else {
            return "redirect:flipr/en/";
        }
    }
    */
    
}
