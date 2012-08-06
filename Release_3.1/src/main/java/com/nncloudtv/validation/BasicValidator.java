package com.nncloudtv.validation;

import java.util.logging.Logger;
import java.util.regex.Pattern;

public class BasicValidator {
	protected static final Logger log = Logger.getLogger(BasicValidator.class.getName());	
	
	public static boolean validateEmail(String email) {
		String regex = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
		if (!Pattern.matches(regex, email.trim().toLowerCase())) {
			return false;
		}
		return true;
	}
	
    public static boolean validateRequired(String[] inputs) {
        for (int i=0; i<inputs.length; i++) {
            if (inputs[i] == null || inputs[i].length() < 0 || inputs[i].equals("undefined")) {
            	return false;
            }
        }
        return true;
    }
	
}
