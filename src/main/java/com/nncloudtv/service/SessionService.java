package com.nncloudtv.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.CookieHelper;

@Service
// for stateless, we save session in cache now, or other media later
public class SessionService {
    
    protected static final Logger log = Logger.getLogger(SessionService.class.getName());
    
    private HttpSession session;
    
    public SessionService() {
        session = null;
    }
    
    public SessionService(HttpServletRequest request) {        
        session = request.getSession();
        String sessionId = CookieHelper.getCookie(request, CookieHelper.CMS_SESSION);
        if (sessionId == null) {
            sessionId = session.getId();
        }
        log.info("session id = " + sessionId);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) CacheFactory.get(sessionId); 
        if (map != null) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                log.info("cached session = " + name);
                session.setAttribute(name, map.get(name));
            }
        }
    }
    
    public void saveSession(HttpSession session, HttpServletResponse resp) {
        this.session = session;
        saveSession(resp);
    }
    
    public void saveSession(HttpServletResponse resp) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (@SuppressWarnings("unchecked") Enumeration<String> enu = (Enumeration<String>)session.getAttributeNames(); enu.hasMoreElements();) {
            String name = (String)enu.nextElement();
            log.info("put to cache = " + name);
            map.put(name, session.getAttribute(name));
        }
        CacheFactory.set(session.getId(), map);
        CookieHelper.setCookie(resp, CookieHelper.CMS_SESSION, session.getId());
    }
    
    public void removeSession(HttpServletResponse resp) {
        CacheFactory.delete(session.getId());
        CookieHelper.deleteCookie(resp, CookieHelper.CMS_SESSION);
        session.invalidate();
    }
    
    public HttpSession getSession() {
        return session;
    }
}
