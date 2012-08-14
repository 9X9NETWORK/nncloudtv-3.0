package com.nncloudtv.web.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.JDOUserException;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.JqgridHelper;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.service.CntSubscribeManager;
import com.nncloudtv.service.NnChannelManager;

@Controller
@RequestMapping("admin/channel")
public class AdminChannelController {
	protected static final Logger logger = Logger.getLogger(AdminChannelController.class.getName());		
	
	private final NnChannelManager channelMngr;
	
	@Autowired
	public AdminChannelController(NnChannelManager channelMngr) {
		this.channelMngr = channelMngr;
	}

	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		NnLogUtil.logException(e);
		return "error/exception";				
	}	

	/**
	 * Channel creation
	 * 
	 * @param url source url
	 * @param name channel name
	 * @param isPublic to be shown in the directory or not
	 * @param devel is in devel mode or not, i.e. whether to submit to transcoding service 
	 * @return status in text
	 */
	@RequestMapping("create")
	public @ResponseBody String create(HttpServletRequest req,				
			                           @RequestParam(value="sourceUrl", required=false)String url,
				                       @RequestParam(value="name", required=false) String name,
				                       @RequestParam(required=false) Boolean isPublic,
				                       @RequestParam(value="devel",required=false) boolean devel) {
		NnChannel c = channelMngr.findBySourceUrl(channelMngr.verifyUrl(url));
		if (c != null)
			return "Existed channel:" + c.getId();
		c = channelMngr.create(url, name, req);
		if (c == null)
			return "Fail"; 
		c.setPublic(isPublic);
		channelMngr.save(c);
		return "OK";
	}	
	
	/**
	 * Channel listing. List items in jqGrid format.
	 *
	 * A jqGrid response format should look like:
	 *
	 * {
	 *   page: 1
	 *   total: 10
	 *   records: 100
	 *   rows:
	 *   [
	 *     ["13671109", "5f", "http://5f.tv", "MSO", "true", "24"],
	 *     ~
	 *     ~
	 *     ["938362", "9x9", "http://9x9.tv", "NN", "false", "13"]
	 *   ]
	 * }
	 * 
	 * @param currentPage current page
	 * @param rowsPerPage rows per page
	 * @param sortIndex sort field
	 * @param sortDirection asc or desc
	 * @param searchField search field
	 * @param searchOper search condition
	 * @param searchString search string 
	 * @param notify set to true for notification page
	 * @return status in text
	 */
	@RequestMapping(value = "list", params = {"page", "rows", "sidx", "sord"})
	public @ResponseBody String list	(
			         @RequestParam(value = "page")   Integer      currentPage,
	                 @RequestParam(value = "rows")   Integer      rowsPerPage,
	                 @RequestParam(value = "sidx")   String       sortIndex,
	                 @RequestParam(value = "sord")   String       sortDirection,
	                 @RequestParam(required = false) String       searchField,
	                 @RequestParam(required = false) String       searchOper,
	                 @RequestParam(required = false) String       searchString,
	                 @RequestParam(required = false) boolean      notify,
	                 OutputStream out) {
		CntSubscribeManager subMngr = new CntSubscribeManager();
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
		
		List<NnChannel> results;
		int totalRecords, totalPages;
		if (searchField != null && searchOper != null && searchString != null
		    && searchOper.equals("eq") && searchField.equals("id")) {			
			logger.info("searchString = " + searchString);
			totalRecords = 0;
			totalPages = 1;
			currentPage = 1;
			results = new ArrayList<NnChannel>();
			if (searchString.matches("^[0-9]+$")) {				
				NnChannel found = channelMngr.findById(Long.parseLong(searchString));
				if (found != null) {
					totalRecords++;
					results.add(found);
				}
			}
		} else if (searchField != null && searchOper != null && searchString != null
		           && searchOper.equals("eq")
		           && (searchField.equals("status") || 
		        	   searchField.equals("contentType") || 
		        	   searchField.equals("isPublic") || 
		        	   searchField.equals("featured") || 
		        	   searchField.equals("sourceUrl") )) {			
			if (searchField.equals("sourceUrl")) {
				searchString = NnStringUtil.escapedQuote(searchString.toLowerCase());
			}
			String filter = searchField + " == " + searchString;
			logger.info("filter = " + filter);
			totalRecords = channelMngr.total(filter);
			totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
			if (currentPage > totalPages)
				currentPage = totalPages;
			results = channelMngr.list(currentPage, rowsPerPage, "updateDate", "desc", filter);
		} else if (searchField != null && searchOper != null && searchString != null
		           && searchOper.equals("eq")
		           && searchField.equals("name")){
			//use fuzzy search for "name"			
			results = new ArrayList<NnChannel>();
			List<NnChannel> totalResults = new ArrayList<NnChannel>();
			try{
				totalResults = NnChannelManager.search(searchString, true);
				totalRecords = totalResults.size();
				totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);				
				if(totalPages==0)
				{
					currentPage = 1;
					totalPages = 1;
				}
				else if(currentPage > totalPages)
					currentPage = totalPages;
				
				if(totalRecords>0)
				{
					for(int i=(currentPage-1)*rowsPerPage;i<currentPage*rowsPerPage;i++)
					{
						if(i<totalRecords)
						{
							results.add(totalResults.get(i));
						}
					}
				}
			}
			catch(JDOUserException e)
			{
				//handle illegal input from user, return an empty page, see more at "NnChannelManager.searchChannelEntries".
				logger.warning("illegal input from user");
				logger.warning(e.getMessage());

				totalRecords = 0;
				totalPages = 1;
				currentPage = 1;
			}
		}else {		
			totalRecords = channelMngr.total();
			totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
			if (currentPage > totalPages)
				currentPage = totalPages;
			results = channelMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection);
		}
		
		for (NnChannel channel : results) {			
			Map<String, Object> map = new HashMap<String, Object>();
			List<Object> cell = new ArrayList<Object>();
			boolean qualified = true;
			if (notify) {
				qualified = false;
				Calendar cal = Calendar.getInstance();		
				cal.add(Calendar.DAY_OF_MONTH, - 14);
				Date d = cal.getTime();
				if (channel.getCreateDate().after(d)) {
					if (channel.getStatus() != NnChannel.STATUS_SUCCESS) {
						qualified = true;
					}
				}
			}
			if (qualified) {
				cell.add(channel.getId());
				cell.add(channel.getName());
				cell.add(channel.getSourceUrl());
				cell.add(channel.getStatus());
				cell.add(channel.getContentType());
				cell.add(channel.isPublic());
				cell.add(channel.getPiwik());
				cell.add(channel.getImageUrl());
				cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(channel.getUpdateDate()));
				cell.add(channel.getProgramCnt());
				cell.add(subMngr.findTotalCountByChannel(channel.getId()));
				map.put("id", channel.getId());
				map.put("cell", cell);
				dataRows.add(map);
			}
		}
		
		try {
			mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
		return "OK";
	}
	
	/**
	 * Channel modification
	 * 
	 * @param id channel id
	 * @param name channel name
	 * @param intro channel description
	 * @param imageUrl channel image url
	 * @param status channel status
	 * @param isPublic to show in the directory or not
	 * @param programCnt program count
	 * @return status in text
	 */
	@RequestMapping("modify")
	public @ResponseBody String modify(@RequestParam(required=true)  Long    id,
	                                   @RequestParam(required=false) String  name,
	                                   @RequestParam(required=false) String  intro,
	                                   @RequestParam(required=false) String  imageUrl,
	                                   @RequestParam(required=false) Short   status,
	                                   @RequestParam(required=false) Boolean isPublic,
	                                   @RequestParam(required=false) Integer programCnt) {
				
		NnChannel channel = channelMngr.findById(id);
		if (channel == null)
			return "Channel Not Found";
		
		if (name != null) {
			logger.info("name = " + name);
			channel.setName(name);
		}
		if (imageUrl != null) {
			logger.info("imageUrl = " + imageUrl);
			channel.setImageUrl(imageUrl);
		}
		if (intro != null) {
			logger.info("intro = " + intro);
			if (intro.length() > 255)
				return "Introduction Is Too Long";
			channel.setIntro(intro);
		}		
		if (status != null) {
			logger.info("status = " + status);
			channel.setStatus(status);
		}
		if (isPublic != null) {
			logger.info("isPublic = " + isPublic);
			channel.setPublic(isPublic);
		}
		if (programCnt != null) {
			logger.info("programCnt = " + programCnt);
			channel.setProgramCnt(programCnt);
		}		
		channelMngr.save(channel);
				
		return "OK";
	}	
	
	/*
	@RequestMapping("addSet")
	public @ResponseBody String addCategory(@RequestParam(value = "channel")  Long channelId,
	                                        @RequestParam(value = "set") Long setId) {
				
		NnSetToNnChannelManager csMngr = new NnSetToNnChannelManager();
		NnSetManager setMngr = new NnSetManager();
		logger.info("setId = " + setId);
		NnSet set = setMngr.findById(setId);
		if (set == null) {
			String error = "Invalid Set";
			logger.warning(error);
			return error;
		}
		logger.info("channelId = " + channelId);
		NnChannel channel = channelMngr.findById(channelId);
		if (channel == null) {
			String error = "Invalid Channel";
			logger.warning(error);
			return error;
		}
		NnSetToNnChannel cs = csMngr.findBySetAndChannel(setId, channelId);
		if (cs != null) {
			String error = "Channel Is Already in Set";
			logger.warning(error);
			return error;
		}
		
		//seq not set appropriately, need fix later.
		csMngr.create(new NnSetToNnChannel(setId, channelId, (short) 0));
		
		set.setChannelCnt(set.getChannelCnt() + 1);
		setMngr.save(set);
		
		return "OK";
	}
	*/
	/*
	@RequestMapping("deleteSets")
	public @ResponseBody String deleteCategories(@RequestParam(required=true)long channel, String sets) {
		if (sets == null) {return "fail";}

		//find channel
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel c = channelMngr.findById(channel);
		
		if (c != null) {
			//find all the sets
			NnSetToNnChannelManager csMngr = new NnSetToNnChannelManager();
			NnSetManager setMngr = new NnSetManager();
			List<Long> setIdList = new ArrayList<Long>();	
			String[] arr = sets.split(",");
			for (int i=0; i<arr.length; i++) { setIdList.add(Long.parseLong(arr[i])); }
			//delete them in CategoryChannel table
			List<NnSet> existing = setMngr.findByIds(setIdList);
			csMngr.deleteChannelSet(c, existing);
		}
		
		return "success";
	}
	*/
	/*
	@RequestMapping("deleteSet")
	public @ResponseBody String deleteCategory(@RequestParam(value = "id") Long csId) {		
		NnSetToNnChannelManager csMngr = new NnSetToNnChannelManager();
		
		logger.info("csId = " + csId);
		NnSetToNnChannel cs = csMngr.findById(csId);
		if (cs == null) {
			String error = "SetChannel Does Not Exist";
			logger.warning(error);
			return error;
		}
		csMngr.delete(cs);
		// TODO: deal with Set.channelCount
		return "OK";
	}	
	 */		

	/*
	@RequestMapping(value = "listSets", params = {"channel", "page", "rows", "sidx", "sord"})
	public void listSets(@RequestParam(value = "channel") Long         channelId,
	                           @RequestParam(value = "page")    Integer      currentPage,
	                           @RequestParam(value = "rows")    Integer      rowsPerPage,
	                           @RequestParam(value = "sidx")    String       sortIndex,
	                           @RequestParam(value = "sord")    String       sortDirection,
	                                                            OutputStream out) {		
		NnSetManager setMngr = new NnSetManager();
		NnSetToNnChannelManager csMngr = new NnSetToNnChannelManager();
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
		
		if (channelId == 0) {
			try {
				mapper.writeValue(out, JqgridHelper.composeJqgridResponse(1, 1, 0, new ArrayList<Map<String, Object>>()));
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
			return;
		}
		
		String filter = "channelId == " + channelId;
		int totalRecords = csMngr.total(filter);
		int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
		if (currentPage > totalPages)
			currentPage = totalPages;
		
		List<NnSetToNnChannel> results = csMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
		
		for (NnSetToNnChannel cs : results) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			List<Object> cell = new ArrayList<Object>();
			
			NnSet set = setMngr.findById(cs.getSetId());			
			cell.add(cs.getChannelId());
			cell.add(cs.getSetId());
			cell.add(set.getName());
			cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cs.getUpdateDate()));
			cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cs.getCreateDate()));
			cell.add(set.isPublic());
			cell.add(set.getChannelCnt());
			
			map.put("id", cs.getId());
			map.put("cell", cell);
			dataRows.add(map);
		}		
		try {
			mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}
	*/
	
	/*
	@RequestMapping("createPiwik")
	public @ResponseBody String createPiwik(
				HttpServletRequest req,
				@RequestParam(value="id",required=false) long id) {
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel c = channelMngr.findById(id); 
		if (c != null) {
			if (c.getPiwik() == null) {
				String piwikId = PiwikLib.createPiwikSite(0, c.getId(), req);
				logger.info("piwikId:" + piwikId);
				c.setPiwik(piwikId);
				channelMngr.save(c);					
			}			
		}
		return "OK";
	}
	*/
	
}
