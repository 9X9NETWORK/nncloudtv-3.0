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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.JqgridHelper;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryToNnSet;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnSetToNnChannel;
import com.nncloudtv.model.ContentOwnership;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
//import com.nncloudtv.service.CategoryChannelSetManager;
import com.nncloudtv.service.CategoryManager;
import com.nncloudtv.service.NnSetChannelManager;
import com.nncloudtv.service.NnSetManager;
import com.nncloudtv.service.ContentOwnershipManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.MsoManager;

@Controller
@RequestMapping("admin/set")
public class AdminNnSetController {
	protected static final Logger logger = Logger.getLogger(AdminNnSetController.class.getName());
	
	public final NnSetManager setMngr;
	
	@Autowired
	public AdminNnSetController(NnSetManager setMngr) {
		this.setMngr = setMngr;		
	}
/*	there are no mso in nnset model	
	@RequestMapping("deleteMso")
	public ResponseEntity<String> deleteMso(@RequestParam(required=false) long id) {					
		NnSetManager csMngr = new NnSetManager();
		List<NnSet> sets = csMngr.findByMso(id);
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		for (NnSet cs : sets) {
			List<NnSetToNnChannel> list = cscMngr.findBySet(cs.getId());
			cscMngr.deleteAll(list);
			csMngr.delete(cs);		
		}
		return NnNetUtil.textReturn("OK");
	}
*/
	@RequestMapping("changeSet")
	public ResponseEntity<String> changeSet() {
		String[] urls = {
				"http://www.youtube.com/user/achun5",
				"http://www.youtube.com/user/luckyboommini",
				"http://www.youtube.com/user/wondergirls",
				"http://www.youtube.com/user/beyonceVEVO",
				"http://www.youtube.com/user/linkinparktv",
				"http://www.youtube.com/user/ladygagavevo",
				"http://www.youtube.com/user/KatyPerryVEVO",
				"http://www.youtube.com/user/CAguileraVevo",
				"http://www.youtube.com/user/RihannaVevo",
		};
		NnSetManager setMngr = new NnSetManager();
		NnChannelManager channelMngr = new NnChannelManager();
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		NnSet set = setMngr.findByName("本周主打星");
		if (set != null) {
			List<NnSetToNnChannel> list = cscMngr.findBySet(set.getId());
			cscMngr.deleteAll(list);
			List<NnChannel> channels = new ArrayList<NnChannel>();
			for (int i=0; i<urls.length; i++) {
				String checkedUrl = YouTubeLib.formatCheck(urls[i]);
				NnChannel c = channelMngr.findBySourceUrl(checkedUrl);
				channels.add(c);
			}
			int i=1;
			for (NnChannel c : channels) {			
				NnSetToNnChannel csc = new NnSetToNnChannel(set.getId(), c.getId(), (short)i);
				i++;
				cscMngr.save(csc);
			}
		}
		return NnNetUtil.textReturn("OK");
		
	}

	@RequestMapping("createBatch2")
	public ResponseEntity<String> createBatch2() {
		String name="頻道策展決賽";
		
		String lang="zh";
		String desc="展示一下你的策展talent! 精彩決賽頻道就在這！";
		String cname="教育學習";
		
		NnChannelManager channelMngr = new NnChannelManager();
		List<NnChannel> channels = new ArrayList<NnChannel>();
		String[] urls = {
				"4599054",
				"4562966",
				"4553178",
				"4420853",
				"http://www.youtube.com/playlist?list=PL3FC1CEA86F082B94",
				"4529036",
				"4574781",
				"4570843",
				"4565833"
		};
		NnSetManager csMngr = new NnSetManager();
		NnSet cs = csMngr.findByName(name);
		if (cs == null) {
			return NnNetUtil.textReturn("channel set zero");
		}
		NnSetChannelManager cscMngr = new NnSetChannelManager();
		for (int i=0; i<urls.length; i++) {
			String checkedUrl = urls[i];
			System.out.println("checked url:" + checkedUrl);
			if (checkedUrl.contains("youtube.com")) {
				checkedUrl = YouTubeLib.formatCheck(checkedUrl);
				NnChannel c = channelMngr.findBySourceUrl(checkedUrl);
				//c.setSeq(i+1+20);
				if (c == null) {
					System.out.println("---id null---");
				}
				channels.add(c);
			} else {
				NnChannel c = channelMngr.findById(Long.parseLong(urls[i]));
				if (c == null) {
					System.out.println("---id null---");
				}
				//c.setSeq(i+1+20);
				channels.add(c);				
			}
		}
		String output = "";
		int i = 20;
		System.out.println("channels size:" + channels.size());
		for (NnChannel c : channels) {
			NnSetToNnChannel csc = new NnSetToNnChannel(cs.getId(), c.getId(), (short)i);
			cscMngr.create(csc);
			i++;
		}
		
		/*
		channelSet.setDefaultUrl(name); 
		channelSet.setBeautifulUrl(name);
		//related channels
		channelSet.setLang(lang);
		channelSetMngr.create(channelSet, channels);
		*/						
		
		//category and set
		/*
		CategoryChannelSetManager cscMngr = new CategoryChannelSetManager();
		CategoryManager cMngr = new CategoryManager();
		Category c = cMngr.findByName(cname);
		CategoryChannelSet csc = new CategoryChannelSet(c.getKey().getId(), channelSet.getKey().getId());
		cscMngr.save(csc);
		*/		
		return NnNetUtil.textReturn("OK");
		
	}
	
	@RequestMapping("createBatch")
	public ResponseEntity<String> createBatch() {
		String name="頻道策展決賽";
		String lang="zh";
		String desc="展示一下你的策展talent! 精彩決賽頻道就在這！";
		String cname="教育學習";
		
		String[] urls = {
				"http://www.9x9.tv/view?channel=4575605",
				"http://www.9x9.tv/view?channel=4503257", 
				"http://www.9x9.tv/view?channel=4324184",
				"http://www.9x9.tv/view?channel=4321246",
				"http://www.9x9.tv/view?channel=4229572",
				"http://www.9x9.tv/view?channel=4430392",
				"http://www.9x9.tv/view?channel=4562725",
				"http://www.9x9.tv/view?channel=4420612",
				"http://www.9x9.tv/view?channel=4541346",
				"http://www.9x9.tv/view?channel=4394854",
				"http://www.9x9.tv/view?channel=4426972",
				"http://www.9x9.tv/view?channel=4429711",
				"http://www.youtube.com/user/yintuosi",
				"http://www.9x9.tv/view?channel=4560416",
				"http://www.9x9.tv/view?channel=4583084",
				"http://www.9x9.tv/view?channel=4570947",
				"www.9x9.tv/view?channel=4565104",
				"http://www.9x9.tv/view?channel=4519095",
				"http://www.9x9.tv/view?channel=4576913",
		};
		NnChannelManager channelMngr = new NnChannelManager();
		List<NnChannel> channels = new ArrayList<NnChannel>();
		for (int i=0; i<urls.length; i++) {
			String checkedUrl = urls[i];
			if (!checkedUrl.contains("maplestage") && !checkedUrl.contains("9x9.tv")) {
				checkedUrl = YouTubeLib.formatCheck(urls[i]);
			}
			NnChannel c = channelMngr.findBySourceUrl(checkedUrl);
			c.setSeq((short)(i+1));
			channels.add(c);
		}
		String output = "";
		for (NnChannel c : channels) {
			output += c.getId() + "\t" + c.getSourceUrl() + "\n";
		}
		Mso mso = new MsoManager().findNNMso();
		NnSetManager channelSetMngr = new NnSetManager();
		NnSet channelSet = new NnSet(name, desc, true);
//		channelSet.setDefaultUrl(name); 
		channelSet.setBeautifulUrl(name);
		//related channels
		channelSet.setLang(lang);
		channelSetMngr.create(channelSet, channels);						
		
		//category and set		
//		CategoryChannelSetManager cscMngr = new CategoryChannelSetManager();
		CategoryManager cMngr = new CategoryManager();
		Category c = cMngr.findByName(cname);
/*
		CategoryToNnSet csc = new CategoryToNnSet(c.getId(), channelSet.getId());
		cMngr.save(csc);
*/
		cMngr.addSet(c, channelSet);
		return NnNetUtil.textReturn("OK");
	}

	@RequestMapping("recommendedChange")
	public ResponseEntity<String> recommendedChange(
			@RequestParam(required=false) String lang,
			@RequestParam(required=false) String ids,
			@RequestParam(required=false) String seqs) {

		String[] id = ids.split(",");
		String[] seq = seqs.split(",");
		
		//either turn off a recommended
		//if it's on, then re-arrange the sequence of everything
		NnSetManager channelSetMngr = new NnSetManager();
		List<NnSet> list = channelSetMngr.findFeaturedSets(lang);
		for (NnSet cs : list) {
			cs.setFeatured(false);
		}
		channelSetMngr.saveAll(list);		
		list = new ArrayList<NnSet>();
		for (int i=0; i<id.length; i++) {
			NnSet cs = channelSetMngr.findById(Long.parseLong(id[i]));
			cs.setSeq(Short.parseShort(seq[i]));
			cs.setFeatured(true);
			list.add(cs);
		}
		channelSetMngr.saveAll(list);
		return NnNetUtil.textReturn("OK");
	}

	@RequestMapping("recommendedList")
	public ResponseEntity<String> recommendedList() {							
		NnSetManager channelSetMngr = new NnSetManager();
		List<NnSet> list = new ArrayList<NnSet>();
		list.addAll(channelSetMngr.findFeaturedSets(LangTable.LANG_EN));
		list.addAll(channelSetMngr.findFeaturedSets(LangTable.LANG_ZH));
		String output = "";
		for (NnSet cs : list) {
			output += cs.getId() + "\t" + cs.getName() + "\n";
		}
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
		NnSetChannelManager cscMngr = new NnSetChannelManager();
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
		Mso mso = new MsoManager().findNNMso();
		NnSetManager channelSetMngr = new NnSetManager();
		NnSet cs = new NnSet(name, intro, true);		
//		cs.setDefaultUrl(name); 
		cs.setBeautifulUrl(name);
		cs.setLang(lang);		
		cs.setBeautifulUrl(beautifulUrl);
		cs.setImageUrl(imageUrl);
		cs.setFeatured(featured);
		channelSetMngr.create(cs, null);
		this.addRecCategory(cs);
		this.adjustSeq(cs, seq);
		return "OK";		
	}
	
	@RequestMapping("createTest")
	public ResponseEntity<String> createTest(@RequestParam(required=false) String name, 			                             
			                             @RequestParam(required=false) String desc,
			                             @RequestParam(required=false) String channelIds,
			                             @RequestParam(required=false) String seqs,
			                             @RequestParam(required=false) String lang,
			                             @RequestParam(required=false) String imageUrl,
			                             @RequestParam(required=false) String categoryIds) {
		//set info
		Mso mso = new MsoManager().findNNMso();
		NnSetManager channelSetMngr = new NnSetManager();
		NnSet channelSet = new NnSet(name, desc, true);
//		channelSet.setDefaultUrl(name); 
		channelSet.setBeautifulUrl(name);
		//related channels
		NnChannelManager channelMngr = new NnChannelManager();
		String[] chArr = channelIds.split(",");
		String[] seqArr = seqs.split(",");
		List<Long> list = new ArrayList<Long>();
		for (int i=0; i<chArr.length; i++) { list.add(Long.valueOf(chArr[i]));}
		List<NnChannel> channels = channelMngr.findAllByChannelIds(list);
		for (int i=0; i<channels.size(); i++) {
			channels.get(i).setSeq(Short.valueOf(seqArr[i]));
		}
		channelSet.setLang(lang);
		channelSet.setImageUrl(imageUrl);
		channelSetMngr.create(channelSet, channels);						
		
		//channelSet ownership
		ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
		ownershipMngr.create(new ContentOwnership(), mso, channelSet);
		
		//category and set
		String[] categoryArr = categoryIds.split(",");
//		CategoryChannelSetManager cscMngr = new CategoryChannelSetManager();
		CategoryManager cMngr = new CategoryManager();
		List<Category> categories = new ArrayList<Category>();
		for (int i=0; i<categoryArr.length; i++) {
			Category c = cMngr.findById(Long.parseLong(categoryArr[i]));
			categories.add(c);
		}
		for (Category c : categories) {
/*
			CategoryToNnSet csc = new CategoryToNnSet(c.getId(), channelSet.getId());			
			cscMngr.save(csc);
*/		
			cMngr.addSet(c, channelSet);
		}
		return NnNetUtil.textReturn("OK");
	}	

	@RequestMapping("createTest")
	public ResponseEntity<String> createTest(@RequestParam(required=false) String name, 			                             
			                             @RequestParam(required=false) String desc,
			                             @RequestParam(required=false) String channelIds,
			                             @RequestParam(required=false) String seqs,
			                             @RequestParam(required=false) String lang,
			                             @RequestParam(required=false) String categoryIds) {
		//set info
		name="Most Popular this Week";
		desc = "All your favorites in one place!";
		
		Mso mso = new MsoManager().findNNMso();
		NnSetManager channelSetMngr = new NnSetManager();
		NnSet channelSet = new NnSet(name, desc, true);
//		channelSet.setDefaultUrl(name); 
		channelSet.setBeautifulUrl(name);
		//related channels
		NnChannelManager channelMngr = new NnChannelManager();
		String[] urls = {
				"http://www.youtube.com/user/TEDtalksDirector",
				"http://www.youtube.com/user/AtGoogleTalks",
				"http://www.youtube.com/user/ReutersVideo",
				"http://www.youtube.com/user/AssociatedPress",
				"http://www.youtube.com/user/JimmyKimmelLive",
				"http://www.youtube.com/user/journeymanpictures",
				"http://www.youtube.com/user/NASAtelevision",
				"http://www.youtube.com/user/NewYorkerDotCom",
				"http://www.youtube.com/user/ComputerHistory",
				"http://www.youtube.com/user/animalplanetTV",                                              
				"http://www.youtube.com/user/NBA",
				"http://www.youtube.com/user/bigthink",
				"http://www.youtube.com/user/ResearchChannel",
				"http://www.youtube.com/user/boyceavenue",
				"http://www.youtube.com/user/growingyourgreens",
				"http://www.youtube.com/user/ThirteenWNET",
				"http://www.youtube.com/user/Autoexpress",
				"http://www.youtube.com/user/PulitzerCenter",
				"http://www.youtube.com/user/richarddawkinsdotnet",
				"http://www.youtube.com/user/FunnyorDie",
				"http://www.youtube.com/reelzchannel",
				"http://www.youtube.com/user/realannoyingorange",
				"http://www.youtube.com/user/FoodNetworkTV",
				"http://www.youtube.com/user/japanesepod101",
				"http://www.youtube.com/user/NationalGeographic",
				"http://www.youtube.com/user/CorridorDigital",
				"http://www.youtube.com/user/trendhuntertv"				
		};
		List<NnChannel> channels = new ArrayList<NnChannel>();
		int j=1;
		for (String url : urls) {
			String checkedUrl = YouTubeLib.formatCheck(url);
			NnChannel c = channelMngr.findBySourceUrl(checkedUrl);
			c.setSeq((short)j);
			channels.add(c);
			j++;
		}
		channelSet.setLang(lang);
		channelSetMngr.create(channelSet, channels);						
		
		//channelSet ownership
		ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
		ownershipMngr.create(new ContentOwnership(), mso, channelSet);
		
		//category and set
		String[] categoryArr = categoryIds.split(",");
//		CategoryChannelSetManager cscMngr = new CategoryChannelSetManager();
		CategoryManager cMngr = new CategoryManager();
		List<Category> categories = new ArrayList<Category>();
		for (int i=0; i<categoryArr.length; i++) {
			Category c = cMngr.findById(Long.parseLong(categoryArr[i]));
			categories.add(c);
		}
		for (Category c : categories) {
/*
			CategoryToNnSet csc = new CategoryToNnSet(c.getId(), channelSet.getId());			
			cscMngr.save(csc);	
*/
			cMngr.addSet(c, channelSet);
		}
		return NnNetUtil.textReturn("OK");
	}	
	
	
}
