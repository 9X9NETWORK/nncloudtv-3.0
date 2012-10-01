package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nncloudtv.dao.PdrDao;
import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.model.NnDevice;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserWatched;
import com.nncloudtv.model.Pdr;

public class PdrManager {

    protected static final Logger log = Logger.getLogger(PdrManager.class.getName());
    
    private PdrDao pdrDao= new PdrDao();
            
    public void create(Pdr pdr) {
        Date now = new Date();
        pdr.setCreateDate(now);
        pdr.setUpdateDate(now);
        pdrDao.save(pdr);
    }
    
    public Pdr save(Pdr pdr) {
        pdr.setUpdateDate(new Date());
        return pdrDao.save(pdr);
    }        

    public List<Pdr> findDebugging(
            NnUser user, NnDevice device, String session,
            String ip, Date since) {
        return pdrDao.findDebugging(user, device, session, ip, since);
    }

    //TODO should verify user/device token, but hell later  
    public void processWatched(String userToken, String deviceToken, String pdr) {
        CounterFactory factory = new CounterFactory();        
        String reg = "\\sw\t(\\d++)\t(\\w++)";     
        Pattern pattern = Pattern.compile(reg);
        Matcher m = pattern.matcher(pdr);
        while (m.find()) {          
            long channelId = Long.parseLong(m.group(1));
            if (channelId != 0) {
                String counterName = "ch" + channelId;
                log.info("store cnt channel view - counterName:" + counterName);
                ShardedCounter counter = factory.getOrCreateCounter(counterName);
                counter.increment();
            }
        }
    }
    
    public void processPdr(NnUser user, NnDevice device, String sessionId, String pdr, String ip) {        
        if (pdr == null) {return;}        
        PdrManager pdrMngr = new PdrManager();        
        NnUserWatchedManager watchedMngr = new NnUserWatchedManager();
        String reg = "\\sw\t(\\d++)\t(\\w++)";        
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
        Pdr raw = new Pdr(user, device, sessionId, pdr.trim());            
        raw.setIp(ip);
        pdrMngr.create(raw);        
    }        
}
