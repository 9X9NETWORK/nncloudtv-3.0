package com.nncloudtv.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.mysql.jdbc.CommunicationsException;
import com.nncloudtv.dao.DashboardDao;
import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.dao.UserInviteDao;
import com.nncloudtv.dao.YtProgramDao;
import com.nncloudtv.lib.AuthLib;
import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.QueueFactory;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.Captcha;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.Dashboard;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnContent;
import com.nncloudtv.model.NnDevice;
import com.nncloudtv.model.NnEmail;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnGuest;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserChannelSorting;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.model.NnUserReport;
import com.nncloudtv.model.NnUserShare;
import com.nncloudtv.model.NnUserSubscribe;
import com.nncloudtv.model.NnUserSubscribeGroup;
import com.nncloudtv.model.NnUserWatched;
import com.nncloudtv.model.Tag;
import com.nncloudtv.model.UserInvite;
import com.nncloudtv.model.YtProgram;
import com.nncloudtv.validation.BasicValidator;
import com.nncloudtv.validation.NnUserValidator;
import com.nncloudtv.web.api.NnStatusCode;
import com.nncloudtv.web.json.facebook.FacebookMe;

@Service
public class PlayerApiService {
    
    protected static final Logger log = Logger.getLogger(PlayerApiService.class.getName());    
    
    private NnUserManager userMngr = new NnUserManager();    
    private MsoManager msoMngr = new MsoManager();
    private NnChannelManager chMngr = new NnChannelManager();
    private Locale locale;
    private Mso mso;
    private int version = 32;    

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public void setMso(Mso mso) {
        this.mso = mso;
    }
    
    public String handleException (Exception e) {
        if (e.getClass().equals(NumberFormatException.class)) {
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);            
        } else if (e.getClass().equals(CommunicationsException.class)) {
            log.info("return db error");
            return this.assembleMsgs(NnStatusCode.DATABASE_ERROR, null);
        } 

        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        NnLogUtil.logException((Exception) e);
        return output;
    }    

    public int addMsoInfoVisitCounter(boolean readOnly) {
        if (!readOnly) {
            if (MsoConfigManager.isQueueEnabled(true)) {
                //new QueueMessage().fanout("localhost",QueueMessage.VISITOR_COUNTER, null);
            } else {
                log.info("quque not enabled");
                return msoMngr.addMsoVisitCounter(readOnly);
            }
        }
        return msoMngr.addMsoVisitCounter(readOnly);                                     
    }
            
    //assemble key and value string
    public static String assembleKeyValue(String key, String value) {
        return key + "\t" + value + "\n";
    }
    
    /**
     * assemble final output to player 
     * 1. status line in the front
     * 2. raw: for each section needs to be separated by separator string, "--\n"
     */
    public String assembleMsgs(int status, String[] raw) {
        String result = NnStatusMsg.getPlayerMsg(status, locale);
        String separatorStr = "--\n";
        if (raw != null && raw.length > 0) {
            result = result + separatorStr;
            for (String s : raw) {
                s = s.replaceAll("null", "");
                result += s + separatorStr;
            }
        }
        if (result.substring(result.length()-3, result.length()).equals(separatorStr)) {
            result = result.substring(0, result.length()-3);
        }
        return result;
    }
    
    //Prepare user info, it is used by login, guest register, userTokenVerify
    public String prepareUserInfo(NnUser user, NnGuest guest, HttpServletRequest req) {
        String output = "";
        if (user != null) {
            output += assembleKeyValue("token", user.getToken());
            output += assembleKeyValue("name", user.getName());
            output += assembleKeyValue("lastLogin", String.valueOf(user.getUpdateDate().getTime()));
            output += assembleKeyValue("sphere", user.getSphere());
            output += assembleKeyValue("ui-lang", user.getLang());            
            output += assembleKeyValue("userid", String.valueOf(user.getIdStr()));
            output += assembleKeyValue("curator", String.valueOf(user.getProfileUrl()));
            String fbUser = "0";
            if (user.isFbUser())
                fbUser = "1";
            output += assembleKeyValue("fbUser", fbUser);
            NnUserPrefManager prefMngr = new NnUserPrefManager();
            List<NnUserPref> list = prefMngr.findByUser(user);        
            for (NnUserPref pref : list) {
                output += PlayerApiService.assembleKeyValue(pref.getItem(), pref.getValue());
            }            
        } else {        
            output += assembleKeyValue("token", guest.getToken());
            output += assembleKeyValue("name", NnUser.GUEST_NAME);
            output += assembleKeyValue("lastLogin", "");            
            String sphere = NnUserManager.findLocaleByHttpRequest(req);
            output += assembleKeyValue("sphere", sphere); //TODO should depend on ip
        }
            
        return output;
    }

    public void setUserCookie(HttpServletResponse resp, String cookieName, String userId) {        
        CookieHelper.setCookie(resp, cookieName, userId);
    }    
    
    public String listRecommended(String lang) {
        lang = this.checkLang(lang);    
        if (lang == null)
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        if (version >= 32)
            return NnStatusMsg.assembleMsg(NnStatusCode.API_DEPRECATED, null); 
        return new IosService().listRecommended(lang);        
    }
    
    public String fbDeviceSignup(FacebookMe me, String expire, HttpServletRequest req, HttpServletResponse resp) {
        return this.fbSignup(me, expire, req, resp);
    }
    
    private String fbSignup(FacebookMe me, String expires, HttpServletRequest req, HttpServletResponse resp) {
        long expire = 0;
        if (expires != null && expires.length() > 0)
            expire = Long.parseLong(expires); //TODO pass to FacebookMe
        
        NnUser user = userMngr.findByEmail(me.getId(), req);
        log.info("find user in db from fbId:" + me.getId());
        if (user == null) {
            log.info("FACEBOOK: signup with fb account:" + me.getEmail() + "(" + me.getId() + ")");
            user = new NnUser(me.getEmail(), me.getName(), me.getId(), me.getAccessToken());
            user.setExpires(new Date().getTime() + expire);
            user.setTemp(false);
            user = userMngr.setFbProfile(user, me);
            int status = userMngr.create(user, req, (short)0);
            if (status != NnStatusCode.SUCCESS)
                return this.assembleMsgs(status, null);            
            userMngr.subscibeDefaultChannels(user);
        } else {
            user = userMngr.setFbProfile(user, me);            
            log.info("FACEBOOK: original FB user login with fbId - " + user.getEmail() + ";email:" + user.getFbId());
            userMngr.save(user);
        }        
        
        String[] result = {this.prepareUserInfo(user, null, req)};        
        this.setUserCookie(resp, CookieHelper.USER, user.getToken());
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
        
    //TODO move to usermanager
    public String fbWebSignup(String accessToken, String expires, HttpServletRequest req, HttpServletResponse resp) {
        FacebookMe me = new FacebookLib().getFbMe(accessToken);
        return this.fbSignup(me, expires, req, resp);
    }
    
    public String signup(String email, String password, String name, String token,
                         String captchaFilename, String captchaText,
                         String sphere, String lang,
                         String year,
                         boolean isTemp,
                         HttpServletRequest req, HttpServletResponse resp) {        
        //validate basic inputs
        int status = NnUserValidator.validate(email, password, name, req);
        if (status != NnStatusCode.SUCCESS) 
            return this.assembleMsgs(status, null);
        lang = this.checkLang(lang);    
        sphere = this.checkLang(sphere);
        if (lang == null || sphere == null)
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);                
        
        //convert from guest
        NnGuestManager guestMngr = new NnGuestManager();
        NnGuest guest = guestMngr.findByToken(token);
        if (guest == null && captchaFilename != null) {
            log.info("such guest does not exist, where does this token from");
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        }
        if (guest != null) {
            if (captchaFilename != null || captchaText != null) {
                status = this.checkCaptcha(guest, captchaFilename, captchaText);
                if (status != NnStatusCode.SUCCESS) 
                    return this.assembleMsgs(status, null);
            }
        }
        //Convert email "tdell9x9@gmail.com" to "tdell9x9-AT-gmail.com@9x9.tv",
        short type = NnUser.TYPE_USER;
        if (email.contains("-AT-") && email.contains("@9x9.tv")) {
            type = NnUser.TYPE_YOUTUBE_CONNECT;
        }        
        NnUser user = new NnUser(email, password, name, type, mso.getId());
        user.setSphere(sphere);
        user.setLang(lang);        
        user.setDob(year);        
        user.setTemp(isTemp);
        status = userMngr.create(user, req, (short)0);
        if (status != NnStatusCode.SUCCESS)
            return this.assembleMsgs(status, null);
        
        userMngr.subscibeDefaultChannels(user);                            
        String[] result = {this.prepareUserInfo(user, null, req)};        
        this.setUserCookie(resp, CookieHelper.USER, user.getToken());
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public int checkRO() {
        MsoConfigManager configMngr = new MsoConfigManager();
        MsoConfig config = configMngr.findByItem(MsoConfig.RO);
        if (config != null && config.getValue().equals("1"))            
            return NnStatusCode.DATABASE_READONLY;
        return NnStatusCode.SUCCESS;
    }
    
    public String guestRegister(HttpServletRequest req, HttpServletResponse resp) {
        //verify input        
        NnGuestManager mngr = new NnGuestManager();
        NnGuest guest = new NnGuest(NnGuest.TYPE_GUEST);
        mngr.save(guest, req);        
        
        String[] result = {""};
        String sphere = NnUserManager.findLocaleByHttpRequest(req);
        result[0] += assembleKeyValue("token", guest.getToken());
        result[0] += assembleKeyValue("name", NnUser.GUEST_NAME);
        result[0] += assembleKeyValue("sphere", sphere);
        result[0] += assembleKeyValue("lastLogin", "");

        //prepare cookie and output
        this.setUserCookie(resp, CookieHelper.USER, guest.getToken());
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public String userTokenVerify(String token, HttpServletRequest req, HttpServletResponse resp) {
        if (token == null) {return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);}
        
        NnGuestManager guestMngr = new NnGuestManager();
        NnUser user = userMngr.findByToken(token);
        NnGuest guest = guestMngr.findByToken(token);
        if (user == null && guest == null) {
            CookieHelper.deleteCookie(resp, CookieHelper.USER);
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        }
        if (user != null) {
            if (user.getEmail().equals(NnUser.GUEST_EMAIL))
                return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
            if (user.isFbUser()) {
                FacebookLib lib = new FacebookLib();
                FacebookMe me = lib.getFbMe(user.getToken());
                if (me.getStatus() == FacebookMe.STATUS_ERROR) {
                    log.info("FACEBOOK: access token invalid");
                    CookieHelper.deleteCookie(resp, CookieHelper.USER);
                    return this.assembleMsgs(NnStatusCode.USER_INVALID, null);                    
                } else {
                    user = userMngr.setFbProfile(user, me);
                    log.info("reset fb info:" + user.getId());
                }
            }

            userMngr.save(user); //change last login time (ie updateTime)
        }
        String[] result = {""};
        result[0] = this.prepareUserInfo(user, guest, req);
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }

    public String category(String id, String lang, boolean flatten) {
        lang = this.checkLang(lang);    
        if (lang == null)
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);                        
        if (id == null)
            id = "0";
        
        String[] result = {"", "", ""};
        CategoryManager catMngr = new CategoryManager();
        
        result[0] = "id" + "\t" + id + "\n";        
        List<Category> categories = catMngr.findPlayerCategories(lang);        
        for (Category c : categories) {
            String name =  c.getName();
            int cnt = c.getChannelCnt();
            String subItemHint = "ch"; //what's under this level
            String[] str = {String.valueOf(c.getId()), 
                            name, 
                            String.valueOf(cnt), 
                            subItemHint};                
            result[1] += NnStringUtil.getDelimitedStr(str) + "\n";
        }

        //flatten result process
        if (id.equals("0") && flatten) {
            List<String> flattenResult = new ArrayList<String>();
            flattenResult.add(result[0]);
            flattenResult.add(result[1]);
            String size[] = new String[flattenResult.size()];
            return this.assembleMsgs(NnStatusCode.SUCCESS, flattenResult.toArray(size));
        } else {
            return this.assembleMsgs(NnStatusCode.SUCCESS, result);            
        }
    }
     
    public String brandInfo(HttpServletRequest req) {
        String[] result = msoMngr.getBrandInfoCache(false);
        boolean readOnly = MsoConfigManager.isInReadonlyMode(false);
        //locale
        String locale = this.findLocaleByHttpRequest(req);
        result[0] += PlayerApiService.assembleKeyValue("locale", locale);
        //counter
        int counter = 0;
        if (!readOnly)
            counter = this.addMsoInfoVisitCounter(readOnly);        
        result[0] += PlayerApiService.assembleKeyValue("brandInfoCounter", String.valueOf(counter));
        //piwik
        result[0] += PlayerApiService.assembleKeyValue("piwik", "http://" + MsoConfigManager.getPiwikDomain() + "/");
        String acceptLang = req.getHeader("Accept-Language");
        result[0] += PlayerApiService.assembleKeyValue("acceptLang", acceptLang);
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);        
    }    

    public String findLocaleByHttpRequest(HttpServletRequest req) {
        String locale = NnUserManager.findLocaleByHttpRequest(req);
        return locale;
    }
    
    public String pdr(String userToken, String deviceToken,             
                      String session, String pdr,
                      HttpServletRequest req) {
        if (userToken == null && deviceToken == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        if (pdr == null || pdr.length() == 0) 
            return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);    
        
        //pdr process
        PdrManager pdrMngr = new PdrManager(); 
        NnUser user = null;
        if (userToken != null) { 
            //verify input
            @SuppressWarnings("rawtypes")
            HashMap map = this.checkUser(userToken, false);
            user = (NnUser) map.get("u");
        }
        List<NnDevice> devices = new ArrayList<NnDevice>();
        NnDevice device = null;
        if (deviceToken != null) {
            NnDeviceManager deviceMngr = new NnDeviceManager();
            devices = deviceMngr.findByToken(deviceToken);
            if (devices.size() > 0)
                device = devices.get(0);
        }
        if (device == null && user == null)
            return this.assembleMsgs(NnStatusCode.ACCOUNT_INVALID, null);
        
        String ip = NnNetUtil.getIp(req);         
        pdrMngr.processPdr(user, device, session, pdr, ip);
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }    
        
    public String unsubscribe(String userToken, String channelId, String grid, String pos) {
        //verify input
        if (userToken == null || userToken.equals("undefined"))
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        if (channelId == null && pos == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        //verify user
        NnUser user = new NnUserManager().findByToken(userToken);
        if (user == null)
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);        
        //unsubscribe
        NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
        if (channelId != null) {
            String[] chArr = channelId.split(",");
            if (chArr.length == 1) {
                log.info("unsubscribe single channel");
                NnUserSubscribe s = null;
                if (grid == null) {
                    s = subMngr.findByUserAndChannel(user, Long.parseLong(channelId));
                } else {
                    s = subMngr.findChannelSubscription(user, Long.parseLong(channelId), Short.parseShort(grid));
                }
                subMngr.unsubscribeChannel(user, s);                
                NnUserWatchedManager watchedMngr = new NnUserWatchedManager();
                NnUserWatched watched = watchedMngr.findByUserTokenAndChannel(user.getToken(), Long.parseLong(channelId));
                if (watched != null) {
                    watchedMngr.delete(user, watched);
                }                                
            } else {
                log.info("unsubscribe multiple channels");
            }
        }
        if (pos != null) {
            NnUserSubscribeGroupManager groupMngr = new NnUserSubscribeGroupManager();
            NnUserSubscribeGroup group = groupMngr.findByUserAndSeq(user, Short.parseShort(pos));
            if (group != null)
                groupMngr.delete(user, group);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }        
    
    public String subscribe(String userToken, String channelId, String gridId) {        
        //verify input
        @SuppressWarnings("rawtypes")
        HashMap map = this.checkUser(userToken, false);
        if ((Integer)map.get("s") != NnStatusCode.SUCCESS) {
            return this.assembleMsgs((Integer)map.get("s"), null);
        }
        if (channelId == null && gridId == null) 
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);        
        NnUser user = (NnUser) map.get("u");                
        NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
        //verify channel and grid
        if (channelId == null)
            return this.assembleMsgs(NnStatusCode.CHANNEL_INVALID, null);        
        if (channelId.contains("f-")) {
            String profileUrl = channelId.replaceFirst("f-", "");
            NnUser curator = userMngr.findByProfileUrl(profileUrl);
            if (curator == null)
                return this.assembleMsgs(NnStatusCode.CHANNEL_ERROR, null);
            NnChannel favoriteCh = chMngr.createFavorite(curator);
            channelId = String.valueOf(favoriteCh.getId());
        }
        long cId = Long.parseLong(channelId);            
        NnChannel channel = chMngr.findById(cId);            
        if (channel == null || channel.getStatus() == NnChannel.STATUS_ERROR)
            return this.assembleMsgs(NnStatusCode.CHANNEL_ERROR, null);
        
        short seq = Short.parseShort(gridId);
        if (seq > 72) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }

        NnUserSubscribe s = subMngr.findByUserAndSeq(user, seq);
        if (s != null)
            return this.assembleMsgs(NnStatusCode.SUBSCRIPTION_POS_OCCUPIED, null);        
        s = subMngr.findByUserAndChannel(user, cId);        
        if (s != null)
            return this.assembleMsgs(NnStatusCode.SUBSCRIPTION_DUPLICATE_CHANNEL, null);        
        s = subMngr.subscribeChannel(user, cId, seq, MsoIpg.TYPE_GENERAL);
        String result[] = {""};
        result[0] = s.getSeq() + "\t" + s.getChannelId();
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }

    public String categoryInfo(String id, String tagStr, String start, String count, String sort) {
        if (id == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        CategoryManager catMngr = new CategoryManager();
        long cid = Long.parseLong(id);
        Category cat = catMngr.findById(cid);
        if (cat == null)
            return this.assembleMsgs(NnStatusCode.CATEGORY_INVALID, null);
        List<NnChannel> channels = new ArrayList<NnChannel>();
        if (start == null)
            start = "1";
        if (count == null)
            count = "200";
        int startIndex = Integer.parseInt(start);
        int limit = Integer.valueOf(count);
        if (limit > 200)
            limit = 200;
        int page = 0;
        if (limit != 0) {
            page = (int) (startIndex / limit) + 1;
        }        
        if (tagStr != null) {
            channels = catMngr.findChannelsByTag(cid, true, tagStr); //TODO removed            
        } else {
            channels = catMngr.listChannels(page, limit, cat.getId());
        }
        String result[] = {"", "", ""};
        //category info        
        result[0] += assembleKeyValue("id", String.valueOf(cat.getId()));
        result[0] += assembleKeyValue("name", cat.getName());
        result[0] += assembleKeyValue("start", start);
        String total = String.valueOf(catMngr.findChannelSize(cat.getId()));
        if (count.equals("0"))
            count = total;
        result[0] += assembleKeyValue("count", count);
        result[0] += assembleKeyValue("total", total);        
        //category tag
        String tags = cat.getTag();
        if (tags != null) {
            String[] tag = tags.split(",");
            for (String t : tag) {
                result[1] += t + "\n";
            }
        }
        //result[2] += chMngr.composeChannelLineupCache(channels);
        result[2] += chMngr.composeChannelLineup(channels);
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public String channelStack(String stack, String lang, String userToken, String channel, boolean isReduced) {
        if (stack == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);

        lang = this.checkLang(lang);
        List<String> result = new ArrayList<String>();
        String[] cond = stack.split(",");
        for (String s : cond) {
            List<NnChannel> chs = new ArrayList<NnChannel>(); 
            if (s.equals(Tag.FEATURED)) {
                chs = chMngr.findBillboard(Tag.FEATURED, lang);
            }
            if (s.equals(Tag.HOT)) {
                chs = chMngr.findBillboard(Tag.HOT, lang);               
            }
            if (s.equals("trending")) {
                chs = chMngr.findBillboard(Tag.TRENDING, lang);               
            }
            if (s.equals(Tag.RECOMMEND)) {
                chs = new RecommendService().findRecommend(userToken, lang);
            }
            if (s.equals("mayLike")) {
                chs = new RecommendService().findMayLike(userToken, channel, lang);                
            }
            String output = "";
            if (isReduced) {
                output = chMngr.composeReducedChannelLineup(chs);
            } else {
                output = chMngr.composeChannelLineup(chs);
            }
            result.add(output);
        }
        String size[] = new String[result.size()];
        return this.assembleMsgs(NnStatusCode.SUCCESS, result.toArray(size));
    }

    public String subscriberLineup(String userToken, String curatorIdStr) {
        NnUser curator = userMngr.findByProfileUrl(curatorIdStr);
        if (curator == null)
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        List<NnChannel> channels = new ArrayList<NnChannel>();
        if (curator.getToken().equals(userToken)) {
            log.info("find channels curator himself created");
            channels = chMngr.findByUserAndHisFavorite(curator, 0, true);
        } else {
            log.info("find curator channels");
            channels = chMngr.findByUserAndHisFavorite(curator, 0, false);
        }
        userMngr.composeSubscriberInfoStr(channels);
        return "";
    }
    
    //TODO rewrite
    public String channelLineup(String userToken,
                                String curatorIdStr,
                                String subscriptions,
                                boolean userInfo, 
                                String channelIds, 
                                boolean setInfo, 
                                boolean isRequired,
                                boolean isReduced,
                                HttpServletRequest req) {                                 

        //verify input
        if (((userToken == null && userInfo == true) || 
            (userToken == null && channelIds == null) || 
            (userToken == null && setInfo == true))) {
            if (curatorIdStr == null && subscriptions == null)
                return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        List<String> result = new ArrayList<String>();
        NnUser user = null;
        if (userToken != null || subscriptions != null) {
            if (subscriptions != null) {
                user = userMngr.findByProfileUrl(subscriptions);
            } else {
                user = userMngr.findByToken(userToken);
            }
            if (user == null) {
                NnGuest guest = new NnGuestManager().findByToken(userToken);
                if (guest == null)
                    return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
                else
                    return this.assembleMsgs(NnStatusCode.SUCCESS, null);
            }
        }
        
        //user info
        if (userInfo)
            result.add(this.prepareUserInfo(user, null, req));
        NnUserSubscribeGroupManager groupMngr = new NnUserSubscribeGroupManager();
        if (curatorIdStr == null && user != null ) {
            List<NnUserSubscribeGroup> groups = groupMngr.findByUser(user);    
            //set info
            if (setInfo) {
                String setOutput = "";
                for (NnUserSubscribeGroup g : groups) {
                    String[] obj = {
                            String.valueOf(g.getSeq()),
                            String.valueOf(g.getId()),
                            g.getName(),                        
                            g.getImageUrl(),
                            String.valueOf(g.getType()),
                    };
                    setOutput += NnStringUtil.getDelimitedStr(obj) + "\n";
                }
                result.add(setOutput);
            }
        }
        //find channels
        List<NnChannel> channels = new ArrayList<NnChannel>();
        boolean channelPos = true;
        if (channelIds == null && curatorIdStr == null) {
            //find subscribed channels 
            NnUserSubscribeManager subMngr = new NnUserSubscribeManager();            
            channels = subMngr.findSubscribedChannels(user);
            log.info("user: " + user.getToken() + " find subscribed size:" + channels.size());
        } else if (curatorIdStr != null) {
            channelPos = false;
            NnUser curator = userMngr.findByProfileUrl(curatorIdStr);
            if (curator == null)
                return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
            List<NnChannel> curatorChannels = new ArrayList<NnChannel>();
            if (curator.getToken().equals(userToken)) {
                log.info("find channels curator himself created");
                curatorChannels = chMngr.findByUserAndHisFavorite(curator, 0, true);
            } else {
                log.info("find channels curator created for public");
                curatorChannels = chMngr.findByUserAndHisFavorite(curator, 0, false);
            }
            //List<NnChannel> curatorChannels = chMngr.findByUserAndHisFavorite(curator, 0, true);
            for (NnChannel c : curatorChannels) {
                if (c.isPublic() && c.getStatus() == NnChannel.STATUS_SUCCESS) {
                    channels.add(c);
                }
            }
        } else {
            //find specific channels            
            channelPos = false;
            String[] chArr = channelIds.split(",");
            if (chArr.length > 1) {
                List<Long> list = new ArrayList<Long>();
                for (int i=0; i<chArr.length; i++) { list.add(Long.valueOf(chArr[i]));}
                channels = chMngr.findByIds(list);
            } else {
                NnChannel channel = chMngr.findById(Long.parseLong(channelIds));
                if (channel != null) channels.add(channel);                    
            }
        }
        if (isRequired && channels.size() == 0)
            return this.assembleMsgs(NnStatusCode.CHANNEL_INVALID, null);
        //sort by seq
        if (channelPos) {
            if (user.getType() != NnUser.TYPE_YOUTUBE_CONNECT) {
                TreeMap<Short, NnChannel> channelMap = new TreeMap<Short, NnChannel>();
                for (NnChannel c : channels) {
                    channelMap.put(c.getSeq(), c);                
                }
                Iterator<Entry<Short, NnChannel>> it = channelMap.entrySet().iterator();
                channels.clear();      
                while (it.hasNext()) {
                    Map.Entry<Short, NnChannel> pairs = (Map.Entry<Short, NnChannel>)it.next();
                    channels.add((NnChannel)pairs.getValue());
                }
            }
        }
        String channelOutput = "";
        if (version < 32) {
            log.info("output ios string");
            channelOutput += new IosService().composeChannelLineup(channels);
        } else {
            if (isReduced) {
                log.info("output reduced string");
                channelOutput += chMngr.composeReducedChannelLineup(channels);
            } else {
                log.info("output v32 string");
                channelOutput += chMngr.composeChannelLineup(channels);
            }
        }
        if (channelPos && channelOutput != null) {
            channelOutput = this.chAdjust(channels, channelOutput);
        }
        
        result.add(channelOutput);
        String size[] = new String[result.size()];
        return this.assembleMsgs(NnStatusCode.SUCCESS, result.toArray(size));
    }

    public String channelSubmit(String categoryIds, String userToken, 
                                String url, String grid, 
                                String tags, String lang, 
                                HttpServletRequest req) {
        //verify input
        if (url == null || url.length() == 0 ||  grid == null || grid.length() == 0 ||
             userToken== null || userToken.length() == 0) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        if (Integer.parseInt(grid) < 0 || Integer.parseInt(grid) > 81) {            
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        }
        url = url.trim();
        //verify user
        NnUser user = userMngr.findByToken(userToken);
        if (user == null) 
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);        
        if (user.getEmail().equals(NnUser.GUEST_EMAIL))
            return this.assembleMsgs(NnStatusCode.USER_PERMISSION_ERROR, null);
                
        NnChannelManager chMngr = new NnChannelManager();        
        //verify url, also converge youtube url
        url = chMngr.verifyUrl(url);         
        if (url == null)
            return this.assembleMsgs(NnStatusCode.CHANNEL_URL_INVALID, null);            
        
        //verify channel status for existing channel
        NnChannel channel = chMngr.findBySourceUrl(url);                                        
        if (channel != null && (channel.getStatus() == NnChannel.STATUS_ERROR)) {
            log.info("channel id and status :" + channel.getId()+ ";" + channel.getStatus());
            this.assembleMsgs(NnStatusCode.CHANNEL_STATUS_ERROR, null);
        }
        
        //create a new channel
        lang = this.checkLang(lang);
        if (channel == null) {
            channel = chMngr.create(url, null, lang, req);
            if (channel == null) {
                return this.assembleMsgs(NnStatusCode.CHANNEL_URL_INVALID, null);
            }            
            channel.setTag(tags);
            log.info("User throws a new url:" + url);
        }
        
        //subscribe
        NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
        subMngr.subscribeChannel(user, channel.getId(), Short.parseShort(grid), MsoIpg.TYPE_GENERAL);
        String result[] = {""};
        String channelName = "";
        if (channel.getSourceUrl() != null && channel.getSourceUrl().contains("http://www.youtube.com"))
            channelName = YouTubeLib.getYouTubeChannelName(channel.getSourceUrl());
        if (channel.getContentType() == NnChannel.CONTENTTYPE_FACEBOOK) 
            channelName = channel.getSourceUrl();            
        String output[]= {String.valueOf(channel.getId()),                             
                          channel.getName(),
                          channel.getImageUrl(),
                          String.valueOf(channel.getContentType()),
                          channelName};
        result[0] = NnStringUtil.getDelimitedStr(output);
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    //TODO if use if fb user, redirect to facebook
    public String login(String email, String password, HttpServletRequest req, HttpServletResponse resp) {        
        log.info("login: email=" + email + ";password=" + password);
        if (!BasicValidator.validateRequired(new String[] {email, password}))
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);;        

        String result[] = {""};
        NnUser user = userMngr.findAuthenticatedUser(email, password, req);
        if (user != null) {
            result[0] = this.prepareUserInfo(user, null, req);
            userMngr.save(user); //change last login time (ie updateTime)
            this.setUserCookie(resp, CookieHelper.USER, user.getToken());
        } else {
            return this.assembleMsgs(NnStatusCode.USER_LOGIN_FAILED, null);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public String setProgramProperty(String program, String property, String value) {
        if (program == null || property == null || value == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        NnProgramManager programMngr = new NnProgramManager();
        NnProgram p = programMngr.findById(Long.parseLong(program));
        if (p == null)
            return this.assembleMsgs(NnStatusCode.PROGRAM_INVALID, null);
        if (property.equals("duration")) {
            p.setDuration(value);
            programMngr.save(p);
        } else {
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null); 
    }

    public String setChannelProperty(String channel, String property, String value) {
        if (channel == null || property == null || value == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        NnChannelDao dao = new NnChannelDao();
        NnChannel c = dao.findById(Long.parseLong(channel));
        if (c == null)
            return this.assembleMsgs(NnStatusCode.CHANNEL_INVALID, null);
        if (property.equals("count")) {
            c.setCntEpisode(Integer.valueOf(value));
        } else if (property.equals("updateDate")) {
            long epoch = Long.parseLong(value);
            Date date = new Date (epoch*1000);
            log.info("Date:" + date);
            c.setUpdateDate(date);
        } else {
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        }
        dao.save(c);
        return this.assembleMsgs(NnStatusCode.SUCCESS, null); 
    }
    
    //TODO real range search
    public String tagInfo(String name, String start, String count) {
        if (name == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        String[] result = {"", ""};
        TagManager tagMngr = new TagManager();
        Tag tag = tagMngr.findByName(name);
        if (tag == null)
            return this.assembleMsgs(NnStatusCode.TAG_INVALID, null);
                        
        List<NnChannel> channels = new TagManager().findChannelsByTag(name, true);
        start = start == null ? "1" : start;
        count = count == null ? String.valueOf(channels.size()) : count;        
        int startIndex = Integer.parseInt(start) - 1;
        int endIndex = Integer.parseInt(count);
        startIndex = startIndex < 0 ? 0 : startIndex;
        endIndex = endIndex > channels.size() ? channels.size() : endIndex;
        int total = channels.size();
        channels = channels.subList(startIndex, startIndex + Integer.parseInt(count));
        log.info("startIndex:" + startIndex + ";endIndex:" + endIndex);
        result[1] += chMngr.composeChannelLineup(channels);
        
        result[0] += assembleKeyValue("id", String.valueOf(tag.getId()));
        result[0] += assembleKeyValue("name", tag.getName());
        result[0] += assembleKeyValue("start", start);
        result[0] += assembleKeyValue("count", count);
        result[0] += assembleKeyValue("total", String.valueOf(total));
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public String programInfo(String channelIds, String userToken, 
                                  String ipgId, boolean userInfo,
                                  String sidx, String limit, HttpServletRequest req) {
        if (channelIds == null || (channelIds.equals("*") && userToken == null && ipgId == null)) {           
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        NnProgramManager programMngr = new NnProgramManager();        
        String[] chArr = channelIds.split(",");
        NnUser user = null;
        long sidxL = 0;
        long limitL = 0;
        if (sidx != null) { sidxL = Long.parseLong(sidx); } 
        if (limit != null) {limitL = Long.parseLong(limit);}
        if ((sidx != null && limit == null) || (sidx == null && limit != null))
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
    
        String programInfoStr = "";
        if (channelIds.equals("*")) {
            user = userMngr.findByToken(userToken);
            if (user == null) {
                NnGuest guest = new NnGuestManager().findByToken(userToken);
                if (guest == null)
                    return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
                else
                    return this.assembleMsgs(NnStatusCode.SUCCESS, null);
            }
        } else if (chArr.length > 1) {            
            List<Long> list = new ArrayList<Long>();
            for (int i=0; i<chArr.length; i++) { list.add(Long.valueOf(chArr[i]));}
            for (Long l : list) {
                if (version < 32) {
                    programInfoStr = new IosService().findPlayerProgramInfoByChannel(l, sidxL, limitL);
                } else {
                    programInfoStr += programMngr.findPlayerProgramInfoByChannel(l, sidxL, limitL);
                }
            }
        } else {
            if (version < 32) {                
                programInfoStr = new IosService().findPlayerProgramInfoByChannel(Long.parseLong(channelIds), sidxL, limitL);
                if (programInfoStr != null && new PlayerService().isIos(req)) {
                    String[] lines = programInfoStr.split("\n");
                    String debugStr = "";
                    if (lines.length > 0) {
                        for (int i=0; i<lines.length; i++) {
                            String[] tabs = lines[i].split("\t");
                            if (tabs.length > 1)
                                debugStr += tabs[1] + "; ";
                        }
                    }
                    log.info("ios program info debug string:" + debugStr);
                }                                   
            } else {            
                programInfoStr = programMngr.findPlayerProgramInfoByChannel(Long.parseLong(channelIds), sidxL, limitL);
            }
        }        
        
        MsoConfig config = new MsoConfigManager().findByMsoAndItem(mso, MsoConfig.CDN);
        if (config == null) {
            config = new MsoConfig(mso.getId(), MsoConfig.CDN, MsoConfig.CDN_AMAZON);
            log.severe("mso config does not exist! mso: " + mso.getId());
        }
        String userInfoStr = "";
        if (userInfo) {
            if (user == null && userToken != null) 
                user = userMngr.findByToken(userToken);
                userInfoStr = this.prepareUserInfo(user, null, req);
        }
        if (userInfo) {
            String[] result = {userInfoStr, programInfoStr};
            return this.assembleMsgs(NnStatusCode.SUCCESS, result);
        } else {
            String[] result = {programInfoStr};
            return this.assembleMsgs(NnStatusCode.SUCCESS, result);            
        }
    }    

    public String saveIpg(String userToken, String channelId, String programId) {
        //obsolete
        return this.assembleMsgs(NnStatusCode.ERROR, null);                 
    }    

    public String loadIpg(long ipgId) {
        //obsolete
        return this.assembleMsgs(NnStatusCode.ERROR, null);                 
    }            

    public String moveChannel(String userToken, String grid1, String grid2) {        
        //verify input
        if (userToken == null || userToken.length() == 0 || userToken.equals("undefined") || grid1 == null || grid2 == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        if (!Pattern.matches("^\\d*$", grid1) || !Pattern.matches("^\\d*$", grid2) ||
            Integer.parseInt(grid1) < 0 || Integer.parseInt(grid1) > 81 ||
            Integer.parseInt(grid2) < 0 || Integer.parseInt(grid2) > 81) {
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        }        
        NnUser user = userMngr.findByToken(userToken);
        if (user == null) 
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        
        NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
        boolean success = subMngr.moveSeq(user, Short.parseShort(grid1), Short.parseShort(grid2));

        if (!success) { return this.assembleMsgs(NnStatusCode.SUBSCRIPTION_ERROR, null); }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String setSetInfo(String userToken, String name, String pos) {
        //verify input
        if (name == null || pos == null)  {            
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        if (!Pattern.matches("^\\d*$", pos) || Integer.parseInt(pos) < 0 || Integer.parseInt(pos) > 9) {            
            return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);
        }

        NnUser user = new NnUserManager().findByToken(userToken);
        if (user == null)
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);    
        
        NnUserSubscribeGroupManager subSetMngr = new NnUserSubscribeGroupManager();
        short position = Short.valueOf(pos);
        NnUserSubscribeGroup subSet = subSetMngr.findByUserAndSeq(user, Short.valueOf(position));
        if (subSet!= null) {
            subSet.setName(name);
            subSetMngr.save(user, subSet);
        } else {
            subSet = new NnUserSubscribeGroup();
            subSet.setUserId(user.getId());
            subSet.setName(name);                
            subSet.setSeq(position);
            subSetMngr.create(user, subSet);            
        }
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }
    
    private String checkLang(String lang) {
        if (lang == null || lang.length() == 0)
            return LangTable.LANG_EN;
        if (lang != null && !lang.equals(LangTable.LANG_EN) && !lang.equals(LangTable.LANG_ZH))
            return null;
        return lang;
    }
    
    public String staticContent(String key, String lang) {
        NnContentManager contentMngr = new NnContentManager();
        NnContent content = contentMngr.findByItemAndLang(key, lang);        
        if (content == null)
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        lang = this.checkLang(lang);
        if (lang == null)
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);        
        String[] result = {content.getValue()};
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public String deviceRegister(String userToken, String type, HttpServletRequest req, HttpServletResponse resp) {
        NnUser user = null;
        if (userToken != null) {
            @SuppressWarnings({ "rawtypes"})
            HashMap map = this.checkUser(userToken, false);
            if ((Integer)map.get("s") != NnStatusCode.SUCCESS) {
                return this.assembleMsgs((Integer)map.get("s"), null);
            }
            user = (NnUser) map.get("u");
        }
        NnDeviceManager deviceMngr = new NnDeviceManager();
        deviceMngr.setReq(req); //!!!
        NnDevice device = deviceMngr.create(null, user, type);        
        String[] result = {device.getToken()};
        this.setUserCookie(resp, CookieHelper.DEVICE, device.getToken());
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
      
    public String deviceTokenVerify(String token, HttpServletRequest req) {
        if (token == null)
            return this.assembleMsgs(NnStatusCode.SUCCESS, null);
        NnDeviceManager deviceMngr = new NnDeviceManager();
        deviceMngr.setReq(req); //!!!
        List<NnDevice> devices = deviceMngr.findByToken(token);
        if (devices.size() == 0)
            return this.assembleMsgs(NnStatusCode.DEVICE_INVALID, null);
        List<NnUser> users = new ArrayList<NnUser>(); 
        log.info("<<<<<<<<<<< device size>>>>>>>" + devices.size());
        for (NnDevice d : devices) {
            if (d.getUserId() != 0) {
                NnUser user = userMngr.findById(d.getUserId(), (short)1);
                if (user != null)
                    users.add(user);
                else
                    log.info("bad data in device:" + d.getToken() + ";userId:" + d.getUserId());
            }    
        }        
        String[] result = {""};
        for (NnUser u : users) {
            result[0] += u.getToken() + "\t" + u.getName() + "\t" + u.getUserEmail() + "\n";
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }

    public String deviceAddUser(String deviceToken, String userToken, HttpServletRequest req) {
        NnUser user = null;
        if (userToken != null) {
            @SuppressWarnings("rawtypes")
            HashMap map = this.checkUser(userToken, false);
            if ((Integer)map.get("s") != NnStatusCode.SUCCESS) {
                return this.assembleMsgs((Integer)map.get("s"), null);
            }
            user = (NnUser) map.get("u");
        }
        if (deviceToken == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        NnDeviceManager deviceMngr = new NnDeviceManager();
        NnDevice device = deviceMngr.addUser(deviceToken, user);
        if (device == null)
            return this.assembleMsgs(NnStatusCode.DEVICE_INVALID, null);
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String deviceRemoveUser(String deviceToken, String userToken, HttpServletRequest req) {
        NnUser user = null;
        if (userToken != null) {
            @SuppressWarnings("rawtypes")
            HashMap map = this.checkUser(userToken, false);
            if ((Integer)map.get("s") != NnStatusCode.SUCCESS) {
                return this.assembleMsgs((Integer)map.get("s"), null);
            }
            user = (NnUser) map.get("u");
        }
        if (deviceToken == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        NnDeviceManager deviceMngr = new NnDeviceManager();
        boolean success = deviceMngr.removeUser(deviceToken, user);
        if (!success) 
            return this.assembleMsgs(NnStatusCode.DEVICE_INVALID, null);
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private HashMap checkUser(String userToken, boolean guestOK) {
        HashMap map = new HashMap();        
        //verify input
        if (userToken == null || userToken.length() == 0 || userToken.equals("undefined"))
            map.put("s", NnStatusCode.INPUT_MISSING);
        if (guestOK) {
            map.put("s", NnStatusCode.SUCCESS);
            return map;
        }
        //verify user
        NnUser user = userMngr.findByToken(userToken);
        if (user == null) {
            map.put("s", NnStatusCode.USER_INVALID);
            return map;
        }
        if (!guestOK && user.getUserEmail().equals(NnUser.GUEST_EMAIL) ) {
            map.put("s", NnStatusCode.USER_PERMISSION_ERROR);
            return map;
        }
        map.put("s", NnStatusCode.SUCCESS);
        map.put("u", user);
        return map;
    }

    private int checkCaptcha(NnGuest guest, String fileName, String name) {
        NnGuestManager guestMngr = new NnGuestManager();
        if (guest == null)
            return NnStatusCode.CAPTCHA_INVALID;
        if (guest.getCaptchaId() == 0)
            return NnStatusCode.CAPTCHA_INVALID;
        Captcha c = new CaptchaManager().findById(guest.getCaptchaId());
        log.info(guest.getGuessTimes() + ";" + NnGuest.GUESS_MAXTIMES);
        if (guest.getGuessTimes() >= NnGuest.GUESS_MAXTIMES)
            return NnStatusCode.CAPTCHA_TOOMANY_TRIES;
        if (!c.getFileName().equals(fileName) || 
            !c.getName().equals(name)) {
            guest.setGuessTimes(guest.getGuessTimes()+1);
            guestMngr.save(guest, null);
            return NnStatusCode.CAPTCHA_FAILED;
        }
        Date now = new Date();
        if (now.after(guest.getExpiredAt()))
            return NnStatusCode.CAPTCHA_FAILED;
        return NnStatusCode.SUCCESS;
    }
     
    public String recentlyWatched(String userToken, String count, boolean channelInfo, boolean episodeIndex) {
        @SuppressWarnings("rawtypes")
        HashMap map = this.checkUser(userToken, false);
        if ((Integer)map.get("s") != NnStatusCode.SUCCESS) {
            return this.assembleMsgs((Integer)map.get("s"), null);
        }
        if (count == null) 
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);        
        int cnt = Integer.parseInt(count);
        if (episodeIndex) {
            if (cnt > 5) {
                return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
            }
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }
    
    public String userReport(String userToken, String deviceToken, String session, String type, String item, String comment) {
        if (comment == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        if (userToken == null && deviceToken == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        if (comment.length() > 500)
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        
        NnUser user = null;
        if (userToken != null) { 
            //verify input
            @SuppressWarnings("rawtypes")
            HashMap map = this.checkUser(userToken, false);
            user = (NnUser) map.get("u");
        }
        List<NnDevice> devices = new ArrayList<NnDevice>();
        NnDevice device = null;
        if (deviceToken != null) {
            NnDeviceManager deviceMngr = new NnDeviceManager();
            devices = deviceMngr.findByToken(deviceToken);
            if (devices.size() > 0)
                device = devices.get(0);
        }
        if (device == null && user == null)
            return this.assembleMsgs(NnStatusCode.ACCOUNT_INVALID, null);

        String content = "";
        if (item != null) {
            String[] key = item.split(",");
            String[] value = comment.split(",");
            if (key.length != value.length)
                return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);
            for (int i=0; i<key.length; i++) {
                content += key[i] + ":" + value[i] + "\n";
            }
        } else {
            content = comment; //backward compatibility
        }
        
        NnUserReportManager reportMngr = new NnUserReportManager();
        String[] result = {""};        
        NnUserReport report = reportMngr.save(user, device, session, type, item, content);
        if (report != null && user != null) {
            result[0] = PlayerApiService.assembleKeyValue("id", String.valueOf(report.getId()));
            EmailService service = new EmailService();
            String toEmail = "feedback@9x9.tv";
            String toName = "feedback";
            String subject = "User send a report";
            String body = "user ui-lang:" + user.getLang() + "\n";
            body += "user region:" + user.getSphere() + "\n\n";
            body += content;
            try {
                body = URLDecoder.decode(body, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }                
            
            subject += (type != null) ? (" (" + type + ")") : "" ;

            log.info("subject:" + subject);
            log.info("content:" + body);
            NnEmail mail = new NnEmail(toEmail, toName, 
                                       user.getUserEmail(), user.getName(), 
                                       user.getUserEmail(), subject, body);
            service.sendEmail(mail, "userfeedback@9x9.tv", "userfeedback");
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }

    public String setUserProfile(String userToken, String items, String values, HttpServletRequest req) {
        //verify input
        if (userToken == null || userToken.length() == 0 || userToken.equals("undefined") ||
            items == null || values == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        //verify user
        NnUser user = userMngr.findByToken(userToken);
        if (user == null) 
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);        
        if (user.getUserEmail().equals(NnUser.GUEST_EMAIL))
            return this.assembleMsgs(NnStatusCode.USER_PERMISSION_ERROR, null);        
        String[] key = items.split(",");
        String[] value = values.split(",");
        String password = "";
        String oldPassword = "";
        if (key.length != value.length) {
            log.info("key and value length mismatches!");
            return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);
        }
        String[] valid = {"name", "year", "password", 
                "oldPassword", "sphere", "ui-lang", "gender", "description", "image"};
        //description,lang,name
        HashSet<String> dic = new HashSet<String>();
        for (int i=0; i<valid.length; i++) {
            dic.add(valid[i]);
        }
        for (int i=0; i<key.length; i++) {
           if (!dic.contains(key[i])) {
                log.info("contains not valid key value!");
                return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);
            }
           if (user.isFbUser()) {
               if (!key[i].equals("sphere") && !key[i].equals("ui-lang")) {
                   log.info("fbuser: not permitted key:" + key);
                   return this.assembleMsgs(NnStatusCode.USER_PERMISSION_ERROR, null);
               }
               log.info("fbuser: otherwise key:" + key);
           }
            String theValue = value[i];
            try {
                theValue = URLDecoder.decode(theValue, "utf-8");
                theValue = NnStringUtil.htmlSafeAndTruncated(theValue);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }                
            
            if (key[i].equals("name")) {
                if (theValue.equals(NnUser.GUEST_NAME))
                    return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);
                user.setName(theValue);
            }
            if (key[i].equals("image"))
                user.setImageUrl(theValue);
            if (key[i].equals("year"))
                user.setDob(theValue);
            if (key[i].equals("description"))
                user.setIntro(theValue);
            if (key[i].equals("password"))
                password = theValue;                
            if (key[i].equals("oldPassword"))
                oldPassword = theValue;                
            if (key[i].equals("sphere")) {
                if ((theValue == null) || (this.checkLang(theValue) == null))
                    return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
                user.setSphere(theValue);
            }
            if (key[i].equals("gender"))
                user.setGender(Short.parseShort(theValue));                        
            if (key[i].equals("ui-lang")) {
                if ((theValue == null) || (this.checkLang(theValue) == null))
                    return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
                user.setLang(theValue);
            }
        }        
        int status = NnUserValidator.validateProfile(user);
        if (status != NnStatusCode.SUCCESS) {
            log.info("profile fail");
            return this.assembleMsgs(status, null);
        }
        if (password.length() > 0 && oldPassword.length() > 0) {
            NnUser authenticated = userMngr.findAuthenticatedUser(user.getEmail(), oldPassword, req);
            if (authenticated == null)
                return this.assembleMsgs(NnStatusCode.USER_LOGIN_FAILED, null);
            status = NnUserValidator.validatePassword(password);
            if (status != NnStatusCode.SUCCESS)
                return this.assembleMsgs(status, null);
            user.setPassword(password);
            user.setSalt(AuthLib.generateSalt());
            user.setCryptedPassword(AuthLib.encryptPassword(user.getPassword(), user.getSalt()));            
        }
        
        userMngr.save(user);
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }
    
    public String getUserProfile(String userToken) {
        //verify input
        if (userToken == null || userToken.length() == 0 || userToken.equals("undefined"))
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        //verify user
        NnUser user = userMngr.findByToken(userToken);
        if (user == null) 
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        String[] result = {""};     
        result[0] += assembleKeyValue("name", user.getName());
        result[0] += assembleKeyValue("email", user.getUserEmail());
        result[0] += assembleKeyValue("description", user.getIntro());
        result[0] += assembleKeyValue("image", user.getImageUrl());
        String gender = "";
        if (user.getGender() != 2)
            gender = String.valueOf(user.getGender());
        result[0] += assembleKeyValue("gender", gender);
        result[0] += assembleKeyValue("year", String.valueOf(user.getDob()));
        result[0] += assembleKeyValue("sphere", user.getSphere());
        result[0] += assembleKeyValue("ui-lang", user.getLang());
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public String shareByEmail(String userToken, String toEmail, String toName, 
            String subject, String content, 
            String captcha, String text) {        
        @SuppressWarnings("rawtypes")
        HashMap map = this.checkUser(userToken, false);
        if ((Integer)map.get("s") != NnStatusCode.SUCCESS) {
            return this.assembleMsgs((Integer)map.get("s"), null);
        }
        if (captcha == null || text == null || toEmail == null || content == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        NnUser user = (NnUser) map.get("u");
        if (captcha != null) {
            NnGuestManager guestMngr = new NnGuestManager();
            NnGuest guest = guestMngr.findByToken(userToken);
            int status = this.checkCaptcha(guest, captcha, text);
            if (status != NnStatusCode.SUCCESS)
                return this.assembleMsgs(status, null);
            guestMngr.delete(guest);
        }
        EmailService service = new EmailService();
        NnEmail mail = new NnEmail(toEmail, toName, NnEmail.SEND_EMAIL_SHARE, user.getName(), user.getUserEmail(), subject, content);        
        service.sendEmail(mail, null, null);
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String setUserPref(String token, String item, String value) {
        //verify input
        if (token == null || token.length() == 0 || token.equals("undefined") ||
            item == null || value == null || item.length() == 0 || value.length() == 0) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }        
        //verify user
        NnUser user = userMngr.findByToken(token);
        if (user == null) 
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);        
        //get preference
        NnUserPrefManager prefMngr = new NnUserPrefManager();
        NnUserPref pref = prefMngr.findByUserAndItem(user, item);
        if (pref != null) {
            pref.setValue(value);            
            prefMngr.save(user, pref);
        } else {
            pref = new NnUserPref(user, item, value);
            prefMngr.save(user, pref);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }
    
    public String requestCaptcha(String token, String action, HttpServletRequest req) {
        if (token == null || action == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        CaptchaManager mngr = new CaptchaManager();
        Captcha c = mngr.getRandom();
        if (c == null)
            return this.assembleMsgs(NnStatusCode.CAPTCHA_ERROR, null);
        short a = Short.valueOf(action);   
        Calendar cal = Calendar.getInstance();        
        cal.add(Calendar.MINUTE, 5);        
        NnGuestManager guestMngr = new NnGuestManager();
        NnGuest guest = guestMngr.findByToken(token);                
        if (a == Captcha.ACTION_SIGNUP) {
            if (guest == null)
                return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        } else if (a == Captcha.ACTION_EMAIL) {
            if (guest == null) {
                guest = new NnGuest(NnGuest.TYPE_USER);
                guest.setToken(token);
            }
        }
        guest.setCaptchaId(c.getId());
        guest.setExpiredAt(cal.getTime());
        guest.setGuessTimes(0);
        guestMngr.save(guest, req);        
        return this.assembleMsgs(NnStatusCode.SUCCESS, new String[] {c.getFileName()});
    }    

    public String saveSorting(String token, String channelId, String sort) {
        @SuppressWarnings("rawtypes")
        HashMap map = this.checkUser(token, false);
        if ((Integer)map.get("s") != NnStatusCode.SUCCESS) {
            return this.assembleMsgs((Integer)map.get("s"), null);
        }
        NnUser user = (NnUser) map.get("u");
        NnUserChannelSorting sorting = new NnUserChannelSorting(user.getId(), 
                                           Long.parseLong(channelId), Short.parseShort(sort));
        NnUserChannelSortingManager sortingMngr = new NnUserChannelSortingManager();
        sortingMngr.save(user, sorting);        
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String saveShare(String userToken, String channelId, String programId, String setId) {
        if (userToken == null || userToken.length() == 0 || userToken.equals("undefined") ||
            channelId == null || programId == null || channelId.length() == 0 || programId.length() == 0) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        if (!Pattern.matches("^\\d*$", channelId))
            return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);
        
        NnUser user = userMngr.findByToken(userToken);                
        if (user == null) 
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);

        NnUserShare share = new NnUserShare();
        share.setChannelId(Long.parseLong(channelId));
        if (Pattern.matches("^\\d*$", programId)) {
            share.setProgramId(Long.parseLong(programId));
        } else {
            share.setProgramIdStr(programId);
        }
        share.setUserId(user.getId());
        NnUserShareManager shareMngr = new NnUserShareManager();
        shareMngr.create(share);
        String result[] = {String.valueOf(share.getId())};
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);                
    }

    //deprecated
    public String loadShare(long id) {
        NnUserShareManager shareMngr = new NnUserShareManager();
        NnUserShare share= shareMngr.findById(id);
        if (share== null)
            return this.assembleMsgs(NnStatusCode.IPG_INVALID, null);
        
        String[] result = {"", ""};
        NnProgramManager programMngr = new NnProgramManager();
        NnProgram program = programMngr.findById(share.getProgramId());
        if (program != null) {
            List<NnProgram> programs = new ArrayList<NnProgram>();
            programs.add(program);
            result[0] = ""; //need to be something if not deprecated
        } else {            
            result[0] = share.getChannelId() + "\t" + share.getProgramIdStr() + "\n";            
        }
        NnChannel channel = chMngr.findById(share.getChannelId());        
        if (channel != null) {
            result[1] = chMngr.composeChannelLineupStr(channel) + "\n";
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public String userWatched(String userToken, String count, boolean channelInfo, boolean episodeIndex, String channel) {
        @SuppressWarnings("rawtypes")
        HashMap map = this.checkUser(userToken, false);
        if ((Integer)map.get("s") != NnStatusCode.SUCCESS) {
            return this.assembleMsgs((Integer)map.get("s"), null);
        }
        if (count == null) 
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);        
        int cnt = Integer.parseInt(count);
        if (episodeIndex) {
            if (cnt > 5) {
                return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
            }
        }
        String[] result = {"", ""};
        NnUserWatchedManager watchedMngr = new NnUserWatchedManager();
        NnProgramManager programMngr = new NnProgramManager();
        List<NnUserWatched> watched = new ArrayList<NnUserWatched>();
        if (channel == null) {
            watched = watchedMngr.findByUserToken(userToken);
        } else {
            NnUserWatched w = watchedMngr.findByUserTokenAndChannel(userToken, Long.parseLong(channel));
            if (w != null) { watched.add(w); }                
        }
        List<NnChannel> channels = new ArrayList<NnChannel>();
        int i = 1;
        for (NnUserWatched w : watched) {
            if (i > cnt)
                break;
            int index = 0;            
            if (episodeIndex && Pattern.matches("^\\d*$", w.getProgram())) {
                String programInfo = programMngr.findPlayerProgramInfoByChannel(w.getChannelId());
                if (programInfo != null && programInfo.length() > 0) {
                    index = programMngr.getEpisodeIndex(programInfo, w.getProgram());
                }
            }
            
            result[0] += w.getChannelId() + "\t" + w.getProgram() + "\t" + index + "\n";
            NnChannel c = chMngr.findById(w.getChannelId());
            if (c != null) { 
                channels.add(c);
            }
            i++;
        }
        if (channelInfo) {
            chMngr.composeChannelLineup(channels);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }

    public String copyChannel(String userToken, String channelId, String grid) {        
        //verify input
        if (userToken == null || userToken.length() == 0 || userToken.equals("undefined") || grid == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        if (!Pattern.matches("^\\d*$", grid) ||
            Integer.parseInt(grid) < 0 || Integer.parseInt(grid) > 81)
            return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);
        NnUser user = userMngr.findByToken(userToken);
        if (user == null)
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null); 
        
        NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
        boolean success = false;
        success = subMngr.copyChannel(user, Long.parseLong(channelId), Short.parseShort(grid));
        if (!success) 
            return this.assembleMsgs(NnStatusCode.SUBSCRIPTION_ERROR, null);
        else
            return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String forgotpwd(String email, HttpServletRequest req) {
        NnUser user = userMngr.findByEmail(email, req);
        if (user == null) {
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        }
        if (user.isFbUser())
            return this.assembleMsgs(NnStatusCode.USER_PERMISSION_ERROR, null);
        

        String link = NnNetUtil.getUrlRoot(req) + "/#!resetpwd!e=" + email + "!pass=" + userMngr.forgotPwdToken(user);        
        log.info("link:" + link);
        
        NnContentManager contentMngr = new NnContentManager();
        String lang = user.getLang();
        lang = this.checkLang(lang);
        log.info("user language:" + lang);
        NnContent content = contentMngr.findByItemAndLang("resetpwd", lang);        
        String subject = "Forgotten Password";
        if (lang.equals(LangTable.LANG_ZH))
            subject = "忘記密碼";
        String sentense = "<p>To reset the password, click on the link or copy and paste the following link into the address bar of your browser</p>";
        String body = sentense + "<p><a href = '" + link  + "'>" + link +  "</a></p>";            
        if (content != null) {
            log.info("get email template from admin portal");
            body = content.getValue();
            body = body.replace("Hello,<br>", "Hello " + user.getName() + ",<br>");
            body = body.replace("<a href=\"#\" style=\"color:#777777;font-weight:bold;\">Click here</a> to reset your 9x9.tv password.", 
                                "<a href=\"" + link + "\" style=\"color:#777777;font-weight:bold;\">Click here</a> to reset your 9x9.tv password.");
            body = body.replace("<a href=\"#\" style=\"color:#777777;font-weight:bold;\">http://9x9.tv/username/number</a>",
                                "<a href=\"" + link + "\" style=\"color:#777777;font-weight:bold;\">" + link + "</a>");
            body = body.replace("<a href=\"#\" style=\"border:0;\"><img src=\"http://s3.amazonaws.com/9x9ui/war/v2/images/resetpwd/btn.png\"", 
                                "<a href=\"" + link + "\" style=\"border:0;\"><img src=\"http://s3.amazonaws.com/9x9ui/war/v2/images/resetpwd/btn.png\"");

        }

        EmailService service = new EmailService();
        NnEmail mail = new NnEmail(
                email, user.getName(), 
                NnEmail.SEND_EMAIL_NOREPLY, "noreply", NnEmail.SEND_EMAIL_NOREPLY,                                     
                subject, body);
        
        mail.setHtml(true);
        service.sendEmail(mail, null, null);
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String resetpwd(String email, String token, String password, HttpServletRequest req) {
        if (email == null || token == null || password == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }

        NnUser user = userMngr.findByEmail(email, req);
        if (user == null) {
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        }
        String forgotPwdToken = userMngr.forgotPwdToken(user);
        log.info("forgotPwdToken:" + forgotPwdToken + ";token user passes:" + token);
        if (forgotPwdToken.equals(token)) {
            user.setPassword(password);
            userMngr.resetPassword(user);
            userMngr.save(user);
            log.info("reset password success:" + user.getEmail());
        } else {
            log.info("reset password token mismatch");
            return this.assembleMsgs(NnStatusCode.USER_PERMISSION_ERROR, null);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String search(String text, String stack, String start, String count, HttpServletRequest req) {                       
        if (text == null || text.length() == 0)
            return this.assembleMsgs(NnStatusCode.SUCCESS, null);
        if (version < 32) {
            return new IosService().search(text);
        }
        
        //matched channels
        //List<NnChannel> channels = NnChannelManager.search(text, false);
        /*
        String userToken = CookieHelper.getCookie(req, CookieHelper.USER);
        boolean guest = NnUserManager.isGuestByToken(userToken);
        short userShard = 1;
        long userId = 1;
        String sphere = LangTable.LANG_EN;
        if (!guest) {
            NnUser u = userMngr.findByToken(userToken);
            if (u != null) {
                userShard = u.getShard();
                userId = u.getId();
                sphere = u.getSphere();
            }
        }
        */
        //List<NnChannel> channels = chMngr.searchBySvi(text, userShard, userId, sphere);
        if (start == null)
            start = "1";
        if (count == null)
            count = "9";
        int startIndex = Integer.parseInt(start);
        int limit = Integer.parseInt(count);
        if (startIndex < 1)
            startIndex = 1;
        if (limit < 0 || limit > 9)
            limit = 9;
        startIndex = startIndex - 1;

        //public static List<NnChannel> search(String queryStr, boolean total, boolean all, int start, int count) {
        List<NnChannel> channels = NnChannelManager.search(text, false, startIndex, limit);
        String[] result = {"", "", "", ""}; //count, curator, curator's channels, channels, suggestion channels
        result[2] = chMngr.composeChannelLineup(channels);
        //matched curators
        List<NnUser> users = userMngr.search(null, null, text);
        int endIndex = (users.size() > 9) ? 9: users.size();
        users = users.subList(0, endIndex);
        result[1] += userMngr.composeCuratorInfo(users, true, false, req);
        
        //if none matched, return suggestion channels
        List<NnChannel> suggestion = new ArrayList<NnChannel>();
        if (channels.size() == 0 && users.size() == 0) {
            suggestion = chMngr.findBillboard(Tag.TRENDING, LangTable.LANG_EN);
            result[3] = chMngr.composeChannelLineup(suggestion);
        }
        
        long channelSize = NnChannelManager.searchSize(text, false);
        
        result[0] = assembleKeyValue("curator", String.valueOf(users.size()) + "\t" + String.valueOf(users.size()));
        result[0] += assembleKeyValue("channel", String.valueOf(channels.size()) + "\t" + String.valueOf(channelSize));
        result[0] += assembleKeyValue("suggestion", String.valueOf(suggestion.size()) + "\t" + String.valueOf(suggestion.size()));
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
        
    }
    
    public String programRemove(String programId, String ytVideoId, String userToken, String secret, String status) {
        if (userToken == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        if (programId == null && ytVideoId == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        if (ytVideoId != null && ytVideoId.length() < 11) {
            log.info("invalid youtube id:" + ytVideoId);
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        }
        
        try {
            NnProgramManager programMngr = new NnProgramManager();
            if (programId != null) {
                NnProgram program = programMngr.findById(Long.parseLong(programId));
                if (program != null) {
                    if (secret != null && secret.equals("chicken")) {
                        program.setStatus(NnProgram.STATUS_ERROR);
                    } else {
                        program.setStatus(NnProgram.STATUS_NEEDS_REVIEWED);
                    }            
                    programMngr.save(program);
                    programMngr.resetCache(program.getChannelId());
                }
            }
            if (ytVideoId != null) {
                List<NnProgram> programs = programMngr.findByYtVideoId(ytVideoId);
                if (programs.size() == 0) {
                    return this.assembleMsgs(NnStatusCode.PROGRAM_INVALID, null);
                }
                for (NnProgram p : programs) {
                    log.info("mark program:" + p.getId() + "(" + ytVideoId + ") status:" + status + " by " + userToken);
                    p.setStatus(Short.parseShort(status));
                    programMngr.save(p);
                }
            }
        } catch (NumberFormatException e) {
            log.info("pass invalid program id:" + programId);
        } catch (NullPointerException e) {
            log.info("program does not exist: " + programId);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);        
    }
    
    public String channelCreate(String token, String name, String intro, String imageUrl, boolean isTemp) {                                 
        //verify input
        if (token == null || token.length() == 0 || name == null || name.length() == 0 ||  imageUrl == null || imageUrl.length() == 0) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        NnUser user = new NnUserManager().findByToken(token);
        if (user == null)
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);        
        NnChannel channel = new NnChannel(name, intro, imageUrl);
        channel.setPublic(false);
        channel.setStatus(NnChannel.STATUS_WAIT_FOR_APPROVAL);
        channel.setContentType(NnChannel.CONTENTTYPE_MIXED); // a channel type in podcast does not allow user to add program in it, so change to mixed type
        channel.setTemp(isTemp);
        chMngr.save(channel);
        String[] result = {chMngr.composeChannelLineupStr(channel)};        
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);        
    }
    
    public String programCreate(String channel, String name, String description, String image, String audio, String video, boolean temp) {
        if (channel == null || channel.length() == 0 || name == null || name.length() == 0 || 
            (audio == null && video == null)) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        long cid = Long.parseLong(channel);        
        NnChannel ch = chMngr.findById(cid);
        if (ch == null)
            return this.assembleMsgs(NnStatusCode.CHANNEL_INVALID, null);
        NnProgramManager programMngr = new NnProgramManager();
        NnProgram program = new NnProgram(ch.getId(), name, description, image);
        program.setChannelId(cid);
        program.setAudioFileUrl(audio);
        program.setFileUrl(video);
        program.setContentType(programMngr.getContentType(program));
        program.setStatus(NnProgram.STATUS_OK);
        program.setPublic(true);
        programMngr.save(program);
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    private short getStatus(String msg) {
        String[] status = msg.split("\t");
        if (status.length > 0)
            return Short.valueOf(status[0]);
        return NnStatusCode.ERROR;
    }

    public String sphereData(String token, String email, String password, HttpServletRequest req, HttpServletResponse resp) {
        List<String> data = new ArrayList<String>();
        return this.assembleSections(data);
    }
    
    public String auxLogin(String token, String email, String password, HttpServletRequest req, HttpServletResponse resp) {
        //1. user info
        List<String> data = new ArrayList<String>();
        log.info ("[quickLogin] verify user: " + token);
        String userInfo = "";
        if (token != null) {
            userInfo = this.userTokenVerify(token, req, resp);
        } else if (email != null || password != null) {        
            userInfo = this.login(email, password, req, resp);            
        } else {
            userInfo = this.guestRegister(req, resp);
        }
        if (this.getStatus(userInfo) != NnStatusCode.SUCCESS) {
            return userInfo;
        }        
        String sphere = "en";
        Pattern pattern = Pattern.compile(".*sphere\t((en|zh)).*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(userInfo);
        if (matcher.matches()) {
            sphere = matcher.group(1);
        }        
        data.add(userInfo);
        //2. trending
        log.info ("[quickLogin] trending channels");
        String trending = this.channelStack(Tag.TRENDING, sphere, token, null, false);
        data.add(trending);
        if (this.getStatus(trending) != NnStatusCode.SUCCESS) {
            return this.assembleSections(data);
        }
        //3. hottest
        log.info ("[quickLogin] hot channels");
        String hot = this.channelStack(Tag.HOT, sphere, token, null, false);
        data.add(hot);
        if (this.getStatus(hot) != NnStatusCode.SUCCESS) {
            return this.assembleSections(data);
        }
        return this.assembleSections(data);
    }
    
    /**
     * 1. user info
     * 2. channel lineup (grid)
     * 3. featured curators
     * 4. trending channels
     */

    public String quickLogin(String token, String email, String password, HttpServletRequest req, HttpServletResponse resp) {
        //1. user info
        List<String> data = new ArrayList<String>();
        log.info ("[quickLogin] verify user: " + token);
        String userInfo = "";
        if (token != null) {
            userInfo = this.userTokenVerify(token, req, resp);
        } else if (email != null || password != null) {        
            userInfo = this.login(email, password, req, resp);            
        } else {
            userInfo = this.guestRegister(req, resp);
        }
        if (this.getStatus(userInfo) != NnStatusCode.SUCCESS) {
            return userInfo;
        }        
        String sphere = "en";
        Pattern pattern = Pattern.compile(".*sphere\t((en|zh)).*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(userInfo);
        if (matcher.matches()) {
            sphere = matcher.group(1);
        }        
        data.add(userInfo);
        //2. channel lineup
        log.info ("[quickLogin] channel lineup: " + token);
        String lineup = this.channelLineup(token, null, null, false, null, true, false, false, req);
        data.add(lineup);
        if (this.getStatus(lineup) != NnStatusCode.SUCCESS) {
            return this.assembleSections(data);
        }
        //3. featured curators
        log.info ("[quickLogin] featured curators");
        String curatorInfo = this.curator(null, null, "featured", req);
        data.add(curatorInfo);
        //4. trending
        log.info ("[quickLogin] trending channels");
        String trending = this.channelStack(Tag.TRENDING, sphere, token, null, false);
        data.add(trending);
        if (this.getStatus(trending) != NnStatusCode.SUCCESS) {
            return this.assembleSections(data);
        }
        //5. recommended
        log.info ("[quickLogin] recommended channels");
        String recommended = this.channelStack(Tag.RECOMMEND, sphere, token, null, false);        
        data.add(recommended);
        if (this.getStatus(recommended) != NnStatusCode.SUCCESS) {
            return this.assembleSections(data);
        }
        //6. featured
        log.info ("[quickLogin] featured channels");
        String featured = this.channelStack(Tag.FEATURED, sphere, token, null, false);
        data.add(featured);
        if (this.getStatus(featured) != NnStatusCode.SUCCESS) {
            return this.assembleSections(data);
        }
        //7. hottest
        log.info ("[quickLogin] hot channels");
        String hot = this.channelStack(Tag.HOT, sphere, token, null, false);
        data.add(hot);
        if (this.getStatus(hot) != NnStatusCode.SUCCESS) {
            return this.assembleSections(data);
        }
        //8. category top level
        // log.info ("[quickLogin] top level categories: " + ((sphere == null) ? "default" : sphere));
        // hardcoding to English for now, and keeping translations on the player side
        log.info ("[quickLogin] top level categories: " + sphere);
        String categoryTop = this.category (null, sphere, false);
        data.add(categoryTop);
        return this.assembleSections(data);
    }

    private String assembleSections(List<String> data) {
        String output = "";
        for (String d : data) {
            output += d + "----\n";
        }
        return output;                
    }

    public String favorite(String userToken, String channel, String program, String fileUrl, String name, String imageUrl, String duration, boolean delete) {
        if (userToken == null || (program == null && fileUrl == null) || channel == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        String[] result = {""};                

        NnUser u = userMngr.findByToken(userToken);
        if (u == null)
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);        
        long pid = 0;
        NnChannel c = chMngr.findById(Long.parseLong(channel));
        if (c == null)
            return this.assembleMsgs(NnStatusCode.CHANNEL_INVALID, null);
        NnProgram p = null;
        NnEpisode e = null;
        if (program != null) {
            if (program.contains("e")) {
                program = program.replace("e", "");
                e = new NnEpisodeManager().findById(Long.parseLong(program));
                if (e == null)
                    return this.assembleMsgs(NnStatusCode.PROGRAM_INVALID, null);
            } else {
                pid = Long.parseLong(program);
                p = new NnProgramManager().findById(Long.parseLong(program));
                if (p == null)
                    return this.assembleMsgs(NnStatusCode.PROGRAM_INVALID, null);
            }
        }
        //delete pid should not contain "e"
        if (delete) {
            chMngr.deleteFavorite(u, pid);
        } else {
            chMngr.saveFavorite(u, c, e, p, fileUrl, name, imageUrl, duration);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);

    }
        
    public NnChannel getChannel(String channel) {
        NnChannel c = chMngr.findById(Long.parseLong(channel));
        return c;
    }
    
    public String graphSearch(String email, String name) {
        if (email == null && name == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        NnUserManager userMngr = new NnUserManager();
        List<NnUser> users = userMngr.search(email, name, null);
        String[] result = {""};
        for (NnUser u : users) {
            result[0] += u.getUserEmail() + "\t" + u.getName() + "\n";
        }        
        return this.assembleMsgs(NnStatusCode.SUCCESS, result); 
    }

    public String userInvite(String token, String toEmail, String toName, String channel, HttpServletRequest req) {
        if (token == null || toEmail == null)
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        NnUserManager userMngr = new NnUserManager();
        NnUser user = userMngr.findByToken(token);
        if (user == null) {
            return this.assembleMsgs(NnStatusCode.ACCOUNT_INVALID, null);
        }
        NnChannel c = chMngr.findById(Long.parseLong(channel));
        if (c == null) {
            return this.assembleMsgs(NnStatusCode.CHANNEL_INVALID, null);
        }
        EmailService service = new EmailService();
        UserInviteDao dao = new UserInviteDao();
        UserInvite invite = dao.findInitiate(user.getId(), user.getShard(), toEmail, Long.parseLong(channel));
        if (invite != null) {
            log.info("old invite:" + invite.getId());
            return this.assembleMsgs(NnStatusCode.SUCCESS, new String[] {invite.getInviteToken()});
        }
        String inviteToken = UserInvite.generateToken();        
        invite = new UserInvite(user.getShard(), user.getId(), 
                                           inviteToken, c.getId(), toEmail, toName);
        
        invite = new UserInviteDao().save(invite);
        String content = UserInvite.getInviteContent(user, invite.getInviteToken(), toName, user.getName(), req); 
        NnEmail mail = new NnEmail(toEmail, toName, NnEmail.SEND_EMAIL_SHARE,                                   
                                   user.getName(), user.getUserEmail(), UserInvite.getInviteSubject(), 
                                   content);
        log.info("email content:" + UserInvite.getInviteContent(user, invite.getInviteToken(), toName, user.getName(), req));
        service.sendEmail(mail, null, null);
        String[] result = {invite.getInviteToken()};
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);        
    }

    public String inviteStatus(String inviteToken) {
        UserInvite invite = new UserInviteDao().findByToken(inviteToken);
        if (invite == null)
            return this.assembleMsgs(NnStatusCode.INVITE_INVALID, null);
        String[] result = {String.valueOf(invite.getStatus())};        
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);        
    }
    
    public String disconnect(String userToken, String email, String channel, HttpServletRequest req) {
        if (userToken == null || email == null || channel == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        NnUser user = userMngr.findByToken(userToken);
        if (user == null) {
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        }
        NnUser invitee = userMngr.findByEmail(email, req);
        if (invitee == null) {
            log.info("invitee does not exist:" + email);
            return this.assembleMsgs(NnStatusCode.SUCCESS, null);
        }
        UserInviteDao dao = new UserInviteDao();
        List<UserInvite> invites = new UserInviteDao().findSubscribers(user.getId(), invitee.getId(), Long.parseLong(channel));
        if (invites.size() == 0) {
            log.info("invite not exist: user id:" + user.getId() + ";invitee id:" + invitee.getId());
            return this.assembleMsgs(NnStatusCode.SUCCESS, null);
        }
        for (UserInvite u : invites) {
            u.setInviteeId(0);
            dao.save(u);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    @SuppressWarnings({ "rawtypes" })
    public String notifySubscriber(String userToken, String channel, HttpServletRequest req) {
        NnUser user = userMngr.findByToken(userToken);            
        if (user == null) {
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        }
        NnChannelManager chMngr = new NnChannelManager();
        NnUserManager userMngr = new NnUserManager();
        Map<String, NnUser> umap = new HashMap<String, NnUser>();        
        Map<String, String> cmap = new HashMap<String, String>();
        String[] cid = channel.split(",");
                
        for (String id : cid) {
            NnChannel c = chMngr.findById(Long.parseLong(id));
            if (c == null) {
                return this.assembleMsgs(NnStatusCode.CHANNEL_INVALID, null);
            }
            List<UserInvite> invites = new UserInviteDao().findSubscribers(user.getId(), user.getShard(), Long.parseLong(id));
            log.info("invite size with cid " + id + ":" + invites.size());
            for (UserInvite invite : invites) {    
                NnUser u = userMngr.findById(invite.getInviteeId(), invite.getShard());
                if (u != null) {
                    String content = "";
                    if (umap.containsKey(u.getUserEmail())) {
                        content = (String)cmap.get(u.getUserEmail()) + ";" + c.getName();
                        cmap.put(u.getUserEmail(), content);
                    } else {
                        umap.put(u.getUserEmail(), u);
                        cmap.put(u.getUserEmail(), c.getName());
                    }                    
                }
            }
        }
        Set set = umap.entrySet(); 
        Iterator i = set.iterator(); 
        while(i.hasNext()) { 
            Map.Entry me = (Map.Entry)i.next();
            NnUser u = (NnUser) me.getValue();
            String ch = (String) cmap.get(u.getUserEmail());
            String subject = UserInvite.getNotifySubject(ch);
            String content = UserInvite.getNotifyContent(ch);
            log.info("send to " + u.getUserEmail());
            log.info("subject:" + subject);
            log.info("content:" + content);
            NnEmail mail = new NnEmail(u.getUserEmail(), u.getName(), NnEmail.SEND_EMAIL_SHARE, user.getName(), user.getUserEmail(), subject, content);        
            new EmailService().sendEmail(mail, null, null);                        
        }         
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String curator(String profile, String userToken, String stack, HttpServletRequest req) {
        if (stack == null && profile == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        
        List<NnUser> users = new ArrayList<NnUser>();
        if (profile != null) {
            NnUser user = userMngr.findByProfileUrl(profile);
            if (user == null)
                return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
            users.add(user);
        } else {
            users = userMngr.findFeatured();
        }
        NnUser user = null;
        if (userToken != null) {
            user = userMngr.findByToken(userToken);            
        }
        
        String[] result = {"", ""};
        boolean isAllChannel = false;
        if (profile != null && user != null && users.size() > 0 && users.get(0).getToken().equals(user.getToken())) {
            log.info("find curator channels");
            isAllChannel = true;
        } else {
            log.info("find channels curator created for public");
        }
         
        if (stack != null)
            result[0] = userMngr.composeCuratorInfo(users, true, isAllChannel, req);
        else
            result[0] = userMngr.composeCuratorInfo(users, false, isAllChannel, req);
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
        
    public String virtualChannel(String stack, String lang, String userToken, String channel, boolean isPrograms) {
        //check input
        if (stack == null && userToken == null && channel == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        //get channels
        List<NnChannel> channels = new ArrayList<NnChannel>();
        NnUser user = null;
        boolean chPos = false;
        if (userToken != null) {
            user = userMngr.findByToken(userToken);
            if (user == null) {
                NnGuest guest = new NnGuestManager().findByToken(userToken);
                if (guest == null)
                    return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
                else
                    return this.assembleMsgs(NnStatusCode.SUCCESS, null);
            }
            NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
            channels.addAll(subMngr.findSubscribedChannels(user));
            chPos = true;
            log.info("virtual channel find by subscriptions:" + user.getId());
        } else if (stack != null) {
            lang = this.checkLang(lang);
            log.info("virtual channel find by stack:" + stack + ";lang=" + lang);
            String[] cond = stack.split(",");
            for (String s : cond) {
                if (s.equals(Tag.RECOMMEND)) {
                    channels.addAll(new RecommendService().findRecommend(userToken, lang));
                } else if (s.equals("mayLike")) {                
                    return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
                } else {
                    channels.addAll(chMngr.findStack(s, lang));
                }
            }
        } else if (channel != null) {
            log.info("virtual channel find by channel ids:" + channel);
            String[] chArr = channel.split(",");
            if (chArr.length > 0) {
                List<Long> list = new ArrayList<Long>();
                for (int i=0; i<chArr.length; i++) { list.add(Long.valueOf(chArr[i]));}                
                channels.addAll(chMngr.findByIds(list));
            }
        }
        if (!isPrograms) {
            String channelInfo = chMngr.composeReducedChannelLineup(channels);
            if (chPos)
                channelInfo = this.chAdjust(channels, channelInfo);
            log.info("virtual channel, return channel only");
            return this.assembleMsgs(NnStatusCode.SUCCESS, new String[] {channelInfo});
        }    
        //get programs
        List<String> result = new ArrayList<String>();        
        List<YtProgram> ytprograms = new YtProgramDao().findByChannels(channels);
        String programInfo = "";
        for (YtProgram p : ytprograms) {
            String[] ori = {
                    String.valueOf(p.getChannelId()),
                    p.getYtVideoId(),
                    p.getPlayerName(),
                    String.valueOf(p.getUpdateDate().getTime()),
                    p.getDuration(),
                    p.getImageUrl(),
                    p.getPlayerIntro(),
            };
            String output = NnStringUtil.getDelimitedStr(ori);
            output = output.replaceAll("null", "");
            output += "\n";
            programInfo += output;
        }
        String channelInfo = chMngr.composeReducedChannelLineup(channels);
        if (chPos) {
            log.info("adjust sequence of channellineup for user:" + user.getId());
            channelInfo = this.chAdjust(channels, channelInfo);
        }
        result.add(channelInfo);                
        result.add(programInfo);
        String size[] = new String[result.size()];
        return this.assembleMsgs(NnStatusCode.SUCCESS, result.toArray(size));        
    }

    private String chAdjust(List<NnChannel> channels, String channelInfo) {
        String adjust = "";            
        String[] lines = channelInfo.split("\n");
        if (channels.size() > 0) {
            for (int i=0; i<lines.length; i++) {                   
                lines[i] = lines[i].replaceAll("^\\d+\\t", channels.get(i).getSeq() + "\t");
                log.info("ch id:" + channels.get(i).getId() + "; seq = " + channels.get(i).getSeq());
                adjust += lines[i] + "\n";
            }
        }
        return adjust;
        
    }
    
    public String frontpage(String time, String stack, String user) {
        DashboardDao dao = new DashboardDao();
        short baseTime = Short.valueOf(time);
        List<Dashboard> baseList = dao.findFrontpage(baseTime);
        //section 1: items
        log.info("board size:" + baseList.size());
        List<String> data = new ArrayList<String>();
        String[] itemOutput = {""};
        List<Dashboard> openList = new ArrayList<Dashboard>(); //things need to call virtualChannel

        //for on previously
        Dashboard daypart = null;
        for(Dashboard d : baseList) {
            if (baseTime < d.getTimeEnd() && baseTime >= d.getTimeStart()) {
                daypart = d;
                log.info("land on :" + d.getName());
                break;
            }
        }
        for (Dashboard d : baseList) {
            if (d.getOpened() == 1)
                openList.add(d);
            String stackname = d.getStackName();
            if (d.getAttr() == -1 && daypart != null) {
                Dashboard previous = dao.findById(daypart.getAttr());
                if (previous != null)
                    stackname = previous.getStackName();
                log.info("previous on name:" + stackname);
            }
            if (d.getType() == Dashboard.TYPE_STACK && d.getStackName() == null)
                stackname = "recommend";            
            String[] ori = {
               d.getName(),
               String.valueOf(d.getType()),
               stackname,
               String.valueOf(d.getOpened()),
               String.valueOf(d.getIcon()),
            };
            itemOutput[0] += NnStringUtil.getDelimitedStr(ori) + "\n";            
        }
        itemOutput[0] = itemOutput[0].replaceAll("null", "");
        itemOutput[0] = this.assembleMsgs(NnStatusCode.SUCCESS, itemOutput);
        data.add(itemOutput[0]);
        try {
            //section 2: virtual channels
            String virtualOutput = "";
            for (Dashboard d : openList) {
                virtualOutput = this.virtualChannel(d.getStackName(), LangTable.LANG_EN, user, null, false);
            }
            data.add(virtualOutput);
            return this.assembleSections(data);
        } catch (Exception e) {
            NnLogUtil.logException((Exception) e);
            return this.assembleSections(data);
        }
    }

    public String virtualChannelAdd(String user, String channel, String payload, 
                                    boolean isQueued, HttpServletRequest req) {        
        if (user == null || payload == null || channel == null) {
            log.info("data is missing");
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }

        if (isQueued) {        
            log.info("virtual channel from user:" + user + " throwing to queue" );
            String url = "/playerAPI/virtualChannelAdd?queued=false";
            String data = "channel=" + channel + "&payload=" + payload + ";&user=" + user;             
            QueueFactory.add(url, QueueFactory.METHOD_POST, QueueFactory.CONTENTTYPE_TEXT, data);
            return this.assembleMsgs(NnStatusCode.SUCCESS, null);
        }
        log.info("--- process virtual channels ---");
        YtProgramDao dao = new YtProgramDao();        
        String[] lines = payload.split("\n");
        log.info("lines:" + lines.length);
        List<YtProgram> ytprograms = new ArrayList<YtProgram>();        
        for (String line : lines) {
            String[] tabs = line.split("\t");
            log.info("columns:" + tabs.length);
            if (tabs.length >= 9) {
                try {
                    String chstr = tabs[0];
                    String ytUserName = tabs[1];
                    String crawlD = tabs[2];
                    String ytVideoId = tabs[3];
                    String name = tabs[4];
                    String updateD = tabs[5];
                    String duration = tabs[6];
                    String imageUrl = tabs[7];
                    String intro = tabs[8];
                    YtProgram program = dao.findByVideo(ytVideoId);
                    if (program == null) {
                       long chId = Long.parseLong(chstr);
                       Date updateDate = null;
                       long epoch = 0;
                       if (updateD != null) {
                          epoch = Long.parseLong(updateD);
                          updateDate= new Date (epoch*1000);
                       }
                       Date crawlDate = null;
                       if (crawlD != null) {
                          epoch = Long.parseLong(crawlD);
                          crawlDate = new Date (epoch*1000);
                       }
                       YtProgram ytprogram = new YtProgram(chId, ytUserName, ytVideoId, 
                                                           name, duration, imageUrl, 
                                                           intro, crawlDate, updateDate);
                       log.info("ytprogram:" + chId + ";" + ytUserName + ";" + ytVideoId + ";" +
                                ";" + name + ";" + duration + ";" + imageUrl + ";" + intro + ";" + 
                                crawlDate + ";" + updateDate);
                       ytprograms.add(ytprogram);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }        
        dao.saveAll(ytprograms);
        log.info("new ytprograms size:" + ytprograms.size());
        int existedSize = lines.length - ytprograms.size();
        log.info("existed ytprograms size:" + existedSize);
        NnChannel c = chMngr.findById(Long.parseLong(channel));
        if (c != null) {
            if (c.getStatus() == NnChannel.STATUS_PROCESSING) {
                log.info("change channel status from processing to success:" + c.getId());
                c.setStatus(NnChannel.STATUS_SUCCESS);
                chMngr.save(c);
            }
        }
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }
    
    public String bulkSubscribe(String userToken, String ytUsers) {
        //input
        if (userToken == null || ytUsers == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        //user
        NnUser user = userMngr.findByToken(userToken);
        if (user == null) 
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        //channels
        List<NnChannel> channels = new ArrayList<NnChannel>();
        String[] ytUser = ytUsers.split(",");        
        for (String yt : ytUser) {
            yt = "http://www.youtube.com/user/" + yt;
            NnChannel existed = chMngr.findBySourceUrl(yt);
            if (existed == null) {
                NnChannel c = chMngr.createYoutubeChannel(yt);
                if (c != null) channels.add(c);
            } else {
                channels.add(existed);
            }            
        }
        String channelInfo = chMngr.composeReducedChannelLineup(channels);        
        //subscribe
        NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
        List<NnUserSubscribe> list = subMngr.findAllByUser(user);
        Map<Long, NnUserSubscribe> map = new HashMap<Long, NnUserSubscribe>();        
        for (NnUserSubscribe s : list) {
            map.put(s.getChannelId(), s);
        }
        for (NnChannel c : channels) {
            if (!map.containsKey(c.getId())) {         
                log.info("user automate subscribe:" + user.getToken() + ";" + c.getId());
                subMngr.subscribeChannel(user, c.getId(), (short)0, MsoIpg.TYPE_GENERAL);
            }
        }
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, new String[] {channelInfo});        
    }    
}
