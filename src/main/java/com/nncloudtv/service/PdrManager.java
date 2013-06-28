package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nncloudtv.dao.PdrDao;
import com.nncloudtv.model.NnDevice;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserWatched;
import com.nncloudtv.model.Pdr;
import com.nncloudtv.model.Poi;
import com.nncloudtv.model.PoiEvent;
import com.nncloudtv.model.PoiPdr;

public class PdrManager {

    protected static final Logger log = Logger.getLogger(PdrManager.class.getName());
    
    private PdrDao dao = new PdrDao();
            
    public void create(Pdr pdr) {
        Date now = new Date();
        pdr.setUpdateDate(now);
        dao.save(pdr);
    }
    
    public Pdr save(Pdr pdr) {
        pdr.setUpdateDate(new Date());
        return dao.save(pdr);
    }        

    public List<Pdr> findDebugging(
            NnUser user, NnDevice device, String session,
            String ip, Date since) {
        return dao.findDebugging(user, device, session, ip, since);
    }

    public PoiPdr findPoiPdr(NnUser user, long poiId) {
        return dao.findPoiPdr(user, poiId);
    }
    
    //for poll
    public void processPoiPdr(NnUser user, Poi poi, PoiEvent event, String select) {
        PoiPdr pdr = new PoiPdr(user.getId(), user.getMsoId(), poi.getId(), event.getId(), select);
        dao.savePoiPdr(pdr);
    }
    
    public void processPoiScheduler(NnUser user, Poi poi, PoiEvent event, String select) {
        PoiPdr pdr = new PoiPdr(user.getId(), user.getMsoId(), poi.getId(), event.getId(), select);
        String list = event.getNotifyScheduler();
        if (list != null) {
            String[] time = list.split(",");
        	log.info("process poi scheduler (quantity): " + time.length);        
            if (time.length > 0) {
                long epoch = Long.parseLong(time[0]);
                Date myDate = new Date (epoch*1000);
                pdr.setScheduledDate(myDate);
                dao.savePoiPdr(pdr);
            }
        }        
    }
    
    //for statistics
    public void processPoi(NnUser user, Poi poi, PoiEvent event, String select) {        
        String session = "poi";
        String detail = "poi id:" + poi.getId() + ";select:" + select;
        Pdr pdr = new Pdr(user, null, session, detail);
        this.save(pdr);
    }
    
    public void processPdr(NnUser user, NnDevice device, String sessionId, String pdr, String ip) {        
        if (pdr == null) {return;}        
        NnUserWatchedManager watchedMngr = new NnUserWatchedManager();
        String reg = "\\bw\t(\\d+)\t(\\S+)";        
        Pattern pattern = Pattern.compile(reg);
        Matcher m = pattern.matcher(pdr);
        if (user != null) {
            log.info("user is not null");
            while (m.find()) {            
                long channelId = Long.parseLong(m.group(1));
		log.info("found channelId = " + channelId);
                String program = m.group(2);
		log.info("found program = " + program);
                if (channelId != 0 && !program.equals("0")) {
                    NnUserWatched watched = new NnUserWatched(user, channelId, program);
                    log.info("user watched channel and program:" + user.getToken() + ";" + channelId + ";" + program);
                    watchedMngr.savePersonalHistory(user, watched);
                }
            }
        }
        if (pdr.length() > 65535) {
            pdr = pdr.substring(0, 65530);
        }
        Pdr raw = new Pdr(user, device, sessionId, pdr.trim());            
        raw.setIp(ip);
        this.create(raw);        
    }        
}
