package com.nncloudtv.web;

import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnStatusCode;
import com.nncloudtv.service.NnStatusMsg;
import com.nncloudtv.service.PlayerApiService;

/**
 * This is API specification for 9x9 Player. Please note although the document is written in JavaDoc form, it is generic Web Service API via HTTP request-response, no Java necessary.
 * <p>
 * <blockquote>
 * Example:
 * <p>
 * Player Request: <br/>
 * http://qa.9x9.tv/playerAPI/brandInfo?mso=9x9
 * <p>
 * Service response:  <br/>
 * 0	success<br/>
 * --<br/>
 * name		9x9<br/>
 * title	9x9.tv<br/>
 * </blockquote>
 * <p>
 * <b>In this document, method name is used as part of the URL</b>, examples:
 * <p>   
 * <blockquote>
 * http://hostname:port/playerAPI/channelBrowse?category=1<br/>
 * http://hostname:port/playerAPI/brandInfo?mso=9x9<br/>
 * </blockquote>
 * 
 * <p>
 * <b>API categories:</b
 * <p>
 * <blockquote>
 * Brand information: brandInfo
 * <p>
 * Account related: guestRegister, signup, login, userTokenVerify, signout
 * <p>
 * Category listing: categoryBrowse
 * <p>
 * Channel and program listing: channelLineup, programInfo
 * <p>
 * IPG action: moveChannel, channelSubmit, subscribe, unsubscribe
 * <p>
 * IPG snapshot: saveIpg, loadIpg
 * <p>
 * Data collection: pdr, programRemove
 * </blockquote>
 * <p>
 * <b>9x9 Player API always returns a string:</b>
 * <p>
 * First line is status code and status message, separated by tab.<br/>
 * <p>
 * Different sets of data are separated by "--\n".
 * <p>
 * Data representation is \t separated of each field, \n separated of each record.
 * <p>
 * <blockquote>
 * Example 1: login 
 * <p>
 * 0	success  <br/>
 * -- <br/>
 * token	a466D491UaaU245P412a <br/>
 * name	a
 * <p>
 * Example 2: categoryBrowse
 * <p>
 * 0	success  <br/>
 * -- <br/>
 * 1201	Movie	5 <br/>
 * 1203	TV	2 <br/>
 * 1204 Sports 2 <br/>
 * </blockquote>
 * <p>     
 * Please note each api's document omits status code and status message.
 * <p>    
 * <b>Basic API flows:</b>
 * <blockquote>
 * The first step is to call brandInfo to retrieve brand information. It returns brand id, brand logo, and any necessary brand information.
 * <p>
 * The next step depends on the UI requirement. Use categoryBrowse to find category listing based on the brand. 
 * Or get an account first. Use userTokenVerify if there's an existing user token. 
 * If there's no token at hand, either sign up for user as a guest(guestRegister) or ask user to signup(signup).
 * <p>
 * Channel and program listing(channelLineup and programInfo) would be ready after an account is registered.
 * <p>
 * </blockquote>
 * <p>
 * <b>Guideline</b> 
 * <blockquote>
 * If there's any API change in terms of return value, new fields will be added to the end of the line, or present in the next block.
 * <p>
 * Please prepare your player being able to handle it. i.e. existing player should NOT have to modify your code to be able to work with this kind of API change.
 * </blockquote>
 */

@Controller
@RequestMapping("playerAPI")
public class PlayerApiController {
	protected static final Logger log = Logger.getLogger(PlayerApiController.class.getName());
	
	private final PlayerApiService playerApiService;	
	private Locale locale;
	
	@Autowired
	public PlayerApiController(PlayerApiService playerApiService) {
		this.playerApiService= playerApiService;
	}	
	
	private void prepService(HttpServletRequest req) {
		/*
		String userAgent = req.getHeader("user-agent");
		if ((userAgent.indexOf("CFNetwork") > -1) && (userAgent.indexOf("Darwin") > -1))	 {
			playerApiService.setUserAgent(PlayerApiService.PLAYER_IOS);
			log.info("from iOS");
		}
		*/
		MsoManager msoMngr = new MsoManager();
		Mso mso = msoMngr.findMsoViaHttpReq(req);
		Locale locale = Locale.ENGLISH;
		/*
		if (mso.getPreferredLangCode().equals(Mso.LANG_ZH_TW)){
			locale = Locale.TRADITIONAL_CHINESE;
		}
		*/
		playerApiService.setLocale(locale);
		playerApiService.setMso(mso);
		this.locale = locale;
	}
	
	/**
	 * To be ignored  
	 */
	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		NnLogUtil.logException(e);
		return "error/blank";
	}
	
	/**
	 * Register a guest account. A "guest" cookie will be set.
	 * If ipg is provided, guest is automatically subscribed to all the channels in the ipg. 
	 * 
	 * @param ipg ipg identifier, it is optional
	 * @return please reference login
	 */	
	@RequestMapping(value="guestRegister")
	public ResponseEntity<String> guestRegister(@RequestParam(value="ipg", required = false) String ipg, HttpServletRequest req, HttpServletResponse resp) {
		log.info("guest register: (ipg)" + ipg);
		this.prepService(req);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.createGuest(ipg, req, resp); 
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}	
		log.info(output);
		return NnNetUtil.textReturn(output);
	} 

	/**
	 *  User signup.
	 *  
	 *  <p>only POST operation is supported.</p>
	 *  
	 *  @param email email
	 *  @param password password
	 *  @param name display name
	 *  @return please reference login
	 */	
	@RequestMapping(value="signup")
    public ResponseEntity<String> signup(HttpServletRequest req, HttpServletResponse resp) {
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String name = req.getParameter("name");
		String userToken = req.getParameter("user");				
		log.info("signup: email=" + email + ";name=" + name + ";userToken=" + userToken + ";password=" + password);

		this.prepService(req);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.createUser(email, password, name, userToken, req, resp);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		log.info(output);
		return NnNetUtil.textReturn(output);
	}	
	
	/**
	 * Verify user token <br/>
	 * Example: http://host:port/playerAPI/userTokenVerify?token=QQl0l208W2C4F008980F
	 * 
	 * @param token user key 
	 * @return Will delete the user cookie if token is invalid.<br/>
	 * 		   Return info please reference login.
	 */	
	@RequestMapping(value="userTokenVerify")
	public ResponseEntity<String> userTokenVerify(@RequestParam(value="token") String token, HttpServletRequest req, HttpServletResponse resp) {
		log.info("userTokenVerify() : userToken=" + token);		

		this.prepService(req);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.findUserByToken(token, req, resp);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		}
		log.info(output);
		return NnNetUtil.textReturn(output);
	}
	
	/**
	 * "user" cookie will be removed
	 * 
	 * @param user user key identifier 
	 */		
	@RequestMapping(value="signout")
    public ResponseEntity<String> signout(@RequestParam(value="user", required=false) String userKey, HttpServletRequest req, HttpServletResponse resp) {
		this.prepService(req);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			CookieHelper.deleteCookie(resp, CookieHelper.USER);
			output = NnStatusMsg.getMsg(NnStatusCode.SUCCESS, locale);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		log.info(output);
		return NnNetUtil.textReturn(output);
	}	
	
	/**
	 * Get brand information. 
	 * 	
	 * @param mso mso name, optional, server returns default mso 9x9 if omiited
	 * @return <p>Data returns in key and value pair. Key and value is tab separated. Each pair is \n separated.<br/> 
	 * 		   keys include "key", "name", logoUrl", "jingleUrl", "preferredLangCode" "debug"<br/></p>
	 *         <p>Example: <br/>
	 *          0	success <br/>
	 *          --<br/>
	 *          key	1<br/>
	 *          name	9x9<br/>
	 *          title	9x9.tv<br/>
	 *          logoUrl	/WEB-INF/../images/logo_9x9.png<br/>
	 *          jingleUrl	/WEB-INF/../videos/opening.swf<br/>
	 *          logoClickUrl	/<br/>
	 *          preferredLangCode	en<br/>
	 *          debug	1<br/>
	 *         </p>
	 */	
	@RequestMapping(value="brandInfo")
	public ResponseEntity<String> brandInfo(@RequestParam(value="mso", required=false)String brandName, HttpServletRequest req) {
		log.info("brandInfo:" + brandName);
		this.prepService(req);		
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.findMsoInfo(req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		log.info(output);
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Browse categories.
	 *  
	 * @return Categories info. Each category is \n separated.<br/>
	 *         Category info has category id, category name, channel count.<br/>
	 */		
	@RequestMapping(value="categoryBrowse")
	public ResponseEntity<String> categoryBrowse(@RequestParam(value="lang", required=false) String lang,
											     HttpServletRequest req) {
		this.prepService(req);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);		
		try {
			output = playerApiService.findCategories(lang);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Collecting PDR
	 * 
	 * @param user user token
	 * @param session indicates the session when user starts using the player
	 * @param pdr pdr data
	 * 		  <p> Expecting lines(separated by \n) of the following:<br/>  
	 * 		  delta verb info <br/>
	 * 		  Example: delta watched 1 1 2 3 <br/>
	 * 		  Note: first 1 is channel, the rest are program ids. <br/>  
	 * 		  Note: each field is separated by tab.
	 * 		  </p> 
	 */	
	@RequestMapping(value="pdr")
	public ResponseEntity<String> pdr(@RequestParam(value="user", required=false) String userToken,
									  @RequestParam(value="session", required=false) String session,
									  @RequestParam(value="pdr", required=false) String pdr,
									  HttpServletRequest req) {
		this.prepService(req);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		log.info("user=" + userToken + ";session=" + session);
		try {
			output = playerApiService.processPdr(userToken, pdr, session);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Retrieves set information
	 * 
	 * @param set set id
	 * @param landing the name used as part of the URL. query with either set or landing 
	 * @return first block: status <br/>
	 *         second block: brand info, returns in key and value pair. <br/>                     
	 *         third block: set info, returns in key and value pair <br/>
	 *         4th block: channel details. reference "channelLineup". <br/>
	 *         <p>
	 *         Example: <br/>
	 *         0	success<br/>
 	 *         --<br/>
	 *         name	daai<br/>
	 *         imageUrl	http://9x9ui.s3.amazonaws.com/9x9playerV52/images/logo_tzuchi.png<br/>
	 *         intro	daai<br/>
	 *         --<br/>
	 *         name	Daai3x3<br/>
	 *         imageUrl	null<br/>
	 *         --<br/>
	 *         1	396	channel1	channel1 http://podcast.daaitv.org/Daai_TV_Podcast/da_ai_dian_shi/da_ai_dian_shi_files/shapeimage_3.png	3	0	0	2<br/>	
	 *         2	399	channel2	channel2 http://podcast.daaitv.org/Daai_TV_Podcast/jing_si_yu/jing_si_yu_files/shapeimage_4.png	3	0	0	2	<br/>
	 */
	@RequestMapping(value="setInfo")
	public ResponseEntity<String> setInfo(@RequestParam(value="set", required=false) String id,
			                              @RequestParam(value="landing", required=false) String beautifulUrl,
			                              HttpServletRequest req ) {
		log.info("setInfo: id =" + id + ";landing=" + beautifulUrl);
		this.prepService(req);		
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.findSetInfo(id, beautifulUrl);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Browse all the on-air channels by category.
	 * 
	 * @param category category id
	 * @return Category info and channels info. <br/>
	 *  	   First section is category info, follows channels info. Each channel is \n separated.<br/>    
	 *         Category info has category id. <br/>
	 *         Channel info includes channel id, channel name, channel image url, program count, subscription count <br/>
	 *         Example: 	<br/>
	 *         0	success<br/>
	 *         --<br/>
	 *         1174<br/>
	 *         --<br/>
	 *         0	1207	Etsy	http://s3.amazonaws.com/9x9chthumb/a.gif	2	2 <br/>
	 *         0	1217	System	http://s3.amazonaws.com/9x9chthumb/b.gif	1	2 <br/>        
	 */		
	@RequestMapping(value="channelBrowse")
	public ResponseEntity<String> channelBrowse(@RequestParam(value="category", required=false) String categoryIds,
			                                    @RequestParam(value="lang", required=false) String lang,
			                                    HttpServletRequest req) {
			                                    
		this.prepService(req);
		log.info(categoryIds);		
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.findChannelsByCategory(categoryIds, lang);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		}
		return NnNetUtil.textReturn(output);
	}	

	/**
	 * User subscribes a channel on a designated grid location. 
	 * Or, user changes 3x3 categorization. 
	 * 
	 * <p>Example: http://host:port/playerAPI/subscribe?user=QQl0l208W2C4F008980F&channel=51&grid=2</p>
	 * 
	 * @param user user's unique identifier
	 * @param channel channelId
	 * @param set setId
	 * @param grid grid location, from 1 to 81. use with channel
	 * @param pos set location, from 1 to 9. use with set       
	 * @return status code and status message for the first block; <br/>
	 *         second block shows channel id, status code and status message
	 */		
	@RequestMapping(value="subscribe")
	public ResponseEntity<String> subscribe(@RequestParam(value="user", required=false) String userToken, 
			                                @RequestParam(value="channel", required=false) String channelIds,
			                                @RequestParam(value="set", required=false) String setId,
			                                @RequestParam(value="grid", required=false) String gridIds, 
			                                @RequestParam(value="pos", required=false) String pos,
			                                HttpServletRequest req ) {
		log.info("subscribe: userToken=" + userToken+ "; channel=" + channelIds + "; grid=" + gridIds + "; set=" + setId + ";pos=" + pos);
		this.prepService(req);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.subscribe(userToken, channelIds, setId, gridIds, pos);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		log.info(output);
		return NnNetUtil.textReturn(output);
	}
	
	/**
	 * User unsubscribes a channel
	 * 
	 * <p>Example: http://host:port/playerAPI/unsubscribe?user=QQl0l208W2C4F008980F&channel=51</p>
	 * 
	 * @param user user's unique identifier
	 * @param channel channelId
	 * @return status code and status message 
	 */			
	@RequestMapping(value="unsubscribe")
	public ResponseEntity<String> unsubscribe(@RequestParam(required=false, value="user") String userToken, 
								              @RequestParam(required=false, value="channel") String channelId,
								              HttpServletRequest req) {
		this.prepService(req);
		log.info("userToken=" + userToken + "; channel=" + channelId);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.unsubscribe(userToken, channelId);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		log.info(output);
		return NnNetUtil.textReturn(output);
	}	

	/**
	 * Get all of a user's subscriptions. 
	 * 
	 * @param user user's unique identifier
	 * @param userInfo true or false. Whether to return user information as login. If asked, it will be returned after status code.
	 * @param channel channel id, optional, can be one or multiple;  example, channel=1 or channel=1,2,3
	 * @param setInfo true or false. Whether to return set information.
	 * @param required used with channel; 
	 *                 if required is set to true and channel id is not found in the system, error will be returned in the general status. 
	 *                 if required is set to false, and channel id is not found in the system, success will be returned in the general status, only the channel section is empty.   
	 * @return A string of all of the user's subscribed channels' information.
	 *         <p>
	 *         First block: status. Second block: set information. This block will show only when setInfo is true. 
	 *         Third block: channel information. It would be the second block if setInfo is false
	 *         <p>
	 *         Set info has following fields: <br/>
	 *         position, set id, set name, set image url                  
	 *         <p>  
	 *         Channel info has following fields: <br/>
	 *         channel name, channel description, channel image url, <br/>
	 *         program count, type(integer, see following), status(integer, see following),
	 *         contentType(integer, see following), sourceUrl
	 *         </blockquote>
	 *         <p>
	 *         type: TYPE_GENERAL = 1; TYPE_READONLY = 2;
	 *         <br/>
	 *         status: STATUS_SUCCESS = 0; STATUS_ERROR = 1;
	 *         <br/> 
	 *         contentType: SYSTEM_CHANNEL=1; PODCAST=2; YOUTUBE_CHANNEL=3; YOUTUBE_PLAYERLIST=4 FACEBOOK_CHANNEL=5
	 *         <p> 
	 *         Example: <br/>
	 *         0	success<br/>
	 *         --<br/>
	 *         1239   1   Daai3x3   null<br/>
	 *         -- <br/>
	 *         1	1207	Channel1	http://hostname/images/img.jpg	3	1	0	3	http://www.youtube.com/user/android<br/>
	 *         </p>
	 */		
	@RequestMapping(value="channelLineup")
	public ResponseEntity<String> channelLineup(@RequestParam(value="user", required=false) String userToken,
												@RequestParam(value="userInfo", required=false) String userInfo,
												@RequestParam(value="channel", required=false) String channelIds,
												@RequestParam(value="setInfo", required=false) String setInfo,
										        @RequestParam(value="required", required=false) String required,												
											    HttpServletRequest req) {
		this.prepService(req);
		log.info("userToken=" + userToken + ";isUserInfo:" + userInfo);				
		boolean isUserInfo = Boolean.parseBoolean(userInfo);
		boolean isSetInfo = Boolean.parseBoolean(setInfo);
		boolean isRequired = Boolean.parseBoolean(required);		
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.findChannelInfo(userToken, isUserInfo, channelIds, isSetInfo, isRequired);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		}
		return NnNetUtil.textReturn(output);
	}	

	/**
	 * Get program information based on query criteria.
	 * 
	 * <p>
	 * Examples: <br/>
	 *  http://host:port/playerAPI/programInfo?channel=*&user=QQl0l208W2C4F008980F <br/>
	 *  http://host:port/playerAPI/programInfo?channel=*&ipg=13671109 <br/>
	 *  http://host:port/playerAPI/programInfo?channel=153,158 <br/>
	 *  http://host:port/playerAPI/programInfo?channel=153 <br/>
	 * </p> 
	 * @param  channel (1)Could be *, all the programs, e.g. channel=* (user is required for wildcard query). 
	 * 		           (2)Could be a channel Id, e.g. channel=1 <br/>
	 * 		           (3)Could be list of channels, e.g. channels = 34,35,36.
	 * @param  user user's unique identifier, it is required for wildcard query
	 * @param  userInfo true or false. Whether to return user information as login. If asked, it will be returned after status code. 
	 * @param  ipg  ipg's unique identifier, it is required for wildcard query
	 * @return <p>Programs info. Each program is separate by \n.</p>
	 *   	   <p>Program info has: <br/>
	 *            channelId, programId, programName, description(max length=256),<br/>
	 *            programType, duration, <br/>
	 *            programThumbnailUrl, programLargeThumbnailUrl, <br/>
	 *            url1(mpeg4/slideshow), url2(webm), url3(flv more likely), url4(audio), <br/> 
	 *            publish date timestamp</p>
	 */
	@RequestMapping("programInfo")
	public ResponseEntity<String> programInfo(@RequestParam(value="channel", required=false) String channelIds,
									          @RequestParam(value="user", required = false) String userToken,
									          @RequestParam(value="userInfo", required=false) String userInfo,
									          @RequestParam(value="ipg", required = false) String ipgId,
									          HttpServletRequest req) {
		this.prepService(req);		
		log.info("params: channel:" + channelIds + ";user:" + userToken + ";ipg:" + ipgId);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		boolean isUserInfo = Boolean.parseBoolean(userInfo);
		try {
			output =  playerApiService.findProgramInfo(channelIds, userToken, ipgId, isUserInfo);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		}
		return NnNetUtil.textReturn(output);
	}
	
	/**
	 * Generate a channel based on a podcast RSS feed or a YouTube URL.
	 * 
	 * <p>Only POST operation is supported.</p>
	 *  
	 * @param url a podcast RSS feed or a YouTube url or a FB url
	 * @param user user's unique identifier
	 * @param grid grid location, 0 - 81
	 * @param category category id
	 * @param langCode language code, en or zh.
	 * 
	 * @return channel id, channel name, image url. <br/>
	 */	
	@RequestMapping(value="channelSubmit")
	public ResponseEntity<String> channelSubmit(HttpServletRequest req) {
		String url = req.getParameter("url") ;
		String userToken= req.getParameter("user");
		String grid = req.getParameter("grid");
		String categoryIds = req.getParameter("category");

		this.prepService(req);		
		log.info("player input - userToken=" + userToken+ "; url=" + url + ";grid=" + grid + ";categoryId=" + categoryIds);				
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);				
		try {
			output = playerApiService.createChannel(categoryIds, userToken, url, grid, req);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		}
		log.info(output);
		return NnNetUtil.textReturn(output);		
	}

	/**
	 * User login. A "user" cookie will be set.
	 * 
	 * <p>Only POST operation is supported.</p>
	 * 
	 * @param email email
	 * @param password password
	 * 
	 * @return If signup succeeds, the return message will be         
	 *         <p>preference1 key name (tab) preference1 value (\n)<br/>            
	 *            preference2 key name (tab) preference2 value (\n)<br/>
	 *            preferences.....
	 *         </p> 
	 *         <p> Example: <br/>
	 *         0	success <br/>
	 *         --<br/>
	 *         token	QQl0l208W2C4F008980F<br/>
	 *         name	c<br/>
	 *         lastLogin	1300822489194<br/>
	 */	
	@RequestMapping(value="login")
	public ResponseEntity<String> login(HttpServletRequest req, HttpServletResponse resp) {
		String email = req.getParameter("email");
		String password = req.getParameter("password");		
		this.prepService(req);
		log.info("login: email=" + email);		
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);		
		try {
			output = playerApiService.findAuthenticatedUser(email, password, req, resp);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		log.info(output);
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Save User IPG (snapshot)
	 *
	 * @param user user's unique identifier
	 * @param channel channel id
	 * @param program program id
	 * @return A unique IPG identifier
	 */
	@RequestMapping(value="saveIpg")
	public ResponseEntity<String> saveIpg(@RequestParam(value="user", required=false) String userToken, 
			                              @RequestParam(value="channel", required=false) String channelId, 
			                              @RequestParam(value="program", required=false) String programId, 
			                              HttpServletRequest req) {		
		this.prepService(req);
		log.info("saveIpg(" + userToken + ")");
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);		
		try {
			output = playerApiService.saveIpg(userToken, channelId, programId);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		return NnNetUtil.textReturn(output);
	}	
	
	/**
	 * Load User IPG (snapshot)
	 *
	 * @param ipg IPG's unique identifier
	 * @return  Returns a program to play follows ipg information.
	 * 	        The program to play returns in the 2nd section, format please reference programInfo format.
	 *          3rd section is ipg information, format please reference channelLineup.
	 */
	@RequestMapping(value="loadIpg")
	public ResponseEntity<String> loadIpg(@RequestParam(value="ipg") Long ipgId, 
										  HttpServletRequest req) {		
		log.info("ipgId:" + ipgId);		
		this.prepService(req);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);		
		try {
			output = playerApiService.loadIpg(ipgId);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		return NnNetUtil.textReturn(output);						
	}

	/**
	 * Move a channel from grid 1 to grid2
	 * 
	 * @param user user's unique identifier
	 * @param grid1 "from" grid
	 * @param grid2 "to" grid
	 * 
	 * @return status code and status message
	*/
	@RequestMapping(value="moveChannel")
	public ResponseEntity<String> moveChannel(@RequestParam(value="user", required=false) String userToken, 
											  @RequestParam(value="grid1", required=false) String grid1,
											  @RequestParam(value="grid2", required=false) String grid2,
											  HttpServletRequest req){
		this.prepService(req);
		log.info("userToken=" + userToken + ";grid1=" + grid1 + ";grid2=" + grid2);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);		
		try {
			output = playerApiService.moveChannel(userToken, grid1, grid2);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		}	
		log.info(output);
		return NnNetUtil.textReturn(output);		
	}
	
	/**
	 * Mark a program bad when player sees it 
	 * 
	 * @param user user token
	 * @param program programId
	 */	
	@RequestMapping(value="programRemove")
	public ResponseEntity<String> programRemove(@RequestParam(value="program", required=false) String programId,
				                                @RequestParam(value="user", required=false) String userToken,
				                                HttpServletRequest req) {
		this.prepService(req);
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		log.info("bad program:" + programId + ";reported by user:" + userToken);
		try {
			output = playerApiService.markBadProgram(programId, userToken);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		return NnNetUtil.textReturn(output);
	}
	
	/**
	 * Set user preference. Preferences can be retrieved from login, or apis with isUserInfo option. 
	 * 	
	 * @param user user token
	 * @param key preference name
	 * @param value preference value
	 * @return status block
	 */          
	@RequestMapping(value="setUserPref")
	public ResponseEntity<String> setUserPref(@RequestParam(value="user", required=false)String user,
			                               @RequestParam(value="key", required=false)String key,
			                               @RequestParam(value="value", required=false)String value,
			                               HttpServletRequest req) {
		log.info("userPref: key(" + key + ");value(" + value + ")");
		this.prepService(req);		
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.setUserPref(user, key, value);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		log.info(output);
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Change set name
	 * 
	 * @param user user token
	 * @param name set name
	 * @param pos set position, from 1 to 9 
	 * @return status
	*/
	@RequestMapping(value="setSetInfo")
	public ResponseEntity<String> setSetInfo (@RequestParam(value="user", required=false) String userToken,
			                                  @RequestParam(value="name", required=false) String name,
			                                  @RequestParam(value="pos", required=false) String pos,
			                                  HttpServletRequest req) {
		log.info("setInfo: user=" + userToken + ";pos =" + pos);
		this.prepService(req);		
		String output = NnStatusMsg.getMsg(NnStatusCode.ERROR, locale);
		try {
			output = playerApiService.changeSetInfo(userToken, name, pos);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		}
		log.info(output);
		return NnNetUtil.textReturn(output);
	}	
	
	
}