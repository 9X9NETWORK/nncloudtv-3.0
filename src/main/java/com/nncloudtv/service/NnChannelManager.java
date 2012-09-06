package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.PiwikLib;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.web.api.NnStatusCode;

@Service
public class NnChannelManager {

    protected static final Logger log = Logger.getLogger(NnChannelManager.class.getName());
    
    private NnChannelDao dao = new NnChannelDao();
    
    public NnChannel create(String sourceUrl, String name, HttpServletRequest req) {
        if (sourceUrl == null) 
            return null;
        String url = this.verifyUrl(sourceUrl);
        log.info("valid url=" + url);
        if (url == null) 
            return null;
        
        NnChannel channel = this.findBySourceUrl(url);        
        if (channel != null) {
            log.info("submit a duplicate channel:" + channel.getId());
            return channel; 
        }
        channel = new NnChannel(url);
        channel.setContentType(this.getContentTypeByUrl(url));
        log.info("new channel contentType:" + channel.getContentType());
        if (channel.getContentType() == NnChannel.CONTENTTYPE_FACEBOOK) {
            FacebookLib lib = new FacebookLib();
            String[] info = lib.getFanpageInfo(url);
            channel.setName(info[0]);
            channel.setImageUrl(info[1]);
            channel.setStatus(NnChannel.STATUS_SUCCESS);            
        } else {
            channel.setImageUrl(NnChannel.IMAGE_PROCESSING_URL);
            channel.setName("Processing");
            channel.setStatus(NnChannel.STATUS_PROCESSING);
            if (channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL ||
                channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_PLAYLIST) {
                Map<String, String> info = null;
                String youtubeName = YouTubeLib.getYouTubeChannelName(url);
                if (channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL) {
                    info = YouTubeLib.getYouTubeEntry(youtubeName, true);
                } else {
                    info = YouTubeLib.getYouTubeEntry(youtubeName, false);
                }
                if (!info.get("status").equals(String.valueOf(NnStatusCode.SUCCESS)))
                    return null;
                if (name != null)
                    channel.setName(name);
                String oriName = info.get("title");
                if (info.get("title") != null) {
                    channel.setOriName(oriName);
                    if (name == null)
                        channel.setName(oriName);
                }
                if (info.get("description") != null)
                    channel.setIntro(info.get("description"));
                if (info.get("thumbnail") != null)
                    channel.setImageUrl(info.get("thumbnail"));
            }            
        }
        channel.setPublic(false);
        channel = this.save(channel);
        if (channel.getContentType() == NnChannel.CONTENTTYPE_MAPLE_SOAP ||
            channel.getContentType() == NnChannel.CONTENTTYPE_MAPLE_VARIETY ||
            channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_SPECIAL_SORTING) {
            new DepotService().submitToTranscodingService(channel.getId(), channel.getSourceUrl(), req);                                
        }
        
        // piwik
        if (channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL || 
            channel.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_PLAYLIST) {            
            PiwikLib.createPiwikSite(channel.getId());
        }
        
        return channel;
    }

    public void saveFavorite(NnUser user, long pId, String fileUrl, String name, String imageUrl, boolean del) {
        NnChannel c = dao.findByUserIdStr(user.getIdStr());
        if (c == null) {
            c = new NnChannel(user.getName() + "'s Favorite", "", "");
            c.setUserIdStr(user.getIdStr());
            c.setContentType(NnChannel.CONTENTTYPE_FAVORITE);
            dao.save(c);
        }
        NnProgramManager pMngr = new NnProgramManager();
        
        if (fileUrl != null) {
            NnProgram p = pMngr.findByChannelAndFileUrl(c.getId(), fileUrl);
            if (p == null) {
                System.out.println("store a new favorite program:" + fileUrl);
                p = new NnProgram(c.getId(), name, "", imageUrl);
                p.setFileUrl(fileUrl);
                p.setPublic(true);
                p.setStatus(NnProgram.STATUS_OK);                
                pMngr.save(p);                
            }
        } else {
            NnProgram p = pMngr.findById(pId);
            System.out.println("referenceid:" + p.getReferenceStorageId());
            if (p != null) {
                NnProgram existed = pMngr.findByChannelAndStorageId(c.getId(), p.getReferenceStorageId());
                if (existed != null)
                    return;
                NnProgram newP = new NnProgram(c.getId(), p.getName(), p.getIntro(), p.getImageUrl());
                newP.setPublic(true);
                newP.setStatus(NnProgram.STATUS_OK);
                newP.setContentType(NnProgram.CONTENTTYPE_REFERENCE);
                String seq = p.getSeq();
                if (seq == null)
                    seq = "";
                newP.setStorageId(p.getReferenceStorageId()); //channelId + seq
                pMngr.save(newP);
            }            
        }
    }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
    
    public NnChannel save(NnChannel channel) {
        NnChannel original = dao.findById(channel.getId());
        Date now = new Date();
        if (channel.getCreateDate() == null)
            channel.setCreateDate(now);
        channel.setUpdateDate(now);        
        if (channel.getIntro() != null) {
            channel.setIntro(channel.getIntro().replaceAll("\n", ""));
            channel.setIntro(channel.getIntro().replaceAll("\t", " "));
            if (channel.getIntro().length() > 500)
                channel.getIntro().substring(0, 499);
        }
        if (channel.getName() != null) {
            channel.setName(channel.getName().replaceAll("\n", ""));
            channel.setName(channel.getName().replaceAll("\t", " "));
        }
        channel = dao.save(channel);
        NnChannel[] channels = {original, channel};
        if (MsoConfigManager.isQueueEnabled(true)) {
            //new QueueMessage().fanout("localhost",QueueMessage.CHANNEL_CUD_RELATED, channels);
        } else {
            this.processChannelRelatedCounter(channels);
        }
        return channel;
    }        
    
    public void processChannelRelatedCounter(NnChannel[] channels) {
    }
        
    public static List<NnChannel> search(String queryStr, boolean all) {
        return NnChannelDao.search(queryStr, all);        
    }
    
    /**
     * No deletion so we can keep track of blacklist urls 
     */
    public void delete(NnChannel channel) {
    }        
    
    //the url has to be verified(verifyUrl) first
    public short getContentTypeByUrl(String url) {
        short type = NnChannel.CONTENTTYPE_PODCAST;
        if (url.contains("http://www.youtube.com"))
            type = NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL;
        if (url.contains("http://www.youtube.com/view_play_list?p="))
            type = NnChannel.CONTENTTYPE_YOUTUBE_PLAYLIST;
        if (url.contains("facebook.com")) 
            type = NnChannel.CONTENTTYPE_FACEBOOK;
        if (url.contains("http://www.maplestage.net/show"))
            type = NnChannel.CONTENTTYPE_MAPLE_VARIETY;
        if (url.contains("http://www.maplestage.net/drama"))
            type = NnChannel.CONTENTTYPE_MAPLE_SOAP;
        return type;
    }        
            
    public boolean isCounterQualified(NnChannel channel) {
        boolean qualified = false;
        if (channel.getStatus() == NnChannel.STATUS_SUCCESS &&
            channel.getProgramCnt() > 0 &&
            channel.isPublic()) {
            qualified = true;
        }
        return qualified;
    }

    public NnChannel findBySourceUrl(String url) {
        if (url == null) {return null;}
        return dao.findBySourceUrl(url);
    }
    
    public NnChannel findById(long id) {
        NnChannel channel = dao.findById(id);
        return channel;
    }

    public List<NnChannel> findMsoDefaultChannels(long msoId, boolean needSubscriptionCnt) {        
        //find msoIpg
        MsoIpgManager msoIpgMngr = new MsoIpgManager();
        List<MsoIpg>msoIpg = msoIpgMngr.findAllByMsoId(msoId);
        //retrieve channels
        List<NnChannel> channels = new ArrayList<NnChannel>();
        for (MsoIpg i : msoIpg) {
            NnChannel channel = this.findById(i.getChannelId());
            if (channel != null) {
                channel.setType(i.getType());
                channel.setSeq(i.getSeq());
                channels.add(channel);
            }
        }
        return channels;
    }    
    
    //!!! different channel might have program count == 0
    public List<NnChannel> findGoodChannelsByCategoryId(long categoryId) {
        //channels within a category
        //CategoryChannelManager ccMngr = new CategoryChannelManager();
        List<NnChannel> channels = new ArrayList<NnChannel>();
        /*
        List<CategoryChannel> ccs = (List<CategoryChannel>) ccMngr.findAllByCategoryId(categoryId);

        //retrieve channels
        List<NnChannel> channels = new ArrayList<NnChannel>();
        for (CategoryChannel cc : ccs) {
            NnChannel channel = this.findById(cc.getChannelId());
            if (channel != null && 
                channel.getStatus() == NnChannel.STATUS_SUCCESS &&  
                channel.isPublic()) { 
                channels.add(channel);
            }
        }                
        */
        return channels; 
    }

    public List<NnChannel> findByType(short type) {
        return dao.findByType(type);        
    }
    
    public List<NnChannel> findMaples() {
        List<NnChannel> variety = this.findByType(NnChannel.CONTENTTYPE_MAPLE_VARIETY);
        List<NnChannel> soap = this.findByType(NnChannel.CONTENTTYPE_MAPLE_SOAP);
        List<NnChannel> channels = new ArrayList<NnChannel>();
        channels.addAll(variety);
        channels.addAll(soap);
        return channels;
    }

    /**
     * (1)featured: randomized; mutually exclusive from "recommended" or "hottest"; 
     *    comes from the 50-100 channels first picked by shawn (ie: "special" channels)
     * (2)recommended: initial user - randomize 
     *    (same as featured, must be mutually exclusive from "featured" or "hottest"); returning user - based on history
     * (3)hottest: most viewed within the last 24 hours 
     *    (within the 50-100 channels; ie: "special" channels); 
     *    also mutually exclusive from "featured" or "recommended"
     */
    public List<NnChannel> findHot() {
        List<NnChannel> hot = dao.findSpecial(NnChannel.POOL_HOTTEST);
        return hot;
    }
    
    //note, recommended goes before featured for "exclusive" result
    //since recommended might achieve from some engine    
    public List<NnChannel> findRecommended() {
        List<NnChannel> channels = dao.findSpecial(NnChannel.POOL_FEATUERD);
        List<NnChannel> recommended = new ArrayList<NnChannel>();
        int i=0;
        for (NnChannel c : channels) {
            recommended.add(c);
            i++;
            if (i > 9)
                break;
        }
        return recommended;
    }
    
    public List<NnChannel> findTrending() {
        List<NnChannel> channels = dao.findSpecial(NnChannel.POOL_TRENDING);    
        return channels;
    }
    
    //featured depends on recommended
    public List<NnChannel> findFeatured(List<NnChannel> exclusive) {
        List<NnChannel> channels = dao.findSpecial(NnChannel.POOL_FEATUERD);
        List<NnChannel> featured = new ArrayList<NnChannel>();
        HashMap<Long, NnChannel> map = new HashMap<Long, NnChannel>();
        for (NnChannel c : exclusive) {
            map.put(c.getId(), c);
        }
        int i=0;
        for (NnChannel c : channels) {
            if (!map.containsKey(c.getId())) {
                featured.add(c);
            }
            i++;
            if (i > 9)
                break;
        }
        return featured;
    }
    
    public int addCntView(boolean readOnly, long channelId) {        
        String counterName = "cid" + channelId;
        CounterFactory factory = new CounterFactory();
        ShardedCounter counter = factory.getOrCreateCounter(counterName);
        if (!readOnly) {
            counter.increment();
        }
        return counter.getCount();
    }
    
    public List<NnChannel> findByChannelIds(List<Long> channelIds) {
        List<NnChannel> channels = new ArrayList<NnChannel>();
        for (Long id : channelIds) {
            NnChannel channel = this.findById(id);
            if (channel != null) channels.add(channel);
        }
        return channels;        
    }
    
    public List<NnChannel> findByStatus(short status) {
        List<NnChannel> channels = dao.findAllByStatus(status);        
        return channels;
    }
    
    public List<NnChannel> findAll() {
        return dao.findAll();
    }
    
    public List<NnChannel> list(int page, int limit, String sidx, String sord) {
        return dao.list(page, limit, sidx, sord);
    }
    
    public List<NnChannel> list(int page, int limit, String sidx, String sord, String filter) {
        return dao.list(page, limit, sidx, sord, filter);
    }
    
    public int total() {
        return dao.total();
    }
    
    public int total(String filter) {
        return dao.total(filter);
    }

    public String verifyUrl(String url) {
        if (url == null) return null;
        if (!url.contains("http://") && !url.contains("https://"))
            return null;        
        if (url.contains("youtube.com")) {
            return YouTubeLib.formatCheck(url);
        } else if (url.contains("facebook.com")) {
            return url;
        } else if (url.contains("www.maplestage.net")) {
        //} else if (url.contains("www.maplestage.net") && !url.contains("9x9.tv")) {
            return url;
        }
        return null;
    }

    //find channels created by the user, aka curator
    //player true returns only good and public channels
    public List<NnChannel> findByUser(NnUser user, int number) {
        String userIdStr = user.getShard() + "-" + user.getId();
        List<NnChannel> channels = dao.findByUser(userIdStr); //!!! pass the number for limited search
        if (number == 0) {
            return channels;
        } else {             
            if (channels.size() > number)
            return channels.subList(0, number);
        }
        return channels;
    }
    
    public static short getDefaultSorting(NnChannel c) {
        short sorting = NnChannel.SORT_NEWEST_TO_OLDEST; 
        if (c.getContentType() == NnChannel.CONTENTTYPE_MAPLE_SOAP || 
            c.getContentType() == NnChannel.CONTENTTYPE_MAPLE_VARIETY || 
            c.getContentType() == NnChannel.CONTENTTYPE_MIXED)
            sorting = NnChannel.SORT_DESIGNATED;
        return sorting;
    }

}
