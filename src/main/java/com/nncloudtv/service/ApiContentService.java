package com.nncloudtv.service;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnEpisode;

@Service
public class ApiContentService {
    
    protected static final Logger log = Logger.getLogger(ApiContentService.class.getName());
    
    private NnChannelManager channelMngr;
    private MsoManager msoMngr;
    private StoreService storeService;
    private NnChannelPrefManager channelPrefMngr;
    private NnEpisodeManager episodeMngr;
    
    @Autowired
    public ApiContentService(NnChannelManager channelMngr, MsoManager msoMngr, StoreService storeService,
                NnChannelPrefManager channelPrefMngr, NnEpisodeManager episodeMngr) {
        this.channelMngr = channelMngr;
        this.msoMngr = msoMngr;
        this.storeService = storeService;
        this.channelPrefMngr = channelPrefMngr;
        this.episodeMngr = episodeMngr;
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
    
    public NnChannel channelUpdate(Long channelId, String name, String intro, String lang, String sphere, Boolean isPublic,
            String tag, String imageUrl, Long categoryId, Date updateDate, Boolean autoSync, Short sorting) {
        
        if (channelId == null) {
            return null;
        }
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            return null;
        }
        
        if (name != null) {
            channel.setName(name);
        }
        if (intro != null) {
            channel.setIntro(intro);
        }
        if (lang != null) {
            channel.setLang(lang);
        }
        if (sphere != null) {
            channel.setSphere(sphere);
        }
        if (isPublic != null) {
            channel.setPublic(isPublic);
        }
        if (tag != null) {
            channel.setTag(tag);
        }
        if (imageUrl != null) {
            channel.setImageUrl(imageUrl);
        }
        if (updateDate != null) {
            channel.setUpdateDate(updateDate);
        }
        if (sorting != null) {
            channel.setSorting(sorting);
        }
        
        NnChannel savedChannel = channelMngr.save(channel);
        
        if (categoryId != null) {
            storeService.setupChannelCategory(categoryId, channel.getId());
        }
        
        if (autoSync != null) {
            channelPrefMngr.setAutoSync(savedChannel.getId(), autoSync);
        }
        
        return savedChannel;
    }
    
    public Map<String, String> channelYoutubeDataSync(Long channelId) {
        
        if (channelId == null) {
            return null;
        }
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            return null;
        }
        
        channel.setReadonly(true);
        channel = channelMngr.save(channel);
        
        Map<String, String> obj = new HashMap<String, String>();
        obj.put("id", channel.getIdStr());
        obj.put("sourceUrl", channel.getSourceUrl());
        obj.put("contentType", String.valueOf(channel.getContentType()));
        obj.put("isRealtime", "true");
        
        Map<String, String> response = NnNetUtil.urlPostWithJson("http://" + MsoConfigManager.getCrawlerDomain() + "/ytcrawler/crawlerAPI.php", obj);
        
        // roll back
        if (String.valueOf(HttpURLConnection.HTTP_OK).equals(response.get(NnNetUtil.STATUS)) == false ||
                "Ack\n".equals(response.get(NnNetUtil.TEXT)) == false) {
            
            channel.setReadonly(false);
            channelMngr.save(channel);
            
            log.info("response status = " + response.get(NnNetUtil.STATUS));
            log.info("response content = " + response.get(NnNetUtil.TEXT));
        }
        
        return response;
    }
    
    public List<NnEpisode> channelEpisodes(Long channelId, Long page, Long rows) {
        
        if (channelId == null) {
            return null;
        }
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            return null;
        }
        
        if (page == null) {
            page = (long) 0;
        }
        if (rows == null) {
            rows = (long) 0;
        }
        
        List<NnEpisode> results = null;
        if (page > 0 && rows > 0) {
            
            if (channel.getSorting() == NnChannel.SORT_POSITION_REVERSE) {
                results = episodeMngr.list(page, rows, "seq", "desc", "channelId == " + channelId);
            } else {
                results = episodeMngr.list(page, rows, "seq", "asc", "channelId == " + channelId);
            }
        } else {
            results = episodeMngr.findByChannelId(channelId);
            if (channel.getSorting() == NnChannel.SORT_POSITION_REVERSE) {
                Collections.sort(results, episodeMngr.getEpisodeReverseSeqComparator());
            } else {
                Collections.sort(results, episodeMngr.getEpisodeSeqComparator());
            }
        }
        
        episodeMngr.normalize(results);
        for (NnEpisode episode : results) {
            episode.setPlaybackUrl(NnStringUtil.getSharingUrl(episode.getChannelId(), episode.getId(), null));
        }
        
        return results;
    }

}
