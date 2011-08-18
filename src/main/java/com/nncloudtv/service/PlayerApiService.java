package com.nncloudtv.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.QueueMessage;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.Ipg;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.model.Subscription;
import com.nncloudtv.model.SubscriptionGroup;
import com.nncloudtv.validation.BasicValidator;
import com.nncloudtv.validation.NnUserValidator;
import com.nncloudtv.validation.PdrRawValidator;

@Service
public class PlayerApiService {
    
    protected static final Logger log = Logger.getLogger(PlayerApiService.class.getName());    
    
    private NnUserManager userMngr = new NnUserManager();    
    private MsoManager msoMngr = new MsoManager();
    private Locale locale;
    private Mso mso;
        
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public void setMso(Mso mso) {
        this.mso = mso;
    }        

    public String handleException (Exception e) {
    	if (e.getClass().equals(NumberFormatException.class)) {
    		return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);    		
    	}
        String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
        NnLogUtil.logException((Exception) e);
        return output;
    }    

    public int addMsoInfoVisitCounter(String msoName) {
        try {
            new QueueMessage().fanout("localhost",QueueMessage.BRAND_COUNTER, msoName);
        } catch (IOException e) {
            e.printStackTrace();
        }        
        String counterName = msoName + "BrandInfo";
        CounterFactory factory = new CounterFactory();
        ShardedCounter counter = factory.getOrCreateCounter(counterName);            
        return counter.getCount()+1;                                 
    }
        
    //assemble key and value string
    private String assembleKeyValue(String key, String value) {
        return key + "\t" + value + "\n";
    }
    
    /**
     * assemble final output to player 
     * 1. status line in the front
     * 2. raw: for each section needs to be separated by separator string, "--\n"
     */
    private String assembleMsgs(int status, String[] raw) {
        String result = NnStatusMsg.getMsg(status, locale);
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
    
    public String prepareUserInfo(NnUser user) {
        String[] result = {""};
        //basic data
        result[0] += assembleKeyValue("token", user.getToken());
        result[0] += assembleKeyValue("name", user.getName());
        result[0] += assembleKeyValue("lastLogin", String.valueOf(user.getUpdateDate().getTime()));
        //pref data if any
        NnUserPrefManager prefMngr = new NnUserPrefManager();
        List<NnUserPref> list = prefMngr.findByUser(user);        
        for (NnUserPref pref : list) {
            result[0] += this.assembleKeyValue(pref.getItem(), pref.getValue());
        }
        return result[0];
    }

    public void setUserCookie(HttpServletResponse resp, String cookieName, String userId) {        
        CookieHelper.setCookie(resp, cookieName, userId);
    }    

    public String createUser(String email, String password, String name, String userToken, 
                             HttpServletRequest req, HttpServletResponse resp) {
        
        int status = NnUserValidator.validate(email, password, name, req);                        
        if (status != NnStatusCode.SUCCESS) return this.assembleMsgs(status, null);
        
        boolean convertFromGuest = true;
        NnUser user = null;
        //create user
        if (userToken != null) { user = userMngr.findByToken(userToken);}
        if (user == null ) {
            convertFromGuest = false;
            log.info("User signup userToken NOT FOUND. Token=" + userToken);
            user = new NnUser(email, password, name, NnUser.TYPE_USER, mso.getId());
            userMngr.create(user, req, (short)0);
            userMngr.subscibeDefaultChannels(user);
        } else {
            log.info("User signup with guest token=" + userToken + "; email=" + email + "; name=" + name + ";password=" + password);                             
            if (user.getEmail().equals(NnUser.GUEST_EMAIL)) {
                log.info("1st time signup after being a guest");
                user.setEmail(email);
                user.setPassword(password);
                user.setName(name);
                userMngr.save(user);
            } else {
                return this.assembleMsgs(NnStatusCode.USER_TOKEN_TAKEN, null);                
            }
        }            
        String[] result = {this.prepareUserInfo(user)};
        if (convertFromGuest) {
            CookieHelper.deleteCookie(resp, CookieHelper.GUEST);
        }
        this.setUserCookie(resp, CookieHelper.USER, user.getToken());
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);        
    }
    
    public String createGuest(String ipgId, HttpServletRequest req, HttpServletResponse resp) {
        IpgManager ipgMngr = new IpgManager();
        Ipg ipg = null;
        if (ipgId != null) {
            ipg = ipgMngr.findById(Long.decode(ipgId));
            if (ipg == null) 
                return this.assembleMsgs(NnStatusCode.IPG_INVALID, null);
        }
        //create guest
        NnUser guest = userMngr.createGuest(mso, req);
        
        //subscribe default channels
        if (ipg != null) {
            SubscriptionManager sMngr = new SubscriptionManager();
            List<NnChannel> ipgChannels = ipgMngr.findIpgChannels(ipg);
            for (NnChannel c : ipgChannels)
                sMngr.subscribeChannel(guest, c);            
        } else {
            userMngr.subscibeDefaultChannels(guest);
        }

        //prepare cookie and output        
        String[] result = {this.prepareUserInfo(guest)};
        this.setUserCookie(resp, CookieHelper.GUEST, guest.getToken());
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public String findUserByToken(String token, HttpServletRequest req, HttpServletResponse resp) {
        if (token == null) {return NnStatusMsg.getMsg(NnStatusCode.INPUT_MISSING, locale);}
        
        NnUser found = userMngr.findByToken(token);            
        if (found == null) {
            CookieHelper.deleteCookie(resp, CookieHelper.USER);
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        }
        System.out.println("my mso is, " + mso.getName());
        //for backward fix, old guest user does not have "guest" cookie
        if (found.getEmail().equals(NnUser.GUEST_EMAIL)) {
            this.setUserCookie(resp, CookieHelper.GUEST, found.getToken());            
        } else {
            this.setUserCookie(resp, CookieHelper.USER, found.getToken());
        }
        userMngr.save(found); //change last login time (ie updateTime)
        String[] result = {this.prepareUserInfo(found)};
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }

    public String findMsoInfo(HttpServletRequest req) {
        Mso theMso = msoMngr.findMsoViaHttpReq(req);
        if (theMso == null) {return this.assembleMsgs(NnStatusCode.MSO_INVALID, null); }
        int counter = this.addMsoInfoVisitCounter(theMso.getName());
        MsoConfigManager configMngr = new MsoConfigManager();
        MsoConfig config = configMngr.findByMsoIdAndItem(theMso.getId(), MsoConfig.DEBUG);
        MsoConfig fbConfig = configMngr.findByItem(MsoConfig.FBTOKEN);
        
        //general setting
        String[] result = {""};
        result[0] += this.assembleKeyValue("key", String.valueOf(mso.getId()));
        result[0] += this.assembleKeyValue("name", mso.getName());
        result[0] += this.assembleKeyValue("title", mso.getTitle());        
        result[0] += this.assembleKeyValue("logoUrl", mso.getLogoUrl());
        result[0] += this.assembleKeyValue("jingleUrl", mso.getJingleUrl());
        result[0] += this.assembleKeyValue("preferredLangCode", mso.getLangCode());
        result[0] += this.assembleKeyValue("jingleUrl", mso.getJingleUrl());
        result[0] += this.assembleKeyValue("brandInfoCounter", String.valueOf(counter));
        result[0] += this.assembleKeyValue("debug", config.getValue());
        if (fbConfig!=null)
            result[0] += this.assembleKeyValue(MsoConfig.FBTOKEN, fbConfig.getValue());
        
        //set info, for default ipg page display
        //format: set-1 id|type|name
        CategoryManager categoryMngr = new CategoryManager();
        List<Category> categories = categoryMngr.findAllByMsoId(mso.getId());
        int i=1;
        for (Category c : categories) {
            String key = "set-" + i;
            String value = c.getId() + "|" + c.getType() + "|" + c.getName();
            result[0] += this.assembleKeyValue(key, value);            
            i++;
        }
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }

    private String langCheck(String lang) {
        if (lang != null && !lang.equals(LangTable.LANG_EN) && !lang.equals(LangTable.LANG_ZH))
            return null;
        if (lang == null)
            return LangTable.LANG_EN;
        return lang;
    }
    
    public String findCategories(String lang) {
        lang = this.langCheck(lang);
        if (lang == null)
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        
        CategoryManager categoryMngr = new CategoryManager();
        List<Category> categories = categoryMngr.findIpgCategoryByMsoId(mso.getId());
        if (categories.size() < 1) { return this.assembleMsgs(NnStatusCode.CATEGORY_INVALID, null); }
        
        String[] result = {""};
        for (Category c : categories) {
            String[] str = {String.valueOf(c.getId()), c.getName(), String.valueOf(c.getChannelCount())};
            result[0] += NnStringUtil.getDelimitedStr(str) + "\n";            
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }

    public String processPdr(String userToken, String pdr, String session) {
        int status = PdrRawValidator.validate(userToken, pdr);
        if (status != NnStatusCode.SUCCESS) return this.assembleMsgs(status, null);
                
        //verify user
        NnUser user = userMngr.findByToken(userToken);
        if (user == null) {return this.assembleMsgs(NnStatusCode.USER_INVALID, null);}
        
        //pdr process        
        new PdrRawManager().processPdr(pdr, user.getId(), session);
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String findSetInfo(String id, String beautifulUrl) {
        if (id == null && beautifulUrl == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        NnSetManager setMngr = new NnSetManager();
        NnSet set = null;
        if (id != null) {
            set = setMngr.findById(Long.parseLong(id));
        } else {
            set = setMngr.findBybeautifulUrl(beautifulUrl);
        }
        if (set == null)
            return this.assembleMsgs(NnStatusCode.SET_INVALID, null);            
        
        List<NnChannel> channels = setMngr.findChannelsById(set.getId());
        String result[] = {"", "", ""};

        Mso csMso = msoMngr.findById(set.getMsoId());
        //mso info        
        result[0] += assembleKeyValue("name", csMso.getName());
        result[0] += assembleKeyValue("imageUrl", csMso.getLogoUrl()); 
        result[0] += assembleKeyValue("intro", csMso.getIntro());            
        //set info
        result[1] += assembleKeyValue("id", String.valueOf(set.getId()));
        result[1] += assembleKeyValue("name", set.getName());
        result[1] += assembleKeyValue("imageUrl", set.getImageUrl());
        //channel info
        for (NnChannel c : channels) {
            result[2] += this.composeChannelLineupStr(c, csMso) + "\n";                                                    
        }        
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);
    }    
        
    private String composeChannelLineupStr(NnChannel c, Mso mso) {
        String[] ori = {Integer.toString(c.getSeq()), 
                        String.valueOf(c.getId()),
                        c.getPlayerPrefName(),
                        c.getPlayerPrefIntro(),
                        c.getPlayerPrefImageUrl(),
                        String.valueOf(c.getProgramCount()),
                        String.valueOf(c.getType()),
                        String.valueOf(c.getStatus()),
                        String.valueOf(c.getContentType()),
                        c.getPlayerPrefSource(),
                        this.convertEpochToTime(c.getTranscodingUpdateDate(), c.getUpdateDate())
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
    
    public String findChannelsByCategory(String categoryId, String lang) {
      //verify input    	
        if (!BasicValidator.validateRequired(new String[] {categoryId}))
    	    return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);;    	
        lang = this.langCheck(lang);
        if (lang == null)
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        
        //find public channels by categoryId
        List<NnChannel> channels = new ArrayList<NnChannel>();        
        NnChannelManager channelMngr = new NnChannelManager();
        channels = channelMngr.findGoodChannelsByCategoryId(Long.parseLong(categoryId));
        if (channels == null || channels.size() == 0) 
            return this.assembleMsgs(NnStatusCode.SUCCESS, null);
        
        //assemble output
        log.info("find " + channels.size() + " of channels in category, category id:" + categoryId);        
        String[] result = {"", ""};
        result[0] = categoryId + "\n";
        for (NnChannel c : channels) {
            String[] ori = {String.valueOf(c.getSeq()),
                    String.valueOf(c.getId()), 
                    c.getPlayerPrefName(),
                    c.getImageUrl(), 
                    Integer.toString(c.getProgramCount()),
                    String.valueOf(c.getSubscriptionCount()),
                    String.valueOf(c.getContentType()),
                    this.convertEpochToTime(c.getTranscodingUpdateDate(), c.getUpdateDate()),
                    c.getPlayerPrefSource(),
                    c.getPlayerPrefIntro()};
            result[1] += NnStringUtil.getDelimitedStr(ori);
            result[1] += "\n";            
        }
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);        
    }    

    public String unsubscribe(String userToken, String channelId) {
		if (!BasicValidator.validateRequired(new String[] {userToken, channelId}))
		    return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        //verify user
        NnUser user = new NnUserManager().findByToken(userToken);
        if (user == null)
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);        
        //unsubscribe
        SubscriptionManager subMngr = new SubscriptionManager();
        Subscription sub = subMngr.findByUserAndChannelId(user, Long.parseLong(channelId)); 
        if (sub != null && sub.getType() == MsoIpg.TYPE_READONLY) {
            return this.assembleMsgs(NnStatusCode.SUBSCRIPTION_RO_CHANNEL, null);            
        }                    
        subMngr.unsubscribeChannel(user, sub);
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

	//@@@ throw partial work to queue
    public String subscribe(String userToken, String channelId, String itemId, String gridId, String pos) {
    	//verify user
		if (!BasicValidator.validateRequired(new String[] {userToken}))
		    return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        if ((itemId != null && pos == null) || (itemId == null && pos != null)) 
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        if ((channelId != null && gridId == null) || (channelId == null && gridId != null)) 
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        NnUser user = new NnUserManager().findByToken(userToken);
        if (user == null)
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        
        //subscribe channel
        SubscriptionManager subMngr = new SubscriptionManager();                
        NnChannel channel = null;
        if (channelId != null) {
            channel = new NnChannelManager().findById(Long.parseLong(channelId));            
            if (channel == null || channel.getStatus() == NnChannel.STATUS_ERROR) {
                return this.assembleMsgs(NnStatusCode.CHANNEL_ERROR, null);
            }
            channel.setSeq(Short.parseShort(gridId));
            channel.setType(MsoIpg.TYPE_GENERAL);            
            subMngr.subscribeChannel(user, channel);
        }
        //change category type
        if (itemId != null) {            
			CategoryManager categoryMngr = new CategoryManager();
			Category c = categoryMngr.findById(Long.parseLong(itemId));
			if (c == null)
				return this.assembleMsgs(NnStatusCode.CATEGORY_INVALID, null);
			SubscriptionGroupManager sgMngr = new SubscriptionGroupManager();
			SubscriptionGroup subGroup = sgMngr.findByUserAndItemId(user, c.getId());
			if (subGroup == null) {
				subGroup = new SubscriptionGroup();
			}			
			subGroup.setUserId(user.getId());
			subGroup.setItemId(c.getId());
			subGroup.setName(c.getName());
			subGroup.setSeq(Short.parseShort(pos));
			subGroup.setType(c.getType());
			sgMngr.save(user, subGroup);
        }
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String findChannelInfo(String userToken, boolean userInfo, String channelIds, boolean setInfo, boolean isRequired) {
        //verify input    	
        if ((userToken == null && userInfo == true) || 
        	(userToken == null && channelIds == null) || 
        	(userToken == null && setInfo == true)) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        NnUser user = null;
        if (userToken != null) {
            user = new NnUserManager().findByToken(userToken);
            if (user == null)
                return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        }
        
        ArrayList<String> result = new ArrayList<String>();
        //user info
        if (userInfo)
            result.add(this.prepareUserInfo(user));
        //set info
        if (setInfo) {        	
			CategoryManager categoryMngr = new CategoryManager();
			List<Category> categories = categoryMngr.findAllInIpg(mso.getId());
			String[] objs = new String[9];
			int i = 0;
			//in theory it has to have 9, should log something if it is not
			for (Category c : categories) { 
				String[] obj = {
						String.valueOf(i+1),
						String.valueOf(c.getId()),
						c.getName(),						
						"",
						String.valueOf(c.getType())};									
				objs[i] = NnStringUtil.getDelimitedStr(obj);
				i++;
			}
			//overwrite with customized values
            SubscriptionGroupManager subSetMngr = new SubscriptionGroupManager();
            List<SubscriptionGroup> sets = subSetMngr.findByUser(user);
            i=1;
            for (SubscriptionGroup s : sets) {
                String[] obj = {String.valueOf(s.getSeq()),                        
                                String.valueOf(s.getId()),
                                s.getName(),                        
                                s.getImageUrl(),
                };                
                objs[s.getSeq()-1] = NnStringUtil.getDelimitedStr(obj);
            }
            String setStr = "";
            for (i=0;i<objs.length; i++) {
				setStr += objs[i];
				setStr += "\n";
			}        	           
            result.add(setStr);
        }        
        //channel info
        List<NnChannel> channels = new ArrayList<NnChannel>();
        if (channelIds == null) {
            //find subscribed channels 
            SubscriptionManager subMngr = new SubscriptionManager();
            channels = subMngr.findSubscribedChannels(user, mso);
        } else {
            //find specific channels
            NnChannelManager channelMngr = new NnChannelManager();
            String[] chArr = channelIds.split(","); 
            if (chArr.length > 1) {
                List<Long> list = new ArrayList<Long>();
                for (int i=0; i<chArr.length; i++) { list.add(Long.valueOf(chArr[i]));}
                channels = channelMngr.findAllByChannelIds(list);
            } else {
                NnChannel channel = channelMngr.findById(Long.parseLong(channelIds));
                if (channel != null) channels.add(channel);
    			if (isRequired && channels.size() == 0)
    				return this.assembleMsgs(NnStatusCode.CHANNEL_INVALID, null);
            }
        }
        String channelInfo = "";
        for (NnChannel c : channels) {
            channelInfo += this.composeChannelLineupStr(c, mso);
            channelInfo += "\n";
        }
        result.add(channelInfo);
        return this.assembleMsgs(NnStatusCode.SUCCESS, result.toArray(new String[result.size()]));
    }
    
    public String createChannel1(String categoryIds, String userToken, String url, String grid, HttpServletRequest req) {
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findBySourceUrlSearch(url);                                        
        CategoryManager categoryMngr = new CategoryManager();
        List<Category> categories = categoryMngr.findCategoriesByIdStr(categoryIds);        
        categoryMngr.addCategory(channel.getId(), categories);
        Object[] obj = {channel.getId(), categories};
        new QueueMessage().fanout("localhost",QueueMessage.CATEGORY_CREATE, obj);
        return "hello";
    }
    
    public String createChannel(String categoryIds, String userToken, String url, String grid, HttpServletRequest req) {
        //verify input
        if (url == null || url.length() == 0 ||  grid == null || grid.length() == 0 ||
            categoryIds == null || categoryIds.equals("undefined") || categoryIds.length() == 0 ||
            userToken== null || userToken.length() == 0) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        if (Integer.parseInt(grid) < 0 || Integer.parseInt(grid) > 81) {            
            return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        }
        
        //verify user
        NnUser user = userMngr.findByToken(userToken);
        if (user == null)
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);        
        if (user.getEmail().equals(NnUser.GUEST_EMAIL))
            return this.assembleMsgs(NnStatusCode.USER_PERMISSION_ERROR, null);

        url = url.trim();                
        //verify category
        CategoryManager categoryMngr = new CategoryManager();
        List<Category> categories = categoryMngr.findCategoriesByIdStr(categoryIds);
        if (categories.size() == 0)
            return this.assembleMsgs(NnStatusCode.CATEGORY_INVALID, null);
        
        NnChannelManager channelMngr = new NnChannelManager();        
        //verify url, also converge youtube url
        url = channelMngr.verifyUrl(url);
        if (url == null)
            return this.assembleMsgs(NnStatusCode.CHANNEL_URL_INVALID, null);            
        
        //verify channel status for existing channel
        NnChannel channel = channelMngr.findBySourceUrlSearch(url);                                        
        if (channel != null && (channel.getStatus() == NnChannel.STATUS_ERROR)) {
            log.info("channel id and status :" + channel.getId()+ ";" + channel.getStatus());
            this.assembleMsgs(NnStatusCode.CHANNEL_STATUS_ERROR, null);
        }
        
        if (channel != null) {
            //add categories if necessary
            log.info("User submits a duplicate url:" + url);
            categoryMngr.addCategory(channel.getId(), categories);
            Object[] obj = {channel.getId(), categories};
            new QueueMessage().fanout("localhost",QueueMessage.CATEGORY_CREATE, obj);
        } else {
            //create a new channel
            channel = channelMngr.createChannelFromUrl(url, user, categories, req);
        }
        
        //subscribe
        SubscriptionManager subMngr = new SubscriptionManager();
        channel.setSeq(Integer.parseInt(grid));
        channel.setType(MsoIpg.TYPE_GENERAL);
        boolean success = subMngr.subscribeChannel(user, channel);

        if (!success) 
            return this.assembleMsgs(NnStatusCode.SUBSCRIPTION_DUPLICATE_CHANNEL, null);
                 
        String channelStr[]= {String.valueOf(channel.getId()),
                                 channel.getPlayerPrefName(),
                                 channel.getPlayerPrefImageUrl(),
                                 String.valueOf(channel.getContentType()),
                                 channel.getPlayerPrefSource()};
        //!!!
        return this.assembleMsgs(NnStatusCode.SUCCESS, new String[] {NnStringUtil.getDelimitedStr(channelStr) + "\n"});
    }

    public String findAuthenticatedUser(String email, String password, HttpServletRequest req, HttpServletResponse resp) {        
        log.info("login: email=" + email + "; mso=" + mso.getId() + ";password=" + password);
        if (!BasicValidator.validateRequired(new String[] {email, password}))
    	    return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);;    	

        NnUser user = userMngr.findAuthenticatedUser(email, password, req);
        if (user != null) {
            userMngr.save(user); //change last login time (ie updateTime)
            this.setUserCookie(resp, CookieHelper.USER, user.getToken());
            return this.assembleMsgs(NnStatusCode.SUCCESS, new String[] {this.prepareUserInfo(user)});
        }
        return this.assembleMsgs(NnStatusCode.USER_LOGIN_FAILED, null);
    }
    
    public String findProgramInfo(String channelIds, String userToken, String ipgId, boolean userInfo) {
        if (channelIds == null || (channelIds.equals("*") && userToken == null && ipgId == null)) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        NnProgramManager programMngr = new NnProgramManager();        
        String[] chArr = channelIds.split(",");
        List<NnProgram> programs = new ArrayList<NnProgram>();
        NnUser user = null;
        if (channelIds.equals("*") && ipgId != null) {
            IpgManager ipgMngr = new IpgManager();
            Ipg ipg = ipgMngr.findById(Long.parseLong(ipgId));
            if (ipg == null) { return this.assembleMsgs(NnStatusCode.IPG_INVALID, null);}
            programs = ipgMngr.findIpgPrograms(ipg);
            log.info("ipg program count: " + programs.size());
        } else if (channelIds.equals("*")) {
            user = userMngr.findByToken(userToken);
            if (user == null) { return this.assembleMsgs(NnStatusCode.USER_INVALID, null); }
            programs = programMngr.findSubscribedPrograms(user);
        } else if (chArr.length > 1) {
            List<Long> list = new ArrayList<Long>();
            for (int i=0; i<chArr.length; i++) { list.add(Long.valueOf(chArr[i]));}
            programs = programMngr.findGoodProgramsByChannelIds(list);
        } else {
            programs = programMngr.findGoodProgramsByChannelId(Long.parseLong(channelIds));
        }        
                
        MsoConfig config = new MsoConfigManager().findByMsoIdAndItem(mso.getId(), MsoConfig.CDN);
        if (config == null) {
            config = new MsoConfig(mso.getId(), MsoConfig.CDN, MsoConfig.CDN_AMAZON);
            log.severe("mso config does not exist! mso: " + mso.getId());
        }
        String userInfoStr = "";
        if (userInfo) {
            if (user == null && userToken != null) 
                user = userMngr.findByToken(userToken);
                userInfoStr = this.prepareUserInfo(user);
        }
        String programInfoStr = this.composeProgramInfoStr(programs, config);
        //!!!
        if (userInfo) {
            String[] result = {userInfoStr, programInfoStr};
            return this.assembleMsgs(NnStatusCode.SUCCESS, result);
        } else {
            String[] result = {programInfoStr};
            return this.assembleMsgs(NnStatusCode.SUCCESS, result);            
        }
    }
    
    private String composeProgramInfoStr(List<NnProgram> programs, MsoConfig config) {        
        String output = "";
        
        String regexCache = "^(http|https)://(9x9cache.s3.amazonaws.com|s3.amazonaws.com/9x9cache)";
        String regexPod = "^(http|https)://(9x9pod.s3.amazonaws.com|s3.amazonaws.com/9x9pod)";
        String cache = "http://cache.9x9.tv";
        String pod = "http://pod.9x9.tv";
        
        for (NnProgram p : programs) {
            //file urls
            String url1 = p.getMpeg4FileUrl();
            String url2 = p.getWebMFileUrl();
            String url3 = p.getOtherFileUrl();
            String url4 = p.getAudioFileUrl();
            if (url1 == null) {url1 = "";}
            if (url2 == null) {url2 = "";}
            if (url3 == null) {url3 = "";}
            if (url4 == null) {url4 = "";}    
            String imageUrl = p.getImageUrl();
            String imageLargeUrl = p.getImageLargeUrl();
            if (imageUrl == null) {imageUrl = "";}
            if (imageLargeUrl == null) {imageLargeUrl = "";}
            if (config.getValue().equals(MsoConfig.CDN_AKAMAI)) {
                url1 = url1.replaceFirst(regexCache, cache);
                url1 = url1.replaceAll(regexPod, pod);
                url2 = url2.replaceFirst(regexCache, cache);
                url2 = url2.replaceAll(regexPod, pod);
                url3 = url3.replaceFirst(regexCache, cache);
                url3 = url3.replaceAll(regexPod, pod);
                url4 = url4.replaceFirst(regexCache, cache);
                url4 = url4.replaceAll(regexPod, pod);
                imageUrl = imageUrl.replaceFirst(regexCache, cache);
                imageUrl = imageUrl.replaceAll(regexPod, pod);
                imageLargeUrl = imageLargeUrl.replaceFirst(regexCache, cache);
                imageLargeUrl = imageLargeUrl.replaceAll(regexPod, pod);                 
            }
                    
            //intro
            String intro = p.getIntro();            
            if (intro != null) {
                int introLenth = (intro.length() > 256 ? 256 : intro.length()); 
                intro = intro.replaceAll("\\s", " ");                
                intro = intro.substring(0, introLenth);
            }
            
            //the rest
            String[] ori = {String.valueOf(p.getChannelId()), 
                            String.valueOf(p.getId()), 
                            p.getName(), 
                            intro,
                            String.valueOf(p.getType()), 
                            p.getDuration(),
                            imageUrl,
                            imageLargeUrl,
                            url1, 
                            url2, 
                            url3, 
                            url4, 
                            String.valueOf(p.getPubDate().getTime())};
            output = output + NnStringUtil.getDelimitedStr(ori);
            output = output.replaceAll("null", "");
            output = output + "\n";
        }
        return output;        
    }

    public String saveIpg(String userToken, String channelId, String programId) {
        if (userToken == null || userToken.length() == 0 || userToken.equals("undefined") ||
            channelId == null || programId == null || channelId.length() == 0 || programId.length() == 0) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }                
        NnUser foundUser = userMngr.findByToken(userToken);                
        if (foundUser == null) 
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);

        Ipg ipg = new Ipg();
        ipg.setChannelId(Long.parseLong(channelId));
        if (Pattern.matches("^\\d*$", programId)) {
            ipg.setProgramId(Long.parseLong(programId));
        } else {
            ipg.setProgramIdStr(programId);
        }
        ipg.setUserId(foundUser.getId());
        IpgManager ipgMngr = new IpgManager();
        ipgMngr.create(ipg, foundUser);    
        return this.assembleMsgs(NnStatusCode.SUCCESS, new String[] {Long.toString(ipg.getId())+"\n"});                 
    }    

    public String loadIpg(long ipgId) {
        IpgManager ipgMngr = new IpgManager();
        Ipg ipg = ipgMngr.findById(ipgId);        
        if (ipg == null) 
            return this.assembleMsgs(NnStatusCode.IPG_INVALID, null); 
        List<NnChannel> channels = ipgMngr.findIpgChannels(ipg);
        String[] result = {"", ""};

        //second block: episode information
        NnProgramManager programMngr = new NnProgramManager();
        NnProgram program = programMngr.findById(ipg.getProgramId());
        if (program != null) {
            List<NnProgram> programs = new ArrayList<NnProgram>();
            programs.add(program);
            MsoConfig config = new MsoConfigManager().findByMsoIdAndItem(mso.getId(), MsoConfig.CDN);
            result[0] += result[0] + this.composeProgramInfoStr(programs, config);
        } else {
            result[0] += result[0]  + ipg.getChannelId() + "\t" + ipg.getProgramIdStr() + "\n";            
        }
        //third block: channelLineup 
        for (NnChannel c : channels) {
            result[1] += this.composeChannelLineupStr(c, mso);
            result[1] += "\n";            
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, result);

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
        
        SubscriptionManager subMngr = new SubscriptionManager();
        boolean success = subMngr.moveSeq(user, Integer.parseInt(grid1), Integer.parseInt(grid2));

        if (!success) { return this.assembleMsgs(NnStatusCode.SUBSCRIPTION_ERROR, null); }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String markBadProgram(String programId, String userToken) {        
        if (programId == null || userToken == null) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        try {
            NnProgramManager programMngr = new NnProgramManager();
            NnProgram program = programMngr.findById(Long.parseLong(programId));
            program.setStatus(NnProgram.STATUS_ERROR);
            programMngr.save(program);
        } catch (NumberFormatException e) {
            log.info("pass invalid program id:" + programId);
        } catch (NullPointerException e) {
            log.info("program does not exist: " + programId);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }

    public String setUserPref(String userToken, String item, String value) {
        //verify input
        if (userToken == null || userToken.length() == 0 || userToken.equals("undefined") ||
            item == null || value == null || item.length() == 0 || value.length() == 0) {
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }        
        //verify user
        NnUser user = userMngr.findByToken(userToken);
        if (user == null)
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        
        //get preference
        NnUserPrefManager prefMngr = new NnUserPrefManager();
        NnUserPref pref = prefMngr.findByUserAndItem(user, item);
        if (pref != null) {
            pref.setValue(value);
            prefMngr.save(user, pref);
        } else {
            pref = new NnUserPref();
            pref.setValue(value);
            pref.setItem(item);            
            pref.setUserId(user.getId());
            prefMngr.create(user, pref);
        }
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }    

    public String changeSetInfo(String userToken, String name, String pos) {
        //verify input
        if (name == null || pos == null)  {            
            return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        if (!Pattern.matches("^\\d*$", pos) || Integer.parseInt(pos) < 0 || Integer.parseInt(pos) > 9) {            
            return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);
        }

        NnUser user = new NnUserManager().findByToken(userToken);
        if (user == null) {
            return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
        }    
        
        SubscriptionGroupManager subSetMngr = new SubscriptionGroupManager();
        short position = Short.valueOf(pos);
        SubscriptionGroup subSet = subSetMngr.findByUserAndSeq(user, Short.valueOf(position));
        if (subSet!= null) {
            subSet.setName(name);
            subSetMngr.save(user, subSet);
        } else {
            subSet = new SubscriptionGroup();
            subSet.setUserId(user.getId());
            subSet.setName(name);                
            subSet.setSeq(position);
            subSetMngr.create(user, subSet);            
        }
        
        return this.assembleMsgs(NnStatusCode.SUCCESS, null);
    }    
}