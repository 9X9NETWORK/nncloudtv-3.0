package com.nncloudtv.lib;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieHelper {

    static public String GUEST = "guest";
    static public String USER = "user";
    static public String DEVICE = "device";
    static public String PLATFORM = "platform";
    static public String PLATFORM_GAE = "gae";
    static public String MSO = "mso";
    static public String CMS_SESSION = "session";
    
    static public void deleteCookie(HttpServletResponse resp, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }
    
    //add expiration
    static public void setCookie(HttpServletResponse resp, String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        // set cookie to expire after one year
        cookie.setMaxAge(31536000);
        resp.addCookie(cookie);        
    }
    
    static public String getCookie(HttpServletRequest req, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(cookieName)) {
                    return cookies[i].getValue();
                }           
            }
        }
        return null;
    }
}
