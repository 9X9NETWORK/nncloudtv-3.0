package com.nncloudtv.web.admin;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.ContentOwnership;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.service.ContentOwnershipManager;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnSetManager;
import com.nncloudtv.service.NnUserManager;

@Controller
@RequestMapping("admin/mso")
public class AdminMsoController {

	protected static final Logger log = Logger.getLogger(AdminMsoController.class.getName());

	public final MsoManager  msoMngr;
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> exception(Exception e) {
		return NnNetUtil.textReturn("exception");
	}

	@Autowired
	public AdminMsoController(MsoManager msoMngr) {
		this.msoMngr     = msoMngr;		
	}	
	
	@RequestMapping("create")
	public @ResponseBody String create(@RequestParam(value="name")String name, @RequestParam(value="intro")String intro,
			                           @RequestParam(value="contactEmail")String contactEmail) {
		System.out.println("contact:" + contactEmail);
		Mso mso = new Mso(name, intro, contactEmail, Mso.TYPE_MSO);		
		msoMngr.save(mso);
		return "OK";
	}
	
	@RequestMapping(value = "create", params = {"name", "contactEmail", "password", "logoUrl", "type"})
	public @ResponseBody String create(@RequestParam String name,
	                                   @RequestParam String contactEmail,
	                                   @RequestParam String password,
	                                   @RequestParam String logoUrl,
	                                   @RequestParam Short  type,
	                                   HttpServletRequest req) {
		log.info("name = " + name);
		log.info("contactEmail = " + contactEmail);
		log.info("password = " + password);
		log.info("logoUrl = " + logoUrl);
		log.info("type = " + type);
		
		NnUserManager userMngr = new NnUserManager();
		MsoManager msoMngr = new MsoManager();
		NnSetManager setMngr = new NnSetManager();
		
		Mso found = msoMngr.findByName(name);
		if (found != null) {
			return "Name In Used";
		}
		short userType;
		if (type == Mso.TYPE_3X3) {
			userType = NnUser.TYPE_3X3;
		} else if (type == Mso.TYPE_ENTERPRISE) {
			userType = NnUser.TYPE_ENTERPRISE;
		} else {
			return "Only Type 3x3 Is Applicable";
		}
		
		Mso mso = new Mso(name, name, contactEmail, type);
		mso.setTitle(name);
		mso.setLang(LangTable.LANG_EN);
		mso.setLogoUrl(logoUrl);
		msoMngr.save(mso);
		
		NnUser user = new NnUser(contactEmail, password, name, userType, mso.getId());
		userMngr.create(user, req, NnUser.SHARD_DEFAULT);
		
		NnSet set = new NnSet(name, name, true);
		set.setBeautifulUrl(name);
		set.setPublic(false); // to prevent set to appear to directory
		setMngr.create(set, new ArrayList<NnChannel>());
		
		//channelSet ownership
		ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
		ownershipMngr.create(new ContentOwnership(), mso, set);
		
		return "OK";
	}
}
