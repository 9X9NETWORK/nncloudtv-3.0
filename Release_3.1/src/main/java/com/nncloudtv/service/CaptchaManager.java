package com.nncloudtv.service;

import java.util.List;

import com.nncloudtv.dao.CaptchaDao;
import com.nncloudtv.model.Captcha;

public class CaptchaManager {
	
	private CaptchaDao captchaDao = new CaptchaDao();	

	public void saveAll(List<Captcha> list) {
		captchaDao.saveAll(list);		
	}
	
	public Captcha getRandom() {
		return captchaDao.getRandom();
	}
	
	public Captcha findById(long id) {
		return captchaDao.findById(id);
	}	
}
