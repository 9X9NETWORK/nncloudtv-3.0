package com.nncloudtv.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.model.Mso;

public class PlayerService {
	
	public Model prepareBrand(Model model, String msoName, HttpServletResponse resp) {
		if (msoName != null) {
			msoName = msoName.toLowerCase();
		} else {
			msoName = Mso.NAME_9X9;
		}
		
		FBService fbService = new FBService();
		model = fbService.setBrandMetadata(model, msoName);
		if (msoName.equals(Mso.NAME_5F)) {
			System.out.println("<<<<< i am 5f >>>>>>> ");
			model.addAttribute("brandInfo", "5f");
			CookieHelper.setCookie(resp, CookieHelper.MSO, Mso.NAME_5F);
		} else {
			model.addAttribute("brandInfo", "9x9");
			CookieHelper.deleteCookie(resp, CookieHelper.MSO); //delete brand cookie
		}
		return model;		
	}

}
