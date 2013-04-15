package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.TagDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.Tag;
import com.nncloudtv.model.TagMap;

@Service
public class TagManager {

    protected static final Logger log = Logger.getLogger(TagManager.class.getName());
    private TagDao dao = new TagDao();    

    //player=true returns only "good" channels
    public List<NnChannel> findChannelsByTag(String name, boolean player) {        
        Tag tag = dao.findByName(name);
        List<NnChannel> channels = new ArrayList<NnChannel>();
        NnChannelManager chMngr = new NnChannelManager();
        if (tag != null) {            
            List<TagMap> map = dao.findMapByTag(tag.getId());
            for (TagMap m : map) {
                NnChannel c = chMngr.findById(m.getChannelId());
                if (c != null) {
                    if (!player)
                        channels.add(c);
                    if (player && c.isPublic() && c.getStatus() == NnChannel.STATUS_SUCCESS)
                        channels.add(c);
                }
            }
        }        
        return channels;
    }        

    public List<Tag> findByChannel(long chId) {
    	List<TagMap> list = dao.findMapByChannel(chId);
    	List<Tag> tags = new ArrayList<Tag>();
    	for (TagMap m : list) {
    		Tag t = this.findById(m.getTagId());
    		if (t != null)
    			tags.add(t);
    	}    	
    	return tags;
    }
    
    public Tag findById(long id) {        
        return dao.findById(id);
    }
    
    public static String getValidTag(String tag) {
    	if (tag == null)
    		return null;
        tag = tag.replaceAll("[^\\w\\s\\p{L}]", "");
        tag = tag.replaceAll("[\\t\\n\\x0B\\f\\r]", " ");        
        tag = tag.replaceAll("[\\s]+", " ");
        tag = tag.trim();
        tag = tag.toLowerCase();
    	return tag;
    }
    
    public Tag findByName(String name) {
        Tag tag = dao.findByName(name);
        return tag;
    }
    
    public void save(Tag tag) {
        tag.setUpdateDate(new Date());
        dao.save(tag);
    }

    public void createTagMap(long tagId, long channelId) {
        TagMap map = new TagMap(tagId, channelId);
        dao.saveMap(map);
    }

    public void deleteChannel(long tagId, long channelId) {
        TagMap map = this.findByTagAndChannel(tagId, channelId);
        if (map != null) {
            log.info("tag removed: tagId(" + tagId + ") channelId(" + channelId + ")");
        	dao.deleteTagMap(map);
        }
    }    
    
    public TagMap findByTagAndChannel(long tagId, long channelId) {
        return dao.findMapByTagAndChannel(tagId, channelId);
    }
    
    public static String assembleStackName(String name, String lang, String msoName) {
        return name + "(" + msoName + lang + ")"; 
    }
    
    public static String processTagText(String tagText) {
        String result = "";
        String[] multiples = tagText.split(",");
        for (String m : multiples) {
            String tag = TagManager.getValidTag(m);
            if (tag != null && tag.length() > 0 && tag.length() < 20)
                result += "," + tag;
        }
        result = result.replaceFirst(",", "");
        if (result.length() == 0)
            return null;
        return result;
    }
}
