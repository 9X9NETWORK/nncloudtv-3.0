package com.nncloudtv.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnUserDao;
import com.nncloudtv.lib.AuthLib;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnGuest;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.web.api.NnStatusCode;

@Service
public class NnUserManager {
    
    protected static final Logger log = Logger.getLogger(NnUserManager.class.getName());
        
    private NnUserDao dao = new NnUserDao();
    
    //@@@IMPORTANT email duplication is your responsibility
    public int create(NnUser user, HttpServletRequest req, short shard) {
        if (this.findByEmail(user.getEmail(), req) != null) //!!!!! shard or req flexible
            return NnStatusCode.USER_EMAIL_TAKEN;
        user.setName(user.getName().replaceAll("\\s", " "));
        user.setEmail(user.getEmail().toLowerCase());
        if (shard == 0)
            shard= NnUserManager.getShardByLocale(req);
        if (user.getToken() == null)
            user.setToken(NnUserManager.generateToken(shard));
        user.setShard(shard);
        Date now = new Date();
        user.setCreateDate(now);
        user.setUpdateDate(now);
        dao.save(user);
        return NnStatusCode.SUCCESS;
    }

    //Default is 1; Asia (tw, cn, hk) is 2
    public static short getShardByLocale(HttpServletRequest req) {
        String locale = NnUserManager.findLocaleByHttpRequest(req);
        short shard = NnUser.SHARD_DEFAULT;
        if (locale.equals("tw") || locale.equals("cn") || locale.equals("hk")) {
            shard = NnUser.SHARD_CHINESE;
        }          
        return shard;
    }
    
    public static String findLocaleByHttpRequest(HttpServletRequest req) {
        String ip = req.getRemoteAddr();
        log.info("findLocaleByHttpRequest() ip is " + ip);
        ip = NnNetUtil.getIp(req);
        log.info("try to find ip behind proxy " + ip);
        String country = "";
        try {
            URL url = new URL("http://brussels.teltel.com/geoip/?ip=" + ip);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setDoOutput(true);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.info("findLocaleByHttpRequest() IP service returns error:" + connection.getResponseCode());                
            }
            BufferedReader rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = rd.readLine(); 
            if (line != null) {
                log.info("country from locale service:" + line);
                country = line.toLowerCase();
            } //assuming one line
            rd.close();            
        } catch (java.net.SocketTimeoutException e) {
           log.info("socket timeout");   
        } catch (Exception e) {
            log.info("exception");
            NnLogUtil.logException(e);
        } finally {            
        }
        log.info("country from query:" + country + ";with ip:" + ip);
        String locale = "en";
        if (country.equals("tw")) {
            locale = "zh";
        }
        return locale;
    }
    
    public static short shardIterate(short shard) {
        if (shard == NnUser.SHARD_DEFAULT)
            return NnUser.SHARD_CHINESE;
        return NnUser.SHARD_DEFAULT;
    }
    
    public NnUser createGuest(Mso mso, HttpServletRequest req) {
        String password = String.valueOf(("token" + Math.random() + new Date().getTime()).hashCode());
        NnUser guest = new NnUser(NnUser.GUEST_EMAIL, password, NnUser.GUEST_NAME, NnUser.TYPE_USER);
        this.create(guest, req, (short)0);
        return guest;
    }
    
    public NnUser save(NnUser user) {
        if (user.getPassword() != null) {
            user.setSalt(AuthLib.generateSalt());
            user.setCryptedPassword(AuthLib.encryptPassword(user.getPassword(), user.getSalt()));
        }
        user.setEmail(user.getEmail().toLowerCase());
        user.setUpdateDate(new Date());
        return dao.save(user);
    }

    /**
     * GAE can only write 5 records a sec, maybe safe enough to do so w/out DB retrieving.
     * taking the chance to speed up signin (meaning not to consult DB before creating the account).
     */
    public static String generateToken(short shard) {
        if (shard == 0)
            return null;
        String time = String.valueOf(new Date().getTime());
        String random = RandomStringUtils.randomAlphabetic(10);
        String result = time + random;
        result = RandomStringUtils.random(20, 0, 20, true, true, result.toCharArray());
        result = shard + "-" + result;
        return result;
    }    
    
    //!!! able to assign shard
    public NnUser findByEmail(String email, HttpServletRequest req) {
        short shard= NnUserManager.getShardByLocale(req);
        return dao.findByEmail(email.toLowerCase(), shard);
    }
    
    public NnUser findAuthenticatedUser(String email, String password, HttpServletRequest req) {
        short shard= NnUserManager.getShardByLocale(req); 
        return dao.findAuthenticatedUser(email.toLowerCase(), password, shard);
    }
    
    public NnUser findAuthenticatedMsoUser(String email, String password, long msoId) {
        return dao.findAuthenticatedMsoUser(email.toLowerCase(), password, msoId);
    }
    
    public NnUser findMsoUser(Mso mso) {        
        if (mso.getType() == Mso.TYPE_NN) {
            return this.findNNUser();
        } else if (mso.getType() == Mso.TYPE_MSO) {
            List<NnUser> users = dao.findByType(NnUser.TYPE_TBC);
            for (NnUser user : users) {
                if (user.getMsoId() == mso.getId()) {
                    log.info("found TYPE_MSO");
                    return user;
                }
            }
        } else if (mso.getType() == Mso.TYPE_3X3) {
            List<NnUser> users = dao.findByType(NnUser.TYPE_3X3);
            for (NnUser user : users) {
                if (user.getMsoId() == mso.getId()) {
                    log.info("found TYPE_3X3");
                    return user;
                }
            }
        } else if (mso.getType() == Mso.TYPE_ENTERPRISE) {
            List<NnUser> users = dao.findByType(NnUser.TYPE_ENTERPRISE);
            for (NnUser user : users) {
                if (user.getMsoId() == mso.getId()) {
                    log.info("found TYPE_ENTERPRISE");
                    return user;
                }
            }
        }
        return null;
    }
    
    public NnUser findTcoUser(Mso mso, short shard) {
        List<NnUser> users = dao.findTcoUser(mso, shard);
        if (users.size() > 0) {
            log.info("found TYPE_TCO");
            return users.get(0);
        }
        return null;
    }

    public NnUser resetPassword(NnUser user) {
        user.setPassword(user.getPassword());
        user.setSalt(AuthLib.generateSalt());
        user.setCryptedPassword(AuthLib.encryptPassword(user.getPassword(), user.getSalt()));
        this.save(user);
        return user;
    }
    
    public NnUser findNNUser() {
        return dao.findNNUser();
    }
     
    public void subscibeDefaultChannels(NnUser user) {
        NnChannelManager channelMngr = new NnChannelManager();        
        List<NnChannel> channels = channelMngr.findMsoDefaultChannels(user.getMsoId(), false);    
        NnUserSubscribeManager subManager = new NnUserSubscribeManager();
        for (NnChannel c : channels) {
            subManager.subscribeChannel(user, c);
        }
        log.info("user " +  user.getId() + "(" + user.getToken() + ") subscribe " + channels.size() + " channels (mso:" + user.getMsoId() + ")");
    }
    
    public NnUser findByToken(String token) {
        return dao.findByToken(token);
    }
    
    //expect format shard-userId. example 1-1
    //if "-" is not present, assuming it's shard 1    
    public NnUser findByIdStr(String id) {
        if (id == null)
            return null;
        String[] splits = id.split("-");
        short shard = 1;
        long uid = 0;
        if (splits.length == 2) {
            uid = Long.parseLong(splits[1]);
            if (splits[0].equals("2"))
                shard = 2;
        } else {
            uid = Long.parseLong(id);
        }
        return this.findById(uid, shard);
    }
    
    //TODO add shard search
    public NnUser findByFbId(String fbId) {
        return dao.findByFbId(fbId);
    }
    
    // find user by ID without providing shard number
    public NnUser findById(long id) {
        return dao.findById(id);
    }
    
    public NnUser findById(long id, short shard) {
        return dao.findById(id, shard);
    }
    
    public List<NnUser> list(int page, int limit, String sidx, String sord) {
        return dao.list(page, limit, sidx, sord);
    }
    
    public List<NnUser> list(int page, int limit, String sidx, String sord, String filter) {
        return dao.list(page, limit, sidx, sord, filter);
    }
    
    public int total() {
        return dao.total();
    }
    
    public int total(String filter) {
        return dao.total(filter);
    }
    
    //specify email or name is used in flipr, otherwise use generic to match email/name/intro
    public List<NnUser> search(String email, String name, String generic) {
        return dao.search(email, name, generic);
    }
    
    public List<NnUser> findFeatured() {
        return dao.findFeatured();
    }
    
    public NnUser findByProfileUrl(String profileUrl) {
        return dao.findByProfileUrl(profileUrl);
    }

    public String composeCuratorInfo(List<NnUser> users, boolean chCntLimit, HttpServletRequest req) {
        String result = "";
        NnChannelManager chMngr = new NnChannelManager();
        List<NnChannel> curatorChannels = new ArrayList<NnChannel>();
        for (NnUser u : users) {
            if (chCntLimit) {
                curatorChannels.addAll(chMngr.findByUser(u, 1, true));
            } else {
                curatorChannels.addAll(chMngr.findByUser(u, 0, true));
            }
            String ch = "";
            if (curatorChannels.size() > 0) {
                ch = String.valueOf(curatorChannels.get(0).getId());
            }
            result += this.composeCuratorInfoStr(u, ch, req) + "\n";
        }
        result += "--\n";
        System.out.println("curator channel:" + curatorChannels.size());
        result += chMngr.composeChannelLineup(curatorChannels);
        return result;
    }
    
    public String composeCuratorInfoStr(NnUser user, String channelId, HttpServletRequest req) {
        String uid = user.getShard() + "-" + user.getId();
        //#!curator=xxx-name
        String profileUrl = "";
        if (user.getProfileUrl() != null)
            profileUrl = NnNetUtil.getUrlRoot(req) + "/#!curator=" + user.getProfileUrl();
        String[] info = {
                uid,                
                user.getName(),
                user.getIntro(),
                user.getImageUrl(),
                profileUrl,
                String.valueOf(user.getCntChannel()),
                String.valueOf(user.getCntSubscribe()),
                String.valueOf(user.getCntFollower()),              
                channelId,
               };
        String output = NnStringUtil.getDelimitedStr(info);
        return output;
    }

    public static boolean isGuestByToken(String token) {
        if (token != null && token.contains(NnGuest.TOKEN_PREFIX)) {
            return true;
        }
        return false;
    }
    
}
