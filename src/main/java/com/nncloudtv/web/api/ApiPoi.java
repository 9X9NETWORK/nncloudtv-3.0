package com.nncloudtv.web.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.Poi;
import com.nncloudtv.model.PoiCampaign;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnProgramManager;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.service.PoiCampaignManager;

@Controller
@RequestMapping("api")
public class ApiPoi extends ApiGeneric {
    
    protected static Logger log = Logger.getLogger(ApiPoi.class.getName());
    
    NnUserManager userMngr;
    NnProgramManager programMngr;
    PoiCampaignManager campaignMngr;
    
    @Autowired
    public ApiPoi(NnUserManager userMngr, NnProgramManager programMngr, PoiCampaignManager campaignMngr) {
        this.userMngr = userMngr;
        this.programMngr = programMngr;
        this.campaignMngr = campaignMngr;
    }
    
    @RequestMapping(value = "users/{userId}/poi_campaigns", method = RequestMethod.GET)
    public @ResponseBody
    List<PoiCampaign> userCampaigns(HttpServletRequest req,
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
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        List<PoiCampaign> results = campaignMngr.findByUserId(user.getId());
        if (results == null) {
            return new ArrayList<PoiCampaign>();
        }
        
        return results;
    }
    
    @RequestMapping(value = "users/{userId}/poi_campaigns", method = RequestMethod.POST)
    public @ResponseBody
    PoiCampaign userCampaignCreate(HttpServletRequest req,
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
        NnUser user = userMngr.findById(userId, brand.getId());
        if (user == null) {
            notFound(resp, "User Not Found");
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        name = NnStringUtil.htmlSafeAndTruncated(name);
        
        PoiCampaign newCampaign = new PoiCampaign();
        newCampaign.setMsoId(user.getMsoId());
        newCampaign.setUserId(user.getId());
        newCampaign.setName(name);
        
        // startDate
        String startDateStr = req.getParameter("startDate");
        if (startDateStr != null && startDateStr.length() > 0) {
            Long startDateLong = null;
            try {
                startDateLong = Long.valueOf(startDateStr);
            } catch (NumberFormatException e) {
            }
            if (startDateLong == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            
            newCampaign.setStartDate(new Date(startDateLong));
        } else {
            newCampaign.setStartDate(null);
        }
        
        // endDate
        String endDateStr = req.getParameter("endDate");
        if (endDateStr != null && endDateStr.length() > 0) {
            Long endDateLong = null;
            try {
                endDateLong = Long.valueOf(endDateStr);
            } catch (NumberFormatException e) {
            }
            if (endDateLong == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            
            newCampaign.setEndDate(new Date(endDateLong));
        } else {
            newCampaign.setEndDate(null);
        }
        
        PoiCampaign savedCampaign = campaignMngr.save(newCampaign);
        savedCampaign.setName(NnStringUtil.revertHtml(savedCampaign.getName()));
        
        return savedCampaign;
    }
    
    @RequestMapping(value = "poi_campaigns/{poiCampaignId}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> campaign(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiCampaignId") String poiCampaignIdStr) {
        
        Long poiCampaignId = null;
        try {
            poiCampaignId = Long.valueOf(poiCampaignIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiCampaignId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "poi_campaigns/{poiCampaignId}", method = RequestMethod.PUT)
    public @ResponseBody
    Map<String, Object> campaignUpdate(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiCampaignId") String poiCampaignIdStr) {
        
        Long poiCampaignId = null;
        try {
            poiCampaignId = Long.valueOf(poiCampaignIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiCampaignId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name != null) {
            name = NnStringUtil.htmlSafeAndTruncated(name);
        }
        
        // startDate
        String startDateStr = req.getParameter("startDate");
        if (startDateStr != null && startDateStr.length() > 0) {
            
        }
        
        // endDate
        String endDateStr = req.getParameter("endDate");
        if (endDateStr != null && endDateStr.length() > 0) {
            
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "poi_campaigns/{poiCampaignId}", method = RequestMethod.DELETE)
    public @ResponseBody
    String campaignDelete(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiCampaignId") String poiCampaignIdStr) {
        
        Long poiCampaignId = null;
        try {
            poiCampaignId = Long.valueOf(poiCampaignIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiCampaignId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "poi_campaigns/{poiCampaignId}/pois", method = RequestMethod.GET)
    public @ResponseBody
    List<Poi> campaignPois(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiCampaignId") String poiCampaignIdStr) {
        
        Long poiCampaignId = null;
        try {
            poiCampaignId = Long.valueOf(poiCampaignIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiCampaignId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        PoiCampaign campaign = campaignMngr.findCompaignById(poiCampaignId);
        if (campaign == null) {
            notFound(resp, "Campaign Not Found");
            return null;
        }
        
        // poiPointId
        Long poiPointId = null;
        String poiPointIdStr = req.getParameter("poiPointId");
        if (poiPointIdStr != null) {
            try {
                poiPointId = Long.valueOf(poiPointIdStr);
            } catch (NumberFormatException e) {
            }
            if (poiPointId == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        }
        
        List<Poi> results = null;
        if (poiPointId != null) {
            // find pois with campaign and point
            results = campaignMngr.findPoisByPointId(poiPointId);
        } else {
            // find pois with campaign
            results = campaignMngr.findPoisByCampaignId(campaign.getId());
        }
        
        if (results == null) {
            return new ArrayList<Poi>();
        }
        return results;
    }
    
    @RequestMapping(value = "poi_campaigns/{poiCampaignId}/pois", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> campaignPoiCreate(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiCampaignId") String poiCampaignIdStr) {
        
        Long poiCampaignId = null;
        try {
            poiCampaignId = Long.valueOf(poiCampaignIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiCampaignId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        // poiPointId
        Long poiPointId = null;
        String poiPointIdStr = req.getParameter("poiPointId");
        if (poiPointIdStr != null) {
            try {
                poiPointId = Long.valueOf(poiPointIdStr);
            } catch (NumberFormatException e) {
            }
            if (poiPointId == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        // poiPointId
        Long poiEventId = null;
        String poiEventIdStr = req.getParameter("poiEventId");
        if (poiEventIdStr != null) {
            try {
                poiEventId = Long.valueOf(poiEventIdStr);
            } catch (NumberFormatException e) {
            }
            if (poiEventId == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        // create the poi
        
        // startDate
        String startDateStr = req.getParameter("startDate");
        if (startDateStr != null && startDateStr.length() > 0) {
            
        }
        
        // endDate
        String endDateStr = req.getParameter("endDate");
        if (endDateStr != null && endDateStr.length() > 0) {
            
        }
        
        // hoursOfWeek
        String hoursOfWeek = req.getParameter("hoursOfWeek");
        if (hoursOfWeek != null) {
            if (hoursOfWeek.matches("[01]{168}")) {
                // valid hoursOfWeek format
            } else {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "pois/{poiId}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> poi(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiId") String poiIdStr) {
        
        Long poiId = null;
        try {
            poiId = Long.valueOf(poiIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "pois/{poiId}", method = RequestMethod.PUT)
    public @ResponseBody
    Map<String, Object> poiUpdate(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiId") String poiIdStr) {
        
        Long poiId = null;
        try {
            poiId = Long.valueOf(poiIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        // startDate
        String startDateStr = req.getParameter("startDate");
        if (startDateStr != null && startDateStr.length() > 0) {
            
        }
        
        // endDate
        String endDateStr = req.getParameter("endDate");
        if (endDateStr != null && endDateStr.length() > 0) {
            
        }
        
        // hoursOfWeek
        String hoursOfWeek = req.getParameter("hoursOfWeek");
        if (hoursOfWeek != null) {
            if (hoursOfWeek.matches("[01]{168}")) {
                // valid hoursOfWeek format
            } else {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "pois/{poiId}", method = RequestMethod.DELETE)
    public @ResponseBody
    String poiDelete(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiId") String poiIdStr) {
        
        Long poiId = null;
        try {
            poiId = Long.valueOf(poiIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "programs/{programId}/poi_points", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, Object>> programPoints(HttpServletRequest req,
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
        
        NnProgram program = programMngr.findById(programId);
        if (program == null) {
            notFound(resp, "Program Not Found");
            return null;
        }
        
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        Map<String, Object> result = new TreeMap<String, Object>();
        
        results.add(result);
        
        return results;
    }
    
    @RequestMapping(value = "programs/{programId}/poi_points", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> programPointCreate(HttpServletRequest req,
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
        
        NnProgram program = programMngr.findById(programId);
        if (program == null) {
            notFound(resp, "Program Not Found");
            return null;
        }
        
        // targetId
        Long targetId = programId;
        
        // targetType
        Short targetType = 0;
        
        // name
        String name = req.getParameter("name");
        if (name == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        name = NnStringUtil.htmlSafeAndTruncated(name);
        
        // offsetStart & offsetEnd
        Integer offsetStart = null;
        Integer offsetEnd = null;
        String offsetStartStr = req.getParameter("offsetStart");
        String offsetEndStr = req.getParameter("offsetEnd");
        if (offsetStartStr == null || offsetEndStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        } else {
            try {
                offsetStart = Integer.valueOf(offsetStartStr);
                offsetEnd = Integer.valueOf(offsetEndStr);
            } catch (NumberFormatException e) {
            }
            if ((offsetStart == null) || (offsetStart < 0) || (offsetEnd == null) || (offsetEnd <= 0) || (offsetEnd - offsetStart <= 0)) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        }
        
        // tags
        String tags = req.getParameter("tags");
        if (tags != null) {
            
        }
        
        // activate
        Boolean activate;
        String activateStr = req.getParameter("activate");
        if (activateStr != null) {
            activate = Boolean.valueOf(activateStr);
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "poi_points/{poiPointId}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> point(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiPointId") String poiPointIdStr) {
        
        Long poiPointId = null;
        try {
            poiPointId = Long.valueOf(poiPointIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiPointId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "poi_points/{poiPointId}", method = RequestMethod.PUT)
    public @ResponseBody
    Map<String, Object> pointUpdate(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiPointId") String poiPointIdStr) {
        
        Long poiPointId = null;
        try {
            poiPointId = Long.valueOf(poiPointIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiPointId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name != null) {
            name = NnStringUtil.htmlSafeAndTruncated(name);
        }
        
        // offsetStart
        Integer offsetStart = null;
        String offsetStartStr = req.getParameter("offsetStart");
        if (offsetStartStr != null) {
            try {
                offsetStart = Integer.valueOf(offsetStartStr);
            } catch (NumberFormatException e) {
            }
            if ((offsetStart == null) || (offsetStart < 0)) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            // origin setting
        }
        
        // offsetEnd
        Integer offsetEnd = null;
        String offsetEndStr = req.getParameter("offsetEnd");
        if (offsetEndStr != null) {
            try {
                offsetEnd = Integer.valueOf(offsetEndStr);
            } catch (NumberFormatException e) {
            }
            if ((offsetEnd == null) || (offsetEnd <= 0)) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            // origin setting
        }
        
        if (offsetEnd - offsetStart <= 0) {
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        
        // tags
        String tags = req.getParameter("tags");
        if (tags != null) {
            
        }
        
        // activate
        Boolean activate;
        String activateStr = req.getParameter("activate");
        if (activateStr != null) {
            activate = Boolean.valueOf(activateStr);
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "poi_points/{poiPointId}", method = RequestMethod.DELETE)
    public @ResponseBody
    String pointDelete(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiPointId") String poiPointIdStr) {
        
        Long poiPointId = null;
        try {
            poiPointId = Long.valueOf(poiPointIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiPointId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "poi_events", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> eventCreate(HttpServletRequest req,
            HttpServletResponse resp) {
        
        // name
        String name = req.getParameter("name");
        if (name == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        name = NnStringUtil.htmlSafeAndTruncated(name);
        
        // type TODO : need range check ?
        Short type = null;
        String typeStr = req.getParameter("type");
        if (typeStr != null) {
            try {
                type = Short.valueOf(typeStr);
            } catch (NumberFormatException e) {
            }
            if (type == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        // context
        String context = req.getParameter("context");
        if (context == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        // notificationMsg
        String notificationMsg = req.getParameter("notificationMsg");
        
        // notificationSchedule
        String notificationSchedule = req.getParameter("notificationSchedule");
        
        if (type == 3) {
            // set notificationMsg and notificationSchedule
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "poi_events/{poiEventId}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> event(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiEventId") String poiEventIdStr) {
        
        Long poiEventId = null;
        try {
            poiEventId = Long.valueOf(poiEventIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiEventId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "poi_events/{poiEventId}", method = RequestMethod.PUT)
    public @ResponseBody
    Map<String, Object> eventUpdate(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiEventId") String poiEventIdStr) {
        
        Long poiEventId = null;
        try {
            poiEventId = Long.valueOf(poiEventIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiEventId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name != null) {
            name = NnStringUtil.htmlSafeAndTruncated(name);
        }
        
        // type TODO : need range check ?
        Short type = null;
        String typeStr = req.getParameter("type");
        if (typeStr != null) {
            try {
                type = Short.valueOf(typeStr);
            } catch (NumberFormatException e) {
            }
            if (type == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            // origin setting
        }
        
        // context
        String context = req.getParameter("context");
        if (context != null) {
            
        }
        
        // notificationMsg
        String notificationMsg = req.getParameter("notificationMsg");
        
        // notificationSchedule
        String notificationSchedule = req.getParameter("notificationSchedule");
        
        if (type == 3) {
            // set notificationMsg and notificationSchedule
        }
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
    }
    
    @RequestMapping(value = "poi_events/{poiEventId}", method = RequestMethod.DELETE)
    public @ResponseBody
    String eventDelete(HttpServletRequest req,
            HttpServletResponse resp,
            @PathVariable("poiEventId") String poiEventIdStr) {
        
        Long poiEventId = null;
        try {
            poiEventId = Long.valueOf(poiEventIdStr);
        } catch (NumberFormatException e) {
        }
        if (poiEventId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        okResponse(resp);
        return null;
    }
    
}
