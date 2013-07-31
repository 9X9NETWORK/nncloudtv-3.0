package com.nncloudtv.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.web.api.NnStatusCode;

@Service
public class IosService {
    
    protected static final Logger log = Logger.getLogger(IosService.class.getName());
    private static final String urlRoot = "http://s3.amazonaws.com/9x9ui/war/v2/ios/";
    
    public String listRecommended(String lang, long msoId) {
        if (msoId == 1) {
            log.info("use file mode");
            String filename = "listRecommended_en";
            if (lang != null && lang.equals(LangTable.LANG_ZH)) {
                filename = "listRecommended_zh";
            }
            if (!filename.equals("listRecommended_zh")) {
                String url = urlRoot + filename;
                String result = NnNetUtil.urlGet(url);
                if (result == null)
                    return new PlayerApiService().assembleMsgs(NnStatusCode.INPUT_BAD, null);
                return result;                
            }            
        }
        PlayerApiService api = new PlayerApiService();
        lang = api.checkLang(lang);    
        if (lang == null)
            return api.assembleMsgs(NnStatusCode.INPUT_BAD, null);             
        NnSetManager setMngr = new NnSetManager();
        List<NnSet> sets = setMngr.findFeatured(lang, msoId);
        String[] result = {""};
        for (NnSet set : sets) {
            String[] obj = {
                String.valueOf(set.getId()),
                set.getName(),
                "",
                set.getImageUrl(),
                String.valueOf(set.getCntChannel()),
            };
            result[0] += NnStringUtil.getDelimitedStr(obj) + "\n";          
        }
        return api.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public String category(String id, String lang, boolean flatten, Mso mso) {
        if (mso.getId() == 1) {
            String filename = "category_en";
            if (lang != null && lang.equals(LangTable.LANG_ZH)) {
                filename = "category_zh";
            }
            if (id != null) {
                filename = "category_" + id;
                if (id.contains("s"))
                    filename = id;
            }
            log.info("file mode:" + filename);
            String url = urlRoot + filename;
            String result = NnNetUtil.urlGet(url);            
            if (result == null)
                return new PlayerApiService().assembleMsgs(NnStatusCode.INPUT_BAD, null);
            else
                return result;
        }
        PlayerApiService api = new PlayerApiService();
        lang = api.checkLang(lang);    
        if (lang == null) {
            return api.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        }
        if (id == null) {
            id = "0";
        }        
        String[] result = {"", "", ""};
        SysTagManager tagMngr = new SysTagManager();        
        //if it's a set, find channel info
        result[0] = "id" + "\t" + id + "\n";
        if (!id.equals("0")) {            
            long tagId = Long.parseLong(id);
            SysTag tag = tagMngr.findById(tagId);
            if (tag != null) {
                result[0] += "piwik" + "\t" + "" + "\n";
            }
            List<NnChannel> channels = tagMngr.findPlayerChannelsById(tagId, lang, SysTag.SORT_SEQ, 0);
            for (NnChannel c : channels) {
                c.setSorting(NnChannelManager.getDefaultSorting(c));
            }
            result[2] = this.composeChannelLineup(channels);
            return api.assembleMsgs(NnStatusCode.SUCCESS, result);
        }        
        
        SysTagDisplayManager displayMngr = new SysTagDisplayManager();
        List<SysTagDisplay> categories = displayMngr.findPlayerCategories(lang, mso.getId());
        //if it's just categories, find categories
        for (SysTagDisplay c : categories) { 
            String name =  c.getName();
            int cnt = c.getCntChannel();
            String subItemHint = "ch"; //what's under this level
            String[] str = {String.valueOf(c.getId()), 
                            name, 
                            String.valueOf(cnt), 
                            subItemHint};               
            result[1] += NnStringUtil.getDelimitedStr(str) + "\n";
        }
        return api.assembleMsgs(NnStatusCode.SUCCESS, result);
    }
    
    public String setInfo(String id, String name, Mso mso) {
        PlayerApiService api = new PlayerApiService();
        if (id == null && name == null) {
            return api.assembleMsgs(NnStatusCode.INPUT_MISSING, null);
        }
        if (mso == null) {
            return api.assembleMsgs(NnStatusCode.INPUT_BAD, null);
        }
        
        if (mso.getId() == 1) {                        
            if (id != null && id.startsWith("s")) id = id.replace("s", "");
            long setId = Long.parseLong(id);
            if (setId < 5000) {                
                String filename = id;
                String url = urlRoot + "s" + filename;
                String result = NnNetUtil.urlGet(url);
                if (result == null)
                    return new PlayerApiService().assembleMsgs(NnStatusCode.INPUT_BAD, null);        
                return result;
            }
        }
        if (id != null && id.startsWith("s")) id = id.replace("s", "");
        NnSetManager setMngr = new NnSetManager();
        NnSet set = null;
        if (id != null) {
            set = setMngr.findById(Long.parseLong(id));
        } else {
            set = setMngr.findByName(name, mso.getId());
        }
        if (set == null)
            return api.assembleMsgs(NnStatusCode.SET_INVALID, null);            
        
        List<NnChannel> channels = setMngr.findChannels(set, mso);
        String result[] = {"", "", ""};

        //mso info
        result[0] += PlayerApiService.assembleKeyValue("name", mso.getName());
        result[0] += PlayerApiService.assembleKeyValue("imageUrl", mso.getLogoUrl()); 
        result[0] += PlayerApiService.assembleKeyValue("intro", mso.getIntro());            
        //set info
        result[1] += PlayerApiService.assembleKeyValue("id", String.valueOf(set.getId()));
        result[1] += PlayerApiService.assembleKeyValue("name", set.getName());
        result[1] += PlayerApiService.assembleKeyValue("imageUrl", set.getImageUrl());
        result[1] += PlayerApiService.assembleKeyValue("piwik", "");
        //channel info
        for (NnChannel c : channels) {
            if (c.getStatus() == NnChannel.STATUS_SUCCESS && c.isPublic())
                c.setSorting(NnChannelManager.getDefaultSorting(c));
        }   
        result[2] = this.composeChannelLineup(channels);
        return api.assembleMsgs(NnStatusCode.SUCCESS, result);        
    }
    
    public String composeChannelLineup(List<NnChannel> channels) {
        String result = "";
        for (NnChannel c : channels) {
            String name = c.getPlayerName();
            if (name != null)
                name = name.replaceAll("\n", " ").replaceAll("\t", " ");
            else {
                name = "";
            }
            String[] split = name.split("\\|");
            name = split.length == 2 ? split[0] : name;            
            String intro = c.getPlayerIntro();        
            if (intro != null)
                intro = intro.replaceAll("\n", " ").replaceAll("\t", " ");
            String imageUrl = c.getPlayerPrefImageUrl();
            log.info("ios: imageUrl:" + imageUrl);
            imageUrl = imageUrl.indexOf("|") < 0 ? imageUrl : imageUrl.substring(0, imageUrl.indexOf("|"));
            log.info("ios: after imageUrl:" + imageUrl);
            String youtubeId = "";
            if (c.getSourceUrl() != null && c.getSourceUrl().contains("http://www.youtube.com"))
                youtubeId = YouTubeLib.getYouTubeChannelName(c.getSourceUrl());
            if (c.getContentType() == NnChannel.CONTENTTYPE_FACEBOOK) 
                youtubeId = c.getSourceUrl();
            String[] ori = {Integer.toString(c.getSeq()),                         
                            String.valueOf(c.getId()),
                            name,
                            intro,
                            imageUrl,
                            String.valueOf(c.getCntEpisode()),
                            String.valueOf(c.getType()),
                            String.valueOf(c.getStatus()),
                            String.valueOf(c.getContentType()),
                            youtubeId,
                            NnChannelManager.convertEpochToTime(c.getTranscodingUpdateDate(), c.getUpdateDate()),
                            String.valueOf(c.getSorting()),
                            c.getPiwik(),
                            String.valueOf(c.getRecentlyWatchedProgram()),
                            c.getOriName(),
                            String.valueOf(c.getCntSubscribe()),
                            };
            result += NnStringUtil.getDelimitedStr(ori) + "\n";
        }
        return result;
    }

    private String composeLimitProgramInfoStr(String input, long sidx, long limit) {
        if (sidx == 0 && limit == 0)
            return input;
        String[] lines = input.split("\n");
        String result = "";
        long start = sidx - 1;
        long end = start + limit;
        for (int i=0; i<lines.length; i++) {
            if (i>=start && i<end) {
                result += lines[i] + "\n";
            }
            if (i > end) {
                return result;
            }
        }        
        return result;
    }

    public String findPlayerProgramInfoByChannel(long channelId, long sidx, long limit) {
        String result = this.findPlayerProgramInfoByChannel(channelId);
        return this.composeLimitProgramInfoStr(result, sidx, limit);
    }    
    
    public String findPlayerProgramInfoByChannel(long channelId) {
        log.info("request from != v32");
        String result = null;
        NnProgramManager programMngr = new NnProgramManager();
        try {
            result = (String)CacheFactory.get(programMngr.getV31CacheKey(channelId));            
        } catch (Exception e) {
            log.info("memcache error");
        }
        if (result != null && channelId != 0) { //id = 0 means fake channel, it is dynamic
            log.info("get programInfo from v31 cache");
            return result;
        }        
        List<NnProgram> programs = programMngr.findPlayerProgramsByChannel(channelId);
        log.info("retrieve v31 from db, channel id:" + channelId + "; program size:" + programs.size());        
        String str = this.composeProgramInfoStr(programs);
        CacheFactory.set(programMngr.getV31CacheKey(channelId), str);
        
        return str;
    }    

	public String search(String text) {
		List<NnChannel> searchResults = NnChannelManager.search(text, null, null, false, 1, 9);
		String[] result = {""};
		result[0] = this.composeChannelLineup(searchResults);
		return new PlayerApiService().assembleMsgs(NnStatusCode.SUCCESS, result);
	}
    
    public String composeProgramInfoStr(List<NnProgram> programs) {        
        String output = "";        
        String regexCache = "^(http|https)://(9x9cache.s3.amazonaws.com|s3.amazonaws.com/9x9cache)";
        String regexPod = "^(http|https)://(9x9pod.s3.amazonaws.com|s3.amazonaws.com/9x9pod)";
        String cache = "http://cache.9x9.tv";
        String pod = "http://pod.9x9.tv";
        for (NnProgram p : programs) {
            //file urls
            String url1 = p.getFileUrl();
            String url2 = ""; //not used for now
            String url3 = ""; //not used for now
            String url4 = p.getAudioFileUrl();
            String imageUrl = p.getImageUrl();
            String imageLargeUrl = p.getImageUrl();
            if (imageUrl == null) {imageUrl = "";}
            if (imageLargeUrl == null) {imageLargeUrl = "";}
            if (url1 != null) {
                url1 = url1.replaceFirst(regexCache, cache);
                url1 = url1.replaceAll(regexPod, pod);
            }
            url2 = url2.replaceFirst(regexCache, cache);
            url2 = url2.replaceAll(regexPod, pod);
            url3 = url3.replaceFirst(regexCache, cache);
            url3 = url3.replaceAll(regexPod, pod);
            if (url4 != null) {
                url4 = url4.replaceFirst(regexCache, cache);
                url4 = url4.replaceAll(regexPod, pod);
            }
            if (imageUrl != null) {
                imageUrl = imageUrl.replaceFirst(regexCache, cache);
                imageUrl = imageUrl.replaceAll(regexPod, pod);
            }
            if (imageLargeUrl != null) {
                imageLargeUrl = imageLargeUrl.replaceFirst(regexCache, cache);
                imageLargeUrl = imageLargeUrl.replaceAll(regexPod, pod);
            }
                    
            //the rest
            String[] ori = {String.valueOf(p.getChannelId()), 
                            String.valueOf(p.getId()), 
                            p.getPlayerName(), 
                            p.getPlayerIntro(),
                            String.valueOf(p.getContentType()), 
                            p.getDuration(),
                            imageUrl,
                            imageLargeUrl,
                            url1, 
                            url2, 
                            url3, 
                            url4,            
                            String.valueOf(p.getUpdateDate().getTime()),
                            p.getComment()};
            output = output + NnStringUtil.getDelimitedStr(ori);
            output = output.replaceAll("null", "");
            output = output + "\n";
        }
        return output;        
    }    
}
