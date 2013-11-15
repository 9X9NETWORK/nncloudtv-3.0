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
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.web.api.ApiContext;

public class PlayerService {
    
    protected static final Logger log = Logger.getLogger(PlayerService.class.getName());
    
    public static final String META_NAME = "fbName";
    public static final String META_IMAGE = "fbImg";
    public static final String META_DESCRIPTION = "fbDescription";
    public static final String META_URL = "fbUrl";
    public static final String META_KEYWORD = "fbKeyword";
    public static final String META_TITLE = "fbTitle"; // <title/>
    public static final String META_BRANDINFO = "brandInfo";
    public static final String META_CHANNEL_TITLE = "crawlChannelTitle";
    public static final String META_EPISODE_TITLE = "crawlEpisodeTitle";
    public static final String META_VIDEO_THUMBNAIL = "crawlVideoThumb";
    public static final String META_FAVICON = "favicon";
    
    public Model prepareBrand(Model model, String msoName, HttpServletResponse resp) {
        
        if (msoName != null) {
            msoName = msoName.toLowerCase();
        } else {
            msoName = Mso.NAME_9X9;
        }
        
        // bind favicon
        MsoManager msoMngr = new MsoManager();
        Mso mso = msoMngr.findByName(msoName);
        MsoConfigManager msoConfigMngr = new MsoConfigManager();
        MsoConfig item = msoConfigMngr.findByMsoAndItem(mso, MsoConfig.FAVICON_URL);
        if (item != null && item.getValue() != null && item.getValue().isEmpty() == false) {
            model.addAttribute(META_FAVICON, "<link rel=\"icon\" href=\"" + item.getValue() + "\" type=\"image/x-icon\"/>" +
                "<link rel=\"shortcut icon\" href=\"" + item.getValue() + "\" type=\"image/x-icon\"/>");
        }
        
        // TODO: move to mso_config
        if (msoName.equals(Mso.NAME_CTS)) {
            
            model.addAttribute(META_BRANDINFO, Mso.NAME_CTS);
            model.addAttribute(META_TITLE, "微電影節 - 華視");
            model.addAttribute(META_DESCRIPTION, "「華視雲端電視網」讓您跨地區、跨時間、跨裝置地收看華視為您精選的節目，現正推出：微新運動(weiflim)元年第一屆臺灣微電影節選拔活動，主題以臺灣的社會創新，鼓勵臺灣人民與各行各業運用新科技、新方法、新思維、新管理方式，解決社會問題，創造價值與幸福的精彩故事");
            model.addAttribute(META_KEYWORD, "微電影節,微新運動,2013台灣微電影節-微視界‧大創新,華視 微電影節,臺灣微電影節,weifilm");
            model.addAttribute(META_IMAGE, "http://9x9ui.s3.amazonaws.com/tv4.0/img/cts-logo.png");
            
        } else {
            
            model.addAttribute(META_BRANDINFO, Mso.NAME_9X9);
            model.addAttribute(META_TITLE, "9x9.tv");
            model.addAttribute(META_DESCRIPTION, "&nbsp;");
            model.addAttribute(META_IMAGE, "http://9x9ui.s3.amazonaws.com/9x9playerV39/images/9x9-facebook-icon.png");
            CookieHelper.deleteCookie(resp, CookieHelper.MSO); //delete brand cookie
        }
        return model;        
    }

    //!!! many places in playercontroller, playerservice needs to be changed here
    public String getBrandName(String mso) {
    	String name = Mso.NAME_9X9;
    	if (mso != null && mso.equals(Mso.NAME_CTS))
    		name = Mso.NAME_CTS;
    	return name;
    }
    
    public String getTransitionPageFile(String brandName) {
        if (brandName != null && brandName.equals(Mso.NAME_CTS)) {
        	return "player/ios_cts";
        }
        return "player/ios";
    }
    
    //name has to be processed by getBrandName first    
    public Model getTransitionModel(Model model, String name, HttpServletRequest req) {
    	boolean isIos = this.isIos(req);    	
    	boolean isAndroid = this.isAndroid(req);
    	if (!isIos && !isAndroid)
    		return null;
    	String androidNnStoreUrl = "market://details?id=tv.tv9x9.player";
    	String androidCtsStoreUrl = "market://details?id=tw.com.cts.player";
    	String iosNnStoreUrl = "https://itunes.apple.com/app/9x9.tv/id443352510?mt=8";
    	String iosCtsStoreUrl = "https://itunes.apple.com/app/hua-shi-yun-duan-dian-shi-wang/id623085456?mt=8";    	
    	String storeUrl = androidNnStoreUrl;
    	String ch = "0";
    	String ep = "0";
    	//report url
    	String reportUrl = this.getGAReportUrl(ch, ep, name);
    	log.info("reportUrl:" + reportUrl); 
    	//flipr url
        String fliprStr = "flipr://";
        if (name.equals(Mso.NAME_CTS))
        	fliprStr = "flipr-cts://";
        //store url
        if (isIos) {
        	storeUrl = iosNnStoreUrl;
	    	if (name.equals(Mso.NAME_CTS)) {
	    		storeUrl = iosCtsStoreUrl;
	    	}
        }
        if (isAndroid) {
        	storeUrl = androidNnStoreUrl;
	    	if (name.equals(Mso.NAME_CTS)) {
	    		storeUrl = androidCtsStoreUrl;
	    	}
        }
    	model.addAttribute("name", name);
        model.addAttribute("fliprUrl", fliprStr);    	
    	model.addAttribute("reportUrl", reportUrl);
    	model.addAttribute("storeUrl", storeUrl);
    	return model;
    }
    //http://www.9x9.tv/flview?ch=572&ep=pVf0dc15igo&fb_action_ids=10151150850017515%2C10151148373067515%2C10151148049832515%2C10151148017797515%2C10151140220097515&fb_action_types=og.likes&fb_source=other_multiline&action_object_map=%7B%2210151150850017515%22%3A369099836508025%2C%2210151148373067515%22%3A518842601477880%2C%2210151148049832515%22%3A486192988067672%2C%2210151148017797515%22%3A374063942680087%2C%2210151140220097515%22%3A209326199200849%2C%2210151140216042515%22%3A361904300567180%7D
    //to flipr://www.9x9.tv/view?ch=572&ep=pVf0dc15igo                    
    public String getFliprUrl(String cid, String pid, String mso, HttpServletRequest req) {
        String root = NnNetUtil.getUrlRoot(req);
        root = root.replace("http://", "");
        String urlScheme = "flipr";        
        if (mso != null && !mso.equals(Mso.NAME_9X9))
        	urlScheme += "-" + mso;
        String iosStr = urlScheme + "://" + root;
        iosStr += cid != null ? "/view?ch=" + cid : "";
        if (cid != null)
            iosStr += pid != null ? "&ep=" + pid : "";        	
        log.info("flipr url:" + iosStr);
        return iosStr;
    }

    public String getGAReportUrl(String ch, String ep, String mso) {
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
    	String msoDomain = "www.9x9.tv";
    	if (mso != null && !mso.equals(Mso.NAME_9X9))
    		msoDomain = mso + ".9x9.tv"; 
    	reportUrl += "?mso=" + msoDomain;
    	return reportUrl;
    }
    
    public String getRedirectAndroidUrl(String cid, String pid, String mso, HttpServletRequest req) {
        //String root = NnNetUtil.getUrlRoot(req);
        String url = "redirect/";
        String msoName = "9x9";
        if (mso != null && !mso.equals(Mso.NAME_9X9))
        	msoName = mso;
        if (mso == null)
        	msoName = Mso.NAME_9X9;
        url += msoName;
        url += cid != null ? "/view?ch=" + cid : "";
        if (cid != null)
            url += pid != null ? "&ep=" + pid : "";   
        log.info("android redirect url:" + url);
        return url;
    }
    
    public boolean isIos(HttpServletRequest req) {
        String userAgent = req.getHeader(ApiContext.HEADER_USER_AGENT);
        log.info("user agent:" + userAgent);
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            log.info("request from ios");
            return true;            
        }        
        return false;
    }

    public boolean isAndroid(HttpServletRequest req) {
        String userAgent = req.getHeader(ApiContext.HEADER_USER_AGENT);
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
        	 if (text == null || text.length() == 0) {
        		 log.info("make fb description empty space with &nbsp;");
        		 return "&nbsp;";
        	 }
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
            String mso, HttpServletResponse resp) {
        if (pid == null)
            return model;
        if (pid.matches("[0-9]+")) {
            NnProgramManager programMngr = new NnProgramManager();
            NnProgram program = programMngr.findById(Long.valueOf(pid));
            if (program != null) {
                log.info("nnprogram found = " + pid);
                model.addAttribute(META_EPISODE_TITLE, program.getName());
                model.addAttribute("crawlEpThumb1", program.getImageUrl());
                model.addAttribute(META_NAME, this.prepareFb(program.getName(), 0));
                model.addAttribute(META_DESCRIPTION, this.prepareFb(program.getIntro(), 1));
                model.addAttribute(META_IMAGE, this.prepareFb(program.getImageUrl(), 2));
                model.addAttribute(META_URL, this.prepareFb(NnStringUtil.getProgramPlaybackUrl("" + program.getChannelId(), pid), 3));
            }
        } else if (pid.matches("e[0-9]+")){
            String eid = pid.replace("e", "");
            NnEpisodeManager episodeMngr = new NnEpisodeManager();
            NnEpisode episode = episodeMngr.findById(Long.valueOf(eid));
            if (episode != null) {
                log.info("nnepisode found = " + eid);
                model.addAttribute(META_EPISODE_TITLE, episode.getName());
                model.addAttribute("crawlEpThumb1", episode.getImageUrl());
                model.addAttribute(META_NAME, this.prepareFb(episode.getName(), 0));
                model.addAttribute(META_DESCRIPTION, this.prepareFb(episode.getIntro(), 1));
                model.addAttribute(META_IMAGE, this.prepareFb(episode.getImageUrl(), 2));
                model.addAttribute(META_URL, this.prepareFb(NnStringUtil.getSharingUrl(episode.getChannelId(), episode.getId(), mso), 3));
            }
            /*
            Map<String, String> entry = YouTubeLib.getYouTubeVideoEntry(pid);
            model.addAttribute(META_NAME, NnStringUtil.htmlSafeChars(entry.get("title")));
            model.addAttribute(META_DESCRIPTION, NnStringUtil.htmlSafeChars(entry.get("description")));
            model.addAttribute(META_IMAGE, NnStringUtil.htmlSafeChars(entry.get("thumbnail")));
            */
        }
        return model;
    }

    public Model prepareChannel(Model model, String cid,
            String mso, HttpServletResponse resp) {
        NnChannelManager channelMngr = new NnChannelManager();
        if (cid == null || !cid.matches("[0-9]+")) {
            return model;
        }
        NnChannel channel = channelMngr.findById(Long.valueOf(cid));
        if (channel != null) {
            log.info("found channel = " + cid);
            model.addAttribute(META_CHANNEL_TITLE, channel.getName());
            model.addAttribute(META_VIDEO_THUMBNAIL, channel.getOneImageUrl());
            model.addAttribute(META_NAME, this.prepareFb(channel.getName(), 0));
            model.addAttribute(META_DESCRIPTION, this.prepareFb(channel.getIntro(), 1));
            model.addAttribute(META_IMAGE, this.prepareFb(channel.getOneImageUrl(), 2));
            model.addAttribute(META_URL, this.prepareFb(NnStringUtil.getSharingUrl(channel.getId(), null, mso), 3));
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
                model.addAttribute(META_CHANNEL_TITLE, c.getName());
                //in case not enough episode data, use channel for default  
                model.addAttribute(META_EPISODE_TITLE, c.getName());
                model.addAttribute(META_VIDEO_THUMBNAIL, c.getOneImageUrl());
                model.addAttribute("crawlEpThumb1", c.getOneImageUrl());                
                model.addAttribute(META_NAME, this.prepareFb(c.getName(), 0));
                model.addAttribute(META_DESCRIPTION, this.prepareFb(c.getIntro(), 1));                
                model.addAttribute(META_IMAGE, this.prepareFb(c.getOneImageUrl(), 2));  

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
                            model.addAttribute(META_VIDEO_THUMBNAIL, e.getImageUrl());
                            model.addAttribute(META_EPISODE_TITLE, e.getName());
                            model.addAttribute("crawlEpThumb" + i, e.getImageUrl());
                            if (episodeShare) {
                               model.addAttribute(META_NAME, this.prepareFb(e.getName(), 0));   
                               model.addAttribute(META_DESCRIPTION, this.prepareFb(e.getIntro(), 1));
                               model.addAttribute(META_IMAGE, this.prepareFb(e.getImageUrl(), 2));
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
                                model.addAttribute(META_VIDEO_THUMBNAIL, p.getImageUrl());
                                model.addAttribute(META_EPISODE_TITLE, p.getName());
                                model.addAttribute("crawlEpThumb" + i, p.getImageUrl());
                                if (episodeShare) {
                                   model.addAttribute(META_NAME, this.prepareFb(p.getName(), 0));
                                   model.addAttribute(META_DESCRIPTION, this.prepareFb(p.getIntro(), 1));
                                   model.addAttribute(META_IMAGE, this.prepareFb(p.getImageUrl(), 2));
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
                            model.addAttribute(META_EPISODE_TITLE, result.get("title"));
                            model.addAttribute(META_VIDEO_THUMBNAIL, result.get("imageUrl"));
                            model.addAttribute("crawlEpThumb1", result.get("imageUrl"));
                            model.addAttribute("crawlEpThumb2", result.get("imageUrl"));
                            model.addAttribute("crawlEpThumb3", result.get("imageUrl"));
                        }
                    }
                    if (episodeShare) {
                        model.addAttribute(META_NAME, this.prepareFb((String)model.asMap().get(META_EPISODE_TITLE), 0));
                        model.addAttribute(META_IMAGE, this.prepareFb((String)model.asMap().get(META_VIDEO_THUMBNAIL), 2));
                    }
                }
                /*
                String fbDescription = (String) model.asMap().get(META_DESCRIPTION);
                if (fbDescription == null || fbDescription.length() == 0) {
                    model.addAttribute(META_DESCRIPTION, " ");
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
