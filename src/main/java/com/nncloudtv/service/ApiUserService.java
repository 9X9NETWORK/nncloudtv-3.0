package com.nncloudtv.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnUser;

@Service
public class ApiUserService {
    
    protected static final Logger log = Logger.getLogger(ApiUserService.class.getName());
    
    private NnChannelManager channelMngr;
    private StoreService storeService;
    private NnChannelPrefManager channelPrefMngr;
    
    @Autowired
    public ApiUserService(NnChannelManager channelMngr, StoreService storeService, NnChannelPrefManager channelPrefMngr) {
        this.channelMngr = channelMngr;
        this.storeService = storeService;
        this.channelPrefMngr = channelPrefMngr;
    }
    
    public NnChannel userChannelCreate(NnUser user, String name, String intro, String imageUrl, String lang, Boolean isPublic,
                String sphere, String tag, Long categoryId, Boolean autoSync, String sourceUrl) {
        
        if (user == null || name == null) {
            return null;
        }
        
        NnChannel newChannel = new NnChannel(name, null, NnChannel.IMAGE_WATERMARK_URL); // default : watermark
        newChannel.setContentType(NnChannel.CONTENTTYPE_MIXED);
        newChannel.setPublic(true); // default : true
        newChannel.setStatus(NnChannel.STATUS_WAIT_FOR_APPROVAL);
        newChannel.setPoolType(NnChannel.POOL_BASE);
        newChannel.setUserIdStr(user.getShard(), user.getId());
        newChannel.setLang(LangTable.LANG_EN); // default : en
        newChannel.setSphere(LangTable.LANG_EN); // default : en
        newChannel.setSeq((short) 0);
        
        if (intro != null) {
            newChannel.setIntro(intro);
        }
        if (imageUrl != null) {
            newChannel.setImageUrl(imageUrl);
        }
        if (lang != null) {
            newChannel.setLang(lang);
        }
        if (isPublic != null) {
            newChannel.setPublic(isPublic);
        }
        if (sphere != null) {
            newChannel.setSphere(sphere);
        }
        if (tag != null) {
            newChannel.setTag(tag);
        }
        if (sourceUrl != null) {
            newChannel.setSourceUrl(sourceUrl);
        }
        
        NnChannel savedChannel = channelMngr.save(newChannel);
        
        channelMngr.reorderUserChannels(user);
        
        if (categoryId != null) {
            storeService.setupChannelCategory(categoryId, savedChannel.getId());
        }
        
        if (autoSync != null) {
            channelPrefMngr.setAutoSync(savedChannel.getId(), autoSync);
        }
        
        return savedChannel;
    }
    
}
