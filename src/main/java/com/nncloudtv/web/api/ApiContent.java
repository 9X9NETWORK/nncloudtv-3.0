package com.nncloudtv.web.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryMap;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnAd;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnChannelPref;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUserLibrary;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.model.TitleCard;
import com.nncloudtv.service.CategoryManager;
import com.nncloudtv.service.NnAdManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnChannelPrefManager;
import com.nncloudtv.service.NnEpisodeManager;
import com.nncloudtv.service.NnProgramManager;
import com.nncloudtv.service.NnUserLibraryManager;
import com.nncloudtv.service.TitleCardManager;

@Controller
@RequestMapping("api")
public class ApiContent extends ApiGeneric {
    
    protected static Logger log = Logger.getLogger(ApiContent.class.getName());
    
    @RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.DELETE)
    public @ResponseBody
    String facebookAutosharingDelete(HttpServletRequest req,
            HttpServletResponse resp,
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
        
        NnChannelPref pref = null;
        NnChannelPrefManager prefMngr = new NnChannelPrefManager();
        
        // fbUserId
        pref = prefMngr
                .findByChannelIdAndItem(channelId, NnUserPref.FB_USER_ID);
        if (pref != null) {
            prefMngr.delete(pref);
        }
        
        // accessToken
        pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_TOKEN);
        if (pref != null) {
            prefMngr.delete(pref);
        }
        
        return "OK";
    }
    
    @RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.POST)
    public @ResponseBody
    String facebookAutosharingCreate(HttpServletRequest req,
            HttpServletResponse resp,
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
        
        String fbUserId = req.getParameter("userId");
        String accessToken = req.getParameter("accessToken");
        if (fbUserId == null || accessToken == null) {
            
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        NnChannelPref pref = null;
        NnChannelPrefManager prefMngr = new NnChannelPrefManager();
        
        // fbUserId
        pref = prefMngr
                .findByChannelIdAndItem(channelId, NnUserPref.FB_USER_ID);
        if (pref != null) {
            pref.setValue(fbUserId);
        } else {
            pref = new NnChannelPref(channel, NnUserPref.FB_USER_ID, fbUserId);
        }
        prefMngr.save(pref);
        
        // accessToken
        pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_TOKEN);
        if (pref != null) {
            pref.setValue(accessToken);
        } else {
            pref = new NnChannelPref(channel, NnUserPref.FB_TOKEN, accessToken);
        }
        prefMngr.save(pref);
        
        return "OK";
    }
    
    @RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> facebookAutosharing(HttpServletRequest req,
            HttpServletResponse resp,
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
        
        NnChannelPrefManager prefMngr = new NnChannelPrefManager();
        NnChannelPref pref = null;
        Map<String, Object> result = new TreeMap<String, Object>();
        String fbUserId = null;
        String accessToken = null;
        
        // fbUserId
        pref = prefMngr
                .findByChannelIdAndItem(channelId, NnUserPref.FB_USER_ID);
        if (pref != null) {
            fbUserId = pref.getValue();
            result.put("userId", fbUserId);
        }
        
        // accessToken
        pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_TOKEN);
        if (pref != null) {
            accessToken = pref.getValue();
            result.put("accessToken", accessToken);
        }
        
        if (accessToken != null && fbUserId != null) {
            return result;
        }
        
        return null;
    }
    
    @RequestMapping(value = "programs/{programId}", method = RequestMethod.GET)
    public @ResponseBody
    NnProgram program(@PathVariable("programId") String programIdStr,
            HttpServletRequest req, HttpServletResponse resp) {
        
        Long programId = null;
        try {
            programId = Long.valueOf(programIdStr);
        } catch (NumberFormatException e) {
        }
        
        if (programId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnProgramManager programMngr = new NnProgramManager();
        NnProgram program = programMngr.findById(programId);
        if (program == null) {
            notFound(resp, "Pogram Not Found");
            return null;
        }
        
        program.setName(NnStringUtil.revertHtml(program.getName()));
        program.setIntro(NnStringUtil.revertHtml(program.getIntro()));
        
        return program;
    }
    
    @RequestMapping(value = "programs/{programId}", method = RequestMethod.PUT)
    public @ResponseBody
    NnProgram programUpdate(@PathVariable("programId") String programIdStr,
            HttpServletRequest req, HttpServletResponse resp) {
        
        Long programId = null;
        try {
            programId = Long.valueOf(programIdStr);
        } catch (NumberFormatException e) {
        }
        if (programId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnProgramManager programMngr = new NnProgramManager();
        NnProgram program = programMngr.findById(programId);
        if (program == null) {
            notFound(resp, "Program Not Found");
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name != null) {
            program.setName(NnStringUtil.htmlSafeAndTruncated(name));
        }
        
        // intro
        String intro = req.getParameter("intro");
        if (intro != null) {
            program.setIntro(NnStringUtil.htmlSafeAndTruncated(intro));
        }
        
        // imageUrl
        String imageUrl = req.getParameter("imageUrl");
        if (imageUrl != null) {
            program.setImageUrl(imageUrl);
        }
        
        // subSeq
        String subSeqStr = req.getParameter("subSeq");
        if (subSeqStr != null && subSeqStr.length() > 0) {
            Short subSeq = null;
            try {
                subSeq = Short.valueOf(subSeqStr);
            } catch (NumberFormatException e) {
            }
            if (subSeq == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            } else {
                program.setSubSeq(subSeq);
            }
        }
        
        // startTime
        String startTimeStr = req.getParameter("startTime");
        if (startTimeStr != null && startTimeStr.length() > 0) {
            Integer startTime = null;
            try {
                startTime = Integer.valueOf(startTimeStr);
            } catch (NumberFormatException e) {
            }
            if ((startTime == null) || (startTime < 0)) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            program.setStartTime(startTime);
        }
        
        // endTime
        String endTimeStr = req.getParameter("endTime");
        if (endTimeStr != null && endTimeStr.length() > 0) {
            Integer endTime = null;
            try {
                endTime = Integer.valueOf(endTimeStr);
            } catch (NumberFormatException e) {
            }
            if ((endTime == null) || (endTime <= program.getStartTimeInt())) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            program.setEndTime(endTime);
        }
        
        // update duration = endTime - startTime
        if ((program.getEndTimeInt() - program.getStartTimeInt()) > 0) {
            program.setDuration((short)(program.getEndTimeInt() - program.getStartTimeInt()));
        } else {
            // ex : new start = 10, old end = 5
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        
        program = programMngr.save(program);
        
        program.setName(NnStringUtil.revertHtml(program.getName()));
        program.setIntro(NnStringUtil.revertHtml(program.getIntro()));
        
        return program;
    }
    
    @RequestMapping(value = "programs/{programId}", method = RequestMethod.DELETE)
    public @ResponseBody
    String programDelete(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("programId") String programIdStr) {
        
        Long programId = null;
        try {
            programId = Long.valueOf(programIdStr);
        } catch (NumberFormatException e) {
        }
        if (programId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnProgramManager programMngr = new NnProgramManager();
        
        NnProgram program = programMngr.findById(programId);
        if (program == null) {
            return "Program Not Found";
        }
        
        // delete title cards
        TitleCardManager titleCardMngr = new TitleCardManager();
        List<TitleCard> titleCards = titleCardMngr.findByProgramId(programId);
        titleCardMngr.delete(titleCards);
        
        programMngr.delete(program);
        
        return "OK";
    }
    
    // delete programs in one episode
    @RequestMapping(value = "episodes/{episodeId}/programs", method = RequestMethod.DELETE)
    public @ResponseBody
    String programsDelete(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("episodeId") String episodeIdStr) {
    
        Long episodeId = null;
        try {
            episodeId = Long.valueOf(episodeIdStr);
        } catch (NumberFormatException e) {
        }
        if (episodeId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnProgramManager programMngr = new NnProgramManager();
        TitleCardManager titlecardMngr = new TitleCardManager();
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            notFound(resp, "Episode Not Found");
            return null;
        }
        
        List<NnProgram> episodePrograms = programMngr.findByEpisodeId(episode.getId());
        List<Long> episodeProgramIdList = new ArrayList<Long>();
        for (NnProgram episodeProgram : episodePrograms) {
            episodeProgramIdList.add(episodeProgram.getId());
        }
        
        String programIdsStr = req.getParameter("programs");
        if (programIdsStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        log.info(programIdsStr);
        
        String[] programIdStrList = programIdsStr.split(",");
        List<NnProgram> programDeleteList = new ArrayList<NnProgram>();
        List<TitleCard> titlecardDeleteList = new ArrayList<TitleCard>();
        
        for (String programIdStr : programIdStrList) {
            
            Long programId = null;
            try {
                
                programId = Long.valueOf(programIdStr);
                
            } catch(Exception e) {
            }
            if (programId != null) {
                
                NnProgram program = programMngr.findById(programId);
                if (program != null && episodeProgramIdList.indexOf(program.getId()) > -1) {
                    
                    programDeleteList.add(program);
                    List<TitleCard> titlecards = titlecardMngr.findByProgramId(programId);
                    if (titlecards.size() > 0) {
                        titlecardDeleteList.addAll(titlecards);
                    }
                }
            }
        }
        log.info("program delete count = " + programDeleteList.size());
        log.info("titlecard delete count = " + titlecardDeleteList.size());
        
        titlecardMngr.delete(titlecardDeleteList);
        programMngr.delete(programDeleteList);
        
        return "OK";
    }
    
    @RequestMapping(value = "episodes/{episodeId}/programs", method = RequestMethod.POST)
    public @ResponseBody
    NnProgram programCreate(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("episodeId") String episodeIdStr) {
        
        Long episodeId = null;
        try {
            episodeId = Long.valueOf(episodeIdStr);
        } catch (NumberFormatException e) {
        }
        if (episodeId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            notFound(resp, "Episode Not Found");
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        name = NnStringUtil.htmlSafeAndTruncated(name);
        
        String intro = req.getParameter("intro");
        if (intro != null) {
            intro = NnStringUtil.htmlSafeAndTruncated(intro);
        }
        String imageUrl = req.getParameter("imageUrl");
        if (imageUrl == null) {
            imageUrl = NnChannel.IMAGE_WATERMARK_URL;
        }
        
        NnProgram program = new NnProgram(episode.getChannelId(), episodeId, name, intro, imageUrl);
        program.setPublic(true);
        
        // fileUrl
        String fileUrl = req.getParameter("fileUrl");
        if (fileUrl == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        program.setFileUrl(fileUrl);
        
        // contentType
        program.setContentType(NnProgram.CONTENTTYPE_YOUTUBE);
        String contentTypeStr = req.getParameter("contentType");
        if (contentTypeStr != null) {
            
            Short contentType = Short.valueOf(contentTypeStr);
            if (contentType == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            
            program.setContentType(contentType);
        }
        if (program.getContentType() == NnProgram.CONTENTTYPE_YOUTUBE && !YouTubeLib.isVideoUrlNormalized(fileUrl)) {
            badRequest(resp, INVALID_YOUTUBE_URL);
            return null;
        }
        
        // duration
        String durationStr = req.getParameter("duration");
        if (durationStr == null) {
            
            program.setDuration((short)0);
            
        } else {
            Short duration = null;
            try {
                duration = Short.valueOf(durationStr);
            } catch (NumberFormatException e) {
            }
            if ((duration == null) || (duration <= 0)) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            program.setDuration(duration);
        }
        
        // startTime
        String startTimeStr = req.getParameter("startTime");
        if (startTimeStr == null) {
            
            program.setStartTime(0);
            
        } else {
            
            Short startTime = null;
            try {
                startTime = Short.valueOf(startTimeStr);
            } catch (NumberFormatException e) {
            }
            if ((startTime == null) || (startTime < 0)) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            program.setStartTime(startTime);
        }
        
        // endTime
        String endTimeStr = req.getParameter("endTime");
        if (endTimeStr == null) {
            if (program.getDurationInt() == 0) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            program.setEndTime(program.getStartTimeInt() + program.getDurationInt());
            
        } else {
            
            Short endTime = null;
            try {
                endTime = Short.valueOf(endTimeStr);
            } catch (NumberFormatException e) {
            }
            if ((endTime == null) || (endTime <= program.getStartTimeInt()) ) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            program.setEndTime(endTime);
        }
        
        // duration = endTime - startTime
        if (program.getDurationInt() == 0) {
            program.setDuration((short)(program.getEndTimeInt() - program.getStartTimeInt()));
        }
        
        NnProgramManager programMngr = new NnProgramManager();
        
        // subSeq
        String subSeqStr = req.getParameter("subSeq");
        if (subSeqStr == null) {
            
            program.setSubSeq(0);
        } else {
            Short subSeq = null;
            try {
                subSeq = Short.valueOf(subSeqStr);
            } catch (NumberFormatException e) {
            }
            if (subSeq == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            program.setSubSeq(subSeq);
        }
        
        // publish
        program.setPublishDate(new Date());
        program.setPublic(true);
        
        // episode sequence
        if (episode.getSeq() != 0) {
            program.setSeq(episode.getSeq());
        } else {
            program.setSeq(episodeMngr.getEpisodeSeq(episode));
        }
        
        program = programMngr.create(episode, program);
        
        program.setName(NnStringUtil.revertHtml(program.getName()));
        program.setIntro(NnStringUtil.revertHtml(program.getIntro()));
        
        return program;
    }
    
    @RequestMapping(value = "channels/{channelId}", method = RequestMethod.GET)
    public @ResponseBody
    NnChannel channel(HttpServletRequest req, HttpServletResponse resp,
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
        
        channel.setName(NnStringUtil.revertHtml(channel.getName()));
        channel.setIntro(NnStringUtil.revertHtml(channel.getIntro()));
        
        return channel;
    }
    
    @RequestMapping(value = "channels/{channelId}", method = RequestMethod.PUT)
    public @ResponseBody
    NnChannel channelUpdate(HttpServletRequest req, HttpServletResponse resp,
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
        
        // name
        String name = req.getParameter("name");
        if (name != null) {
            channel.setName(NnStringUtil.htmlSafeAndTruncated(name));
        }
        
        // intro
        String intro = req.getParameter("intro");
        if (intro != null) {
            channel.setIntro(NnStringUtil.htmlSafeAndTruncated(intro));
        }
        
        // lang
        String lang = req.getParameter("lang");
        if (lang != null && NnStringUtil.validateLangCode(lang) != null) {
            
            channel.setLang(lang);
        }
        
        // sphere
        String sphere = req.getParameter("sphere");
        if (sphere != null && NnStringUtil.validateLangCode(sphere) != null) {
            
            channel.setSphere(sphere);
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
        
        // imageUrl
        String imageUrl = req.getParameter("imageUrl");
        if (imageUrl != null) {
            channel.setImageUrl(imageUrl);
        }
        
        // categoryId
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
            if (categoryId != channel.getCategoryId()) {
                
                List<CategoryMap> cats = catMngr.findMapByChannelId(channelId);
                
                catMngr.delete(cats);
                
                catMngr.save(new CategoryMap(categoryId, channelId));
                
                if (channel.getSphere() != null && channel.getSphere().equalsIgnoreCase(LangTable.OTHER)) {
                    
                    Category twin = catMngr.findTwin(category);
                    if (twin != null) {
                        catMngr.save(new CategoryMap(twin.getId(), channelId));
                    }
                }
                
                channel.setCategoryId(categoryId);
            }
        }
        
        NnChannel savedChannel = channelMngr.save(channel);
        savedChannel.setCategoryId(channel.getCategoryId());
        savedChannel.setName(NnStringUtil.revertHtml(savedChannel.getName()));
        savedChannel.setIntro(NnStringUtil.revertHtml(savedChannel.getIntro()));
        
        return savedChannel;
    }
    
    @RequestMapping(value = "channels/{channelId}/isOwner", method = RequestMethod.GET)
    public @ResponseBody
    Boolean isChannelOwner(HttpServletRequest req, HttpServletResponse resp,
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
        
        String mail = req.getParameter("mail");
        if (mail == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        return channelMngr.isChannelOwner(channel, mail);
    }
    
    @RequestMapping(value = "tags", method = RequestMethod.GET)
    public @ResponseBody String[] tags(HttpServletRequest req, HttpServletResponse resp) {
        
        String categoryIdStr = req.getParameter("categoryId");
        if (categoryIdStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
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
        
        String result = category.getTag();
        if (result == null || result.length() == 0) {
            return new String[0];
        }
        return result.split(",");
    }
    
    @RequestMapping(value = "categories", method = RequestMethod.GET)
    public @ResponseBody
    List<Category> categories(HttpServletRequest req, HttpServletResponse resp) {
        
        String lang = req.getParameter("lang");
        CategoryManager catMngr = new CategoryManager();
        List<Category> categories;
        
        if (lang != null) {
            categories = catMngr.findByLang(lang);
        } else {
            categories = catMngr.findAll();
        }
        
        List<Category> temp = new ArrayList<Category>();
        temp.addAll(categories);
        for (Category category : temp) {
            if (category.isPublic() == false) {
                categories.remove(category);
            }
        }
        
        Collections.sort(categories, catMngr.getCategorySeqComparator());
        
        return categories;
    }
    
    // TODO: paging 
    @RequestMapping(value = "channels/{channelId}/episodes", method = RequestMethod.GET)
    public @ResponseBody
    List<NnEpisode> channelEpisodes(HttpServletResponse resp,
            HttpServletRequest req,
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
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        List<NnEpisode> results = new ArrayList<NnEpisode>();
        
        // paging
        long page = 0, rows = 0;
        try {
            String pageStr = req.getParameter("page");
            String rowsStr = req.getParameter("rows");
            if (pageStr != null && rowsStr != null) {
                page = Long.valueOf(pageStr);
                rows = Long.valueOf(rowsStr);
            }
        } catch (NumberFormatException e) {
        }
        
        if (page > 0 && rows > 0) {
            
            results = episodeMngr.list(page, rows, "seq", "asc", "channelId == " + channelId);
            
        } else {
            
            results = episodeMngr.findByChannelId(channelId);
            
        }
        
        List<NnEpisode> needPopulateSeq = new ArrayList<NnEpisode>();
        for (NnEpisode episode : results) {
            if (episode.getSeq() == 0) {
                needPopulateSeq.add(episode);
            }
        }
        results.removeAll(needPopulateSeq);
        episodeMngr.populateEpisodesSeq(needPopulateSeq);
        results.addAll(needPopulateSeq);
        Collections.sort(results, episodeMngr.getEpisodeSeqComparator());
        
        for (NnEpisode episode : results) {
            
            episode.setName(NnStringUtil.revertHtml(episode.getName()));
            episode.setIntro(NnStringUtil.revertHtml(episode.getIntro()));
            episode.setPlaybackUrl(NnStringUtil.getEpisodePlaybackUrl(episode.getChannelId(), episode.getId()));
        }
        
        return results;
    }
    
    // TODO: need to be optimized
    @RequestMapping(value = "channels/{channelId}/episodes/sorting", method = RequestMethod.PUT)
    public @ResponseBody
    String channelEpisodesSorting(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("channelId") String channelIdStr) {
        
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
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            notFound(resp, "Channel Not Found");
            return null;
        }
        
        String episodeIdsStr = req.getParameter("episodes");
        if (episodeIdsStr == null) {
            episodeMngr.reorderChannelEpisodes(channelId);
            return "OK";
        }
        String[] episodeIdStrList = episodeIdsStr.split(",");
        
        List<NnEpisode> episodes = episodeMngr.findByChannelId(channelId); // it must same as channelEpisodes result
        List<NnEpisode> orderedEpisodes = new ArrayList<NnEpisode>();
        List<Long> episodeIdList = new ArrayList<Long>();
        List<Long> checkedEpisodeIdList = new ArrayList<Long>();
        for (NnEpisode episode : episodes) {
            episodeIdList.add(episode.getId());
            checkedEpisodeIdList.add(episode.getId());
        }
        
        int index;
        for (String episodeIdStr : episodeIdStrList) {
            
            Long episodeId = null;
            try {
                
                episodeId = Long.valueOf(episodeIdStr);
                
            } catch(Exception e) {
            }
            if (episodeId != null) {
                index = episodeIdList.indexOf(episodeId);
                if (index > -1) {
                    orderedEpisodes.add(episodes.get(index));
                    checkedEpisodeIdList.remove(episodeId);
                }
            }
        }
        // parameter should contain all episodeId
        if (checkedEpisodeIdList.size() != 0) {
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        
        int counter = 1;
        for (NnEpisode episode : orderedEpisodes) {
            episode.setSeq(counter);
            counter++;
        }
        
        episodeMngr.save(orderedEpisodes);
        channelMngr.save(channel); // update channel updateDate
        
        return "OK";
    }
    
    @RequestMapping(value = "episodes/{episodeId}", method = RequestMethod.DELETE)
    public @ResponseBody
    String episodeDelete(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("episodeId") String episodeIdStr) {
    
        Long episodeId = null;
        try {
            episodeId = Long.valueOf(episodeIdStr);
        } catch (NumberFormatException e) {
        }
        if (episodeId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            
            return "Episode Not Found";
        }
        
        // delete shopping_info
        NnAdManager adMngr = new NnAdManager();
        NnAd nnAd = adMngr.findByEpisode(episode);
        if(nnAd != null) {
            adMngr.delete(nnAd);
        }
        
        // delete programs / title cards
        TitleCardManager titlecardMngr = new TitleCardManager();
        NnProgramManager programMngr = new NnProgramManager();
        List<TitleCard> titlecards = new ArrayList<TitleCard>();
        List<NnProgram> programs = programMngr.findByEpisodeId(episode.getId());
        for (NnProgram program : programs) {
            titlecards.addAll(titlecardMngr.findByProgramId(program.getId()));
        }
        titlecardMngr.delete(titlecards);
        programMngr.delete(programs);
        
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(episode.getChannelId());
        
        // delete episode
        episodeMngr.delete(episode);
        
        // re-calcuate episode count
        if (channel != null) {
            channel.setCntEpisode(channelMngr.calcuateEpisodeCount(channel));
            channelMngr.save(channel);
        }
        
        return "OK";
    }
    
    @RequestMapping(value = "episodes/{episodeId}", method = RequestMethod.GET)
    public @ResponseBody NnEpisode episode(HttpServletRequest req, HttpServletResponse resp, @PathVariable("episodeId") String episodeIdStr) {
        
        Long episodeId = null;
        try {
            episodeId = Long.valueOf(episodeIdStr);
        } catch (NumberFormatException e) {
        }
        if (episodeId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            notFound(resp, "Episode Not Found");
            return null;
        }
        
        if (episode.getSeq() == 0) {
            episode.setSeq(episodeMngr.getEpisodeSeq(episode));
        }
        episode.setName(NnStringUtil.revertHtml(episode.getName()));
        episode.setIntro(NnStringUtil.revertHtml(episode.getIntro()));
        
        return episode;
    }
    
    @RequestMapping(value = "episodes/{episodeId}", method = RequestMethod.PUT)
    public @ResponseBody
    NnEpisode episodeUpdate(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("episodeId") String episodeIdStr) {
        
        Long episodeId = null;
        try {
            episodeId = Long.valueOf(episodeIdStr);
        } catch (NumberFormatException e) {
        }
        if (episodeId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            notFound(resp, "Episode Not Found");
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name != null) {
            episode.setName(NnStringUtil.htmlSafeAndTruncated(name));
        }
        
        // intro
        String intro = req.getParameter("intro");
        if (intro != null) {
            episode.setIntro(NnStringUtil.htmlSafeAndTruncated(intro));
        }
        
        // imageUrl
        String imageUrl = req.getParameter("imageUrl");
        if (imageUrl != null) {
            episode.setImageUrl(imageUrl);
        }
        
        // scheduleDate
        String scheduleDateStr = req.getParameter("scheduleDate");
        if (scheduleDateStr != null) {
            
            if (scheduleDateStr.isEmpty()) {
                
                episode.setScheduleDate(null);
                
            } else {
                
                Long scheduleDateLong = null;
                try {
                    scheduleDateLong = Long.valueOf(scheduleDateStr);
                } catch (NumberFormatException e) {
                }
                if (scheduleDateLong == null) {
                    badRequest(resp, INVALID_PARAMETER);
                    return null;
                }
                
                episode.setScheduleDate(new Date(scheduleDateLong));
            }
        }
        
        // publishDate
        String publishDateStr = req.getParameter("publishDate");
        if (publishDateStr != null) {
            
            log.info("publishDate = " + publishDateStr);
            
            if (publishDateStr.isEmpty()) {
                
                log.info("set publishDate to null");
                episode.setPublishDate(null);
                
            } else if (publishDateStr.equalsIgnoreCase("NOW")) {
                
                episode.setPublishDate(new Date());
                
            } else {
                
                Long publishDateLong = null;
                try {
                    publishDateLong = Long.valueOf(publishDateStr);
                } catch (NumberFormatException e) {
                }
                if (publishDateLong == null) {
                    badRequest(resp, INVALID_PARAMETER);
                    return null;
                }
                
                episode.setPublishDate(new Date(publishDateLong));
            }
        }
        
        // isPublic
        String isPublicStr = req.getParameter("isPublic");
        if (isPublicStr != null) {
            Boolean isPublic = Boolean.valueOf(isPublicStr);
            if (isPublic != null) {
                episode.setPublic(isPublic);
            }
        }
        
        // rerun
        String rerunStr = req.getParameter("rerun");
        boolean rerun = false;
        if (rerunStr != null && Boolean.valueOf(rerunStr)) {
            rerun = true;
        }
        
        episode.setDuration(episodeMngr.calculateEpisodeDuration(episode));
        episode = episodeMngr.save(episode, rerun);
        
        episode.setName(NnStringUtil.revertHtml(episode.getName()));
        episode.setIntro(NnStringUtil.revertHtml(episode.getIntro()));
        
        return episode;
    }
    
    @RequestMapping(value = "channels/{channelId}/episodes", method = RequestMethod.POST)
    public @ResponseBody NnEpisode episodeCreate(HttpServletRequest req, HttpServletResponse resp, @PathVariable("channelId") String channelIdStr) {
        
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
        
        NnEpisode episode = new NnEpisode(channelId);
        episode.setName(name);
        episode.setIntro(intro);
        episode.setImageUrl(imageUrl);
        episode.setChannelId(channel.getId());
        
        // scheduleDate
        String scheduleDateStr = req.getParameter("scheduleDate");
        if (scheduleDateStr != null) {
            
            if (scheduleDateStr.isEmpty()) {
                
                episode.setScheduleDate(null);
                
            } else {
                
                Long scheduleDateLong = null;
                try {
                    scheduleDateLong = Long.valueOf(scheduleDateStr);
                } catch (NumberFormatException e) {
                }
                if (scheduleDateLong == null) {
                    badRequest(resp, INVALID_PARAMETER);
                    return null;
                }
                
                episode.setScheduleDate(new Date(scheduleDateLong));
            }
        }
        
        // publishDate
        String publishDateStr = req.getParameter("publishDate");
        if (publishDateStr != null) {
            
            log.info("publishDate = " + publishDateStr);
            
            if (publishDateStr.isEmpty()) {
                
                log.info("set publishDate to null");
                episode.setPublishDate(null);
                
            } else if (publishDateStr.equalsIgnoreCase("NOW")) {
                
                episode.setPublishDate(new Date());
                
            } else {
                
                Long publishDateLong = null;
                try {
                    publishDateLong = Long.valueOf(publishDateStr);
                } catch (NumberFormatException e) {
                }
                if (publishDateLong == null) {
                    badRequest(resp, INVALID_PARAMETER);
                    return null;
                }
                
                episode.setPublishDate(new Date(publishDateLong));
            }
        }
        
        // isPublic
        episode.setPublic(false); // default is draft
        String isPublicStr = req.getParameter("isPublic");
        if (isPublicStr != null) {
            Boolean isPublic = Boolean.valueOf(isPublicStr);
            if (isPublic != null) {
                episode.setPublic(isPublic);
            }
        }
        
        // seq
        episode.setSeq(0);
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        episode = episodeMngr.save(episode);
        episodeMngr.reorderChannelEpisodes(channelId);
        
        episode.setName(NnStringUtil.revertHtml(episode.getName()));
        episode.setIntro(NnStringUtil.revertHtml(episode.getIntro()));
        
        channel.setCntEpisode(channelMngr.calcuateEpisodeCount(channel));
        channelMngr.save(channel);
        
        return episode;
    }
    
    @RequestMapping(value = "episodes/{episodeId}/programs", method = RequestMethod.GET)
    public @ResponseBody
    List<NnProgram> episodePrograms(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable(value = "episodeId") String episodeIdStr) {
    
        Long episodeId = null;
        try {
            episodeId = Long.valueOf(episodeIdStr);
        } catch (NumberFormatException e) {
        }
        if (episodeId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            
            notFound(resp, "Episode Not Found");
            return null;
        }
        
        NnProgramManager programMngr = new NnProgramManager();
        List<NnProgram> results = programMngr.findByEpisodeId(episodeId);
        for (NnProgram result : results) {
            result.setName(NnStringUtil.revertHtml(result.getName()));
            result.setIntro(NnStringUtil.revertHtml(result.getIntro()));
        }
        
        return results;
    }
    
    @RequestMapping(value = "episodes/{episodeId}/shopping_info", method = RequestMethod.DELETE)
    public @ResponseBody
    String shoppingInfoDelete(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable(value = "episodeId") String episodeIdStr) {
        
        Long episodeId = null;
        try {
            episodeId = Long.valueOf(episodeIdStr);
        } catch (NumberFormatException e) {
        }
        if (episodeId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnAdManager adMngr = new NnAdManager();
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            
            notFound(resp, "Episode Not Found");
            return null;
        }
        
        episode.setAdId(0);
        episodeMngr.save(episode);
        
        NnAd nnad = adMngr.findByEpisode(episode);
        if (nnad != null) {
            adMngr.delete(nnad);
        }
        
        return "OK";
    }
    
    @RequestMapping(value = "episodes/{episodeId}/shopping_info", method = RequestMethod.POST)
    public @ResponseBody
    NnAd shoppingInfoCreate(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable(value = "episodeId") String episodeIdStr) {        
        
        Long episodeId = null;
        try {
            episodeId = Long.valueOf(episodeIdStr);
        } catch (NumberFormatException e) {
        }
        if (episodeId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnAdManager adMngr = new NnAdManager();
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            notFound(resp, "Episode Not Found");
            return null;
        }
        
        NnAd nnad = adMngr.findByEpisode(episode);
        if (nnad == null) {
            nnad = new NnAd();
        }
        
        // merchantEmail
        String merchantEmail = req.getParameter("merchantEmail");
        if (merchantEmail != null) {
            nnad.setMerchantEmail(merchantEmail);
        }
        
        // message
        String message = req.getParameter("message");
        if (message != null) {
            nnad.setMessage(NnStringUtil.htmlSafeAndTruncated(message));
        }
        
        // url
        String url = req.getParameter("url");
        if (url != null) {
            nnad.setUrl(url);
        }
        
        resp.setStatus(201);
        
        nnad = adMngr.save(nnad, episode);
        nnad.setMessage(NnStringUtil.revertHtml(nnad.getMessage()));
        
        return nnad;
    }
    
    @RequestMapping(value = "episodes/{episodeId}/shopping_info", method = RequestMethod.GET)
    public @ResponseBody
    NnAd shoppingInfo(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable(value = "episodeId") String episodeIdStr) {
        
        Long episodeId = null;
        try {
            episodeId = Long.valueOf(episodeIdStr);
        } catch (NumberFormatException e) {
        }
        if (episodeId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnAdManager adMngr = new NnAdManager();
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            
            notFound(resp, "Episode Not Found");
            return null;
        }
        
        NnAd nnad = adMngr.findByEpisode(episode);
        
        if (nnad != null) {
            nnad.setMessage(NnStringUtil.revertHtml(nnad.getMessage()));
        }
        
        return nnad;
    }
    
    @RequestMapping(value = "programs/{programId}/title_cards", method = RequestMethod.GET)
    public @ResponseBody
    List<TitleCard> programTitleCards(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("programId") String programIdStr) {
        
        Long programId = null;
        try {
            programId = Long.valueOf(programIdStr);
        } catch (NumberFormatException e) {
        }
        if (programId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        TitleCardManager titleCardMngr = new TitleCardManager();
        List<TitleCard> results = titleCardMngr.findByProgramId(programId);
        
        for (TitleCard result : results) {
            result.setMessage(NnStringUtil.revertHtml(result.getMessage()));
        }
        
        return results;
    }
    
    @RequestMapping(value = "episodes/{episodeId}/title_cards", method = RequestMethod.GET)
    public @ResponseBody
    List<TitleCard> episodeTitleCards(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("episodeId") String episodeIdStr) {
    
        Long episodeId = null;
        try {
            episodeId = Long.valueOf(episodeIdStr);
        } catch (NumberFormatException e) {
        }
        if (episodeId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            
            notFound(resp, "Episode Not Found");
            return null;
        }
        
        TitleCardManager titleCardMngr = new TitleCardManager();
        List<TitleCard> results = titleCardMngr.findByEpisodeId(episodeId);
        
        for (TitleCard result : results) {
            result.setMessage(NnStringUtil.revertHtml(result.getMessage()));
        }
        
        return results;
    }

    @RequestMapping(value = "programs/{programId}/title_cards", method = RequestMethod.POST)
    public @ResponseBody
    TitleCard titleCardCreate(HttpServletResponse resp, HttpServletRequest req,
            @PathVariable("programId") String programIdStr) {
        
        Long programId = null;
        try {
            programId = Long.valueOf(programIdStr);
        } catch (NumberFormatException e) {
        }
        if (programId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        NnProgramManager programMngr = new NnProgramManager();
        NnProgram program = programMngr.findById(programId);
        if (program == null) {
            notFound(resp, "Program Not Found");
            return null;
        }
        
        // type
        String typeStr = req.getParameter("type");
        if (typeStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        Short type = null;
        try {
            type = Short.valueOf(typeStr);
        } catch (NumberFormatException e) {
        }
        if (type == null) {
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        if (type != TitleCard.TYPE_BEGIN && type != TitleCard.TYPE_END) {
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        
        TitleCardManager titleCardMngr = new TitleCardManager();
        
        TitleCard titleCard = titleCardMngr.findByProgramIdAndType(programId, type);
        if (titleCard == null) {
            titleCard = new TitleCard(program.getChannelId(), programId, type);
        }
        
        // message
        String message = req.getParameter("message");
        if (message == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        titleCard.setMessage(NnStringUtil.htmlSafeAndTruncated(message));
        
        // duration
        String duration = req.getParameter("duration");
        if (duration == null) {
            titleCard.setDuration(TitleCard.DEFAULT_DURATION);
        } else {
            titleCard.setDuration(duration);
        }
        
        // size
        String size = req.getParameter("size");
        if (size == null) {
            titleCard.setSize(TitleCard.DEFAULT_SIZE);
        } else {
            titleCard.setSize(size);
        }
        
        // color
        String color = req.getParameter("color");
        if (color == null) {
            titleCard.setColor(TitleCard.DEFAULT_COLOR);
        } else {
            titleCard.setColor(color);
        }
        
        // effect
        String effect = req.getParameter("effect");
        if (effect == null) {
            titleCard.setEffect(TitleCard.DEFAULT_EFFECT);
        } else {
            titleCard.setEffect(effect);
        }
        
        // align
        String align = req.getParameter("align");
        if (align == null) {
            titleCard.setAlign(TitleCard.DEFAULT_ALIGN);
        } else {
            titleCard.setAlign(align);
        }
        
        // bgColor
        String bgColor = req.getParameter("bgColor");
        if (bgColor == null) {
            titleCard.setBgColor(TitleCard.DEFAULT_BG_COLOR);
        } else {
            titleCard.setBgColor(bgColor);
        }
        
        // style
        String style = req.getParameter("style");
        if (style == null) {
            titleCard.setStyle(TitleCard.DEFAULT_STYLE);
        } else {
            titleCard.setStyle(style);
        }
        
        // weight
        String weight = req.getParameter("weight");
        if (weight == null) {
            titleCard.setWeight(TitleCard.DEFAULT_WEIGHT);
        } else {
            titleCard.setWeight(weight);
        }
        
        // bgImg
        String bgImage = req.getParameter("bgImage");
        if ((bgImage == null) || bgImage.equals("")) {
            titleCard.setBgImage(TitleCard.DEFAULT_BG_IMG);
        } else {
            titleCard.setBgImage(bgImage);
        }
        
        
        //        episode.setDuration(0); // set 0 to notify episode get operation to recalculate duration.
        
        
        titleCard = titleCardMngr.save(titleCard);
        
        titleCard.setMessage(NnStringUtil.revertHtml(titleCard.getMessage()));
        
        return titleCard;
    }
    
    @RequestMapping(value = "title_card/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    String titleCardDelete(HttpServletResponse resp, HttpServletRequest req,
            @PathVariable("id") String idStr) {
        
        Long id = null;
        try {
            id = Long.valueOf(idStr);
        } catch (NumberFormatException e) {
        }
        if (id == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        TitleCardManager titleCardMngr = new TitleCardManager();
        TitleCard titleCard = titleCardMngr.findById(id);
        if (titleCard==null) {
            notFound(resp, "TitleCard Not Found");
            return null;
        }
        
        
        //            episode.setDuration(0); // set 0 to notify episode get operation to recalculate duration.
        
        
        titleCardMngr.delete(titleCard);
        
        return "OK";
    }
    
    @RequestMapping(value = "my_uploads/{id}", method = RequestMethod.GET)
    public @ResponseBody
    NnUserLibrary userUpload(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("id") String idStr) {
        
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
            notFound(resp, "Item Not Found");
            return null;
        }
        
        lib.setName(NnStringUtil.revertHtml(lib.getName()));
        
        return lib;
    }
    
    @RequestMapping(value = "my_uploads/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    NnUserLibrary userUploadUpdate(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("id") String idStr) {
        
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
            notFound(resp, "Item Not Found");
            return null;
        }
        
        String name = req.getParameter("name");
        if (name != null) {
            lib.setName(NnStringUtil.htmlSafeAndTruncated(name));
        }
        
        String imageUrl = req.getParameter("imageUrl");
        if (imageUrl != null) {
            lib.setImageUrl(imageUrl);
        }
        
        lib = libMngr.save(lib);
        lib.setName(NnStringUtil.revertHtml(lib.getName()));
        
        return lib;
    }
    
    @RequestMapping(value = { "my_uploads/{id}", "my_library/{id}" }, method = RequestMethod.DELETE)
    public @ResponseBody
    String userUploadsDelete(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("id") String idStr) {        
        
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
}
