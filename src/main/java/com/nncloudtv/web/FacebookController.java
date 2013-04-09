package com.nncloudtv.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.service.PlayerApiService;
import com.nncloudtv.web.json.facebook.FBPost;

@Controller
@RequestMapping("fb")
public class FacebookController {
    protected static final Logger log = Logger.getLogger(FacebookController.class.getName());
    
    @RequestMapping("login")    
    public String login(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam(value="subChannel", required=false) String subChannel,            
            @RequestParam(value="mso", required=false) String mso,
            @RequestParam(value="stage", required=false) String stage,
            @RequestParam(value="code", required=false) String code,
            @RequestParam(value="error", required=false) String error,
            @RequestParam(value="errorReason", required=false) String errorReason,
            @RequestParam(value="errorDescription", required=false) String errorDescription,
            @RequestParam(value="accessToken", required=false) String accessToken,
            @RequestParam(value="expirse", required=false) String expires                    
            ) throws IOException {
        log.info("FACEBOOK: (login) - back from facebook page(mso):" + mso);
        NnNetUtil.logUrl(req);
        String userCookie = CookieHelper.getCookie(req, CookieHelper.USER);
        log.info("FACEBOOK: (login) - our cookie:" + userCookie);                                                      
        log.info("FACEBOOK: (login) error:" + error + ";errorReason:" + errorReason + 
                            ";errorDescription:" + errorDescription + 
                            ";accessToken:" + accessToken + ";stage:" + stage);
        if (code != null && accessToken == null) {
            String[] data = new FacebookLib().getOAuthAccessToken(code, mso);
            log.info("FACEBOOK: (login) back from access token request");
            if (data[0] != null) {               
                log.info("FACEBOOK: (login) going to use data from facebook");
                new PlayerApiService().fbWebSignup(data[0], data[1], mso, req, resp);
                log.info("FACEBOOK: (login) have used data from facebook to create a 9x9 account");
            }                         
        }
        String redirectPath = "/";
        if (subChannel != null)
            redirectPath = "#!subCh=" + subChannel;
        log.info("FACEBOOK: (login) last step redirect to 9x9 player:" + redirectPath);
        return "redirect:" + redirectPath;
    }
    
    /**
    * Post the FBPost object to facebook
    * 
    * @param fbPost FBPost object
    */
    @RequestMapping("postToFacebook")
    public @ResponseBody void postToFacebook(@RequestBody FBPost fbPost, HttpServletRequest req) {
        try {
            log.info("server name: " + req.getServerName());
            fbPost.setLink(fbPost.getLink().replaceFirst("localhost", req.getServerName()));
            log.info(fbPost.toString());
            FacebookLib.postToFacebook(fbPost);
        } catch (IOException e) {
            NnLogUtil.logException(e);
        }
    }
     
}