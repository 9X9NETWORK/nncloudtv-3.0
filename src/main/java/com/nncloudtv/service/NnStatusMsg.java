package com.nncloudtv.service;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.nncloudtv.web.api.NnStatusCode;

/**
 * Transition implementation before we can switch to real locale instead of mso.
 * 
 * Shortcut to retrieve messages that are used often.  
 * 
 */
@Service
public class NnStatusMsg {
	private static MessageSource messageSource = new ClassPathXmlApplicationContext("locale.xml");
	
	public static String assembleMsg(int statusCode, String msg) {
		return statusCode + "\t" + msg + "\n";
	}
	
	public static String getPlayerMsg(int status, Locale locale) {
		try {
			switch (status) {
				case NnStatusCode.SUCCESS: return NnStatusMsg.assembleMsg(status, "SUCCESS");
				case NnStatusCode.INFO: return NnStatusMsg.assembleMsg(status, "INFO");
				case NnStatusCode.WARNING: return NnStatusMsg.assembleMsg(status, "WARNING");
				case NnStatusCode.FATAL: return NnStatusMsg.assembleMsg(status, "FATAL");
				case NnStatusCode.ERROR: return NnStatusMsg.assembleMsg(status, "ERROR");
	
				case NnStatusCode.API_DEPRECATED: return NnStatusMsg.assembleMsg(status, "API_DEPRECATED");
				case NnStatusCode.API_UNDER_CONSTRUCTION: return NnStatusMsg.assembleMsg(status, "API_UNDER_CONSTRUCTION");
	
				case NnStatusCode.INPUT_ERROR: return NnStatusMsg.assembleMsg(status, "INPUT_ERROR");
				case NnStatusCode.INPUT_MISSING: return NnStatusMsg.assembleMsg(status, "INPUT_MISSING");
				case NnStatusCode.INPUT_BAD: return NnStatusMsg.assembleMsg(status, "INPUT_BAD");
				case NnStatusCode.CAPTCHA_FAILED: return NnStatusMsg.assembleMsg(status, "CAPTCHA_FAILED");
				case NnStatusCode.CAPTCHA_EXPIRED: return NnStatusMsg.assembleMsg(status, "CAPTCHA_EXPIPRED");
				case NnStatusCode.CAPTCHA_TOOMANY_TRIES: return NnStatusMsg.assembleMsg(status, "CAPTCHA_TOOMANY_TRIES");
				case NnStatusCode.CAPTCHA_INVALID: return NnStatusMsg.assembleMsg(status, "CAPTCHA_INVALID");
				case NnStatusCode.CAPTCHA_ERROR: return NnStatusMsg.assembleMsg(status, "CAPTCHA_ERROR");
	
				case NnStatusCode.PIWIK_INVALID: return NnStatusMsg.assembleMsg(status, "PIWIK_INVALID");
				case NnStatusCode.PIWIK_ERROR: return NnStatusMsg.assembleMsg(status, "PIWIK_ERROR");
	
				case NnStatusCode.DEVICE_INVALID: return NnStatusMsg.assembleMsg(status, "DEVICE_INVALID");
	
				case NnStatusCode.OUTPUT_NO_MSG_DEFINED: return NnStatusMsg.assembleMsg(status, "OUTPUT_NO_MSG_DEFINED");
	
				case NnStatusCode.DATA_ERROR: return NnStatusMsg.assembleMsg(status, "DATA_ERROR");
	
				case NnStatusCode.USER_ERROR: return NnStatusMsg.assembleMsg(status, "USER_ERROR");
				case NnStatusCode.USER_LOGIN_FAILED: return NnStatusMsg.assembleMsg(status, "USER_LOGIN_FAILED");
				case NnStatusCode.USER_EMAIL_TAKEN: return NnStatusMsg.assembleMsg(status, "USER_EMAIL_TAKEN");
				case NnStatusCode.USER_INVALID: return NnStatusMsg.assembleMsg(status, "USER_INVALID");
				case NnStatusCode.USER_TOKEN_TAKEN: return NnStatusMsg.assembleMsg(status, "USER_TOKEN_TAKEN"); 
				case NnStatusCode.USER_PERMISSION_ERROR: return NnStatusMsg.assembleMsg(status, "USER_PERMISSION_ERROR");
				case NnStatusCode.ACCOUNT_INVALID: return NnStatusMsg.assembleMsg(status, "ACCOUNT_INVALID");
				case NnStatusCode.INVITE_INVALID: return NnStatusMsg.assembleMsg(status, "INVITE_INVALID");
				
				case NnStatusCode.MSO_ERROR: return NnStatusMsg.assembleMsg(status, "MSO_ERROR");
				case NnStatusCode.MSO_INVALID: return NnStatusMsg.assembleMsg(status, "MSO_INVALID");
	
				case NnStatusCode.CHANNEL_ERROR: return NnStatusMsg.assembleMsg(status, "CHANNEL_ERROR");
				case NnStatusCode.CHANNEL_URL_INVALID: return NnStatusMsg.assembleMsg(status, "CHANNEL_URL_INVALID");
				case NnStatusCode.CHANNEL_INVALID: return NnStatusMsg.assembleMsg(status, "CHANNEL_INVALID");
				case NnStatusCode.CHANNEL_OR_USER_INVALID: return NnStatusMsg.assembleMsg(status, "CHANNEL_OR_USER_INVALID");
				case NnStatusCode.CHANNEL_STATUS_ERROR: return NnStatusMsg.assembleMsg(status, "CHANNEL_STATUS_ERROR");
				case NnStatusCode.CHANNEL_MAXSIZE_EXCEEDED: return NnStatusMsg.assembleMsg(status, "CHANNEL_MAXSIZE_EXCEEDED");
				case NnStatusCode.CHANNEL_YOUTUBE_NOT_AVAILABLE: return NnStatusMsg.assembleMsg(status, "CHANNEL_YOUTUBE_NOT_AVAILABLE");
	
				case NnStatusCode.PROGRAM_ERROR: return NnStatusMsg.assembleMsg(status, "PROGRAM_ERROR");
				case NnStatusCode.PROGRAM_INVALID: return NnStatusMsg.assembleMsg(status, "PROGRAM_INVALID");
	
				case NnStatusCode.SUBSCRIPTION_ERROR: return NnStatusMsg.assembleMsg(status, "SUBSCRIPTION_ERROR");
				case NnStatusCode.SUBSCRIPTION_DUPLICATE_CHANNEL: return NnStatusMsg.assembleMsg(status, "SUBSCRIPTION_DUPLICATE_CHANNEL");
				case NnStatusCode.SUBSCRIPTION_SET_OCCUPIED: return NnStatusMsg.assembleMsg(status, "SUBSCRIPTION_SET_OCCUPIED");
				case NnStatusCode.SUBSCRIPTION_DUPLICATE_SET: return NnStatusMsg.assembleMsg(status, "SUBSCRIPTION_DUPLICATE_SET");
				case NnStatusCode.SUBSCRIPTION_RO_SET: return NnStatusMsg.assembleMsg(status, "SUBSCRIPTION_RO_SET");
				case NnStatusCode.SUBSCRIPTION_POS_OCCUPIED: return NnStatusMsg.assembleMsg(status, "SUBSCRIPTION_POS_OCCUPIED");
	
				case NnStatusCode.CATEGORY_ERROR: return NnStatusMsg.assembleMsg(status, "CATEGORY_ERROR");
				case NnStatusCode.CATEGORY_INVALID: return NnStatusMsg.assembleMsg(status, "CATEGORY_INVALID");
	
				case NnStatusCode.IPG_ERROR: return NnStatusMsg.assembleMsg(status, "IPG_ERROR");
				case NnStatusCode.IPG_INVALID: return NnStatusMsg.assembleMsg(status, "IPG_INVALID");
	
				case NnStatusCode.SET_ERROR: return NnStatusMsg.assembleMsg(status, "SET_ERROR");
				case NnStatusCode.SET_INVALID: return NnStatusMsg.assembleMsg(status, "SET_INVALID");
	
				case NnStatusCode.SERVER_ERROR: return NnStatusMsg.assembleMsg(status, "SERVER_ERROR");
				case NnStatusCode.SERVER_TIMEOUT: return NnStatusMsg.assembleMsg(status, "SERVER_TIMEOUT");
	
				case NnStatusCode.DATABASE_ERROR: return NnStatusMsg.assembleMsg(status, "DATABASE_ERROR");
				case NnStatusCode.DATABASE_TIMEOUT: return NnStatusMsg.assembleMsg(status, "DATABASE_TIMEOUT");
				case NnStatusCode.DATABASE_NEED_INDEX: return NnStatusMsg.assembleMsg(status, "DATABASE_NEED_INDEX");
				case NnStatusCode.DATABASE_READONLY: return NnStatusMsg.assembleMsg(status, "DATABASE_READONLY:");
				default: return NnStatusMsg.assembleMsg(status, "MESSAGE_UNDEFINED");
			}
		} catch (NoSuchMessageException e) {
			return messageSource.getMessage("nnstatus.output_no_msg_defined", new Object[] {status} , locale);			
		}
	}
}
