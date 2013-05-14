package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.PoiDao;
import com.nncloudtv.dao.PoiPointDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.Poi;
import com.nncloudtv.model.PoiPoint;

@Service
public class PoiPointManager {
    protected static final Logger log = Logger.getLogger(PoiPointManager.class.getName());
    
    private PoiPointDao pointDao = new PoiPointDao();
    private PoiDao poiDao = new PoiDao();
    private PoiEventManager poiEventMngr = new PoiEventManager();
    private NnProgramManager programMngr = new NnProgramManager();
    private NnChannelManager channelMngr = new NnChannelManager();
    
    public PoiPoint create(PoiPoint point) {
        Date now = new Date();
        point.setCreateDate(now);
        point.setUpdateDate(now);
        point = pointDao.save(point);
        return point;
    }
    
    public PoiPoint save(PoiPoint point) {
        Date now = new Date();
        point.setUpdateDate(now);
        point = pointDao.save(point);
        return point;
    }
    
    public void delete(PoiPoint point) {
        
        if (point == null) {
            return ;
        }
        
        List<Poi> pois = poiDao.findByPointId(point.getId());
        List<Long> eventIds = new ArrayList<Long>();
        if (pois != null) {
            for (Poi p : pois) {
                eventIds.add(p.getEventId());
            }
        }
        
        // TODO : rewrite when AD's cms is ready
        if (eventIds.size() > 0) {
            poiEventMngr.deleteByIds(eventIds);
        }
        if (pois != null && pois.size() > 0) {
            poiDao.deleteAll(pois);
        }
        pointDao.delete(point);
    }
    
    public void delete(List<PoiPoint> points) {
        List<Poi> pois = new ArrayList<Poi>();
        List<Poi> temps;
        List<Long> eventIds = new ArrayList<Long>();
        for (PoiPoint point : points) {
            temps = poiDao.findByPointId(point.getId()); // TODO: computing issue, try to reduce mysql queries
            for (Poi temp : temps) {
                eventIds.add(temp.getEventId());
            }
            pois.addAll(temps);
        }
        
        // TODO : rewrite when AD's cms is ready
        poiEventMngr.deleteByIds(eventIds);
        poiDao.deleteAll(pois);
        pointDao.deleteAll(points);
    }
    
    /*
    public boolean hookEvent(long pointId, long eventId) {
        Poi poi = new Poi();
        Date now = new Date();
        poi.setPointId(pointId);
        poi.setEventId(eventId);
        poi.setUpdateDate(now);
        poi = poiDao.save(poi);
        
        if (poi != null) {
            return true;
        } else {
            return false;
        }
    }
    */
    
    public PoiPoint findById(long id) {
        return pointDao.findById(id);
    }
    
    public List<PoiPoint> findByChannel(long channelId) {
        return pointDao.findByChannel(channelId);
    }

    public List<PoiPoint> findCurrentByChannel(long channelId) {
        return pointDao.findCurrentByChannel(channelId);
    }
    
    public List<PoiPoint> findCurrentByProgram(long programId) {
        return pointDao.findCurrentByProgram(programId);
    }
    
    public List<PoiPoint> findByProgram(long programId) {
        return pointDao.findByProgram(programId);
    }
    
    /*
    public Map<String, Object> getEventByPoi(PoiPoint poi) {
        Map<String, Object> result = new TreeMap<String, Object>();
        if (poi == null) {
            return result;
        }
        
        // event part
        List<PoiEvent> poiEvents = poiEventMngr.findPoiEventsByPoiId(poi.getId());
        if (poiEvents.size() == 0) {
            // dynamic assign event by rules
            
        } else {
            // there should apply some rule to choose an event
            PoiEvent event = poiEvents.get(0);
            result.putAll(poiEventMngr.eventExplainFactory(event));
        }
        
        // POI part
        result.put("id", poi.getId());
        result.put("programId", poi.getTargetId());
        result.put("name", poi.getName());
        //result.put("intro", poi.getIntro());
        result.put("intro", null);
        result.put("startTime", poi.getStartTimeInt());
        result.put("endTime", poi.getEndTimeInt());
        result.put("tag", poi.getTag());
        
        return result;
    }
    */
    
    /*
    public List<Map<String, Object>> getEventsByProgram(NnProgram program) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        if (program == null) {
            return results;
        }
        
        // sort
        List<PoiPoint> pois = pointDao.findByProgram(program.getId());
        Collections.sort(pois, getPoiStartTimeComparator());
        
        for (PoiPoint poi : pois) {
            results.add(getEventByPoi(poi)); // TODO: computing issue, try to reduce mysql queries, List<List<PoiEvent>> List<int>
        }
        return results;
    }
    */
    
    // list which need revertHtml, should avoid collision issue
    /*
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
    */
    
    public boolean isPointCollision(PoiPoint originPoint, NnProgram program, int startTime, int endTime) {
        if (program == null) {
            return true;
        }
        if (startTime < 0 || endTime < 1 || endTime <= startTime) {
            return true;
        }
        if (startTime < program.getStartTimeInt() || startTime >= program.getEndTimeInt()) {
            return true;
        }
        if (endTime <= program.getStartTimeInt() || endTime > program.getEndTimeInt())
        {
            return true;
        }
        
        List<PoiPoint> points = pointDao.findByProgram(program.getId());
        if (originPoint != null) {
            if (points.contains(originPoint)) {
                points.remove(originPoint);
            }
        }
        int duration = program.getDurationInt();
        for (PoiPoint point : points) {
            if (startTime >= point.getStartTimeInt() && startTime < point.getEndTimeInt()) {
                return true;
            }
            if ((point.getStartTimeInt() - startTime) > 0 && (point.getStartTimeInt() - startTime) < duration) {
                duration = point.getStartTimeInt() - startTime;
            }
        }
        if (endTime <= startTime + duration) {
            return false;
        } else {
            return true;
        }
    }
    
    // order by StartTime asc
    public Comparator<PoiPoint> getPointStartTimeComparator() {
        
        class PointStartTimeComparator implements Comparator<PoiPoint> {
            public int compare(PoiPoint point1, PoiPoint point2) {
                return (point1.getStartTimeInt() - point2.getStartTimeInt());
            }
        }
        
        return new PointStartTimeComparator();
    }
    
    /** return owner's userId */
    public Long findOwner(PoiPoint point) {
        
        if (point == null) {
            return null;
        }
        
        if (point.getType() == PoiPoint.TYPE_SUBEPISODE) {
            
            NnProgram program = programMngr.findById(point.getTargetId());
            if (program == null) {
                return null;
            }
            NnChannel channel = channelMngr.findById(program.getChannelId());
            if (channel == null) {
                return null;
            }
            return channel.getUserId();
        } else if (point.getType() == PoiPoint.TYPE_CHANNEL) {
            
            NnChannel channel = channelMngr.findById(point.getTargetId());
            if (channel == null) {
                return null;
            }
            return channel.getUserId();
        } else { // other type not used now
            return null;
        }
    }

    public Poi findPoiById(long id) {
        return poiDao.findById(id);
    }
    
    public List<Poi> findCurrentPoiByChannel(long channelId) {
        return poiDao.findCurrentByChannel(channelId);
    }
    
    public List<Poi> findCurrentPoiByProgram(long programId) {
        return poiDao.findCurrentByProgram(programId);
    }
    
}
