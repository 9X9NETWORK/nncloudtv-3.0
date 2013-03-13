package com.nncloudtv.web.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SignatureException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.client.util.ArrayMap;
import com.nncloudtv.lib.AmazonLib;
import com.nncloudtv.lib.AuthLib;
import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.service.MsoConfigManager;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.web.json.facebook.FBPost;

@Controller
@RequestMapping("api")
public class ApiMisc extends ApiGeneric {
	
	protected static Logger log = Logger.getLogger(ApiMisc.class.getName());
	
	@RequestMapping(value = "s3/attributes", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> s3Attributes(HttpServletRequest req, HttpServletResponse resp) {
		
	    NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        }
		
		String prefix = req.getParameter("prefix");
		String type = req.getParameter("type");
		String acl = req.getParameter("acl");
		long size = 0;
		
		try {
			String sizeStr = req.getParameter("size");
			Long sizeL = Long.valueOf(sizeStr);
			size = sizeL.longValue();
		} catch (NumberFormatException e) {
		}
		
		Map<String, String> result = new TreeMap<String, String>();
		
		if (size == 0 || prefix == null || type == null || acl == null ||
		    (!type.equals("audio") && !type.equals("image")) ||
		    (!acl.equals("public-read"))) {
			
			badRequest(resp, INVALID_PARAMETER);
			return result;
		} 
		
		String bucket = MsoConfigManager.getS3UploadBucket();
		String policy = AmazonLib.buildS3Policy(bucket, acl, type, size);
		String signature = "";
		try {
			signature = AmazonLib.calculateRFC2104HMAC(policy);
		} catch (SignatureException e) {
		}
		
		result.put("bucket", bucket);
		result.put("policy", policy);
		result.put("signature", signature);
		result.put("id", AmazonLib.AWS_ID);
		
		return result;
	}
	
	@RequestMapping(value = "login", method = RequestMethod.DELETE)
	public @ResponseBody String logout(HttpServletRequest req, HttpServletResponse resp) {
		
		CookieHelper.deleteCookie(resp, CookieHelper.USER);
		CookieHelper.deleteCookie(resp, CookieHelper.GUEST);
		
		okResponse(resp);
        return null;
	}
	
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public @ResponseBody NnUser login(HttpServletRequest req, HttpServletResponse resp) {
		
		String token = req.getParameter("token");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		
		NnUserManager userMngr = new NnUserManager();
		NnUser user = null;
		
		if (token != null) {
			
			log.info("token = " + token);
			
			user = userMngr.findByToken(token);
			
		} else if (email != null && password != null) {
			
			log.info("email = " + email + ", password = xxxxxx");
			
			user = userMngr.findAuthenticatedUser(email, password, req);
			if (user != null) {
				CookieHelper.setCookie(resp, CookieHelper.USER, user.getToken());
			}
			
		} else {
			badRequest(resp, MISSING_PARAMETER);
		}
		
		if (user == null) {
		    nullResponse(resp);
		    return null;
		}
		
		return userMngr.purify(user);
	}
	
	@RequestMapping("echo")
	public @ResponseBody Map<String, String> echo(HttpServletRequest req, HttpServletResponse resp) {
		
		@SuppressWarnings("unchecked")
		Map<String, String[]> names = req.getParameterMap();
		Map<String, String> result = new TreeMap<String, String>();
		
		for (String name : names.keySet()) {
			
			String value = names.get(name)[0];
			log.info(name + " = " + value);
			result.put(name, value);
		}
		
		if (result.isEmpty()) {
			
			badRequest(resp, MISSING_PARAMETER);
			
			return null;
		}
		
		if(req.getMethod().equalsIgnoreCase("POST")) {
			resp.setStatus(201);
		}
		
		return result;
	}
	
    @RequestMapping("i18n")
    public @ResponseBody
    Map<String, String> i18n(HttpServletRequest req, HttpServletResponse resp) {
        
        Map<String, String> result = new ArrayMap<String, String>();
        Properties properties = new Properties();
        
        String lang = req.getParameter("lang");
        if (lang == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        } else if (NnStringUtil.validateLangCode(lang) == null) {
            badRequest(resp, "Invalid lang");
            return null;
        }
        
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(
                    LangTable.EN_PROPERTIE_FILE));
            
            Set<String> keys = properties.stringPropertyNames();
            
            if (lang.equals(LangTable.LANG_ZH)) {
                Properties zhProps = new Properties();
                zhProps.load(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(
                        LangTable.ZH_PROPERTIE_FILE), "UTF-8"));
                
                for (String key : keys) {
                    
                    result.put(properties.getProperty(key), zhProps.getProperty(key));
                    log.info(zhProps.getProperty(key));
                }
            } else {
                for (Object key : keys) {
                    String sentence = properties.getProperty((String) key);
                    result.put(sentence, sentence);
                }
            }
        } catch (IOException e) {
            
            NnLogUtil.logException(e);
            internalError(resp, e);
            
            return null;
        }
        resp.setContentType(PLAIN_TEXT_UTF8);
        resp.addDateHeader("Expires", System.currentTimeMillis() + 3600000);
        resp.addHeader("Cache-Control", "private, max-age=3600");
        return result;
    }
	
	@RequestMapping("*")
	public @ResponseBody String blackHole(HttpServletRequest req, HttpServletResponse resp) {
		
		String path = req.getServletPath();
		String message = null;
		Date now = new Date();
		long rand = Math.round(Math.random() * 1000);
		byte[] salt = AuthLib.generateSalt();
		
		try {
			
			log.info("path = " + path);
			String inbound = AmazonLib
			        .decodeS3Token(AmazonLib.AWS_TOKEN, now, salt, rand)
			        .toLowerCase().replace("-", "");
			String outbound = AmazonLib.decodeS3Token(AmazonLib.S3_TOKEN, now,
			        salt, rand);
			
			if (path.indexOf(inbound) > 0) {
				message = AmazonLib.decodeS3Token(AmazonLib.S3_CONTEXT_CODE,
				        now, salt, rand);
			} else if (path.indexOf(outbound) > 0) {
				message = AmazonLib.decodeS3Token(AmazonLib.S3_EXT, now, salt,
				        rand);
			} else {
				message = BLACK_HOLE;
			}
		} catch (IOException e) {
            internalError(resp);
            return null;
        }
        
        if (message.equals(BLACK_HOLE)) {
            notFound(resp, message);
            return null;
        }
        
        resp.setContentType(PLAIN_TEXT_UTF8);
        
        return message + "\n";
    }
	
    @RequestMapping(value = "sns/facebook", method = RequestMethod.POST)
    public @ResponseBody String postToFacebook(HttpServletRequest req, HttpServletResponse resp) {
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        }
        
        FBPost fbPost = new FBPost();
        
        // message
        String message = req.getParameter("message");
        if (message != null){
            fbPost.setMessage(message);
        }
        
        // picture
        String picture = req.getParameter("picture");
        if (picture != null){
            fbPost.setPicture(picture);
        }
        
        // link
        String link = req.getParameter("link");
        if (link != null){
            fbPost.setLink(link);
        }
        
        // name
        String name = req.getParameter("name");
        if (name != null){
            fbPost.setName(name);
        }
        
        // caption
        String caption = req.getParameter("caption");
        if (caption != null){
            fbPost.setCaption(caption);
        }
        
        // description
        String description = req.getParameter("description");
        if (description != null){
            fbPost.setDescription(description);
        }
        
        // facebookId
        if (verifiedUser.isFbUser()) {
            fbPost.setFacebookId(verifiedUser.getEmail());
        } else {
            String facebookId = req.getParameter("facebookId");
            if (facebookId != null){
                fbPost.setFacebookId(facebookId);
            }
        }
         
        // accessToken
        if (verifiedUser.isFbUser()) {
            fbPost.setAccessToken(verifiedUser.getToken());
        } else {
            String accessToken = req.getParameter("accessToken");
            if (accessToken != null){
                fbPost.setAccessToken(accessToken);
            }
        }
        
        if(fbPost.getFacebookId() == null || fbPost.getAccessToken() == null) {
            return "not link to facebook";
        }
        
        try {
            log.info(fbPost.toString());
            FacebookLib.postToFacebook(fbPost);
        } catch (IOException e) {
            NnLogUtil.logException(e);
            internalError(resp, e);
            return null;
        }
        
        okResponse(resp);
        return null;
    }

}
