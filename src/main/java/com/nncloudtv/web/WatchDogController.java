package com.nncloudtv.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
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
            @RequestParam(value="channel", required=false) String channel) {
        NnChannelManager mngr = new NnChannelManager();
        NnChannel c = mngr.findById(Long.parseLong(channel));
        if (c == null)
            return "channel does not exist";
        String result = mngr.composeChannelLineupStr(c);
        if (result == null) {
            return "error, can't be null";
        }
        String[] data = result.split("\t");
        String output = "";
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
            output += data[i] + "\n";            
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
