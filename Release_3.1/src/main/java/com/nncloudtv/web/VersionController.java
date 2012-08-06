package com.nncloudtv.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nncloudtv.lib.NnNetUtil;

@Controller
@RequestMapping("version")
public class VersionController {
		
	@RequestMapping("current")
	public ResponseEntity<String> current() {
        String appVersion = "3.1.11.2";
        String svn = "2737";
        String packagedTime = "2012-05-17 16:20:00.183000";
		String info = "app version: " + appVersion + "\n"; 
		info = info + "svn: " + svn + "\n";
		info = info + "packaged time: " + packagedTime + "\n";
		return NnNetUtil.textReturn(info);
	}		
}
