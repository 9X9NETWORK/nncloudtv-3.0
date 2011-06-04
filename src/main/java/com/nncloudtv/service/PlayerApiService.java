package com.nncloudtv.service;

import java.io.IOException;
import java.util.ArrayList;
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
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.model.Subscription;
import com.nncloudtv.model.SubscriptionSet;
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
	private static String INT = "INT";
		
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public void setMso(Mso mso) {
		this.mso = mso;
	}		

	public String handleException (Exception e) {
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
	
	private String assembleMsgs(int status, String[] raw) {
		String result = NnStatusMsg.getMsg(status, locale);
		String separatorStr = "--\n";
		if (raw != null && raw.length > 0) {
			result = result + separatorStr;
			for (String s : raw) {
				result += s + separatorStr;
			}
		}
		//!!!! can be removed?
		if (result.substring(result.length()-3, result.length()).equals(separatorStr)) {
			result = result.substring(0, result.length()-3);
		}
		 		
		return result;
	}
	
	private int validateInputs(List<String[]> inputs) {
		for (int i=0; i<inputs.size(); i++) {
			String[] input = inputs.get(i);
			String value = input[0];
			String type = input[1];
			if (value == null || value.length() < 0 || value.equals("undefined")) {
				return NnStatusCode.INPUT_MISSING;
			}
			if (type != null && type.equals(INT)) {
				return BasicValidator.validateNumber(type);
			}
		}
		return NnStatusCode.SUCCESS;
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
		int status = NnUserValidator.validate(email, password, name, mso);
		if (status != NnStatusCode.SUCCESS) return this.assembleMsgs(status, null);
		
		boolean convertFromGuest = true;
		NnUser user = null;
		//create user
		if (userToken != null) { user = userMngr.findByTokenAndMso(userToken, mso);}
		if (user == null ) {
			convertFromGuest = false;
			log.info("User signup userToken NOT FOUND. Token=" + userToken);
			user = new NnUser(email, password, name, NnUser.TYPE_USER, mso.getId());
			userMngr.create(user, mso);			
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
		if (convertFromGuest) { //!!!!
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
		
		NnUser found = userMngr.findByTokenAndMso(token, mso);			
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
		
		String[] result = {""};
		result[0] += this.assembleKeyValue("key", String.valueOf(mso.getId()));
		result[0] += this.assembleKeyValue("name", mso.getName());
		result[0] += this.assembleKeyValue("title", mso.getTitle());		
		result[0] += this.assembleKeyValue("logoUrl", mso.getLogoUrl());
		result[0] += this.assembleKeyValue("jingleUrl", mso.getJingleUrl());
		result[0] += this.assembleKeyValue("preferredLangCode", mso.getPreferredLangCode());
		result[0] += this.assembleKeyValue("jingleUrl", mso.getJingleUrl());
		result[0] += this.assembleKeyValue("brandInfoCounter", String.valueOf(counter));
		result[0] += this.assembleKeyValue("debug", config.getValue());
		
		return this.assembleMsgs(NnStatusCode.SUCCESS, result);
	}

	public String findCategoriesByMso() {
		CategoryManager categoryMngr = new CategoryManager();	
		List<Category> categories = categoryMngr.findAllByMsoId(mso.getId());
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
		NnUser user = userMngr.findByTokenAndMso(userToken, mso);
		if (user == null) {return this.assembleMsgs(NnStatusCode.USER_INVALID, null);}
		
		//pdr process		
		new PdrRawManager().processPdr(pdr, user.getId(), session);
		return this.assembleMsgs(NnStatusCode.SUCCESS, null);
	}

	public String findSetInfo(String id, String beautifulUrl) {
		if (id == null && beautifulUrl == null) {
			return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
		}		
		if (id != null && !Pattern.matches("^\\d*$", id)) {
			return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null); 
		}
		
		NnSetManager csMngr = new NnSetManager();
		NnSet set = null;
		if (id != null) {
			set = csMngr.findById(Long.parseLong(id));
		} else {
			set = csMngr.findBybeautifulUrl(beautifulUrl);
		}
		if (set == null) {
			return this.assembleMsgs(NnStatusCode.SET_INVALID, null);			
		}
		
		List<NnChannel> channels = csMngr.findChannelsById(set.getId());
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
	
	private NnChannel customizeChannelOutput(NnChannel c) {
		String intro = c.getIntro();
		if (intro != null) {
			int introLenth = (intro.length() > 256 ? 256 : intro.length()); 
			intro = intro.substring(0, introLenth);
		} else {
			intro = "";
		}
		if (c.getStatus() == NnChannel.STATUS_ERROR)
			c.setImageUrl("/WEB-INF/../images/error.png");
		if (c.getStatus() == NnChannel.STATUS_PROCESSING) {
			if (mso.getPreferredLangCode().equals(Mso.LANG_ZH_TW))
				c.setImageUrl("/WEB-INF/../images/processing_cn.png");
		}
		if (c.getContentType() == NnChannel.CONTENTTYPE_PODCAST || c.getContentType() == NnChannel.CONTENTTYPE_SYSTEM)  
			c.setSourceUrl("");
		return c;		
	}
	
	private String composeChannelLineupStr(NnChannel c, Mso mso) {
		this.customizeChannelOutput(c);
		String[] ori = {Integer.toString(c.getSeq()), 
					    String.valueOf(c.getId()),
					    c.getName(),
					    c.getIntro(),
					    c.getImageUrl(),
					    String.valueOf(c.getProgramCount()),
					    String.valueOf(c.getType()),
					    String.valueOf(c.getStatus()),
					    String.valueOf(c.getContentType()),
					    c.getSourceUrl()
					    };
		String output = NnStringUtil.getDelimitedStr(ori);
		return output;
	}

	//!!! name changed, good channels
	public String findPublicChannelsByCategory(String categoryId) {		
		//verify input
		List<NnChannel> channels = new ArrayList<NnChannel>();
		if (categoryId == null || categoryId.length() < 1)  
			return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
		if (!Pattern.matches("^\\d*$", categoryId))
			return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
		
		//find public channels by categoryId
		NnChannelManager channelMngr = new NnChannelManager();
		channels = channelMngr.findGoodChannelsByCategoryId(Long.parseLong(categoryId));
		if (channels == null || channels.size() == 0) 
			return this.assembleMsgs(NnStatusCode.SUCCESS, null);
		
		//assemble output
		log.info("find " + channels.size() + " of channels in category, category id:" + categoryId);
		String[] result = {""};
		for (int i=0; i< channels.size(); i++) {	
			String[] ori = {String.valueOf(channels.get(i).getSeq()),
					        String.valueOf(channels.get(i).getId()), 
					        channels.get(i).getName(), 
					        channels.get(i).getImageUrl(), 
					        Integer.toString(channels.get(i).getProgramCount()),
					        String.valueOf(channels.get(i).getSubscriptionCount())};
			result[0] += NnStringUtil.getDelimitedStr(ori);
			result[0] += "\n";
		}
		return this.assembleMsgs(NnStatusCode.SUCCESS, result);		
	}	

	public String unsubscribeChannel(String userToken, String channelId) {
		//verify input
		if (userToken == null || channelId == null || userToken.equals("undefined")) {			
			return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
		}
		if (!Pattern.matches("^\\d*$", channelId)) {
			return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);
		}		
		//verify user
		NnUser user = new NnUserManager().findByTokenAndMso(userToken, mso);
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
	
	public String subscribe(String userToken, String channelId, String setId, String gridId, String pos) {
		//verify input
		if (userToken == null || userToken.equals("undefined")) 
			return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
		if ((setId != null && pos == null) || (setId == null && pos != null)) 
			return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
		if ((channelId != null && gridId == null) || (channelId == null && gridId != null)) 
			return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
				
		NnUser user = new NnUserManager().findByTokenAndMso(userToken, mso);
		if (user == null)
			return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
		
		SubscriptionManager subMngr = new SubscriptionManager();				
		//verify channel and grid
		NnChannel channel = null;
		if (channelId != null) {
			channel = new NnChannelManager().findById(Long.parseLong(channelId));			
			if (channel == null || channel.getStatus() == NnChannel.STATUS_ERROR) {
				return this.assembleMsgs(NnStatusCode.CHANNEL_ERROR, null);
			}
			channel.setSeq(Short.parseShort(gridId));
			channel.setType(MsoIpg.TYPE_GENERAL);
		}
		
		boolean status = false;
		if (setId != null) {			
			//find all channels			
			List<Subscription> list = subMngr.findAllByUser(user);
			NnSetManager setMngr = new NnSetManager();
			long sId = Long.parseLong(setId);
			NnSet set = setMngr.findById(sId);
			List<Long> chList = new ArrayList<Long>();
			List<Short> gridList = new ArrayList<Short>();
			if (set != null) {
				List<NnChannel> channels = setMngr.findChannelsById(set.getId());
				Short[] startArr = {1,4,7,28,31,34,55,58,61};
				Short start = startArr[Integer.parseInt(pos)-1]; 
				for (NnChannel c : channels) {					
					chList.add(c.getId());			
					short grid= 0;
					if (c.getSeq() < 4)
						grid = (short) (c.getSeq() + start - 1);
					if (c.getSeq() > 3 && c.getSeq() < 7)
						grid = (short) (start + 9 + c.getSeq()-4);
					else if (c.getSeq() > 6)
						grid = (short) (start + 18 + c.getSeq()-7);
					for (Subscription l : list) {
						if (l.getSeq() == grid) {
							return this.assembleMsgs(NnStatusCode.SUBSCRIPTION_SET_OCCUPIED, null);							
						}
					}					
					gridList.add(grid);
				}
				SubscriptionSet subSet = new SubscriptionSet();
				subSet.setUserId(user.getId());
				subSet.setSetId(set.getId());
				subSet.setSetName(set.getName());
				subSet.setSetImageUrl(set.getImageUrl());
				subSet.setSeq(Short.parseShort(pos));
				status = subMngr.subscribeSet(user, subSet, channels);
			}
		}
		
		//subscribe
		String result[] = {""};
		if (setId == null) {
			status = subMngr.subscribeChannel(user, channel);
		}
		int nnStatus = NnStatusCode.SUCCESS;
		if (!status) 
			nnStatus = NnStatusCode.ERROR;
		
		String[] str = {String.valueOf(channelId), this.assembleMsgs(nnStatus, null)};
		result[0] += NnStringUtil.getDelimitedStr(str);		
		return this.assembleMsgs(NnStatusCode.SUCCESS, result);		
	}
	
	public String findChannelInfo(String userToken, boolean userInfo, String channelIds, boolean setInfo) {
		//verify input
		if ((userToken == null && userInfo == true) || (userToken == null && channelIds == null) || (userToken == null && setInfo == true)) {
			return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
		}
		NnUser user = null;
		if (userToken != null) {
			user = new NnUserManager().findByTokenAndMso(userToken, mso);
			if (user == null)
				return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
		}
		
		ArrayList<String> result = new ArrayList<String>();
		
		if (userInfo)
			result.add(this.prepareUserInfo(user));
		
		if (setInfo) {
			SubscriptionSetManager subSetMngr = new SubscriptionSetManager();
			List<SubscriptionSet> sets = subSetMngr.findByUser(user);
			String setStr = "";
			for (SubscriptionSet s : sets) {
				String[] obj = {
						String.valueOf(s.getSeq()),
						String.valueOf(s.getId()),
						s.getSetName(),						
						s.getSetImageUrl(),
				};				
				setStr += NnStringUtil.getDelimitedStr(obj);;
				setStr += "\n";
			}
			result.add(setStr);
		}		
		
		List<NnChannel> channels = new ArrayList<NnChannel>();
		if (channelIds == null) {
			//find subscribed channels 
			SubscriptionManager subMngr = new SubscriptionManager();
			channels = subMngr.findSubscribedChannels(user, mso);
		} else {
			//find specific channels
			NnChannelManager channelMngr = new NnChannelManager();
			String[] chArr = channelIds.split(",");
			//!!!! catch number format
			if (chArr.length > 1) {
				List<Long> list = new ArrayList<Long>();
				for (int i=0; i<chArr.length; i++) { list.add(Long.valueOf(chArr[i]));}
				channels = channelMngr.findAllByChannelIds(list);
			} else {
				NnChannel channel = channelMngr.findById(Long.parseLong(channelIds));
				channels.add(channel);
			}			
		}
		String channelInfo = "";
		for (NnChannel c : channels) {
			channelInfo += this.composeChannelLineupStr(c, mso);
			channelInfo += "\n";
		}
		result.add(channelInfo);
		//SEVERE: exception:java.lang.ClassCastException: [Ljava.lang.Object; cannot be cast to [Ljava.lang.String;
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
		if (!Pattern.matches("^\\d*$", grid) || Integer.parseInt(grid) < 0 || Integer.parseInt(grid) > 81) {			
			return this.assembleMsgs(NnStatusCode.INPUT_BAD, null);
		}
		
		//verify user
		NnUser user = userMngr.findByTokenAndMso(userToken, mso);
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
		 		
		channel = this.customizeChannelOutput(channel);
		String channelStr[]= {String.valueOf(channel.getId()),
		  	 	              channel.getName(),
		  	 	              channel.getImageUrl(),
		  	 	              String.valueOf(channel.getContentType()),
		  	 	              channel.getSourceUrl()};
		//!!!
		return this.assembleMsgs(NnStatusCode.SUCCESS, new String[] {NnStringUtil.getDelimitedStr(channelStr) + "\n"});
	}

	public String findAuthenticatedUser(String email, String password, HttpServletRequest req, HttpServletResponse resp) {		
		log.info("login: email=" + email + "; mso=" + mso.getId() + ";password=" + password);
		if (email == null || email.length() == 0 ||  password == null || password.length() == 0) {
			return this.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
		}		
		
		NnUser user = userMngr.findAuthenticatedUser(email, password, mso);
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
			user = userMngr.findByTokenAndMso(userToken, mso);
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
				user = userMngr.findByTokenAndMso(userToken, mso);
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
		if (!Pattern.matches("^\\d*$", channelId)) {
			return this.assembleMsgs(NnStatusCode.INPUT_ERROR, null);
		}
				
		NnUser foundUser = userMngr.findByTokenAndMso(userToken, mso);				
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
		NnUser user = userMngr.findByTokenAndMso(userToken, mso);
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
		NnUser user = userMngr.findByTokenAndMso(userToken, mso);
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

		NnUser user = new NnUserManager().findByTokenAndMso(userToken, mso);
		if (user == null) {
			return this.assembleMsgs(NnStatusCode.USER_INVALID, null);
		}	
		
		SubscriptionSetManager subSetMngr = new SubscriptionSetManager();
		short position = Short.valueOf(pos);
		SubscriptionSet subSet = subSetMngr.findByUserAndSeq(user, Short.valueOf(position));
		if (subSet!= null) {
			subSet.setSetName(name);
			subSetMngr.save(user, subSet);
		} else {
			subSet = new SubscriptionSet();
			subSet.setUserId(user.getId());
			subSet.setSetName(name);				
			subSet.setSeq(position);
			subSetMngr.create(user, subSet);			
		}
		
		return this.assembleMsgs(NnStatusCode.SUCCESS, null);
	}

	
}