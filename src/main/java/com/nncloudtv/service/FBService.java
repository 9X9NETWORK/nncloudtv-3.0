package com.nncloudtv.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.ui.Model;

import com.nncloudtv.model.Mso;

public class FBService {

	public Model setBrandMetadata(Model model, String msoName) {
		String now = (new SimpleDateFormat("MM.dd.yyyy")).format(new Date()).toString();
		String fbImg = "http://9x9ui.s3.amazonaws.com/9x9playerV39/images/9x9-facebook-icon.png";
		String fbName = "My 9x9 Channel Guide " + now;
		String fbDescription = "My 9x9 Channel Guide. Easily browse your favorite video podcasts on the 9x9 Player! Podcasts automatically download and update for you, bringing up to 81 channels of new videos daily.";
		
		if (msoName.equals(Mso.NAME_5F)) {
			fbName = "五樓電視－你的鄉民影視情報網 " + now;
			fbDescription = "五樓電視嚴選批踢踢（PTT）熱門主題，不定時提供鄉民最愛的網路影視內容。使用9x9 SmartGuide將全世界的podcast與youtube頻道匯集訂閱於此，便可隨時follow所有您喜愛頻道的最新內容。訂閱、收看、分享您的SmartGuide，人人都是風格獨具、最專業的五樓電視台。";
			fbImg = "http://s3.amazonaws.com/9x9ui/images/fbLogoC.jpg";			
		}
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
