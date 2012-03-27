package com.nncloudtv.lib;

import java.util.logging.Logger;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class JqgridHelper {
	
	protected static final Logger log = Logger.getLogger(JqgridHelper.class.getName());
	
	public static Map<String, Object> composeJqgridResponse(int page, int total, int records, List<Map<String, Object>> rows) {
		
		Map<String, Object> response = new HashMap<String, Object>();
		
		response.put("page", page);
		response.put("total", total);
		response.put("records", records);
		response.put("rows", rows);
		
		return response;
	}
	
	public static Map<String, String> getOpMap() {
		
		Map<String, String> response = new HashMap<String, String>();
		
		response.put("eq", "==");
		response.put("ne", "!=");
		response.put("lt", "<");
		response.put("le", "<=");
		response.put("gt", ">");
		response.put("ge", ">=");
		
		return response;
	}
	
}
