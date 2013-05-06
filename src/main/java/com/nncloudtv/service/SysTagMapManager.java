package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.SysTagMapDao;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTagMap;

@Service
public class SysTagMapManager {
    
    protected static final Logger log = Logger.getLogger(SysTagMapManager.class.getName());
    
    private SysTagMapDao dao = new SysTagMapDao();
    private NnChannelManager channelMngr;
    
    @Autowired
    public SysTagMapManager(NnChannelManager channelMngr) {
        this.channelMngr = channelMngr;
    }
    
    public SysTagMap save(SysTagMap sysTagMap) {
        
        if (sysTagMap == null) {
            return null;
        }
        
        Date now = new Date();
        if (sysTagMap.getCreateDate() == null) {
            sysTagMap.setCreateDate(now);
        }
        sysTagMap.setUpdateDate(now);
        
        SysTagMap result = dao.save(sysTagMap);
        
        return result;
    }
    
    public List<SysTagMap> saveAll(List<SysTagMap> sysTagMaps) {
        
        if (sysTagMaps == null || sysTagMaps.size() == 0) {
            return new ArrayList<SysTagMap>();
        }
        
        Date now = new Date();
        for (SysTagMap item : sysTagMaps) {
            if (item.getCreateDate() == null) {
                item.setCreateDate(now);
            }
            item.setUpdateDate(now);
        }
        
        List<SysTagMap> results = dao.saveAll(sysTagMaps);
        if (results == null) {
            return new ArrayList<SysTagMap>();
        }
        
        return results;
    }
    
    public void reorderSysTagChannels(Long sysTagId) {
        
        if (sysTagId == null) {
            return ;
        }
        
        List<SysTagMap> sysTagMaps = dao.findSysTagMaps(sysTagId);
        if (sysTagMaps == null || sysTagMaps.size() == 0) {
            return ;
        }
        Collections.sort(sysTagMaps, getSysTagMapComparator());
        
        log.info("sysTagMaps.size() = " + sysTagMaps.size());
        
        for (int i = 0; i < sysTagMaps.size(); i++) {
            sysTagMaps.get(i).setSeq((short) (i + 1));
        }
        
        dao.saveAll(sysTagMaps);
    }
    
    private Comparator<SysTagMap> getSysTagMapComparator() {
        class SysTagMapComparator implements Comparator<SysTagMap> {
            public int compare(SysTagMap sysTagMap1, SysTagMap sysTagMap2) {
                short seq1 = sysTagMap1.getSeq();
                short seq2 = sysTagMap2.getSeq();
                return (int) (seq1 - seq2);
            }
        }
        return new SysTagMapComparator();
    }
    
    public void delete(SysTagMap sysTagMap) {
        if (sysTagMap == null) {
            return ;
        }
        dao.delete(sysTagMap);
    }
    
    public void deleteAll(List<SysTagMap> sysTagMaps) {
        if (sysTagMaps == null || sysTagMaps.size() == 0) {
            return ;
        }
        dao.deleteAll(sysTagMaps);
    }
    
    public SysTagMap findSysTagMap(Long sysTagId, Long channelId) {
        
        if (sysTagId == null || channelId == null) {
            return null;
        }
        
        return dao.findSysTagMap(sysTagId, channelId);
    }
    
    public List<SysTagMap> findSysTagMaps(Long sysTagId) {
        
        if (sysTagId == null) {
            return new ArrayList<SysTagMap>();
        }
        
        List<SysTagMap> sysTagMaps = dao.findSysTagMaps(sysTagId);
        if (sysTagMaps == null) {
            return new ArrayList<SysTagMap>();
        }
        
        return sysTagMaps;
    }
    
    /** Used by Set, the channels in the Set with additional column composed from sysTagMap */
    public List<NnChannel> findChannelsBySysTagIdOrderBySeq(Long sysTagId) {
        
        if (sysTagId == null) {
            return new ArrayList<NnChannel>();
        }
        
        List<SysTagMap> sysTagMaps = findSysTagMaps(sysTagId);
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
            }
        }
        
        for (NnChannel channel : results) {
            channelMngr.populateMoreImageUrl(channel); // TODO : the sql will be big n with list's length
            
            channel.setName(NnStringUtil.revertHtml(channel.getName()));
            channel.setIntro(NnStringUtil.revertHtml(channel.getIntro()));
        }
        
        return results;
    }
    
    /**     Rewrite from findChannelsBySysTagIdOrderBySeq method, the channels order by update time,
     *  additional, channel will set on top if needed */
    public List<NnChannel> findChannelsBySysTagIdOrderByUpdateTime(Long sysTagId) {
        
        if (sysTagId == null) {
            return new ArrayList<NnChannel>();
        }
        List<NnChannel> channels = findChannelsBySysTagIdOrderBySeq(sysTagId);
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
    
    /** call when NnChannel is going to delete **/
    public void deleteByChannelId(Long channelId) {
        // delete sysTagMaps
    }
    
}
