package com.nncloudtv.validation;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.nncloudtv.model.NnUser;
import com.nncloudtv.service.NnStatusCode;
import com.nncloudtv.service.NnUserManager;

public class NnUserValidator {

	protected static final Logger log = Logger.getLogger(NnUserValidator.class.getName());	
	
	public static int validate(String email, String password, String name, HttpServletRequest req) {
		if (!BasicValidator.validateRequired(new String[] {email, password, name} ))
			return NnStatusCode.INPUT_MISSING;		
		if (!BasicValidator.validateEmail(email))
			return NnStatusCode.INPUT_BAD;
		
		//verify user
		NnUser user = new NnUserManager().findByEmail(email, req);
		if (user != null) {
			log.info("user email taken:" + user.getEmail() + ";user token=" + user.getToken());
			return NnStatusCode.USER_EMAIL_TAKEN;
		}		
		return NnStatusCode.SUCCESS;
	}
}
