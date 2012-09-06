package com.nncloudtv.web;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.service.MsoManager;
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

}
