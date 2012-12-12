package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnChannelPrefDao;
import com.nncloudtv.model.NnChannelPref;

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
	    if (prefs != null && prefs.size() != 0) {
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
}
