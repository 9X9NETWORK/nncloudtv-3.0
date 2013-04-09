package com.nncloudtv.validation;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserProfile;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.web.api.NnStatusCode;

public class NnUserValidator {

    protected static final Logger log = Logger.getLogger(NnUserValidator.class.getName());    
    
    public static int validate(String email, String password, String name, HttpServletRequest req) {
        if (!BasicValidator.validateRequired(new String[] {email, password, name} ))
            return NnStatusCode.INPUT_MISSING;        
        if (!BasicValidator.validateEmail(email))
            return NnStatusCode.INPUT_BAD;
        
        //verify user
        NnUser user = new NnUserManager().findByEmail(email, 1, req);
        if (user != null) {
            log.info("user email taken:" + user.getEmail() + ";user token=" + user.getToken());
            return NnStatusCode.USER_EMAIL_TAKEN;
        }
        log.info("profile success");
        return NnStatusCode.SUCCESS;
    }

    public static int validateInput(String email, String password, String name, HttpServletRequest req) {
        if (!BasicValidator.validateRequired(new String[] {email, password, name} ))
            return NnStatusCode.INPUT_MISSING;        
        if (!BasicValidator.validateEmail(email))
            return NnStatusCode.INPUT_BAD;
        
        return NnStatusCode.SUCCESS;
    }
    
    public static int validateProfile(NnUser user) {
        NnUserProfile profile = user.getProfile();      
        System.out.println("gender:" + profile.getGender());
        if (profile.getGender() > 2 || profile.getGender() < 0) {
            log.info("gender error:" + profile.getGender() + ";" + profile.getUserId());
            return NnStatusCode.INPUT_BAD;
        }
        String dob = profile.getDob();
        if (dob != null) {
            if (!Pattern.matches("[\\d\\\\/\\\\]*", dob)) {
                log.info("dob error:" + dob + ";" + user.getEmail());
                return NnStatusCode.INPUT_BAD;
            }
        }
        String sphere = profile.getSphere();
        if (sphere != null && sphere.length() > 0) {
            if (sphere.equals(LangTable.LANG_EN) && sphere.equals(LangTable.LANG_ZH)) {
                log.info("sphere error:" + sphere + ";" + user.getEmail());
                return NnStatusCode.INPUT_BAD;
            }
        }
        log.info("profile success");
        return NnStatusCode.SUCCESS;
    }

    public static int validatePassword(String password) {
        if (!BasicValidator.validateRequired(new String[] {password} ))
            return NnStatusCode.INPUT_MISSING;
        int limit = 6;
        if (password.length() < limit) {
            return NnStatusCode.INPUT_BAD;
        }
        return NnStatusCode.SUCCESS;
    }

}
