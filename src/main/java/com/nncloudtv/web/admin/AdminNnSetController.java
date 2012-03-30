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

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.JqgridHelper;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnSetToNnChannel;
import com.nncloudtv.service.CategoryManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnSetChannelManager;
import com.nncloudtv.service.NnSetManager;

@Controller
@RequestMapping("admin/set")
public class AdminNnSetController {
	protected static final Logger logger = Logger.getLogger(AdminNnSetController.class.getName());
	
	public final NnSetManager setMngr;
	
	@Autowired
	public AdminNnSetController(NnSetManager setMngr) {
		this.setMngr = setMngr;		
	}	
	
	@RequestMapping(value = "list", params = {"page", "rows", "sidx", "sord"})
	public void list(@RequestParam(value = "page")   Integer      currentPage,
	                 @RequestParam(value = "rows")   Integer      rowsPerPage,
	                 @RequestParam(value = "sidx")   String       sortIndex,
	                 @RequestParam(value = "sord")   String       sortDirection,
	                 @RequestParam(required = false) String       searchField,
	                 @RequestParam(required = false) String       searchOper,
	                 @RequestParam(required = false) String       searchString,
	                 @RequestParam(required = false) String       search,
	                 @RequestParam(required = false) boolean      notify,
	                 OutputStream out) {
		NnSetManager csMngr = new NnSetManager();
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();

		String filter = "";
		if (searchField != null && searchOper != null && searchString != null && !searchString.isEmpty()) {			
			Map<String, String> opMap = JqgridHelper.getOpMap();
			if (opMap.containsKey(searchOper)) {
				filter = searchField + " " + opMap.get(searchOper) + " " + searchString;
				logger.info("filter: " + filter);
				sortIndex = "updateDate";
				sortDirection = "desc";
			}
		}

		int totalRecords = csMngr.total(filter);
		int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
		if (currentPage > totalPages)
			currentPage = totalPages;		
		List<NnSet> results = csMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		for (NnSet cs : results) {			
			Map<String, Object> map = new HashMap<String, Object>();
			List<Object> cell = new ArrayList<Object>();
			boolean qualified = true;
			if (notify) {
				qualified = false;
				Calendar cal = Calendar.getInstance();		
				cal.add(Calendar.DAY_OF_MONTH, - 14);
				Date d = cal.getTime();
				if (cs.getCreateDate().after(d)) {
					if (!cs.isPublic()) {
						qualified = true;
					} else {
						List<NnSetToNnChannel> cscs = cscMngr.findBySet(cs.getId());
						for (NnSetToNnChannel csc : cscs) {
							if (csc.getCreateDate().after(d)) {
								qualified = true;
							}
						}
					}
				}
			}
			if (qualified) {
				cell.add(cs.getId());
				cell.add(cs.getName());
				cell.add(cs.getIntro());
				cell.add(cs.isFeatured());
				cell.add(cs.isPublic());
				cell.add(cs.getLang());
				cell.add(cs.getSeq());
				cell.add(cs.getBeautifulUrl());
				cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cs.getUpdateDate()));
				cell.add(cs.getImageUrl());
				map.put("id", cs.getId());
				map.put("cell", cell);
				dataRows.add(map);
			}
		}
		try {
			mapper.writeValue(out, JqgridHelper.composeJqgridResponse(1, 50, totalRecords, dataRows));
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
				
	}

	public void channelSeqAdjust(NnSet set, NnChannel channel, int seq, boolean delete, boolean edit) {
		if (!set.isFeatured())
			return;
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		List<NnSetToNnChannel> list = cscMngr.findBySet(set.getId());		
		for (NnSetToNnChannel csc : list) {
			if (!delete && csc.getSeq() >= seq) {
				System.out.println("enter correct adjustment:" + csc.getSeq() + ";" + csc.getSeq()+1);
				csc.setSeq((short)(csc.getSeq() + 1));
				cscMngr.save(csc);
			}			
			if (delete) {
				if (csc.getSeq() >= seq) {
					if (csc != null) {
						csc.setSeq((short)(csc.getSeq() - 1));
						cscMngr.save(csc);
					}
				}				
			}
			
		}
		if (!delete) {
			NnSetToNnChannel csc = cscMngr.findBySetAndChannel(set.getId(), channel.getId());
			if (csc == null) { //add
				csc = new NnSetToNnChannel(set.getId(), channel.getId(), (short)seq);
				cscMngr.create(csc);
			} else {
				if (!edit) {
					csc.setSeq((short)(csc.getSeq() + 1));
				} else {
					csc.setSeq(csc.getSeq());
				}
				cscMngr.save(csc);
			}
		}
	}
	
	@RequestMapping(value="deleteCh", params = {"id", "set"})
	public @ResponseBody String deleteCh(@RequestParam(required = false) long set,	                 
			             @RequestParam(required = false) long id,
	                     OutputStream out) {
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		NnSetToNnChannel csc = cscMngr.findBySetAndChannel(set, id);
		if (csc != null) {
			int seq = csc.getSeq();
			cscMngr.delete(csc);
			NnSet cs = new NnSetManager().findById(set);
			NnChannel c = new NnChannelManager().findById(id);
			this.channelSeqAdjust(cs, c, seq, true, false);
		}
		return "OK";		
	}

	@RequestMapping(value="addCh")
	public @ResponseBody String addCh(
			             @RequestParam(required = false) long channel,
			             @RequestParam(required = false) long set,	                 
			             @RequestParam(required = false) int seq,
	                     OutputStream out) {
		NnSetManager csMngr = new NnSetManager();
		NnChannelManager cMngr = new NnChannelManager();
		NnChannel c = cMngr.findById(channel);
		if (c == null)
			return "Channel does not exist";
		NnSet cs = csMngr.findById(set);
		if (cs == null)
			return "Set does not exist";
		/*
		NnSetToNnChannel csc = cscMngr.findBySetAndChannel(set, channel);		
		if (csc == null) {
			System.out.println("enter empty?:" + set + ";" + channel + ";" + seq);
			csc = new NnSetToNnChannel(set, channel, seq);
			cscMngr.create(csc);
		}
		*/
		this.channelSeqAdjust(cs, c, seq, false, false);
		return "OK";		
	}	

	@RequestMapping(value="editCh")
	public @ResponseBody String editCh(
			             @RequestParam(required = false) long id,
			             @RequestParam(required = false) long channel,
			             @RequestParam(required = false) long set,	                 
			             @RequestParam(required = false) int seq,
	                     OutputStream out) {
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		NnSetToNnChannel csc = cscMngr.findBySetAndChannel(set, id);
		if (csc != null) {
			NnSetToNnChannel c1 = cscMngr.findBySetAndChannel(set, channel);
			NnSetToNnChannel c2 = cscMngr.findBySetAndSeq(set, (short) seq);
			if (c1 != null && c2 != null) {
				c2.setSeq(c1.getSeq());
				c1.setSeq((short)seq);
			}
			/*
			//this.channelSeqAdjust(cs, c, seq, false, true);
		NnChannelManager cMngr = new NnChannelManager();
		NnSetManager csMngr = new NnSetManager();
			NnChannel c = cMngr.findById(channel);
			if (c != null) {
				csc.setChannelId(channel);
				csc.setSeq(seq);
			}
			*/
		}
		return "OK";		
	}
	
	//seq, desired seq
	private void adjustSeq(NnSet set, String seq) {		
		NnSetManager csMngr = new NnSetManager();
		if (!set.isFeatured()) {
			set.setSeq((short)0);
			csMngr.save(set);
			return;
		}
		System.out.println("original:" + set.getSeq());
		System.out.println("new destination:" + seq);

		//swap
		NnSet toBeSwapped = csMngr.findByLangAndSeq(set.getLang(), seq);
		if (toBeSwapped != null) {			
			toBeSwapped.setSeq(set.getSeq());
			set.setSeq(Short.parseShort(seq));
			csMngr.save(set);
			csMngr.save(toBeSwapped);
		} else {
			set.setSeq(Short.parseShort(seq));
			csMngr.save(set);
		}
		
	}
	
	@RequestMapping(value = "listCh", params = {"page", "rows", "sidx", "sord", "set"})
	public @ResponseBody String listCh(
			         @RequestParam(value = "page")   Integer      currentPage,
	                 @RequestParam(value = "rows")   Integer      rowsPerPage,
	                 @RequestParam(value = "sidx")   String       sortIndex,
	                 @RequestParam(value = "sord")   String       sortDirection,
	                 @RequestParam(required = false) String       searchField,
	                 @RequestParam(required = false) String       searchOper,
	                 @RequestParam(required = false) String       searchString,
	                 @RequestParam(required = false) long       set,
	                 OutputStream out) {
		NnSetManager csMngr = new NnSetManager();
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
		NnSet cs = csMngr.findById(set);
		List<NnChannel> channels = csMngr.findChannelsById(cs.getId());
		for (NnChannel c : channels) {
			Map<String, Object> map = new HashMap<String, Object>();
			List<Object> cell = new ArrayList<Object>();			
			cell.add(c.getId());
			cell.add(c.getName());
			cell.add(c.getSeq());
			cell.add(c.getContentType());
			map.put("id", c.getId());
			map.put("cell", cell);
			dataRows.add(map);			
		}
		int totalRecords = channels.size();
		try {
			mapper.writeValue(out, JqgridHelper.composeJqgridResponse(1, 50, totalRecords, dataRows));
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
		return "OK";		
	}

	/*
	@RequestMapping(value = "create", params = {"name", "intro", "featured", "lang"})
	public @ResponseBody String create(@RequestParam String name,	                                   
	*/
	@RequestMapping("delete")
	public @ResponseBody String delete(@RequestParam(required=false) long id) {
		NnSetManager csMngr = new NnSetManager();
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		CategoryManager cMngr = new CategoryManager();
		NnSet cs = csMngr.findById(id);		
		List<NnSetToNnChannel> list = cscMngr.findBySet(cs.getId());
		cscMngr.deleteAll(list);	
				
//		CategoryChannelSetManager ccsMngr = new CategoryChannelSetManager();
//		List<Category> list2 = cMngr.findBySet(cs);
		cMngr.deleteCatToSetBySetId(id);
		
		csMngr.delete(cs);
		return "OK";
	}

	@RequestMapping("edit")
	public @ResponseBody String edit(
			@RequestParam(required=false) long id,
			@RequestParam(required=false) String name,
            @RequestParam(required=false) String desc,
            @RequestParam(required=false) String lang,
            @RequestParam(required=false) String imageUrl,
            @RequestParam(required=false) String beautifulUrl,
            @RequestParam(required=false) String isPublic,
            @RequestParam(required=false) String featured,
            @RequestParam(required=false) String channelIds,
            @RequestParam(required=false) String seq) {
		NnSetManager csMngr = new NnSetManager();
		NnChannelManager cMngr = new NnChannelManager();
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		NnSet cs = csMngr.findById(id);
		if (name != null) cs.setName(name);
		if (desc != null) cs.setIntro(desc);
		if (isPublic != null)
			cs.setPublic(Boolean.parseBoolean(isPublic));
		if (featured != null)			
			cs.setFeatured(Boolean.parseBoolean(featured));
		if (imageUrl != null)
			cs.setImageUrl(imageUrl);
		if (beautifulUrl != null)
			cs.setBeautifulUrl(beautifulUrl);
		cs.setLang(lang);
		if (seq != null) {
			/*
			System.out.println("seqs:" + seq);
			cs.setSeq(Short.parseShort(seq));
			*/
		}		
		csMngr.save(cs);
		this.adjustSeq(cs, seq);
		this.addRecCategory(cs);

		if (channelIds == null) 
			return "OK";
		
		String[] chId = channelIds.split(",");
		String[] chSeq = seq.split(",");		

		List<NnSetToNnChannel> list = cscMngr.findBySet(cs.getId());
		cscMngr.deleteAll(list);
		list = new ArrayList<NnSetToNnChannel>();
		for (int i=0; i<chId.length; i++) {
			NnChannel c = cMngr.findById(Long.parseLong(chId[i]));			
			NnSetToNnChannel csc = new NnSetToNnChannel(cs.getId(), c.getId(), Short.parseShort(chSeq[i]));
			csc.setCreateDate(new Date());
			list.add(csc);
		}
		cscMngr.saveAll(list);
		
		CategoryManager catMngr = new CategoryManager();
//		CategoryChannelSetManager ccsMngr = new CategoryChannelSetManager();		
		
		if (cs.getLang().equals("zh") && cs.getName().equals("綜合推薦")) {
			Category cat = catMngr.findByName("推薦頻道");
			if (cat != null) {
/*				
				CategoryToNnSet existed = 
					catMngr.findByCategoryAndSet(cat, cs);
				if (existed == null) {
					CategoryToNnSet ccs = new CategoryToNnSet(cat.getId(), cs.getId());
					ccsMngr.create(ccs);
				}
*/
				catMngr.addSet(cat, cs);
			}				
		}
		if (cs.getLang().equals("en") && cs.getName().equals("Recommended")) {
			Category cat = catMngr.findByName("Recommended");
			if (cat != null) {
				if (cat != null) {
/*					
					CategoryToNnSet existed = 
						ccsMngr.findByCategoryIdAndChannelSetId(cat.getKey().getId(), cs.getKey().getId());
					if (existed == null) {
						CategoryToNnSet ccs = new CategoryToNnSet(cat.getId(), cs.getId());
						ccsMngr.create(ccs);
					}
*/
					catMngr.addSet(cat, cs);
				}
			}				
		}
		this.adjustSeq(cs, seq);		
		return "OK";
	}
	
	//	@RequestMapping(value = "create", params = {"name", "contactEmail", "password", "logoUrl", "type"})
	/*
	public @ResponseBody String create(@RequestParam String name,
            @RequestParam String contactEmail,
            @RequestParam String password,
            @RequestParam String logoUrl,
            @RequestParam Short  type) {		
	}	
	*/	
	
	private void addRecCategory(NnSet cs) {
		CategoryManager catMngr = new CategoryManager();
//		CategoryChannelSetManager ccsMngr = new CategoryChannelSetManager();
		if (cs.getLang().equals("zh") && cs.isFeatured()) {
			Category cat = catMngr.findByName("推薦頻道");
			if (cat != null) {
/*
				CategoryToNnSet existed = 
					ccsMngr.findByCategoryIdAndChannelSetId(cat.getKey().getId(), cs.getKey().getId());
				if (existed == null) {
					CategoryToNnSet ccs = new CategoryToNnSet(cat.getId(), cs.getId());
					ccsMngr.create(ccs);
				}
*/
				catMngr.addSet(cat, cs);
			}				
		}
		if (cs.getLang().equals("en") && cs.isFeatured()) {
			Category cat = catMngr.findByName("Recommended");
			if (cat != null) {
				if (cat != null) {
/*
					CategoryToNnSet existed = 
						ccsMngr.findByCategoryIdAndChannelSetId(cat.getKey().getId(), cs.getKey().getId());
					if (existed == null) {
						CategoryToNnSet ccs = new CategoryToNnSet(cat.getId(), cs.getId());
						ccsMngr.create(ccs);
					}
*/
					catMngr.addSet(cat, cs);
				}
			}				
		}
	}
	
	@RequestMapping(value = "create", params = {"name", "intro", "featured", "lang", "imageUrl", "beautifulUrl", "seq"})
	public @ResponseBody String create(@RequestParam String name,	                                   
	                                   @RequestParam String intro,
	                                   @RequestParam boolean featured,
	                                   @RequestParam String imageUrl,
	                                   @RequestParam String beautifulUrl,
	                                   @RequestParam String seq,
	                                   @RequestParam String lang) {
		NnSetManager setMngr = new NnSetManager();
		NnSet set = new NnSet(name, intro, true);
		set.setBeautifulUrl(name);
		set.setLang(lang);		
		set.setBeautifulUrl(beautifulUrl);
		set.setImageUrl(imageUrl);
		set.setFeatured(featured);
		setMngr.create(set, null);
		this.addRecCategory(set);
		this.adjustSeq(set, seq);
		return "OK";		
	}
		
}
