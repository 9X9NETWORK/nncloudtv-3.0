package com.nncloudtv.web.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.web.json.cms.Set;
import com.nncloudtv.web.json.cms.User;

public class ApiGeneric {
	
	protected static Logger log = Logger.getLogger(ApiGeneric.class.getName());
	
	public static final String MISSING_PARAMETER = "Missing Parameter";
	public static final String INVALID_PATH_PARAMETER = "Invalid Path Parameter";
	public static final String INVALID_PARAMETER = "Invalid Parameter";
	public static final String INVALID_YOUTUBE_URL = "Invalid YouTube URL";
	public static final String PLAIN_TEXT_UTF8 = "text/plain; charset=utf-8";
	public static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
	public static final String API_DOC = "API-DOC";
	public static final String API_DOC_URL = "http://goo.gl/H7Jzl"; // API design document url
	public static final String BLACK_HOLE = "Black Hole!";
	
	public void unauthorized(HttpServletResponse resp, String message) {
        try {
            resp.reset();
            resp.setContentType(PLAIN_TEXT_UTF8);
            resp.setHeader(API_DOC, API_DOC_URL);
            if (message != null) {
                log.warning(message);
                resp.getWriter().println(message);
            }
            resp.setStatus(401);
            resp.flushBuffer();
        } catch (IOException e) {
            internalError(resp, e);
        }
	}
	
	public void unauthorized(HttpServletResponse resp) {
	    unauthorized(resp, null);
	}
	
	public void forbidden(HttpServletResponse resp, String message) {
        try {
            resp.reset();
            resp.setContentType(PLAIN_TEXT_UTF8);
            resp.setHeader(API_DOC, API_DOC_URL);
            if (message != null) {
                log.warning(message);
                resp.getWriter().println(message);
            }
            resp.setStatus(403);
            resp.flushBuffer();
        } catch (IOException e) {
            internalError(resp, e);
        }
    }
	
	public void forbidden(HttpServletResponse resp) {
	    forbidden(resp, null);
    }
	
	public void notFound(HttpServletResponse resp, String message) {
		
		try {
			resp.reset();
            resp.setContentType(PLAIN_TEXT_UTF8);
            resp.setHeader(API_DOC, API_DOC_URL);
			if (message != null) {
				log.warning(message);
				resp.getWriter().println(message);
			}
			resp.setStatus(404);
			resp.flushBuffer();
		} catch (IOException e) {
			internalError(resp, e);
		}
		
	}
	
	public void notFound(HttpServletResponse resp) {
		notFound(resp, null);
	}
	
	public void badRequest(HttpServletResponse resp) {
		badRequest(resp, null);
	}
	
	public void badRequest(HttpServletResponse resp, String message) {
		
		try {
			resp.reset();
            resp.setContentType(PLAIN_TEXT_UTF8);
            resp.setHeader(API_DOC, API_DOC_URL);
			if (message != null) {
				log.warning(message);
				resp.getWriter().println(message);
			}
			resp.setStatus(400);
			resp.flushBuffer();
		} catch (IOException e) {
			internalError(resp, e);
		}
		
	}
	
	public void internalError(HttpServletResponse resp) {
	    internalError(resp, null);
	}
	
	public void internalError(HttpServletResponse resp, Exception e) {
		
		try {
			resp.reset();
            resp.setContentType(PLAIN_TEXT_UTF8);
            resp.setHeader(API_DOC, API_DOC_URL);
			PrintWriter writer = resp.getWriter();
			if (e != null) {
                NnLogUtil.logException(e);
	            writer.println(e.getMessage());
			}
			resp.setStatus(500);
			resp.flushBuffer();
		} catch (IOException ex) {
			NnLogUtil.logException(ex);
		}
	}
	
	/** used for identify the client who is, return userId if exist. */
	public Long userIdentify(HttpServletRequest req) {
	    
	    String token = CookieHelper.getCookie(req, "user");
	    if (token == null) {
            return null;
        }
	    NnUserManager userMngr = new NnUserManager();
	    Long userId = userMngr.findUserIdByToken(token);
	    return userId;
	}
	
    public void okResponse(HttpServletResponse resp) {

        try {
            resp.setContentType(APPLICATION_JSON_UTF8);
            resp.getWriter().print("\"OK\"");
            resp.flushBuffer();
        } catch (IOException e) {
            internalError(resp, e);
        }

    }
    
	public void nullResponse(HttpServletResponse resp) {
        
        try {
            resp.setContentType(APPLICATION_JSON_UTF8);
            resp.getWriter().print("null");
            resp.flushBuffer();
        } catch (IOException e) {
            internalError(resp, e);
        }
        
    }
	
	/** adapt response for user change to user+userProfile */
	public User userResponse(NnUser user) {
	    //Map<String, Object> result = new TreeMap<String, Object>();
	    User userResp = new User();
	    
	    //result.put("id", user.getId());
	    userResp.setId(user.getId());
	    //result.put("createDate", user.getCreateDate());
	    userResp.setCreateDate(user.getCreateDate());
	    //result.put("updateDate", user.getUpdateDate());
	    userResp.setUpdateDate(user.getUpdateDate());
	    //result.put("userEmail", user.getUserEmail());
	    userResp.setUserEmail(user.getUserEmail());
	    //result.put("fbUser", user.isFbUser());
	    userResp.setFbUser(user.isFbUser());
	    //result.put("name", NnStringUtil.revertHtml(user.getProfile().getName()));
	    userResp.setName(NnStringUtil.revertHtml(user.getProfile().getName()));
	    //result.put("intro", NnStringUtil.revertHtml(user.getProfile().getIntro()));
	    userResp.setIntro(NnStringUtil.revertHtml(user.getProfile().getIntro()));
	    //result.put("imageUrl", user.getProfile().getImageUrl());
	    userResp.setImageUrl(user.getProfile().getImageUrl());
	    //result.put("lang", user.getProfile().getLang());
	    userResp.setLang(user.getProfile().getLang());
	    //result.put("profileUrl", user.getProfile().getProfileUrl());
	    userResp.setProfileUrl(user.getProfile().getProfileUrl());
	    //result.put("shard", user.getShard());
	    userResp.setShard(user.getShard());
	    //result.put("sphere", user.getProfile().getSphere());
	    userResp.setSphere(user.getProfile().getSphere());
	    //result.put("type", user.getType());
	    userResp.setType(user.getType());
	    //result.put("cntSubscribe", user.getProfile().getCntSubscribe());
	    userResp.setCntSubscribe(user.getProfile().getCntSubscribe());
	    //result.put("cntChannel", user.getProfile().getCntChannel());
	    userResp.setCntChannel(user.getProfile().getCntChannel());
	    //result.put("cntFollower", user.getProfile().getCntFollower());
	    userResp.setCntFollower(user.getProfile().getCntFollower());
	    userResp.setMsoId(user.getProfile().getMsoId());
	    if (user.getProfile().getPriv() == null) {
	        userResp.setPriv("000111"); // TODO hard coded default
	    } else {
	        userResp.setPriv(user.getProfile().getPriv());
	    }
	    
	    return userResp;
	}
	
	/** compose set response **/
	public Set setResponse(SysTag set, SysTagDisplay setMeta) {
        //Map<String, Object> result = new TreeMap<String, Object>();
        Set setResp = new Set();
        
        //result.put("id", set.getId());
        setResp.setId(set.getId());
        //result.put("msoId", set.getMsoId());
        setResp.setMsoId(set.getMsoId());
        //result.put("channelCnt", setMeta.getCntChannel());
        setResp.setChannelCnt(setMeta.getCntChannel());
        //result.put("lang", setMeta.getLang());
        setResp.setLang(setMeta.getLang());
        //result.put("seq", set.getSeq());
        setResp.setSeq(set.getSeq());
        //result.put("tag", setMeta.getPopularTag());
        setResp.setTag(setMeta.getPopularTag());
        //result.put("name", NnStringUtil.revertHtml(setMeta.getName()));
        setResp.setName(NnStringUtil.revertHtml(setMeta.getName()));
        //result.put("sortingType", set.getSorting());
        setResp.setSortingType(set.getSorting());
        
        //return result;
        return setResp;
    }
	
	/** log the enter state
	 *  @param now the enter time
	 *  */
	public String printEnterState(Date now, HttpServletRequest req) {
	    
	    if (now == null || req == null) {
            return null;
        }
	    
	    String result = req.getRequestURI() + "@" + now + "[";
	    String parameterPairs = "";
	    
	    try {
	        Map<String, String[]> map = (Map<String, String[]>) req.getParameterMap();
	        Enumeration<String> names = (Enumeration<String>) req.getParameterNames();
        
	        while(names.hasMoreElements()) {
	            
	            String name = names.nextElement();
	            String[] values = map.get(name);
	            
	            parameterPairs += "," + name + "=";
	            if (values.length > 1) {
	                parameterPairs += "(";
	            }
	            
	            String parameterValues = "";
	            for (String value : values) {
	                parameterValues += ",\"" + value + "\"";
	            }
	            parameterValues = parameterValues.replaceFirst(",", "");
	            
	            parameterPairs += parameterValues;
	            if (values.length > 1) {
	                parameterPairs += ")";
	            }
	        }
	        parameterPairs = parameterPairs.replaceFirst(",", "");
	        
	    } catch (ClassCastException e) {
	        NnLogUtil.logException(e);
	    }
	    
	    result += parameterPairs + "]";
        
	    return result;
	}
	
	/** log the exit state
	 *  @param now the enter time, not the exit time
	 *  @param exitState the exit state : ok, 400, 401, 403, 404
	 *  */
	public String printExitState(Date now, HttpServletRequest req, String exitState) {
	    return req.getRequestURI() + "@" + now + "[exit-state=" + exitState + "]";
	}
	
}
