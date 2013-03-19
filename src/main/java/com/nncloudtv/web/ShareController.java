package com.nncloudtv.web;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nncloudtv.dao.NnUserSubscribeDao;
import com.nncloudtv.dao.UserInviteDao;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserSubscribe;
import com.nncloudtv.model.UserInvite;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnUserManager;
import com.nncloudtv.service.NnUserSubscribeManager;
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
    
    /**
     * original url: /share/1
     * rewrite to  : #!share=x
     */
    @RequestMapping("{ipgId}")
    public String zooatomics(@PathVariable String ipgId,
            @RequestParam(value="js",required=false) String js,
            @RequestParam(value="jsp",required=false) String jsp,            
            HttpServletRequest req, 
            HttpServletResponse resp, 
            Model model) {
        log.info("/share/" + ipgId);
        PlayerService service = new PlayerService();
        String url = service.rewrite(req); 
        return "redirect:/" + url + "#!share=" + ipgId;
        
        /*
        String msoName = null;
        //find mso info of the user who shares the ipg
        model = service.prepareBrand(model, msoName, resp);
        model = service.preparePlayer(model, js, jsp);
        if (jsp != null && jsp.length() > 0) {
            return "player/" + jsp;
        }
        return "player/zooatomics";
        */
    }
    
    //http://localhost:8080/share/invite/lalala
    @RequestMapping("invite/{token}")    
    public String invite(@PathVariable String token, Model model) {
        log.info("token:" + token);        
        UserInviteDao dao = new UserInviteDao();
        UserInvite invite = dao.findByToken(token);
        if (invite == null) {
            model = model.addAttribute("invite", "null");
        } else {
            NnUser user = new NnUserManager().findById(invite.getUserId(), 1, invite.getShard());
            model = model.addAttribute("userName", user.getProfile().getName());
            model = model.addAttribute("token", token);
        }
        return "flipr/invite";
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping("invite/response")
    public String inviteResponse(@RequestParam(value="token", required=false) String token, 
            @RequestParam String q1, 
            Model model,
            HttpServletRequest req) {
        log.info("token:" + token + ";" + "answer:" + q1);
        UserInviteDao dao = new UserInviteDao();
        UserInvite invite = dao.findByToken(token);
        if (invite == null) {
            return "redirect:/share/invite/" + token;
        } else {
            if (invite.getStatus() != UserInvite.STATUS_PENDING) {
                model.addAttribute("old", "y");                
                return "share/response";
            }
            if (q1.equals("y")) {
                invite.setStatus(UserInvite.STATUS_ACCEPTED);
                NnUserManager userMngr = new NnUserManager();
                NnUserSubscribeDao subDao = new NnUserSubscribeDao();
                NnUserSubscribeManager subMngr = new NnUserSubscribeManager();
                NnUser user = userMngr.findByEmail(invite.getInviteeEmail(), 1, req);
                NnChannelManager channelMngr = new NnChannelManager();
                NnChannel c = channelMngr.findById(invite.getChannelId());
                if (user == null) {
                    model = model.addAttribute("exist", "n");
                    Mso mso = new MsoManager().findNNMso();
                    user = new NnUser(invite.getInviteeEmail(), "123456", NnUser.TYPE_USER, mso.getId());
                    /*
                    user.setSphere("en");
                    user.setLang("en");
                    */        
                    user.setTemp(false);
                    userMngr.create(user, req, (short)0);
                    c.setSeq((short)1);
                    subMngr.subscribeChannel(user, c);
                } else {
                    List<NnUserSubscribe> subs = subDao.findAllByUser(user);
                    HashMap map = new HashMap();
                    for (NnUserSubscribe s : subs) {
                        map.put(s.getSeq(), s.getSeq());
                    }
                    for (short i=1; i<82; i++) {
                        if (map.get(i) == null) {
                            c.setSeq(i);
                            subMngr.subscribeChannel(user, c);
                            break;
                        }
                    }
                }
                invite.setInviteeId(user.getId());
            }
            
            if (q1.equals("n"))
                invite.setStatus(UserInvite.STATUS_REJECTED);
            dao.save(invite);
            log.info("invite status set:" + invite.getStatus());
        }
        model = model.addAttribute("resp", q1);
        return "flipr/response";
    }    
    
}
