package com.nncloudtv.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.ui.Model;

public class FBService {

	public Model setBrandMetadata(Model model, String msoName) {
		String now = (new SimpleDateFormat("MM.dd.yyyy")).format(new Date()).toString();
		String fbImg = "http://9x9ui.s3.amazonaws.com/9x9playerV39/images/9x9-facebook-icon.png";
		String fbName = "My 9x9 Channel Guide " + now;
		String fbDescription = "My 9x9 Channel Guide. Easily browse your favorite video podcasts on the 9x9 Player! Podcasts automatically download and update for you, bringing up to 81 channels of new videos daily.";
		
		model.addAttribute("fbName", fbName);
		model.addAttribute("fbDescription", fbDescription);
		model.addAttribute("fbImg", fbImg);
		return model;
	}
	
	/**
	 * You might to use setBrandMetadata before you come here.  
	 */
	public Model setEpisodeMetadata(Model model, String name, String intro, String imageUrl) {
		if (name != null && name.length() > 0)
			model.addAttribute("fbName", name);
		if (intro != null && intro.length() > 0)
			model.addAttribute("fbDescription", intro);
		if (imageUrl != null && imageUrl.length() > 0)
			model.addAttribute("fbImg", imageUrl);
		return model;
	}
}
