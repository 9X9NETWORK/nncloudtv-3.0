package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.CategoryMapDao;
import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.PiwikLib;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryMap;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.Tag;
import com.nncloudtv.model.TagMap;
import com.nncloudtv.web.api.NnStatusCode;

@Service
public class NnChannelManager {

    protected static final Logger log = Logger.getLogger(NnChannelManager.class.getName());
    
    private NnChannelDao dao = new NnChannelDao();
    private CategoryMapDao catMapDao = new CategoryMapDao();
    
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

    //TODO remove unwanted text from tag
    public void processTag(NnChannel c) {
        String tag = c.getTag();
        String[] multiples = tag.split(",");
        TagManager tagMngr = new TagManager();
        for (String m : multiples) {
            m = m.trim();            
            Tag t = tagMngr.findByName(m);
            if (t == null) {
                t = new Tag(m);
                tagMngr.save(t);
            }
            TagMap tm = tagMngr.findByTagAndChannel(t.getId(), c.getId());
            if (tm == null)
                tagMngr.createTagMap(t.getId(), c.getId());
        }
    }
    
    public void deleteFavorite(NnUser user, long pId) {
        NnChannel favoriteCh = dao.findFavorite(user.getIdStr());
        if (favoriteCh == null)
            return;
        NnProgramManager pMngr = new NnProgramManager();
        NnProgram p = pMngr.findById(pId);
        if (p != null) {
            if (p.getChannelId() == favoriteCh.getId())
                pMngr.delete(p);
        }
    }
    
    public void saveFavorite(NnUser user, long pId, String fileUrl, String name, String imageUrl) {
        NnChannel favoriteCh = dao.findFavorite(user.getIdStr());
        if (favoriteCh == null) {
            favoriteCh = new NnChannel(user.getName() + "'s Favorite", "", ""); //TODO, maybe assemble the name to avoid name change
            favoriteCh.setUserIdStr(user.getIdStr());
            favoriteCh.setContentType(NnChannel.CONTENTTYPE_FAVORITE);
            dao.save(favoriteCh);
        }
        NnProgramManager pMngr = new NnProgramManager();
        NnProgram toBeFavorite = null;
        if (pId != 0) {
            toBeFavorite = pMngr.findById(pId);
            if (toBeFavorite == null) {
                log.info("program invalid:" + pId);
                return;
            }
            NnChannel c = new NnChannelManager().findById(toBeFavorite.getChannelId());
            if (c == null) {
                log.info("program doesn't have channel info:" + toBeFavorite.getChannelId());
                return;
            }
            if (c.getContentType() != NnChannel.CONTENTTYPE_MIXED) {
                if (toBeFavorite.getContentType() != NnProgram.CONTENTTYPE_REFERENCE) {
                    fileUrl = toBeFavorite.getFileUrl();
                    name = toBeFavorite.getName();
                    imageUrl = toBeFavorite.getImageUrl();
                }
            }
        }
        if (fileUrl != null) {
            NnProgram existFavorite = pMngr.findByChannelAndFileUrl(favoriteCh.getId(), fileUrl);
            if (existFavorite == null) {
                existFavorite = new NnProgram(favoriteCh.getId(), name, "", imageUrl);
                existFavorite.setFileUrl(fileUrl);
                existFavorite.setPublic(true);
                existFavorite.setStatus(NnProgram.STATUS_OK);                
                pMngr.save(existFavorite);                
            }
            return;
        }
        //only 9x9 channel or reference of 9x9 channel should hit here,  
        String storageId = "";
        if (toBeFavorite.getContentType() == NnProgram.CONTENTTYPE_REFERENCE) {
            storageId = toBeFavorite.getStorageId();
        } else {                     
            storageId = String.valueOf(toBeFavorite.getEpisodeId());
        }
        NnProgram existFavorite = pMngr.findByChannelAndStorageId(favoriteCh.getId(), storageId);        
        if (existFavorite != null)
            return;
        
        NnProgram newP = new NnProgram(favoriteCh.getId(), toBeFavorite.getName(), toBeFavorite.getIntro(), toBeFavorite.getImageUrl());
        newP.setPublic(true);
        newP.setStatus(NnProgram.STATUS_OK);
        newP.setContentType(NnProgram.CONTENTTYPE_REFERENCE);
        newP.setStorageId(storageId);
        newP.setUpdateDate(new Date());
        pMngr.save(newP);            
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
        this.processTag(channel);
        return channel;
    }
    
    public void saveChannelToCategory (long channelId, long categoryId, long oldCategoryId) {
        
        List<CategoryMap> categoryMaps = catMapDao.findByChannelId(channelId);
        if (oldCategoryId == 0) { // newly created
            int i;
            for (i=0; i<categoryMaps.size(); i++) {
                if (categoryMaps.get(i).getCategoryId() == categoryId) {
                    // the database already has one, do nothing.
                    break;
                }
            }
            if (i == categoryMaps.size()) {
                CategoryMap catMap = new CategoryMap(categoryId, channelId);
                catMapDao.save(catMap); // not found in database, newly created.
            }
        } else { // modify origin one
            int i;
            for (i=0; i<categoryMaps.size(); i++) {
                if (categoryMaps.get(i).getCategoryId() == oldCategoryId) {
                    categoryMaps.get(i).setCategoryId(categoryId);
                    categoryMaps.get(i).setUpdateDate(new Date());
                    catMapDao.save(categoryMaps.get(i));
                    break;
                }
            }
            if (i == categoryMaps.size()) {
                // not found in database, do nothing.
            }
        }
        
    }
    
    public void saveChannelToCategoryWithSphereJudgement (long channelId, String sphere, long categoryId) {
        
        CategoryManager catMngr = new CategoryManager();
        Category category = catMngr.findById(categoryId);
        if (category == null) {
            return ;
        }
        
        /*
         * always set categoryId that the user pick up, despite if sphere not match category's lang.
         */
        if (sphere.equals(LangTable.OTHER)) { // pick up opposite lang's category
            Category category_opposite = null;                
            if (category.getLang().equals(LangTable.LANG_EN)) {
                category_opposite = catMngr.findByLangAndSeq(LangTable.LANG_ZH, category.getSeq());
                if (category_opposite != null) {
                    saveChannelToCategory(channelId, category_opposite.getId(), 0);
                }
            }
            if (category.getLang().equals(LangTable.LANG_ZH)) {
                category_opposite = catMngr.findByLangAndSeq(LangTable.LANG_EN, category.getSeq());
                if (category_opposite != null) {
                    saveChannelToCategory(channelId, category_opposite.getId(), 0);
                }
            }
        }
        
        saveChannelToCategory(channelId, categoryId, 0);
    }
    
    public void saveChannelToCategoryWithSphereJudgement (long channelId, String sphere, String oldSphere, long CategoryId, long oldCategoryId) {
        
        if (CategoryId == oldCategoryId) {
            return ;
        }
        
        if (oldSphere.equals(sphere)) {
            if (sphere.equals(LangTable.OTHER)) { // other -> other
                //TODO the opposite category need change with sync ?
                saveChannelToCategory(channelId, CategoryId, oldCategoryId);
            } else { // zh -> zh, en -> en
                saveChannelToCategory(channelId, CategoryId, oldCategoryId);
            }
        } else { // other -> zh, other -> en, en -> zh, en -> other, zh -> en, zh -> other
            //TODO need more judgement
            saveChannelToCategory(channelId, CategoryId, oldCategoryId);
        }
        
    }
    
    public void processChannelRelatedCounter(NnChannel[] channels) {
    }
        
    public List<NnChannel> searchBySvi(String queryStr, short userShard, long userId, String sphere) {
        List<NnChannel> channels = new ArrayList<NnChannel>();
        String url = "http://svi.9x9.tv/api/search.php?";
        url += "shard=" + userShard;
        url += "&userid=" + userId;
        url += "&lang=" + sphere;        
        url += "&s=" + queryStr;
        log.info("svi query url:" + url);
        String chStr = NnNetUtil.urlGet(url);
        log.info("return from svi:" + chStr);
        if (chStr != null) {
            String chs[] = chStr.split(",");
            for (String cId : chs) {
                NnChannel c = this.findById(Long.parseLong(cId));
                if (c != null)
                    channels.add(c);
            }
        }
        return channels;
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
        if (channel == null) {
            return null;
        }
        
        Category category = findCategoryByChannelId(id);
        if (category != null) {
            channel.setCategoryId(category.getId());
        }
        
        return channel;
    }
    
    public Category findCategoryByChannelId(long channelId) {
        
        NnChannel channel = dao.findById(channelId);
        if (channel == null) {
            return null;
        }
        
        List<Category> categories = findCategoriesByChannelId(channelId);
        
        /*
         * if duplicate in en or zh categories, the newly updated category will be return.
         */
        if (channel.getSphere() != null) {
            if (channel.getSphere().equals(LangTable.OTHER)) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getLang().equals(LangTable.LANG_EN)) {
                        return categories.get(i);
                    }
                }
            } else {
                for (int i = 0; i < categories.size(); i++) {
                    if (channel.getSphere().equals(categories.get(i).getLang())) {
                        return categories.get(i);
                    }
                }
            }
            
            if (categories != null) {
                return categories.get(0); // if sphere set but match fail, the newly updated category will return.
            } else {
                return null;
            }
            
        } else {
            if (categories != null) {
                return categories.get(0); // if sphere not set, the newly updated category will return.
            } else {
                return null;
            }
        }
        
    }
    
    public List<Category> findCategoriesByChannelId(Long channelId) {
        CategoryManager catMngr = new CategoryManager();
        List<CategoryMap> categoryMaps = catMapDao.findByChannelId(channelId);
        List<Category> categories = new ArrayList<Category>();
        for (int i=0; i<categoryMaps.size(); i++) {
            categories.add(catMngr.findById(categoryMaps.get(i).getCategoryId()));
        }
        return categories;
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
     * Find hot, featured, trending stories.
     * Featured and recommended can not be overlapped
     * 
     * Hot: 9 channels automatically selected from the BILLBOARD POOL according to the number-of-views (on 9x9.tv)
     * Featured: 9 channels randomly selected from the BILLBOARD POOL; can overlap with HOTTEST, but not RECOMMENDED
     */
    public List<NnChannel> findBillboard(String name, String lang) { 
        List<NnChannel> channels = new ArrayList<NnChannel>();
        RecommendService service = new RecommendService();
        if (name == null)
            return channels;
        if (name.equals(Tag.TRENDING)) {            
            TagManager tagMngr = new TagManager();        
            name += "(9x9" + lang + ")";
            log.info("find billboard, tag:" + name);
            channels = tagMngr.findChannelsByTag(name, true);
        } else if (name.equals(Tag.FEATURED)) {
            channels = service.findBillboardPool(9, lang);
        } else if (name.equals(Tag.HOT)) {
            channels = service.findBillboardPool(9, lang);
        }
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
    public List<NnChannel> findByUser(NnUser user, int limit, boolean isPlayer) {
        String userIdStr = user.getShard() + "-" + user.getId();
        List<NnChannel> channels = dao.findByUser(userIdStr, limit, isPlayer);
        if (limit == 0) {
            return channels;
        } else {             
            if (channels.size() > limit)
            return channels.subList(0, limit);
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
        String imageUrl = c.getPlayerPrefImageUrl();

        NnUserManager userMngr = new NnUserManager();
        NnUser u = userMngr.findByIdStr(c.getUserIdStr());        
        String userName = "";
        String userIntro = "";
        String userImageUrl = "";
        String curatorProfile = "";
        if (u != null) {
            userName = u.getName();
            userIntro = u.getIntro();
            userImageUrl = u.getImageUrl();
            curatorProfile = u.getProfileUrl();
        }            
       
        String subscriberProfile = "";
        String subscriberImage = "";
        String subscribersIdStr = c.getSubscribersIdStr();
        if (subscribersIdStr != null) {
            String[] list = subscribersIdStr.split(";");
            for (String l : list ) {
                NnUser sub = userMngr.findByIdStr(l);
                if (sub != null) {
                    subscriberProfile += "|" + sub.getProfileUrl();
                    subscriberImage += "|" + sub.getImageUrl();                    
                }
            }
            if (subscriberProfile.length() > 0) {
                subscriberProfile = subscriberProfile.replaceFirst("\\|", "");
            }
            if (subscriberImage.length() > 0) {
                subscriberImage = subscriberImage.replaceFirst("\\|", "");
            }
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
                        curatorProfile, //curator id
                        userName,
                        userIntro,
                        userImageUrl,
                        subscriberProfile,
                        subscriberImage,
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
