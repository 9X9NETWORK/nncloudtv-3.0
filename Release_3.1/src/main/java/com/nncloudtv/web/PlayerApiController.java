package com.nncloudtv.web;

import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	
	private int prepService(HttpServletRequest req, boolean log) {		
		/*
		String userAgent = req.getHeader("user-agent");
		if ((userAgent.indexOf("CFNetwork") > -1) && (userAgent.indexOf("Darwin") > -1))	 {
			playerApiService.setUserAgent(PlayerApiService.PLAYER_IOS);
			log.info("from iOS");
		}
		*/ 
		if (log)
			NnNetUtil.logUrl(req);
		HttpSession session = req.getSession();
		session.setMaxInactiveInterval(60);
		MsoManager msoMngr = new MsoManager();
		Mso mso = msoMngr.findNNMso();
		Locale locale = Locale.ENGLISH;
		playerApiService.setLocale(locale);
		playerApiService.setMso(mso);
		int status = playerApiService.checkRO();
		this.locale = locale;
		return status;				
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
	public ResponseEntity<String> guestRegister(
			@RequestParam(value="ipg", required = false) String ipg, 
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req, 
			HttpServletResponse resp) {
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);
			output = playerApiService.guestRegister(req, resp);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
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
	 *  @param captcha captcha image file name
	 *  @param text captcha text
	 *  @param sphere zh or en
	 *  @param ui-lang zh or en
	 *  @param year year or birth
	 *  @param temp not specify means false 
	 *  @return please reference login
	 */	
	@RequestMapping(value="signup")
    public ResponseEntity<String> signup(HttpServletRequest req, HttpServletResponse resp) {
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String name = req.getParameter("name");
		String userToken = req.getParameter("user");
		String captcha = req.getParameter("captcha");
		String text = req.getParameter("text");
		String sphere = req.getParameter("sphere");
		String year = req.getParameter("year");
		String lang = req.getParameter("ui-lang");
		String rx = req.getParameter("rx");
		boolean isTemp = Boolean.parseBoolean(req.getParameter("temp"));
				
		log.info("signup: email=" + email + ";name=" + name + 
				 ";userToken=" + userToken + ";sphere=" + sphere + 
				 ";year=" + year + ";ui-lang=" + lang + 
				 ";rx=" + rx);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));		
			output = playerApiService.signup(email, password, name, userToken, captcha, text, sphere, lang, year, isTemp, req, resp);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
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
	public ResponseEntity<String> userTokenVerify(
			@RequestParam(value="token") String token,
			@RequestParam(value="rx", required = false) String rx,			
			HttpServletRequest req, 
			HttpServletResponse resp) {
		log.info("userTokenVerify() : userToken=" + token);		
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);

		try {
			this.prepService(req, true);
			output = playerApiService.userTokenVerify(token, req, resp);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}
	
	/**
	 * "user" cookie will be removed
	 * 
	 * @param user user key identifier 
	 */		
	@RequestMapping(value="signout")
    public ResponseEntity<String> signout(
    		@RequestParam(value="user", required=false) String userKey,
			@RequestParam(value="rx", required = false) String rx,
    		HttpServletRequest req, HttpServletResponse resp) {
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);
			CookieHelper.deleteCookie(resp, CookieHelper.USER);
			CookieHelper.deleteCookie(resp, CookieHelper.GUEST);
			output = NnStatusMsg.getPlayerMsg(NnStatusCode.SUCCESS, locale);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
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
	public ResponseEntity<String> brandInfo(
			@RequestParam(value="mso", required=false)String brandName,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req) {
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);
			output = playerApiService.brandInfo(req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);			
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * For directory query. Depending on the query level, it returns category, set, or channel info.    
	 * API returns list of category info until it reaches category leaf.
	 * API returns list of set info when the query "category" is a set.
	 * API returns list channel info when the query "category" is a set.
	 * 
	 * @param category category id, category id empty indicates top level category query
	 * @param lang en or zh
	 * 
	 * @return <p>Block one, the requested category. Block two, category or set info. Block three, channel info.
	 *            Block can be blank if such info does not exist.       
	 *         <p>Category info includes id, name, channel count, sub-category count         
	 *         <p>Set info includes set id, set name, channel count
	 *         <p>Channel info please refer to channelLineup
	 */
	@RequestMapping(value="category")
	public ResponseEntity<String> category(
			@RequestParam(value="category", required=false) String category,
			@RequestParam(value="lang", required=false) String lang,
			@RequestParam(value="flatten", required=false) String isFlatten,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {				                                
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);
			boolean flatten = Boolean.parseBoolean(isFlatten);
			output = playerApiService.category(category, lang, flatten);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
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
	public ResponseEntity<String> pdr(
			@RequestParam(value="user", required=false) String userToken,
			@RequestParam(value="device", required=false) String deviceToken,
			@RequestParam(value="session", required=false) String session,
			@RequestParam(value="pdr", required=false) String pdr,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("user=" + userToken + ";device=" + deviceToken + ";session=" + session);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, false);
			if (status != NnStatusCode.SUCCESS) {
				return NnNetUtil.textReturn(
						playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			}
			output = playerApiService.pdr(userToken, deviceToken, session, pdr, req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
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
	public ResponseEntity<String> setInfo(
			@RequestParam(value="set", required=false) String id,
			@RequestParam(value="landing", required=false) String beautifulUrl,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("setInfo: id =" + id + ";landing=" + beautifulUrl);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);		
		try {
			this.prepService(req, true);		
			output = playerApiService.setInfo(id, beautifulUrl);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);			
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * User subscribes a channel on a designated grid location.
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
	public ResponseEntity<String> subscribe(
			@RequestParam(value="user", required=false) String userToken, 
			@RequestParam(value="channel", required=false) String channelId,
			@RequestParam(value="set", required=false) String setId,
			@RequestParam(value="grid", required=false) String gridId, 
			@RequestParam(value="pos", required=false) String pos,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("subscribe: userToken=" + userToken+ "; channel=" + channelId + "; grid=" + gridId + "; set=" + setId + ";pos=" + pos);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS) {
				return NnNetUtil.textReturn(
						playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			}
			output = playerApiService.subscribe(userToken, channelId, gridId);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);			
		}
		return NnNetUtil.textReturn(output);
	}
	
	/**
	 * User unsubscribes a channel or a set. 
	 * 
	 * To unsubscribe a channel, use params channel and grid; to unsubscribe a set, use param set.
	 * 
	 * <p>Example: http://host:port/playerAPI/unsubscribe?user=QQl0l208W2C4F008980F&channel=51</p>
	 * 
	 * @param user user's unique identifier
	 * @param channel channelId
	 * @param grid grid location. use with channel.   
	 * giving channel only is valid (for backward compatibility), 
	 * but since one channel can exist on multiple  locations in a smart guide,
	 * it could result in unsubscribing on an unexpected grid location. 
	 * @param set set id.
	 * @return status code and status message
	 */			
	@RequestMapping(value="unsubscribe")
	public ResponseEntity<String> unsubscribe(
			@RequestParam(value="user", required=false) String userToken, 
			@RequestParam(value="channel", required=false) String channelId,
			@RequestParam(value="grid", required=false) String grid,
			@RequestParam(value="set", required=false) String setId,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {			
		log.info("userToken=" + userToken + "; channel=" + channelId + "; set=" + setId + "; seq=" + grid);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS) {
				return NnNetUtil.textReturn(
						playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			}
			output = playerApiService.unsubscribe(userToken, channelId, setId, grid);	
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}	

	/**
	 * Get channel information 
	 * 
	 * @param user user's unique identifier
	 * @param userInfo true or false. Whether to return user information as login does. If asked, it will be returned after status code.
	 * @param channel channel id, optional, can be one or multiple;  example, channel=1 or channel=1,2,3
	 * @param setInfo true or false. Whether to return set information.  
	 * @param required true or false. Will return error in status block if the requested channel is not found.
	 * @return A string of all of requested channel information
	 *         <p>
	 *         First block: status. Second block: set information. This block shows only if setInfo is set to true. 
	 *         Third block: channel information. It would be the second block if setInfo is false
	 *         <p>
	 *         Set info has following fields: <br/>
	 *         position, set id, set name, set image url, set type
	 *         <p>  
	 *         Channel info has following fields: <br/>
	 *         grid position, <br/> 
	 *         channel id, <br/>
	 *         channel name, <br/> 
	 *         channel description, <br/> 
	 *         channel image url, <br/>
	 *         program count, <br/> 
	 *         channel type(integer, see note), <br/> 
	 *         channel status(integer, see note), <br/>
	 *         contentType(integer, see note), <br/> 
	 *         youtube id (for player youtube query), <br/>
	 *         channel/episodes last update time (see note) <br/>
	 *         channel sorting (see note), <br/> 
	 *         piwik id, <br/> 
	 *         last watched episode <br/>
	 *         youtube real channel name <br/>
	 *         subscription count
	 *         </blockquote>
	 *         <p>
	 *         set type: TYPE_USER = 1; TYPE_READONLY = 2;
	 *         <p>
	 *         channel type: TYPE_GENERAL = 1; TYPE_READONLY = 2;
	 *         <p>
	 *         status: STATUS_SUCCESS = 0; STATUS_ERROR = 1;
	 *         <p> 
	 *         contentType: SYSTEM_CHANNEL=1; PODCAST=2; 
	 *                      YOUTUBE_CHANNEL=3; YOUTUBE_PLAYERLIST=4                        
	 *                      FACEBOOK_CHANNEL=5; 
	 *                      MIX_CHANNEL=6; SLIDE=7;
	 *                      MAPLESTAGE_VARIETY=8; MAPLESTAGE_SOAP=9	
	 *         <p>
	 *         channel episodes last update time: it does not always accurate on Youtube channels. It will pass channel create date on FB channels.
	 *         <p>
	 *         sorting: NEWEST_TO_OLDEST=1; SORT_OLDEST_TO_NEWEST=2; SORT_MAPEL=3
	 *         <p> 
	 *         Example: <br/>
	 *         0	success<br/>
	 *         --<br/>
	 *         1239   1   Daai3x3   null<br/>
	 *         -- <br/>
	 *         1	1207	Channel1	http://hostname/images/img.jpg	3	1	0	3	http://www.youtube.com/user/android <br/>
	 *         </p>
	 */		
	@RequestMapping(value="channelLineup")
	public ResponseEntity<String> channelLineup(
			@RequestParam(value="user", required=false) String userToken,
			@RequestParam(value="userInfo", required=false) String userInfo,
			@RequestParam(value="channel", required=false) String channelIds,
			@RequestParam(value="setInfo", required=false) String setInfo,
			@RequestParam(value="required", required=false) String required,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("userToken=" + userToken + ";isUserInfo=" + userInfo + ";channel=" + channelIds + ";setInfo=" + setInfo);				
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);
			boolean isUserInfo = Boolean.parseBoolean(userInfo);
			boolean isSetInfo = Boolean.parseBoolean(setInfo);
			boolean isRequired = Boolean.parseBoolean(required);		
			output = playerApiService.channelLineup(userToken, isUserInfo, channelIds, isSetInfo, isRequired);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
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
	public ResponseEntity<String> programInfo(
			@RequestParam(value="channel", required=false) String channelIds,
			@RequestParam(value="user", required = false) String userToken,
			@RequestParam(value="userInfo", required=false) String userInfo,
			@RequestParam(value="ipg", required = false) String ipgId,
			@RequestParam(value="sidx", required = false) String sidx,
			@RequestParam(value="limit", required = false) String limit,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("params: channel:" + channelIds + ";user:" + userToken + ";ipg:" + ipgId);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);		
			boolean isUserInfo = Boolean.parseBoolean(userInfo);
			output =  playerApiService.programInfo(channelIds, userToken, ipgId, isUserInfo, sidx, limit);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}
	
	/**
	 * Generate a channel based on a podcast RSS feed or a YouTube URL.
	 * 
	 * <p>Only POST operation is supported.</p>
	 *  
	 * @param url YouTube url
	 * @param user user's unique identifier
	 * @param grid grid location, 1 - 81
	 * @param category category id, not mandatory
	 * @param langCode language code, en or zh.
	 * @param tag tag string, separated by comma
	 * 
	 * @return channel id, channel name, image url. <br/>
	 */	
	@RequestMapping(value="channelSubmit")
	public ResponseEntity<String> channelSubmit(HttpServletRequest req) {
		String url = req.getParameter("url") ;
		String userToken= req.getParameter("user");
		String grid = req.getParameter("grid");
		String categoryIds = req.getParameter("category");
		String tags = req.getParameter("tag");
		String lang = req.getParameter("lang");
		String rx = req.getParameter("rx");
		
		log.info("player input - userToken=" + userToken+ "; url=" + url + 
				 ";grid=" + grid + ";categoryId=" + categoryIds +
				 ";rx=" + rx);				
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);		
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS) {
				return NnNetUtil.textReturn(
						playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			}
			output = playerApiService.channelSubmit(categoryIds, userToken, url, grid, tags, lang, req);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
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
		String rx = req.getParameter("rx");
		log.info("login: email=" + email + ";rx=" + rx);		
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);		
		try {
			this.prepService(req, true);
			output = playerApiService.login(email, password, req, resp);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}
		
	/**
	 * Set user preference. Preferences can be retrieved from login, or APIs with isUserInfo option.
	 * Things are not provided in userProfile API should be stored in user preference.  
	 * 	
	 * @param user user token
	 * @param key preference name
	 * @param value preference value
	 * @return status block
	 */          
	@RequestMapping(value="setUserPref")
	public ResponseEntity<String> setUserPref(
			@RequestParam(value="user", required=false)String user,
			@RequestParam(value="key", required=false)String key,
			@RequestParam(value="value", required=false)String value,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("userPref: key(" + key + ");value(" + value + ")");
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS) {
				return NnNetUtil.textReturn(
						playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			}
			output = playerApiService.setUserPref(user, key, value);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Change subscription's set(group) name.
	 * 
	 * @param user user token
	 * @param name set name
	 * @param pos set position, from 1 to 9 
	 * @return status
	*/
	@RequestMapping(value="setSetInfo")
	public ResponseEntity<String> setSetInfo (
			@RequestParam(value="user", required=false) String userToken,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="pos", required=false) String pos,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("setInfo: user=" + userToken + ";pos =" + pos);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS) {
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			}
			output = playerApiService.setSetInfo(userToken, name, pos);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}	

	/**
	 * Static content for help or general section
	 * 
	 * @param key key name to retrieve the content
	 * @param lang en or zh
	 * @return static content
	 */
	@RequestMapping(value="staticContent")
	public ResponseEntity<String> staticContent(
			@RequestParam(value="key", required=false) String key,
			@RequestParam(value="lang", required=false) String lang,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {				                                
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);
			output = playerApiService.staticContent(key, lang);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Register a device. Will set a "device" cookie if registration is successful.
	 * 
	 * @param user user token, optional. will bind to device if user token is provided.
	 * @return device token
	 */
	@RequestMapping(value="deviceRegister")
	public ResponseEntity<String> deviceRegister(
			@RequestParam(value="user", required=false) String userToken,
			@RequestParam(value="type", required=false) String type,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("user:" + userToken);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS) {
				return NnNetUtil.textReturn(
						playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			}
			output = playerApiService.deviceRegister(userToken, type, req, resp);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * List recommendation sets 
	 * 
	 * @return <p>lines of set info.
	 *         <p>Set info includes set id, set name, set description, set image, set channel count. Fields are separated by tab.          
	 */		
	@RequestMapping(value="listRecommended")
	public ResponseEntity<String> listRecommended(
			@RequestParam(value="lang", required=false) String lang,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {				                                
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);
			output = playerApiService.listRecommended(lang);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Verify device token
	 *  
	 * @param device device token
	 * @return user token, user name, user email if any. multiple entries will be separated by \n
	 */
	@RequestMapping(value="deviceTokenVerify")
	public ResponseEntity<String> deviceTokenVerify(
			@RequestParam(value="device", required=false) String token,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("user:" + token);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);		
			output = playerApiService.deviceTokenVerify(token, req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Bind a user to device
	 * 
	 * @param device device token
	 * @param user user token
	 * @return status
	 */
	@RequestMapping(value="deviceAddUser")
	public ResponseEntity<String> deviceAddUser(
			@RequestParam(value="device", required=false) String deviceToken,
			@RequestParam(value="user", required=false) String userToken,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("user:" + userToken + ";device=" + deviceToken);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS) {
				return NnNetUtil.textReturn(
						playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			}
			output = playerApiService.deviceAddUser(deviceToken, userToken, req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}
	
	/**
	 * Unbind a user from the device
	 * 
	 * @param device device token
	 * @param user user token
	 * @return status
	 */
	@RequestMapping(value="deviceRemoveUser")
	public ResponseEntity<String> deviceRemoveUser(
			@RequestParam(value="device", required=false) String deviceToken,
			@RequestParam(value="user", required=false) String userToken,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("user:" + userToken + ";device=" + deviceToken);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));			
			output = playerApiService.deviceRemoveUser(deviceToken, userToken, req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}	

	/**
	 * For users to report problem. Either user or device needs to be provided.
	 * 
	 * @param user user token
	 * @param device device token
	 * @param session session id, same as pdr session id
	 * @param comment user's problem description
	 * @return report id
	 */
	@RequestMapping(value="userReport")
	public ResponseEntity<String> userReport(
			@RequestParam(value="user", required=false) String user,
			@RequestParam(value="device", required=false) String device,
			@RequestParam(value="session", required=false) String session,
			@RequestParam(value="comment", required=false) String comment,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("user:" + user + ";session=" + session);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));		
			output = playerApiService.userReport(user, device, session, comment);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Set user profile information
	 * 
	 * @param user user token
	 * @param <p>key keys include "name", "email", "gender", "year", "sphere", "ui-lang", "password", "oldPassword". <br/> 
	 *               Keys are separated by comma.
	 * @param <p>value value that pairs with keys. values are separated by comma. The sequence of value has to be the same as 
	 *        the sequence of keys. 
	 *        <p>Key and value are used in pairs with corresponding sequence. 
	 *           For example key=name,email,gender&value=john,john@example.com,1
	 *        <p>password: if password is provided, oldPassword becomes a mandatory field.
	 *        <p>gender: valid gender value is 1 and 0
	 *        <p>ui-lang: ui language. Currently valid values are "zh" and "en".
	 *        <p>sphere: content region. Currently valid values are "zh" and "en".
	 */
	@RequestMapping(value="setUserProfile")
	public ResponseEntity<String> setUserProfile(
			@RequestParam(value="user", required=false)String user,
			@RequestParam(value="key", required=false)String key,
			@RequestParam(value="value", required=false)String value,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {		
		log.info("set user profile: key(" + key + ");value(" + value + ")");
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS) {
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));		
			}
			output = playerApiService.setUserProfile(user, key, value, req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}	

	/**
	 * Get user profile information
	 * 
	 * @param user user token
	 * @return <p>Data returns in key and value pair. Key and value is tab separated. Each pair is \n separated.<br/>
	 *            keys include "name", "email", "gender", "year", "sphere" "ui-lang"<br/></p>"
	 *         <p>Example<br/>: name John <br/>email john@example.com<br/>ui-lang en                 
	 */	
	@RequestMapping(value="getUserProfile")
	public ResponseEntity<String> getUserProfile(
			@RequestParam(value="user", required=false)String user,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);		
			output = playerApiService.getUserProfile(user);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * For user's sharing via email function
	 * 
	 * @param user user token
	 * @param toEmail receiver email
	 * @param toName receiver name 
	 * @param subject email subject
	 * @param content email content
	 * @param captcha captcha
	 * @param text captcha text
	 * @return status
	 */
	@RequestMapping(value="shareByEmail")
	public ResponseEntity<String> shareByEmail(
			@RequestParam(value="user", required=false) String userToken,			                            
			@RequestParam(value="toEmail", required=false) String toEmail,
			@RequestParam(value="toName", required=false) String toName,
			@RequestParam(value="subject", required=false) String subject,
			@RequestParam(value="content", required=false) String content,
			@RequestParam(value="captcha", required=false) String captcha,
			@RequestParam(value="text", required=false) String text,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("user:" + userToken + ";to whom:" + toEmail + ";content:" + content);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);		
			output = playerApiService.shareByEmail(userToken, toEmail, toName, subject, content, captcha, text);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Request captcha for later verification
	 * 
	 * @param user user token 
	 * @param action action 1 is used for signup. action 2 is used for shareByEmail
	 * @return status
	 */
	@RequestMapping(value="requestCaptcha")
	public ResponseEntity<String> requestCaptcha(
			@RequestParam(value="user", required=false) String token,
			@RequestParam(value="action", required=false) String action,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("user:" + token);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);		
			output = playerApiService.requestCaptcha(token, action, req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Save user's channel sorting sequence
	 * 
	 * @param user user token
	 * @param channel channel id
	 * @param sorting sorting sequence. NEWEST_TO_OLDEST = 1, OLDEST_TO_NEWEST=2  
	 * @return status
	 */		
	@RequestMapping(value="saveSorting")
	public ResponseEntity<String> saveSorting(
			@RequestParam(value="user", required=false) String userToken,
			@RequestParam(value="channel", required=false) String channelId,
			@RequestParam(value="sorting", required=false) String sorting,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		
		log.info("user:" + userToken + ";channel:" + channelId + ";sorting:" + sorting);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));		
			output = playerApiService.saveSorting(userToken, channelId, sorting);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Save User Sharing
	 *
	 * @param user user's unique identifier
	 * @param channel channel id
	 * @param program program id
	 * @param set set id (place holder for now)
	 * @return A unique sharing identifier
	 */
	@RequestMapping(value="saveShare")
	public ResponseEntity<String> saveShare(
			@RequestParam(value="user", required=false) String userToken, 
			@RequestParam(value="channel", required=false) String channelId,
			@RequestParam(value="set", required=false) String setId,
			@RequestParam(value="program", required=false) String programId,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {		
		
		log.info("saveShare(" + userToken + ")");
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);		
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));	
			output = playerApiService.saveShare(userToken, channelId, programId, setId);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}	

	/**
	 * Load User Sharing
	 *
	 * @param id unique identifier from saveShare
	 * @return  Returns a program to play follows channel information.
	 * 	        The program to play returns in the 2nd section, format please reference programInfo format.
	 *          3rd section is channel information, format please reference channelLineup.
	 */
	@RequestMapping(value="loadShare")
	public ResponseEntity<String> loadShare(
			@RequestParam(value="id") Long id, 
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req) {		
		log.info("ipgShare:" + id);		
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);		
		try {
			this.prepService(req, true);
			output = playerApiService.loadShare(id);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);						
	}

	/**
	 * User's recently watched channel and its episode.
	 * 
	 * @param user user token
	 * @param count number of recently watched entries
	 * @param channelInfo true or false
	 * @param episodeIndex true or false. if episodeIndex = true, count has to be less 5.
	 * @return Fist block: Lines of channel id and program id.<br/>
	 *         Second block: if channelInfo is set to true, detail channel information will be returned. Please reference channelLineup for format.
	 */
	@RequestMapping(value="recentlyWatched")
	public ResponseEntity<String> recentlyWatched(
			@RequestParam(value="user", required=false) String userToken,
			@RequestParam(value="count", required=false) String count,
			@RequestParam(value="channel", required=false) String channel,
			@RequestParam(value="channelInfo", required=false) String channelInfo,
			@RequestParam(value="episodeIndex", required=false) String episodeIndex,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req) {				                                
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);		
		try {
			this.prepService(req, true);
			boolean isChannelInfo = Boolean.parseBoolean(channelInfo);
			boolean isEpisodeIndex = Boolean.parseBoolean(episodeIndex);
			output = playerApiService.userWatched(userToken, count, isChannelInfo, isEpisodeIndex, channel);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Copy a channel to grid location
	 * 
	 * @param user user's unique identifier
	 * @param channel channel id
	 * @param grid grid location 
	 * 
	 * @return status code and status message
	*/
	@RequestMapping(value="copyChannel")
	public ResponseEntity<String> copyChannel(
			@RequestParam(value="user", required=false) String userToken, 
			@RequestParam(value="channel", required=false) String channelId,
			@RequestParam(value="grid", required=false) String grid,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req){
		log.info("userToken=" + userToken + ";grid=" + grid);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			output = playerApiService.copyChannel(userToken, channelId, grid);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
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
	public ResponseEntity<String> moveChannel(
			@RequestParam(value="user", required=false) String userToken, 
			@RequestParam(value="grid1", required=false) String grid1,
			@RequestParam(value="grid2", required=false) String grid2,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp){
		log.info("userToken=" + userToken + ";grid1=" + grid1 + ";grid2=" + grid2);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			output = playerApiService.moveChannel(userToken, grid1, grid2);
		} catch (Exception e){
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}	
		return NnNetUtil.textReturn(output);		
	}					
	
	/**
	 * Search channel name and description
	 * 
	 * @param search search text
	 * @return matched channels, format please reference channelLineup
	 */
	@RequestMapping(value="search")
	public ResponseEntity<String> search(
			@RequestParam(value="text", required=false) String text,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);
			output = playerApiService.search(text);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}
	
	/**
	 * Mark a program bad when player sees it 
	 * 
	 * @param user user token
	 * @param program programId
	 */	
	@RequestMapping(value="programRemove")
	public ResponseEntity<String> programRemove(
			@RequestParam(value="program", required=false) String programId,
			@RequestParam(value="user", required=false) String userToken,
			@RequestParam(value="bird", required=false) String secret,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		log.info("bad program:" + programId + ";reported by user:" + userToken);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));
			output = playerApiService.programRemove(programId, userToken, secret);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Create a 9x9 channel
	 * 
	 * @param name name
	 * @param description description
	 * @param image image url
	 * @param temp not specify means false 
	 */	
	@RequestMapping(value="channelCreate")
	public ResponseEntity<String> channelCreate(
			@RequestParam(value="user", required=false) String user,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="description", required=false) String description,
			@RequestParam(value="image", required=false) String image,
			@RequestParam(value="rx", required = false) String rx,
			@RequestParam(value="temp", required=false) String temp,
			HttpServletRequest req,
			HttpServletResponse resp) {
		
		log.info("user:" + user + ";name:" + name + ";description:" + description + ";temp:" + temp);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);		
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));	
			boolean isTemp= Boolean.parseBoolean(temp);		
			output = playerApiService.channelCreate(user, name, description, image, isTemp);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Create a 9x9 program
	 * 
	 * @param channel channel id
	 * @param name name
	 * @param image image url
	 * @param description description
	 * @param audio audio url
	 * @param video video url
	 * @param temp not specify means false 
	 */	
	@RequestMapping(value="programCreate")
	public ResponseEntity<String> programCreate(
			@RequestParam(value="channel", required=false) String channel,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="image", required=false) String image,
			@RequestParam(value="description", required=false) String description,
			@RequestParam(value="audio", required=false) String audio,
			@RequestParam(value="video", required=false) String video,
			@RequestParam(value="temp", required=false) String temp,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
		
		log.info("name:" + name + ";description:" + description + ";audio:" + audio+ ";video:" + video);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));	
			boolean isTemp= Boolean.parseBoolean(temp);		
			output = playerApiService.programCreate(channel, name, description, image, audio, video, isTemp);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}
	
	/**
	 * Set program property
	 * 
	 * @param program program id
	 * @param property program property
	 * @param value program property value
	 */	
	@RequestMapping(value="setProgramProperty")
	public ResponseEntity<String> setProgramProperty(
			@RequestParam(value="program", required=false) String program,
			@RequestParam(value="property", required=false) String property,
			@RequestParam(value="value", required=false) String value,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
						
		log.info("program:" + program + ";property:" + property + ";value:" + value);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));						
			output = playerApiService.setProgramProperty(program, property, value);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * Set channel property
	 * 
	 * @param channel channel id
	 * @param property channel property
	 * @param value channel property value
	 */	
	@RequestMapping(value="setChannelProperty")
	public ResponseEntity<String> setChannelProperty(
			@RequestParam(value="channel", required=false) String channel,
			@RequestParam(value="property", required=false) String property,
			@RequestParam(value="value", required=false) String value,
			@RequestParam(value="rx", required = false) String rx,
			HttpServletRequest req,
			HttpServletResponse resp) {
						
		log.info("channel:" + channel + ";property:" + property + ";value:" + value);
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));						
			output = playerApiService.setChannelProperty(channel, property, value);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	/**
	 * It's merged of 4 APIs, userTokenVerify(login, guestRegister), listRecommended, setInfo and programInfo.
	 * 
	 * If token is provided, will do userTokenVerify.
	 * If token is not provided, will do login
	 * If token and email/password is not provided, will do guestRegister.
	 * 
	 * Return text includes 4 sections. 
	 * 
	 * first: userTokenVerify, or login, or guestRegister
	 * second: listRecommended
	 * third: setInfo
	 * fourth: programInfo 
	 * 
	 * @param token if not empty, will do userTokenVerify
	 * @param email if token is not provided, will do login check with email and password
	 * @param password password 
	 * @param rx rx
	 * @return please reference api introduction
	 */
	@RequestMapping(value="quickLogin")
	public ResponseEntity<String> quickLogin(
			@RequestParam(value="token", required=false) String token,
			@RequestParam(value="email", required=false) String email,
			@RequestParam(value="password", required=false) String password,			
			@RequestParam(value="rx", required = false) String rx,			
			HttpServletRequest req,
			HttpServletResponse resp) {		
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			int status = this.prepService(req, true);
			if (status != NnStatusCode.SUCCESS)
				return NnNetUtil.textReturn(playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null));						
			output = playerApiService.quickLogin(token, email, password, req, resp);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);				 
	}

	@RequestMapping(value="graphSearch")
	public ResponseEntity<String> graphSearch(
			@RequestParam(value="email", required=false) String email,			
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="rx", required = false) String rx,			
			HttpServletRequest req,
			HttpServletResponse resp) {		
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);
			output = playerApiService.graphSearch(email, name);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}			
		return NnNetUtil.textReturn(output);
	}

	@RequestMapping(value="userInvite")
	public ResponseEntity<String> userInvite(
			@RequestParam(value="user", required=false) String userToken,			                            
			@RequestParam(value="toEmail", required=false) String toEmail,
			@RequestParam(value="toName", required=false) String toName,
			@RequestParam(value="channel", required=false) String channel,
			HttpServletRequest req,
			HttpServletResponse resp) {
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);		
			output = playerApiService.userInvite(userToken, toEmail, toName, channel, req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	@RequestMapping(value="inviteStatus")
	public ResponseEntity<String> inviteStatus(
			@RequestParam(value="token", required=false) String token,			                            
			HttpServletRequest req,
			HttpServletResponse resp) {
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);		
			output = playerApiService.inviteStatus(token);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	@RequestMapping(value="disconnect")
	public ResponseEntity<String> disconnect(
			@RequestParam(value="user", required=false) String userToken,			                            
			@RequestParam(value="toEmail", required=false) String toEmail,
			@RequestParam(value="channel", required=false) String channel,
			HttpServletRequest req,
			HttpServletResponse resp) {
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);		
			output = playerApiService.disconnect(userToken, toEmail, channel, req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}

	@RequestMapping(value="notifySubscriber")
	public ResponseEntity<String> notifySubscriber(
			@RequestParam(value="user", required=false) String userToken,
			@RequestParam(value="channel", required=false) String channel,
			HttpServletRequest req,
			HttpServletResponse resp) {
		String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
		try {
			this.prepService(req, true);		
			output = playerApiService.notifySubscriber(userToken, channel, req);
		} catch (Exception e) {
			output = playerApiService.handleException(e);
		} catch (Throwable t) {
			NnLogUtil.logThrowable(t);
		}
		return NnNetUtil.textReturn(output);
	}
	
	
	/*
	@RequestMapping(value="piwikCreate")
	public ResponseEntity<String> piwikCreate(
			@RequestParam(value="setId", required=false) long setId,
			@RequestParam(value="channelId", required=false) long channelId) {
		String piwikId = PiwikLib.createPiwikSite(setId, channelId);
		log.info("setId:" + setId + ";channelId:" + channelId + ";piwik id:" + piwikId);
		return NnNetUtil.textReturn(piwikId);		
	}
	*/
}