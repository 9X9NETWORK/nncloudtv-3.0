package com.nncloudtv.web;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.service.PlayerService;

@Controller
@RequestMapping("share")
public class ShareController {

	protected static final Logger log = Logger.getLogger(ShareController.class.getName());
	
	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		NnLogUtil.logException(e);
		return "error/exception";				
	}
	
	@RequestMapping("{ipgId}")
	public String zooatomics(@PathVariable String ipgId, HttpServletResponse resp, Model model) {
		log.info("/share/" + ipgId);
		PlayerService playerService = new PlayerService();		
		String msoName = null;
		//find mso info of the user who shares the ipg
		model = playerService.prepareBrand(model, msoName, resp);
		return "player/zooatomics";
	}
}
