package com.nncloudtv.service;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnSet;

public class PlayerService {
	
	protected static final Logger logger = Logger.getLogger(PlayerService.class.getName());
	
	public Model prepareBrand(Model model, String msoName, HttpServletResponse resp) {
		if (msoName != null) {
			msoName = msoName.toLowerCase();
		} else {
			msoName = Mso.NAME_9X9;
		}
		
		FBService fbService = new FBService();
		model = fbService.setBrandMetadata(model, msoName);
		if (msoName.equals(Mso.NAME_5F)) {
			model.addAttribute("brandInfo", "5f");
			CookieHelper.setCookie(resp, CookieHelper.MSO, Mso.NAME_5F);
		} else {
			model.addAttribute("brandInfo", "9x9");
			CookieHelper.deleteCookie(resp, CookieHelper.MSO); //delete brand cookie
		}
		return model;		
	}

	public Model prepareSetInfo(Model model, String name,
			HttpServletResponse resp) {		
		NnSetManager setMngr = new NnSetManager();
		NnSet channelSet = setMngr.findBybeautifulUrl(name);
		if (channelSet != null) {
			logger.info("found set name = " + name);
			model.addAttribute("fbName", NnStringUtil.htmlSafeChars(channelSet.getName()));
			model.addAttribute("fbDescription", NnStringUtil.htmlSafeChars(channelSet.getIntro()));
			model.addAttribute("fbImg", NnStringUtil.htmlSafeChars(channelSet.getImageUrl()));
		}
		
		return model;
	}
	
	public Model prepareEpisode(Model model, String pid,
			HttpServletResponse resp) {
		
		if (pid.matches("[0-9]+")) {
			NnProgramManager programMngr = new NnProgramManager();
			NnProgram program = programMngr.findById(Long.valueOf(pid));
			if (program != null) {
				logger.info("episode found = " + pid);
				model.addAttribute("fbName", NnStringUtil.htmlSafeChars(program.getName()));
				model.addAttribute("fbDescription", NnStringUtil.htmlSafeChars(program.getIntro()));
				model.addAttribute("fbImg", NnStringUtil.htmlSafeChars(program.getImageUrl()));
			}
		} else {
			/*
			Map<String, String> entry = YouTubeLib.getYouTubeVideoEntry(pid);
			model.addAttribute("fbName", NnStringUtil.htmlSafeChars(entry.get("title")));
			model.addAttribute("fbDescription", NnStringUtil.htmlSafeChars(entry.get("description")));
			model.addAttribute("fbImg", NnStringUtil.htmlSafeChars(entry.get("thumbnail")));
			*/
		}
		return model;
	}

	public Model prepareChannel(Model model, String cid,
			HttpServletResponse resp) {
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel channel = channelMngr.findById(Long.valueOf(cid));
		if (channel != null) {
			logger.info("found channel = " + cid);
			model.addAttribute("fbName", NnStringUtil.htmlSafeChars(channel.getName()));
			model.addAttribute("fbDescription", NnStringUtil.htmlSafeChars(channel.getIntro()));
			model.addAttribute("fbImg", NnStringUtil.htmlSafeChars(channel.getImageUrl()));
		}
		return model;
	}
	
}
