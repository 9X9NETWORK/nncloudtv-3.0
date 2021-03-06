package com.nncloudtv.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.nncloudtv.model.NnUserProfile;
import com.nncloudtv.web.api.NnStatusCode;
import com.nncloudtv.web.json.facebook.FacebookMe;

@Service
public class NnUserManager {
    
    protected static final Logger log = Logger.getLogger(NnUserManager.class.getName());
        
    private NnUserDao dao = new NnUserDao();
    private NnUserProfileManager profileMngr = new NnUserProfileManager();
    public static short MSO_DEFAULT = 1; 
    
    //@@@IMPORTANT email duplication is your responsibility
    public int create(NnUser user, HttpServletRequest req, short shard) {
        if (this.findByEmail(user.getEmail(), user.getMsoId(), req) != null) //!!!!! shard or req flexible
            return NnStatusCode.USER_EMAIL_TAKEN;
        NnUserProfile profile = user.getProfile();
        if (profile.getName() != null)            
            profile.setName(profile.getName().replaceAll("\\s", " "));
        user.setEmail(user.getEmail().toLowerCase());
        if (shard == 0)
            shard= NnUserManager.getShardByLocale(req);
        if (user.getToken() == null)
            user.setToken(NnUserManager.generateToken(shard));
        user.setShard(shard);
        Date now = new Date();
        user.setCreateDate(now);
        user.setUpdateDate(now);
        if (profile.getProfileUrl() == null) {
            profile.setProfileUrl(this.generateProfile(user.getProfile().getName()));            
        }                        
        dao.save(user);
        profile.setUserId(user.getId());
        profileMngr.save(user, profile);
        resetChannelCache(user);        
        return NnStatusCode.SUCCESS;
    }

    public NnUser createFakeYoutube(Map<String, String> info, HttpServletRequest req) {
        String name = info.get("author");
        if (name == null)
            return null;
        String imageUrl = info.get("thumbnail");

        /*
        if (info.get("type").equals("playlist")) {
            log.info("author:" + name);            
            Map<String, String> authorData = YouTubeLib.getYouTubeEntry(name, true);
            imageUrl = authorData.get("thumbnail");
        }
        */
        name = name.toLowerCase();
        name = name.replaceAll("\\s", "");
        String email = name + "@9x9.tv";
        log.info("fake youtube email:" + email);
        NnUser user = this.findByEmail(email, 1, req);
        if (user != null)
            return user;
        user = new NnUser(email, "9x9x9x", NnUser.TYPE_FAKE_YOUTUBE);
        user.setShard((short)1);
        user.setMsoId(1);
        NnUserProfile profile = user.getProfile();
        user = this.save(user);
        profile.setProfileUrl(name);
        profile.setImageUrl(imageUrl);
        profile.setUserId(user.getId());
        profileMngr.save(user, user.getProfile());
        log.info("fake youtube user created:" + email);
        return user;
    }
    
    public NnUser setFbProfile(NnUser user, FacebookMe me) {
        if (user == null || me == null)
            return null;
        NnUserProfile profile = user.getProfile();
        if (me.getId() != null) {
            String imageUrl = "http://graph.facebook.com/" + me.getId() + "/picture?width=180&height=180";
            profile.setImageUrl(imageUrl);
        }
        user.setEmail(me.getId());
        user.setFbId(me.getEmail());
        profile.setName(me.getName());
        profile.setGender(me.getGender());
        profile.setSphere(me.getLocale());
        profile.setDob(me.getBirthday());
        user.setToken(me.getAccessToken());
        return user;
    }
    
    //TODO replace name with none-digit/characters
    public String generateProfile(String name) {
    	String profile = RandomStringUtils.randomNumeric(10);
    	return profile;
    	/*
        String profile = "";
        if (name != null) {
            String random = RandomStringUtils.randomNumeric(5);
            name.replace(" ", "");
            profile = random + "-" + name;
        } else {
            profile = RandomStringUtils.randomAlphabetic(10);
        }
        return profile;
        */
    }

    public String forgotPwdToken(NnUser user) {
		byte[] pwd = user.getCryptedPassword();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(pwd);
			StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < thedigest.length; i++) {
	          sb.append(Integer.toString((thedigest[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        String token = sb.toString();
	        log.info("Digest(in hex format):: " + token);
	        return token;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
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
            //URL url = new URL("http://brussels.teltel.com/geoip/?ip=" + ip);
            URL url = new URL("http://geoip.9x9.tv/geoip/?ip=" + ip);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
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
        } catch (java.net.SocketException e) {
           log.info("socket connect exception");
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
        NnUser guest = new NnUser(NnUser.GUEST_EMAIL, password, NnUser.TYPE_USER);
        this.create(guest, req, (short)0);
        return guest;
    }
    
    public NnUser save(NnUser user) {
        if (user == null) {
            return null;
        }
        if (user.getPassword() != null) {
            user.setSalt(AuthLib.generateSalt());
            user.setCryptedPassword(AuthLib.encryptPassword(user.getPassword(), user.getSalt()));
        }
        user.setEmail(user.getEmail().toLowerCase());
        user.setUpdateDate(new Date());
        NnUserProfile profile = user.getProfile();
        profile = profileMngr.save(user, profile);
        resetChannelCache(user);
        long msoId = user.getMsoId();
        user = dao.save(user);
        user.setMsoId(msoId);
        user.setProfile(profile);
        return user;
    }

    public void resetChannelCache(NnUser user) {
        NnChannelManager chMngr = new NnChannelManager();
        List<NnChannel> channels = chMngr.findByUser(user, 0, false);
        chMngr.resetCache(channels);
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

    private NnUser setUserProfile(NnUser user) {
        if (user != null) {
            log.info("user mso id:" + user.getMsoId());
            NnUserProfile profile = new NnUserProfileManager().findByUser(user);
            if (profile == null)
                profile = new NnUserProfile(user.getId(), user.getMsoId());
            user.setProfile(profile);
        }
        return user;
    }
    
    //TODO able to assign shard
    //find by email means find by unique id
    public NnUser findByEmail(String email, long msoId, HttpServletRequest req) {
        short shard= NnUserManager.getShardByLocale(req);
        log.info("find by email:" + email.toLowerCase());
        NnUser user = dao.findByEmail(email.toLowerCase(), shard);
        if (user != null) {
            user.setMsoId(msoId);
            user = this.setUserProfile(user);
        }
        return user;
    }        
    
    public NnUser findAuthenticatedUser(String email, String password, long msoId, HttpServletRequest req) {
        short shard= NnUserManager.getShardByLocale(req); 
        NnUser user = dao.findAuthenticatedUser(email.toLowerCase(), password, shard);
        if (user != null) {
            user.setMsoId(msoId);
            user = this.setUserProfile(user);
        }
        return user;
    }

    public NnUser findAuthenticatedMsoUser(String email, String password, long msoId) {
        NnUser user = dao.findAuthenticatedMsoUser(email.toLowerCase(), password, msoId);
        user.setMsoId(msoId);
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
    
    public NnUser findByToken(String token, long msoId) {
        NnUser user = dao.findByToken(token);
        if (user != null) {
            user.setMsoId(msoId);
            this.setUserProfile(user);
        }
        return user;
    }
    
    /** retrieve userId only, use this function to reduce query to userProfile. */
    public Long findUserIdByToken(String token) {
        NnUser user = dao.findByToken(token);
        if (user != null) {
            return user.getId();
        }
        return null;
    }
    
    //expect format shard-userId. example 1-1
    //if "-" is not present, assuming it's shard 1    
    public NnUser findByIdStr(String id, long msoId) {
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
        return this.findById(uid, msoId, shard);
    }
    
    //TODO add shard search
    public NnUser findByFbId(String fbId, long msoId) {
        return dao.findByFbId(fbId);
    }
    
    // find user by ID without providing shard number
    public NnUser findById(long id, long msoId) {
        NnUser user = dao.findById(id);
        if (user != null) {
            user.setMsoId(msoId);
            user = this.setUserProfile(user);
        }
        //user.setMsoId(msoId);
        return user;
    }
    
    public NnUser findById(long id, long msoId, short shard) {
        NnUser user = dao.findById(id, shard);
        if (user != null) {
            user.setMsoId(msoId);
            user = this.setUserProfile(user);
        }
        return user;
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
    public List<NnUser> search(String email, String name, String generic, long msoId) {
        List<NnUser> users = dao.search(email, name, generic, msoId);
        for (NnUser user : users ) {
            user.setMsoId(msoId);
            user = this.setUserProfile(user);            
        }
        return users;
        
    }
    
    public List<NnUser> findFeatured(long msoId) {
        List<NnUser> users = dao.findFeatured(msoId);
        for (NnUser user : users ) {
            user.setMsoId(msoId);
            user = this.setUserProfile(user);            
        }
        return users;
    }
    
    public NnUser findByProfileUrl(String profileUrl, long msoId) {
        NnUser user = dao.findByProfileUrl(profileUrl);
        if (user != null)
            user.setMsoId(msoId);
            user = this.setUserProfile(user);
        return user;
    }

    public String composeCuratorInfo(List<NnUser> users, boolean chCntLimit, boolean isAllChannel, HttpServletRequest req, int version) {
        log.info("looking for all channels of a curator?" + isAllChannel);
        String result = "";
        NnChannelManager chMngr = new NnChannelManager();
        List<NnChannel> curatorChannels = new ArrayList<NnChannel>();
        for (NnUser u : users) {
            List<NnChannel> channels = new ArrayList<NnChannel>();
            if (chCntLimit) {
                channels = chMngr.findByUserAndHisFavorite(u, 1, isAllChannel);
            } else {
                channels = chMngr.findByUserAndHisFavorite(u, 0, isAllChannel);
            }
            curatorChannels.addAll(channels);            
            String ch = "";
            if (channels.size() > 0) {                
                ch = channels.get(0).getIdStr();
            }
            result += this.composeCuratorInfoStr(u, ch, req) + "\n";
        }
        result += "--\n";
        System.out.println("curator channel:" + curatorChannels.size());
        result += chMngr.composeChannelLineup(curatorChannels, version);
        return result;
    }
    
    public String composeSubscriberInfoStr(List<NnChannel> channels) {
        for (NnChannel c : channels) {
            if (c.getContentType() != NnChannel.CONTENTTYPE_FAKE_FAVORITE) {                
            }
        }
        return "";
    }
    
    public String composeCuratorInfoStr(NnUser user, String channelId, HttpServletRequest req) {
        //#!curator=xxxxxxxx
        String profileUrl = "";
        NnUserProfile profile = user.getProfile();
        if (profile.getProfileUrl() != null) {
            profileUrl = NnNetUtil.getUrlRoot(req) + "/#!curator=" + profile.getProfileUrl();
        }
        
        String[] info = {
                profile.getBrandUrl(),                
                profile.getName(),
                profile.getIntro(),
                profile.getImageUrl(),
                profileUrl,
                String.valueOf(profile.getCntChannel()),
                String.valueOf(profile.getCntSubscribe()),
                String.valueOf(profile.getCntFollower()),              
                channelId,
               };
        String output = NnStringUtil.getDelimitedStr(info);
        return output;
    }
    
    public static boolean isGuestByToken(String token) {
        if (token == null || token.length() == 0)
            return true;
        if (token != null && token.contains(NnGuest.TOKEN_PREFIX)) {
            return true;
        }
        return false;
    }
    
    public NnUser purify(NnUser user) {
    
        if (user == null) {
            return null;
        }
        user.setSalt(null);
        user.setCryptedPassword(null);
        
        NnUserProfile profile = user.getProfile();
        if (profile != null) {
            profile.setName(NnStringUtil.revertHtml(profile.getName()));
            profile.setIntro(NnStringUtil.revertHtml(profile.getIntro()));
        }
        
        return user;
    }

    public List<NnUser> findAllByIds(Set<Long> userIdSet) {
    
        return dao.findAllByIds(userIdSet);
    }
    
}
