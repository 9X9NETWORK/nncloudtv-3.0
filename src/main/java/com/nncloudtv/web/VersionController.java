package com.nncloudtv.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nncloudtv.lib.NnNetUtil;

@Controller
@RequestMapping("version")
public class VersionController {
        
    @RequestMapping(value="current", produces = "text/plain; charset=utf-8")
    public ResponseEntity<String> current() {
        String appVersion = "3.1.12.3";
        String svn = "2766";
        String packagedTime = "2012-06-01 00:04:01.456000";
        String info = "app version: " + appVersion + "\n"; 
        info += "svn: " + svn + "\n";
        info += "packaged time: " + packagedTime + "\n";
        return NnNetUtil.textReturn(info);
    }
    
}
