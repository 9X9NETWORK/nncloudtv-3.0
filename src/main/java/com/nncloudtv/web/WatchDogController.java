package com.nncloudtv.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.dao.SysTagDao;
import com.nncloudtv.dao.SysTagDisplayDao;
import com.nncloudtv.dao.SysTagMapDao;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.model.SysTagMap;
import com.nncloudtv.model.Tag;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnProgramManager;
import com.nncloudtv.service.PlayerApiService;
import com.nncloudtv.service.TagManager;
import com.nncloudtv.web.api.NnStatusCode;

@Controller
@RequestMapping("wd")
public class WatchDogController {

    protected static final Logger log = Logger.getLogger(WatchDogController.class.getName());
    
    @RequestMapping(value="search")
    public ResponseEntity<String> search(HttpServletRequest req) {
       String text = req.getParameter("text");
       List<NnChannel> channels = NnChannelDao.searchTemp(text, false, 0, 100);
       String result = "";
       if (channels.size() > 0) {
    	   for (NnChannel c : channels) {
        	   result += "id:" + c.getId() + "; name:" + c.getName() + "; status:" + c.getStatus() + "\n";	       		   
    	   }
       } else {
    	   result = "no data";
       }
       return NnNetUtil.textReturn(result);
    }
    
    @RequestMapping(value="statusChange")
    public ResponseEntity<String> statusChange(HttpServletRequest req) {
       String chId = req.getParameter("channel");
       String status = req.getParameter("status");
       NnChannelDao dao = new NnChannelDao();
       NnChannel c = dao.findById(Long.parseLong(chId));
       String result = "";
       if (c != null) {
    	   c.setStatus(Short.parseShort(status));
    	   dao.save(c);
    	   result = "success";
       } else {
    	   result = "no channel found";
       }
       return NnNetUtil.textReturn(result);
    }    

    @RequestMapping(value="categoryChange")
    public ResponseEntity<String> storeChange(HttpServletRequest req) {
       String chId = req.getParameter("channel");
       String categoryId = req.getParameter("category");
       
       SysTagMapDao dao = new SysTagMapDao();
       SysTagDisplay display = new SysTagDisplayDao().findById(Long.parseLong(categoryId));
       String result = "";
       if (display != null) {       
	       SysTagMap map = dao.findSysTagMap(display.getSystagId(), Long.parseLong(chId));
	       if (map == null) {
	    	   NnChannel c = new NnChannelDao().findById(Long.parseLong(chId));
	    	   if (c != null) {
	    		   SysTag systag = new SysTagDao().findById(display.getSystagId());
	    		   if (systag == null) {
	    			   result = "category (systagId) is null";
	    		   } else {
	    			   result = "add";
		    		   log.info("channel is not null:" + c.getName() + ";systag id=" + systag.getId());
	    			   map = new SysTagMap(display.getSystagId(), c.getId());
	    			   dao.save(map);
	    		   }
	    	   } else {
	    		   result = "channel is null";
	    	   }
	       } else {
	    	   result = "delete";
	    	   dao.delete(map);
	       }
       } else {
    	   result = "category (systag display) is null";
       }
       return NnNetUtil.textReturn(result);
    }    
    
    @RequestMapping(value="urlSubmit")
    public ResponseEntity<String> urlSubmit(HttpServletRequest req) {
       String url = req.getParameter("url") ;
       String lang = req.getParameter("lang") ;
       if (url == null)
          return NnNetUtil.textReturn("error\nurl empty");
       if (lang == null)
          lang = "en";
       url = url.trim();               
       NnChannelManager chMngr = new NnChannelManager();               
       url = chMngr.verifyUrl(url); //verify url, also converge youtube url         
       if (url == null)
          return NnNetUtil.textReturn("error\nurl invalid");    	   
       
       //verify channel status for existing channel
       NnChannel channel = chMngr.findBySourceUrl(url);                                        
       if (channel != null && (channel.getStatus() == NnChannel.STATUS_ERROR)) {
           log.info("channel id and status :" + channel.getId()+ ";" + channel.getStatus());
           return NnNetUtil.textReturn("error\nstatus error:" + channel.getId() + "; status:" + channel.getStatus());  
       }
       if (channel == null) {
           channel = chMngr.create(url, null, lang, req);
           if (channel == null)
        	   return NnNetUtil.textReturn("error\nurl invalid verified by youtube");  
       }           
       return NnNetUtil.textReturn(channel.getIdStr());
    }
    
    @RequestMapping(value="msoInfo")
    public ResponseEntity<String> msoInfo(HttpServletRequest req) {
        MsoManager msoMngr = new MsoManager();
        Mso mso = msoMngr.findNNMso();
        String[] result = {""};
        result[0] += PlayerApiService.assembleKeyValue("key", String.valueOf(mso.getId()));
        result[0] += PlayerApiService.assembleKeyValue("name", mso.getName());
        result[0] += PlayerApiService.assembleKeyValue("title", mso.getTitle());        
        result[0] += PlayerApiService.assembleKeyValue("logoUrl", mso.getLogoUrl());
        result[0] += PlayerApiService.assembleKeyValue("jingleUrl", mso.getJingleUrl());
        result[0] += PlayerApiService.assembleKeyValue("preferredLangCode", mso.getLang());
        result[0] += PlayerApiService.assembleKeyValue("jingleUrl", mso.getJingleUrl());

        PlayerApiService s = new PlayerApiService();
        String output = s.assembleMsgs(NnStatusCode.SUCCESS, result);
        return NnNetUtil.textReturn(output);
    }    

    @RequestMapping(value="programInfo", produces = "text/plain; charset=utf-8")
    public @ResponseBody String programInfo(
            @RequestParam(value="channel", required=false) String channel) {
        NnProgramManager mngr = new NnProgramManager();
        String result = mngr.findPlayerProgramInfoByChannel(Long.parseLong(channel));        
        if (result == null)
            return "null, error";
        String output = "";
        if (result != null) {
            String[] lines = result.split("\n");
            output += lines.length + " program records. \n--\n";
            for (String l : lines) {
                String[] data = l.split("\t");
                for (int i=0; i < data.length; i++) {
                    if (i == 0)  output += "channel id:";
                    if (i == 1)  output += "program id:"; 
                    if (i == 2 ) output += "name:";                        
                    if (i == 3)  output += "description:";
                    if (i == 4)  output += "content type:";
                    if (i == 5)  output += "duration:";
                    if (i == 6)  output += "image url:";
                    if (i == 7)  output += "image large url:";
                    if (i == 8)  output += "video url:";                         
                    if (i == 9)  output += "url2:";
                    if (i == 10) output += "url3:";
                    if (i == 11) output += "audior url:";
                    if (i == 12) output += "last update time:";
                    if (i == 13) output += "comment:";
                    if (i == 14) output += "title card:";
                    if (i == 15) output += "poi:";
                    if (data[i] != null && (i == 2 || i == 3 || i == 6 || i == 8 || i == 11)) {
                        String sub[] = data[i].split("\\|");
                        output += "\n";
                        for (int j=0; j < sub.length; j++) {
                            int index = j+1;
                            output += "(" + index + ")" + sub[j] + "\n";
                        }
                    } else if (data[i] != null && i == 14) {
                        try {
                            output += "\n" + URLDecoder.decode(data[i], "utf-8") + "--\n";
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else if (data[i] != null && i == 15) {
                        String sub[] = data[i].split("\\|");
                        output += "\n";
                        for (int j=0; j < sub.length; j++) {
                            int index = j+1;
                            output += "(" + index + ")" + sub[j] + "\n";
                        }
                    } else {   
                        output += data[i] + "\n";
                    }
                }
                output += "----\n";
            }
        }
                    
        return output;        
    }

    @RequestMapping(value="channelLineup", produces = "text/plain; charset=utf-8")
    public @ResponseBody String channelLineup(
            @RequestParam(value="channel", required=false) String channel,
            @RequestParam(value="v", required=false) String v) {
        NnChannelManager mngr = new NnChannelManager();
        NnChannel c = mngr.findById(Long.parseLong(channel));
        if (c == null)
            return "channel does not exist";
        List<NnChannel> channels = new ArrayList<NnChannel>();
        channels.add(c);
        int version = 40;
        if (v != null)
            version = Integer.parseInt(v);
        String result = mngr.composeChannelLineup(channels, version);
        if (result == null) {
            return "error, can't be null";
        }
        String[] lengthTest = result.split("\t", -1);
        String[] data = result.split("\t");
        String output = "";
        output += "number of fields:" + lengthTest.length + "\n\n";

        for (int i=0; i < data.length; i++) {
            if (i == 0)  output += "grid:";
            if (i == 1)  output += "channel id:"; 
            if (i == 2 ) output += "name:";                        
            if (i == 3)  output += "description:";
            if (i == 4)  output += "image:";
            if (i == 5)  output += "episode count:";
            if (i == 6)  output += "type:";
            if (i == 7)  output += "status:";
            if (i == 8)  output += "content type:";
            if (i == 9)  output += "source url:";
            if (i == 10)  output += "update time:";
            if (i == 11)  output += "sorting:";
            if (i == 12)  output += "piwik:";
            if (i == 13)  output += "recently watched program:";
            if (i == 14)  output += "youtube original name:";
            if (i == 15)  output += "subscriber count:";
            if (i == 16)  output += "view count:";
            if (i == 17)  output += "tag:";
            if (i == 18)  output += "curator id:";
            if (i == 19)  output += "curator name:";
            if (i == 20)  output += "curator description:";
            if (i == 21)  output += "curator image url:";
            if (i == 22)  output += "subscriber profile urls:";
            if (i == 23)  output += "subscriber thumbnail urls:";
            if (i == 24)  output += "last episode title:";
            if (i == 25)  output += "poi:";
            if (data[i] != null && (i == 25)) {
                String sub[] = data[i].split("\\|");
                output += "\n";
                for (int j=0; j < sub.length; j++) {
                    String json[] = sub[j].split(";");
                    if (json.length == 4) {
                        output += "(" + j + ")";
                        try {
                            String jsonstr = URLDecoder.decode(json[3], "utf-8");
                            ObjectMapper mapper = new ObjectMapper();
                            JsonFactory factory = mapper.getJsonFactory(); 
                            JsonParser jp = factory.createJsonParser(jsonstr); //to test valid json
                            output += "\n" + URLDecoder.decode(json[3], "utf-8") + "\n--\n";
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
 
                    }
                }
            } else {
                output += data[i] + "\n";
            }
        }        
        return output;        
    }
    
    @RequestMapping(value="programCache", produces = "text/plain; charset=utf-8")
    public @ResponseBody String programCache(
            @RequestParam(value="channel", required=false) long chId ) {
        NnProgramManager mngr = new NnProgramManager();
        mngr.resetCache(chId);
        return "OK";
    }

    @RequestMapping(value="channelCache", produces = "text/plain; charset=utf-8")
    public @ResponseBody String channelCache(@RequestParam(value="channel", required=false) long chId ) {            
        NnChannelManager mngr = new NnChannelManager();
        mngr.resetCache(chId); 
        return "OK";                
    }

    @RequestMapping(value="channelSubmit", produces = "text/plain; charset=utf-8")
    public @ResponseBody String channelCache(
            HttpServletRequest req,
            @RequestParam(value="url", required=false) String url, 
            @RequestParam(value="name", required=false) String name) {            
        NnChannelManager mngr = new NnChannelManager();
        NnChannel c = mngr.create(url, name, "en", req);
        if ( c!= null)
            return c.getIdStr();
        return "channel submission failed";                
    }

    @RequestMapping(value="tag", produces = "text/plain; charset=utf-8")
    public @ResponseBody String tag(
            HttpServletRequest req, 
            @RequestParam(value="name", required=false) String name) {            
        TagManager tagMngr = new TagManager();
        Tag t = tagMngr.findByName(name);
        if (t == null) {
            t = new Tag(name);
            tagMngr.save(t);
        }
        return String.valueOf(t.getId());                
    }

    @RequestMapping(value="tagMap", produces = "text/plain; charset=utf-8")
    public @ResponseBody String tagMap(
            HttpServletRequest req, 
            @RequestParam(value="tagId", required=false) long tagId,            
            @RequestParam(value="chId", required=false) long chId) {
        TagManager tagMngr = new TagManager();
        tagMngr.createTagMap(tagId, chId);
        return "OK";                
    }    
    
}
