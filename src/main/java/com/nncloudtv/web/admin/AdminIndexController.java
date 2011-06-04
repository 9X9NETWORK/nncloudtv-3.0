package com.nncloudtv.web.admin;

import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/index")
public class AdminIndexController {
	
	protected static final Logger logger = Logger.getLogger(AdminIndexController.class.getName());		

	@RequestMapping("")
	public String index() {
		return "admin/index";
	}
	
}
