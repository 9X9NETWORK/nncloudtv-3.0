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
import com.nncloudtv.model.NnUser;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.service.PlayerService;

@Controller
@RequestMapping("")
public class PlayerController {

    protected static final Logger log = Logger.getLogger(PlayerController.class.getName());
    
    @ExceptionHandler(Exception.class)
    public String exception(Exception e) {
        NnLogUtil.logException(e);
        return "error/exception";                
    }        
        
    @RequestMapping("10ft")
    public String tenft(@RequestParam(value="mso",required=false) String mso, HttpServletRequest req, HttpServletResponse resp, Model model,
            @RequestParam(value="jsp",required=false) String jsp,
            @RequestParam(value="js",required=false) String js) {
        try {
            PlayerService service = new PlayerService();
            model = service.prepareBrand(model, mso, resp);
            model = service.preparePlayer(model, js, jsp, req);            
            if (jsp != null && jsp.length() > 0) {
                return "player/" + jsp;
            }
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);            
        }            
        return "player/mini";
    }    
    
    @RequestMapping("tv")
    public String tv(@RequestParam(value="mso",required=false) String mso, 
            HttpServletRequest req, HttpServletResponse resp, Model model,
            @RequestParam(value="jsp",required=false) String jsp,
            @RequestParam(value="js",required=false) String js) {
        try {
            PlayerService service = new PlayerService();
            model = service.prepareBrand(model, mso, resp);
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
        if (name != null) {
            if (name.matches("[a-zA-Z].+")) {
                NnUser user = new NnUserManager().findByProfileUrl(name);
                if (user != null) {
                    log.info("user enter from curator brand url:" + name);
                    name = "#!" + user.getProfileUrl();
                } else {
                    log.info("invalid curator brand url:" + name);
                    name = "";
                }
            }
            PlayerService service = new PlayerService();
            String url = service.rewrite(req) + name;
            log.info("redirect url:" + url);
            return "redirect:/" + url;
        }
        return "player/zooatomics";
    }
    
    /**
     * original url: view?channel=x&episode=y
     * redirect to:  #!ch=x!ep=y  
     */
    @RequestMapping("view")
    public String view(@RequestParam(value="mso",required=false) String mso, 
                       HttpServletRequest req, HttpServletResponse resp, Model model, 
                       @RequestParam(value="channel", required=false) String channel,
                       @RequestParam(value="episode", required=false) String episode,
                       @RequestParam(value="js",required=false) String js,
                       @RequestParam(value="jsp",required=false) String jsp,
                       @RequestParam(value="ch", required=false) String ch,
                       @RequestParam(value="ep", required=false) String ep) {
        //additional params
        PlayerService service = new PlayerService();
        boolean isIos = service.isIos(req);
        if (isIos) {
            String cid = channel != null ? channel : ch;
            String pid = episode != null ? episode : ep;
            pid = service.findFirstSubepisodeId(pid);
            String iosStr = service.getRedirectIosUrl(cid, pid, req);
            model.addAttribute("fliprUrl", iosStr);
            return "player/ios";
        }
        String str = service.getQueryString(req, channel, episode, ch, ep);
        return "redirect:/" + str;
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
                    pid = service.findFirstSubepisodeId(pid);
                    String iosStr = service.getRedirectIosUrl(cid, pid, req);
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
            model = service.prepareChannel(model, cid, resp);
            model = service.prepareEpisode(model, pid, resp);
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
