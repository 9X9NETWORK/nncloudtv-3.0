package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.model.SysTagMap;
import com.nncloudtv.web.json.cms.Set;

@Service
public class SetService {
    
    protected static final Logger log = Logger.getLogger(SetService.class.getName());
    
    private SysTagManager sysTagMngr;
    private SysTagDisplayManager sysTagDisplayMngr;
    private SysTagMapManager sysTagMapMngr;
    private NnChannelManager channelMngr;
    
    @Autowired
    public SetService(SysTagManager sysTagMngr, SysTagDisplayManager sysTagDisplayMngr,
                        SysTagMapManager sysTagMapMngr, NnChannelManager channelMngr) {
        this.sysTagMngr = sysTagMngr;
        this.sysTagDisplayMngr = sysTagDisplayMngr;
        this.sysTagMapMngr = sysTagMapMngr;
        this.channelMngr = channelMngr;
    }
    
    /** build Set from SysTag and SysTagDisplay */
    public Set composeSet(SysTag set, SysTagDisplay setMeta) {
        
        Set setResp = new Set();
        setResp.setId(set.getId());
        setResp.setMsoId(set.getMsoId());
        setResp.setDisplayId(setMeta.getId());
        setResp.setChannelCnt(setMeta.getCntChannel());
        setResp.setLang(setMeta.getLang());
        setResp.setSeq(set.getSeq());
        setResp.setTag(setMeta.getPopularTag());
        setResp.setName(NnStringUtil.revertHtml(setMeta.getName()));
        setResp.setSortingType(set.getSorting());
        
        return setResp;
    }
    
    /** find Sets that owned by Mso with specify display language
     *  @param msoId required, result Sets that belong to this specified Mso
     *  @param lang optional, result Sets has specified display language
     *  @return list of Sets */
    public List<Set> findByMsoIdAndLang(Long msoId, String lang) {
        
        List<Set> results = new ArrayList<Set>();
        Set result = null;
        
        if (msoId == null) {
            return new ArrayList<Set>();
        }
        
        //List<SysTag> results = dao.findByMsoIdAndType(msoId, SysTag.TYPE_SET);
        List<SysTag> sets = sysTagMngr.findByMsoIdAndType(msoId, SysTag.TYPE_SET);
        if (sets == null || sets.size() == 0) {
            return new ArrayList<Set>();
        }
        
        SysTagDisplay setMeta = null;
        for (SysTag set : sets) {
            
            if (lang != null) {
                setMeta = sysTagDisplayMngr.findBySysTagIdAndLang(set.getId(), lang);
            } else {
                setMeta = sysTagDisplayMngr.findBySysTagId(set.getId());
            }
            
            if (setMeta != null) {
                result = composeSet(set, setMeta);
                results.add(result);
            } else {
                if (lang == null) {
                    log.warning("invalid structure : SysTag's Id=" + set.getId() + " exist but not found any of SysTagDisPlay");
                } else {
                    log.info("SysTag's Id=" + set.getId() + " exist but not found match SysTagDisPlay for lang=" + lang);
                }
            }
        }
        
        return results;
    }
    
    /** find Sets that owned by Mso
     *  @param msoId required, result Sets that belong to this specified Mso
     *  @return list of Sets */
    public List<Set> findByMsoId(Long msoId) {
        
        if (msoId == null) {
            return new ArrayList<Set>();
        }
        
        return findByMsoIdAndLang(msoId, null);
    }
    
    /** find Set by SysTag's Id
     *  @param setId required, SysTag's ID with type = Set
     *  @return object Set or null if not exist */
    public Set findById(Long setId) {
        
        if (setId == null) {
            return null;
        }
        
        SysTag set = sysTagMngr.findById(setId);
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            return null;
        }
        
        SysTagDisplay setMeta = sysTagDisplayMngr.findBySysTagId(set.getId());
        if (setMeta == null) {
            log.warning("invalid structure : SysTag's Id=" + set.getId() + " exist but not found any of SysTagDisPlay");
            return null;
        }
        
        return composeSet(set, setMeta);
    }
    
    /** get Channels from Set ordered by Seq, the Channels populate additional information (TimeStart, TimeEnd, Seq, AlwaysOnTop)
     *  retrieve from SysTagMap
     *  @param setId required, SysTag's ID with type = Set
     *  @return list of Channels */
    public List<NnChannel> getChannelsOrderBySeq(Long setId) {
        
        if (setId == null) {
            return new ArrayList<NnChannel>();
        }
        
        List<SysTagMap> sysTagMaps = sysTagMapMngr.findBySysTagId(setId);
        if (sysTagMaps == null || sysTagMaps.size() == 0) {
            return new ArrayList<NnChannel>();
        }
        
        List<Long> channelIdList = new ArrayList<Long>();
        for (SysTagMap item : sysTagMaps) {
            channelIdList.add(item.getChannelId());
        }
        List<NnChannel> channels = channelMngr.findByIds(channelIdList);
        if (channels == null || channels.size() == 0) {
            return new ArrayList<NnChannel>();
        }
        
        Map<Long, NnChannel> channelMap = new TreeMap<Long, NnChannel>();
        for (NnChannel channel : channels) {
            channelMap.put(channel.getId(), channel);
        }
        List<NnChannel> results = new ArrayList<NnChannel>();
        NnChannel result = null;
        for (SysTagMap item : sysTagMaps) {
            result = channelMap.get(item.getChannelId());
            if (result != null) {
                result.setTimeStart(item.getTimeStart());
                result.setTimeEnd(item.getTimeEnd());
                result.setSeq(item.getSeq());
                result.setAlwaysOnTop(item.isAlwaysOnTop());
                results.add(result);
            } else {
                // TODO : Channel not exist, delete SysTagMap ?
            }
        }
        
        return results;
    }
    
    /** get Channels from Set ordered by UpdateTime, Channel with AlwaysOnTop set to True will put in the head of results,
     *  the Channels populate additional information (TimeStart, TimeEnd, Seq, AlwaysOnTop) retrieve from SysTagMap
     *  @param setId required, SysTag's ID with type = Set
     *  @return list of Channels */
    public List<NnChannel> getChannelsOrderByUpdateTime(Long setId) {
        
        if (setId == null) {
            return new ArrayList<NnChannel>();
        }
        List<NnChannel> channels = getChannelsOrderBySeq(setId);
        if (channels == null) {
            return new ArrayList<NnChannel>();
        }
        if (channels.size() < 2) {
            return channels;
        }
        
        List<NnChannel> results = new ArrayList<NnChannel>();
        List<NnChannel> orderedChannels = new ArrayList<NnChannel>();
        for (NnChannel channel : channels) {
            if (channel.isAlwaysOnTop() == true) {
                results.add(channel);
            } else {
                orderedChannels.add(channel);
            }
        }
        
        Collections.sort(orderedChannels, channelMngr.getChannelComparator("default"));
        results.addAll(orderedChannels);
        
        return results;
    }
    
    /** check if input Channel's IDs represent all Channels in the Set
     *  @param setId required, SysTag's ID with type = Set
     *  @param channelIds required, Channel's IDs to be tested
     *  @return true if full match, false for not */
    public boolean isContainAllChannels(Long setId, List<Long> channelIds) {
        
        if (setId == null || channelIds == null) {
            return false;
        }
        
        // it must same as setChannels's result
        List<SysTagMap> setChannels = sysTagMapMngr.findBySysTagId(setId);
        if (setChannels == null) {
            if (channelIds.size() == 0) {
                return true;
            } else {
                return false;
            }
        }
        
        int index;
        for (SysTagMap channel : setChannels) {
            index = channelIds.indexOf(channel.getChannelId());
            if (index > -1) {
                // pass
            } else {
                // input missing this Channel ID 
                return false;
            }
        }
        
        if (setChannels.size() != channelIds.size()) {
            // input contain duplicate or other Channel Id
            return false;
        }
        
        return true;
    }

}
