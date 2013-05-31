package com.nncloudtv.web.api;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.memcached.MemcachedClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.service.IosService;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnEpisodeManager;
import com.nncloudtv.service.NnStatusMsg;
import com.nncloudtv.service.PlayerApiService;
import com.nncloudtv.web.json.facebook.FacebookMe;

/**
// * This is API specification for 9x9 chaPlayer. Please note although the document is written in JavaDoc form, it is generic Web Service API via HTTP request-response, no Java necessary.
 * <p>
 * <blockquote>/
// * Example:
 * <p>
 * Player Request: <br/>
 * http://qa.9x9.tv/playerAPI/brandInfo?mso=9x9
 * <p>
 * Service response:  <br/>
 * 0    success<br/>
 * --<br/>
 * name        9x9<br/>
 * title    9x9.tv<br/>
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
 * Account related: guestRegister, signup, login, userTokenVerify, signout, fbLogin, fbSignup, 
 *                  setUserProfile, getUserProfile, setUesrPref
 * <p>
 * <p>
 * Category listing: categoryInfo, tagInfo, setInfo
 * <p>
 * Curator: curator
 * <p>
 * Channel and program listing: channelLineup, programInfo
 * <p>
 * IPG action: moveChannel, channelSubmit, subscribe, unsubscribe, setSetInfo
 * <p>
 * YouTube connect: obtainAccount, bulkSubscribe 
 * <p>
 * YouTube info update: virtualChannelAdd, channelUpdate 
 * <p>
 * Data collection: pdr, programRemove
 * <p>
 * System message: staticContent 
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
 * 0    success  <br/>
 * -- <br/>
 * token    a466D491UaaU245P412a <br/>
 * name    a
 * <p>
 * Example 2: categoryBrowse
 * <p>
 * 0    success  <br/>
 * -- <br/>
 * 1201    Movie    5 <br/>
 * 1203    TV    2 <br/>
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
    
    private int prepService(HttpServletRequest req, boolean tolog) {        
        /*
        String userAgent = req.getHeader("user-agent");
        if ((userAgent.indexOf("CFNetwork") > -1) && (userAgent.indexOf("Darwin") > -1))     {
            playerApiService.setUserAgent(PlayerApiService.PLAYER_IOS);
            log.info("from iOS");
        }
        */
        String userAgent = req.getHeader("user-agent");
        log.info("user agent:" + userAgent);
        
        String msoName = req.getParameter("mso");
        if (tolog) 
            NnNetUtil.logUrl(req);
        HttpSession session = req.getSession();
        session.setMaxInactiveInterval(60);
        MsoManager msoMngr = new MsoManager();
        Mso mso = msoMngr.getByNameFromCache(msoName);
        if (mso == null) {
            mso = msoMngr.getByNameFromCache(Mso.NAME_9X9);;
           //mso = msoMngr.findNNMso();
        }
        log.info("mso entrance:" + mso.getId());
        Locale locale = Locale.ENGLISH;
        playerApiService.setLocale(locale);
        playerApiService.setMso(mso);
        int status = playerApiService.checkRO();
        String version = (req.getParameter("v") == null) ? "31" : req.getParameter("v");
        int intVersion = Integer.parseInt(version);
        int minimal = playerApiService.checkApiMinimal();        
        playerApiService.setVersion(Integer.parseInt(version));
        if (intVersion < minimal)
        	status = NnStatusCode.API_FORCE_UPGRADE;
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
    @RequestMapping(value="guestRegister", produces = "text/plain; charset=utf-8")
    public @ResponseBody String guestRegister(
            @RequestParam(value="ipg", required = false) String ipg, 
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req, 
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {            	
                return playerApiService.assembleMsgs(status, null);
            }            
            output = playerApiService.guestRegister(req, resp);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
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
    @RequestMapping(value="signup", produces = "text/plain; charset=utf-8")
    public @ResponseBody String signup(HttpServletRequest req, HttpServletResponse resp) {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String mso = req.getParameter("mso");
        String name = req.getParameter("name");
        String userToken = req.getParameter("user");
        String captcha = req.getParameter("captcha");
        String text = req.getParameter("text");
        String sphere = req.getParameter("sphere");
        String year = req.getParameter("year");
        String lang = req.getParameter("ui-lang");
        String rx = req.getParameter("rx");
        boolean isTemp = Boolean.parseBoolean(req.getParameter("temp"));
                
        log.info("signup: email=" + email + ";name=" + name + ";mso:" + mso + 
                 ";userToken=" + userToken + ";sphere=" + sphere + 
                 ";year=" + year + ";ui-lang=" + lang + 
                 ";rx=" + rx);
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {            	
                return playerApiService.assembleMsgs(status, null);
            }            
            output = playerApiService.signup(email, password, name, userToken, captcha, text, sphere, lang, year, isTemp, req, resp);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }    

    /**
     * Pass every param passing from Facebook in its original format
     * 
     * @param id facebook id
     * @param name name
     * @param username facebook username
     * @param birthday birthday
     * @param email user email
     * @param locale locale
     * @param token access token
     * @param expire expiration date
     * @return 
     */
    @RequestMapping(value="fbSignup", produces = "text/plain; charset=utf-8")
    public @ResponseBody String fbSignup(HttpServletRequest req, HttpServletResponse resp) {
        String msoString = req.getParameter("mso");
        if (msoString == null) {
            msoString = "9x9"; 
        }
        FacebookMe me = new FacebookMe();        
        me.setId(req.getParameter("id"));
        me.setName(req.getParameter("name"));
        me.setUsername(req.getParameter("username"));
        me.setBirthday(req.getParameter("birthday"));
        me.setEmail(req.getParameter("email"));
        me.setLocale(req.getParameter("locale"));
        me.setAccessToken(req.getParameter("token"));
        String expire = req.getParameter("expire");

        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {            	
                return playerApiService.assembleMsgs(status, null);
            }            
            output = playerApiService.fbDeviceSignup(me, expire, msoString, req, resp);
        } catch (Exception e){
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;

     }
    
    /**
     * Verify user token <br/>
     * Example: http://host:port/playerAPI/userTokenVerify?token=QQl0l208W2C4F008980F
     * 
     * @param token user key 
     * @return Will delete the user cookie if token is invalid.<br/>
     *            Return info please reference login.
     */    
    @RequestMapping(value="userTokenVerify", produces = "text/plain; charset=utf-8")
    public @ResponseBody String userTokenVerify(
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
        return output;
    }
    
    /**
     * "user" cookie will be removed
     * 
     * @param user user key identifier 
     */        
    @RequestMapping(value="signout", produces = "text/plain; charset=utf-8")
    public @ResponseBody String signout(
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
        return output;
    }    
    
    /**
     * Get brand information. 
     *     
     * @param mso mso name, optional, server returns default mso 9x9 if omiited
     * @return <p>Data returns in key and value pair. Key and value is tab separated. Each pair is \n separated.<br/> 
     *            keys include "key", "name", logoUrl", "jingleUrl", "preferredLangCode" "debug"<br/></p>
     *         <p>Example: <br/>
     *          0    success <br/>
     *          --<br/>
     *          key    1<br/>
     *          name    9x9<br/>
     *          title    9x9.tv<br/>
     *          logoUrl    /WEB-INF/../images/logo_9x9.png<br/>
     *          jingleUrl    /WEB-INF/../videos/opening.swf<br/>
     *          logoClickUrl    /<br/>
     *          preferredLangCode    en<br/>
     *          debug    1<br/>
     *         </p>
     */    
    @RequestMapping(value="brandInfo", produces = "text/plain; charset=utf-8")
    public @ResponseBody String brandInfo(
            @RequestParam(value="mso", required=false)String brandName,
            @RequestParam(value="version", required=false)String version,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req, HttpServletResponse resp) {
        //resp.setContentType(ApiGeneric.PLAIN_TEXT_UTF8); // Louis: this be work as well (return value is not correct to me)
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {            	
                return playerApiService.assembleMsgs(status, null);
            }                                    
            output = playerApiService.brandInfo(req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**   
     * For directory query. Returns list of categories.
     * To get furthur info, use categoryInfo   
     * 
     * @param lang en or zh 
     * @return <p>Block one, the requested category info. Always one for now. Block two, list of categories.
     *            List of categories has category id, cateogory name, channel count, items after category. 
     *            Currently items after category will always be "ch" indicating channels.     
     *         <p>Example: <br/>
     *            id    0 <br/>
     *            --<br/>
     *            1    News    55    ch <br/>
     *            9    Sports    124    ch <br/>
     *            22    Music    165    ch
     */
    @RequestMapping(value="category", produces = "text/plain; charset=utf-8")
    public @ResponseBody String category(
            @RequestParam(value="lang", required=false) String lang,
            @RequestParam(value="category", required=false) String category,
            @RequestParam(value="mso", required=false) String mso,            
            @RequestParam(value="flatten", required=false) String isFlatten,
            @RequestParam(value="v", required=false) String v,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {            	
                return playerApiService.assembleMsgs(status, null);
            }                        
            boolean flatten = Boolean.parseBoolean(isFlatten);
            if (playerApiService.getVersion() < 32) {
                log.info("category:" + category);
                String msoName = req.getParameter("mso");
                MsoManager msoMngr = new MsoManager();
                Mso brand = msoMngr.findByName(msoName);
                if (brand == null) {
                   brand = msoMngr.findNNMso();
                }                
                return new IosService().category(category, lang, flatten, brand ); 
            }
            output = playerApiService.category(category, lang, flatten);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Collecting PDR
     * 
     * @param user user token
     * @param session indicates the session when user starts using the player
     * @param pdr pdr data
     *           <p> Expecting lines(separated by \n) of the following:<br/>  
     *           delta verb info <br/>
     *           Example: delta watched 1 1 2 3 <br/>
     *           Note: first 1 is channel, the rest are program ids. <br/>  
     *           Note: each field is separated by tab.
     *           </p> 
     */    
    @RequestMapping(value="pdr", produces = "text/plain; charset=utf-8")
    public @ResponseBody String pdr(
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="device", required=false) String deviceToken,
            @RequestParam(value="session", required=false) String session,
            @RequestParam(value="pdr", required=false) String pdr,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String root = NnNetUtil.getUrlRoot(req); //http://dev.9x9.tv
        String pdrServer = "http://v32d.9x9.tv";
        String prod1 = "http://www.9x9.tv";
        String prod2 = "http://9x9.tv";
        String path = "";
        
        /*
        String userToken = req.getParameter("user");
        String deviceToken = req.getParameter("device");
        String session = req.getParameter("session");
        String pdr = req.getParameter("session");
        */
        if (!root.equals(pdrServer) && (root.equals(prod1) || root.equals(prod2))) {
        //if (!root.equals(pdrServer)) {
            path = "/playerAPI/pdrServer";
            URL url;
            try {        
                String urlStr = pdrServer + path;
                String params = "user=" + userToken + 
                 "&device=" + deviceToken + 
                 "&session=" + session +
                 "&pdr=" + pdr +                     
                 "&rx=" + rx;
                url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(params);
                writer.close();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    log.info("redirection failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return playerApiService.assembleMsgs(NnStatusCode.SUCCESS, null); 
            
//        if (!root.equals(pdrServer) && (root.equals(prod1) || root.equals(prod2))) {
//        //if (!root.equals(pdrServer)) {
//            String queryStr = req.getQueryString(); //user=a&session=1
//            if (queryStr != null && !queryStr.equals("null"))
//                queryStr = "?" + queryStr;
//            else 
//                queryStr = "";
//            path = "/playerAPI/pdrServer" + queryStr;
//            String url = pdrServer + path;
//            log.info("url:" + url);
//            log.info("redirect to v32d");
//            return "redirect:" + url;
//        }
//        log.info("path:" + path);
//        return "redirect:" + path;
    }

    /**
     * To be ignored 
     */
    @RequestMapping(value="pdrServer", produces = "text/plain; charset=utf-8")
    public @ResponseBody String pdrServer(
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
                return playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null);                        
            }
            output = playerApiService.pdr(userToken, deviceToken, session, pdr, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    
    /**
     * Retrieves set information. A "set" can be a dayparting, or a set.
     * 
     * @param set set id
     * @param landing the name used as part of the URL. query with either set or landing 
     * @return first block: status <br/>
     *         second block: brand info, returns in key and value pair. <br/>                     
     *         third block: set info, returns in key and value pair <br/>
     *         4th block: channel details. reference "channelLineup". <br/>
     *         5th block: first episode of every channel from the 4th block. reference "programInfo". <br/>
     *         <p>
     *         Example: <br/>
     *         0    success<br/>
      *         --<br/>
     *         name    daai<br/>
     *         imageUrl    http://9x9ui.s3.amazonaws.com/9x9playerV52/images/logo_tzuchi.png<br/>
     *         intro    daai<br/>
     *         --<br/>
     *         name    Daai3x3<br/>
     *         imageUrl    null<br/>
     *         --<br/>
     *         1    396    channel1    channel1 http://podcast.daaitv.org/Daai_TV_Podcast/da_ai_dian_shi/da_ai_dian_shi_files/shapeimage_3.png    3    0    0    2...<br/>    
     *         2    399    channel2    channel2 http://podcast.daaitv.org/Daai_TV_Podcast/jing_si_yu/jing_si_yu_files/shapeimage_4.png    3    0    0    2    ...<br/>
     *         --<br/>
     *         11274    50265348    Melissa hello  1   50  http://i.ytimg.com/vi/ss0ELKuua2I/mqdefault.jpg     .....<br/>       
     */
    @RequestMapping(value="setInfo", produces = "text/plain; charset=utf-8")
    public @ResponseBody String setInfo(
            @RequestParam(value="set", required=false) String id,
            @RequestParam(value="v", required=false) String v,
            @RequestParam(value="landing", required=false) String name,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        log.info("setInfo: id =" + id + ";landing=" + name);
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);        	
            if (status == NnStatusCode.API_FORCE_UPGRADE) {            	
                return playerApiService.assembleMsgs(status, null);
            }                    	
            output = playerApiService.setInfo(id, name);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;      
 
        /*
        String msoName = req.getParameter("mso");
        MsoManager msoMngr = new MsoManager();
        Mso mso = msoMngr.findByName(msoName);
        if (mso == null) {
           mso = msoMngr.findNNMso();
        }
        return new IosService().setInfo(id, beautifulUrl, mso);        
        */
    }

    /** 
     * Get channel list based on tag. 
     * 
     * @param name tag name. currently only one name is supported
     * @param start the start of the index
     * @param count count of records returned
     * @return list of channel information <br/>
     *         First block is tag id, tag name, start index, counts of records. <br/> 
     *         Second block is list of channel information. Please reference channelLineup, version = 32 <br/>
     *         Example of the first block: <br/>
     *              id    1539<br/>
     *              name    news<br/>
     *              start   1<br/>
     *              count   1<br/>
     *              total   1<br/>
     */
    @RequestMapping(value="tagInfo", produces = "text/plain; charset=utf-8")
    public @ResponseBody String tagInfo(
            @RequestParam(value="name", required=false) String name,
            @RequestParam(value="mso", required=false) String mso,
            @RequestParam(value="start", required=false) String start,
            @RequestParam(value="count", required=false) String count,            
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {        
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {            	
                return playerApiService.assembleMsgs(status, null);
            }                                                
            output = playerApiService.tagInfo(name, start, count);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);            
        }
        return output;                
    }
    
    /** 
     * Get list of channels under the category. Start and count is for the channel records in the last block.  
     *  
     * @param category category id
     * @param start the start of the index
     * @param count count of records
     * 
     * @return First block has category info, id and name. <br/>
     *         Second block lists the most popular tags. Separated by \n <br/> 
     *         Third block lists channels under the category. Format please reference channelLineup.
     *         <p>  Example:
     *         0    SUCCESS<br/>
     *         --<br/>
     *         id    2<br/>
     *         name    Tech & Gaming<br/>
     *         --<br/>
     *         tech<br/>
     *         gaming<br/>
     *         --<br/>
     *         (channelLineup version > 32)         
     */
    @RequestMapping(value="categoryInfo", produces = "text/plain; charset=utf-8")
    public @ResponseBody String categoryInfo(
            @RequestParam(value="category", required=false) String id,
            @RequestParam(value="programInfo", required=false) String programInfo,
            @RequestParam(value="mso", required=false) String mso,
            @RequestParam(value="start", required=false) String start,
            @RequestParam(value="count", required=false) String count,
            @RequestParam(value="sort", required=false) String sort,
            @RequestParam(value="tag", required=false) String tag,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        log.info("categoryInfo: id =" + id);        
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {            	
                return playerApiService.assembleMsgs(status, null);
            }
            boolean isProgramInfo = Boolean.parseBoolean(programInfo);
            output = playerApiService.categoryInfo(id, tag, start, count, sort, isProgramInfo);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);            
        }
        return output;                
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
    @RequestMapping(value="subscribe", produces = "text/plain; charset=utf-8")
    public @ResponseBody String subscribe(
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
                return playerApiService.assembleMsgs(status, null);
            }                                                
            output = playerApiService.subscribe(userToken, channelId, gridId);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);            
        }
        return output;
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
     * @param pos set position
     * @return status code and status message
     */            
    @RequestMapping(value="unsubscribe", produces = "text/plain; charset=utf-8")
    public @ResponseBody String unsubscribe(
            @RequestParam(value="user", required=false) String userToken, 
            @RequestParam(value="channel", required=false) String channelId,
            @RequestParam(value="grid", required=false) String grid,
            @RequestParam(value="pos", required=false) String pos,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {            
        log.info("userToken=" + userToken + "; channel=" + channelId + "; pos=" + pos + "; seq=" + grid);
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {            	
                return playerApiService.assembleMsgs(status, null);
            }                                                            
            output = playerApiService.unsubscribe(userToken, channelId, grid, pos);    
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }    

    /**
     * Get list of channel based on special stack
     *  
     * @param stack legal values include "recommend", "hot", "mayLike", "featured", "trending"
     * @param lang language, en or zh
     * @param userToken user token, used for recommend
     * @param channel channel id, used for recommend
     * @return Reference channelLineup
     */
    @RequestMapping(value="channelStack", produces = "text/plain; charset=utf-8")
    public @ResponseBody String channelStack(
            @RequestParam(value="stack", required=false) String stack,
            @RequestParam(value="mso", required=false) String mso,            
            @RequestParam(value="lang", required=false) String lang,
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="channel", required=false) String channel,
            @RequestParam(value="reduced", required=false) String reduced,            
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {
                playerApiService.assembleMsgs(status, null);                        
            }
            boolean isReduced= Boolean.parseBoolean(reduced);            
            output = playerApiService.channelStack(stack, lang, userToken, channel, isReduced);    
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    
    /**
     * Get channel information 
     * 
     * @param user user's unique identifier. 
     * @param curator curator's id. giving curator id returns channels the curator creates.
     * @param subscriptions curator id. giving subscriptions returns channels the curator subscribes.
     * @param stack trending, recommended, hot
     * @param userInfo true or false. Whether to return user information as login does. If asked, it will be returned after status code.
     * @param channel channel id, optional, can be one or multiple;  example, channel=1 or channel=1,2,3
     * @param setInfo true or false. Whether to return subscription set information.  
     * @param required true or false. Will return error in status block if the requested channel is not found.
     * @param stack featured/recommended/hot/trending. Each stack in return is separated by "--\n"
     * @return A string of all of requested channel information
     *         <p>
     *         First block: status. Second block: set information. This block shows only if setInfo is set to true. 
     *         Third block: channel information. It would be the second block if setInfo is false
     *         <p>
     *         Set info has following fields: <br/>
     *         position, set id, set name, set image url, set type
     *         <p>  
     *         Channel info has following fields: <br/>
     *         1.  grid position, <br/> 
     *         2.  channel id, <br/>
     *         3.  channel name, <br/>
     *         4.  channel description, <br/> 
     *         5.  channel image url, first is channel thumbnail, followed by 3 latest episode thumbnail(could be empty), all separeted by "|". Version before 3.2 will have one image url without |<br/>
     *         6.  program count, <br/> 
     *         7.  channel type(integer, see note), <br/> 
     *         8.  channel status(integer, see note), <br/>
     *         9.  contentType(integer, see note), <br/> 
     *         10. youtube id (for player youtube query), <br/>
     *         11. channel/episodes last update time (see note) <br/>
     *         12. channel sorting (see note), <br/> 
     *         13. piwik id, <br/> 
     *         14. last watched episode <br/>
     *         15. youtube real channel name <br/>
     *         16. subscription count. it is the last field of the version before 3.2.<br/>
     *         17. view count <br/>
     *         18. tags, separated by comma. example "run,marathon" <br/>         
     *         19. curator id <br/>
     *         20. curator name <br/>
     *         21. curator description <br/>
     *         22. curator imageUrl <br/>
     *         23. subscriber ids, separated by "|" <br/>
     *         24. subscriber images, separated by "|" <br/>
     *         25. last episode title<br/>
     *         26. poi information, each poi is separated by "|". 
     *         Each poi unit has start time, end time, poi type, urlencode json poi context; they are separated by ";".
     *         Poi currently there's only one type hyper link. This info is also included in the poi context.
     *         A poi unit looks like this: 3;5;1;%7B%0Amessage%3A+%22%E6%9B%B4%E5%A4%9A%E5%A3%B9%E5%82%B3%E5%AA%92%E5%85%A7%E5%B9%95%2C%E7%9B%A1%E5%9C%A8%E5%AA%92%E9%AB%94%E5%81%9C%E7%9C%8B%E8%81%BD%22%2C%0Abutton%3A+%5B%0A%7Btext%3A+%22%E4%BA%86%E8%A7%A3%E6%9B%B4%E5%A4%9A%22%2C+actionUrl%3A+%22http%3A%2F%2Fwww.9x9.tv%2Fview%3Fch%3D1380%26ep%3D6789%5D%22%7D%0A++++++++%5D%0A%7D|<br/> 
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
     *         0    success<br/>
     *         --<br/>
     *         1239   1   Daai3x3   null<br/>
     *         -- <br/>
     *         1    1207    Channel1    http://hostname/images/img.jpg    3    1    0    3    http://www.youtube.com/user/android <br/>
     *         </p>
     */        
    @RequestMapping(value="channelLineup", produces = "text/plain; charset=utf-8")
    public @ResponseBody String channelLineup(
            @RequestParam(value="v", required=false) String v,
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="programInfo", required=false) String programInfo,
            @RequestParam(value="subscriptions", required=false) String subscriptions,
            @RequestParam(value="curator", required=false) String curatorIdStr,
            @RequestParam(value="userInfo", required=false) String userInfo,
            @RequestParam(value="channel", required=false) String channelIds,
            @RequestParam(value="setInfo", required=false) String setInfo,
            @RequestParam(value="required", required=false) String required,
            @RequestParam(value="tag", required=false) String tag,
            @RequestParam(value="category", required=false) String category,
            @RequestParam(value="reduced", required=false) String reduced,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        log.info("userToken=" + userToken + ";isUserInfo=" + userInfo + ";channel=" + channelIds + ";setInfo=" + setInfo);                
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {            	
                return playerApiService.assembleMsgs(status, null);
            }                                                            
            boolean isUserInfo = Boolean.parseBoolean(userInfo);
            boolean isSetInfo = Boolean.parseBoolean(setInfo);
            boolean isRequired = Boolean.parseBoolean(required);
            boolean isReduced= Boolean.parseBoolean(reduced);
            boolean isProgramInfo = Boolean.parseBoolean(programInfo);
            output = playerApiService.channelLineup(userToken, curatorIdStr, subscriptions, isUserInfo, channelIds, isSetInfo, isRequired, isReduced, isProgramInfo, req);
        } catch (Exception e){
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }    

    /**
     * To be ignored 
     */
    @RequestMapping(value="subscriberLineup", produces = "text/plain; charset=utf-8")
    public @ResponseBody String subscriberLineup(
            @RequestParam(value="v", required=false) String v,
            @RequestParam(value="user", required=false) String userToken,            
            @RequestParam(value="curator", required=false) String curatorIdStr,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            this.prepService(req, true);
            output = playerApiService.subscriberLineup(userToken, curatorIdStr);
        } catch (Exception e){
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
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
     *                    (2)Could be a channel Id, e.g. channel=1 <br/>
     *                    (3)Could be list of channels, e.g. channels = 34,35,36.
     * @param  user user's unique identifier, it is required for wildcard query
     * @param  userInfo true or false. Whether to return user information as login. If asked, it will be returned after status code. 
     * @param  ipg  ipg's unique identifier, it is required for wildcard query
     * @param  sidx the start index for pagination
     * @param  limit the count of records
     * @return <p>Programs info. Each program is separate by \n.</p>
     *          <p>Program info has: <br/>
     *            1. channelId <br/>
     *            2. programId <br/>
     *            3. program name, version after 3.2 has "|" to separate between sub-episodes, it starts from the umbrella episode name and follows by each sub-episode's name. <br/>
     *            4. description(max length=256), version after 3.2 has "|" to separate between sub-episodes, it starts from the umbrella episode description and follows by each sub-episode's description.<br/>
     *            5. programType, version after 3.2 has "|" to separate between sub-episodes, expect the first field to be empty since it has no umbrella program type.<br/>
     *            6. duration, version after 3.2 has "|" to separate between sub-episodes, it starts from the umbrella episode duration and follows by each sub-episode's duration.<br/>
     *            7. programThumbnailUrl, version after 3.2 has "|" to separate between sub-episodes, it starts from the umbrella episode image and follows by each sub-episode's image.<br/>
     *            8. programLargeThumbnailUrl, version after 3.2 has "|" to separate between sub-episodes, rule same as programThumbnailUrl<br/>
     *            9. url1(mpeg4/slideshow), version after 3.2 has "|" to separate between videos. 
     *            Each unit starts with a video url, separated by ";" is the start time (unit is seconds), 
     *            after the next ";" is the end play time of the video. Expect the first field to be empty since there is no umbrella video url.
     *            Example: |http://www.youtube.com/watch?v=TDpqS6GS_OQ;50;345|http://www.youtube.com/watch?v=JcmKq9kmP5U;0;380<br/> 
     *            10. url2(webm), reserved<br/> 
     *            11. url3(flv more likely), reserved<br/>
     *            12. url4(audio), reserved<br/> 
     *            13. publish date timestamp, version before 3.2 stops here<br/>
     *            14. reserved<br/>
     *            15. title card <br/>
     *            16. poi information, each poi is separated by "|". 
     *            Each poi unit starts with a number, to indicate what sub-episode it is in; follows start time; end time; poi type; urlencode json context. 
     *            The information within the poi unit is separated by ";".
     *            Currently there's only one poi type which is hyper link. This info is also included in the context.
     *            <br/>
     */        
    @RequestMapping(value="programInfo", produces = "text/plain; charset=utf-8")
    public @ResponseBody String programInfo(
            @RequestParam(value="v", required=false) String v,
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
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {            	
                return playerApiService.assembleMsgs(status, null);
            }         
            boolean isUserInfo = Boolean.parseBoolean(userInfo);
            output =  playerApiService.programInfo(channelIds, userToken, ipgId, isUserInfo, sidx, limit, req);
        } catch (Exception e){
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
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
    @RequestMapping(value="channelSubmit", produces = "text/plain; charset=utf-8")
    public @ResponseBody String channelSubmit(HttpServletRequest req) {
        String url = req.getParameter("url") ;
        String userToken= req.getParameter("user");
        String grid = req.getParameter("grid");
        String categoryIds = req.getParameter("category");
        String tags = req.getParameter("tag");
        String lang = req.getParameter("lang");
        String rx = req.getParameter("rx");
        
        log.info("player input - userToken=" + userToken+ "; url=" + url + 
                 ";grid=" + grid + ";categoryId=" + categoryIds +
                 ";rx=" + rx + ";tags" + tags + ";lang=" + lang);
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);        
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);                        
            }
            output = playerApiService.channelSubmit(categoryIds, userToken, url, grid, tags, lang, req);
        } catch (Exception e){
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
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
     *         0    success <br/>
     *         --<br/>
     *         token    QQl0l208W2C4F008980F<br/>
     *         name    c<br/>
     *         lastLogin    1300822489194<br/>
     */    
    @RequestMapping(value="login", produces = "text/plain; charset=utf-8")
    public @ResponseBody String login(HttpServletRequest req, HttpServletResponse resp) {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        @SuppressWarnings("unused")
        String mso = req.getParameter("mso");
        String rx = req.getParameter("rx");
        log.info("login: email=" + email + ";rx=" + rx);        
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);        
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {            	
                return playerApiService.assembleMsgs(status, null);
            }                                                
            output = playerApiService.login(email, password, req, resp);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
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
    @RequestMapping(value="setUserPref", produces = "text/plain; charset=utf-8")
    public @ResponseBody String setUserPref(
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
                return playerApiService.assembleMsgs(status, null);                        
            }
            output = playerApiService.setUserPref(user, key, value);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Change subscription's set(group) name.
     * 
     * @param user user token
     * @param name set name
     * @param pos set position, from 1 to 9 
     * @return status
    */
    @RequestMapping(value="setSetInfo", produces = "text/plain; charset=utf-8")
    public @ResponseBody String setSetInfo (
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
                return playerApiService.assembleMsgs(status, null);
            }
            output = playerApiService.setSetInfo(userToken, name, pos);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }    

    /**
     * Static content for help or general section
     * 
     * @param key key name to retrieve the content
     * @param lang en or zh
     * @return static content
     */
    @RequestMapping(value="staticContent", produces = "text/plain; charset=utf-8")
    public @ResponseBody String staticContent(
            @RequestParam(value="key", required=false) String key,
            @RequestParam(value="lang", required=false) String lang,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {                                                
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {            	
                return playerApiService.assembleMsgs(status, null);
            }            
            output = playerApiService.staticContent(key, lang);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Register a device. Will set a "device" cookie if registration is successful.
     * 
     * @param user user token, optional. will bind to device if user token is provided.
     * @return device token
     */
    @RequestMapping(value="deviceRegister", produces = "text/plain; charset=utf-8")
    public @ResponseBody String deviceRegister(
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
                return playerApiService.assembleMsgs(status, null);                        
            }
            output = playerApiService.deviceRegister(userToken, type, req, resp);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    
    /**
     * Used for version before 3.2.
     * List recommendation sets.
     * 
     * @return <p>lines of set info.
     *         <p>Set info includes set id, set name, set description, set image, set channel count. Fields are separated by tab.          
     */        
    @RequestMapping(value="listRecommended", produces = "text/plain; charset=utf-8")
    public @ResponseBody String listRecommended(
            @RequestParam(value="lang", required=false) String lang,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {                                                
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {            	
                return playerApiService.assembleMsgs(status, null);
            }                                                            
            output = playerApiService.listRecommended(lang);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;      
    }

    /**
     * Verify device token
     *  
     * @param device device token
     * @return user token, user name, user email if any. multiple entries will be separated by \n
     */
    @RequestMapping(value="deviceTokenVerify", produces = "text/plain; charset=utf-8")
    public @ResponseBody String deviceTokenVerify(
            @RequestParam(value="device", required=false) String token,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        log.info("user:" + token);
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {            	
                return playerApiService.assembleMsgs(status, null);
            }                                                            
            output = playerApiService.deviceTokenVerify(token, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Bind a user to device
     * 
     * @param device device token
     * @param user user token
     * @return status
     */
    @RequestMapping(value="deviceAddUser", produces = "text/plain; charset=utf-8")
    public @ResponseBody String deviceAddUser(
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
                return playerApiService.assembleMsgs(status, null);                        
            }
            output = playerApiService.deviceAddUser(deviceToken, userToken, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    
    /**
     * Unbind a user from the device
     * 
     * @param device device token
     * @param user user token
     * @return status
     */
    @RequestMapping(value="deviceRemoveUser", produces = "text/plain; charset=utf-8")
    public @ResponseBody String deviceRemoveUser(
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
                return playerApiService.assembleMsgs(status, null);            
            output = playerApiService.deviceRemoveUser(deviceToken, userToken, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }    

    /**
     * For users to report anything. Either user or device needs to be provided.
     * User report content can either use "comment" (version before 3.2), or "key" and "value" pair.
     * 
     * @param user user token
     * @param device device token
     * @param session session id, same as pdr session id
     * @param type type of report, not required, examples like feedback, sales, problem
     * @param comment user's problem description. Can be omitted if use key/value.
     * @param key used with value, like key and value pair
     * @param value used with key
     * @return report id
     */
    @RequestMapping(value="userReport", produces = "text/plain; charset=utf-8")
    public @ResponseBody String userReport(
            @RequestParam(value="user", required=false) String user,
            @RequestParam(value="device", required=false) String device,
            @RequestParam(value="session", required=false) String session,
            @RequestParam(value="comment", required=false) String comment,
            @RequestParam(value="type", required=false) String type,
            @RequestParam(value="key", required=false) String item,
            @RequestParam(value="value", required=false) String value,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        log.info("user:" + user + ";session=" + session);
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);        
            }            
            String query = req.getQueryString();
            String[] params = query.split("&");
            for (String param : params) {
                String[] pairs = param.split("=");
                if (pairs.length > 1 && pairs[0].equals("comment"))
                    comment = pairs[1];
                if (pairs.length > 1 && pairs[0].equals("value"))
                    value = pairs[1];
            }
            
            if (value != null)
                comment = value;
            output = playerApiService.userReport(user, device, session, type, item, comment, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Set user profile information. Facebook users will be turned down for most of the options.
     * 
     * @param user user token
     * @param <p>key keys include "name", "email", "gender", "year", "sphere", "ui-lang", "password", "oldPassword", "description", "image". <br/> 
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
    //localhost:8080/playerAPI/setUserProfile?user=8s12689Ns28RN2992sut&key=description,lang&value=hello%2C妳好,en
    @RequestMapping(value="setUserProfile", produces = "text/plain; charset=utf-8")
    public @ResponseBody String setUserProfile(
            @RequestParam(value="user", required=false)String user,
            @RequestParam(value="key", required=false)String key,
            @RequestParam(value="value", required=false)String value,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {       
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);        
            }            
            String query = req.getQueryString();
            String[] params = query.split("&");
            for (String param : params) {
                String[] pairs = param.split("=");
                if (pairs.length > 1 && pairs[0].equals("value"))
                    value = pairs[1];
            }
            log.info("set user profile: key(" + key + ");value(" + value + ")");
            output = playerApiService.setUserProfile(user, key, value, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }    

    /**
     * Request to reset the password. System will send out an email to designated email address. 
     *  
     * @param email user email
     * @return status
     */
    @RequestMapping(value="forgotpwd", produces = "text/plain; charset=utf-8")
    public @ResponseBody String forgotpwd(
            @RequestParam(value="email", required=false)String email,
            HttpServletRequest req,
            HttpServletResponse resp) {        
        log.info("forgot password email:" + email);
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);        
            }
            output = playerApiService.forgotpwd(email, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }    

    /**
     * Reset password
     * 
     * @param token token in the email
     * @param email user's email
     * @param password user's new password
     * @return status
     */
    @RequestMapping(value="resetpwd", produces = "text/plain; charset=utf-8")
    public @ResponseBody String resetpwd(
            @RequestParam(value="email", required=false)String email,
            @RequestParam(value="token", required=false)String token,
            @RequestParam(value="password", required=false)String password,
            HttpServletRequest req,
            HttpServletResponse resp) {        
        log.info("reset password email:" + token);
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);        
            }
            output = playerApiService.resetpwd(email, token, password, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }    
    
    /**
     * Get user profile information
     * 
     * @param user user token
     * @return <p>Data returns in key and value pair. Key and value is tab separated. Each pair is \n separated.<br/>
     *            keys include "name", "email", "gender", "year", "sphere" "ui-lang", "description", "image"</p>
     *         <p>Example<br/>: name John <br/>email john@example.com<br/>ui-lang en                 
     */    
    @RequestMapping(value="getUserProfile", produces = "text/plain; charset=utf-8")
    public @ResponseBody String getUserProfile(
            @RequestParam(value="user", required=false)String user,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {
                return playerApiService.assembleMsgs(status, null);        
            }            
            output = playerApiService.getUserProfile(user);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
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
    @RequestMapping(value="shareByEmail", produces = "text/plain; charset=utf-8")
    public @ResponseBody String shareByEmail(
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
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {
                return playerApiService.assembleMsgs(status, null);        
            }            
            output = playerApiService.shareByEmail(userToken, toEmail, toName, subject, content, captcha, text, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Request captcha for later verification
     * 
     * @param user user token 
     * @param action action 1 is used for signup. action 2 is used for shareByEmail
     * @return status
     */
    @RequestMapping(value="requestCaptcha", produces = "text/plain; charset=utf-8")
    public @ResponseBody String requestCaptcha(
            @RequestParam(value="user", required=false) String token,
            @RequestParam(value="action", required=false) String action,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        log.info("user:" + token);
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);        
            }            
            output = playerApiService.requestCaptcha(token, action, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Save user's channel sorting sequence
     * 
     * @param user user token
     * @param channel channel id
     * @param sorting sorting sequence. NEWEST_TO_OLDEST = 1, OLDEST_TO_NEWEST=2  
     * @return status
     */        
    @RequestMapping(value="saveSorting", produces = "text/plain; charset=utf-8")
    public @ResponseBody String saveSorting(
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
                return playerApiService.assembleMsgs(status, null);        
            output = playerApiService.saveSorting(userToken, channelId, sorting);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * @deprecated
     * Save User Sharing
     *
     * @param user user's unique identifier
     * @param channel channel id
     * @param program program id
     * @param set set id (place holder for now)
     * @return A unique sharing identifier
     */
    @RequestMapping(value="saveShare", produces = "text/plain; charset=utf-8")
    public @ResponseBody String saveShare(
            @RequestParam(value="user", required=false) String userToken, 
            @RequestParam(value="channel", required=false) String channelId,
            @RequestParam(value="set", required=false) String setId,
            @RequestParam(value="program", required=false) String programId,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {        
        
        log.info("saveShare(" + userToken + ")");
        return playerApiService.assembleMsgs(NnStatusCode.API_DEPRECATED, null);
        /*
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);        
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS)
                return playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null);    
            output = playerApiService.saveShare(userToken, channelId, programId, setId);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
        */        
    }    

    /**
     * @deprecated
     * Load User Sharing
     *
     * @param id unique identifier from saveShare
     * @return  Returns a program to play follows channel information.
     *             The program to play returns in the 2nd section, format please reference programInfo format.
     *          3rd section is channel information, format please reference channelLineup.
     */
    @RequestMapping(value="loadShare", produces = "text/plain; charset=utf-8")
    public @ResponseBody String loadShare(
            @RequestParam(value="id") Long id, 
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req) {        
        log.info("ipgShare:" + id);        
        return playerApiService.assembleMsgs(NnStatusCode.API_DEPRECATED, null);
        /*
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);        
        try {
            this.prepService(req, true);
            output = playerApiService.loadShare(id);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
        */                        
    }

    /**
     *  Return personal history, for streaming portal version
     * 
     * @param user user token
     * @param mso mso name, default is 9x9 if not given
     * @return list of channels. Reference channelLineup.
     */
    @RequestMapping(value="personalHistory", produces = "text/plain; charset=utf-8")
    public @ResponseBody String personalHistory(
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="mso", required=false) String mso,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);        
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {
                return playerApiService.assembleMsgs(status, null);        
            }            
            output = playerApiService.personalHistory(userToken);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;                
    }
    
    /**
     * @deprecated
     * User's recently watched channel and its episode.
     * 
     * @param user user token
     * @param count number of recently watched entries
     * @param channelInfo true or false
     * @param episodeIndex true or false. if episodeIndex = true, count has to be less 5.
     * @return Fist block: Lines of channel id and program id.<br/>
     *         Second block: if channelInfo is set to true, detail channel information will be returned. Please reference channelLineup for format.
     */
    @RequestMapping(value="recentlyWatched", produces = "text/plain; charset=utf-8")
    public @ResponseBody String recentlyWatched(
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="count", required=false) String count,
            @RequestParam(value="channel", required=false) String channel,
            @RequestParam(value="channelInfo", required=false) String channelInfo,
            @RequestParam(value="episodeIndex", required=false) String episodeIndex,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req) {                                                
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);        
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);        
            }            
            boolean isChannelInfo = Boolean.parseBoolean(channelInfo);
            boolean isEpisodeIndex = Boolean.parseBoolean(episodeIndex);
            output = playerApiService.userWatched(userToken, count, isChannelInfo, isEpisodeIndex, channel);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
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
    @RequestMapping(value="copyChannel", produces = "text/plain; charset=utf-8")
    public @ResponseBody String copyChannel(
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
                return playerApiService.assembleMsgs(status, null);
            output = playerApiService.copyChannel(userToken, channelId, grid);
        } catch (Exception e){
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }    
        return output;        
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
    @RequestMapping(value="moveChannel", produces = "text/plain; charset=utf-8")
    public @ResponseBody String moveChannel(
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
                return playerApiService.assembleMsgs(status, null);
            output = playerApiService.moveChannel(userToken, grid1, grid2);
        } catch (Exception e){
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }    
        return output;        
    }                    
    
    /**
     * Search channel name and description, curator name and description
     * 
     * @param search search text
     * @param start start index
     * @param count number of records returned. Returns 9 if not specified.
     * @return matched channels and curators
     *         <p> 
     *         For version before 3.2, search returns a list of channel info. Please reference channelLineup. 
     *         For version 3.2, search returns in 5 blocks. Describes as follows.
     *         <p>
     *         First block: general statistics. Format in the following paragraph <br/>
     *         Second block: list of curators. Please reference curator api <br/>
     *         Third block: curatos' channels. Please reference channelLineup api.<br/>
     *         Forth block: List of matched channels. Please reference channelLineup api<br/>
     *         Fifth block: List of suggested channels. It will only return things when there's no match of curator and channel.<br/>              
     *         <p>  
     *         General statistics: (item name : number of return records : total number of records)<br/>
     *         curator    4    4 <br/> 
     *         channel    2    2 <br/>
     *         suggestion    0  0 <br/> 
 
     */
    @RequestMapping(value="search", produces = "text/plain; charset=utf-8")
    public @ResponseBody String search(
            @RequestParam(value="text", required=false) String text,
            @RequestParam(value="start", required=false) String start,
            @RequestParam(value="content", required=false) String content,
            @RequestParam(value="count", required=false) String count,            
            @RequestParam(value="stack", required=false) String stack,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {
                return playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null);        
            }            
            output = playerApiService.search(text, stack, content, start, count, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    
    /**
     * Mark a program bad when player sees it 
     * 
     * @param user user token
     * @param program programId
     */    
    @RequestMapping(value="programRemove", produces = "text/plain; charset=utf-8")
    public @ResponseBody String programRemove(
            @RequestParam(value="program", required=false) String programId,
            @RequestParam(value="youtube", required=false) String ytVideoId,
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="bird", required=false) String secret,
            @RequestParam(value="status", required=false) String status,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        log.info("bad program:" + programId + ";reported by user:" + userToken);
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int systemStatus = this.prepService(req, true);
            if (systemStatus != NnStatusCode.SUCCESS)
                return playerApiService.assembleMsgs(systemStatus, null);
            output = playerApiService.programRemove(programId, ytVideoId, userToken, secret, status);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Create a 9x9 channel. To be ignored.
     * 
     * @param name name
     * @param description description
     * @param image image url
     * @param temp not specify means false 
     */    
    @RequestMapping(value="channelCreate", produces = "text/plain; charset=utf-8")
    public @ResponseBody String channelCreate(
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
                return playerApiService.assembleMsgs(status, null);    
            boolean isTemp= Boolean.parseBoolean(temp);        
            output = playerApiService.channelCreate(user, name, description, image, isTemp);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Create a 9x9 program. To be ignored.
     * 
     * @param channel channel id
     * @param name name
     * @param image image url
     * @param description description
     * @param audio audio url
     * @param video video url
     * @param temp not specify means false 
     */    
    @RequestMapping(value="programCreate", produces = "text/plain; charset=utf-8")
    public @ResponseBody String programCreate(
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
                return playerApiService.assembleMsgs(status, null);    
            boolean isTemp= Boolean.parseBoolean(temp);        
            output = playerApiService.programCreate(channel, name, description, image, audio, video, isTemp);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    
    /**
     * Set program properties. To be ignored.
     * 
     * @param program program id
     * @param property program property
     * @param value program property value
     */    
    @RequestMapping(value="setProgramProperty", produces = "text/plain; charset=utf-8")
    public @ResponseBody String setProgramProperty(
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
                return playerApiService.assembleMsgs(status, null);                        
            output = playerApiService.setProgramProperty(program, property, value);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Set channel property. To be ignored.
     * 
     * @param channel channel id
     * @param property channel property
     * @param value channel property value
     */    
    @RequestMapping(value="setChannelProperty", produces = "text/plain; charset=utf-8")
    public @ResponseBody String setChannelProperty(
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
                return playerApiService.assembleMsgs(status, null);                        
            output = playerApiService.setChannelProperty(channel, property, value);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Mix of account authentication and directory listing  
     * 
     * If token is provided, will do userTokenVerify.
     * If token is not provided, will do login
     * If token and email/password is not provided, will do guestRegister.
     *  
     * @param token if not empty, will do userTokenVerify
     * @param email if token is not provided, will do login check with email and password
     * @param password password 
     * @return please reference api introduction
     */
    @RequestMapping(value="quickLogin", produces = "text/plain; charset=utf-8")
    public @ResponseBody String quickLogin(
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
                return playerApiService.assembleMsgs(status, null);                        
            output = playerApiService.quickLogin(token, email, password, req, resp);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;                 
    }

    /**
     * Mix of account authentication and directory listing  
     * 
     * If token is provided, will do userTokenVerify.
     * If token is not provided, will do login
     * If token and email/password is not provided, will do guestRegister.
     *  
     * @param token if not empty, will do userTokenVerify
     * @param email if token is not provided, will do login check with email and password
     * @param password password 
     * @return please reference api introduction
     */
    @RequestMapping(value="auxLogin", produces = "text/plain; charset=utf-8")
    public @ResponseBody String auxLogin(
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
                return playerApiService.assembleMsgs(status, null);                        
            output = playerApiService.auxLogin(token, email, password, req, resp);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;                 
    }
    
    /**
     * Mix of sphere related content listing 
     *   
     * @param token if not empty, will do userTokenVerify
     * @param email if token is not provided, will do login check with email and password
     * @param password password 
     * @param rx rx
     * @return please reference api introduction
     */    
    @RequestMapping(value="sphereData", produces = "text/plain; charset=utf-8")
    public @ResponseBody String sphereData(
            @RequestParam(value="token", required=false) String token,
            @RequestParam(value="email", required=false) String email,
            @RequestParam(value="password", required=false) String password,            
            @RequestParam(value="rx", required = false) String rx,            
            HttpServletRequest req,
            HttpServletResponse resp) {        
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE)
                return playerApiService.assembleMsgs(status, null);                        
            output = playerApiService.sphereData(token, email, password, req, resp);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;                 
    }
    
    /**
     * iOS flipr demo feature. Users search. 
     * 
     * @param email email address
     * @param name user name
     */
    @RequestMapping(value="graphSearch", produces = "text/plain; charset=utf-8")
    public @ResponseBody String graphSearch(
            @RequestParam(value="email", required=false) String email,            
            @RequestParam(value="name", required=false) String name,
            @RequestParam(value="rx", required = false) String rx,            
            HttpServletRequest req,
            HttpServletResponse resp) {        
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);        
            }            
            output = playerApiService.graphSearch(email, name);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }            
        return output;
    }

    /**
     * iOS flipr demo feature. Invite users
     * 
     * @param userToken user token
     * @param toEmail invitee email
     * @param toName invitee name
     * @param channel channel id
     * @return status
     */
    @RequestMapping(value="userInvite", produces = "text/plain; charset=utf-8")
    public @ResponseBody String userInvite(
            @RequestParam(value="user", required=false) String userToken,                                        
            @RequestParam(value="toEmail", required=false) String toEmail,
            @RequestParam(value="toName", required=false) String toName,
            @RequestParam(value="channel", required=false) String channel,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);        
            }            
            output = playerApiService.userInvite(userToken, toEmail, toName, channel, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * iOS flipr demo feature, search users. Check user invitation status.
     * 
     * @param token invitation token
     * @return status
     */
    @RequestMapping(value="inviteStatus", produces = "text/plain; charset=utf-8")
    public @ResponseBody String inviteStatus(
            @RequestParam(value="token", required=false) String token,                                        
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status,  null);        
            }            
            output = playerApiService.inviteStatus(token);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * iOS flipr demo feature, search users. Check invite status
     * 
     * @param userToken user token
     * @param toEmail disconnect email
     * @param channel channel id
     * @return status
     */
    @RequestMapping(value="disconnect", produces = "text/plain; charset=utf-8")
    public @ResponseBody String disconnect(
            @RequestParam(value="user", required=false) String userToken,                                        
            @RequestParam(value="toEmail", required=false) String toEmail,
            @RequestParam(value="channel", required=false) String channel,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);        
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status,  null);        
            }                        
            output = playerApiService.disconnect(userToken, toEmail, channel, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * iOS flipr demo feature, search users. Notify subscribers with channel status.
     * 
     * @param userToken user token
     * @param channel channel id
     * @return status
     */
    @RequestMapping(value="notifySubscriber", produces = "text/plain; charset=utf-8")
    public @ResponseBody String notifySubscriber(
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="channel", required=false) String channel,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status,  null);        
            }                        
            output = playerApiService.notifySubscriber(userToken, channel, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    /**
     * Curator info. Use "curator" to get specific curator information.
     * Or specify stack = featured to get list of featured curators. 
     * 
     * @param curator curator id
     * @param stack if specify "featued" will return list of featured curators
     * @param profile curator's 9x9 url
     * @return list of curator information. <br/>
     *         First block: <br/>
     *         curator id, <br/>
     *         curator name,<br/>
     *         curator description<br/> 
     *         curator image url,<br/>
     *         curator profile url,<br/>
     *         channel count, (channel count curator create) <br/>
     *         channel subscription count, <br/>
     *         follower count, <br/> 
     *         top channel id <br/>
     *         <p> Second block: <br/>
     *         Reference channelLineup
     */
    @RequestMapping(value="curator", produces = "text/plain; charset=utf-8")
    public @ResponseBody String curator(
            @RequestParam(value="curator", required=false) String profile,             
            @RequestParam(value="user", required=false) String user,
            @RequestParam(value="stack", required=false) String stack,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {
                return playerApiService.assembleMsgs(status,  null);        
            }                        
            output = playerApiService.curator(profile, user, stack, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    /**
     * <p> Record user's favorite channel and episode.</p>
     * <p> For non-Youtube episode, supply channel and program id. 
     * Supply the rest (video, name, image, duration, channelid) for Youtube channels.
     * </p> 
     * <p> delete equalst to true will delete the exising favorite program.
     * For deletion, user and program is expected.
     * 
     * @param user user token
     * @param channel channel id
     * @param program program id
     * @param video youtube video url
     * @param name video name
     * @param image image url
     * @param delete true or false. default is false
     * @param req
     * @param resp
     * @return status
     */
    @RequestMapping(value="favorite", produces = "text/plain; charset=utf-8")
    public @ResponseBody String favorite(
            @RequestParam(value="user", required=false) String user,
            @RequestParam(value="channel", required=false) String channel,
            @RequestParam(value="program", required=false) String program,
            @RequestParam(value="video", required=false) String fileUrl,
            @RequestParam(value="name", required=false) String name,
            @RequestParam(value="image", required=false) String imageUrl,            
            @RequestParam(value="duration", required=false) String duration,
            @RequestParam(value="delete", required=false) String delete,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            boolean del = Boolean.parseBoolean(delete);
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status,  null);        
            }                        
            output = playerApiService.favorite(user, channel, program, fileUrl, name, imageUrl, duration, del);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    
    /**
     * Link of facebook login
     */
    @RequestMapping(value="fbLogin")
    public String fbLogin(HttpServletRequest req,            
            @RequestParam(value="mso", required=false) String mso) {
        if (mso == null) 
            mso = "9x9"; 
        String uri = "";
        try {
            uri= new URI(req.getHeader("referer")).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        log.info("uri:" + uri);
        uri = uri.replaceAll("\\/", "_");
        log.info("rewrite uri:" + uri);
        String url = FacebookLib.getDialogOAuthPath(uri);
        String userCookie = CookieHelper.getCookie(req, CookieHelper.USER);
        log.info("FACEBOOK: user:" + userCookie + " redirect to fbLogin:" + url);
        return "redirect:" + url;
    }

    /**
     * @deprecated 
     */
    @RequestMapping("flush")
    public ResponseEntity<String> flush() {
        MemcachedClient cache = CacheFactory.getClient();
        if (cache != null) {
            try {
                cache.flush().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            cache.shutdown();
        }
        return NnNetUtil.textReturn("flush");
    }
 
    /**
     * @deprecated 
     */
    @RequestMapping(value="episodeUpdate", produces = "text/plain; charset=utf-8")
    public @ResponseBody String episodeUpdate(
            @RequestParam(value="epId", required=false) long epId ) {
        NnEpisodeManager mngr = new NnEpisodeManager();
        NnEpisode e = new NnEpisodeManager().findById(epId);
        if (e != null) {
            int duration = mngr.calculateEpisodeDuration(e);
            log.info("new duration:" + duration);
            e.setDuration(duration);
            mngr.save(e);
        }
        return "OK";                
    }
 
    /**
     * Get list of episodes based on channel stack. Used by Android device.
     *  
     * @param stack leagle value includes "recommend", "hot", "mayLike", "featured", "trending"
     * @param lang
     * @param userToken
     * @param channel
     * @return Reference channelLineup and programInfo. Work in progress.
     */
    @RequestMapping(value="virtualChannel", produces = "text/plain; charset=utf-8")
    public @ResponseBody String virtualChannel(
            @RequestParam(value="stack", required=false) String stack,
            @RequestParam(value="lang", required=false) String lang,
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="channel", required=false) String channel,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {        
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {
                playerApiService.assembleMsgs(NnStatusCode.DATABASE_READONLY, null);                        
            }
            output = playerApiService.virtualChannel(stack, lang, userToken, channel, true);    
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    @RequestMapping(value="latestEpisode", produces = "text/plain; charset=utf-8")
    public @ResponseBody String latestEpisode(
            @RequestParam(value="channel", required=false) String channel,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {        
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                playerApiService.assembleMsgs(status, null);                        
            }
            output = playerApiService.latestEpisode(channel);    
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    
    /**
     * Used by Android device. Things to list on the front page
     * 
     * @param time hour, 0-23
     * @param stack reserved
     * @param user user token
     * @return <p>Two sections, First is things to disply, see the following. 
     *            Second is the list of episodes, please reference VirtualChannel.
     *         <p>Things to display: name, type(*1), stack name, default open(1) or closed(0), icon   
     *         <p>*1: 0 stack, 1 subscription, 2 account, 3 channel, 4 directory, 5 search  
     */
    @RequestMapping(value="frontpage", produces = "text/plain; charset=utf-8")
    public @ResponseBody String frontpage(                      
            @RequestParam(value="time", required=false) String time,
            @RequestParam(value="stack", required=false) String stack,
            @RequestParam(value="user", required=false) String user,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status == NnStatusCode.API_FORCE_UPGRADE) {
                playerApiService.assembleMsgs(status, null);                        
            }
            output = playerApiService.frontpage(time, stack, user);    
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;        
    }

    /**
     * Streaming portal API. It returns list of sets; channel list of the first set; first episode of every channel in the first set.
     * 
     * @param time hour, 0-23, required
     * @param lang en or zh. default is en
     * @return <p>Three sections, First is sets. Format please reference listRecommended. <br/> 
     *            Second is the list of channels of the first set from the first section. Format please reference chanenlLineup. <br/>
     *            Third is the first episode of every channel from the second section. Format please reference programInfo.
     */    
    @RequestMapping(value="portal", produces = "text/plain; charset=utf-8")
    public @ResponseBody String portal(                      
            @RequestParam(value="lang", required=false) String lang,
            @RequestParam(value="time", required=false) String time,
            @RequestParam(value="minimal", required=false) String minimal,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                playerApiService.assembleMsgs(status, null);                        
            }
            boolean isMinimal = Boolean.parseBoolean(minimal);
            output = playerApiService.portal(lang, time, isMinimal);    
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;        
    }
    
    
    @RequestMapping(value="bulkIdentifier", produces = "text/plain; charset=utf-8")
    public @ResponseBody String bulkIdentifier(                      
            @RequestParam(value="channelNames", required=false) String ytUsers,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                playerApiService.assembleMsgs(status, null);                        
            }
            ytUsers = req.getParameter("channelNames");
            output = playerApiService.bulkIdentifier(ytUsers);    
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;        
    }
    
    @RequestMapping(value="bulkSubscribe", produces = "text/plain; charset=utf-8")
    public @ResponseBody String bulkSubscribe(                      
            @RequestParam(value="channelNames", required=false) String ytUsers,
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="mso", required=false) String mso,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                playerApiService.assembleMsgs(status, null);                        
            }
            ytUsers = req.getParameter("channelNames");
            userToken = req.getParameter("user");
            output = playerApiService.bulkSubscribe(userToken, ytUsers);    
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;        
    }
    
    @RequestMapping(value="virtualChannelAdd", produces = "text/plain; charset=utf-8")
    public @ResponseBody String virtualChannelAdd(                      
            @RequestParam(value="user", required=false) String user,            
            @RequestParam(value="channel", required=false) String channel,
            @RequestParam(value="payload", required=false) String payload,            
            @RequestParam(value="queued", required = false) String queued,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {        
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                playerApiService.assembleMsgs(status, null);                        
            }
            boolean isQueued = Boolean.parseBoolean(queued);
            if (queued == null) isQueued = true;
            log.info("in queue?" + isQueued);  

            user = req.getParameter("user");
            channel = req.getParameter("channel");
            payload = req.getParameter("payload");
            
            output = playerApiService.virtualChannelAdd(user, channel, payload, isQueued, req);    
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;        
    }
 
    //used by android only, no cookie is set
    @RequestMapping(value="obtainAccount", produces = "text/plain; charset=utf-8")
    public @ResponseBody String obtainAccount(HttpServletRequest req, HttpServletResponse resp) {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        @SuppressWarnings("unused")
        String mso = req.getParameter("mso");
                
        log.info("signup: email=" + email + ";name=" + name); 
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS)
                return playerApiService.assembleMsgs(status, null);        
            output = playerApiService.obtainAccount(email, password, name, req, resp);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }    
 
    /**
     * Create a 9x9 channel. To be ignored.
     * 
     * @param name name
     * @param description description
     * @param image image url
     * @param temp not specify means false 
     */    
    @RequestMapping(value="channelUpdate", produces = "text/plain; charset=utf-8")
    public @ResponseBody String channelUpdate(
            @RequestParam(value="user", required=false) String user,
            @RequestParam(value="queued", required = false) String queued,            
            @RequestParam(value="payload", required=false) String payload,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {

        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);        
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS)
                return playerApiService.assembleMsgs(status, null);    
            boolean isQueued = Boolean.parseBoolean(queued);
            if (queued == null) isQueued = true;
            log.info("in queue?" + isQueued);
            user = req.getParameter("user");
            payload = req.getParameter("payload");
            output = playerApiService.channelUpdate(user, payload, isQueued, req);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
 
    @RequestMapping(value="endpointRegister", produces = "text/plain; charset=utf-8")
    public @ResponseBody String endpointRegister(
            @RequestParam(value="action", required=false) String action,
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="mso", required=false) String mso,
            @RequestParam(value="device", required=false) String device,
            @RequestParam(value="vendor", required=false) String vendor,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);                        
            }
            output = playerApiService.endpointRegister(userToken, device, vendor, action);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }

    //http://www.9x9.tv/poiaction?poiId={}&userId={}&select={}
    //{msg:"", duration:0} 
    @RequestMapping(value="poiAction", produces = "text/plain; charset=utf-8")
    public @ResponseBody String poiAction(
            @RequestParam(value="poi", required=false) String poiId,
            @RequestParam(value="user", required=false) String userToken,
            @RequestParam(value="device", required=false) String deviceToken,
            @RequestParam(value="vendor", required=false) String vendor,
            @RequestParam(value="select", required=false) String select,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            int status = this.prepService(req, true);
            if (status != NnStatusCode.SUCCESS) {
                return playerApiService.assembleMsgs(status, null);                        
            }
            output = playerApiService.poiAction(userToken, deviceToken, vendor, poiId, select);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    
    /*
    @RequestMapping(value="solr", produces = "text/plain; charset=utf-8")
    public @ResponseBody String solr(
            @RequestParam(value="text", required=false) String text,
            @RequestParam(value="rx", required = false) String rx,
            HttpServletRequest req,
            HttpServletResponse resp) {
        String output = NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, locale);
        try {
            this.prepService(req, true);
            output = playerApiService.solr(text);
        } catch (Exception e) {
            output = playerApiService.handleException(e);
        } catch (Throwable t) {
            NnLogUtil.logThrowable(t);
        }
        return output;
    }
    */
}