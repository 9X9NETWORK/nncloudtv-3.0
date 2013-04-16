package com.nncloudtv.web.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.model.SysTagMap;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.StoreListingManager;
import com.nncloudtv.service.SysTagDisplayManager;
import com.nncloudtv.service.SysTagManager;
import com.nncloudtv.service.SysTagMapManager;
import com.nncloudtv.service.TagManager;

@Controller
@RequestMapping("api")
public class ApiMso extends ApiGeneric {
    
    protected static Logger log = Logger.getLogger(ApiMso.class.getName());
    
    private MsoManager msoMngr;
    private NnChannelManager channelMngr;
    private StoreListingManager storeListingMngr;
    private SysTagManager sysTagMngr;
    private SysTagDisplayManager sysTagDisplayMngr;
    private SysTagMapManager sysTagMapManager;
    
    @Autowired
    public ApiMso(MsoManager msoMngr, NnChannelManager channelMngr, StoreListingManager storeListingMngr,
            SysTagManager sysTagMngr, SysTagDisplayManager sysTagDisplayMngr, SysTagMapManager sysTagMapManager) {
        this.msoMngr = msoMngr;
        this.channelMngr = channelMngr;
        this.storeListingMngr = storeListingMngr;
        this.sysTagMngr = sysTagMngr;
        this.sysTagDisplayMngr = sysTagDisplayMngr;
        this.sysTagMapManager = sysTagMapManager;
    }
    
    @RequestMapping(value = "msos/{msoId}/sets", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, Object>> msoSets(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
        }
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            return null;
        }
        
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        Map<String, Object> result = null;
        
        List<SysTag> sets = sysTagMngr.findSetsByMsoId(mso.getId());
        if (sets == null || sets.size() == 0) {
            return results;
        }
        
        SysTagDisplay setMeta = null;
        for (SysTag set : sets) {
            setMeta = sysTagDisplayMngr.findBySysTagId(set.getId());
            if (setMeta != null) {
                result = setResponse(set, setMeta);
                results.add(result);
            }
        }
        
        return results;
    }
    
    @RequestMapping(value = "msos/{msoId}/sets", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> msoSetCreate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
        }
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name == null || name.isEmpty()) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        name = NnStringUtil.htmlSafeAndTruncated(name);
        
        // lang, default : en
        String lang = req.getParameter("lang");
        if (lang != null && NnStringUtil.validateLangCode(lang) != null) {
            // valid
        } else {
            lang = LangTable.LANG_EN;
        }
        
        // seq, default : 1
        Short seq = 1;
        String seqStr = req.getParameter("seq");
        if (seqStr != null) {
            try {
                seq = Short.valueOf(seqStr);
            } catch (NumberFormatException e) {
            }
            if (seq == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        }
        
        // tag TODO see NnChannelManager .processTagText .processChannelTag
        String tagText = req.getParameter("tag");
        String tag = null;
        if (tagText != null) {
            tag = TagManager.processTagText(tagText);
        }
        
        SysTag newSet = new SysTag();
        newSet.setType(SysTag.TYPE_SET);
        newSet.setMsoId(msoId);
        newSet.setSeq(seq);
        
        SysTagDisplay newSetMeta = new SysTagDisplay();
        newSetMeta.setCntChannel(0);
        newSetMeta.setLang(lang);
        newSetMeta.setName(name);
        if (tagText != null) {
            newSetMeta.setPopularTag(tag);
        }
        
        newSet = sysTagMngr.save(newSet);
        newSetMeta.setSystagId(newSet.getId());
        newSetMeta = sysTagDisplayMngr.save(newSetMeta);
        
        return setResponse(newSet, newSetMeta);
    }
    
    @RequestMapping(value = "sets/{setId}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> set(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Long setId = null;
        try {
            setId = Long.valueOf(setIdStr);
        } catch (NumberFormatException e) {
        }
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        SysTag set = sysTagMngr.findById(setId);
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        SysTagDisplay setMeta = sysTagDisplayMngr.findBySysTagId(set.getId());
        if (setMeta == null) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        return setResponse(set, setMeta);
    }
    
    @RequestMapping(value = "sets/{setId}", method = RequestMethod.PUT)
    public @ResponseBody
    Map<String, Object> setUpdate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Long setId = null;
        try {
            setId = Long.valueOf(setIdStr);
        } catch (NumberFormatException e) {
        }
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        SysTag set = sysTagMngr.findById(setId);
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        SysTagDisplay setMeta = sysTagDisplayMngr.findBySysTagId(set.getId());
        if (setMeta == null) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        // name
        String name = req.getParameter("name");
        if (name != null) {
            setMeta.setName(NnStringUtil.htmlSafeAndTruncated(name));
        }
        
        // lang
        String lang = req.getParameter("lang");
        if (lang != null && NnStringUtil.validateLangCode(lang) != null) {
            setMeta.setLang(lang);
        }
        
        // seq
        Short seq = null;
        String seqStr = req.getParameter("seq");
        if (seqStr != null) {
            try {
                seq = Short.valueOf(seqStr);
            } catch (NumberFormatException e) {
            }
            if (seq == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            set.setSeq(seq);
        }
        
        // tag TODO see NnChannelManager .processTagText .processChannelTag
        String tagText = req.getParameter("tag");
        String tag = null;
        if (tagText != null) {
            tag = TagManager.processTagText(tagText);
            setMeta.setPopularTag(tag);
        }
        
        if (seqStr != null) {
            set = sysTagMngr.save(set);
        }
        
        if (name != null || lang != null || tagText != null) {
            setMeta = sysTagDisplayMngr.save(setMeta);
        }
        
        return setResponse(set, setMeta);
    }
    
    @RequestMapping(value = "sets/{setId}", method = RequestMethod.DELETE)
    public @ResponseBody
    String setDelete(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Long setId = null;
        try {
            setId = Long.valueOf(setIdStr);
        } catch (NumberFormatException e) {
        }
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        SysTag set = sysTagMngr.findById(setId);
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        SysTagDisplay setMeta = sysTagDisplayMngr.findBySysTagId(set.getId());
        if (setMeta == null) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        // delete channels in set, SysTagMap
        List<SysTagMap>  sysTagMaps = sysTagMapManager.findSysTagMaps(set.getId());
        if (sysTagMaps != null && sysTagMaps.size() > 0) {
            sysTagMapManager.deleteAll(sysTagMaps);
        }
        // delete setMeta, SysTagDisplay
        sysTagDisplayMngr.delete(setMeta);
        // delete set, SysTag
        sysTagMngr.delete(set);
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "sets/{setId}/channels", method = RequestMethod.GET)
    public @ResponseBody
    List<NnChannel> setChannels(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Long setId = null;
        try {
            setId = Long.valueOf(setIdStr);
        } catch (NumberFormatException e) {
        }
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        SysTag set = sysTagMngr.findById(setId);
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        List<NnChannel> results = new ArrayList<NnChannel>();
        
        return results;
    }
    
    @RequestMapping(value = "sets/{setId}/channels", method = RequestMethod.POST)
    public @ResponseBody
    String setChannelAdd(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Long setId = null;
        try {
            setId = Long.valueOf(setIdStr);
        } catch (NumberFormatException e) {
        }
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        SysTag set = sysTagMngr.findById(setId);
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        // channelId
        Long channelId = null;
        String channelIdStr = req.getParameter("channelId");
        if (channelIdStr != null) {
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch (NumberFormatException e) {
            }
            if (channelId == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        NnChannel channel = null;
        channel = channelMngr.findById(channelId);
        if (channel == null) {
            badRequest(resp, "Channel Not Found");
            return null;
        }
        
        // create if not exist
        SysTagMap sysTagMap = sysTagMapManager.findSysTagMap(set.getId(), channel.getId());
        if (sysTagMap == null) {
            sysTagMap = new SysTagMap();
            sysTagMap.setSysTagId(set.getId());
            sysTagMap.setChannelId(channel.getId());
            sysTagMap.setSeq((short) 0);
        }
        
        // timeStart
        String timeStartStr = req.getParameter("timeStart");
        Short timeStart = null;
        if (timeStartStr != null) {
            try {
                timeStart = Short.valueOf(timeStartStr);
            } catch (NumberFormatException e) {
            }
            if (timeStart == null || timeStart < 0 || timeStart > 23) {
                badRequest(resp, INVALID_PARAMETER);
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
            }
            if (timeEnd == null || timeEnd < 0 || timeEnd > 23) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        }
        
        if (timeStartStr == null && timeEndStr == null) {
            timeStart = 0;
            timeEnd = 0;
        } else if (timeStartStr != null && timeEndStr != null) {
            if (timeStart == timeEnd) {
                timeStart = 0;
                timeEnd = 0;
            }
        } else { // they should be pair
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        sysTagMap.setTimeStart(timeStart);
        sysTagMap.setTimeEnd(timeEnd);
        
        sysTagMapManager.save(sysTagMap);
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "sets/{setId}/channels", method = RequestMethod.DELETE)
    public @ResponseBody
    String setChannelRemove(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Long setId = null;
        try {
            setId = Long.valueOf(setIdStr);
        } catch (NumberFormatException e) {
        }
        if (setId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        SysTag set = sysTagMngr.findById(setId);
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        Long channelId = null;
        String channelIdStr = req.getParameter("channelId");
        if (channelIdStr != null) {
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch (NumberFormatException e) {
            }
            if (channelId == null) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            badRequest(resp, MISSING_PARAMETER);
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
        
        SysTagMap sysTagMap = sysTagMapManager.findSysTagMap(set.getId(), channelId);
        if (sysTagMap == null) {
            // do nothing
        } else {
            sysTagMapManager.delete(sysTagMap);
        }
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "msos/{msoId}/store", method = RequestMethod.GET)
    public @ResponseBody
    List<NnChannel> storeChannels(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
        }
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            return null;
        }
        
        // channels
        String channelIdsStr = req.getParameter("channels");
        
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
        
        List<NnChannel> results = null;
        if (channelIdsStr != null) { // find by channelIdList
            
            String[] channelIdStrList = channelIdsStr.split(",");
            List<Long> channelIdList = new ArrayList<Long>();
            Long channelId = null;
            for (String channelIdStr : channelIdStrList) {
                
                channelId = null;
                try {
                    channelId = Long.valueOf(channelIdStr);
                } catch(Exception e) {
                }
                if (channelId != null) {
                    channelIdList.add(channelId);
                }
            }
            results = storeListingMngr.findByChannelIdsAndMsoId(channelIdList, msoId);
            
        } else if ((page > 0) && (rows > 0)) { // find by paging
            results = storeListingMngr.findByPaging(page, rows, msoId);
        } else { // default 50 items
            results = storeListingMngr.findByPaging(1, 50, msoId);
        }
        
        if (results == null) {
            return new ArrayList<NnChannel>();
        }
        
        return results;
    }
    
    @RequestMapping(value = "msos/{msoId}/store", method = RequestMethod.POST)
    public @ResponseBody
    String storeChannelAdd(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
        }
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            return null;
        }
        
        // channels
        String channelIdsStr = req.getParameter("channels");
        if (channelIdsStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        String[] channelIdStrList = channelIdsStr.split(",");
        List<Long> channelIdList = new ArrayList<Long>();
        Long channelId = null;
        for (String channelIdStr : channelIdStrList) {
            
            channelId = null;
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch(Exception e) {
            }
            if (channelId != null) {
                channelIdList.add(channelId);
            }
        }
        
        storeListingMngr.addChannelsToStore(channelIdList, mso.getId());
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "msos/{msoId}/store", method = RequestMethod.DELETE)
    public @ResponseBody
    String storeChannelRemove(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
        }
        if (msoId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            return null;
        }
        
        // channels
        String channelIdsStr = req.getParameter("channels");
        if (channelIdsStr == null) {
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        String[] channelIdStrList = channelIdsStr.split(",");
        List<Long> channelIdList = new ArrayList<Long>();
        Long channelId = null;
        for (String channelIdStr : channelIdStrList) {
            
            channelId = null;
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch(Exception e) {
            }
            if (channelId != null) {
                channelIdList.add(channelId);
            }
        }
        
        storeListingMngr.removeChannelsFromStore(channelIdList, mso.getId());
        
        okResponse(resp);
        return null;
    }

}
