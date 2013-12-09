package com.nncloudtv.lib;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Key;
import com.nncloudtv.web.api.NnStatusCode;

public class YouTubeLib {
    
    protected static final Logger log = Logger.getLogger(YouTubeLib.class.getName());
    
    public static final String regexNormalizedVideoUrl = "^http:\\/\\/www\\.youtube\\.com\\/watch\\?v=[^&]+$";
    public static final String regexVideoIdStr = "v=([^&]+)";
    public static final String youtubeChannelPrefix = "http://www.youtube.com/user/";
    public static final String youtubePlaylistPrefix = "http://www.youtube.com/view_play_list?p=";
        
    /** 
     * 1. remove those invalid keywords we already know.
     * 2. merge the following youtube channel formats to one, http://www.youtube.com/user/<userid>
     *    http://www.youtube.com/<usrid>
     *    http://www.youtube.com/user/<usrid>
     *    http://www.youtube.com/profile?user=<usrid>
     * 3. merge the following youtube playlist formats to one, http://www.youtube.com/view_play_list?p=<pid>
     *    http://www.youtube.com/view_play_list?p=<pid>
     *    http://www.youtube.com/playlist?list=PL03D59E2ECDDA66DF
     *    http://www.youtube.com/user/UCBerkeley#p/c/<pid>
     *    http://www.youtube.com/user/UCBerkeley#g/c/<pid>
     *    http://www.youtube.com/user/UCBerkeley#p/c/<pid>/0/-dQltKG3NlI
     *    http://www.youtube.com/profile?user=UCBerkeley#grid/user/<pid>
     *    http://www.youtube.com/watch?v=-dQltKG3NlI&p=<pid>
     *    http://www.youtube.com/watch?v=-dQltKG3NlI&playnext=1&list=<pid>
     * 4. youtube api call (disabled for now)
     * Example1: they should all become http://www.youtube.com/user/davidbrucehughes    
     *    http://www.youtube.com/profile?user=davidbrucehughes#g/u
     *    http://www.youtube.com/davidbrucehughes#g/a
     *    http://www.youtube.com/user/davidbrucehughes#g/p
     * Example2: they should all become http://www.youtube.com/user/view_play_list?p=03D59E2ECDDA66DF
     *    http://www.youtube.com/playlist?list=PL03D59E2ECDDA66DF
     *    http://www.youtube.com/view_play_list?p=03D59E2ECDDA66DF
     *    http://www.youtube.com/user/UCBerkeley#p/c/03D59E2ECDDA66DF
     *    http://www.youtube.com/user/UCBerkeley#g/c/095393D5B42B2266
     *    http://www.youtube.com/user/UCBerkeley#p/c/03D59E2ECDDA66DF/0/-dQltKG3NlI
     *    http://www.youtube.com/profile?user=UCBerkeley#grid/user/03D59E2ECDDA66DF
     *    http://www.youtube.com/watch?v=-dQltKG3NlI&p=03D59E2ECDDA66DF
     *    http://www.youtube.com/watch?v=-dQltKG3NlI&playnext=1&list=PL03D59E2ECDDA66DF
     *    http://www.youtube.com/watch?v=-dQltKG3NlI&playnext=1&list=PL03D59E2ECDDA66DF&feature=list_related
     */        
    public static String formatCheck(String urlStr) {
        if (urlStr == null) {return null;}
        String[] invalid = {"index", "videos",
                            "entertainment", "music", "news", "movies",
                            "comedy", "gaming", "sports", "education",
                            "shows", 
                            "store", "channels", "contests_main"};        
        HashSet<String> dic = new HashSet<String>();
        for (int i=0; i<invalid.length; i++) {
            dic.add(invalid[i]);
        }
        //youtube channel
        String url = null;
        String reg = "^(http|https)://?(www.)?youtube.com/(user/|profile\\?user=)?(\\w+)";        
        Pattern pattern = Pattern.compile(reg);
        Matcher m = pattern.matcher(urlStr);
        while (m.find()) {
            if (dic.contains(m.group(4))) {return null;}
            url = "http://www.youtube.com/user/" + m.group(4);
        }
        //youtube playlist
        reg = "^(http|https)://?(www.)?youtube.com/(user/|profile\\?user=)?(.+)(#(p/c|g/c|grid/user)/(\\w+))";
        pattern = Pattern.compile(reg);
        m = pattern.matcher(urlStr);
        while (m.find()) {
            url = "http://www.youtube.com/view_play_list?p=" + m.group(7);
        }
        reg = "^(http|https)://?(www.)?youtube.com/view_play_list\\?p=(\\w+)";
        pattern = Pattern.compile(reg);
        m = pattern.matcher(urlStr);        
        while (m.find()) {
            //log.info("match:view_play_list\\?p=(\\w+)");
            url = "http://www.youtube.com/view_play_list?p=" + m.group(3);
        }        
        //http://www.youtube.com/playlist?list=PLJ2QT-PhqTiI-BWE0Efr4rhbfVO-Qg_4q
        reg = "^(http|https)://?(www.)?youtube.com/(.+)?(p|list)=(PL)?([\\w|_|-]+)";
        pattern = Pattern.compile(reg);
        m = pattern.matcher(urlStr);
        while (m.find()) {
            //log.info("match:(p|list)=(PL)?(\\w+)");
            url = "http://www.youtube.com/view_play_list?p=" + m.group(6);
        }

        // http://www.youtube.com/playlist?list=03D59E2ECDDA66DF
        // http://www.youtube.com/playlist?list=PL03D59E2ECDDA66DF               
        reg = "^(http|https)://?(www.)?youtube.com/playlist?list=(PL)?(\\w+)";
        pattern = Pattern.compile(reg);
        m = pattern.matcher(urlStr);
        while (m.find()) {
            //log.info("match playlist?list=(PL)?(\\w+)");
            url = "http://www.youtube.com/view_play_list?p=" + m.group(5);
        }

        if (url != null) { 
            //url = url.toLowerCase();
            if (url.equals("http://www.youtube.com/user/watch")) {
                url = null;
            }
        }
        log.info("original url:" + urlStr + ";result=" + url);        
        //if (!youTubeCheck(result)) {return null;} //till the function is fixed        
        return url;        
    }

     public static class YouTubeUrl extends GenericUrl {
        @Key final String alt = "jsonc";
        @Key String author;
        @Key String q;
        @Key("max-results") Integer maxResults;
        YouTubeUrl(String url) {
          super(url);
        }
    }

    public static class VideoFeed {
       @Key String title;
       @Key String subtitle;
       @Key String logo;
       @Key String description;
       @Key String author;
       @Key int totalItems;
       @Key List<Video> items;
    }

    public static class Author {
        @Key String name;
    }
    
    public static class MyFeed {
       @Key List<Video> items;
    }
    
    public static class Video {
        @Key String id;
        @Key String title;
        @Key String description;
        @Key Thumbnail thumbnail;
        @Key Player player;
        @Key Video video;
    }
    
    public static class Thumbnail {
        @Key String sqDefault;                    
    }
    
    public static class ProfileFeed {
        @Key List<String> items;
    }
        
    public static class Player {
        @Key("default") String defaultUrl;
    }
        
    public static HttpRequestFactory getFactory() {
        HttpTransport transport = new NetHttpTransport();
        final JsonFactory jsonFactory = new JacksonFactory();
        HttpRequestFactory factory = transport.createRequestFactory(new HttpRequestInitializer() {
               public void initialize(HttpRequest request) {
                  // set the parser
                  JsonCParser parser = new JsonCParser(jsonFactory);
                  //parser.jsonFactory = jsonFactory;
                  request.addParser(parser);
                   //set up the Google headers
                  GoogleHeaders headers = new GoogleHeaders();
                  headers.setApplicationName("Google-YouTubeSample/1.0");
                  headers.gdataVersion = "2";
                  request.setHeaders(headers);
               }
            });
        return factory;
    }

    public static Map<String, String> getYouTubeVideo(String videoId) {
        Map<String, String> results = new HashMap<String, String>();
        HttpRequestFactory factory = YouTubeLib.getFactory();        
        HttpRequest request;
        MyFeed feed;
        try {
            //https://gdata.youtube.com/feeds/api/videos/nIbzpk8FjbU?v=2&alt=jsonc            
            YouTubeUrl videoUrl = new YouTubeUrl("https://gdata.youtube.com/feeds/api/videos");
            videoUrl.q = videoId;
            videoUrl.maxResults = 1;
            request = factory.buildGetRequest(videoUrl);
            feed = request.execute().parseAs(MyFeed.class);
            if (feed.items != null) {                
                Video video = feed.items.get(0);                
                results.put("title", video.title);
                results.put("description", video.description);
                results.put("imageUrl", video.thumbnail.sqDefault);
            }
        } catch (Exception e) {
            NnLogUtil.logException(e);
        }
        return results;
    }
    
    //return key "status", "title", "thumbnail", "description"
    //TODO boolean channel removed, can easily tell by format
    public static Map<String, String> getYouTubeEntry(String userIdStr, boolean channel) {        
        //http://code.google.com/apis/youtube/2.0/developers_guide_jsonc.html
        Map<String, String> results = new HashMap<String, String>();
        results.put("status", String.valueOf(NnStatusCode.SUCCESS));
        HttpRequestFactory factory = YouTubeLib.getFactory();
        // set up the HTTP request factory
        /*
        HttpTransport transport = new NetHttpTransport();
        final JsonFactory jsonFactory = new JacksonFactory();
        HttpRequestFactory factory = transport.createRequestFactory(new HttpRequestInitializer() {
           public void initialize(HttpRequest request) {
              // set the parser
              JsonCParser parser = new JsonCParser(jsonFactory);
              //parser.jsonFactory = jsonFactory;
              request.addParser(parser);
               //set up the Google headers
              GoogleHeaders headers = new GoogleHeaders();
              headers.setApplicationName("Google-YouTubeSample/1.0");
              headers.gdataVersion = "2";
              request.setHeaders(headers);
           }
        });
        */
        
        // build the HTTP GET request
        HttpRequest request;
        VideoFeed feed;
        try {
            if (channel) {
                GenericUrl profileUrl = new GenericUrl("http://gdata.youtube.com/feeds/api/users/" + userIdStr);
                //jsonc does not support profile api
                request = factory.buildGetRequest(profileUrl);                         
                String reg = "(.*media:thumbnail url=')(.*)('/>.*)";                
                Pattern pattern = Pattern.compile(reg);
                Matcher m = pattern.matcher(request.execute().parseAsString());
                while (m.find()) {
                    results.put("thumbnail", m.group(2));
                }
            }
            //jsonc video support
            YouTubeUrl videoUrl = new YouTubeUrl("https://gdata.youtube.com/feeds/api/videos");
            if (channel) {
                videoUrl.author = userIdStr;
                results.put("author", userIdStr);
            } else {
                videoUrl = new YouTubeUrl("https://gdata.youtube.com/feeds/api/playlists/" + userIdStr);
            }
            videoUrl.maxResults = 1;            
            request = factory.buildGetRequest(videoUrl);
            feed = request.execute().parseAs(VideoFeed.class);
            results.put("totalItems", String.valueOf(feed.totalItems));            
            if (feed.items != null) {
                Video video = feed.items.get(0);                
                results.put("title", video.title);
                results.put("description", video.description);
                if (!channel) {
                    if (video.video != null && video.video.thumbnail != null) {
                        results.put("thumbnail", video.video.thumbnail.sqDefault);
                    } else {
                        log.info("video no thumbnail:" + videoUrl);
                    }
                }
            }
            if (!channel) {                
                results.put("title", feed.title);
                results.put("author", feed.author);                
                results.put("description", feed.description);
                log.info("play list title:" + feed.title + "; author:" + feed.author + "; description:" + feed.description);
            }
        } catch (HttpResponseException e) {
            results.put("status", String.valueOf(NnStatusCode.ERROR));            
            log.info("youtube http response error:" + e.getResponse().getStatusCode());
        } catch (IOException e) {
            results.put("status", String.valueOf(NnStatusCode.ERROR));
            NnLogUtil.logException(e);
        }
        return results;
    }
            
    public static String getYouTubeChannelName(String urlStr) {
        String channelUrl = "http://www.youtube.com/user/";
        String playListUrl = "http://www.youtube.com/view_play_list?p=";
        String name = urlStr.substring(channelUrl.length(), urlStr.length());        
        if (urlStr.contains("view_play_list")) {
            name = urlStr.substring(playListUrl.length(), urlStr.length()); 
        }
        return name;
    }

    /**
     * YouTube API request format, http://gdata.youtube.com/feeds/api/users/androidcentral
     * This function currently checks only if the query status is not 200.
     * 
     * @@@ IMPORTANT: This code will be blocked by YouTube, need to add user's IP, indicating you are on behalf of the user.
     * 
     * @param urlStr support only format of http://www.youtube.com/user/android  
     */
    public static boolean youTubeCheck(String urlStr) {        
        String[] splits = urlStr.split("/");
        String apiReq = "http://gdata.youtube.com/feeds/api/users/" + splits[splits.length-1];
        URL url;
        try {
            //HTTP GET
            url = new URL(apiReq);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                log.info("yutube GET response not ok with url:" + urlStr + "; status code = " + connection.getResponseCode());
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static boolean isVideoUrlNormalized(String url) {
        
        if (url == null) {
            return false;
        }
        
        return url.matches(regexNormalizedVideoUrl);
    }
    
    public static String getYouTubeVideoIdStr(String url) {
        
        if (url == null || url.length() == 0) {
            return url;
        }
        
        if (!isVideoUrlNormalized(url)) {
            return null;
        }
        
        Pattern pattern = Pattern.compile(regexVideoIdStr);
        Matcher matcher = pattern.matcher(url);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
}
