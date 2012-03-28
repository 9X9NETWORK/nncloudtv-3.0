package com.nncloudtv.web.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.jdo.JDOUserException;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

//import com.google.appengine.api.users.UserService;
//import com.google.appengine.api.users.UserServiceFactory;
import com.nncloudtv.lib.JqgridHelper;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.PiwikLib;
import com.nncloudtv.lib.YouTubeLib;
//import com.nncloudtv.model.Category;
//import com.nncloudtv.model.CategoryChannel;
import com.nncloudtv.model.ContentOwnership;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnSetToNnChannel;
import com.nncloudtv.model.NnUser;
//import com.nncloudtv.service.CategoryChannelManager;
//import com.nncloudtv.service.CategoryManager;
import com.nncloudtv.service.ContentOwnershipManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnSetChannelManager;
import com.nncloudtv.service.NnSetManager;
import com.nncloudtv.service.NnUserManager;
//import com.nncloudtv.service.SubscriptionLogManager;
import com.nncloudtv.service.TranscodingService;

@Controller
@RequestMapping("admin/channel")
public class AdminNnChannelController {
	protected static final Logger logger = Logger.getLogger(AdminNnChannelController.class.getName());		
	
	private final NnChannelManager channelMngr;
//	private final UserService       userService;
	
	@Autowired
	public AdminNnChannelController(NnChannelManager channelMngr) {
		this.channelMngr = channelMngr;
//		this.userService = UserServiceFactory.getUserService();
	}

	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		NnLogUtil.logException(e);
		return "error/exception";				
	}	
	
	@RequestMapping("tmpList")
	public ResponseEntity<String> tmpList(
				HttpServletRequest req) {
		String[] urls = {
				"http://www.youtube.com/user/goodtv/user/FBE16B28C166951F",
				"http://www.youtube.com/user/DianaAmazing",
				"http://www.youtube.com/user/tbwtv",
				"http://www.youtube.com/user/TVHS109",
				"http://www.youtube.com/user/ntdchinese",
				"http://www.youtube.com/user/ChinaTimes",
				"http://www.youtube.com/user/TheChineseNews",				
		};
		List<NnChannel> channels = new ArrayList<NnChannel>();
		for (String url : urls) {
			String checkedUrl = YouTubeLib.formatCheck(url);
			NnChannel c = channelMngr.findBySourceUrl(checkedUrl);
			channels.add(c);
		}
		String output = "";
		for (NnChannel c : channels) {
			output += c.getId() + "\t" + c.getSourceUrl() + "\n";
		}
		return NnNetUtil.textReturn(output);
	}	

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
	
	@RequestMapping("createBatch")
	public @ResponseBody String createBatch(
				HttpServletRequest req,
				@RequestParam(value="devel",required=false) boolean devel) {
		String[] urls = {
				"http://www.youtube.com/playlist?list=PL3FC1CEA86F082B94",
		};
		String[] names= {
				"Brand New Life",                       
		};
		for (int i=0; i<urls.length; i++) {
//			channelMngr.create(urls[i], names[i], devel, req);
			channelMngr.create(urls[i], req);
		}
		return "OK";
	}		
	
	@RequestMapping("create")
	public @ResponseBody String create(
				HttpServletRequest req,
			    @RequestParam(value="sourceUrl", required=false)String url,
				@RequestParam(value="name", required=false) String name,
				@RequestParam(value="devel",required=false) boolean devel) {
//		channelMngr.create(url, name, false, req);
		channelMngr.create(url, req);
		return "OK";
	}	

	@RequestMapping("list")
	public ResponseEntity<String> list(
			@RequestParam(value="status", required=false)String status,
			@RequestParam(value="since", required=false)String since) {		
		List<NnChannel> channels = new ArrayList<NnChannel>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date sinceDate = null;
		String output = "";
		
		if (since != null) {
			try {
				sinceDate = sdf.parse(since);
			} catch (ParseException e) {
				return NnNetUtil.textReturn("wrong date format: yyyymmdd");
			}			
			channels.addAll(channelMngr.findSince(sinceDate));			
		}		
		List<NnChannel> bad = new ArrayList<NnChannel>();		
		if (status == null) {
			channels = channelMngr.findAll();
			for (NnChannel c : channels) {
				/*
				if (c.getName() == null || c.getStatus() != NnChannel.STATUS_SUCCESS || 
					c.isPublic() != true || c.getProgramCnt() < 1 ) {
					bad.add(c);
				}
				*/
			}
			output = "Total count: " + channels.size() + "\n" + "Bad channel count(not public, name=null, status!=success, programCount < 1): " + bad.size();
			output = output + "\n\n --------------- \n\n";
			output = output + this.printChannelData(channels);				
			output = output + "\n\n --------------- \n\n";
			//output = output + this.printChannelData(bad);			
		} else {
			channels = channelMngr.findAllByStatus(Short.valueOf(status));
		}
		
		output = output + this.printChannelData(channels);		
		return NnNetUtil.textReturn(output);
	}
		
	/**
	 * List items in jqGrid format
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
	                 @RequestParam(required = false) String       set,
	                 @RequestParam(required = false) boolean      notify,
	                 OutputStream out) {
//		SubscriptionLogManager subLogMngr = new SubscriptionLogManager();
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
		
		List<NnChannel> results;
		int totalRecords, totalPages;
		if (searchField != null && searchOper != null && searchString != null
		    && searchOper.equals("eq") && searchField.equals("channel")) {			
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
		        	   searchField.equals("sourceUrl") || 
		        	   searchField.equals("langCode"))) {			
			if (searchField.equals("sourceUrl")) {
				searchString = NnStringUtil.escapedQuote(searchString.toLowerCase());
				searchField += "Search";
			}  else if (searchField.equals("langCode")) {
				searchString = NnStringUtil.escapedQuote(searchString);
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
				totalResults = NnChannelManager.searchChannelEntries(searchString);
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
				//cell.add(channel.getImageUrl());
				cell.add(channel.getId());
				cell.add(channel.getName());
				cell.add(channel.getSourceUrl());
				cell.add(channel.getStatus());
				cell.add(channel.getContentType());
//				cell.add(channel.getLangCode());
				cell.add(channel.isPublic());
				cell.add(channel.getPiwik());
				cell.add(channel.getImageUrl());
				cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(channel.getUpdateDate()));
				cell.add(channel.getProgramCnt());
//				cell.add(subLogMngr.findTotalCountByChannelId(channel.getId()));
				//cell.add(channel.getIntro());				
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
	
	private String printChannelData(List<NnChannel> channels) {
		String[] title = {"id", "name", "sourceUrl", "isPublic", "status", "programCount"};		
		String result = "";		
		for (NnChannel c : channels) {
			String[] ori = {String.valueOf(c.getId()),
				    	    c.getName(),
				    	    c.getSourceUrl(), 
				    	    String.valueOf(c.isPublic()),
				    	    String.valueOf(c.getStatus()), 
				    	    String.valueOf(c.getProgramCnt())}; 						
			result = result + NnStringUtil.getDelimitedStr(ori);		
			result = result + "\n";			
		}
		String output = NnStringUtil.getDelimitedStr(title) + "\n" + result;		
		return output;
	}

/*  
	//add only, a channel's categories are accumulated
	@RequestMapping("addCategories")
	public @ResponseBody String addCategories(@RequestParam(required=true)long channel, String categories) {
		CategoryManager categoryMngr = new CategoryManager();
		List<Category> categoryList = categoryMngr.findCategoriesByIdStr(categories);
		List<Category> list = categoryMngr.changeCategory(channel, categoryList);
		String output = "";
		for (Category c : list) {
			output = output + c.getId() + "\t" + c.getName() + "<br/>";
		}
		return output;
	}
*/	


	@RequestMapping("addSet")
	public @ResponseBody String addCategory(@RequestParam(value = "channel")  Long channelId,
	                                        @RequestParam(value = "set") Long setId) {
		
//		logger.info("admin = " + userService.getCurrentUser().getEmail());
		
		NnSetChannelManager csMngr = new NnSetChannelManager();
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



	@RequestMapping("deleteSets")
	public @ResponseBody String deleteCategories(@RequestParam(required=true)long channel, String sets) {
		if (sets == null) {return "fail";}

		//find channel
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel c = channelMngr.findById(channel);
		
		if (c != null) {
			//find all the sets
			NnSetChannelManager csMngr = new NnSetChannelManager();
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
	
	
	@RequestMapping("deleteSet")
	public @ResponseBody String deleteCategory(@RequestParam(value = "id") Long csId) {
		
//		logger.info("admin = " + userService.getCurrentUser().getEmail());
		
		NnSetChannelManager csMngr = new NnSetChannelManager();
		
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

	
	@RequestMapping("listSets")
	public @ResponseBody String listSets(@RequestParam(required=true)long channel) {
		NnSetManager setMngr = new NnSetManager();
		List<NnSet> sets = setMngr.findSetsByChannel(channel);
		String output = "";
		for (NnSet s : sets) {
			output = output + s.getId() + "\t" + s.getName() + "<br/>";
		}
		return output;
	}
	
	
	@RequestMapping(value = "listSets", params = {"channel", "page", "rows", "sidx", "sord"})
	public void listCategories(@RequestParam(value = "channel") Long         channelId,
	                           @RequestParam(value = "page")    Integer      currentPage,
	                           @RequestParam(value = "rows")    Integer      rowsPerPage,
	                           @RequestParam(value = "sidx")    String       sortIndex,
	                           @RequestParam(value = "sord")    String       sortDirection,
	                                                            OutputStream out) {		
		NnSetManager setMngr = new NnSetManager();
		NnSetChannelManager csMngr = new NnSetChannelManager();
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
		
		// no channel was specified
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
			
//			cell.add(set.getMsoId());
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
	
	@RequestMapping("modify")
	public @ResponseBody String modify(@RequestParam(required=true)  Long    id,
			                           @RequestParam(required=true)  Long    channelId,
	                                   @RequestParam(required=false) String  name,
	                                   @RequestParam(required=false) String  intro,
	                                   @RequestParam(required=false) String  imageUrl,
	                                   @RequestParam(required=false) Short   status,
//	                                   @RequestParam(required=false) String  langCode,
	                                   @RequestParam(required=false) Boolean isPublic,
//	                                   @RequestParam(required=false) Boolean featured,
	                                   @RequestParam(required=false) Integer programCount) {
		
//		logger.info("admin = " + userService.getCurrentUser().getEmail());		
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
		//!!! change counter implementation
		if (status != null) {
			logger.info("status = " + status);
			channel.setStatus(status);
		}
/*		
		if (langCode != null) {
			logger.info("langCode = " + langCode);
			channel.setLangCode(langCode);
		}
*/		
		if (isPublic != null) {
			logger.info("isPublic = " + isPublic);
			channel.setPublic(isPublic);
		}
/*		
		if (featured != null) {
			logger.info("featured = " + featured);
			channel.setFeatured(featured);
		}
*/		
		if (programCount != null) {
			logger.info("programCount = " + programCount);
			channel.setProgramCnt(programCount);
		}		
		channelMngr.save(channel);
		
		if (status != null) {
			NnSetManager setMngr = new NnSetManager();
			NnSetChannelManager csMngr = new NnSetChannelManager();
			List<NnSetToNnChannel> csList = csMngr.findByChannel(channel.getId());
			List<Long> setIds = new ArrayList<Long>();
			for (NnSetToNnChannel cs : csList) {
				setIds.add(cs.getSetId());
			}
			List<NnSet> sets = setMngr.findByIds(setIds);
			for (NnSet s : sets) {
				List<NnChannel> channels = setMngr.findPublicChannelsById(s.getId());
				s.setChannelCnt(channels.size());
				setMngr.save(s);				
			}			
		}
		
		return "OK";
	}
	
	@RequestMapping("findUnUniqueSourceUrl")
	public ResponseEntity<String> findUnUniqueSourceUrl() {
		List<NnChannel> channels = channelMngr.findUnUniqueSourceUrl();
		String result = this.printChannelData(channels);
		return NnNetUtil.textReturn(result);
	}
}