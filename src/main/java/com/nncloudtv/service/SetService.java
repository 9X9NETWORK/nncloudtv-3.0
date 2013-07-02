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
    private Set composeSet(SysTag set, SysTagDisplay setMeta) {
        
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
    
    /** find Sets that owned by Mso with specify display language */
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
    
    /** find Sets that owned by Mso */
    public List<Set> findByMsoId(Long msoId) {
        
        if (msoId == null) {
            return new ArrayList<Set>();
        }
        
        return findByMsoIdAndLang(msoId, null);
    }
    
    /** find Set by SysTag's Id */
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
            log.warning("invalid structure : SysTag's Id=" + setId + " exist but not found any of SysTagDisPlay");
            return null;
        }
        
        return composeSet(set, setMeta);
    }
    
    /** get Channels from Set ordered by Seq, the Channels populate additional information (TimeStart, TimeEnd, Seq, AlwaysOnTop)
     *  retrieve from SysTagMap */
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
                // TODO : Channel not exist
            }
        }
        
        for (NnChannel channel : results) {
            channelMngr.populateMoreImageUrl(channel); // TODO : sql in loop
            
            channel.setName(NnStringUtil.revertHtml(channel.getName()));
            channel.setIntro(NnStringUtil.revertHtml(channel.getIntro()));
        }
        
        return results;
    }
    
    /** get Channels from Set ordered by UpdateTime, Channel with AlwaysOnTop set to True will put in the head of results,
     *  the Channels populate additional information (TimeStart, TimeEnd, Seq, AlwaysOnTop) retrieve from SysTagMap */
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
    
    /** service for ApiMso.set */
    public Set set(Long setId) {
        
        if (setId == null) {
            return null;
        }
        
        return findById(setId);
    }
    
    /** service for ApiMso.setChannels */
    public List<NnChannel> setChannels(SysTag set) {
        
        if (set == null || set.getType() != SysTag.TYPE_SET) {
            return new ArrayList<NnChannel>();
        }
        
        List<NnChannel> results = null;
        if (set.getSorting() == SysTag.SORT_SEQ) {
            results = getChannelsOrderBySeq(set.getId());
        }
        if (set.getSorting() == SysTag.SORT_DATE) {
            results = getChannelsOrderByUpdateTime(set.getId());
        }
        
        if (results == null) {
            return new ArrayList<NnChannel>();
        }
        return results;
    }
    
    /** service for ApiMso.setChannelAdd */
    public void setChannelAdd(Long setId, Long channelId, Short timeStart, Short timeEnd, Boolean alwaysOnTop) {
        
        if (setId == null || channelId == null) {
            return ;
        }
        
        // create if not exist
        SysTagMap sysTagMap = sysTagMapMngr.findBySysTagIdAndChannelId(setId, channelId);
        if (sysTagMap == null) {
            sysTagMap = new SysTagMap(setId, channelId);
            sysTagMap.setSeq((short) 0);
            sysTagMap.setTimeStart((short) 0);
            sysTagMap.setTimeEnd((short) 0);
            sysTagMap.setAlwaysOnTop(false);
        }
        
        if (timeStart != null) {
            sysTagMap.setTimeStart(timeStart);
        }
        if (timeEnd != null) {
            sysTagMap.setTimeEnd(timeEnd);
        }
        if (alwaysOnTop != null) {
            sysTagMap.setAlwaysOnTop(alwaysOnTop);
        }
        
        sysTagMapMngr.save(sysTagMap);
    }
    
    /** service for ApiMso.setChannelRemove */
    public void setChannelRemove(Long setId, Long channelId) {
        
        if (setId == null || channelId == null) {
            return ;
        }
        
        SysTagMap sysTagMap = sysTagMapMngr.findBySysTagIdAndChannelId(setId, channelId);
        if (sysTagMap == null) {
            // do nothing
        } else {
            sysTagMapMngr.delete(sysTagMap);
        }
    }

}
