package com.nncloudtv.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nncloudtv.lib.NnNetUtil;;

@Controller
@RequestMapping("version")
public class VersionController {
	
	@RequestMapping("current")
	public ResponseEntity<String> current() {
		String appVersion = "2";
		String server = "java1";
		String svn = "$Revision: 2 $";
		String info = "app version: " + appVersion + "\n"; 
		info = info + "app server: " + server + "\n";
		info = info + "svn: " + svn;
		return NnNetUtil.textReturn(info);
	}	

}
