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
import com.nncloudtv.lib.NnNetUtil;
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

    //http://www.9x9.tv/flview?ch=572&ep=pVf0dc15igo&fb_action_ids=10151150850017515%2C10151148373067515%2C10151148049832515%2C10151148017797515%2C10151140220097515&fb_action_types=og.likes&fb_source=other_multiline&action_object_map=%7B%2210151150850017515%22%3A369099836508025%2C%2210151148373067515%22%3A518842601477880%2C%2210151148049832515%22%3A486192988067672%2C%2210151148017797515%22%3A374063942680087%2C%2210151140220097515%22%3A209326199200849%2C%2210151140216042515%22%3A361904300567180%7D
    //to flipr://www.9x9.tv/view?ch=572&ep=pVf0dc15igo                    
    public String getRedirectIosUrl(String cid, String pid, String mso, HttpServletRequest req) {
        String root = NnNetUtil.getUrlRoot(req);
        root = root.replace("http://", "");
        String urlScheme = "flipr";        
        if (mso != null && !mso.equals(Mso.NAME_9X9))
        	urlScheme += "-" + mso;
        String iosStr = urlScheme + "://" + root;
        iosStr += cid != null ? "/view?ch=" + cid : "";
        if (cid != null)
            iosStr += pid != null ? "&ep=" + pid : "";        	
        log.info("ios redirect url:" + iosStr);
        return iosStr;
    }

    public String getGAReportUrl(String ch, String ep) {
    	String reportUrl = "/promotion";
    	if (ch != null) {
    		reportUrl += "/ch" + ch;
    	}
    	if (ep != null) {
    		if (ep.matches("e[0-9]+")) {
    			reportUrl += "/" + ep;
    		} else {
    			reportUrl += "/yt" + ep;
    		}
    	} 
    	return reportUrl;
    }
    
    public String getRedirectAndroidUrl(String cid, String pid, String mso, HttpServletRequest req) {
        //String root = NnNetUtil.getUrlRoot(req);
        String url = "redirect/";
        if (mso != null && !mso.equals(Mso.NAME_9X9))
        	url += mso;
        if (mso == null)
        	url += Mso.NAME_9X9;
        url += cid != null ? "/view?ch=" + cid : "";
        if (cid != null)
            url += pid != null ? "&ep=" + pid : "";   
        log.info("android redirect url:" + url);
        return url;
    }
    
    public boolean isIos(HttpServletRequest req) {
        String userAgent = req.getHeader("user-agent");
        log.info("user agent:" + userAgent);
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            log.info("request from ios");
            return true;            
        }        
        return false;
    }

    public boolean isAndroid(HttpServletRequest req) {
        String userAgent = req.getHeader("user-agent");
        log.info("user agent:" + userAgent);
        if (userAgent.contains("Android")) {
            log.info("request from Android");
            return true;            
        }        
        return false;
    }
        
    //it is likely for old ios app who doesn't know about episdoe
    public String findFirstSubepisodeId(String eId) {
        if (eId != null && eId.matches("e[0-9]+")) {
            String eid = eId.replace("e", "");
            NnEpisodeManager episodeMngr = new NnEpisodeManager();
            NnEpisode episodeObj = episodeMngr.findById(Long.valueOf(eid));
            if (episodeObj != null) {
                List<NnProgram> programs = new NnProgramManager().findByEpisodeId(episodeObj.getId());
                if (programs.size() > 0)
                    eId = String.valueOf(programs.get(0).getId());
            }
        }
        log.info("first subepisode id:" + eId);
        return eId;        
    }
    
    private String prepareFb(String text, int type) {
        //0 = name, 1 = description, 2 = image
        if (type == 1) {
            return PlayerService.revertHtml(text);
        }
        if (type == 2) {
            if (text == null || text.length() == 0) {
                return PlayerService.revertHtml(" ");
            }
            return PlayerService.revertHtml(text);
        }
        if (type == 3) {
            return NnStringUtil.htmlSafeChars(text);
        }
        return PlayerService.revertHtml(text); 
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
                model.addAttribute("crawlEpisodeTitle", program.getName());
                model.addAttribute("crawlEpThumb1", program.getImageUrl());
                model.addAttribute("fbName", this.prepareFb(program.getName(), 0));
                model.addAttribute("fbDescription", this.prepareFb(program.getIntro(), 1));
                model.addAttribute("fbImg", this.prepareFb(program.getImageUrl(), 2));
                model.addAttribute("fbUrl", this.prepareFb(NnStringUtil.getProgramPlaybackUrl("" + program.getChannelId(), pid), 3));
            }
        } else if (pid.matches("e[0-9]+")){
            String eid = pid.replace("e", "");
            NnEpisodeManager episodeMngr = new NnEpisodeManager();
            NnEpisode episode = episodeMngr.findById(Long.valueOf(eid));
            if (episode != null) {
                log.info("nnepisode found = " + eid);
                model.addAttribute("crawlEpisodeTitle", episode.getName());
                model.addAttribute("crawlEpThumb1", episode.getImageUrl());
                model.addAttribute("fbName", this.prepareFb(episode.getName(), 0));
                model.addAttribute("fbDescription", this.prepareFb(episode.getIntro(), 1));
                model.addAttribute("fbImg", this.prepareFb(episode.getImageUrl(), 2));
                model.addAttribute("fbUrl", this.prepareFb(NnStringUtil.getEpisodePlaybackUrl(episode.getChannelId(), episode.getId()), 3));
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
            model.addAttribute("crawlChannelTitle", channel.getName());
            model.addAttribute("crawlVideoThumb", channel.getOneImageUrl());
            model.addAttribute("fbName", this.prepareFb(channel.getName(), 0));
            model.addAttribute("fbDescription", this.prepareFb(channel.getIntro(), 1));
            model.addAttribute("fbImg", this.prepareFb(channel.getOneImageUrl(), 2));
            model.addAttribute("fbUrl", this.prepareFb(NnStringUtil.getEpisodePlaybackUrl(channel.getId(), null), 3));
        }
        return model;
    }

    public Model preparePlayer(Model model, String js, String jsp, HttpServletRequest req) {
        model.addAttribute("js", "");
        if (js != null && js.length() > 0) {
            model.addAttribute("js", js);
        }
        if (jsp != null && jsp.length() > 0) {
            log.info("alternate is enabled: " + jsp);
        }
        model.addAttribute("locale", NnUserManager.findLocaleByHttpRequest(req));
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
    
    // http://player.9x9.tv/tv#/promotion/{ch}/{ep}
    public String getPlayerPromotionUrl(HttpServletRequest req, Mso mso, String ch, String ep) {
        
        String root = null;
        
        if (MsoManager.isNNMso(mso)) {
            root = NnNetUtil.getUrlRoot(req).replaceFirst("^http(s)?:\\/\\/", "");
        } else {
            root = mso.getName() + "." + NnNetUtil.getUrlRoot(req).replaceFirst("^http(s)?:\\/\\/(www\\.)?", "");
        }
        
        String url = "http://" + root + "/tv#/promotion/" + ch;
        if (ep != null) {
            url += "/" + ep;
        }
        
        return url;
    }
    
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
                model.addAttribute("fbName", this.prepareFb(c.getName(), 0));
                model.addAttribute("fbDescription", this.prepareFb(c.getIntro(), 1));                
                model.addAttribute("fbImg", this.prepareFb(c.getOneImageUrl(), 2));  

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
                               model.addAttribute("fbName", this.prepareFb(e.getName(), 0));   
                               model.addAttribute("fbDescription", this.prepareFb(e.getIntro(), 1));
                               model.addAttribute("fbImg", this.prepareFb(e.getImageUrl(), 2));
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
                                   model.addAttribute("fbName", this.prepareFb(p.getName(), 0));
                                   model.addAttribute("fbDescription", this.prepareFb(p.getIntro(), 1));
                                   model.addAttribute("fbImg", this.prepareFb(p.getImageUrl(), 2));
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
                        model.addAttribute("fbName", this.prepareFb((String)model.asMap().get("crawlEpisodeTitle"), 0));
                        model.addAttribute("fbImg", this.prepareFb((String)model.asMap().get("crawlVideoThumb"), 2));
                    }
                }
                /*
                String fbDescription = (String) model.asMap().get("fbDescription");
                if (fbDescription == null || fbDescription.length() == 0) {
                    model.addAttribute("fbDescription", " ");
                }
                */
            }
        }
        
        return model;
    }
    
    public static String revertHtml(String str) {
        if (str == null) return null;
        return str.replace("&gt;", ">")
                  .replace("&lt;", "<")
                  .replace("&amp;", "&");
    }
    
}
