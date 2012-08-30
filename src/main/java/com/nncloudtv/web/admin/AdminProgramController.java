package com.nncloudtv.web.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.JqgridHelper;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.service.NnProgramManager;

@Controller
@RequestMapping("admin/program")
public class AdminProgramController {
	protected static final Logger logger = Logger.getLogger(AdminProgramController.class.getName());		
	
	private final NnProgramManager programMngr;
	
	@Autowired
	public AdminProgramController(NnProgramManager programMngr) {
		this.programMngr = programMngr;
	}

	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		NnLogUtil.logException(e);
		return "error/exception";				
	}			
	
	@RequestMapping("list")
	public ResponseEntity<String> list(@RequestParam(value="channel")long channelId) {
		//find all programs, including the not public ones
		List<NnProgram> programs = programMngr.findByChannel(channelId);
		String[] title = {"id", "channelId", "isPublic", "status", "updateDate", "name"};		
		String result = "";
		for (NnProgram p:programs) {
			String[] ori = {String.valueOf(p.getId()),
			                String.valueOf(p.getChannelId()),
			                Boolean.toString(p.isPublic()),
			                String.valueOf(p.getStatus()),
			                NnStringUtil.getDateString(p.getUpdateDate()),
			                p.getName()};
			result = result + NnStringUtil.getDelimitedStr(ori);
			result = result + "\n";
		}
		String output = NnStringUtil.getDelimitedStr(title) + "\n" + result;
		return NnNetUtil.textReturn(output);
	}
	
	@RequestMapping(value = "list", params = {"channel", "page", "rows", "sidx", "sord"})
	public void list(@RequestParam(value = "channel") Long         channelId,
	                 @RequestParam(value = "page")    Integer      currentPage,
	                 @RequestParam(value = "rows")    Integer      rowsPerPage,
	                 @RequestParam(value = "sidx")    String       sortIndex,
	                 @RequestParam(value = "sord")    String       sortDirection,
	                                                  OutputStream out) {
		
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
		
		String filter = "channelId == " + channelId;
		int totalRecords = programMngr.total(filter);
		int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
		if (currentPage > totalPages)
			currentPage = totalPages;
		
		List<NnProgram> results = programMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
		
		for (NnProgram program : results) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			List<Object> cell = new ArrayList<Object>();
			
			cell.add(program.getImageUrl());
			cell.add(program.getId());
			cell.add(program.getName());
			cell.add(new SimpleDateFormat("yyyyMMddHHmmss").format(program.getUpdateDate()));
			cell.add(new SimpleDateFormat("yyyyMMddHHmmss").format(program.getCreateDate()));
			cell.add(program.getAudioFileUrl());
			cell.add(program.getStatus());
			cell.add(program.getContentType());
			cell.add(program.isPublic());
			cell.add(program.getDuration());
			cell.add(program.getIntro());
			
			map.put("id", program.getId());
			map.put("cell", cell);
			dataRows.add(map);
		}
		
		try {
			mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}
	
	@RequestMapping("modify")
	public @ResponseBody String modify(@RequestParam(required=true)  Long    id,
	                                   @RequestParam(required=false) String  name,
	                                   @RequestParam(required=false) String  intro,
	                                   @RequestParam(required=false) String  imageUrl,
	                                   @RequestParam(required=false) Short   status,
	                                   @RequestParam(required=false) Boolean isPublic,
	                                   @RequestParam(required=false) String  updateDate) {
		
//		logger.info("admin: " + userService.getCurrentUser().getEmail());
		
		logger.info("programId: " + id);
		NnProgram program = programMngr.findById(id);
		if (program == null)
			return "Program Not Found";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			if (updateDate != null) {
				logger.info("updateDate: " + updateDate);
				Date date = sdf.parse(updateDate);
				program.setUpdateDate(date);
			}
		} catch (ParseException e) {
			return "Parsing Error";
		}
		if (name != null) {
			logger.info("name: " + name);
			program.setName(name);
		}
		if (imageUrl != null) {
			logger.info("imageUrl: " + imageUrl);
			program.setImageUrl(imageUrl);
		}
		if (intro != null) {
			logger.info("intro: " + intro);
			if (intro.length() > 255)
				return "Introduction Is Too Long";
			program.setIntro(intro);
		}
		if (status != null) {
			logger.info("status: " + status);
			program.setStatus(status);
		}
		if (isPublic != null) {
			logger.info("isPublic: " + isPublic);
			program.setPublic(isPublic);
		}
		
		programMngr.save(program);
		return "OK";
	}
	
	
}
