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
	public static final String BAD_PARAMETER = "Bad Parameter";
	public static final String PLAIN_TEXT_UTF8 = "plain/text;charset=utf-8";
	
	public void unauthorized(HttpServletResponse resp) {
		try {
			resp.sendError(401);
		} catch (IOException e) {
			internalError(resp, e);
		}
	}
	
	public void notFound(HttpServletResponse resp, String message) {
		
		log.warning(message);
		
		try {
			resp.reset();
			if (message != null)
				resp.getWriter().println(message);
			resp.setContentType(PLAIN_TEXT_UTF8);
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
		
		log.warning(message);
		
		try {
			resp.reset();
			if (message != null)
				resp.getWriter().println(message);
			resp.setContentType(PLAIN_TEXT_UTF8);
			resp.setStatus(400);
			resp.flushBuffer();
		} catch (IOException e) {
			internalError(resp, e);
		}
		
	}
	
	public void internalError(HttpServletResponse resp, Exception e) {
		
		NnLogUtil.logException(e);
		
		try {
			resp.reset();
			PrintWriter writer = resp.getWriter();
			writer.println(e.getMessage());
			resp.setContentType(PLAIN_TEXT_UTF8);
			resp.setStatus(500);
			resp.flushBuffer();
		} catch (IOException ex) {
			// nothing can do
		}
	}
}
