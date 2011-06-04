package com.nncloudtv.web;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.model.Ipg;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.service.FBService;
import com.nncloudtv.service.IpgManager;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnProgramManager;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.service.PlayerService;

@Controller
@RequestMapping("share")
public class ShareController {

	protected static final Logger log = Logger.getLogger(ShareController.class.getName());
	
	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		NnLogUtil.logException(e);
		return "error/exception";				
	}
	
	@RequestMapping("{ipgId}")
	public String zooatomics(@PathVariable String ipgId, HttpServletResponse resp, Model model) {
		log.info("/share/" + ipgId);
		//invalid ipgid
		IpgManager ipgMngr = new IpgManager();
		if (!Pattern.matches("^\\d*$", ipgId)) {
			log.info("invalid ipg id");
			return "redirect:/";
		}				
		Ipg ipg = ipgMngr.findById(Long.parseLong(ipgId));
		if (ipg == null) {
			log.info("can not find ipg:" + ipgId);
			return "redirect:/";
		}
		
		PlayerService playerService = new PlayerService();		
		String msoName = null;
		//find mso info of the user who shares the ipg
		if (ipg.getUserId() != 0) { //old data does not have userId
			NnUser user = new NnUserManager().findById(ipg.getUserId());		
			if (user != null) {
				log.info("This user," + user.getId() + ", shares ipg.");
				Mso mso = new MsoManager().findById(user.getMsoId());
				if (mso != null) msoName = mso.getName(); //!!!!  and continue, ....
			}
		}
		model = playerService.prepareBrand(model, msoName, resp);

		//fb: change to episode mode
		FBService fbService = new FBService();
		NnProgramManager programMngr = new NnProgramManager();
		NnProgram p = programMngr.findById(ipg.getProgramId()); 		
		if (p != null) {
			model = fbService.setEpisodeMetadata(model, p.getName(), p.getIntro(), p.getImageUrl());
		}
		return "player/zooatomics";
	}
}
