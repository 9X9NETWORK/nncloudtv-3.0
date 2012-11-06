package com.nncloudtv.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import com.nncloudtv.lib.CookieHelper;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;

public class PlayerService {
    
    protected static final Logger log = Logger.getLogger(PlayerService.class.getName());
    
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
    
    public Model prepareEpisode(Model model, String pid,
            HttpServletResponse resp) {
        if (pid == null)
            return model;
        if (pid.matches("[0-9]+")) {
            NnProgramManager programMngr = new NnProgramManager();
            NnProgram program = programMngr.findById(Long.valueOf(pid));
            if (program != null) {
                log.info("nnprogram found = " + pid);
                model.addAttribute("fbName", NnStringUtil.htmlSafeChars(program.getName()));
                model.addAttribute("fbDescription", NnStringUtil.htmlSafeChars(program.getIntro()));
                model.addAttribute("fbImg", NnStringUtil.htmlSafeChars(program.getImageUrl()));
            }
        } else if (pid.matches("e[0-9]+")){
            String eid = pid.replace("e", "");
            NnEpisodeManager episodeMngr = new NnEpisodeManager();
            NnEpisode episode = episodeMngr.findById(Long.valueOf(eid));
            if (episode != null) {
                log.info("nnepisode found = " + eid);
                model.addAttribute("fbName", NnStringUtil.htmlSafeChars(episode.getName()));
                model.addAttribute("fbDescription", NnStringUtil.htmlSafeChars(episode.getIntro()));
                model.addAttribute("fbImg", NnStringUtil.htmlSafeChars(episode.getImageUrl()));
            }
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
        if (cid == null || !cid.matches("[0-9]+")) {
            return model;
        }
        NnChannel channel = channelMngr.findById(Long.valueOf(cid));
        if (channel != null) {
            log.info("found channel = " + cid);
            model.addAttribute("fbName", NnStringUtil.htmlSafeChars(channel.getName()));
            model.addAttribute("fbDescription", NnStringUtil.htmlSafeChars(channel.getIntro()));
            model.addAttribute("fbImg", NnStringUtil.htmlSafeChars(channel.getOneImageUrl()));
        }
        return model;
    }

    public Model preparePlayer(Model model, String js, String jsp) {
        model.addAttribute("js", "");
        if (js != null && js.length() > 0) {
            model.addAttribute("js", js);
        }
        if (jsp != null && jsp.length() > 0) {
            log.info("alternate is enabled: " + jsp);
        }
        return model;
    }

    //get all the query string(things after ?) from url except ch/channel, ep/episode
    public String rewrite(HttpServletRequest req) {
        String url = req.getRequestURL().toString();        
        String queryStr = req.getQueryString();        
        if (queryStr != null && !queryStr.equals("null"))
            queryStr = "?" + queryStr;
        else 
            queryStr = "";
        url = url + queryStr;
        Pattern pattern = Pattern.compile("(.*)\\?(.*)");
        Matcher m = pattern.matcher(url);
        if (m.find()) {
            String matched = m.group(2);
            matched = matched.replaceAll("ch=[^&]*&?",  "");
            log.info("matched 1:" + matched);
            matched = matched.replaceAll("ep=[^&]*&?", "");
            log.info("matched 2:" + matched);
            matched = matched.replaceAll("channel=[^&]*&?", "");
            log.info("matched 3:" + matched);
            matched = matched.replaceAll("episode=[^&]*&?", "");
            log.info("matched 4:" + matched);
            matched = matched.replaceAll("fb_action=[^&]*&?", "");
            log.info("matched 5:" + matched);            
            if (matched.length() > 0)
                return "?" + matched;
        }
        return "";
    }
    
    public String getQueryString(HttpServletRequest req, String channel, String episode, String ch, String ep) {
        String queryStr = this.rewrite(req);
        System.out.println("query str:" + queryStr);
        String cid = channel;
        if (ch != null)
            cid = ch;
        String pid = episode;
        if (ep != null)
            pid = ep;
        String epStr = "";
        if (pid != null) {
            Pattern pattern = Pattern.compile("[\\d]*");
            Matcher matcher = pattern.matcher(cid);
            if (matcher.matches()) {
                NnChannel c = new NnChannelManager().findById(Long.parseLong(cid));
                if (c != null) {
                    if (c.getContentType() == NnChannel.CONTENTTYPE_MIXED) {
                        matcher = pattern.matcher(pid);
                        if (matcher.matches()) {
                            NnProgram p = new NnProgramManager().findById(Long.parseLong(pid));
                            if (p != null) {
                                log.info("before pid:" + pid + ";after pid:" + p.getEpisodeId());
                                pid = String.valueOf("e" + p.getEpisodeId());                                
                            }
                        }
                    }
                }
            }            
            epStr = "!ep=" + pid;
        }
        log.info("rewritten url to : " + queryStr + "#!ch=" + cid + epStr);
        return queryStr + "#!ch=" + cid + epStr;
    }
    
    /*
    public String rewrite(String js, String jsp) {
        String url = "";
        if (jsp != null)
            url += "?jsp=" + jsp;
        if (js != null) {
            if (jsp != null)
                url += "&js=" + js;
            else
                url += "?js=" + js;
        }
        return url;
    }
    */
        
    public Model prepareCrawled(Model model, String escaped) {
        try {
            escaped = URLDecoder.decode(escaped, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("escaped=" + escaped);        
        
        //-- determine channel and episode and set --
        String ch=null, ep=null, youtubeEp=null, landing=null;
        boolean episodeShare = false;
        Pattern pattern = Pattern.compile("(ch=)(\\d+)");
        Matcher m = pattern.matcher(escaped);
        if (m.find()) {            
            ch = m.group(2);
        }
        pattern = Pattern.compile("(ep=)(\\d+)");
        m = pattern.matcher(escaped);
        if (m.find()) {            
            ep = m.group(2);
            episodeShare = true;
        }
        pattern = Pattern.compile("(ep=)(\\w+)");
        m = pattern.matcher(escaped);
        if (m.find()) {            
            youtubeEp = m.group(2);
            episodeShare = true;
        }
        pattern = Pattern.compile("(ep=)(e\\d+)");
        m = pattern.matcher(escaped);
        if (m.find()) {            
            ep = m.group(2);
            episodeShare = true;
            youtubeEp = null;
        }        
        if (ch == null) {
            pattern = Pattern.compile("^\\d+$");
            m = pattern.matcher(escaped);
            if (m.find()) {
                ch = m.group(0);
            }
        }
        pattern = Pattern.compile("(landing=)(\\w+)");
        m = pattern.matcher(escaped);
        if (m.find()) {            
            landing = m.group(2);
        }
        log.info("ch:" + ch + ";ep:" + ep + ";youtubeEp:" + youtubeEp + ";set:" + landing);        
        
        //-- channel/episode info --
        if (ch != null) {
            NnChannelManager channelMngr = new NnChannelManager();        
            NnChannel c = channelMngr.findById(Long.parseLong(ch));
            if (c != null) {
                model.addAttribute("crawlChannelTitle", c.getName());
                //in case not enough episode data, use channel for default  
                model.addAttribute("crawlEpisodeTitle", c.getName());
                model.addAttribute("crawlVideoThumb", c.getOneImageUrl());
                model.addAttribute("crawlEpThumb1", c.getOneImageUrl());                
                model.addAttribute("fbName", NnStringUtil.htmlSafeChars(c.getName()));
                model.addAttribute("fbDescription", NnStringUtil.htmlSafeChars(c.getIntro()));
                model.addAttribute("fbImg", NnStringUtil.htmlSafeChars(c.getOneImageUrl()));  

                if (ep != null && ep.startsWith("e")) {
                    ep = ep.replaceFirst("e", "");
                    NnEpisodeManager episodeMngr = new NnEpisodeManager(); 
                    List<NnEpisode> episodes = episodeMngr.findPlayerEpisodes(c.getId());
                    int i = 1;                    
                    for (NnEpisode e : episodes) {
                        if (i > 1 && i < 4) {
                            model.addAttribute("crawlEpThumb" + i, e.getImageUrl());
                            System.out.println("crawlEpThumb" + i + ":" + e.getImageUrl());
                            i++;
                        }
                        if (e.getId() == Long.parseLong(ep)) {
                            model.addAttribute("crawlVideoThumb", e.getImageUrl());
                            model.addAttribute("crawlEpisodeTitle", e.getName());
                            model.addAttribute("crawlEpThumb" + i, e.getImageUrl());
                            if (episodeShare) {
                               model.addAttribute("fbName", NnStringUtil.htmlSafeChars(e.getName()));   
                               model.addAttribute("fbDescription", NnStringUtil.htmlSafeChars(e.getIntro()));
                               model.addAttribute("fbImg", NnStringUtil.htmlSafeChars(e.getImageUrl()));
                            }
                            i++;
                        }
                        if (i == 4) {
                            break;
                        }
                    }            
                } else {
                    NnProgramManager programMngr = new NnProgramManager();
                    List<NnProgram> programs = programMngr.findPlayerProgramsByChannel(c.getId());
                    if (programs.size() > 0) {
                        int i=1;                    
                        if (ep == null)
                            ep = String.valueOf(programs.get(0).getId());
                        for (NnProgram p : programs) {
                            if (i > 1 && i < 4) {
                                model.addAttribute("crawlEpThumb" + i, p.getImageUrl());
                                System.out.println("crawlEpThumb" + i + ":" + p.getImageUrl());
                                i++;
                            }
                            if (p.getId() == Long.parseLong(ep)) {
                                model.addAttribute("crawlVideoThumb", p.getImageUrl());
                                model.addAttribute("crawlEpisodeTitle", p.getName());
                                model.addAttribute("crawlEpThumb" + i, p.getImageUrl());
                                if (episodeShare) {
                                   model.addAttribute("fbName", NnStringUtil.htmlSafeChars(p.getName()));
                                   model.addAttribute("fbDescription", NnStringUtil.htmlSafeChars(p.getIntro()));
                                   model.addAttribute("fbImg", NnStringUtil.htmlSafeChars(p.getImageUrl()));
                                }
                                i++;
                            }
                            if (i == 4) {
                                break;
                            }
                        }
                    } else {                    
                        if (youtubeEp != null) {
                            Map<String, String> result = YouTubeLib.getYouTubeVideo(youtubeEp);
                            model.addAttribute("crawlEpisodeTitle", result.get("title"));
                            model.addAttribute("crawlVideoThumb", result.get("imageUrl"));
                            model.addAttribute("crawlEpThumb1", result.get("imageUrl"));
                            model.addAttribute("crawlEpThumb2", result.get("imageUrl"));
                            model.addAttribute("crawlEpThumb3", result.get("imageUrl"));
                        }
                    }
                    if (episodeShare) {
                        model.addAttribute("fbName", NnStringUtil.htmlSafeChars((String)model.asMap().get("crawlEpisodeTitle")));
                        model.addAttribute("fbImg", NnStringUtil.htmlSafeChars((String)model.asMap().get("crawlVideoThumb")));
                    }
                }
            }
        }
        
        return model;
    }
}
