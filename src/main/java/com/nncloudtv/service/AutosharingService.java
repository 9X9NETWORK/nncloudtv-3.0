package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelAutosharingDao;
import com.nncloudtv.model.NnChannelAutosharing;

@Service
public class AutosharingService {
    
    protected static final Logger log = Logger.getLogger(AutosharingService.class.getName());
    
    private NnChannelAutosharingDao channelAutosharingDao = new NnChannelAutosharingDao();    
    ///////// channel autosharing /////////
    
    public List<NnChannelAutosharing> findByChannel(long channelId) {
        return channelAutosharingDao.findByChannel(channelId);
    }
    
    public List<NnChannelAutosharing> findByChannelAndMso(long channelId, long msoId) {
        return channelAutosharingDao.findByChannelAndMso(channelId, msoId);
    }
    
    public boolean isChannelAutosharedByMso(long msoId, long channelId, short type) {
        return channelAutosharingDao.isChannelAutosharedByMso(msoId, channelId, type);
    }
    
    public NnChannelAutosharing findChannelAutosharing(long msoId, long channelId, short type) {
        return channelAutosharingDao.findChannelAutosharing(msoId, channelId, type);
    }
    
    public void save(NnChannelAutosharing autosharing) {
        channelAutosharingDao.save(autosharing);
    }
    
    public void create(NnChannelAutosharing autosharing) {
        autosharing.setCreateDate(new Date());
        channelAutosharingDao.save(autosharing);
    }
    
    public void delete(NnChannelAutosharing autosharing) {
        channelAutosharingDao.delete(autosharing);
    }
        
    public List<NnChannelAutosharing> findByChannelAndType(long channelId, short type) {
        return channelAutosharingDao.findByChannelAndType(channelId, type);
        
    }

    public List<NnChannelAutosharing> findChannelsByMso(long msoId) {
        return channelAutosharingDao.findChannelsByMso(msoId);
    }
    
    public List<NnChannelAutosharing> findChannelsByMsoAndType(long msoId, short type) {
        return channelAutosharingDao.findChannelsByMsoAndType(msoId, type);
    }
}
