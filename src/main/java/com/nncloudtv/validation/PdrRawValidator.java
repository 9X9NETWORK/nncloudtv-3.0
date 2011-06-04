package com.nncloudtv.validation;

import java.util.logging.Logger;

import com.nncloudtv.service.NnStatusCode;

public class PdrRawValidator {
	protected static final Logger log = Logger.getLogger(PdrRawValidator.class.getName());
	
	public static int validate(String userToken, String pdr) {
		if (userToken == null || userToken.length() == 0 || userToken.equals("undefined") || 
			pdr == null || pdr.length() == 0) {
			return NnStatusCode.INPUT_MISSING;
		}		
		return NnStatusCode.SUCCESS;
	}
}
