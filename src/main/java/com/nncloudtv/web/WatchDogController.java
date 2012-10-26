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
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnProgramManager;
import com.nncloudtv.service.PlayerApiService;
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
    
}
