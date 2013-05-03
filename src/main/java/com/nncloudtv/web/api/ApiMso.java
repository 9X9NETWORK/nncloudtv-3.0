package com.nncloudtv.web.api;

import java.util.ArrayList;
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
import com.nncloudtv.web.json.cms.Set;

@Controller
@RequestMapping("api")
public class ApiMso extends ApiGeneric {
    
    protected static Logger log = Logger.getLogger(ApiMso.class.getName());
    
    private MsoManager msoMngr;
    private NnChannelManager channelMngr;
    private StoreListingManager storeListingMngr;
    private SysTagManager sysTagMngr;
    private SysTagDisplayManager sysTagDisplayMngr;
    private SysTagMapManager sysTagMapMngr;
    
    @Autowired
    public ApiMso(MsoManager msoMngr, NnChannelManager channelMngr, StoreListingManager storeListingMngr,
            SysTagManager sysTagMngr, SysTagDisplayManager sysTagDisplayMngr, SysTagMapManager sysTagMapMngr) {
        this.msoMngr = msoMngr;
        this.channelMngr = channelMngr;
        this.storeListingMngr = storeListingMngr;
        this.sysTagMngr = sysTagMngr;
        this.sysTagDisplayMngr = sysTagDisplayMngr;
        this.sysTagMapMngr = sysTagMapMngr;
    }
    
    @RequestMapping(value = "mso/{msoId}/sets", method = RequestMethod.GET)
    public @ResponseBody
    List<Set> msoSets(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            return null;
        }
        
        List<Set> results = new ArrayList<Set>();
        Set result = null;
        
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
    
    @RequestMapping(value = "mso/{msoId}/sets", method = RequestMethod.POST)
    public @ResponseBody
    Set msoSetCreate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
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
        Short seq = null;
        String seqStr = req.getParameter("seq");
        if (seqStr != null) {
            try {
                seq = Short.valueOf(seqStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            seq = 1;
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
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            if (sysTagMngr.isValidSortingType(sortingType) == false) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            sortingType = 1;
        }
        
        SysTag newSet = new SysTag();
        newSet.setType(SysTag.TYPE_SET);
        newSet.setMsoId(msoId);
        newSet.setSeq(seq);
        newSet.setSorting(sortingType);
        
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
    Set set(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Long setId = null;
        try {
            setId = Long.valueOf(setIdStr);
        } catch (NumberFormatException e) {
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
    Set setUpdate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Long setId = null;
        try {
            setId = Long.valueOf(setIdStr);
        } catch (NumberFormatException e) {
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
        
        // sortingType
        Short sortingType = null;
        String sortingTypeStr = req.getParameter("sortingType");
        if (sortingTypeStr != null) {
            try {
                sortingType = Short.valueOf(sortingTypeStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            if (sysTagMngr.isValidSortingType(sortingType) == false) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            set.setSorting(sortingType);
        }
        
        if (seqStr != null || sortingTypeStr != null) {
            set = sysTagMngr.save(set);
        }
        
        // cntChannel
        List<SysTagMap> channels = sysTagMapMngr.findSysTagMaps(set.getId());
        setMeta.setCntChannel(channels.size());
        
        setMeta = sysTagDisplayMngr.save(setMeta);
        
        sysTagMapMngr.reorderSysTagChannels(set.getId());
        
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
        List<SysTagMap>  sysTagMaps = sysTagMapMngr.findSysTagMaps(set.getId());
        if (sysTagMaps != null && sysTagMaps.size() > 0) {
            sysTagMapMngr.deleteAll(sysTagMaps);
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
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        SysTag set = sysTagMngr.findById(setId);
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        List<NnChannel> results = null;
        if (set.getSorting() == SysTag.SORT_SEQ) {
            results = sysTagMapMngr.findChannelsBySysTagIdOrderBySeq(set.getId());
        }
        if (set.getSorting() == SysTag.SORT_DATE) {
            results = sysTagMapMngr.findChannelsBySysTagIdOrderByUpdateTime(set.getId());
        }
        if (results == null) {
            return new ArrayList<NnChannel>();
        }
        
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
        SysTagMap sysTagMap = sysTagMapMngr.findSysTagMap(set.getId(), channel.getId());
        if (sysTagMap == null) {
            sysTagMap = new SysTagMap(set.getId(), channel.getId());
            sysTagMap.setSeq((short) 0);
            sysTagMap.setTimeStart((short) 0);
            sysTagMap.setTimeEnd((short) 0);
            sysTagMap.setAlwaysOnTop(false);
        }
        
        // timeStart
        String timeStartStr = req.getParameter("timeStart");
        Short timeStart = null;
        if (timeStartStr != null) {
            try {
                timeStart = Short.valueOf(timeStartStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            if (timeStart < 0 || timeStart > 23) {
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
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            if (timeEnd < 0 || timeEnd > 23) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        }
        
        if (timeStartStr == null && timeEndStr == null) {
            // as origin setting
        } else if (timeStartStr != null && timeEndStr != null) {
            if (timeStart == timeEnd) {
                sysTagMap.setTimeStart((short) 0);
                sysTagMap.setTimeEnd((short) 0);
            } else {
                sysTagMap.setTimeStart(timeStart);
                sysTagMap.setTimeEnd(timeEnd);
            }
        } else { // they should be pair
            badRequest(resp, MISSING_PARAMETER);
            return null;
        }
        
        // alwaysOnTop
        String alwaysOnTopStr = req.getParameter("alwaysOnTop");
        if (alwaysOnTopStr != null) {
            Boolean alwaysOnTop = Boolean.valueOf(alwaysOnTopStr);
            sysTagMap.setAlwaysOnTop(alwaysOnTop);
        }
        
        sysTagMapMngr.save(sysTagMap);
        
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
        
        SysTagMap sysTagMap = sysTagMapMngr.findSysTagMap(set.getId(), channelId);
        if (sysTagMap == null) {
            // do nothing
        } else {
            sysTagMapMngr.delete(sysTagMap);
        }
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "sets/{setId}/channels/sorting", method = RequestMethod.PUT)
    public @ResponseBody
    String setChannelsSorting(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("setId") String setIdStr) {
        
        Long setId = null;
        try {
            setId = Long.valueOf(setIdStr);
        } catch (NumberFormatException e) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        SysTag set = sysTagMngr.findById(setId);
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            notFound(resp, "Set Not Found");
            return null;
        }
        
        String channelIdsStr = req.getParameter("channels");
        if (channelIdsStr == null) {
            sysTagMapMngr.reorderSysTagChannels(set.getId());
            okResponse(resp);
            return null;
        }
        String[] channelIdStrList = channelIdsStr.split(",");
        
        List<SysTagMap> setChannels = sysTagMapMngr.findSysTagMaps(set.getId()); // it must same as setChannels's result
        List<Long> channelIdList = new ArrayList<Long>();
        List<Long> checkedChannelIdList = new ArrayList<Long>();
        for (SysTagMap item : setChannels) {
            channelIdList.add(item.getChannelId());
            checkedChannelIdList.add(item.getChannelId());
        }
        
        int index;
        Long channelId = null;
        List<SysTagMap> orderedSetChannels = new ArrayList<SysTagMap>();
        for (String channelIdStr : channelIdStrList) {
            
            channelId = null;
            try {
                channelId = Long.valueOf(channelIdStr);
            } catch(Exception e) {
            }
            if (channelId != null) {
                index = channelIdList.indexOf(channelId);
                if (index > -1) {
                    orderedSetChannels.add(setChannels.get(index));
                    checkedChannelIdList.remove(channelId);
                }
            }
        }
        // parameter should contain all channelId
        if (checkedChannelIdList.size() != 0) {
            badRequest(resp, INVALID_PARAMETER);
            return null;
        }
        
        int counter = 1;
        for (SysTagMap item : orderedSetChannels) {
            item.setSeq((short) counter);
            counter++;
        }
        
        sysTagMapMngr.saveAll(orderedSetChannels);
        
        okResponse(resp);
        return null;
    }
    
    @RequestMapping(value = "mso/{msoId}/store", method = RequestMethod.GET)
    public @ResponseBody
    List<Long> storeChannels(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            return null;
        }
        
        // categoryId, default : 1, category : All
        Long categoryId = null;
        String categoryIdStr = req.getParameter("categoryId");
        if (categoryIdStr != null) {
            try {
                categoryId = Long.valueOf(categoryIdStr);
            } catch (NumberFormatException e) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
            if (sysTagMngr.is9x9category(categoryId) == false) {
                badRequest(resp, INVALID_PARAMETER);
                return null;
            }
        } else {
            categoryId = (long) 1; // TODO categoryId = 1 is hard coded
        }
        
        // channels
        String channelIdsStr = req.getParameter("channels");
        
        // paging
        /*
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
        */
        
        List<Long> results = null;
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
            
        } else {
            results = storeListingMngr.findByMsoId(msoId, categoryId);
        }
        
        if (results == null) {
            return new ArrayList<Long>();
        }
        
        return results;
    }
    
    @RequestMapping(value = "mso/{msoId}/store", method = RequestMethod.DELETE)
    public @ResponseBody
    String storeChannelRemove(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
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
    
    @RequestMapping(value = "mso/{msoId}/store", method = RequestMethod.POST)
    public @ResponseBody
    String storeChannelAdd(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
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
    
    @RequestMapping(value = "mso/{msoId}", method = RequestMethod.GET)
    public @ResponseBody
    Mso mso(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            return null;
        }
        
        Mso result = mso;
        result.setTitle(NnStringUtil.revertHtml(result.getTitle()));
        result.setIntro(NnStringUtil.revertHtml(result.getIntro()));
        
        return result;
    }
    
    @RequestMapping(value = "mso/{msoId}", method = RequestMethod.PUT)
    public @ResponseBody
    Mso msoUpdate(HttpServletRequest req,
            HttpServletResponse resp, @PathVariable("msoId") String msoIdStr) {
        
        Long msoId = null;
        try {
            msoId = Long.valueOf(msoIdStr);
        } catch (NumberFormatException e) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            notFound(resp, "Mso Not Found");
            return null;
        }
        
        // title
        String title = req.getParameter("title");
        if (title != null) {
            title = NnStringUtil.htmlSafeAndTruncated(title);
            mso.setTitle(title);
        }
        
        // logoUrl
        String logoUrl = req.getParameter("logoUrl");
        if (logoUrl != null) {
            mso.setLogoUrl(logoUrl);
        }
        
        Mso result = null;
        if (title != null || logoUrl != null) {
            result = msoMngr.save(mso);
        } else {
            result = mso;
        }
        result.setTitle(NnStringUtil.revertHtml(result.getTitle()));
        result.setIntro(NnStringUtil.revertHtml(result.getIntro()));
        
        return result;
    }

}
