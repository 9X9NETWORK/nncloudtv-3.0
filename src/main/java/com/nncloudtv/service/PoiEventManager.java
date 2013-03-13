package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.PoiEventDao;
import com.nncloudtv.dao.PoiMapDao;
import com.nncloudtv.model.PoiEvent;
import com.nncloudtv.model.PoiMap;

@Service
public class PoiEventManager {
    
    private PoiEventDao dao = new PoiEventDao();
    private PoiMapDao poiMapDao = new PoiMapDao();
    
    protected static final Logger log = Logger.getLogger(PoiEventManager.class
            .getName());
    
    public PoiEvent create(PoiEvent poiEvent) {
        Date now = new Date();
        poiEvent.setCreateDate(now);
        poiEvent.setUpdateDate(now);
        poiEvent = dao.save(poiEvent);
        return poiEvent;
    }
    
    public PoiEvent save(PoiEvent poiEvent) {
        Date now = new Date();
        poiEvent.setUpdateDate(now);
        poiEvent = dao.save(poiEvent);
        return poiEvent;
    }
    
    public void delete(List<PoiEvent> poiEvents) {
        dao.deleteAll(poiEvents);
    }
    
    public void deleteByIds(List<Long> eventIds) {
        List<PoiEvent> poiEvents = dao.findAllByIds(eventIds);
        dao.deleteAll(poiEvents);
    }
    
    public static String composeContext(Map<String, Object> context, int eventType) {
        // compose rule
        String result = "";
        if (eventType == PoiEvent.EVENTTYPE_HYPERCHANNEL) {
            if (context.containsKey("link")) {
                result += context.get("link");
            }
            result += "|";
            if (context.containsKey("button")) {
                result += context.get("button");
            }
        }
        return result;
    }
    
    private Map<String, Object> explainContext_hyperChannel(String context) {
        // pair with compose rule
        Map<String, Object> output = new TreeMap<String, Object>();
        String[] values = context.split("\\|");
        switch (values.length) {
        case 0:
            output.put("link", "");
            output.put("button", "");
            break;
        case 1:
            output.put("link", values[0]);
            output.put("button", "");
            break;
        case 2:
            output.put("link", values[0]);
            output.put("button", values[1]);
            break;
        default:
            output.put("link", "");
            output.put("button", "");
        }
        
        return output;
    }
    
    public Map<String, Object> eventExplainFactory(PoiEvent event) {
        Map<String, Object> output = new TreeMap<String, Object>();
        if (event.getType() == PoiEvent.EVENTTYPE_HYPERCHANNEL) {
            Map<String, Object> context = explainContext_hyperChannel(event.getContext());
            output.putAll(context);
        }
        output.put("message", event.getMessage());
        
        return output;
    }
    
    public List<PoiEvent> findByPoiId(long poiId) {
        List<Long> eventIds = new ArrayList<Long>();
        List<PoiEvent> poiEvents = new ArrayList<PoiEvent>();
        List<PoiMap> poiMaps = poiMapDao.findByPoiId(poiId);
        if (poiMaps.size() == 0) {
            return poiEvents;
        }
        for (PoiMap poiMap : poiMaps) {
            eventIds.add(poiMap.getEventId());
        }
        poiEvents = dao.findAllByIds(eventIds);
        return poiEvents;
    }
    
    public PoiEvent findByPoi(long poiId) {
        return dao.findByPoi(poiId);
    }

}
