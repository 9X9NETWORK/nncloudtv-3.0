package com.nncloudtv.web.api;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api")
public class ApiMisc extends ApiGeneric {
	
	@RequestMapping("echo")
	public @ResponseBody Map<String, String> echo(HttpServletRequest req, HttpServletResponse resp) {
		
		log = Logger.getLogger(ApiMisc.class.getName());
		
		@SuppressWarnings("unchecked")
		Map<String, String[]> names = req.getParameterMap();
		Map<String, String> result = new TreeMap<String, String>();
		
		for (String name : names.keySet()) {
			
			String value = names.get(name)[0];
			log.info(name + " = " + value);
			result.put(name, value);
		}
		
		if (result.isEmpty()) {
			
			badRequest(resp, "a least one parameter");
			
			return null;
		}
		
		if(!req.getMethod().equalsIgnoreCase("GET"))
			resp.setStatus(201);
		
		return result;
	}
	
}
