package com.nncloudtv.web.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

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
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryToNnSet;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.service.CategoryManager;
import com.nncloudtv.service.NnSetManager;

@Controller
@RequestMapping("admin/category")
public class AdminCategoryController {
	
	protected static final Logger logger = Logger.getLogger(AdminCategoryController.class.getName());
	
	private final CategoryManager categoryMngr;	
//	private final UserService     userService;
	
	@Autowired
	public AdminCategoryController(CategoryManager categoryMngr) {
		this.categoryMngr = categoryMngr;
//		this.userService  = UserServiceFactory.getUserService();
	}	

	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		NnLogUtil.logException(e);
		return "error/exception";				
	}

/*  no more channel in category	
	//list every channel under a category	
	@RequestMapping(value = "channelList", params = {"category", "page", "rows", "sidx", "sord"})
	public void channelList(@RequestParam(value = "category") Long         categoryId,
	                        @RequestParam(value = "page")     Integer      currentPage,
	                        @RequestParam(value = "rows")     Integer      rowsPerPage,
	                        @RequestParam(value = "sidx")     String       sortIndex,
	                        @RequestParam(value = "sord")     String       sortDirection,
	                                                          OutputStream out) {		
//		SubscriptionLogManager subLogMngr  = new SubscriptionLogManager();
//		CategoryChannelManager ccMngr      = new CategoryChannelManager();
		CategoryManager ccMngr      = new CategoryManager();
		NnChannelManager      channelMngr = new NnChannelManager();
		
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
		
		Category category = categoryMngr.findById(categoryId);
		if (category == null) {
			try {
				mapper.writeValue(out, JqgridHelper.composeJqgridResponse(1, 1, 0, new ArrayList<Map<String, Object>>()));
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
			return;
		}
		
		String filter = "categoryId == " + categoryId;
		int totalRecords = ccMngr.total(filter);
		int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
		if (currentPage > totalPages)
			currentPage = totalPages;
		
		List<CategoryChannel> results = ccMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
		
		for (CategoryChannel cc : results) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			List<Object> cell = new ArrayList<Object>();
			
			MsoChannel channel = channelMngr.findById(cc.getChannelId());
			SubscriptionLog subLog = subLogMngr.findByMsoIdAndChannelId(category.getMsoId(), cc.getChannelId());
			int subscribers = 0;
			if (subLog != null)
				subscribers = subLog.getCount();
			
			cell.add(cc.getCategoryId());
			cell.add(cc.getChannelId());
			cell.add(channel.getName());
			cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cc.getUpdateDate()));
			cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cc.getCreateDate()));
			cell.add(channel.isPublic());
			cell.add(channel.getLangCode());
			cell.add(channel.getContentType());
			cell.add(subscribers);
			
			map.put("id", cc.getKey().getId());
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
	
	@RequestMapping(value="addSet")
	public @ResponseBody String addSet(
			             @RequestParam(required = false) long category,
			             @RequestParam(required = false) long set,
	                     OutputStream out) {
		System.out.println("category:" + category + ";set=" + set);
//		CategoryChannelSetManager ccsMngr = new CategoryNnSet();
		NnSetManager csMngr = new NnSetManager();
		NnSet cs = csMngr.findById(set);
		Category cat = categoryMngr.findById(category);
		if (cs == null)
			return "Set does not exist";
		if (cat == null)
			return "Category does not exist";
/*
		CategoryChannelSet existed = ccsMngr.findByCategoryIdAndChannelSetId(category, set);		
		if (cs != null & existed == null) {
			CategoryChannelSet csc = new CategoryChannelSet(category,set);
			ccsMngr.create(csc);
		}
*/
		categoryMngr.addSet(cat, cs);
		return "OK";		
	}

	@RequestMapping(value="deleteSet")
	public @ResponseBody String deleteSet(
			             @RequestParam(required = false) long category,
			             @RequestParam(required = false) long id,
	                     OutputStream out) {		
//		CategoryChannelSetManager ccsMngr = new CategoryChannelSetManager();
		if(categoryMngr.deleteSet(category, id))
			return "OK";
		else
			return "there are nothing can delete";
	}
	
	//list every channel under a category	
	@RequestMapping(value = "listSet", params = {"category", "page", "rows", "sidx", "sord"})
	public void listSet(@RequestParam(value = "category") Long         categoryId,
	                    @RequestParam(value = "page")     Integer      currentPage,
	                    @RequestParam(value = "rows")     Integer      rowsPerPage,
	                    @RequestParam(value = "sidx")     String       sortIndex,
	                    @RequestParam(value = "sord")     String       sortDirection,
	                    OutputStream out) {
//		System.out.println("hit setList");		
//		CategoryChannelManager ccMngr      = new CategoryChannelManager();
//		System.out.println(categoryId);
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
		
		Category category = categoryMngr.findById(categoryId);
		if (category == null) {
			try {
				mapper.writeValue(out, JqgridHelper.composeJqgridResponse(1, 1, 0, new ArrayList<Map<String, Object>>()));
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
			return;
		}
		
		String filter = "categoryId == " + categoryId;
		int totalRecords = categoryMngr.totalCatToSet(filter);
		int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
		if (currentPage > totalPages)
			currentPage = totalPages;

		//check if data exist
		List<NnSet> list = categoryMngr.findSetsByCategory(categoryId, false);
		if(list == null) {
			System.out.println("its null");
		} else {
			System.out.println("its not null");
			for (NnSet aSet : list)
				System.out.println(aSet.getName());
		}
		
		List<CategoryToNnSet> listCatToSet =  categoryMngr.listCatToSet(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
		NnSetManager setMngr = new NnSetManager();
		NnSet set = null;
		
		for (CategoryToNnSet catToSet : listCatToSet) {
			System.out.println(catToSet.getSetId());
			set = setMngr.findById(catToSet.getSetId());
			
			Map<String, Object> map = new HashMap<String, Object>();
			List<Object> cell = new ArrayList<Object>();
//			NnSet set = csMngr.findById(ccs.getChannelSetId());
			if (set != null) {
				cell.add(categoryId);
				cell.add(set.getId());
				cell.add(set.getName());
				cell.add(set.isPublic());
				cell.add(set.getLang());
				cell.add(set.getSeq());
				map.put("id", set.getId());
				map.put("cell", cell);
				dataRows.add(map);
			}
		}
		
		try {
			mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}

//  no more msoid in category model
	@RequestMapping("list")
	public ResponseEntity<String> list(@RequestParam(required=false)String mso) {		
		List<Category> categories = null;
//		if (mso == null) {
			categories = categoryMngr.findAll();
//		} else {	
//			categories = categoryMngr.findAllByMsoId(Long.parseLong(mso));
//		}
		
		String[] title = {"id", "isPublic", "channelCount", "name"};
		String result = "";
		for (Category c:categories) {
			String[] ori = {String.valueOf(c.getId()),
//			                Long.toString(c.getMsoId()),
			                Boolean.toString(c.isPublic()),
			                String.valueOf(c.getChannelCnt()),
			                c.getName()};
			result = result + NnStringUtil.getDelimitedStr(ori);
			result = result + "\n";
		}
		String output = NnStringUtil.getDelimitedStr(title) + "\n" + result;
		return NnNetUtil.textReturn(output);
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
			
			if (searchField.equals("lang")) {
				searchString = NnStringUtil.escapedQuote(searchString);
			}
			
			Map<String, String> opMap = JqgridHelper.getOpMap();
			if (opMap.containsKey(searchOper)) {
				filter = searchField + " " + opMap.get(searchOper) + " " + searchString;
				logger.info("filter: " + filter);
			}
		}
		
		int totalRecords = categoryMngr.total(filter);
		int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
		if (currentPage > totalPages)
			currentPage = totalPages;
		
		List<Category> results = categoryMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
		
		for (Category category : results) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			List<Object> cell = new ArrayList<Object>();
			
			//cell.add(category.getMsoId());
			cell.add(category.getId());
			//cell.add(category.getParentId());
			cell.add(category.getName());
			cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(category.getUpdateDate()));
			//cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(category.getCreateDate()));
			cell.add(category.getLang());
			cell.add(category.isPublic());
			cell.add(category.getSeq());
			cell.add(category.getChannelCnt());
			
			map.put("id", category.getId());
			map.put("cell", cell);
			dataRows.add(map);
		}
		
		try {
			mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}
	
	@RequestMapping("create") 	
	public @ResponseBody String create(@RequestParam(required=true) String name,
									   @RequestParam(required=true) String lang,
									   @RequestParam String seq,
									   @RequestParam(required=false) String isPublic) {
	                                   //@RequestParam(required=true) String setIds
		//System.out.println("hit category create");
		Category category = new Category(name, Boolean.parseBoolean(isPublic));
		category.setLang(lang);
		category.setPublic(Boolean.parseBoolean(isPublic));
		categoryMngr.create(category);
		this.adjustSeq(category, seq);
		/*
		ChannelSetManager csMngr = new ChannelSetManager();
		CategoryChannelSetManager ccsMngr = new CategoryChannelSetManager();
	    List<CategoryChannelSet> list = new ArrayList<CategoryChannelSet>();
	    //String[] ids = setIds.split(",");
	    for (int i=0; i<ids.length; i++) {
	    	ChannelSet cs = csMngr.findById(Long.parseLong(ids[i]));
	    	CategoryChannelSet csc = new CategoryChannelSet(category.getKey().getId(), cs.getKey().getId());	    	
	    	list.add(csc);
	    }
	    ccsMngr.saveAll(list);
	    */
		return "OK";
	}

	@RequestMapping("delete")
	public @ResponseBody String delete(
			@RequestParam(required=true)  Long    id) {
		Category category = categoryMngr.findById(id);
		categoryMngr.deleteCatToSetByCatId(category.getId());
		categoryMngr.delete(category);
		return "OK";
	}
	
	private void adjustSeq(Category category, String seq) {				
		//swap
		Category toBeSwapped = categoryMngr.findByLangAndSeq(category.getLang(), seq);
		if (toBeSwapped != null) {			
			toBeSwapped.setSeq(category.getSeq());
			category.setSeq(Short.parseShort(seq));
			categoryMngr.save(category);
			categoryMngr.save(toBeSwapped);
		} else {
			category.setSeq(Short.parseShort(seq));
			categoryMngr.save(category);
		}
	}
	
	@RequestMapping("edit")
	public @ResponseBody String edit(
			@RequestParam(required=true)  Long    id,
			@RequestParam(required=false) String  name,			
	        @RequestParam(required=false) Boolean isPublic,
	        @RequestParam(required=false) String  seq,
	        @RequestParam(required=false) String  lang) {
	        //@RequestParam(required=false) String setIds		
		
		System.out.println("seq:" + seq);
//		logger.info("admin = " + userService.getCurrentUser().getEmail());		
		Category category = categoryMngr.findById(id);
		if (category == null) {
			String error = "Category Not Found";
			logger.warning(error);
			return error;
		}		
		if (name != null) {
			logger.info("name = " + name);
			category.setName(name);
		}
		if (isPublic != null) {
			logger.info("isPublic = " + isPublic);
			category.setPublic(isPublic);
		}
		/*
		if (setIds != null) {
			ChannelSetManager csMngr = new ChannelSetManager();
			CategoryChannelSetManager ccsMngr = new CategoryChannelSetManager();
			List<CategoryChannelSet> oldList = ccsMngr.findAllByCategoryId(id);
			ccsMngr.deleteAll(oldList);
		    List<CategoryChannelSet> list = new ArrayList<CategoryChannelSet>();
		    String[] ids = setIds.split(",");
			categoryMngr.create(category);		
		    for (int i=0; i<ids.length; i++) {
		    	ChannelSet cs = csMngr.findById(Long.parseLong(ids[i]));
		    	CategoryChannelSet csc = new CategoryChannelSet(category.getKey().getId(), cs.getKey().getId());	    	
		    	list.add(csc);
		    }
		    ccsMngr.saveAll(list);			
		}
		*/
		if (lang != null) {
			logger.info("lang = " + lang);
			category.setLang(lang);
		}		
		categoryMngr.save(category);
		this.adjustSeq(category, seq);
		return "OK";
	}
	
	@RequestMapping("categoriesHtmlSelectOptions")
	public void categoriesHtmlSelectOptions(HttpServletResponse response, OutputStream out) {
		
		response.setContentType("text/html;charset=utf-8");
		OutputStreamWriter writer;
		try {
			writer = new OutputStreamWriter(out, "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			return;
		}
		List<Category> categories = categoryMngr.findAll();
		try {
			writer.write("<select>");
			for (Category category : categories)
				writer.write("<option value=\"" + category.getId() + "\">" + category.getName() + "</option>");
			writer.write("</select>");
			writer.close();
		} catch (IOException e) {
			return;
		}
	}
}
