package com.nncloudtv.web.api;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.StoreListingManager;

@Controller
@RequestMapping("api")
public class ApiPcs extends ApiGeneric {
    
    protected static Logger log = Logger.getLogger(ApiPcs.class.getName());
    
    private MsoManager msoMngr;
    private NnChannelManager channelMngr;
    private StoreListingManager storeListingMngr;
    
    @Autowired
    public ApiPcs(MsoManager msoMngr, NnChannelManager channelMngr, StoreListingManager storeListingMngr) {
        this.msoMngr = msoMngr;
        this.channelMngr = channelMngr;
        this.storeListingMngr = storeListingMngr;
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
        Map<String, Object> result = new TreeMap<String, Object>();
        
        results.add(result);
        
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
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
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
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
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
        
        Map<String, Object> result = new TreeMap<String, Object>();
        
        return result;
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
