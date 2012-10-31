package com.nncloudtv.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.service.MsoConfigManager;
import com.nncloudtv.web.json.facebook.FBPost;
import com.nncloudtv.web.json.facebook.FacebookError;
import com.nncloudtv.web.json.facebook.FacebookMe;
import com.nncloudtv.web.json.facebook.FacebookPage;
import com.nncloudtv.web.json.facebook.FacebookResponse;

public class FacebookLib {
    protected static final Logger log = Logger.getLogger(FacebookLib.class.getName());

    //TODO move to db or property file
    //dev1
    protected static String clientId = "417419178315486";
    protected static String secret = "bb96e3578cfb0822796810601d554e97";
    protected static String redirectUri = "http://dev1.9x9.tv/fb/login";

    //dev2
    /*
    protected static String clientId = "409773719071373";
    protected static String secret = "412472443830cf5dd62ee5f5cd2450a4";
    protected static String redirectUri = "http://dev2.9x9.tv/fb/login";
    */

    //beagle    
    /*
    protected static String clientId = "411604618902543";
    protected static String secret = "ea2ba5658851e3c02a97b10dc9c99146";
    protected static String redirectUri = "http://beagle.9x9.tv/fb/login";
    */

    //demo
    /*
    protected static String clientId = "361253423962738";
    protected static String secret = "85106cc16d80a5705a060a0bbae7cb60";
    protected static String redirectUri = "http://demo.9x9.tv/fb/login";
    */

    //production
    /*
    protected static String clientId = "110847978946712";
    protected static String secret = "1a6abc521920290b1e8c489134daeb06";
    protected static String redirectUri = "http://www.9x9.tv/fb/login";
    */
    
    private static String generateState() {
        String time = String.valueOf(new Date().getTime());
        String random = RandomStringUtils.randomAlphabetic(10);
        String result = time + random;
        result = RandomStringUtils.random(10, 0, 10, true, true, result.toCharArray());     
        return result;
    }
    
    public String getProfilePic(String username) {
        try {
            URL url = new URL(" http://graph.facebook.com/" + username + "/picture");
            log.info("FACEBOOK:(profile pic)-query:" + url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = reader.readLine();
                reader.close();
                log.info(line);     
                return line;
            } else {
                log.info("FACEBOOK: (profile pic)-response status:" + connection.getResponseCode() + " with url: " + url.toString());
            }                
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public FacebookMe getFbMe(String accessToken) {
        try {
            URL url = new URL("https://graph.facebook.com/me?access_token=" + URLEncoder.encode(accessToken, "utf-8"));
            log.info("FACEBOOK:(me)-query:" + url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = reader.readLine();
                reader.close();
                log.info(line);                        
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                //StringEscapeUtils.unescapeJava(data[2])
                FacebookMe me = mapper.readValue(line, FacebookMe.class);
                me.setAccessToken(accessToken);
                log.info("FACEBOOK: (me)-return:" + me.toString());                
                return me;
            } else {
                log.info("FACEBOOK: (me)-response status:" + connection.getResponseCode() + " with url: " + url.toString());
            }
        } catch (MalformedURLException e) {
                e.printStackTrace();
        } catch (ProtocolException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        FacebookMe me = new FacebookMe();
        me.setStatus(FacebookMe.STATUS_ERROR);
        return me;
    }
    
    public String[] getOAuthAccessToken(String code){
        String urlBase = "https://graph.facebook.com/oauth/access_token";
        String data[] = {null, null}; //token, expires
        try {
            URL url = new URL(urlBase);
            String params = "client_id=" + clientId +             
                            "&code=" + code + 
                            "&client_secret=" + secret +
                            "&redirect_uri=" + redirectUri;                 
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(params);
            writer.close();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                reader.close();
                log.info("FACEBOOK: (oauth)-return:" + line);
                try {
                    if (line.contains("expires")) {
                        data[0] = line.split("access_token=")[1].split("&expires=")[0];
                        if (line.split("expires=").length > 0)
                            data[1] = line.split("expires=")[1];
                    } else {
                        log.info("FACEBOOK: (oauth)-return does NOT have expires");
                        data[0] = line.split("access_token=")[1];
                    }
                } catch (Exception e) {
                    log.info("FACEBOOK: (oauth) get token error");
                    e.printStackTrace();
                }
                log.info("FACEBOOK: (oauth token)-token:" + data[0] + "; expires:" + data[1]);
            } else {
                log.info("FACEBOOK: (oauth token)-failed response status:" + connection.getResponseCode());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public static String getDialogOAuthPath() {        
        String scope = "user_likes,user_location,user_interests,email,user_birthday";
        String state = FacebookLib.generateState();
        String urlBase = "http://www.facebook.com/dialog/oauth?";
        String url = urlBase +
                     "client_id=" + clientId +
                     "&redirect_uri=" + redirectUri +
                     "&scope=" + scope +
                     "&state=" + state;
        return url;
    }

    public String[] getFanpageInfo(String urlStr) {
        String query = urlStr.replace("www.facebook.com", "graph.facebook.com");
        query = query.replace("https", "http");
        URL url;
        String fbInfo[] = new String[2];
        String username = "";
        String picture = "";
        if (urlStr.contains("pages")) {
            int start = urlStr.indexOf("pages/", 1);
            query = urlStr.substring(start+6);
            start = query.indexOf("/", 1);
            if (start > 1)
               query = query.substring(start+1);
               query = "http://graph.facebook.com/" + query;
        }
        try {
            //HTTP GET
            url = new URL(query);                       
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            
            //connection.setDoOutput(true);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.info("podcast GET response not ok!" + connection.getResponseCode());                
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                String[] line = inputLine.split(",");
                for (String l : line) {
                    if (l.contains("name") || l.contains("picture")) {
                        l = l.replaceAll("\"", "");
                        String[] entity = l.split(":");                     
                        if (l.contains("name")) 
                            username = entity[1];
                        if (l.contains("picture")) 
                            picture = entity[1] + entity[2];
                            picture = picture.replace("http\\/\\/", "http://");
                            picture = picture.replace("\\/", "/");
                    }                           
                }
            }
            reader.close();            
            fbInfo[0] = username;
            fbInfo[1] = picture; 
        } catch (Exception e) {
            fbInfo[0] = "";
        }
        return fbInfo;
    }
    
    public static void postToFacebook(FBPost fbPost) throws IOException {
        
        MsoConfigManager configMngr = new MsoConfigManager();
        MsoConfig fbConfig = configMngr.findByItem(MsoConfig.REALFBTOKEN);
        String accessToken = null;
        if (fbConfig != null) {
            accessToken = fbConfig.getValue();
        }
        if (fbPost.getAccessToken() != null) {
            accessToken = fbPost.getAccessToken();
        }
        
        URL url = new URL("https://graph.facebook.com/" + fbPost.getFacebookId() + "/feed");
        String post =
            "access_token=" + URLEncoder.encode(accessToken, "US-ASCII") +
            "&picture=" + URLEncoder.encode(fbPost.getPicture(), "US-ASCII") +
            "&name=" + URLEncoder.encode(fbPost.getName(), "UTF-8") +
            "&link=" + URLEncoder.encode(fbPost.getLink(), "US-ASCII")+
            "&caption=" + URLEncoder.encode(fbPost.getCaption(), "UTF-8") +
            "&description=" + URLEncoder.encode(fbPost.getDescription(), "UTF-8");
        if (fbPost.getMessage() != null && !fbPost.getMessage().isEmpty()) {
            post += "&message=" + URLEncoder.encode(fbPost.getMessage(), "UTF-8");
        }
        log.info(post);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(post);
        writer.close();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        reader.close();
        log.info(line);
    }
    
    public static void postToTwitter(FBPost fbPost) throws IOException, TwitterException {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer("udWzz6YrsaNlbJ18vZ7aCA", "Pf0TdB2QFXWKyphbIdnPG4vZhLVze0cPCxlLkfBwtQ");
        AccessToken accessToken = new AccessToken(fbPost.getFacebookId(), fbPost.getAccessToken());
        twitter.setOAuthAccessToken(accessToken);
        twitter.updateStatus(fbPost.getName()+" "+fbPost.getLink());
    }
    
    
    static public List<FacebookPage> populatePageList(String fbUserId, String accessToken) {
        
        log.info("fbUserId = " + fbUserId + ", accessToken = " + accessToken);
        
        if (fbUserId == null || accessToken == null) {
            return null;
        }
        
        List<FacebookPage> pages = null;
        
        try {
            String fullpath = 
                    "https://graph.facebook.com/" + fbUserId + 
                    "/accounts?access_token=" + URLEncoder.encode(accessToken, "US-ASCII");
            log.info(fullpath);
            URL url = new URL(fullpath);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            ObjectMapper mapper = new ObjectMapper();
            FacebookResponse response = mapper.readValue(connection.getInputStream(), FacebookResponse.class);
            if (response.getData() != null) {
                pages = response.getData();
                log.info("pages count: " + pages.size());
            } else if (response.getError() != null) {
                FacebookError error = response.getError();
                log.warning("error message: " + error.getMessage());
                log.warning("error type:" + error.getType());
            } else {
                log.warning("neither no data nor error");
            }
        } catch (MalformedURLException e) {
            logException(e);
        } catch (UnsupportedEncodingException e) {
            logException(e);
        } catch (IOException e) {
            logException(e);
        }
        
        return pages;
    }
    
    static private void logException(Exception e) {
        log.warning(e.getClass().getCanonicalName());
        NnLogUtil.logException(e);
    }
}