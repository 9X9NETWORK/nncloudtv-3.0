package com.nncloudtv.web;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.CounterShard;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.service.DepotService;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnStatusMsg;
import com.nncloudtv.service.PlayerService;
import com.nncloudtv.web.api.NnStatusCode;
import com.nncloudtv.web.json.transcodingservice.Channel;
import com.nncloudtv.web.json.transcodingservice.ChannelInfo;
import com.nncloudtv.web.json.transcodingservice.PostResponse;
import com.nncloudtv.web.json.transcodingservice.RtnChannel;
import com.nncloudtv.web.json.transcodingservice.RtnProgram;


/**
 * <p>Serves for Transcoding Service.</p>
 * <p>Url examples: (method name is used at the end of URL) <br/> 
 * http://hostname:port/podcastAPI/itemUpdate<br/>
 * http://hostname:port/podcastAPI/channelUpdate<br/>
 * <p/>
 * <p>Flow: <br/>
 * (1) nnsmvo notify transcoding service a new podcast channel <br/>
 * (2) transcoding service returns channel metadata via channelUpdate <br/>
 * (3) transcoding service returns program metadata via itemUpdate. (the episode is ready with MP4 format and basic metadata)<br/>
 * (4) transcoding service returns additional program metadata via itemUpdate. (webm is supported) <br/> 
 * </p>
 */
@Controller
@RequestMapping("podcastAPI")
public class DepotController {

    protected static final Logger log = Logger.getLogger(DepotController.class.getName());
    
    private DepotService depotService;
    
    @Autowired
    public DepotController(DepotService depotService) {
        this.depotService = depotService;
    }
    
    @ExceptionHandler(Exception.class)
    public @ResponseBody PostResponse exception(Exception e, HttpServletRequest req, HttpServletResponse resp) {
        if (e.getClass().equals(HttpMediaTypeNotSupportedException.class)) {
            log.info("HttpMediaTypeNotSupportedException");
        }
        NnLogUtil.logException(e);
        PostResponse post = new PostResponse();
        post.setErrorCode(String.valueOf(NnStatusCode.ERROR));
        post.setErrorReason("error");
        resp.setStatus(200);
        return post;
    }        
    
    /**
     * Transcoding Service update Podcast Program information
     * 
     * @param podcastProgram podcastProgram returns in Json format <br/>
     * {<br/>
     * "action":"updateItem",<br/>
     * "key":"channel_key_id",<br/>
     * "errorCode":0, <br/>
     * "errorReason":"error description", <br/>        
     * "item": [ <br/>        
     *   {<br/>
     *     "title":"title", <br/>
     *     "description":"description", <br/>
     *     "pubDate","pubDate", <br/>
     *     "enclosure":"video_url",    <br/>    
     *     "type":"mp4",<br/>
     *   }<br/>
     *} 
     * 
     * @return keys keys include channel key and item key <br/>
     *  {<br/>
      *     "key":"channel_key_id",<br/>
      *      "itemkey":"item_key_id",<br/>
     *  } 
     */
    @RequestMapping("itemUpdate")
    public ResponseEntity<String> itemUpdate(@RequestBody RtnProgram rtnProgram, HttpServletRequest req) {
        log.info(rtnProgram.toString());
        PostResponse resp = new PostResponse(
                String.valueOf(NnStatusCode.ERROR), NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, Locale.ENGLISH));        
        try {
            resp = depotService.updateProgram(rtnProgram);
        } catch (Exception e) {
            resp = depotService.handleException(e);
        }
        log.info(resp.getErrorCode());
        return NnNetUtil.textReturn("OK");
    }
    
    /**
     * Get Channel/Set Meta data (title,description,image)
     * 
     * This is used by Piwik to fetch channel/set info
     * 
     * @return json encoded key value pair
     */
    
    @RequestMapping("getMetaInfo")
    public @ResponseBody void getMetaInfo(Model model,
                                          HttpServletResponse resp,
                                          HttpServletRequest req,
                                          @RequestParam(required=false) String jsoncallback,
                                          @RequestParam(required=false) Long set,
                                          @RequestParam(required=false) Long ch) {
        
        PlayerService playerService = new PlayerService();
        
        ObjectMapper mapper = new ObjectMapper();
        
        if (set != null) {
            model.addAttribute("type", "set");
            model.addAttribute("id", String.valueOf(set));
        } else if (ch != null) {
            model = playerService.prepareChannel(model, String.valueOf(ch), resp);
            model.addAttribute("type", "ch");
            model.addAttribute("id", String.valueOf(ch));
        }
        
        try {
            OutputStream out = resp.getOutputStream();
            resp.setCharacterEncoding("UTF-8");
            resp.addDateHeader("Expires", System.currentTimeMillis() + 3600000);
            resp.addHeader("Cache-Control", "private, max-age=3600");
            if (jsoncallback == null) {
                resp.setContentType("application/json");
                mapper.writeValue(out, model.asMap());
            } else {
                resp.setContentType("application/x-javascript");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(baos, "UTF-8");
                writer.write(jsoncallback + "(");
                writer.close();
                baos.writeTo(out);
                
                baos = new ByteArrayOutputStream();
                writer = new OutputStreamWriter(baos, "UTF-8");
                log.info("encoding: " + writer.getEncoding());
                mapper.writeValue(writer, model.asMap());
                baos.writeTo(out);
                out.write(')');
                out.close();
            }
        } catch (Exception e) {
            NnLogUtil.logException(e);
        }
    }
    
    /** 
     * @param page
     * @param msoName * indicates to retrieve all the channels
     *                msoName indicates to retrieve a mso's Ipg
     * @return channel list
     */
    @RequestMapping("getChannelList")
    public @ResponseBody ChannelInfo getChannelList(
              @RequestParam(value="page", required=false)String page, 
                                                @RequestParam(value="msoName", required=false)String msoName,
                                                @RequestParam(value="type", required=false)String type,
                                                HttpServletRequest req) {
        ChannelInfo info = new ChannelInfo();
        List<NnChannel> channels = new ArrayList<NnChannel>();
        NnChannelManager channelMngr = new NnChannelManager();
        short srtType = 0;
        if (type== null)
            srtType = 0;//place holder
        else
            srtType = Short.parseShort(type);
        try {
            if (srtType == NnChannel.CONTENTTYPE_YOUTUBE_SPECIAL_SORTING) {
                channels = channelMngr.findByType(NnChannel.CONTENTTYPE_YOUTUBE_SPECIAL_SORTING);
            } else {
                channels = channelMngr.findMaples();
            }
            String[] transcodingEnv = depotService.getTranscodingEnv(req);        
            String callbackUrl = transcodingEnv[1];        
            List<Channel> cs = new ArrayList<Channel>();
            for (NnChannel c : channels) {
                cs.add(new Channel(String.valueOf(c.getId()), 
                                   c.getSourceUrl(), 
                                   c.getTranscodingUpdateDate(), 
                                   "0",
                                   String.valueOf(c.getCntSubscribe())));
            }
            log.info("maple channels:" + channels.size());
            info.setErrorCode(String.valueOf(NnStatusCode.SUCCESS));
            info.setErrorReason("Success");
            info.setChannels(cs);
            info.setCallBack(callbackUrl);
        } catch (Exception e) {
            PostResponse resp = depotService.handleException(e);
            info.setErrorCode(resp.getErrorCode());
            info.setErrorReason(resp.getErrorReason());
        }
        return info;
    }
        
    /**
     * Transcoding service update Podcast Channel Information
     * 
     * @param podcast podcast in json type <br/>
     * {  <br/>
     *    "action":"updateChannel", <br/>
     *    "key":"channel_key_id", <br/>
     *    "title":"channel_title", <br/>
     *    "description":"channel_description", <br/>    
     *    "pubDate":"channel_pubDate", <br/>
     *    "image":"channel_thumbnail",<br/>   
     *    "errorCode":0, <br/>
     *    "errorReason":"error description" <br/>     
     * } 
     */
    @RequestMapping("channelUpdate")
    public @ResponseBody PostResponse channelUpdate(@RequestBody RtnChannel podcast) {
        log.info(podcast.toString());
        PostResponse resp = new PostResponse(
            String.valueOf(NnStatusCode.ERROR), NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, Locale.ENGLISH));
        try {
            resp = depotService.updateChannel(podcast);
        } catch (Exception e) {
            resp = depotService.handleException(e);
        }
        return resp;
    }

    /**
     * Transcoding service update Podcast Channel Information
     * 
     * @param podcast podcast in json type <br/>
     * {  <br/>
     *    "action":"updateChannel", <br/>
     *    "key":"channel_key_id", <br/>
     *    "title":"channel_title", <br/>
     *    "description":"channel_description", <br/>    
     *    "pubDate":"channel_pubDate", <br/>
     *    "image":"channel_thumbnail",<br/>   
     *    "errorCode":0, <br/>
     *    "errorReason":"error description" <br/>     
     * } 
     */
    @RequestMapping("channelUpdateTest")
    public  @ResponseBody PostResponse channelUpdateTest(
            @RequestParam(value="key", required=false)String key,
            @RequestParam(value="title", required=false)String title            
            ) {
        RtnChannel podcast = new RtnChannel();
        podcast.setKey(key);
        podcast.setTitle(title);
        podcast.setErrorCode("0");
        this.channelUpdate(podcast);
        
        log.info(podcast.toString());
        PostResponse resp = new PostResponse(
                String.valueOf(NnStatusCode.ERROR), NnStatusMsg.getPlayerMsg(NnStatusCode.ERROR, Locale.ENGLISH));
        try {
            resp = depotService.updateChannel(podcast);
        } catch (Exception e) {
            resp = depotService.handleException(e);
        }
        return resp;
    }

    @RequestMapping(value="resetViewCntCache", produces = "text/plain; charset=utf-8")
    public @ResponseBody String resetViewCntCache() {
        MemcachedClient cache = null;
        try {
            cache = CacheFactory.getClient();
        } catch (OperationTimeoutException e) {
            log.info("memcache down");
            return "memcache error";
        }
        List<CounterShard> counters = ShardedCounter.getViewCounters();
        log.info("reset counters:" + counters.size());
        for (CounterShard c : counters) {
            if (cache != null) { 
                log.info("cache name:" + c.getCounterName() + "; value:" + c.getCount());
                cache.set(c.getCounterName(), CacheFactory.EXP_DEFAULT, String.valueOf(c.getCount()));
            }            
        }
        cache.shutdown();        
        return "OK";
    }
    
}
