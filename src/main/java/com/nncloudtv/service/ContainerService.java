package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.model.SysTagMap;

/**
 * This is father class of SetService and CategoryService, since their operation are similar
 *   and should put basic method into father class.
 * Other type list in SysTag.type may has its Service and recommend reference this class. 
 *
 */
@Service
public class ContainerService {
    
    protected static final Logger log = Logger.getLogger(ContainerService.class.getName());
    
    private SysTagManager sysTagMngr;
    private SysTagDisplayManager sysTagDisplayMngr;
    private SysTagMapManager sysTagMapMngr;
    private NnChannelManager channelMngr;
    
    @Autowired
    public ContainerService(SysTagManager sysTagMngr, SysTagDisplayManager sysTagDisplayMngr,
                        SysTagMapManager sysTagMapMngr, NnChannelManager channelMngr) {
        this.sysTagMngr = sysTagMngr;
        this.sysTagDisplayMngr = sysTagDisplayMngr;
        this.sysTagMapMngr = sysTagMapMngr;
        this.channelMngr = channelMngr;
    }
    
    /**
     * Delete a Container(SysTag, SysTagDisplay, SysTagMap).
     * @param sysTagId required, SysTag ID
     */
    public void delete(Long sysTagId) {
        
        // delete SysTagMap
        List<SysTagMap>  channels = sysTagMapMngr.findBySysTagId(sysTagId);
        if (channels != null && channels.size() > 0) {
            sysTagMapMngr.deleteAll(channels);
        }
        // delete SysTagDisplay
        List<SysTagDisplay> displays = sysTagDisplayMngr.findAllBySysTagId(sysTagId);
        if (displays != null && displays.size() > 0) {
            sysTagDisplayMngr.deleteAll(displays);
        }
        // delete SysTag
        SysTag sysTag = sysTagMngr.findById(sysTagId);
        if (sysTag != null) {
            sysTagMngr.delete(sysTag);
        }
    }
    
    /**
     * Get Channels from Container ordered by sequence, the Channels populate additional information (TimeStart, TimeEnd, Seq, AlwaysOnTop)
     *   retrieve from SysTagMap.
     * @param sysTagId required, SysTag ID
     * @return list of Channels */
    public List<NnChannel> getChannelsOrderBySeq(Long sysTagId) {
        
        if (sysTagId == null) {
            return new ArrayList<NnChannel>();
        }
        
        List<SysTagMap> sysTagMaps = sysTagMapMngr.findBySysTagId(sysTagId);
        if (sysTagMaps == null || sysTagMaps.isEmpty()) {
            return new ArrayList<NnChannel>();
        }
        
        List<Long> channelIdList = new ArrayList<Long>();
        for (SysTagMap item : sysTagMaps) {
            channelIdList.add(item.getChannelId());
        }
        List<NnChannel> channels = channelMngr.findByIds(channelIdList);
        if (channels == null || channels.isEmpty()) {
            return new ArrayList<NnChannel>();
        }
        
        Map<Long, NnChannel> channelMap = new TreeMap<Long, NnChannel>();
        for (NnChannel channel : channels) {
            channelMap.put(channel.getId(), channel);
        }
        
        List<NnChannel> results = new ArrayList<NnChannel>();
        for (SysTagMap sysTagMap : sysTagMaps) {
            NnChannel result = channelMap.get(sysTagMap.getChannelId());
            if (result != null) {
                result.setTimeStart(sysTagMap.getTimeStart());
                result.setTimeEnd(sysTagMap.getTimeEnd());
                result.setSeq(sysTagMap.getSeq());
                result.setAlwaysOnTop(sysTagMap.isAlwaysOnTop());
                results.add(result);
            } else {
                // TODO : Channel not exist, delete SysTagMap ?
            }
        }
        
        return results;
    }
    
    /**
     * Get Channels from Container ordered by updateTime, Channel with AlwaysOnTop set to True will put in the head of results,
     *   the Channels populate additional information (TimeStart, TimeEnd, Seq, AlwaysOnTop) retrieve from SysTagMap.
     * @param sysTagId required, SysTag ID
     * @return list of Channels */
    public List<NnChannel> getChannelsOrderByUpdateTime(Long sysTagId) {
        
        if (sysTagId == null) {
            return new ArrayList<NnChannel>();
        }
        List<NnChannel> channels = getChannelsOrderBySeq(sysTagId);
        if (channels == null) {
            return new ArrayList<NnChannel>();
        }
        if (channels.size() < 2) {
            return channels;
        }
        
        List<NnChannel> results = new ArrayList<NnChannel>();
        List<NnChannel> sortedChannels = new ArrayList<NnChannel>();
        for (NnChannel channel : channels) {
            if (channel.isAlwaysOnTop() == true) {
                results.add(channel);
            } else {
                sortedChannels.add(channel);
            }
        }
        
        Collections.sort(sortedChannels, channelMngr.getChannelComparator("default"));
        results.addAll(sortedChannels);
        
        return results;
    }

}
