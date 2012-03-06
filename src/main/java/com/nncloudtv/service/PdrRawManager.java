package com.nncloudtv.service;

import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nncloudtv.dao.PdrRawDao;
import com.nncloudtv.model.NnDevice;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.PdrRaw;
import com.nncloudtv.model.NnUserWatched;
import com.nncloudtv.service.NnUserWatchedManager;
import com.nncloudtv.service.PdrRawManager;

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
	
	public void processPdr(NnUser user, NnDevice device, String sessionId, String pdr, String ip) {		
		if (pdr == null) {return;}		
		PdrRawManager pdrMngr = new PdrRawManager();		
		NnUserWatchedManager watchedMngr = new NnUserWatchedManager();
		String reg = "w\t(\\d++)\t(\\w++)";		
		Pattern pattern = Pattern.compile(reg);
		Matcher m = pattern.matcher(pdr);
		if (user != null) {
			while (m.find()) {			
				long channelId = Long.parseLong(m.group(1));
				String program = m.group(2);
				if (channelId != 0 && !program.equals("0")) {
					NnUserWatched watched = new NnUserWatched(user, channelId, program);
					log.info("user watched channel and program:" + user.getToken() + ";" + channelId + ";" + program);
					watchedMngr.save(user, watched);
				}
			}
		}
		//!!!
		/*
		if (pdr.length() > 5000) {
			pdr = pdr.substring(0, 4999);
		}
		*/
		PdrRaw raw = new PdrRaw(user, device, sessionId, pdr.trim());			
		raw.setIp(ip);
		pdrMngr.create(raw);		
	}
		
}
