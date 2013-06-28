package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.nncloudtv.dao.NnUserWatchedDao;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserWatched;

public class NnUserWatchedManager {

    protected static final Logger log = Logger.getLogger(NnUserWatchedManager.class.getName());
    
    private NnUserWatchedDao dao= new NnUserWatchedDao();
                    
    public NnUserWatched save(NnUser user, NnUserWatched watched) {
        Date now = new Date();
        NnUserWatched existed = this.findByUserAndChannel(user, watched.getChannelId());
        if (existed != null) {
            existed.setProgram(watched.getProgram());
            watched = existed;
        }
        watched.setUpdateDate(now);        
        return dao.save(user, watched);
    }
        
    public void savePersonalHistory(NnUser user, NnUserWatched watched) {
        NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
        //discard if it's already in the guide
        if (subMngr.findByUserAndChannel(user, watched.getChannelId()) == null) {
            List<NnUserWatched> watches = dao.findHistory(user);
            log.info("history size:" + watches.size());
            if (watches.size() <= 26) {
                log.info("add more entries: (userid)" + user.getId() + "(channelId)" + watched.getChannelId());
                this.save(user, watched);
            } else {
                //if already have 27 entries then update the oldest entry
                NnUserWatched old = watches.get(watches.size()-1); 
                log.info("update the oldest:" + old.getChannelId() + ";" + old.getProgram());
                old.setChannelId(watched.getChannelId());
                old.setProgram(watched.getProgram());
                old.setUpdateDate(new Date());
                dao.save(user, old);
            }
        } else {
            log.info("already subscribed");
	}
    }
    
    public NnUserWatched findByUserAndChannel(NnUser user, long channelId) {
        return dao.findByUserAndChannel(user, channelId);
    }

    public void delete(NnUser user, NnUserWatched watched) {
        dao.delete(user, watched);
    }
        
}
