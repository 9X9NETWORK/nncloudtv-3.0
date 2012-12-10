package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.DeepDao;
import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.dao.ShallowDao;
import com.nncloudtv.model.Deep;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.Shallow;

@Service
public class RecommendService {
    protected static final Logger log = Logger.getLogger(RecommendService.class.getName());
    
    private ShallowDao shallowDao = new ShallowDao();    
    private DeepDao deepDao = new DeepDao();
    private NnChannelDao chDao = new NnChannelDao();

    /*
     * for GUESTs and FRESHMAN USERs, SHALLOW RECOMMENDATION,
     * but for USERs, DEEP RECOMMENDATIONS
     */
    public List<NnChannel> findMayLike(String userToken, String channel, String lang) {      
        List<NnChannel> channels = new ArrayList<NnChannel>();
        long cid = 0;
        if (channel != null)
            cid = Long.parseLong(channel);
        if (userToken == null && channel == null) {
            log.info("maylike: nothing provided. get channel from billboard");
            channels = this.findBillboardPool(9, lang);
        } else if (NnUserManager.isGuestByToken(userToken)) {
            log.info("maylike: guest user find from shallow");
            channels = this.findShallowChannels(cid);
        } else {
            NnUser user = new NnUserManager().findByToken(userToken);
            if (user != null) {
                System.out.println("what the user u find:" + user.getId());
                channels = this.findDeepChannels(user);
                if (channels.size() == 0) {
                    log.info("maylike: fresh user find from shallow");
                    channels = this.findShallowChannels(cid);
                } else {
                    log.info("maylike: user find from deep");                    
                }                   
            } else {
                log.info("maylike: wrong user (treat as guest) find from shallow");
                channels = this.findShallowChannels(cid);
            }
        }
        if (channels.size() == 0) {
            log.info("maylike: final frontier: nothing hits, try billboard");
            channels = this.findBillboardPool(9, lang);
        }
       
        return channels;
    }

    /*
     * if GUEST or FRESHMAN USER, channels selected from the BILLBOARD POOL; 
     * if USER, channels selected from the FDM POOL; 
     */
    public List<NnChannel> findRecommend(String userToken, String lang) {
        List<NnChannel> channels = new ArrayList<NnChannel>();
        if (userToken == null || NnUserManager.isGuestByToken(userToken)) {
            log.info("recommend: guest user find from billboard");
            channels = this.findBillboardPool(9, lang);
        } else {
            NnUser user = new NnUserManager().findByToken(userToken);
            if (user != null) {
                Deep deep = deepDao.findByUser(user.getShard(), user.getId());
                if (deep != null) {
                    log.info("recommend: user get recommendation from fdm pool " + userToken);
                    channels = this.findFdm(user.getSphere(), 9);
                } else {
                    log.info("recommend: freshman user get recommendation from billboard pool " + userToken);
                    channels = this.findBillboardPool(9, lang);
                }
            }
        }
        if (channels.size() == 0) {//invalid user
            log.info("recommend: maybe invalid user, same as guest " + userToken);
            channels = this.findBillboardPool(9, lang);
        }
        return channels;        
    }
    
    //randomly pick from billboard, based on language
    public List<NnChannel> findBillboardPool(int limit, String lang) {
        List<NnChannel> channels = chDao.findSpecial(NnChannel.POOL_BILLBOARD, lang, limit);
        List<NnChannel> recommended = new ArrayList<NnChannel>();
        int i=0;
        for (NnChannel c : channels) {
            recommended.add(c);
            i++;
            if (i > 9)
                break;
        }
        return recommended;
    }

    //randomly pick from fdm    
    public List<NnChannel> findFdm(String lang, int limit) {
        List<NnChannel> channels = chDao.findSpecial(NnChannel.POOL_FDM, lang, limit);
        List<NnChannel> recommended = new ArrayList<NnChannel>();
        int i=0;
        for (NnChannel c : channels) {
            recommended.add(c);
            i++;
            if (i > 9)
                break;
        }
        return recommended;
    }
    
    public List<NnChannel> findShallowChannels(long chId) {
        List<NnChannel> channels = new ArrayList<NnChannel>();
        NnChannelManager chMngr = new NnChannelManager();
        Shallow shallow = shallowDao.findByChannelId(chId);
        if (shallow == null)
            return channels; //need to come up something
        
        String list = shallow.getRecommendIds();
        String[] chs = list.split(",");
        for (String ch : chs) {
            NnChannel c = chMngr.findById(Long.parseLong(ch));
            if (c != null)
                channels.add(c);                        
        }
        return channels;
    }
    
    public List<NnChannel> findDeepChannels(NnUser user) {
        List<NnChannel> channels = new ArrayList<NnChannel>();
        NnChannelManager chMngr = new NnChannelManager();
        Deep deep = deepDao.findByUser(user.getShard(), user.getId());
        if (deep == null)
            return channels; //need to come up something
        
        String list = deep.getRecommendIds();
        String[] chs = list.split(",");
        for (String ch : chs) {
            NnChannel c = chMngr.findById(Long.parseLong(ch));
            if (c != null)
                channels.add(c);                        
        }
        return channels;
        
    }

}
