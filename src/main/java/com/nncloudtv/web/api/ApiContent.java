package com.nncloudtv.web.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.NnAd;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnChannelPref;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserLibrary;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.model.NnUserProfile;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.model.TitleCard;
import com.nncloudtv.service.CounterFactory;
import com.nncloudtv.service.MsoConfigManager;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnAdManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnChannelPrefManager;
import com.nncloudtv.service.NnEpisodeManager;
import com.nncloudtv.service.NnProgramManager;
import com.nncloudtv.service.NnUserLibraryManager;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.service.NnUserPrefManager;
import com.nncloudtv.service.NnUserProfileManager;
import com.nncloudtv.service.StoreService;
import com.nncloudtv.service.SysTagDisplayManager;
import com.nncloudtv.service.SysTagManager;
import com.nncloudtv.service.TitleCardManager;
import com.nncloudtv.web.json.cms.Category;

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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != channel.getUserId()) {
            forbidden(resp);
            return null;
        }
        
        NnChannelPrefManager prefMngr = new NnChannelPrefManager();
        
        prefMngr.delete(prefMngr.findByChannelIdAndItem(channelId, NnChannelPref.FB_AUTOSHARE));
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.POST)
    public @ResponseBody
    String facebookAutosharingCreate(HttpServletRequest req,
            HttpServletResponse resp,
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != channel.getUserId()) {
            forbidden(resp);
            return null;
        }
        
        String fbUserId = req.getParameter("userId");
        String accessToken = req.getParameter("accessToken");
        if (fbUserId == null || accessToken == null) {
            
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        String[] fbUserIdList = fbUserId.split(",");
        String[] accessTokenList = accessToken.split(",");
        
        if (fbUserIdList.length != accessTokenList.length) {
            
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        
        NnUserManager userMngr = new NnUserManager();
        Mso brand = new MsoManager().findOneByName(mso);
        NnUser user = userMngr.findById(verifiedUserId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        NnUserPrefManager userPrefMngr = new NnUserPrefManager();
        NnUserPref fbUserToken = userPrefMngr.findByUserAndItem(user, NnUserPref.FB_TOKEN);
        if (fbUserToken == null || fbUserToken.getValue() == null) {
            forbidden(resp);
            return null;
        }
        
        List<NnChannelPref> prefList = new ArrayList<NnChannelPref>();
        NnChannelPrefManager channelPrefMngr = new NnChannelPrefManager();
        
        for (int i = 0; i < fbUserIdList.length; i++) {
            if (accessTokenList[i].equals(fbUserToken.getValue())) { // post to facebook time line use app token
                prefList.add(new NnChannelPref(channel.getId(), NnChannelPref.FB_AUTOSHARE, channelPrefMngr.composeFacebookAutoshare(fbUserIdList[i], MsoConfigManager.getFacebookAppToken())));
            } else {
                prefList.add(new NnChannelPref(channel.getId(), NnChannelPref.FB_AUTOSHARE, channelPrefMngr.composeFacebookAutoshare(fbUserIdList[i], accessTokenList[i])));
            }
        }
        
        channelPrefMngr.delete(channelPrefMngr.findByChannelIdAndItem(channelId, NnChannelPref.FB_AUTOSHARE));
        
        channelPrefMngr.save(prefList);
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, Object>> facebookAutosharing(HttpServletRequest req,
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != channel.getUserId()) {
            forbidden(resp);
            return null;
        }
        
        NnChannelPrefManager prefMngr = new NnChannelPrefManager();
        List<NnChannelPref> prefList = prefMngr.findByChannelIdAndItem(channelId, NnChannelPref.FB_AUTOSHARE);
        
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        Map<String, Object> result;
        String[] parsedObj;
        for (NnChannelPref pref : prefList) {
            parsedObj = prefMngr.parseFacebookAutoshare(pref.getValue());
            if (parsedObj == null) {
                continue;
            }
            result = new TreeMap<String, Object>();
            result.put("userId", parsedObj[0]);
            result.put("accessToken", parsedObj[1]);
            results.add(result);
        }
        
        return results;
    }
    
    @RequestMapping(value = "episodes/{episodeId}/scheduledAutosharing/facebook", method = RequestMethod.GET)
    public @ResponseBody
    String facebookAutosharingScheduled(HttpServletRequest req,
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
        
        // mark as hook position
        episodeMngr.autoShareToFacebook(episode);
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "channels/{channelId}/autosharing/brand", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> brandAutosharingGet(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("channelId") String channelIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            notFound(resp, "Channel Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        NnChannelPrefManager prefMngr = new NnChannelPrefManager();
        NnChannelPref pref = prefMngr.getBrand(channel.getId());
        
        Map<String, Object> result = new TreeMap<String, Object>();
        result.put("brand", pref.getValue());
        log.info(printExitState(now, req, "ok"));
        return result;
    }
    
    @RequestMapping(value = "channels/{channelId}/autosharing/brand", method = RequestMethod.PUT)
    public @ResponseBody
    Map<String, Object> brandAutosharingSet(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("channelId") String channelIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            notFound(resp, "Channel Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        } else if (verifiedUserId != channel.getUserId()) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // brand
        String brand = req.getParameter("brand");
        if (brand == null) {
            badRequest(resp, MISSING_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return null;
        }
        MsoManager msoMngr = new MsoManager();
        Mso mso = msoMngr.findByName(brand);
        if (mso == null) {
            badRequest(resp, INVALID_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return null;
        }
        if (msoMngr.isValidBrand(channel.getId(), mso) == false) {
            badRequest(resp, INVALID_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return null;
        }
        
        NnChannelPrefManager prefMngr = new NnChannelPrefManager();
        prefMngr.setBrand(channel.getId(), mso);
        
        okResponse(resp);
        log.info(printExitState(now, req, "ok"));
        return null;
    }
    
    @RequestMapping(value = "channels/{channelId}/autosharing/validBrands", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, Object>> validBrandsAutosharingGet(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("channelId") String channelIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            notFound(resp, "Channel Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        /*
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        } else if (verifiedUserId != channel.getUserId()) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        */
        
        MsoManager msoMngr = new MsoManager();
        List<Mso> msos = msoMngr.getValidBrands(channel.getId());
        
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        for (Mso mso : msos) {
            if (mso.getName().equals("5f") || mso.getName().equals("tzuchi")) { // hard coded for policy required
                // skip
            } else {
                Map<String, Object> result = new TreeMap<String, Object>();
                result.put("brand", mso.getName());
                results.add(result);
            }
        }
        
        log.info(printExitState(now, req, "ok"));
        return results;
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        }
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(program.getChannelId());
        if ((channel == null) || (verifiedUserId != channel.getUserId())) {
            forbidden(resp);
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
        
        // TODO poiPoint collision
        /*
        if (programMngr.isPoiCollision(program, program.getStartTimeInt(), program.getEndTimeInt())) {
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        */
        
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        }
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(program.getChannelId());
        if ((channel == null) || (verifiedUserId != channel.getUserId())) {
            forbidden(resp);
            return null;
        }
        
        // delete title cards
        /*
        TitleCardManager titleCardMngr = new TitleCardManager();
        List<TitleCard> titleCards = titleCardMngr.findByProgramId(programId);
        titleCardMngr.delete(titleCards);
        */
        
        programMngr.delete(program);
        
        okResponse(resp);
        return null;
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
        //TitleCardManager titlecardMngr = new TitleCardManager();
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            notFound(resp, "Episode Not Found");
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        }
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(episode.getChannelId());
        if ((channel == null) || (verifiedUserId != channel.getUserId())) {
            forbidden(resp);
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
        //List<TitleCard> titlecardDeleteList = new ArrayList<TitleCard>();
        
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
                    /*
                    List<TitleCard> titlecards = titlecardMngr.findByProgramId(programId);
                    if (titlecards.size() > 0) {
                        titlecardDeleteList.addAll(titlecards);
                    }
                    */
                }
            }
        }
        log.info("program delete count = " + programDeleteList.size());
        //log.info("titlecard delete count = " + titlecardDeleteList.size());
        
        //titlecardMngr.delete(titlecardDeleteList);
        programMngr.delete(programDeleteList);
        
        okResponse(resp);
        return null;
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        }
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(episode.getChannelId());
        if ((channel == null) || (verifiedUserId != channel.getUserId())) {
            forbidden(resp);
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
        
        program = programMngr.create(episode, program);
        
        program.setName(NnStringUtil.revertHtml(program.getName()));
        program.setIntro(NnStringUtil.revertHtml(program.getIntro()));
        
        return program;
    }
    
    @RequestMapping(value = "channels", method = RequestMethod.GET)
    public @ResponseBody
    List<NnChannel> channelsSearch(HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam(required = false, value = "mso") String mso,
            @RequestParam(required = false, value = "sphere") String sphereStr,
            @RequestParam(required = false, value = "channels") String channelIdListStr,
            @RequestParam(required = false, value = "keyword") String keyword,
            @RequestParam(required = false, value = "userId") String userIdStr) {
    
        List<NnChannel> results = new ArrayList<NnChannel>();
        NnChannelManager channelMngr = new NnChannelManager();
        NnUserManager userMngr = new NnUserManager();
        NnUserProfileManager profileMngr = new NnUserProfileManager();
        StoreService storeService = new StoreService();
        MsoConfigManager configMngr = new MsoConfigManager();
        Mso brand = new MsoManager().findOneByName(mso);
        
        if (userIdStr != null) {
            
            Long userId = null;
            try {
                userId = Long.valueOf(userIdStr);
            } catch (NumberFormatException e) {
            }
            if (userId == null) {
                notFound(resp, INVALID_PARAMETER);
                return null;
            }
            
            NnUser user = userMngr.findById(userId, brand.getId());
            if (user == null) {
                notFound(resp, "User Not Found");
                return null;
            }
            
            results = channelMngr.findByUser(user, 0, false);
            
            Collections.sort(results, channelMngr.getChannelComparator("seq"));
            
        } else if (channelIdListStr != null) {
            
            Set<Long> channelIds = new HashSet<Long>();
            for (String channelIdStr : channelIdListStr.split(",")) {
                
                Long channelId = null;
                try {
                    channelId = Long.valueOf(channelIdStr);
                } catch (NumberFormatException e) {
                }
                if (channelId != null) {
                    channelIds.add(channelId);
                }
            }
            
            results = channelMngr.findByIds(new ArrayList<Long>(channelIds));
            Set<Long> fetchedChannelIds = new HashSet<Long>();
            for (NnChannel channel : results) {
                fetchedChannelIds.add(channel.getId());
            }
            for (Long channelId : channelIds) {
                if (fetchedChannelIds.contains(channelId) == false) {
                    log.info("channel not found: " + channelId);
                }
            }
            
            log.info("total channels = " + results.size());
            if (mso != null) {
                // filter out channels that not in MSO's store
                Set<Long> verifiedChannelIds = new HashSet<Long>(storeService.checkChannelIdsInMsoStore(fetchedChannelIds, brand.getId()));
                List<NnChannel> verifiedChannels = new ArrayList<NnChannel>();
                for (NnChannel channel : results) {
                    if (verifiedChannelIds.contains(channel.getId()) == true) {
                        verifiedChannels.add(channel);
                    }
                }
                results = verifiedChannels;
                log.info("total channels (filtered) = " + results.size());
            }
            
            Collections.sort(results, channelMngr.getChannelComparator("updateDate"));
            
        } else if (keyword != null && keyword.length() > 0) {
            
            log.info("keyword: " + keyword);
            Set<Long> channelIdSet = new HashSet<Long>();
            List<Long> channelIdList = new ArrayList<Long>();
            List<String> sphereList = new ArrayList<String>();
            String sphereFilter = null;
            if (sphereStr == null && mso != null) {
                log.info("mso = " + mso);
                MsoConfig supportedRegion = configMngr.findByMsoAndItem(brand, MsoConfig.SUPPORTED_REGION);
                if (supportedRegion != null) {
                    List<String> spheres = MsoConfigManager.parseSupportedRegion(supportedRegion.getValue());
                    sphereStr = StringUtils.join(spheres, ',');
                    log.info("mso supported region = " + sphereStr);
                }
            }
            if (sphereStr != null && !sphereStr.isEmpty()) {
                String[] sphereArr = new String[0];
                sphereArr = sphereStr.split(",");
                for (String sphere : sphereArr) {
                    sphereList.add(NnStringUtil.escapedQuote(sphere));
                }
                sphereList.add(NnStringUtil.escapedQuote(LangTable.OTHER));
                sphereFilter = "sphere in (" + StringUtils.join(sphereList, ',') + ")";
                log.info("sphere filter = " + sphereFilter);
            }
            
            List<NnChannel> channels = NnChannelManager.search(keyword, "store_only", sphereFilter, false, 0, 150);
            log.info("found channels = " + channels.size());
            for (NnChannel channel : channels) {
                channelIdSet.add(channel.getId());
            }
            
            Set<NnUserProfile> profiles = profileMngr.search(keyword, 0, 30);
            Set<Long> userIdSet = new HashSet<Long>();
            log.info("found profiles = " + profiles.size());
            for (NnUserProfile profile : profiles) {
                userIdSet.add(profile.getUserId());
            }
            List<NnUser> users = userMngr.findAllByIds(userIdSet);
            log.info("found users = " + users.size());
            
            for (NnUser user : users) {
                List<NnChannel> userChannels = channelMngr.findByUser(user, 30, false);
                for (NnChannel channel : userChannels) {
                    if (channel.getStatus() == NnChannel.STATUS_SUCCESS && channel.isPublic()) {
                        if ((!sphereList.isEmpty() && sphereList.contains(channel.getSphere())) || sphereList.isEmpty()) {
                            log.info("from curator = " + channel.getName());
                            channelIdSet.add(channel.getId());
                        }
                    }
                }
            }
            
            log.info("total channels = " + channelIdSet.size());
            if (mso != null) {
                channelIdList = storeService.checkChannelIdsInMsoStore(channelIdSet, brand.getId());
                log.info("total channels (filtered) = " + channelIdList.size());
            } else {
                channelIdList = new ArrayList<Long>(channelIdSet);
            }
            
            results = channelMngr.findByIds(channelIdList);
            
            Collections.sort(results, channelMngr.getChannelComparator("updateDate"));
        }
        
        results = channelMngr.responseNormalization(results);
        return results;
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
        
        channelMngr.populateCategoryId(channel);
        channelMngr.normalize(channel);
        
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != channel.getUserId()) {
            forbidden(resp);
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
        StoreService storeServ = new StoreService();
        Long categoryId = null;
        String categoryIdStr = req.getParameter("categoryId");
        if (categoryIdStr != null) {
            
            try {
                categoryId = Long.valueOf(categoryIdStr);
            } catch (NumberFormatException e) {
            }
            if (categoryId == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            
            if (storeServ.isNnCategory(categoryId) == false) {
                badRequest(resp, "Category Not Found");
                return null;
            }
        }
        
        // updateDate
        String updateDateStr = req.getParameter("updateDate");
        if (updateDateStr != null) {
            Date now = new Date();
            channel.setUpdateDate(now);
        }
        
        NnChannel savedChannel = channelMngr.save(channel);
        
        channelMngr.populateCategoryId(channel);
        if (categoryIdStr != null && categoryId != channel.getCategoryId()) {
            storeServ.setupChannelCategory(categoryId, channel.getId());
            channel.setCategoryId(categoryId);
        }
        savedChannel.setCategoryId(channel.getCategoryId());
        
        channelMngr.normalize(savedChannel);
        
        return savedChannel;
    }
    
    @RequestMapping(value = "tags", method = RequestMethod.GET)
    public @ResponseBody String[] tags(HttpServletRequest req, HttpServletResponse resp) {
        
        String categoryIdStr = req.getParameter("categoryId");
        if (categoryIdStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        String lang = req.getParameter("lang");
        if (lang == null) {
            lang = NnUserManager.findLocaleByHttpRequest(req);
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
        
        SysTagManager tagMngr = new SysTagManager();
        SysTag sysTag = tagMngr.findById(categoryId);
        if (sysTag == null) {
            
            badRequest(resp, "Category Not Found");
            return null;
        }
        
        SysTagDisplayManager displayMngr = new SysTagDisplayManager();
        SysTagDisplay tagDisplay = displayMngr.findBySysTagIdAndLang(categoryId, lang);
        
        String tagStr = tagDisplay.getPopularTag();
        if (tagDisplay == null || tagStr == null || tagStr.length() == 0) {
            return new String[0];
        }
        return tagStr.split(",");
    }
    
    @RequestMapping(value = "categories", method = RequestMethod.GET)
    public @ResponseBody
    List<Category> categories(HttpServletRequest req, HttpServletResponse resp) {
        
        String lang = req.getParameter("lang");
        if (lang == null) {
            lang = NnUserManager.findLocaleByHttpRequest(req);
        }
        
        StoreService storeServ = new StoreService();
        List<Category> categories = storeServ.getStoreCategories(lang);
        if (categories == null) {
            return new ArrayList<Category>();
        }
        
        return categories;
    }
    
    @RequestMapping(value = "store", method = RequestMethod.GET)
    public @ResponseBody
    List<Long> storeChannels(HttpServletRequest req, HttpServletResponse resp) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        StoreService storeService = new StoreService();
        
        // categoryId
        Long categoryId = null;
        String categoryIdStr = req.getParameter("categoryId");
        if (categoryIdStr != null) {
            try {
                categoryId = Long.valueOf(categoryIdStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
            if (storeService.isNnCategory(categoryId) == false) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
        }
        
        // sphere
        String sphere = req.getParameter("sphere");
        List<String> spheres;
        if (sphere == null || sphere.isEmpty()) {
            spheres = null;
        } else {
            spheres = new ArrayList<String>();
            String[] values = sphere.split(",");
            for (String value : values) {
                if (value.equals(LangTable.LANG_ZH) || value.equals(LangTable.LANG_EN) || value.equals(LangTable.OTHER)) {
                    spheres.add(value);
                } else {
                    badRequest(resp, INVALID_PARAMETER);
                    log.info(printExitState(now, req, "400"));
                    return null;
                }
            }
        }
        
        
        List<Long> channelIds = storeService.storeChannels(categoryId, spheres);
        log.info(printExitState(now, req, "ok"));
        return channelIds;
    }
    
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != channel.getUserId()) {
            forbidden(resp);
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
        
        Collections.sort(results, episodeMngr.getEpisodeSeqComparator());
        
        for (NnEpisode episode : results) {
            
            episode.setName(NnStringUtil.revertHtml(episode.getName()));
            episode.setIntro(NnStringUtil.revertHtml(episode.getIntro()));
            episode.setPlaybackUrl(NnStringUtil.getSharingUrl(episode.getChannelId(), episode.getId(), null));
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != channel.getUserId()) {
            forbidden(resp);
            return null;
        }
        
        String episodeIdsStr = req.getParameter("episodes");
        if (episodeIdsStr == null) {
            episodeMngr.reorderChannelEpisodes(channelId);
            okResponse(resp);
            return null;
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
        channelMngr.renewChannelUpdateDate(channel.getId());
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "episodes", method = RequestMethod.GET)
    public @ResponseBody
    List<NnEpisode> episodesSearch(HttpServletResponse resp,
            HttpServletRequest req,
            @RequestParam(required = false, value = "channelId") String channelIdStr) {
        
        if (channelIdStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
        }
        if (channelId == null) {
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        
        NnChannelManager channelMngr = new NnChannelManager();
        
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            notFound(resp, "Channel Not Found");
            return null;
        }
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        List<NnEpisode> results = null;
        
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
        if (results == null) {
            return new ArrayList<NnEpisode>();
        }
        
        Collections.sort(results, episodeMngr.getEpisodeSeqComparator());
        
        for (NnEpisode episode : results) {
            
            episode.setName(NnStringUtil.revertHtml(episode.getName()));
            episode.setIntro(NnStringUtil.revertHtml(episode.getIntro()));
            episode.setPlaybackUrl(NnStringUtil.getSharingUrl(episode.getChannelId(), episode.getId(), null));
        }
        
        return results;
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        }
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(episode.getChannelId());
        if ((channel == null) || (verifiedUserId != channel.getUserId())) {
            forbidden(resp);
            return null;
        }
        
        // delete shopping_info
        NnAdManager adMngr = new NnAdManager();
        NnAd nnAd = adMngr.findByEpisode(episode);
        if(nnAd != null) {
            adMngr.delete(nnAd);
        }
        
        // delete programs / title cards
        /*
        TitleCardManager titlecardMngr = new TitleCardManager();
        NnProgramManager programMngr = new NnProgramManager();
        List<TitleCard> titlecards = new ArrayList<TitleCard>();
        List<NnProgram> programs = programMngr.findByEpisodeId(episode.getId());
        for (NnProgram program : programs) {
            titlecards.addAll(titlecardMngr.findByProgramId(program.getId()));
        }
        titlecardMngr.delete(titlecards);
        programMngr.delete(programs);
        */
        
        // delete episode
        episodeMngr.delete(episode);
        
        // re-calcuate episode count
        if (channel != null) {
            channel.setCntEpisode(channelMngr.calcuateEpisodeCount(channel));
            channelMngr.save(channel);
        }
        
        okResponse(resp);
        return null;
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        }
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(episode.getChannelId());
        if ((channel == null) || (verifiedUserId != channel.getUserId())) {
            forbidden(resp);
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
        
        boolean autoShare = false;
        // isPublic
        String isPublicStr = req.getParameter("isPublic");
        if (isPublicStr != null) {
            Boolean isPublic = Boolean.valueOf(isPublicStr);
            if (isPublic != null) {
                if (episode.isPublic() == false && isPublic == true) {
                    autoShare = true;
                }
                episode.setPublic(isPublic);
            }
        }
        
        // rerun
        String rerunStr = req.getParameter("rerun");
        boolean rerun = false;
        if (rerunStr != null && Boolean.valueOf(rerunStr)) {
            rerun = true;
        }
        
        // duration
        String durationStr = req.getParameter("duration");
        if (durationStr != null) {
            Integer duration = null;
            try {
                duration = Integer.valueOf(durationStr);
            } catch (NumberFormatException e) {
            }
            if ((duration == null) || (duration <= 0)) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            episode.setDuration(duration);
        } else {
            episode.setDuration(episodeMngr.calculateEpisodeDuration(episode));
        }
        
        // seq
        String seqStr = req.getParameter("seq");
        if (seqStr != null) {
            Integer seq = null;
            try {
                seq = Integer.valueOf(seqStr);
            } catch (NumberFormatException e) {
            }
            if (seq == null || seq < 1) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            episode.setSeq(seq);
        }
        
        episode = episodeMngr.save(episode, rerun);
        
        episode.setName(NnStringUtil.revertHtml(episode.getName()));
        episode.setIntro(NnStringUtil.revertHtml(episode.getIntro()));
        
        // mark as hook position
        if (autoShare == true) {
            episodeMngr.autoShareToFacebook(episode);
            channelMngr.renewChannelUpdateDate(episode.getChannelId());
        }
        
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != channel.getUserId()) {
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
        
        boolean autoShare = false;
        // isPublic
        episode.setPublic(false); // default is draft
        String isPublicStr = req.getParameter("isPublic");
        if (isPublicStr != null) {
            Boolean isPublic = Boolean.valueOf(isPublicStr);
            if (isPublic != null) {
                if (isPublic == true) {
                    autoShare = true;
                }
                episode.setPublic(isPublic);
            }
        }
        
        // seq, default : at first position, trigger reorder 
        String seqStr = req.getParameter("seq");
        if (seqStr != null) {
            Integer seq = null;
            try {
                seq = Integer.valueOf(seqStr);
            } catch (NumberFormatException e) {
            }
            if (seq == null || seq < 1) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            episode.setSeq(seq);
        } else {
            episode.setSeq(0);
        }
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        
        episode = episodeMngr.save(episode);
        if (episode.getSeq() == 0) { // use special value to trigger reorder
            episodeMngr.reorderChannelEpisodes(channelId);
        }
        
        episode.setName(NnStringUtil.revertHtml(episode.getName()));
        episode.setIntro(NnStringUtil.revertHtml(episode.getIntro()));
        
        channel.setCntEpisode(channelMngr.calcuateEpisodeCount(channel));
        channelMngr.save(channel);
        
        // mark as hook position 
        if (autoShare == true) {
            episodeMngr.autoShareToFacebook(episode);
            channelMngr.renewChannelUpdateDate(channelId);
        }
        
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        }
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(episode.getChannelId());
        if ((channel == null) || (verifiedUserId != channel.getUserId())) {
            forbidden(resp);
            return null;
        }
        
        episode.setAdId(0);
        episodeMngr.save(episode);
        
        NnAd nnad = adMngr.findByEpisode(episode);
        if (nnad != null) {
            adMngr.delete(nnad);
        }
        
        okResponse(resp);
        return null;
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        }
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(episode.getChannelId());
        if ((channel == null) || (verifiedUserId != channel.getUserId())) {
            forbidden(resp);
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        }
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(program.getChannelId());
        if ((channel == null) || (verifiedUserId != channel.getUserId())) {
            forbidden(resp);
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
        titleCard.setMessage(NnStringUtil.htmlSafeAndTruncated(message, 2000));
        
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        }
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(titleCard.getChannelId());
        if ((channel == null) || (verifiedUserId != channel.getUserId())) {
            forbidden(resp);
            return null;
        }
        
        titleCardMngr.delete(titleCard);
        
        okResponse(resp);
        return null;
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != lib.getUserId()) {
            forbidden(resp);
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != lib.getUserId()) {
            forbidden(resp);
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
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            return null;
        } else if (verifiedUserId != lib.getUserId()) {
            forbidden(resp);
            return null;
        }
        
        libMngr.delete(lib);
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "weifilm", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, Object>> weifilm(HttpServletRequest req, HttpServletResponse resp) {
        
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        long channelId = 9010;
        log.info("weifilm channel = " + channelId);
        
        NnProgramManager programMngr = new NnProgramManager();
        List<NnProgram> programs = programMngr.findByChannelId(channelId); // input hard coded Channel ID
        if (programs == null || programs.size() == 0) {
            return results;
        }
        log.info("program size = " + programs.size());
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        List<NnEpisode> episodes = episodeMngr.findByChannelId(channelId); // input hard coded Channel ID
        if (episodes == null || episodes.size() == 0) {
            return results;
        }
        log.info("episode size = " + episodes.size());
        
        Map<Long, NnEpisode> episodeMap = new TreeMap<Long, NnEpisode>();
        for (NnEpisode episode : episodes) {
            episodeMap.put(episode.getId(), episode);
        }

        Map<String, Object> result;
        NnEpisode episode;
        CounterFactory factory = new CounterFactory();
        for (NnProgram program : programs) {
            result = new TreeMap<String, Object>();
            String[] fragment = program.getFileUrl().split("watch\\?v=");
            if (fragment.length > 1) {
                String youtubeId = fragment[1];
                result.put("youtubeId", youtubeId); // youtubeId YouTube ID
            }
            episode = episodeMap.get(program.getEpisodeId());
            if (episode != null) {
                String counterName = "s_ch" + episode.getChannelId() + "_e" + episode.getId();
                double score = (double)factory.getCount(counterName) / 10;
                log.info("counter name = " + counterName);
                log.info(episode.getName() + ", score = " + score);
                result.put("score", score); // score: 得分
                // shareUrl 用於分享及點擊觀看的網址
                String url = "http://" + Mso.NAME_CTS + "."
                           + MsoConfigManager.getServerDomain().replaceAll("^www\\.", "")
                           + "/view?mso=cts&ch=" + episode.getChannelId()
                           + "&ep=e" + episode.getId();
                result.put("shareUrl", url);
                result.put("updateDate", episode.getAdId()); // updateDate 更新日期 (timestamp)
            }
            results.add(result);
        }
        
        return results;
    }
}
