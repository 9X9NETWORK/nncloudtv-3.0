package com.nncloudtv.web.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.codehaus.jackson.map.ObjectMapper;

import com.nncloudtv.lib.JqgridHelper;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.*;
import com.nncloudtv.service.*;

@Controller
@RequestMapping("admin/nnuser")
public class AdminNnUserController {

	protected static final Logger logger = Logger.getLogger(AdminNnUserController.class.getName());
	
	public final NnUserManager nnUserMngr;
	
	@Autowired
	public AdminNnUserController(NnUserManager nnUserMngr) {
		this.nnUserMngr = nnUserMngr;		
	}	

	@RequestMapping("subscription")
	public ResponseEntity<String> subscription(@RequestParam(required=false) String token, @RequestParam(required=false) Long id) {
//		SubscriptionManager subMngr = new SubscriptionManager();
		NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
		NnUser user = null;
		if (token != null) {
			user = nnUserMngr.findByToken(token);
		} else {
			user = nnUserMngr.findById(id);
		}
		if (user == null) { return NnNetUtil.textReturn("user does not exist"); }		
//		String output = "email\tkey\tid\ttoken\n-----------------\n";
		String output = "email\tid\ttoken\n-----------------\n";
//		output = output + user.getEmail() + "\t" + NnStringUtil.getKeyStr(user.getKey()) + "\t" + user.getKey().getId() + "\t" + user.getToken();
		output = output + user.getEmail() + "\t" + user.getId() + "\t" + user.getToken();
		output = output + "\n\n";
//		output = output + "key\tid\tname\turl\ttype\tprogramCount\tstatus\n-----------------\n";
		output = output + "id\tname\turl\ttype\tprogramCount\tstatus\n-----------------\n";
		List<NnChannel> channels = subMngr.findSubscribedChannels(user);		
		for (NnChannel c : channels) {			
			output = output + "\t" + c.getId() + "\t" + c.getName() + "\t" + c.getSourceUrl() + 
							  "\t" + c.getType() + "\t" + c.getProgramCnt() + "\t" + c.getStatus();
			output = output + "\n";
		}
		return NnNetUtil.textReturn(output);
	}

	
	@RequestMapping(value = "subscription", params = {"id", "page", "rows", "sidx", "sord"})
	public void subscription(@RequestParam(value = "id")   Long         userId,
	                         @RequestParam(value = "page") Integer      currentPage,
	                         @RequestParam(value = "rows") Integer      rowsPerPage,
	                         @RequestParam(value = "sidx") String       sortIndex,
	                         @RequestParam(value = "sord") String       sortDirection,
	                                                       OutputStream out) {
		
		NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
		NnChannelManager channelMngr = new NnChannelManager();
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
		
		String filter = "userId == " + userId;
		int totalRecords = subMngr.total(filter);
		int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
		if (currentPage > totalPages)
			currentPage = totalPages;
		
		List<NnUserSubscribe> results = subMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
		
		for (NnUserSubscribe sub : results) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			List<Object> cell = new ArrayList<Object>();
			
			NnChannel channel = channelMngr.findById(sub.getChannelId());
			
			cell.add(sub.getChannelId());
			cell.add(channel.getName());
			cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sub.getUpdateDate()));
			cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sub.getCreateDate()));
			cell.add(sub.getSeq());
			cell.add(sub.getType());
			
			map.put("id", sub.getId());
			map.put("cell", cell);
			dataRows.add(map);
		}
		
		try {
			mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}
	
	@RequestMapping("resetPassword")
	public ResponseEntity<String> resetPassword(
			@RequestParam(value="email")String email, 
			@RequestParam(value="password")String password, HttpServletRequest req, HttpServletResponse resp) {
		NnUser user = nnUserMngr.findByEmail(email, req);
		if (user == null)
			return NnNetUtil.textReturn("user does not exist");
		user.setPassword(password);
		nnUserMngr.resetPassword(user);	
		return NnNetUtil.textReturn("OK");
	}
	
	@RequestMapping("login")
	public @ResponseBody String create(@RequestParam(value="email")String email, String password, HttpServletRequest req, HttpServletResponse resp) {
		return "OK";
	}
	
	@RequestMapping(value = "list", params = {"page", "rows", "sidx", "sord"})
	public void list(@RequestParam(value = "page")   Integer      currentPage,
	                 @RequestParam(value = "rows")   Integer      rowsPerPage,
	                 @RequestParam(value = "sidx")   String       sortIndex,
	                 @RequestParam(value = "sord")   String       sortDirection,
	                 @RequestParam(required = false) String       searchField,
	                 @RequestParam(required = false) String       searchOper,
	                 @RequestParam(required = false) String       searchString,
	                                                 OutputStream out) {
		
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
		
		String filter = "";
		if (searchField != null && searchOper != null && searchString != null && !searchString.isEmpty()) {
			
			Map<String, String> opMap = JqgridHelper.getOpMap();
			if (opMap.containsKey(searchOper)) {
				filter = searchField + " " + opMap.get(searchOper) + " " + searchString;
				logger.info("filter: " + filter);
			}
		}
		
		int totalRecords = 0;
		try {
			
			totalRecords = nnUserMngr.total(filter);
			
		} catch(OutOfMemoryError e) {
			totalRecords = 10000;
		}
		
		int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
		if (currentPage > totalPages)
			currentPage = totalPages;
		
		List<NnUser> results = nnUserMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
		
		for (NnUser user : results) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			List<Object> cell = new ArrayList<Object>();
			
			cell.add(user.getImageUrl());
			cell.add(user.getMsoId());
			cell.add(user.getId());
			cell.add(user.getName());
			cell.add(user.getEmail());
			cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.getUpdateDate()));
			cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.getCreateDate()));
			cell.add(user.getType());
			cell.add(user.getDob());
			cell.add(user.getIntro());
			
			map.put("id", user.getId());
			map.put("cell", cell);
			dataRows.add(map);
		}
		
		try {
			mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}
	
}
