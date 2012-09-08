package com.nncloudtv.service;

import java.util.ArrayList;
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
            List<TagMap> map = dao.findMap(tag.getId());
            for (TagMap m : map) {
                NnChannel c = chMngr.findById(m.getChannelId());
                if (c != null)
                    channels.add(c);
            }
        }        
        return channels;
    }        
        
    public Tag findByName(String name) {
        Tag tag = dao.findByName(name);
        return tag;
    }
}
