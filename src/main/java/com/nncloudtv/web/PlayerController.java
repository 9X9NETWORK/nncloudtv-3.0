package com.nncloudtv.web;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.service.PlayerService;

@Controller
@RequestMapping("")
public class PlayerController {

	protected static final Logger log= Logger.getLogger(PlayerController.class.getName());
	
	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		NnLogUtil.logException(e);
		return "error/exception";				
	}		
	
	@RequestMapping("/")
	public String index() {
		return "redirect:9x9";
	}
	
	/**
	 * to become a 9x9 player, 1)delete cookie, 2)set fb info 
	 */	
	@RequestMapping("{name}")
	public String zooatomics(@PathVariable String name,
			                 @RequestParam(value="mso",required=false) String mso, 
			                 HttpServletRequest req, HttpServletResponse resp, Model model) {
		PlayerService service = new PlayerService();
		if (name.equals("5f")) 
			model = service.prepareBrand(model, Mso.NAME_5F, resp);
		else {
			model = service.prepareBrand(model, mso, resp);
		}
		return "player/zooatomics";
	}

	/*
	 * used for dns redirect watch dog 
	 */
	@RequestMapping("{mso}/wd")
	public ResponseEntity<String> watchdog() {		
		return NnNetUtil.textReturn("OK");
	}
	
	
}
