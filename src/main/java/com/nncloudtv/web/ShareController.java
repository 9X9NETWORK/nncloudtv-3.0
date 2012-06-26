package com.nncloudtv.web;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	/**
	 * original url: /share/1
	 * rewrite to  : #!share=x
	 */
	@RequestMapping("{ipgId}")
	public String zooatomics(@PathVariable String ipgId,
	        @RequestParam(value="js",required=false) String js,
			@RequestParam(value="jsp",required=false) String jsp,			
			HttpServletResponse resp, 
			Model model) {
		log.info("/share/" + ipgId);
		PlayerService service = new PlayerService();
		String url = service.rewrite(js, jsp); 
		return "redirect:/" + url + "#!share=" + ipgId;
		
		/*
		String msoName = null;
		//find mso info of the user who shares the ipg
		model = service.prepareBrand(model, msoName, resp);
		model = service.preparePlayer(model, js, jsp);
		if (jsp != null && jsp.length() > 0) {
			return "player/" + jsp;
		}
		return "player/zooatomics";
		*/
	}
}
