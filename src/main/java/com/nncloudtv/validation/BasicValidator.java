package com.nncloudtv.validation;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.nncloudtv.service.NnStatusCode;

public class BasicValidator {
	protected static final Logger log = Logger.getLogger(BasicValidator.class.getName());	
	
	public static int validateNumber(String input) {
		if (input != null && !Pattern.matches("^\\d*$", input)) {
			return NnStatusCode.INPUT_ERROR;
		}
		return NnStatusCode.SUCCESS;		
	}
	
}
