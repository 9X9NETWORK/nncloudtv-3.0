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
        String appVersion = "0.0.9";
        String svn = "2697";
        String packagedTime = "2012-05-08 18:34:42.183000";
		String info = "app version: " + appVersion + "\n"; 
		info = info + "svn: " + svn + "\n";
		info = info + "packaged time: " + packagedTime + "\n";
		return NnNetUtil.textReturn(info);
	}		
}
