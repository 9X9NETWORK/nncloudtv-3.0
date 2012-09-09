package com.nncloudtv.web.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserLibrary;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnUserLibraryManager;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.service.NnUserPrefManager;
import com.nncloudtv.web.json.facebook.FacebookPage;

@Controller
@RequestMapping("api")
public class ApiUser extends ApiGeneric {

    protected static Logger log = Logger.getLogger(ApiUser.class.getName());
    
    @RequestMapping(value = "users/{userId}", method = RequestMethod.GET)
    public @ResponseBody
    NnUser userInfo(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("userId") String userIdStr, @RequestParam(required = false) Short shard) {
        
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        if (shard == null) {
            shard = 0;
        }
        
        NnUserManager userMngr = new NnUserManager();
        
        return userMngr.findById(userId, shard);
    }
    
    @RequestMapping(value = "users/{userId}", method = RequestMethod.PUT)
    public @ResponseBody
    NnUser userInfoUpdate(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("userId") String userIdStr, @RequestParam(required = false) Short shard) {
        
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        if (shard == null) {
            shard = 0;
        }
        
        NnUserManager userMngr = new NnUserManager();
        
        NnUser user = userMngr.findById(userId, shard);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name != null && name.length() > 0){
            
            user.setName(NnStringUtil.htmlSafeAndTrucated(name));
        }
        
        // intro
        String intro = req.getParameter("intro");
        if (intro != null) {
            
            user.setIntro(NnStringUtil.htmlSafeAndTrucated(intro));
        }
        
        // imageUrl
        String imageUrl = req.getParameter("imageUrl");
        if (imageUrl != null) {
            
            // TODO: check for image url
            user.setImageUrl(imageUrl);
        }
        
        // lang
        String lang = req.getParameter("lang");
        if (lang != null) {
            
            if (NnStringUtil.validateLangCode(lang) == null) {
                log.warning("lang is not valid");
            } else {
                user.setLang(lang);
            }
        }
        
        return userMngr.save(user);
    }
    
    @RequestMapping(value = "users/{userId}/my_{repo}", method = RequestMethod.GET)
    public @ResponseBody
    List<NnUserLibrary> userUploads(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("userId") String userIdStr,
            @PathVariable("repo") String repo) {

        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnUserManager userMngr = new NnUserManager();
        
        NnUser user = userMngr.findById(userId);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        NnUserLibraryManager libMngr = new NnUserLibraryManager();
        
        short type = NnUserLibrary.TYPE_UPLOADS;
        if (repo.equalsIgnoreCase("library")) {
            type = NnUserLibrary.TYPE_YOUTUBE;
        }
        
        return libMngr.findByUserAndType(user, type);
    }
    
    @RequestMapping(value = { "users/{userId}/my_uploads/{id}", "users/{userId}/my_library/{id}" }, method = RequestMethod.DELETE)
    public @ResponseBody
    String userUploadsDelete(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("userId") String userIdStr,
            @PathVariable("id") String idStr) {        
        
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnUserManager userMngr = new NnUserManager();
        
        NnUser user = userMngr.findById(userId);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long id = null;
        
        try {
            id = Long.valueOf(idStr);
        } catch (NumberFormatException e) {
        }
        if (id == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnUserLibraryManager libMngr = new NnUserLibraryManager();
        NnUserLibrary lib = libMngr.findById(id);
        if (lib == null) {
            return "Item Not Found";
        }
        
        libMngr.delete(lib);
        
        return "OK";
    }
    
    @RequestMapping(value = "users/{userId}/my_{repo}", method = RequestMethod.POST)
    public @ResponseBody
    String userUploadsCreate(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("userId") String userIdStr,
            @PathVariable("repo") String repo) {
        
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnUserManager userMngr = new NnUserManager();
        
        NnUser user = userMngr.findById(userId);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        NnUserLibraryManager libMngr = new NnUserLibraryManager();
        
        // name, url
        String name = req.getParameter("name");
        String url = req.getParameter("fileUrl");
        String imageUrl = req.getParameter("imageUrl");
        
        if (name == null || url == null) {
            
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        name = NnStringUtil.htmlSafeAndTrucated(name);
        // TODO: check for url
        
        short type = NnUserLibrary.TYPE_UPLOADS;
        if (repo.equalsIgnoreCase("library")) {
            type = NnUserLibrary.TYPE_YOUTUBE;
            String pattern = "^http:\\/\\/www\\.youtube\\.com\\/watch\\?v=[^&]+$";
            if (!url.matches(pattern)) {
                
                badRequest(resp, "Invalid YouTube URL");
                return null;
            }
        }
        
        NnUserLibrary lib = libMngr
                .findByUserAndTypeAndFileUrl(user, type, url);
        if (lib == null) {
            lib = new NnUserLibrary(name, url, type);
        }
        lib.setUserIdStr(user.getIdStr());
        if (imageUrl != null) {
            // TODO: check for imageUrl
            lib.setImageUrl(imageUrl);
        } else {
            lib.setImageUrl(NnChannel.IMAGE_WATERMARK_URL);
        }
        
        libMngr.save(lib);
        
        return "OK";
    }
    
    @RequestMapping(value = "users/{userId}/channels", method = RequestMethod.GET)
    public @ResponseBody List<NnChannel> userChannelList(HttpServletRequest req, HttpServletResponse resp, @PathVariable("userId") String userIdStr) {
        
        List<NnChannel> result = new ArrayList<NnChannel>();
        
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findById(userId);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        result = channelMngr.findByUser(user, 0, false);
        
        return result;
    }
    
    @RequestMapping(value = "users/{userId}/channels", method = RequestMethod.POST)
    public @ResponseBody NnChannel userChannelCreate(HttpServletRequest req, HttpServletResponse resp, @PathVariable("userId") String userIdStr) {
        
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findById(userId);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name == null || name.isEmpty()) {
            
            badRequest(resp, "At Least Provide Channel Name");
            return null;
        }
        name = NnStringUtil.htmlSafeAndTrucated(name);
        
        // intro
        String intro = req.getParameter("intro");
        if (intro != null && intro.length() > 0) {
            intro = NnStringUtil.htmlSafeAndTrucated(intro);
        }
        
        NnChannel channel = new NnChannel(name, intro, null /* thumbnail is generated by back-end */);
        channel.setContentType(NnChannel.CONTENTTYPE_MIXED);
        channel.setPublic(false);
        channel.setPoolType(NnChannel.POOL_BASE);
        channel.setUserIdStr(user.getShard(), user.getId());
        
        // lang
        String lang = req.getParameter("lang");
        if (lang != null && NnStringUtil.validateLangCode(lang) != null) {
            channel.setLang(lang);
        } else {
            channel.setLang(LangTable.LANG_EN);
        }
        
        // isPublic
        String isPublicStr = req.getParameter("isPublic");
        if (isPublicStr != null) {
            Boolean isPublic = Boolean.valueOf(isPublicStr);
            channel.setPublic(isPublic);
        }
        
        // tag
        String tag = req.getParameter("tag");
        if (tag != null) {
            channel.setTag(tag);
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        
        return channelMngr.save(channel);
    }
    
    @RequestMapping(value = "users/{userId}/channels/{channelId}", method = RequestMethod.POST)
    public @ResponseBody
    String userChannelUnlink(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("userId") String userIdStr,
            @PathVariable("channelId") String channelIdStr) {        
        
        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
        }
        if (channelId == null) {
            badRequest(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            
            notFound(resp, "Channel Not Found");
            return null;
        }
        
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            
            badRequest(resp, INVALID_PATH_PARAMETER);
            return null;
        } else if (userId != channel.getUserId()) {
            
            return "Not The Channel Owner";
        }
        
        channel.setUserIdStr(null); // unlink
        channelMngr.save(channel);
        
        return "OK";
    }
    
    @RequestMapping(value = "users/{userId}/sns_auth/facebook", method = RequestMethod.POST)
    public @ResponseBody
    String facebookAuthUpdate(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("userId") String userIdStr) {        
        
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findById(userId);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        String fbUserId = req.getParameter("userId");
        String accessToken = req.getParameter("accessToken");
        if (fbUserId == null || accessToken == null) {
            
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        NnUserPrefManager prefMngr = new NnUserPrefManager();
        NnUserPref userPref = null;
        
        // fbUserId
        userPref = prefMngr.findByUserAndItem(user, NnUserPref.FB_USER_ID);
        if (userPref != null) {
            userPref.setValue(fbUserId);
        } else {
            userPref = new NnUserPref(user, NnUserPref.FB_USER_ID, fbUserId);
        }
        prefMngr.save(user, userPref);
        
        // accessToken
        userPref = prefMngr.findByUserAndItem(user, NnUserPref.FB_TOKEN);
        if (userPref != null) {
            userPref.setValue(accessToken);
        } else {
            userPref = new NnUserPref(user, NnUserPref.FB_TOKEN, accessToken);
        }
        prefMngr.save(user, userPref);
        
        return "OK";
    }
    
    @RequestMapping(value = "users/{userId}/sns_auth/facebook", method = RequestMethod.DELETE)
    public @ResponseBody
    String facebookAuthDelete(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("userId") String userIdStr) {
        
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findById(userId);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        NnUserPrefManager prefMngr = new NnUserPrefManager();
        NnUserPref userPref = null;
        
        // fbUserId
        userPref = prefMngr.findByUserAndItem(user, NnUserPref.FB_USER_ID);
        if (userPref != null) {
            prefMngr.delete(user, userPref);
        }
        
        // accessToken
        userPref = prefMngr.findByUserAndItem(user, NnUserPref.FB_TOKEN);
        if (userPref != null) {
            prefMngr.delete(user, userPref);
        }
        
        return "OK";
    }
    
    @RequestMapping(value = "users/{userId}/sns_auth/facebook", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> facebookAuth(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("userId") String userIdStr) {
        
        Long userId = null;
        try {
            userId = Long.valueOf(userIdStr);
        } catch (NumberFormatException e) {
        }
        if (userId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findById(userId);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        NnUserPrefManager prefMngr = new NnUserPrefManager();
        NnUserPref userPref = null;
        Map<String, Object> result = new TreeMap<String, Object>();
        String fbUserId = null;
        String accessToken = null;
        
        // fbUserId
        userPref = prefMngr.findByUserAndItem(user, NnUserPref.FB_USER_ID);
        if (userPref != null) {
            fbUserId = userPref.getValue();
            result.put("userId", fbUserId);
        }
        
        // accessToken
        userPref = prefMngr.findByUserAndItem(user, NnUserPref.FB_TOKEN);
        if (userPref != null) {
            accessToken = userPref.getValue();
            result.put("accessToken", accessToken);
        }
        
        if (fbUserId != null && accessToken != null) {
            List<FacebookPage> pages = FacebookLib.populatePageList(fbUserId, accessToken);
            result.put("pages", pages);
        } else {
            return null;
        }
        
        return result;
    }
}
