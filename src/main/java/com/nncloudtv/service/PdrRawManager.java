package com.nncloudtv.service;

import java.util.Date;
import java.util.logging.Logger;

import com.nncloudtv.dao.PdrRawDao;
import com.nncloudtv.model.PdrRaw;

public class PdrRawManager {

	protected static final Logger log = Logger.getLogger(PdrRawManager.class.getName());
	
	private PdrRawDao pdrDao= new PdrRawDao();
			
	public void create(PdrRaw pdr) {
		Date now = new Date();
		pdr.setCreateDate(now);
		pdr.setUpdateDate(now);
		pdrDao.save(pdr);
	}
	
	public PdrRaw save(PdrRaw pdr) {
		pdr.setUpdateDate(new Date());
		return pdrDao.save(pdr);
	}		
	
	/**
	 * 	Keep the implementation since the requirement can be changed back again.
	 *  @@@ IMPORTANT It needs to be done in task if there's viewLog process  
	 */
	public void processPdr(String pdr, long userId, String sessionId) {
		if (pdr == null) {return;}		
		
		PdrRawManager pdrMngr = new PdrRawManager();
		log.info("pdr length: " + pdr.length());
		int times = Math.round(pdr.length() / 255) + 1;
		int start = 0;
		int end = 254;
		if (pdr.length() < end) { end = pdr.length();}
		for (int i=0; i<times; i++) {
			String detail = pdr.substring(start, end);
			PdrRaw raw = new PdrRaw(userId, sessionId, 0, null, detail);			
			pdrMngr.create(raw);
			start = start + 255;
			end = end  + 254;
			if (pdr.length() < end) { end = pdr.length();}
		}		
		
		/*
		//if the verb is watch, store the data in viewlog
		String[] lines = pdr.split("\n");
		ViewLogManager viewLogMngr = new ViewLogManager();
		try {			
			for (String line : lines) {						
				String[] data = line.split("\t");			
				String verb = data[1];			
				if (verb.equals(PdrRaw.VERB_WATCH)) {
					viewLogMngr.processPdr(line, userId);
				}
			}
		} catch (Exception e) {
			log.info("exception catpures: " + e.getClass());
		}
		*/
	}
		
}
