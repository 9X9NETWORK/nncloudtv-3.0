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
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserLibrary;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnChannelPrefManager;
import com.nncloudtv.service.NnEpisodeManager;
import com.nncloudtv.service.NnProgramManager;
import com.nncloudtv.service.NnUserLibraryManager;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.service.NnUserPrefManager;
import com.nncloudtv.service.StoreService;
import com.nncloudtv.service.SysTagManager;
import com.nncloudtv.validation.NnUserValidator;
import com.nncloudtv.web.json.cms.User;
import com.nncloudtv.web.json.cms.UserFavorite;
import com.nncloudtv.web.json.facebook.FacebookError;
import com.nncloudtv.web.json.facebook.FacebookPage;
import com.nncloudtv.web.json.facebook.FacebookResponse;

@Controller
@RequestMapping("api")
public class ApiUser extends ApiGeneric {

    protected static Logger log = Logger.getLogger(ApiUser.class.getName());    
    
    /** 
     * this port is closed for security issue
     * @Deprecated */
    //@RequestMapping(value = "users/{userId}", method = RequestMethod.GET)
    public @ResponseBody
    NnUser userInfo(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("userId") String userIdStr, 
            @RequestParam(required = false) String mso,
            @RequestParam(required = false) Short shard) {
        
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
        Mso brand = new MsoManager().findOneByName(mso);
        if (shard == null) {
            user = userMngr.findById(userId, brand.getId());
        } else {
            user = userMngr.findById(userId, brand.getId(), (short) shard);
        }
        
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        return userMngr.purify(user);
    }
    
    @RequestMapping(value = "users/{userId}", method = RequestMethod.PUT)
    public @ResponseBody
    User userInfoUpdate(HttpServletRequest req, HttpServletResponse resp,
            @RequestParam(required = false) String mso,
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
        Mso brand = new MsoManager().findOneByName(mso);
        NnUser user = userMngr.findById(userId, brand.getId(), shard);
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        // password
        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        if (oldPassword != null && newPassword != null) {
            
            if (user.isFbUser()) {
                badRequest(resp, "FB_USER");
                return null;
            }
            
            NnUser passwordCheckedUser = userMngr.findAuthenticatedUser(user.getUserEmail(), oldPassword, brand.getId(), req);
            if (passwordCheckedUser == null) {
                badRequest(resp, "WRONG_PASSWORD");
                return null;
            }
            int status = NnUserValidator.validatePassword(newPassword);
            if (status != NnStatusCode.SUCCESS) {
                badRequest(resp, "WEAK_PASSWORD");
                return null;
            }
            
            user.setPassword(newPassword);
            
        } else if (oldPassword != null || newPassword != null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name != null && name.length() > 0){            
            user.getProfile().setName(NnStringUtil.htmlSafeAndTruncated(name));
        }
        
        // intro
        String intro = req.getParameter("intro");
        if (intro != null) {            
            user.getProfile().setIntro(NnStringUtil.htmlSafeAndTruncated(intro));
        }
        
        // imageUrl
        String imageUrl = req.getParameter("imageUrl");
        if (imageUrl != null) {            
            user.getProfile().setImageUrl(imageUrl);
        }
        
        // lang
        String lang = req.getParameter("lang");
        if (lang != null) {
            
            if (NnStringUtil.validateLangCode(lang) == null) {
                log.warning("lang is not valid");
            } else {
                user.getProfile().setLang(lang);
            }
        }
        
        user = userMngr.save(user);
        
        return userResponse(user);
    }
    
    @RequestMapping(value = "users/{userId}/my_favorites", method = RequestMethod.GET)
    public @ResponseBody
    List<UserFavorite> userFavorites(HttpServletRequest req,
            HttpServletResponse resp, 
            @PathVariable("userId") String userIdStr,
            @RequestParam(required = false) String mso) {
        
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
        Mso brand = new MsoManager().findOneByName(mso);
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
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
            HttpServletResponse resp,
            @RequestParam(required = false) String mso,
            @PathVariable("userId") String userIdStr,
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
        Mso brand = new MsoManager().findOneByName(mso);
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
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
            @RequestParam(required = false) String mso,
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
        Mso brand = new MsoManager().findOneByName(mso);
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
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
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "users/{userId}/channels", method = RequestMethod.GET)
    public @ResponseBody
    List<NnChannel> userChannels(HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam(required = false) String mso,
            @PathVariable("userId") String userIdStr) {
    
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
        Mso brand = new MsoManager().findOneByName(mso);
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        results = channelMngr.findByUserAndHisFavorite(user, 0, true);
        
        for (NnChannel channel : results) {
            
            channelMngr.normalize(channel);
            channelMngr.populateMoreImageUrl(channel);
            
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
            HttpServletResponse resp,
            @RequestParam(required = false) String mso,
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
        Mso brand = new MsoManager().findOneByName(mso);
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
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
        
        channelMngr.saveAll(orderedChannels);
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "users/{userId}/channels", method = RequestMethod.POST)
    public @ResponseBody NnChannel userChannelCreate(HttpServletRequest req, 
            HttpServletResponse resp,
            @RequestParam(required = false) String mso,
            @PathVariable("userId") String userIdStr) {
        
        NnChannelManager channelMngr = new NnChannelManager();
        MsoManager msoMngr = new MsoManager();
        
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
        Mso brand = msoMngr.findOneByName(mso);
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
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
        
        // seq
        channel.setSeq((short)0);
        
        channel = channelMngr.save(channel);
        
        channelMngr.reorderUserChannels(user);
        
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
            
            SysTagManager tagMngr = new SysTagManager();
            StoreService storeServ = new StoreService();
            SysTag category = tagMngr.findById(categoryId);
            if (category == null || category.getType() != SysTag.TYPE_CATEGORY ||
                    category.getMsoId() != msoMngr.findNNMso().getId()) {
                badRequest(resp, "Category Not Found");
                return null;
            }
            
            // category mapping
            storeServ.setupChannelCategory(categoryId, channel.getId());
            
            channel.setCategoryId(categoryId);
        }
        
        channelMngr.normalize(channel);
        
        return channel;
    }
    
    @RequestMapping(value = "users/{userId}/channels/{channelId}", method = RequestMethod.DELETE)
    public @ResponseBody
    String userChannelUnlink(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("userId") String userIdStr,
            @RequestParam(required = false) String mso,
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
        Mso brand = new MsoManager().findOneByName(mso);
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
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
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "users/{userId}/sns_auth/facebook", method = RequestMethod.POST)
    public @ResponseBody
    String facebookAuthUpdate(HttpServletRequest req, HttpServletResponse resp,
            @RequestParam(required = false) String mso,
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
        Mso brand = new MsoManager().findOneByName(mso);
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        String fbUserId = req.getParameter("userId");
        String accessToken = req.getParameter("accessToken");
        if (fbUserId == null || accessToken == null) {
            
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        String[] longLivedAccessToken = FacebookLib.getLongLivedAccessToken(accessToken);
        if (longLivedAccessToken[0] == null) {
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        accessToken = longLivedAccessToken[0];
        
        NnUserPrefManager prefMngr = new NnUserPrefManager();
        NnChannelPrefManager channelPrefMngr = new NnChannelPrefManager();
        NnUserPref userPref = null;
        
        // fbUserId
        userPref = prefMngr.findByUserAndItem(user, NnUserPref.FB_USER_ID);
        if (userPref != null) {
            if (userPref.getValue().equals(fbUserId) == false) {
                // remove all channels autoshare setting
                channelPrefMngr.deleteAllChannelsFBbyUser(user);
            } else {
                // update page token
                List<FacebookPage> pages = null;
                FacebookResponse response = FacebookLib.populatePageList(fbUserId, accessToken);
                if (response == null) {
                    log.warning("connect to facebook failed");
                } else if (response.getData() != null) {
                    pages = response.getData();
                    log.info("pages count: " + pages.size());
                } else if (response.getError() != null) {
                    FacebookError error = response.getError();
                    log.warning("error message: " + error.getMessage());
                    log.warning("error type:" + error.getType());
                } else {
                    log.warning("neither no data nor error");
                }
                
                if (pages != null && pages.size() > 0) {
                    channelPrefMngr.updateAllChannelsFBbyUser(user, pages);
                }
            }
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
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "users/{userId}/sns_auth/facebook", method = RequestMethod.DELETE)
    public @ResponseBody
    String facebookAuthDelete(HttpServletRequest req, HttpServletResponse resp,
            @RequestParam(required = false) String mso,            
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
        Mso brand = new MsoManager().findOneByName(mso);
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
            forbidden(resp);
            return null;
        }
        
        NnUserPrefManager prefMngr = new NnUserPrefManager();
        NnChannelPrefManager channelPrefMngr = new NnChannelPrefManager();
        NnUserPref userPref = null;
        
        // remove all channels autoshare setting
        channelPrefMngr.deleteAllChannelsFBbyUser(user);
        
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
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "users/{userId}/sns_auth/facebook", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> facebookAuth(HttpServletRequest req, HttpServletResponse resp,
            @RequestParam(required = false) String mso,
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
        Mso brand = new MsoManager().findOneByName(mso);
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != user.getId()) {
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
            //List<FacebookPage> pages = FacebookLib.populatePageList(fbUserId, accessToken);
            List<FacebookPage> pages = null;
            FacebookResponse response = FacebookLib.populatePageList(fbUserId, accessToken);
            if (response == null) {
                result.put("pages", "connect to facebook failed");
            } else if (response.getData() != null) {
                pages = response.getData();
                log.info("pages count: " + pages.size());
                result.put("pages", pages);
            } else if (response.getError() != null) {
                FacebookError error = response.getError();
                log.warning("error message: " + error.getMessage());
                log.warning("error type:" + error.getType());
                result.put("pages", "error type:" + error.getType() + "; error message: " + error.getMessage());
            } else {
                log.warning("neither no data nor error");
                result.put("pages", pages);
            }
            
        } else {
            nullResponse(resp);
            return null;
        }
        
        return result;
    }
}
