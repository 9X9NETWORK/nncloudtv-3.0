package com.nncloudtv.web;

import java.security.SignatureException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// CmsController is moved to nncms, CmsConnector is for redirection
@Controller
public class CmsConnector {
    
    protected static final Logger log = Logger.getLogger(CmsConnector.class.getName());
    
    @RequestMapping(value = "{msoName}/admin", method = RequestMethod.GET)
    public String admin(HttpServletRequest request, HttpServletResponse response, @PathVariable("msoName") String msoName, Model model) throws SignatureException {
        
        log.info("msoName = " + msoName);
        
        if (msoName.equals("cms")) {
            return "redirect:/cms/admin";
        } else {
            return "redirect:/cms/" + msoName + "/admin";
        }
    }
}
