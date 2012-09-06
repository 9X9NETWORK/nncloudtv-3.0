package com.nncloudtv.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.service.PlayerApiService;

@Controller
@RequestMapping("fb")
public class FacebookController {
    protected static final Logger log = Logger.getLogger(FacebookController.class.getName());
    
    @RequestMapping("login")    
    public String login(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam(value="stage", required=false) String stage,
            @RequestParam(value="code", required=false) String code,
            @RequestParam(value="error", required=false) String error,
            @RequestParam(value="errorReason", required=false) String errorReason,
            @RequestParam(value="errorDescription", required=false) String errorDescription,
            @RequestParam(value="accessToken", required=false) String accessToken,
            @RequestParam(value="expirse", required=false) String expires                    
            ) throws IOException {
        NnNetUtil.logUrl(req);
        log.info("---(fb/login) fb callback---");                                                      
        log.info("error:" + error + ";errorReason:" + errorReason + 
                 ";errorDescription:" + errorDescription + 
                 ";accessToken:" + accessToken + ";stage:" + stage);
        if (code != null && accessToken == null) {
            String[] data = new FacebookLib().getOAuthAccessToken(code);
            System.out.println("---- (fb/login)back to /fb/login ---");
            if (data[0] != null) {               
                log.info("---- (fb/login)continue to signup with fb account ---");
                new PlayerApiService().fbSignup(data[0], data[1], req, resp);
                log.info("---- (fb/login)finish signup with fb account ---");
            }                         
        }
        System.out.println("---- (fb/login) prepare to redirect ---");
        return "redirect:/";
        //return "redirect:/hello/world";
        //return "redirect:/playerAPI/brandInfo";
    }
}