package com.nncloudtv.service;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnUserManager;

@Service
public class AuthService {
	
	protected static final Logger logger = Logger.getLogger(AuthService.class.getName());
	
	public Mso msoAuthenticate(String email, String password, long msoId) {
		
		NnUserManager userMngr = new NnUserManager();
		MsoManager    msoMngr  = new MsoManager();
		
		NnUser user = userMngr.findAuthenticatedMsoUser(email, password, msoId);
		if (user == null)
			return null;
		return msoMngr.findById(user.getMsoId());
	}
	
}
