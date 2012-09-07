package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.NnStringUtil;
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

    //find hot, featured, trending stories
    public List<NnChannel> findBillboard(String name, String lang) { 
        List<NnChannel> channels = new ArrayList<NnChannel>();
        if (name == null)
            return channels;
        TagManager tagMngr = new TagManager();        
        name += "(9x9" + lang + ")";
        log.info("find billboard, tag:" + name);
        channels = tagMngr.findChannelsByTag(name, true);        
        return channels;
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

    public String composeChannelLineup(List<NnChannel> channels) {
        String output = "";
        for (NnChannel c : channels) {
            output += this.composeChannelLineupStr(c) + "\n";
        }
        return output;
    }
    public String composeChannelLineupStr(NnChannel c) {
        Random r = new Random();
        int viewCount = r.nextInt(300);
        String[] url1 = {
            "http://www.choicematters.org/wp-content/uploads/2012/01/tumblr_ku48goxoYS1qatg16o1_4001.jpg",
            "http://www.motherearthnews.com/uploadedImages/articles/issues/2010-04-01/chickens1.jpg",
            "http://www.organicgardeningfarming.com/wp-content/uploads/2011/12/backyard-chickens.jpg",
            "http://i-cdn.apartmenttherapy.com/uimages/kitchen/2009_03_25-BlackChicken02.jpg",
            "http://graphics8.nytimes.com/images/blogs/greeninc/chickens.jpeg",
            "http://www.slate.com/content/dam/slate/archive/2011/01/1_123125_122975_2243255_2276474_110125_food_chicken_tn.jpg",
            "http://www.myessentia.com/blog/wp-content/uploads/2011/03/chicken.jpg",
            "http://cdn.blogs.sheknows.com/gardening.sheknows.com/2011/09/free-range-chickens.jpg",
            "http://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Buttercup_Chicken1.jpg/250px-Buttercup_Chicken1.jpg",
            "http://leesbirdblog.files.wordpress.com/2009/01/you-can-have-her-chickens-by-maji.jpg",
        };        
        String[] url2 = {
            "http://i2.ytimg.com/i/194cPvPaGJjhJBEGwG6vxg/1.jpg",
            "http://i2.ytimg.com/i/Yjk_zY-iYR8YNfJmuzd70A/1.jpg?v=a5d97c",
            "http://9x9cache.s3.amazonaws.com/system.jpg",                 
            "http://i1.ytimg.com/vi/8yuFvqsL-C0/default.jpg",              
            "http://i1.ytimg.com/i/XNZiORujW__GkIskLb0FNw/1.jpg?v=8401b7", 
            "http://i2.ytimg.com/i/MKvT0YVLufHMdGLH89J1oA/1.jpg",          
            "http://i2.ytimg.com/i/5l1Yto5oOIgRXlI4p4VKbw/1.jpg?v=8157b2", 
            "http://i2.ytimg.com/vi/y367HBFS4hU/default.jpg",              
            "http://i1.ytimg.com/i/lrEYreVkBee7ZQYei_6Jqg/1.jpg?v=9605e9", 
            "http://i3.ytimg.com/i/2LqANmM2rTLGO345OuapuA/1.jpg?v=a9a54a",
        };
        int img1 = r.nextInt(10);
        int img2 = r.nextInt(10);
        String imageUrl = c.getPlayerPrefImageUrl() + "|" + url1[img1] + "|" + url2[img2];

        NnUser u = new NnUserManager().findByIdStr(c.getUserIdStr());        
        String userName = "";
        String userIntro = "";
        String userImageUrl = "";
        if (u != null) {
            userName = u.getName();
            userIntro = u.getIntro();
            userImageUrl = u.getImageUrl();
        }            
        
        String[] ori = {Integer.toString(c.getSeq()), 
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getIntro(),
                        imageUrl, //c.getPlayerPrefImageUrl(),                        
                        String.valueOf(c.getProgramCnt()),
                        String.valueOf(c.getType()),
                        String.valueOf(c.getStatus()),
                        String.valueOf(c.getContentType()),
                        c.getPlayerPrefSource(),
                        this.convertEpochToTime(c.getTranscodingUpdateDate(), c.getUpdateDate()),
                        String.valueOf(c.getSorting()),
                        c.getPiwik(),
                        String.valueOf(c.getRecentlyWatchedProgram()),
                        c.getOriName(),
                        String.valueOf(c.getCntSubscribe()),
                        String.valueOf(viewCount), //view count
                        c.getTag(),
                        c.getUserIdStr(), //curator id
                        userName,
                        userIntro,
                        userImageUrl,
                       };

        String output = NnStringUtil.getDelimitedStr(ori);
        return output;
    }

    private String convertEpochToTime(String transcodingUpdateDate, Date updateDate) {
        String output = "";
        try {
            if (transcodingUpdateDate != null) {
                long epoch = Long.parseLong(transcodingUpdateDate);
                Date myDate = new Date (epoch*1000);
                output = String.valueOf(myDate.getTime());
            } else if (updateDate != null){
                output = String.valueOf(updateDate.getTime());
            }
        } catch (NumberFormatException e) {
            log.info("convertEpochToTime fails:" + transcodingUpdateDate + ";" + updateDate);
        }
        return output;
    }
    
}
