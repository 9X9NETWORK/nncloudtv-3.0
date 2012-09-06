package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnUserSubscribeDao;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserSubscribe;
import com.nncloudtv.model.NnUserSubscribeGroup;

@Service
public class NnUserSubscribeManager {        

    protected static final Logger log = Logger.getLogger(NnUserSubscribeManager.class.getName());
    
    NnUserSubscribeDao subDao = new NnUserSubscribeDao(); 

    public List<NnUserSubscribe> findAllByUser(NnUser user) {
        return subDao.findAllByUser(user);
    }
    
    //make sure your channel has seq and type set
    //@@@ counter work throw to queue
    public boolean subscribeChannel(NnUser user, NnChannel channel) {
        NnUserSubscribe s = new NnUserSubscribe(user.getId(), channel.getId(), channel.getSeq(), channel.getType());
        Date now = new Date();
        s.setCreateDate(now);
        s.setUpdateDate(now);
        subDao.save(user, s);
        return true;
    }

    public boolean subscribeChannel(NnUser user, long channelId, short seq, short type) {
        NnUserSubscribe existed = subDao.findByUserAndChannel(user, channelId);
        if (existed != null) {
            log.info("user trying to subscribe a channel that has been subscribed." + channelId);
            return false;
        }
        NnUserSubscribe s = new NnUserSubscribe(user.getId(), channelId, seq, type);
        Date now = new Date();
        s.setCreateDate(now);
        s.setUpdateDate(now);
        subDao.save(user, s);
        /*
        CntSubscribeManager cntMngr = new CntSubscribeManager();
        CntView cnt = cntMngr.findByChannel(channelId);
        if (cnt == null) {
            cnt = new CntView(channelId);
        } else {
            cnt.setCnt(cnt.getCnt()+1);
        }
        cntMngr.save(cnt);
        */        
        return true;
    }        
    
    public boolean subscribeSet(NnUser user, NnUserSubscribeGroup subSet, List<NnChannel> channels) {
        NnUserSubscribeGroupManager subSetMngr = new NnUserSubscribeGroupManager();
        subSetMngr.create(user, subSet);
        
        for (NnChannel c : channels) {
            NnUserSubscribe existed = subDao.findByUserAndChannel(user, c.getId());
            if (existed == null) {
                NnUserSubscribe sub = new NnUserSubscribe(user.getId(), c.getId(), c.getSeq(), c.getType());
                Date now = new Date();
                sub.setCreateDate(now);
                sub.setUpdateDate(now);
                subDao.save(user, sub);
            }
        }
        return true;
    }

    public NnUserSubscribe findChannelSubscription(NnUser user, long channelId, short seq) {
        NnUserSubscribe s = subDao.findChannelSubscription(user, channelId, seq);
        return s;
    }
    
    public NnUserSubscribe findByUserAndChannel(NnUser user, long channelId) {
        NnUserSubscribe s = subDao.findByUserAndChannel(user, channelId);
        return s;
    }
    
    public NnUserSubscribe findByUserAndSeq(NnUser user, short seq) {
        NnUserSubscribe s = subDao.findByUserAndSeq(user, seq);
        return s;
    }    
    
    public void unsubscribeChannel(NnUser user, NnUserSubscribe s) {
        if (s != null) { subDao.delete(user, s); }
    }
    
    public List<NnChannel> findSubscribedChannels(NnUser user) {
        List<NnUserSubscribe> subs = subDao.findAllByUser(user);
        List<NnChannel> channels = new ArrayList<NnChannel>();
        NnChannelManager channelMngr = new NnChannelManager();
        CntSubscribeManager cntMngr = new CntSubscribeManager();
        for (NnUserSubscribe s : subs) {
            NnChannel c = channelMngr.findById(s.getChannelId()); //!!!
            if (c != null) {
                c.setSeq(s.getSeq());
                c.setType(s.getType());
                /*
                CntView cnt = cntMngr.findByChannel(c.getId());            
                if (cnt != null) {
                    c.setCntSubscribe(cnt.getCnt());
                }
                */
                channels.add(c);
            }
        }
        return channels;             
    }

    //move from seq1 to seq2
    public boolean moveSeq(NnUser user, short seq1, short seq2) {                        
        NnUserSubscribe sub = subDao.findByUserAndSeq(user, seq1);
        if (sub == null) {return false;}
        sub.setSeq(seq2);
        subDao.save(user, sub);
        return true;
    }

    public boolean copyChannel(NnUser user, long channelId, short seq) {
        NnUserSubscribe occupied = this.findByUserAndSeq(user, seq);
        if (occupied != null)
            return false;
        NnUserSubscribe s = new NnUserSubscribe(user.getId(), channelId, seq, MsoIpg.TYPE_GENERAL);
        Date now = new Date();
        s.setCreateDate(now);
        s.setUpdateDate(now);
        subDao.save(s);        
        return true;        
    }
    
    
    public List<NnUserSubscribe> list(int page, int limit, String sidx, String sord) {
        return subDao.list(page, limit, sidx, sord);
    }
    
    public List<NnUserSubscribe> list(int page, int limit, String sidx, String sord, String filter) {
        return subDao.list(page, limit, sidx, sord, filter);
    }
    
    public int total() {
        return subDao.total();
    }
    
    public int total(String filter) {
        return subDao.total(filter);
    }
    
}
