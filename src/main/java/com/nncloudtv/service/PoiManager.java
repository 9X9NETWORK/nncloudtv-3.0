package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.PoiDao;
import com.nncloudtv.dao.PoiMapDao;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.Poi;
import com.nncloudtv.model.PoiEvent;
import com.nncloudtv.model.PoiMap;

@Service
public class PoiManager {
    protected static final Logger log = Logger.getLogger(PoiManager.class.getName());
    
    private PoiDao dao = new PoiDao();
    private PoiMapDao poiMapDao = new PoiMapDao();
    private PoiEventManager poiEventMngr = new  PoiEventManager();
    
    public Poi create(Poi poi) {
        Date now = new Date();
        poi.setCreateDate(now);
        poi.setUpdateDate(now);
        poi = dao.save(poi);
        return poi;
    }
    
    public Poi save(Poi poi) {
        Date now = new Date();
        poi.setUpdateDate(now);
        poi = dao.save(poi);
        return poi;
    }
    
    public void delete(Poi poi) {
        if (poi == null) {
            return ;
        }
        List<PoiMap> poiMaps = poiMapDao.findByPoiId(poi.getId());
        List<Long> eventIds = new ArrayList<Long>();
        for (PoiMap poiMap : poiMaps) {
            eventIds.add(poiMap.getEventId());
        }
        poiEventMngr.deleteByIds(eventIds);
        poiMapDao.deleteAll(poiMaps);
        dao.delete(poi);
    }
    
    public void delete(List<Poi> pois) {
        List<PoiMap> poiMaps = new ArrayList<PoiMap>();
        List<PoiMap> temps;
        List<Long> eventIds = new ArrayList<Long>();
        for (Poi poi : pois) {
            temps = poiMapDao.findByPoiId(poi.getId()); //TODO: computing issue, try to reduce mysql queries
            for (PoiMap temp : temps) {
                eventIds.add(temp.getEventId());
            }
            poiMaps.addAll(temps);
        }
        poiEventMngr.deleteByIds(eventIds);
        poiMapDao.deleteAll(poiMaps);
        dao.deleteAll(pois);
    }
    
    public boolean hookEvent(long poiId, long eventId) {
        PoiMap poiMap = new PoiMap();
        Date now = new Date();
        poiMap.setPoiId(poiId);
        poiMap.setEventId(eventId);
        poiMap.setUpdateDate(now);
        poiMap = poiMapDao.save(poiMap);
        
        if (poiMap != null) {
            return true;
        } else {
            return false;
        }
    }
    
    public Poi findById(long id) {
        return dao.findById(id);
    }
    
    public List<Poi> findByProgramId(long programId) {
        return dao.findByProgram(programId);
    }
    
    public List<Poi> findByChannel(long channelId) {
        return dao.findByChannel(channelId);
    }
    
    public List<Poi> findByProgram(long programId) {
        return dao.findByProgram(programId);
    }
    
    public Map<String, Object> getEventByPoi(Poi poi) {
        Map<String, Object> result = new TreeMap<String, Object>();
        if (poi == null) {
            return result;
        }
        
        // event part
        List<PoiEvent> poiEvents = poiEventMngr.findByPoiId(poi.getId());
        if (poiEvents.size() == 0) {
            // dynamic assign event by rules
            
        } else {
            // there should apply some rule to choose an event
            PoiEvent event = poiEvents.get(0);
            result.putAll(poiEventMngr.eventExplainFactory(event));
        }
        
        // POI part
        result.put("id", poi.getId());
        result.put("programId", poi.getProgramId());
        result.put("name", poi.getName());
        result.put("intro", poi.getIntro());
        result.put("startTime", poi.getStartTimeInt());
        result.put("endTime", poi.getEndTimeInt());
        result.put("tag", poi.getTag());
        
        return result;
    }
    
    public List<Map<String, Object>> getEventsByProgram(NnProgram program) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        if (program == null) {
            return results;
        }
        List<Poi> pois = dao.findByProgram(program.getId());
        for (Poi poi : pois) {
            results.add(getEventByPoi(poi)); // TODO: computing issue, try to reduce mysql queries, List<List<PoiEvent>> List<int>
        }
        return results;
    }
    
    // list which need revertHtml, should avoid collision issue
    public static Map<String, Object> revertHtml(Map<String, Object> responseObj) {
        String[] keys = {"name", "intro", "message", "button"}; 
        
        for (String key : keys) {
            if(responseObj.containsKey(key)) {
                String temp = (String) responseObj.get(key);
                temp = NnStringUtil.revertHtml(temp);
                responseObj.put(key, temp);
            }
        }
        
        return responseObj;
    }
    
    public boolean isPoiCollision(Poi originPoi, NnProgram program, int startTime, int endTime) {
        if (program == null) {
            return true;
        }
        if (startTime < 0 || endTime < 1 || endTime <= startTime) {
            return true;
        }
        if (startTime < program.getStartTimeInt() || startTime >= program.getEndTimeInt())
            return true;
        if (endTime <= program.getStartTimeInt() || endTime > program.getEndTimeInt())
            return true;
        
        List<Poi> pois = dao.findByProgram(program.getId());
        if (originPoi != null) {
            if (pois.contains(originPoi)) {
                pois.remove(originPoi);
            }
        }
        int duration = program.getDurationInt();
        for (Poi poi : pois) {
            if (startTime >= poi.getStartTimeInt() && startTime < poi.getEndTimeInt()) {
                return true;
            }
            if ((poi.getStartTimeInt() - startTime) > 0 && (poi.getStartTimeInt() - startTime) < duration) {
                duration = poi.getStartTimeInt() - startTime;
            }
        }
        if (endTime <= startTime + duration) {
            return false;
        } else {
            return true;
        }
    }

}
