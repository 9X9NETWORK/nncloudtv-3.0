package com.nncloudtv.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

import com.nncloudtv.lib.PiwikLib;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.ContentOwnership;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnSetToNnChannel;
import com.nncloudtv.model.NnUser;

/**
 * for testing, works only for small set of data
 */	
@Service
public class InitService {
	protected static final Logger log = Logger.getLogger(InitService.class.getName());		

	private HttpServletRequest req;

	private Mso mso;
	private NnUser user;
	private static String NNEMAIL = "mso@9x9.tv";

	public void setRequest(HttpServletRequest req) {
		this.req = req;
	}
	
	/** 
	 * @param trans whether to turn on transcoding service for channel creation
	 */
	public void initAll(boolean english, boolean devel) {
		//initFiles();
		
		initMsos();
		initCategories(english);
		initSets(english, devel);
		initChannels(english, devel);
		initSetAndChannels(english);
		initCategoryAndSets(english);
		initRecommended(english);
		initCategoryCount();						
		initMapelPrograms();
	}
	
	 //for local testing only
	public void initMapelPrograms() { 
		NnChannelManager channelMngr = new NnChannelManager();		
		NnChannel soap = new NnChannel("mapel soap", "mapel soap", "");
		soap.setContentType(NnChannel.CONTENTTYPE_MAPLE_SOAP);
		channelMngr.save(soap);
		NnChannel variety = new NnChannel("mapel variety", "mapel variety", "");
		variety.setContentType(NnChannel.CONTENTTYPE_MAPLE_VARIETY);
		channelMngr.save(variety);
		
		NnProgramManager programMngr = new NnProgramManager();
		NnProgram s1 = new NnProgram("s1", "s1", "", NnProgram.TYPE_VIDEO);
		s1.setFileUrl("http://lalala.com");
		s1.setPublic(true);
		s1.setSeq("1");
		programMngr.create(soap, s1);
		NnProgram s3 = new NnProgram("s3", "s3", "", NnProgram.TYPE_VIDEO);
		s3.setFileUrl("http://lalala.com");
		s3.setPublic(true);
		s3.setSeq("3");
		programMngr.create(soap, s3);
		NnProgram s2 = new NnProgram("s2", "s2", "", NnProgram.TYPE_VIDEO);
		s2.setFileUrl("http://lalala.com");
		s2.setPublic(true);
		s2.setSeq("2");
		programMngr.create(soap, s2);
		NnProgram s4 = new NnProgram("s4", "s4", "", NnProgram.TYPE_VIDEO);
		s4.setFileUrl("http://lalala.com");
		s4.setPublic(true);
		s4.setSeq("4");
		programMngr.create(soap, s4);		
		NnProgram v11 = new NnProgram("v11", "v11", "", NnProgram.TYPE_VIDEO);
		v11.setFileUrl("http://lalala.com");
		v11.setPublic(true);
		v11.setSeq("1");
		v11.setSubSeq("1");
		programMngr.create(variety, v11);
		NnProgram v13 = new NnProgram("v13", "v13", "", NnProgram.TYPE_VIDEO);
		v13.setFileUrl("http://lalala.com");
		v13.setPublic(true);
		v13.setSeq("1");
		v13.setSubSeq("3");
		programMngr.create(variety, v13);
		NnProgram v2 = new NnProgram("v2", "v2", "", NnProgram.TYPE_VIDEO);
		v2.setFileUrl("http://lalala.com");
		v2.setPublic(true);
		v2.setSeq("2");
		programMngr.create(variety, v2);
		NnProgram v3 = new NnProgram("v3", "v3", "", NnProgram.TYPE_VIDEO);
		v3.setFileUrl("http://lalala.com");
		v3.setPublic(true);
		v3.setSeq("3");
		programMngr.create(variety, v3);		
		NnProgram v12 = new NnProgram("v12", "v12", "", NnProgram.TYPE_VIDEO);
		v12.setFileUrl("http://lalala.com");
		v12.setPublic(true);
		v12.setSeq("1");
		v12.setSubSeq("2");
		programMngr.create(variety, v12);
	}
	
	public void initFiles() {		
	    try {
	    	InputStream input = new FileInputStream(getClass().getResource("/ESets.xlsx").getFile());
			Workbook wb;
			wb = WorkbookFactory.create(input);
			Sheet category = wb.getSheetAt(0);
			int rows = category.getPhysicalNumberOfRows();
			for (int i=1; i<rows; i++) {
			    Row row = category.getRow(i);
			    Cell cell = row.getCell(0);
			    System.out.println(cell.getStringCellValue());
			}	    	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void initCategoryCount() {
		NnSetManager setMngr = new NnSetManager();
		List<NnSet> sets = setMngr.findAll();
		HashMap<Long, Integer> map = new HashMap<Long, Integer>();
		for (NnSet set : sets) {
			List<NnSetToNnChannel> list = setMngr.findNnSetToNnChannelsBySet(set.getId());
			set.setChannelCnt(list.size());
			System.out.println("cs name:" + set.getName() + ";size:" + list.size());
			setMngr.save(set);
			map.put(set.getId(), list.size());
		}						
		CategoryManager catMngr = new CategoryManager();
		List<Category> categories= catMngr.findAll();
		for (Category c : categories) {
			List<NnSet> list = catMngr.findSetsByCategory(c.getId(), false);
			c.setChannelCnt(list.size());
			catMngr.save(c);			
		}
	}
	

	public String[] getChannelUrlsFromExcel(boolean english) {
		InputStream input;
		Workbook wb;
		int rows = 0;
		Set<String> list = new TreeSet<String>(); 
		try {
			if (english)
			    input = new FileInputStream(getClass().getResource("/ESets.xlsx").getFile());
			else
				input = new FileInputStream(getClass().getResource("/CSets.xlsx").getFile());
			wb = WorkbookFactory.create(input);
			Sheet sets = wb.getSheetAt(3);			
			rows = sets.getPhysicalNumberOfRows();						
		    int maple = 0;
			for (int r=1; r<rows; r++) {
			    Row row = sets.getRow(r);
			    if (row != null) {
				    int col = row.getLastCellNum();
				    for (int c=1; c<col; c+=2) {
				    	Cell cell = row.getCell(c);
				    	if (cell != null) {
				    		String url = cell.getStringCellValue();
				    		if (url != null && url.length() > 0) {
					    		if (!url.contains("maplestage")) {  
						    		String checkedUrl = YouTubeLib.formatCheck(url);
						    		if (checkedUrl != null) {
						    			Cell nameCell = row.getCell(c-1);
						    			String name = "";
						    			if (nameCell != null) {
						    				name = nameCell.getStringCellValue();
						    			}
						    			list.add(checkedUrl + "," + name);
						    		} else {
						    			log.info("url not passed youtube lib check:" + url);
						    		}
					    		} else {
					    			Cell nameCell = row.getCell(c-1);
					    			String name = "";
					    			if (nameCell != null) {
					    				name = nameCell.getStringCellValue();
					    			}					    			
					    			list.add(url + "," + name);
					    			maple++;
					    		}
				    		}
				    	}
				    }
			    }
			}
			log.info("final channel size:" + list.size());
    		System.out.println("mapel channels:" + maple);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return list.toArray(new String[0]);
		//return ;		
	}
	
	public void initSetAndChannels(boolean english) {
		NnChannelManager channelMngr = new NnChannelManager();
		NnSetManager setMngr = new NnSetManager();
		NnSetToNnChannelManager scMngr = new NnSetToNnChannelManager();		
		InputStream input;
		Workbook wb;
		List<NnSet> setList = new ArrayList<NnSet>();
		try {
			if (english)
			    input = new FileInputStream(getClass().getResource("/ESets.xlsx").getFile());
			else
				input = new FileInputStream(getClass().getResource("/CSets.xlsx").getFile());
			wb = WorkbookFactory.create(input);
			Sheet sheet = wb.getSheetAt(3);			
			Row row = sheet.getRow(0);						
			//get sets from excel
			System.out.println("last cell num:" + row.getLastCellNum());
			for (int i=1; i<row.getLastCellNum(); i+=2) {
				Cell cell = row.getCell(i);
				if (cell != null) {
					String name = cell.getStringCellValue();
					if (name != null && name.length() > 0) {
						name = name.trim();
						NnSet cs = setMngr.findByName(name);
						if (cs != null) {
							setList.add(cs);
					    } else {
					    	log.severe("this set is not in db:" + name);
					    }
					}
				}
			}
			//compare list with database
			String lang = "en";
			if (!english)
				lang = "zh";
			List<NnSet> setAll = setMngr.findByLang(lang);
			for (NnSet all : setAll) {
				boolean found = false;
				for (NnSet sl : setList) {
					if (sl.getName().equals(all.getName())) {
						found = true;
						break;
					}						
				}
				if (!found) {
					System.out.println("not found this:" + all.getName());
				}
			}
			if (setAll.size() != setList.size()) {
				log.severe("information inconsistent. set all has " + setAll.size() + ";setList here = " + setList.size());
				//return;
			}
			//put all the channels into a string table for set, channel lookup
			int rows = sheet.getPhysicalNumberOfRows();			
		    ArrayList<List<String>> NnSetList = new ArrayList<List<String>>();
			for (int r=1; r<rows; r++) {
			    row = sheet.getRow(r);
			    if (row != null) {
			    	int col = row.getLastCellNum(); 
			    	int seq = 0;
				    for (int c=1; c<col; c+=2) {
				    	Cell cell = row.getCell(c);
				    	if (cell != null) {
				    		String url = cell.getStringCellValue();
				    		String checkedUrl = url;
				    		if (!url.contains("maplestage"))
				    			checkedUrl = YouTubeLib.formatCheck(url);
			    			List<String> list = new ArrayList<String>();		    			
			    			if (checkedUrl != null) {
					    		if (seq < NnSetList.size()) { 
					    			list = NnSetList.get(seq);
					    		} else {
					    			NnSetList.add(list);
					    		}			    						    			
			    				list.add(checkedUrl);
			    			}
				    	}
				    	seq++;
				    }
			    }
			}
			if (NnSetList.size() != setList.size()) {
				log.severe("set found from db and channel data are not consistent:" + setList.size() + ";" + NnSetList.size());
				//return;
			}

			//real work
			Hashtable<String, NnChannel> table = new Hashtable<String, NnChannel>();
 			for (int i=0; i<setList.size(); i++) {
 				System.out.println(setList.get(i).getName() + NnSetList.get(i));
 				short j=1;
 				for (String url : NnSetList.get(i)) {
					NnChannel c = table.get(url);
 					if (c == null)
 	 					c = channelMngr.findBySourceUrl(url);
 					if (c == null) {
 						log.severe("channel unfound:" + url);
 						//return;
 					} else { 
 						table.put(url, c);
 						NnSet cs = setList.get(i);
 						NnSetToNnChannel sc = scMngr.findBySetAndChannel(cs.getId(), c.getId());
 						//avoid the duplication
 						if (sc == null) {
 							sc = new NnSetToNnChannel(cs.getId(), c.getId(), j);				
 							scMngr.create(sc);
 							j++;
 						}
 					} 						
 				}
 			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initChannels(boolean english, boolean devel) {
		NnUserManager userMngr = new NnUserManager();
		user = userMngr.findByEmail(NNEMAIL, req);
		String[] entries = this.getChannelUrlsFromExcel(english);
		MsoManager msoMngr = new MsoManager();
		Mso mso = msoMngr.findNNMso();
		NnChannelManager channelMngr = new NnChannelManager();		
		DepotService tranService = new DepotService();
		boolean piwik = true;
		int zeroProgramCnt = 0;
		for (String entry : entries) {	
			String[] data = entry.split(",");
			String url = data[0];
			String name = null;
			if (data.length == 2) {
				name = data[1];
			}
			NnChannel c = channelMngr.findBySourceUrl(url);
			if (c == null) {
				c = new NnChannel(url);
				c.setName(name);
				c.setPublic(true);
				c.setStatus(NnChannel.STATUS_SUCCESS);	
				c.setContentType(channelMngr.getContentTypeByUrl(url));
				channelMngr.save(c);
				ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
				ownershipMngr.create(new ContentOwnership(), mso, c);
				if (!devel) {
					tranService.submitToTranscodingService(c.getId(), c.getSourceUrl(), req);
					channelMngr.save(c);
				} else {
					piwik = false; //local testing, no piwik creation
				}
			} else {
				if (c.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL || 
					c.getContentType() == NnChannel.CONTENTTYPE_YOUTUBE_PLAYLIST) {
					/*
					if (c.getOriName() == null) {						
						log.info("re-submit youtube channel:" + c.getSourceUrl());
						if (!devel)
							tranService.submitToTranscodingService(c.getId(), c.getSourceUrl(), req);												
					}
					*/
				}				
				if (c.getStatus() == NnChannel.STATUS_WAIT_FOR_APPROVAL) {
					log.info("mark the channel from waiting to approval to success");
					c.setStatus(NnChannel.STATUS_SUCCESS);
				} else if (c.getStatus() == NnChannel.STATUS_PROCESSING){
					if (!devel)
						tranService.submitToTranscodingService(c.getId(), c.getSourceUrl(), req);
					//log.info("was in processing mode, going to submit again");
				} else if (c.getContentType() == NnChannel.CONTENTTYPE_MAPLE_SOAP && c.getProgramCnt() < 5) {
					zeroProgramCnt++;
					//log.info("maple soap program count < 5; re-send:" + c.getSourceUrl());
					if (!devel)
						tranService.submitToTranscodingService(c.getId(), c.getSourceUrl(), req);
				} else if (c.getContentType() == NnChannel.CONTENTTYPE_MAPLE_VARIETY && c.getProgramCnt() < 5) {
					zeroProgramCnt++;
					//log.info("maple variety program count < 5; re-send:" + c.getSourceUrl());
					if (!devel)
						tranService.submitToTranscodingService(c.getId(), c.getSourceUrl(), req);
				} else if (c.getStatus() != NnChannel.STATUS_SUCCESS){
					//log.info("wanted channel but not success");					
				}
				if (c.getPiwik() != null)
					piwik = false;
				if (c.getPiwik() == null && devel)
					piwik = false;
			}
			if (piwik) {
				String piwikId = PiwikLib.createPiwikSite(0, c.getId());
				c.setPiwik(piwikId);
			}
			c.setName(name);
			//channelMngr.save(c);				
		}
		log.info("< 5 program count:" + zeroProgramCnt); 
	}
	
	public void initCategories(boolean english) {
		int rows = 0;
		ArrayList<String> list = new ArrayList<String>();
		try {
			InputStream input;
			if (english) {
				//input = new FileInputStream(getClass().getResource("/ESets.xlsx").getFile());
				input = new FileInputStream(getClass().getResource("/ESets.xlsx").getFile());
			} else { 
				input = new FileInputStream(getClass().getResource("/CSets.xlsx").getFile());
			}
			Workbook wb = WorkbookFactory.create(input);
			Sheet category = wb.getSheetAt(0);
			rows = category.getPhysicalNumberOfRows();
			for (int i=1; i<rows; i++) {
			    Row row = category.getRow(i);
			    Cell cell = row.getCell(0);
			    list.add(cell.getStringCellValue());
			}
			if (english)
				list.add(Category.UNCATEGORIZED);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		CategoryManager categoryMngr = new CategoryManager();
		int i=1;
		for (String l : list) {
			Category c = new Category(l, true);
			if (english)
				c.setLang(LangTable.LANG_EN);
			else
				c.setLang(LangTable.LANG_ZH);			
			if (c.getName().equals(Category.UNCATEGORIZED))
				c.setPublic(false);
			c.setSeq((short)i);
			c.setSubCatCnt(0);
			categoryMngr.save(c);
			i++;
		}
		log.info("category size:" + list.size());
	}		
	
	public List<String> getSetNamesFromExcel(boolean english) {		
		ArrayList<String> list = new ArrayList<String>();
		try {
			InputStream input;
			if (english)
				input = new FileInputStream(getClass().getResource("/ESets.xlsx").getFile());
			else 
				input = new FileInputStream(getClass().getResource("/CSets.xlsx").getFile());
			Workbook wb = WorkbookFactory.create(input);
			Sheet sets = wb.getSheetAt(1);
			int rows = sets.getPhysicalNumberOfRows();
			for (int i=0; i<rows; i++) {
			    Row row = sets.getRow(i);			    
			    Cell cellName = row.getCell(0);
			    Cell cellDesc = row.getCell(1);
			    if (cellName != null) {
				    if (cellName.getStringCellValue() != null && cellName.getStringCellValue().length() > 0)
				    	list.add(cellName.getStringCellValue().trim() + ";" + cellDesc.getStringCellValue().trim());
			    }
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void initSets(boolean english, boolean devel) {
		MsoManager msoMngr = new MsoManager();
		Mso mso = msoMngr.findNNMso(); 
		List<String> list = this.getSetNamesFromExcel(english);
		ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
		NnSetManager setMngr = new NnSetManager();
		for (int i=0; i<list.size(); i++) {
			String[] value = list.get(i).split(";");
			String name = value[0];
			String intro = value[1];
			NnSet set = new NnSet(name, intro, true);
			set.setBeautifulUrl(String.valueOf(i));
			if (english)
				set.setLang(LangTable.LANG_EN);
			else 
				set.setLang(LangTable.LANG_ZH);
			setMngr.save(set);
			if (!devel) {
				String piwik = PiwikLib.createPiwikSite(set.getId(), 0);
				log.info("piwik id:" + piwik);
				set.setPiwik(piwik);
				setMngr.save(set);
			}
			ownershipMngr.create(new ContentOwnership(), mso, set);
		}
		log.info("set size:" + list.size());
	}
	
	public void initMsos() {
		MsoManager msoMngr = new MsoManager();
		mso = new Mso("9x9", "9x9", NNEMAIL, Mso.TYPE_NN);
		mso.setTitle("9x9.tv");
		mso.setLang(LangTable.LANG_EN);
		mso.setJingleUrl("http://s3.amazonaws.com/9x9ui/videos/opening.swf");
		mso.setLogoUrl("http://s3.amazonaws.com/9x9ui/images/logo_9x9.png");
		msoMngr.save(mso);
				
		//config
		MsoConfigManager configMngr = new MsoConfigManager();
		MsoConfig configCdn = new MsoConfig(mso.getId(), MsoConfig.CDN, MsoConfig.CDN_AKAMAI);
		configMngr.create(configCdn);
		MsoConfig configDebug = new MsoConfig(mso.getId(), MsoConfig.DEBUG, "1");		
		configMngr.create(configDebug);
		MsoConfig configFb = new MsoConfig(mso.getId(), MsoConfig.FBTOKEN, "");
		configMngr.create(configFb);
		MsoConfig configRo = new MsoConfig(mso.getId(), MsoConfig.RO, "0");
		configMngr.create(configRo);
		
		//a default MSO user
		NnUserManager userMngr = new NnUserManager();
		user = new NnUser(NNEMAIL, "9x9mso", "9x9 mso", NnUser.TYPE_NN);  
		userMngr.create(user, req, NnUser.SHARD_DEFAULT);
		//a user for testing
		NnUser a = new NnUser("a@a.com", "foobie", "a", NnUser.TYPE_NN);
		userMngr.create(a, req, NnUser.SHARD_DEFAULT);

		log.info("initializeMso1AndCategories is done");				
	}

	public void auser(HttpServletRequest req) {
		NnUserManager userMngr = new NnUserManager();
		user = new NnUser(NNEMAIL, "9x9mso", "9x9 mso", NnUser.TYPE_NN);  
		userMngr.create(user, req, NnUser.SHARD_DEFAULT);
	}
	
	public void initCategoryAndSets(boolean english) {
		CategoryManager cMngr = new CategoryManager();
		NnSetManager csMngr = new NnSetManager();
		List<Category> categories = new ArrayList<Category>();
		try {
			InputStream input;
			if (english)
				input = new FileInputStream(getClass().getResource("/ESets.xlsx").getFile());
			else 
				input = new FileInputStream(getClass().getResource("/CSets.xlsx").getFile());
			Workbook wb = WorkbookFactory.create(input);
			Sheet sheet = wb.getSheetAt(2);			
			Row row = sheet.getRow(0);
			//get categories
			for (int i=0; i<row.getLastCellNum(); i++) {
				Cell cell = row.getCell(i);
				String name = cell.getStringCellValue();
				Category c = cMngr.findByName(name);
				if (c == null) {
					log.severe("category not found:" + name);
					return;
				}
				categories.add(c);
				System.out.println(c.getName());
			}
		    ArrayList<List<NnSet>> NnSets = new ArrayList<List<NnSet>>();
		    int rows = sheet.getPhysicalNumberOfRows();
			for (int r=1; r<rows; r++) {
			    row = sheet.getRow(r);
		    	int col = row.getLastCellNum();
		    	int seq = 0;
			    for (int c=0; c<col; c++) {
			    	Cell cell = row.getCell(c);
			    	if (cell != null) {
			    		String setName = cell.getStringCellValue();
			    		if (setName != null && setName.length() > 0) {
				    		NnSet set = csMngr.findByName(setName);
			    			if (set == null) {
								log.severe("channel set not found:" + setName + ";r=" + r + ";c=" + c);
								return;		    				
			    			}
			    			List<NnSet> list = new ArrayList<NnSet>();		    			
				    		if (seq < NnSets.size()) { 
				    			list = NnSets.get(seq);
				    		} else {
				    			NnSets.add(list);
				    		}			    						    			
		    				list.add(set);
			    		}
			    	}
			    	seq++;
			    }
			}
			System.out.println("category size:" + categories.size());
			System.out.println("channel set size:" + NnSets.size());
			for (int i=0; i<categories.size(); i++) {
				for (int j=0; j<NnSets.get(i).size(); j++) {
					/*
					CategoryToNnSet cs = new CategoryToNnSet(
							categories.get(i).getId(), 
							NnSets.get(i).get(j).getId());
							*/  
					cMngr.addSets(categories.get(i), NnSets.get(i));
				}
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initRecommended(boolean english) {		
		NnSetManager csMngr = new NnSetManager();
		try {
			InputStream input; 
			if (english)
				input = new FileInputStream(getClass().getResource("/ESets.xlsx").getFile());
			else 
				input = new FileInputStream(getClass().getResource("/CSets.xlsx").getFile());
			Workbook wb = WorkbookFactory.create(input);
			Sheet sheet = wb.getSheetAt(4);
		    int rows = sheet.getPhysicalNumberOfRows();
			for (int r=0; r<rows; r++) {
				Row row = sheet.getRow(r); 
				Cell cell = row.getCell(0);
				String name = cell.getStringCellValue();
				if (name != null && name.length() > 0) {
					NnSet cs = csMngr.findByName(name); 
					if (cs == null) {
						log.severe("set not found:" + name);
						return;
					}
					cs.setFeatured(true);
					cs.setSeq((short)(r+1));
					csMngr.save(cs);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}
