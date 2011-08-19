package com.nncloudtv.web.admin;

import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.service.InitService;

/**
 * for testing only, works only for small set of data
 * 
 * most of the functions are private, turned it on if you need them.
 */	
@Controller
@RequestMapping("admin/init")
public class AdminInitController {
	protected static final Logger log = Logger.getLogger(AdminInitController.class.getName());		
	
	private final InitService initService;		
	
	@Autowired
	public AdminInitController(InitService initService) {
		this.initService = initService;
	}		
	
	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		if (e.getClass().equals(MissingServletRequestParameterException.class) ||
			e.getClass().equals(IllegalStateException.class)) {
		} else {
			NnLogUtil.logException(e);			
		}
		return "error/exception";				
	}
			
	@RequestMapping(value="groundStart", method=RequestMethod.GET)
	public String groundStartGet(HttpServletRequest req) {
		return "admin/groundStart";
	}
	
	@RequestMapping(value="groundStart", method=RequestMethod.POST)
	public String groundStartPost(HttpServletRequest req) {
		String host = NnNetUtil.getUrlRoot(req);
		//if (host.contains("http://localhost")) {
			initService.initAll();
		//}
		return "admin/groundStart";
	}	
		
}
