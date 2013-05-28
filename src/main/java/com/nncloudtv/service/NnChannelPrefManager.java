package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelPrefDao;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnChannelPref;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.web.json.facebook.FacebookPage;

@Service
public class NnChannelPrefManager {
	
	protected static final Logger log = Logger
	        .getLogger(NnChannelPrefManager.class.getName());
	
	private NnChannelPrefDao prefDao = new NnChannelPrefDao();
	
	public NnChannelPref save(NnChannelPref pref) {
		Date now = new Date();
		if (pref.getCreateDate() == null)
			pref.setCreateDate(now);
		pref.setUpdateDate(now);
		return prefDao.save(pref);
	}
	
	public List<NnChannelPref> save(List<NnChannelPref> prefs) {
        Date now = new Date();
        for (NnChannelPref pref : prefs) {
            if (pref.getCreateDate() == null) {
                pref.setCreateDate(now);
            }
            pref.setUpdateDate(now);
        }
        return prefDao.saveAll(prefs);
    }
	
	public List<NnChannelPref> findByChannelId(long channelId) {
		return prefDao.findByChannelId(channelId);
	}
	
	public List<NnChannelPref> findByChannelIdAndItem(long channelId, String item) {
		return prefDao.findByChannelIdAndItem(channelId, item);
	}
	
	public void delete(NnChannelPref pref) {
	    if (pref != null) {
	        prefDao.delete(pref);
	    }
	}
	
	public void delete(List<NnChannelPref> prefs) {
	    if (prefs != null && prefs.size() > 0) {
	        prefDao.deleteAll(prefs);
	    }
    }
	
	public String composeFacebookAutoshare(String fbUserId, String accessToken) {
        return fbUserId + "," + accessToken;
    }
	
	public String[] parseFacebookAutoshare(String value) {
	    int seperatorIndex = value.indexOf(',');
	    if(seperatorIndex == -1) {
	        return null;
	    } else if (seperatorIndex + 1 == value.length()) { // the ',' is the last one char
	        return null;
	    } else if (seperatorIndex == 0) { // the ',' is the first one char
	        return null;
	    }
	    String[] result = new String[2];
	    result[0] = value.substring(0, seperatorIndex); // facebookID
	    result[1] = value.substring(seperatorIndex + 1); // accessToken
	    return result;
	}
	
	public void deleteAllChannelsFBbyUser(NnUser user) {
	    NnChannelManager channelMngr = new NnChannelManager();
        List<NnChannel> channels = channelMngr.findByUser(user, 0, true);
        List<NnChannelPref> channelPrefs = new ArrayList<NnChannelPref>();
        List<NnChannelPref> temp;
        for (NnChannel channel : channels) {
            temp = findByChannelIdAndItem(channel.getId(), NnChannelPref.FB_AUTOSHARE);
            if (temp != null && temp.size() > 0) {
                channelPrefs.addAll(temp);
            }
        }
        delete(channelPrefs);
	}
	
	public void updateAllChannelsFBbyUser(NnUser user, List<FacebookPage> pages) {
	    if (pages == null || pages.size() == 0 || user == null) {
	        return ;
	    }
	    
	    NnChannelManager channelMngr = new NnChannelManager();
        List<NnChannel> channels = channelMngr.findByUser(user, 0, true);
        List<NnChannelPref> channelPrefs = new ArrayList<NnChannelPref>();
        List<NnChannelPref> temp;
        for (NnChannel channel : channels) {
            temp = findByChannelIdAndItem(channel.getId(), NnChannelPref.FB_AUTOSHARE);
            if (temp != null && temp.size() > 0) {
                channelPrefs.addAll(temp);
            }
        }
        
        Map<String, String> pageMap = new TreeMap<String, String>();
        for (FacebookPage page : pages) {
            pageMap.put(page.getId(), page.getAccess_token());
        }
        
        String[] parsed;
        for (NnChannelPref channelPref : channelPrefs) {
            parsed = parseFacebookAutoshare(channelPref.getValue());
            if (pageMap.containsKey(parsed[0])) {
                channelPref.setValue(composeFacebookAutoshare(parsed[0], pageMap.get(parsed[0])));
            }
        }
	    
        save(channelPrefs);
	}
	
	public void setBrand(Long channelId, Mso mso) {
	    
	    if (channelId == null || mso == null) {
	        return ;
	    }
	    
	    List<NnChannelPref> channelPrefs = findByChannelIdAndItem(channelId, NnChannelPref.BRAND_AUTOSHARE);
	    if (channelPrefs != null && channelPrefs.size() > 0) {
	        NnChannelPref channelPref = channelPrefs.get(0);
	        if (channelPref.getValue().equals(mso.getName())) {
	            // skip
	            return ;
	        } else {
	            channelPref.setValue(mso.getName());
	            save(channelPref);
	        }
	    } else {
	        NnChannelPref channelPref = new NnChannelPref(channelId, NnChannelPref.BRAND_AUTOSHARE, mso.getName());
	        save(channelPref);
	    }
	}
	
	public NnChannelPref getBrand(Long channelId) {
	    
	    if (channelId == null) {
	        return null;
	    }
	    
	    List<NnChannelPref> channelPrefs = findByChannelIdAndItem(channelId, NnChannelPref.BRAND_AUTOSHARE);
	    if (channelPrefs != null && channelPrefs.size() > 0) {
	        //return channelPrefs.get(0);
	    } else {
	        MsoManager msoMngr = new MsoManager();
	        return new NnChannelPref(channelId, NnChannelPref.BRAND_AUTOSHARE, msoMngr.findNNMso().getName());
	    }
	    
	    NnChannelPref pref = channelPrefs.get(0);
	    MsoManager msoMngr = new MsoManager();
        Mso mso = msoMngr.findByName(pref.getValue());
        if (msoMngr.isValidBrand(channelId, mso) == false) {
            return new NnChannelPref(channelId, NnChannelPref.BRAND_AUTOSHARE, msoMngr.findNNMso().getName());
        }
        
        return pref;
	}
	
}
