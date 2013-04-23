package com.nncloudtv.web.admin;

import java.io.IOException;
import java.security.Principal;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nncloudtv.service.MsoConfigManager;

@Controller
@RequestMapping("admin")
public class AdminIndexController {
     
     protected static final Logger log = Logger.getLogger(AdminIndexController.class.getName());          

     @RequestMapping("index")
     public String index() {
          return "admin/index";
     }
     
     @RequestMapping(value="welcome", method = RequestMethod.GET)
     public String printWelcome(ModelMap model, Principal principal ) { 
          String name = principal.getName();
          model.addAttribute("username", name);
          model.addAttribute("message", "Spring Security Custom Form example");
          return "hello"; 
     }

     @RequestMapping(value="login", method = RequestMethod.GET)
     public String login(ModelMap model) {          
          model.addAttribute("root", MsoConfigManager.getExternalRootPath());          
          log.info("root path:" + MsoConfigManager.getExternalRootPath());
          return "admin/login";
 
     }
 
     @RequestMapping(value="loginfailed", method = RequestMethod.GET)
     public String loginerror(ModelMap model) {
          model.addAttribute("root", MsoConfigManager.getExternalRootPath());          
          model.addAttribute("error", "true");
          return "admin/login";
 
     }
 
     @RequestMapping(value="logout", method = RequestMethod.GET)
     public String logout(ModelMap model) {
          model.addAttribute("root", MsoConfigManager.getExternalRootPath());          
          return "admin/login";
 
     }     
     
     @RequestMapping("/index/ui")
     public String oss(Model model, 
                          HttpServletRequest request, 
                         HttpServletResponse response,
                         Principal principal)
               throws IOException {          
          //String name = principal.getName();
          System.out.println("root path:" + MsoConfigManager.getExternalRootPath());
          //model.addAttribute("username", name);
          model.addAttribute("root", MsoConfigManager.getExternalRootPath());          
          response.setContentType("text/html");
          return "admin/ui";                    

          /*
          PrintWriter writer = response.getWriter();
          String thisURL = request.getRequestURI();
          UserService userService = UserServiceFactory.getUserService();
          User user = userService.getCurrentUser();
          model.addAttribute("admin", user.getNickname());
          model.addAttribute("logoutURL", userService.createLogoutURL(thisURL));
          logger.info(user.getEmail() + " landing");          
          if (userService.isUserLoggedIn()) {
               if (userService.isUserAdmin()) {
                    User user = userService.getCurrentUser();
                    model.addAttribute("admin", user.getNickname());
                    model.addAttribute("logoutURL", userService.createLogoutURL(thisURL));
                    logger.info(user.getEmail() + " landing");
                    return "admin/ui";
               } else {
                    writer.println("This Google Account is not an Administrator, please <a href=\"" + userService.createLoginURL(thisURL) + "\">re-login</a> again.");
                    return null;
               }
          } else {
               writer.println("You are not logged in, please use Google Account to <a href=\"" + userService.createLoginURL(thisURL) + "\">login</a> as a Administrator.");
               return null;
          }
          */

     }
     
}
