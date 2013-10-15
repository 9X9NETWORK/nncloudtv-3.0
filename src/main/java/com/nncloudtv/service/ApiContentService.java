package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.model.NnChannel;

@Service
public class ApiContentService {
    
    protected static final Logger log = Logger.getLogger(ApiContentService.class.getName());
    
    private NnChannelManager channelMngr;
    private MsoManager msoMngr;
    
    @Autowired
    public ApiContentService(NnChannelManager channelMngr, MsoManager msoMngr) {
        this.channelMngr = channelMngr;
        this.msoMngr = msoMngr;
    }
    
    public List<NnChannel> channelsSearch(Long msoId, String ytPlaylistId, String ytUserId) {
        
        List<NnChannel> results = new ArrayList<NnChannel>();
        
        if (ytPlaylistId != null) {
            
            String sourceUrl = "http://www.youtube.com/view_play_list?p=".concat(ytPlaylistId);
            NnChannel result = channelMngr.findBySourceUrl(sourceUrl);
            if (result != null) {
                results.add(result);
            }
        } else if (ytUserId != null) {
            
            String sourceUrl = "http://www.youtube.com/user/".concat(ytUserId);
            NnChannel result = channelMngr.findBySourceUrl(sourceUrl);
            if (result != null) {
                results.add(result);
            }
        } else {
            
            return new ArrayList<NnChannel>();
        }
        
        // filter part
        if (msoId != null) {
            
            List<Long> unverifiedChannel = new ArrayList<Long>();
            for (NnChannel channel : results) {
                unverifiedChannel.add(channel.getId());
            }
            
            List<Long> verifiedChannel = msoMngr.getPlayableChannels(unverifiedChannel, msoId);
            
            results = channelMngr.findByIds(verifiedChannel);
            Collections.sort(results, channelMngr.getChannelComparator("updateDate"));
        }
        
        return results;
    }

}
