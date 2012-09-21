package com.nncloudtv.web.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.nncloudtv.lib.NnLogUtil;

public class ApiGeneric {
	
	protected static Logger log = Logger.getLogger(ApiGeneric.class.getName());
	
	public static final String MISSING_PARAMETER = "Missing Parameter";
	public static final String INVALID_PATH_PARAMETER = "Invalid Path Parameter";
	public static final String INVALID_PARAMETER = "Invalid Parameter";
	public static final String INVALID_YOUTUBE_URL = "Invalid YouTube URL";
	public static final String PLAIN_TEXT_UTF8 = "plain/text; charset=utf-8";
	public static final String API_DOC = "API-DOC";
	public static final String API_DOC_URL = "http://goo.gl/H7Jzl"; // API design document url
	public static final String BLACK_HOLE = "Black Hole!";
	
	public void unauthorized(HttpServletResponse resp) {
		try {
			resp.sendError(401);
		} catch (IOException e) {
			internalError(resp, e);
		}
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
}
