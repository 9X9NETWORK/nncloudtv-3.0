package com.nncloudtv.validation;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.service.NnStatusCode;
import com.nncloudtv.service.NnUserManager;

public class NnUserValidator {

	protected static final Logger log = Logger.getLogger(NnUserValidator.class.getName());	
	
	public static int validate(String email, String password, String name, Mso mso) {
		//verify input		
		if (email == null || password == null || name == null ||
			email.length() == 0 || password.length() == 0 || name.length() == 0) {			
			return NnStatusCode.INPUT_MISSING;
		}
		
		String regex = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
		if (!Pattern.matches(regex, email.trim().toLowerCase()) || password.length() < 6) {
			return NnStatusCode.INPUT_ERROR;
		}
		//verify user
		NnUser user = new NnUserManager().findByEmailAndMso(email, mso);
		if (user != null) {
			log.info("user email taken:" + user.getEmail() + "; mso=" + mso.getName() + ";user token=" + user.getToken());
			return NnStatusCode.USER_EMAIL_TAKEN;
		}		
		return NnStatusCode.SUCCESS;
	}
}
