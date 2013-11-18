package com.nncloudtv.web.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnUserProfile;
import com.nncloudtv.service.ApiMsoService;
import com.nncloudtv.service.CategoryService;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.service.NnUserProfileManager;
import com.nncloudtv.service.SetService;
import com.nncloudtv.service.StoreService;
import com.nncloudtv.service.TagManager;
import com.nncloudtv.web.json.cms.Category;
import com.nncloudtv.web.json.cms.Set;

@Controller
@RequestMapping("api")
public class ApiMso extends ApiGeneric {
    
    protected static Logger log = Logger.getLogger(ApiMso.class.getName());
    
    private MsoManager msoMngr;
    private NnChannelManager channelMngr;
    private StoreService storeService;
    private NnUserProfileManager userProfileMngr;
    private SetService setService;
    private ApiMsoService apiMsoService;
    private CategoryService categoryService;
    
    @Autowired
    public ApiMso(MsoManager msoMngr, NnChannelManager channelMngr, StoreService storeService,
            NnUserProfileManager userProfileMngr, SetService setService, ApiMsoService apiMsoService,
            CategoryService categoryService) {
        this.msoMngr = msoMngr;
        this.channelMngr = channelMngr;
        this.storeService = storeService;
        this.userProfileMngr = userProfileMngr;
        this.setService = setService;
        this.apiMsoService = apiMsoService;
        this.categoryService = categoryService;
    }
    
    /** indicate logging user has access right to target mso in PCS API
     *  @param requirePriv 3-characters string with '0' or '1' indicate the required of PCS read write delete access right
     */
    private boolean hasRightAccessPCS(Long userId, Long msoId, String requirePriv) {
        
        if (userId == null || msoId == null || requirePriv == null || requirePriv.matches("[01]{3}") == false) {
            return false;
        }
        
        NnUserProfile profile = userProfileMngr.findByUserIdAndMsoId(userId, msoId);
        if (profile == null) {
            profile = new NnUserProfile();
            profile.setPriv("000111");
        }
        if (profile.getPriv() == null) {
            profile.setPriv("000111");
        }
        
        if (requirePriv.charAt(0) == '1' && profile.getPriv().charAt(0) != '1') {
            return false;
        }
        if (requirePriv.charAt(1) == '1' && profile.getPriv().charAt(1) != '1') {
            return false;
        }
        if (requirePriv.charAt(2) == '1' && profile.getPriv().charAt(2) != '1') {
            return false;
        }
        
        return true;
    }
    
    @RequestMapping(value = "mso/{msoId}/sets", method = RequestMethod.GET)
    public @ResponseBody
    List<Set> msoSets(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = evaluateLong(msoIdStr);
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, mso.getId(), "100") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // lang
        String lang = req.getParameter("lang");
        if (lang != null) {
            lang = NnStringUtil.validateLangCode(lang);
        }
        
        List<Set> results = apiMsoService.msoSets(mso.getId(), lang);
        if (results == null) {
            log.info(printExitState(now, req, "ok"));
            return new ArrayList<Set>();
        }
        
        for (Set result : results) {
            result = SetService.normalize(result);
        }
        
        log.info(printExitState(now, req, "ok"));
        return results;
    }
    
    @RequestMapping(value = "mso/{msoId}/sets", method = RequestMethod.POST)
    public @ResponseBody
    Set msoSetCreate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, mso.getId(), "010") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name == null || name.isEmpty()) {
            badRequest(resp, MISSING_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return null;
        }
        name = NnStringUtil.htmlSafeAndTruncated(name);
        
        // seq, default : 0
        Short seq = null;
        String seqStr = req.getParameter("seq");
        if (seqStr != null) {
            try {
                seq = Short.valueOf(seqStr);
            } catch (NumberFormatException e) {
                seq = 0;
            }
        } else {
            seq = 0;
        }
        
        // tag TODO see NnChannelManager .processTagText .processChannelTag
        String tagText = req.getParameter("tag");
        String tag = null;
        if (tagText != null) {
            tag = TagManager.processTagText(tagText);
        }
        
        // sortingType, default : 1, channels sort by seq 
        Short sortingType = null;
        String sortingTypeStr = req.getParameter("sortingType");
        if (sortingTypeStr != null) {
            try {
                sortingType = Short.valueOf(sortingTypeStr);
            } catch (NumberFormatException e) {
                sortingType = Set.SORT_DEFAULT;
            }
            if (SetService.isValidSortingType(sortingType) == false) {
                sortingType = Set.SORT_DEFAULT;
            }
        } else {
            sortingType = Set.SORT_DEFAULT;
        }
        
        Set result = apiMsoService.msoSetCreate(mso.getId(), seq, tag, name, sortingType);
        if (result == null) {
            internalError(resp);
            log.warning(printExitState(now, req, "500"));
            return null;
        }
        
        result = SetService.normalize(result);
        
        log.info(printExitState(now, req, "ok"));
        return result;
    }
    
    @RequestMapping(value = "sets/{setId}", method = RequestMethod.GET)
    public @ResponseBody
    Set set(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long setId = evaluateLong(setIdStr);
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Set set = setService.findById(setId);
        if (set == null) {
            notFound(resp, "Set Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, set.getMsoId(), "100") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        Set result = apiMsoService.set(set.getId());
        if (result == null) {
            notFound(resp, "Set Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        result = SetService.normalize(result);
        
        log.info(printExitState(now, req, "ok"));
        return result;
    }
    
    @RequestMapping(value = "sets/{setId}", method = RequestMethod.PUT)
    public @ResponseBody
    Set setUpdate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long setId = evaluateLong(setIdStr);
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Set set = setService.findById(setId);
        if (set == null) {
            notFound(resp, "Set Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, set.getMsoId(), "110") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name != null) {
            name = NnStringUtil.htmlSafeAndTruncated(name);
        }
        
        // seq
        Short seq = null;
        String seqStr = req.getParameter("seq");
        if (seqStr != null) {
            try {
                seq = Short.valueOf(seqStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
        }
        
        // tag TODO see NnChannelManager .processTagText .processChannelTag
        String tagText = req.getParameter("tag");
        String tag = null;
        if (tagText != null) {
            tag = TagManager.processTagText(tagText);
        }
        
        // sortingType
        Short sortingType = null;
        String sortingTypeStr = req.getParameter("sortingType");
        if (sortingTypeStr != null) {
            try {
                sortingType = Short.valueOf(sortingTypeStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
            if (SetService.isValidSortingType(sortingType) == false) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
        }
        
        Set result = apiMsoService.setUpdate(set.getId(), name, seq, tag, sortingType);
        if (result == null) {
            log.warning("Unexcepted result : setServ.setUpdate return null");
            log.info(printExitState(now, req, "ok"));
            nullResponse(resp);
            return null;
        }
        
        result = SetService.normalize(result);
        
        log.info(printExitState(now, req, "ok"));
        return result;
    }
    
    @RequestMapping(value = "sets/{setId}", method = RequestMethod.DELETE)
    public @ResponseBody
    void setDelete(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long setId = null;
        try {
            setId = Long.valueOf(setIdStr);
        } catch (NumberFormatException e) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return ;
        }
        
        Set set = setService.findById(setId);
        if (set == null) {
            notFound(resp, "Set Not Found");
            log.info(printExitState(now, req, "404"));
            return ;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return ;
        }
        else if (hasRightAccessPCS(verifiedUserId, set.getMsoId(), "101") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return ;
        }
        
        apiMsoService.setDelete(set.getId());
        okResponse(resp);
        log.info(printExitState(now, req, "ok"));
        return ;
    }
    
    @RequestMapping(value = "sets/{setId}/channels", method = RequestMethod.GET)
    public @ResponseBody
    List<NnChannel> setChannels(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long setId = evaluateLong(setIdStr);
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Set set = setService.findById(setId);
        if (set == null) {
            notFound(resp, "Set Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, set.getMsoId(), "100") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        List<NnChannel> results = apiMsoService.setChannels(set.getId());
        if (results == null) {
            log.info(printExitState(now, req, "ok"));
            return new ArrayList<NnChannel>();
        }
        log.info(printExitState(now, req, "ok"));
        return results;
    }
    
    @RequestMapping(value = "sets/{setId}/channels", method = RequestMethod.POST)
    public @ResponseBody
    String setChannelAdd(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long setId = evaluateLong(setIdStr);
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Set set = setService.findById(setId);
        if (set == null) {
            notFound(resp, "Set Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, set.getMsoId(), "110") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // channelId
        Long channelId = null;
        String channelIdStr = req.getParameter("channelId");
        if (channelIdStr != null) {
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
        } else {
            badRequest(resp, MISSING_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return null;
        }
        
        NnChannel channel = null;
        channel = channelMngr.findById(channelId);
        if (channel == null) {
            badRequest(resp, "Channel Not Found");
            log.info(printExitState(now, req, "400"));
            return null;
        }
        
        Mso mso = msoMngr.findById(set.getMsoId());
        if (msoMngr.isPlayableChannel(channel.getId(), mso.getId()) == false) {
            badRequest(resp, "Channel Cant Play On This Mso");
            log.info(printExitState(now, req, "400"));
            return null;
        }
        
        // timeStart
        String timeStartStr = req.getParameter("timeStart");
        Short timeStart = null;
        if (timeStartStr != null) {
            try {
                timeStart = Short.valueOf(timeStartStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
            if (timeStart < 0 || timeStart > 23) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
        }
        
        // timeEnd
        String timeEndStr = req.getParameter("timeEnd");
        Short timeEnd = null;
        if (timeEndStr != null) {
            try {
                timeEnd = Short.valueOf(timeEndStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
            if (timeEnd < 0 || timeEnd > 23) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
        }
        
        if (timeStartStr == null && timeEndStr == null) {
            // as origin setting
        } else if (timeStartStr != null && timeEndStr != null) {
            if (timeStart == timeEnd) {
                timeStart = 0;
                timeEnd = 0;
            }
        } else { // they should be pair
            badRequest(resp, MISSING_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return null;
        }
        
        // alwaysOnTop
        String alwaysOnTopStr = req.getParameter("alwaysOnTop");
        Boolean alwaysOnTop = null;
        if (alwaysOnTopStr != null) {
            alwaysOnTop = Boolean.valueOf(alwaysOnTopStr);
        }
        
        apiMsoService.setChannelAdd(set.getId(), channel.getId(), timeStart, timeEnd, alwaysOnTop);
        okResponse(resp);
        log.info(printExitState(now, req, "ok"));
        return null;
    }
    
    @RequestMapping(value = "sets/{setId}/channels", method = RequestMethod.DELETE)
    public @ResponseBody
    String setChannelRemove(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long setId = evaluateLong(setIdStr);
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Set set = setService.findById(setId);
        if (set == null) {
            notFound(resp, "Set Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, set.getMsoId(), "101") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        Long channelId = null;
        String channelIdStr = req.getParameter("channelId");
        if (channelIdStr != null) {
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
        } else {
            badRequest(resp, MISSING_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return null;
        }
        
        /* NOTE : if channel not exist, this API should still work
        NnChannel channel = null;
        channel = channelMngr.findById(channelId);
        if (channel == null) {
            badRequest(resp, "Channel Not Found");
            return null;
        }
        */
        
        apiMsoService.setChannelRemove(set.getId(), channelId);
        okResponse(resp);
        log.info(printExitState(now, req, "ok"));
        return null;
    }
    
    @RequestMapping(value = "sets/{setId}/channels/sorting", method = RequestMethod.PUT)
    public @ResponseBody
    void setChannelsSorting(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long setId = evaluateLong(setIdStr);
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return ;
        }
        
        Set set = setService.findById(setId);
        if (set == null) {
            notFound(resp, "Set Not Found");
            log.info(printExitState(now, req, "404"));
            return ;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return ;
        }
        else if (hasRightAccessPCS(verifiedUserId, set.getMsoId(), "110") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return ;
        }
        
        String channelIdsStr = req.getParameter("channels");
        List<Long> channelIdList = new ArrayList<Long>();
        if (channelIdsStr == null || channelIdsStr.isEmpty()) {
            channelIdList = null;
        } else {
            
            String[] channelIdStrList = channelIdsStr.split(",");
            for (String channelIdStr : channelIdStrList) {
            
                Long channelId = null;
                try {
                    channelId = Long.valueOf(channelIdStr);
                } catch(Exception e) {
                    badRequest(resp, INVALID_PARAMETER);
                    log.info(printExitState(now, req, "400"));
                    return ;
                }
                channelIdList.add(channelId);
            }
        
            if (setService.isContainAllChannels(set.getId(), channelIdList) == false) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return ;
            }
        }
        
        apiMsoService.setChannelsSorting(set.getId(), channelIdList);
        okResponse(resp);
        log.info(printExitState(now, req, "ok"));
        return ;
    }
    
    @RequestMapping(value = "mso/{msoId}/store", method = RequestMethod.GET)
    public @ResponseBody
    List<Long> storeChannels(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = evaluateLong(msoIdStr);
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
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
        
        // channels
        java.util.Set<Long> channelIds = null;
        String channelsStr = req.getParameter("channels");
        if (channelsStr != null) {
            
            String[] channelIdsStr = channelsStr.split(",");
            channelIds = new HashSet<Long>();
            Long channelId = null;
            for (String channelIdStr : channelIdsStr) {
                
                channelId = null;
                try {
                    channelId = Long.valueOf(channelIdStr);
                } catch(Exception e) {
                }
                if (channelId != null) {
                    channelIds.add(channelId);
                }
            }
        }
        
        List<Long> results = apiMsoService.storeChannels(mso.getId(), channelIds, categoryId);
        if (results == null) {
            log.info(printExitState(now, req, "ok"));
            return new ArrayList<Long>();
        }
        log.info(printExitState(now, req, "ok"));
        return results;
    }
    
    @RequestMapping(value = "mso/{msoId}/store", method = RequestMethod.DELETE)
    public @ResponseBody
    String storeChannelRemove(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = evaluateLong(msoIdStr);
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, mso.getId(), "101") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // channels
        String channelsStr = req.getParameter("channels");
        if (channelsStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return null;
        }
        String[] channelIdsStr = channelsStr.split(",");
        List<Long> channelIds = new ArrayList<Long>();
        Long channelId = null;
        for (String channelIdStr : channelIdsStr) {
            
            channelId = null;
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch(Exception e) {
            }
            if (channelId != null) {
                channelIds.add(channelId);
            }
        }
        
        apiMsoService.storeChannelRemove(mso.getId(), channelIds);
        okResponse(resp);
        log.info(printExitState(now, req, "ok"));
        return null;
    }
    
    @RequestMapping(value = "mso/{msoId}/store", method = RequestMethod.POST)
    public @ResponseBody
    String storeChannelAdd(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = evaluateLong(msoIdStr);
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, mso.getId(), "110") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // channels
        String channelsStr = req.getParameter("channels");
        if (channelsStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return null;
        }
        String[] channelIdsStr = channelsStr.split(",");
        List<Long> channelIds = new ArrayList<Long>();
        Long channelId = null;
        for (String channelIdStr : channelIdsStr) {
            
            channelId = null;
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch(Exception e) {
            }
            if (channelId != null) {
                channelIds.add(channelId);
            }
        }
        
        apiMsoService.storeChannelAdd(mso.getId(), channelIds);
        okResponse(resp);
        log.info(printExitState(now, req, "ok"));
        return null;
    }
    
    @RequestMapping(value = "mso/{msoId}", method = RequestMethod.GET)
    public @ResponseBody
    Mso mso(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = evaluateLong(msoIdStr);
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, mso.getId(), "100") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        Mso result = apiMsoService.mso(mso.getId());
        log.info(printExitState(now, req, "ok"));
        return result;
    }
    
    @RequestMapping(value = "mso/{msoId}", method = RequestMethod.PUT)
    public @ResponseBody
    Mso msoUpdate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = evaluateLong(msoIdStr);
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, mso.getId(), "110") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // title
        String title = req.getParameter("title");
        if (title != null) {
            title = NnStringUtil.htmlSafeAndTruncated(title);
        }
        
        // logoUrl
        String logoUrl = req.getParameter("logoUrl");
        
        Mso result = apiMsoService.msoUpdate(mso.getId(), title, logoUrl);
        log.info(printExitState(now, req, "ok"));
        return result;
    }
    
    @RequestMapping(value = "mso/{msoId}/categories", method = RequestMethod.GET)
    public @ResponseBody
    List<Category> msoCategories(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = evaluateLong(msoIdStr);
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, mso.getId(), "100") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // lang
        String lang = req.getParameter("lang");
        if (lang != null) {
            lang = NnStringUtil.validateLangCode(lang);
            if (lang == null) {
                lang = NnUserManager.findLocaleByHttpRequest(req);
            }
        } else {
            lang = NnUserManager.findLocaleByHttpRequest(req);
        }
        
        List<Category> results = apiMsoService.msoCategories(mso.getId());
        
        for (Category result : results) {
            if (lang.equals(LangTable.LANG_ZH)) {
                result.setLang(LangTable.LANG_ZH);
                result.setName(result.getZhName());
            } else if (lang.equals(LangTable.LANG_EN)) {
                result.setLang(LangTable.LANG_EN);
                result.setName(result.getEnName());
            }
            result = CategoryService.normalize(result);
        }
        
        log.info(printExitState(now, req, "ok"));
        return results;
    }
    
    @RequestMapping(value = "mso/{msoId}/categories", method = RequestMethod.POST)
    public @ResponseBody
    Category msoCategoryCreate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = evaluateLong(msoIdStr);
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, mso.getId(), "010") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // seq, default : 1
        Short seq = null;
        String seqStr = req.getParameter("seq");
        if (seqStr != null) {
            try {
                seq = Short.valueOf(seqStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
        } else {
            seq = 1;
        }
        
        // zhName
        String zhName = req.getParameter("zhName");
        if (zhName != null) {
            zhName = NnStringUtil.htmlSafeAndTruncated(zhName);
        }
        
        // enName
        String enName = req.getParameter("enName");
        if (enName != null) {
            enName = NnStringUtil.htmlSafeAndTruncated(enName);
        }
        
        // lang
        String lang = req.getParameter("lang");
        if (lang != null) {
            lang = NnStringUtil.validateLangCode(lang);
            if (lang == null) {
                lang = NnUserManager.findLocaleByHttpRequest(req);
            }
        } else {
            lang = NnUserManager.findLocaleByHttpRequest(req);
        }
        
        Category result = apiMsoService.msoCategoryCreate(mso.getId(), seq, zhName, enName);
        if (result == null) {
            internalError(resp);
            log.warning(printExitState(now, req, "500"));
            return null;
        }
        
        if (lang.equals(LangTable.LANG_ZH)) {
            result.setLang(LangTable.LANG_ZH);
            result.setName(result.getZhName());
        } else if (lang.equals(LangTable.LANG_EN)) {
            result.setLang(LangTable.LANG_EN);
            result.setName(result.getEnName());
        }
        result = CategoryService.normalize(result);
        
        log.info(printExitState(now, req, "ok"));
        return result;
    }
    
    @RequestMapping(value = "category/{categoryId}", method = RequestMethod.GET)
    public @ResponseBody
    Category category(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("categoryId") String categoryIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long categoryId = evaluateLong(categoryIdStr);
        if (categoryId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            notFound(resp, "Category Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, category.getMsoId(), "100") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // lang
        String lang = req.getParameter("lang");
        if (lang != null) {
            lang = NnStringUtil.validateLangCode(lang);
            if (lang == null) {
                lang = NnUserManager.findLocaleByHttpRequest(req);
            }
        } else {
            lang = NnUserManager.findLocaleByHttpRequest(req);
        }
        
        Category result = apiMsoService.category(category.getId());
        if (result == null) {
            internalError(resp);
            log.warning(printExitState(now, req, "500"));
            return null;
        }
        
        if (lang.equals(LangTable.LANG_ZH)) {
            result.setLang(LangTable.LANG_ZH);
            result.setName(result.getZhName());
        } else if (lang.equals(LangTable.LANG_EN)) {
            result.setLang(LangTable.LANG_EN);
            result.setName(result.getEnName());
        }
        result = CategoryService.normalize(result);
        
        log.info(printExitState(now, req, "ok"));
        return result;
    }
    
    @RequestMapping(value = "category/{categoryId}", method = RequestMethod.PUT)
    public @ResponseBody
    Category categoryUpdate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("categoryId") String categoryIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long categoryId = evaluateLong(categoryIdStr);
        if (categoryId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            notFound(resp, "Category Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, category.getMsoId(), "110") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // seq
        Short seq = null;
        String seqStr = req.getParameter("seq");
        if (seqStr != null) {
            try {
                seq = Short.valueOf(seqStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return null;
            }
        }
        
        // zhName
        String zhName = req.getParameter("zhName");
        if (zhName != null) {
            zhName = NnStringUtil.htmlSafeAndTruncated(zhName);
        }
        
        // enName
        String enName = req.getParameter("enName");
        if (enName != null) {
            enName = NnStringUtil.htmlSafeAndTruncated(enName);
        }
        
        // lang
        String lang = req.getParameter("lang");
        if (lang != null) {
            lang = NnStringUtil.validateLangCode(lang);
            if (lang == null) {
                lang = NnUserManager.findLocaleByHttpRequest(req);
            }
        } else {
            lang = NnUserManager.findLocaleByHttpRequest(req);
        }
        
        Category result = apiMsoService.categoryUpdate(category.getId(), seq, zhName, enName);
        if (result == null) {
            internalError(resp);
            log.warning(printExitState(now, req, "500"));
            return null;
        }
        
        if (lang.equals(LangTable.LANG_ZH)) {
            result.setLang(LangTable.LANG_ZH);
            result.setName(result.getZhName());
        } else if (lang.equals(LangTable.LANG_EN)) {
            result.setLang(LangTable.LANG_EN);
            result.setName(result.getEnName());
        }
        result = CategoryService.normalize(result);
        
        log.info(printExitState(now, req, "ok"));
        return result;
    }
    
    @RequestMapping(value = "category/{categoryId}", method = RequestMethod.DELETE)
    public @ResponseBody
    void categoryDelete(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("categoryId") String categoryIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long categoryId = evaluateLong(categoryIdStr);
        if (categoryId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return ;
        }
        
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            notFound(resp, "Category Not Found");
            log.info(printExitState(now, req, "404"));
            return ;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return ;
        }
        else if (hasRightAccessPCS(verifiedUserId, category.getMsoId(), "101") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return ;
        }
        
        apiMsoService.categoryDelete(category.getId());
        okResponse(resp);
        log.info(printExitState(now, req, "ok"));
        return ;
    }
    
    @RequestMapping(value = "category/{categoryId}/channels", method = RequestMethod.GET)
    public @ResponseBody
    List<NnChannel> categoryChannels(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("categoryId") String categoryIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long categoryId = evaluateLong(categoryIdStr);
        if (categoryId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            notFound(resp, "Category Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, category.getMsoId(), "100") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        List<NnChannel> results = apiMsoService.categoryChannels(category.getId());
        log.info(printExitState(now, req, "ok"));
        return results;
    }
    
    @RequestMapping(value = "category/{categoryId}/channels", method = RequestMethod.POST)
    public @ResponseBody
    void categoryChannelAdd(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("categoryId") String categoryIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long categoryId = evaluateLong(categoryIdStr);
        if (categoryId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return ;
        }
        
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            notFound(resp, "Category Not Found");
            log.info(printExitState(now, req, "404"));
            return ;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return ;
        }
        else if (hasRightAccessPCS(verifiedUserId, category.getMsoId(), "110") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return ;
        }
        
        // channels
        String channelsStr = req.getParameter("channels");
        List<Long> channelIds = null;
        if (channelsStr == null) {
            channelIds = null;
        } else {
            String[] channelIdsStr = channelsStr.split(",");
            channelIds = new ArrayList<Long>();
            Long channelId = null;
            for (String channelIdStr : channelIdsStr) {
            
                channelId = null;
                try {
                    channelId = Long.valueOf(channelIdStr);
                } catch(Exception e) {
                }
                if (channelId != null) {
                    channelIds.add(channelId);
                }
            }
        }
        
        // channelId
        Long channelId = null;
        String channelIdStr = req.getParameter("channelId");
        if (channelIdStr != null) {
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return ;
            }
        }
        
        // seq
        String seqStr = req.getParameter("seq");
        Short seq = null;
        if (seqStr != null) {
            try {
                seq = Short.valueOf(seqStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                log.info(printExitState(now, req, "400"));
                return ;
            }
        }
        
        // alwaysOnTop
        String alwaysOnTopStr = req.getParameter("alwaysOnTop");
        Boolean alwaysOnTop = null;
        if (alwaysOnTopStr != null) {
            alwaysOnTop = Boolean.valueOf(alwaysOnTopStr);
        }
        
        apiMsoService.categoryChannelAdd(category, channelIds, channelId, seq, alwaysOnTop);
        okResponse(resp);
        log.info(printExitState(now, req, "ok"));
        return ;
    }
    
    @RequestMapping(value = "category/{categoryId}/channels", method = RequestMethod.DELETE)
    public @ResponseBody
    void categoryChannelRemove(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("categoryId") String categoryIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long categoryId = evaluateLong(categoryIdStr);
        if (categoryId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return ;
        }
        
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            notFound(resp, "Category Not Found");
            log.info(printExitState(now, req, "404"));
            return ;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return ;
        }
        else if (hasRightAccessPCS(verifiedUserId, category.getMsoId(), "101") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return ;
        }
        
        // channels
        String channelsStr = req.getParameter("channels");
        if (channelsStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return ;
        }
        String[] channelIdsStr = channelsStr.split(",");
        List<Long> channelIds = new ArrayList<Long>();
        Long channelId = null;
        for (String channelIdStr : channelIdsStr) {
            
            channelId = null;
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch(Exception e) {
            }
            if (channelId != null) {
                channelIds.add(channelId);
            }
        }
        
        apiMsoService.categoryChannelRemove(category.getId(), channelIds);
        okResponse(resp);
        log.info(printExitState(now, req, "ok"));
        return ;
    }
    
    @RequestMapping(value = "mso/{msoId}/store/categoryLocks", method = RequestMethod.GET)
    public @ResponseBody
    List<String> msoSystemCategoryLocks(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = evaluateLong(msoIdStr);
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, mso.getId(), "100") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        List<String> results = apiMsoService.msoSystemCategoryLocks(mso.getId());
        log.info(printExitState(now, req, "ok"));
        return results;
    }
    
    @RequestMapping(value = "mso/{msoId}/store/categoryLocks", method = RequestMethod.PUT)
    public @ResponseBody
    List<String> msoSystemCategoryLocksUpdate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Date now = new Date();
        log.info(printEnterState(now, req));
        
        Long msoId = evaluateLong(msoIdStr);
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            log.info(printExitState(now, req, "404"));
            return null;
        }
        
        Long verifiedUserId = userIdentify(req);
        if (verifiedUserId == null) {
            unauthorized(resp);
            log.info(printExitState(now, req, "401"));
            return null;
        }
        else if (hasRightAccessPCS(verifiedUserId, mso.getId(), "110") == false) {
            forbidden(resp);
            log.info(printExitState(now, req, "403"));
            return null;
        }
        
        // categories, indicate which system categories to be locked 
        String categoriesStr = req.getParameter("categories");
        if (categoriesStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            log.info(printExitState(now, req, "400"));
            return null;
        }
        String[] categoryIdsStr = categoriesStr.split(",");
        List<String> categoryIds = new ArrayList<String>();
        for (String categoryIdStr : categoryIdsStr) {
            if (categoryIdStr.equals(MsoConfig.DISABLE_ALL_SYSTEM_CATEGORY)) { // special lock for lock all System Category
                categoryIds.add(MsoConfig.DISABLE_ALL_SYSTEM_CATEGORY);
            } else if (evaluateLong(categoryIdStr) != null) {
                categoryIds.add(categoryIdStr);
            }
        }
        
        List<String> results = apiMsoService.msoSystemCategoryLocksUpdate(mso.getId(), categoryIds);
        log.info(printExitState(now, req, "ok"));
        return results;
    }

}
