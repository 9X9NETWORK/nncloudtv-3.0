package com.nncloudtv.web.api;

import java.util.ArrayList;
import java.util.Collections;
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
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryMap;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserLibrary;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.service.CategoryManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnEpisodeManager;
import com.nncloudtv.service.NnProgramManager;
import com.nncloudtv.service.NnUserLibraryManager;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.service.NnUserPrefManager;
import com.nncloudtv.web.json.cms.UserFavorite;
import com.nncloudtv.web.json.facebook.FacebookPage;

@Controller
@RequestMapping("api")
public class ApiUser extends ApiGeneric {

    protected static Logger log = Logger.getLogger(ApiUser.class.getName());
    
    //@RequestMapping(value = "users/{userId}", method = RequestMethod.GET)
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
        
        NnUserManager userMngr = new NnUserManager();
        NnUser user = null;
        
        if (shard == null) {
            user = userMngr.findById(userId);
        } else {
            user = userMngr.findById(userId, (short) shard);
        }
        
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        return userMngr.purify(user);
    }
    
    //@RequestMapping(value = "users/{userId}", method = RequestMethod.PUT)
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
            
            user.setName(NnStringUtil.htmlSafeAndTruncated(name));
        }
        
        // intro
        String intro = req.getParameter("intro");
        if (intro != null) {
            
            user.setIntro(NnStringUtil.htmlSafeAndTruncated(intro));
        }
        
        // imageUrl
        String imageUrl = req.getParameter("imageUrl");
        if (imageUrl != null) {
            
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
        
        user = userMngr.save(user);
        
        return userMngr.purify(user);
    }
    
    @RequestMapping(value = "users/{userId}/my_favorites", method = RequestMethod.GET)
    public @ResponseBody
    List<UserFavorite> userFavorites(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("userId") String userIdStr) {
        
        List<UserFavorite> results = new ArrayList<UserFavorite>();
        
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
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUser.getId() != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        NnProgramManager programMngr = new NnProgramManager();
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        List<NnProgram> favorites = programMngr.getUserFavorites(user);
        
        log.info("my favorites count = " + favorites.size());
        
        for (NnProgram program : favorites) {
            
            UserFavorite favorite = new UserFavorite();
            
            if (program.getContentType() == NnProgram.CONTENTTYPE_REFERENCE) {
                
                NnEpisode episode = episodeMngr.findById(program.getStorageIdInt());
                if (episode == null) {
                    continue;
                }
                
                favorite.setImageUrl(episode.getImageUrl());
                favorite.setName(episode.getName());
                favorite.setDuration(episode.getDuration());
                favorite.setPublishDate(episode.getPublishDate());
                favorite.setCntView(episode.getCntView());
                favorite.setPublic(episode.isPublic());
                favorite.setPlaybackUrl(NnStringUtil.getEpisodePlaybackUrl(episode.getChannelId(), episode.getId()));
                
            } else {
                
                favorite.setImageUrl(program.getImageUrl());
                favorite.setName(program.getName());
                favorite.setDuration(program.getDurationInt());
                favorite.setPublishDate(program.getPublishDate());
                favorite.setCntView(program.getCntView());
                favorite.setPublic(program.isPublic());
                favorite.setPlaybackUrl(NnStringUtil.getProgramPlaybackUrl(
                        program.getStorageId(),
                        YouTubeLib.getYouTubeVideoIdStr(program.getFileUrl())));
                
            }
            
            results.add(favorite);
        }
        
        return results;
    }
    
    @RequestMapping(value = "users/{userId}/my_{repo}", method = RequestMethod.GET)
    public @ResponseBody
    List<NnUserLibrary> userUploads(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("userId") String userIdStr,
            @PathVariable("repo") String repo) {
        
        List<NnUserLibrary> results;
        
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
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUser.getId() != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        NnUserLibraryManager libMngr = new NnUserLibraryManager();
        
        short type = NnUserLibrary.TYPE_UPLOADS;
        if (repo.equalsIgnoreCase("library")) {
            type = NnUserLibrary.TYPE_YOUTUBE;
        }
        
        String pageStr = req.getParameter("page");
        String rowsStr =  req.getParameter("rows");
        
        if (pageStr != null && rowsStr != null) {
            
            Short page = null;
            try {
                page = Short.valueOf(pageStr);
            } catch (NumberFormatException e) {
            }
            if (page == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            
            Short rows = null;
            try {
                rows = Short.valueOf(rowsStr);
            } catch (NumberFormatException e) {
            }
            if (rows == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            
            if (page < 1 || rows < 1) {
                
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            
            String filter = "userIdStr == '" + user.getIdStr() + "' && type == " + type;
            results = libMngr.list(page, rows, "updateDate", "desc", filter);
            
        } else {
            
            results = libMngr.findByUserAndType(user, type);
        }
        
        for (NnUserLibrary result : results) {
            result.setName(NnStringUtil.revertHtml(result.getName()));
        }
        
        return results;
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
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUser.getId() != user.getId()) {
            forbidden(resp);
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
        
        name = NnStringUtil.htmlSafeAndTruncated(name);
        
        short type = NnUserLibrary.TYPE_UPLOADS;
        if (repo.equalsIgnoreCase("library")) {
            type = NnUserLibrary.TYPE_YOUTUBE;
            if (!YouTubeLib.isVideoUrlNormalized(url)) {
                
                badRequest(resp, INVALID_YOUTUBE_URL);
                return null;
            }
        }
        
        NnUserLibrary lib = libMngr
                .findByUserAndTypeAndFileUrl(user, type, url);
        if (lib == null) {
            lib = new NnUserLibrary(name, url, type);
        }
        lib.setName(name);
        lib.setFileUrl(url);
        lib.setUserIdStr(user.getIdStr());
        if (imageUrl != null) {
            lib.setImageUrl(imageUrl);
        } else {
            lib.setImageUrl(NnChannel.IMAGE_WATERMARK_URL);
        }
        
        libMngr.save(lib);
        
        return "OK";
    }
    
    @RequestMapping(value = "users/{userId}/channels", method = RequestMethod.GET)
    public @ResponseBody
    List<NnChannel> userChannels(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("userId") String userIdStr) {
    
        List<NnChannel> results = new ArrayList<NnChannel>();
        
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
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUser.getId() != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        results = channelMngr.findByUserAndHisFavorite(user, 0, true);
        
        for (NnChannel channel : results) {
            
            channelMngr.populateMoreImageUrl(channel);
            
            channel.setName(NnStringUtil.revertHtml(channel.getName()));
            channel.setIntro(NnStringUtil.revertHtml(channel.getIntro()));
            
            if (channel.getContentType() == NnChannel.CONTENTTYPE_FAKE_FAVORITE) {
                channel.setContentType(NnChannel.CONTENTTYPE_FAVORITE); // To fake is necessary to fake like that
                channel.setMoreImageUrl(NnChannel.IMAGE_EPISODE_URL + "|" + NnChannel.IMAGE_EPISODE_URL + "|" + NnChannel.IMAGE_EPISODE_URL);
            }
        }
        
        Collections.sort(results, channelMngr.getChannelComparator("seq"));
        
        return results;
    }
    
    @RequestMapping(value = "users/{userId}/channels/sorting", method = RequestMethod.PUT)
    public @ResponseBody
    String userChannelsSorting(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("userId") String userIdStr) {
        
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
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUser.getId() != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        String channelIdsStr = req.getParameter("channels");
        if (channelIdsStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        String[] channelIdStrList = channelIdsStr.split(",");
        
        // the result should be same as userChannels but not include fake channel
        NnChannelManager channelMngr = new NnChannelManager();
        List<NnChannel> channels = channelMngr.findByUserAndHisFavorite(user, 0, true);
        for (NnChannel channel : channels) {
            if (channel.getContentType() == NnChannel.CONTENTTYPE_FAKE_FAVORITE) {
                channels.remove(channel);
                break;
            }
        }
        
        List<NnChannel> orderedChannels = new ArrayList<NnChannel>();
        List<Long> channelIdList = new ArrayList<Long>();
        List<Long> checkedChannelIdList = new ArrayList<Long>();
        for (NnChannel channel : channels) {
            channelIdList.add(channel.getId());
            checkedChannelIdList.add(channel.getId());
        }
        
        int index;
        for (String channelIdStr : channelIdStrList) {
            
            Long channelId = null;
            try {
                
                channelId = Long.valueOf(channelIdStr);
                
            } catch(Exception e) {
            }
            if (channelId != null) {
                index = channelIdList.indexOf(channelId);
                if (index > -1) {
                    orderedChannels.add(channels.get(index));
                    checkedChannelIdList.remove(channelId);
                }
            }
        }
        // parameter should contain all channelId
        if (checkedChannelIdList.size() != 0) {
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        
        short counter = 1;
        for (NnChannel channel : orderedChannels) {
            channel.setSeq(counter);
            counter++;
        }
        
        channelMngr.saveOrderedChannels(orderedChannels);
        
        return "OK";
    }
    
    @RequestMapping(value = "users/{userId}/channels", method = RequestMethod.POST)
    public @ResponseBody NnChannel userChannelCreate(HttpServletRequest req, HttpServletResponse resp, @PathVariable("userId") String userIdStr) {
        
        NnChannelManager channelMngr = new NnChannelManager();
        
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
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUser.getId() != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name == null || name.isEmpty()) {
            
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        name = NnStringUtil.htmlSafeAndTruncated(name);
        
        // intro
        String intro = req.getParameter("intro");
        if (intro != null && intro.length() > 0) {
            intro = NnStringUtil.htmlSafeAndTruncated(intro);
        }
        
        // imageUrl
        String imageUrl = req.getParameter("imageUrl");
        if (imageUrl == null) {
            imageUrl = NnChannel.IMAGE_WATERMARK_URL;
        }
        
        NnChannel channel = new NnChannel(name, intro, imageUrl);
        channel.setContentType(NnChannel.CONTENTTYPE_MIXED);
        channel.setPublic(false);
        channel.setStatus(NnChannel.STATUS_WAIT_FOR_APPROVAL);
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
        
        // sphere
        String sphere = req.getParameter("sphere");
        if (sphere != null && NnStringUtil.validateLangCode(sphere) != null) {
            channel.setSphere(sphere);
            
        } else {
            channel.setSphere(LangTable.LANG_EN);
        }
        
        channel = channelMngr.save(channel);
        
        long channelId = channel.getId();
        String categoryIdStr = req.getParameter("categoryId");
        if (categoryIdStr != null) {
            
            Long categoryId = null;
            try {
                categoryId = Long.valueOf(categoryIdStr);
            } catch (NumberFormatException e) {
            }
            if (categoryId == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            
            CategoryManager catMngr = new CategoryManager();
            Category category = catMngr.findById(categoryId);
            if (category == null) {
                badRequest(resp, "Category Not Found");
                return null;
            }
            
            // category mapping
            catMngr.save(new CategoryMap(categoryId, channelId));
            if (sphere != null && sphere.equalsIgnoreCase(LangTable.OTHER)) {
                
                Category twin = catMngr.findTwin(category);
                if (twin != null) {
                    catMngr.save(new CategoryMap(twin.getId(), channelId));
                }
            }
            channel.setCategoryId(categoryId);
        }
        
        channel.setName(NnStringUtil.revertHtml(channel.getName()));
        channel.setIntro(NnStringUtil.revertHtml(channel.getIntro()));
        
        return channel;
    }
    
    @RequestMapping(value = "users/{userId}/channels/{channelId}", method = RequestMethod.DELETE)
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
            notFound(resp, INVALID_PATH_PARAMETER);
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
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findById(userId);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUser.getId() != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        if (userId != channel.getUserId()) {
            //return "Not The Channel Owner";
            forbidden(resp);
            return null;
        }
        
        channel.setUserIdStr(null); // unlink
        channel.setStatus(NnChannel.STATUS_REMOVED);
        channel.setPublic(false);
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
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUser.getId() != user.getId()) {
            forbidden(resp);
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
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUser.getId() != user.getId()) {
            forbidden(resp);
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
        
        NnUser verifiedUser = userIdentify(req);
        if (verifiedUser == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUser.getId() != user.getId()) {
            forbidden(resp);
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
