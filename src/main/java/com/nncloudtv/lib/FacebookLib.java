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
import java.util.logging.Logger;

import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.service.MsoConfigManager;
import com.nncloudtv.web.json.facebook.FBPost;
import com.nncloudtv.web.json.facebook.FacebookMe;
import com.nncloudtv.web.json.facebook.FacebookResponse;

public class FacebookLib {
    
    protected static final Logger log = Logger.getLogger(FacebookLib.class.getName());
    
    private static String generateState() {
        String time = String.valueOf(new Date().getTime());
        String random = RandomStringUtils.randomAlphabetic(10);
        String result = time + random;
        result = RandomStringUtils.random(10, 0, 10, true, true, result.toCharArray());     
        return result;
    }
    
    public String getProfilePic(String username) {
        try {
            URL url = new URL("http://graph.facebook.com/" + username + "/picture");
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
            URL url = new URL("https://graph.facebook.com/me?access_token=" + URLEncoder.encode(accessToken, "ascii"));
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
    
    public String[] getOAuthAccessToken(String code, String uri, String fbLoginUri, Mso mso){
        String urlBase = "https://graph.facebook.com/oauth/access_token";
        String data[] = {null, null}; //token, expires
        MsoConfigManager configMngr = new MsoConfigManager();
        String clientId = configMngr.getFacebookInfo(MsoConfig.FACEBOOK_CLIENTID, mso);
        String secret = configMngr.getFacebookInfo(MsoConfig.FACEBOOK_CLIENTSECRET, mso);                
        log.info("pass back is?:" + uri);
        try {
            URL url = new URL(urlBase);
            log.info("uri using for token:" + uri);
            String modifiedRedirectUri = fbLoginUri + "?uri=" + URLEncoder.encode(uri, "ascii");
            String params = "client_id=" + clientId +             
                            "&code=" + code + 
                            "&client_secret=" + secret +
                            "&redirect_uri=" + URLEncoder.encode(modifiedRedirectUri, "ascii");
            log.info("FACEBOOK: (oauth) params:" + params);
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public static String[] getLongLivedAccessToken(String shortLivedAccessToken, Mso mso){
        String urlBase = "https://graph.facebook.com/oauth/access_token";
        String data[] = {null, null}; //token, expires
        MsoConfigManager configMngr = new MsoConfigManager();
        String clientId = configMngr.getFacebookInfo(MsoConfig.FACEBOOK_CLIENTID, mso);
        String secret = configMngr.getFacebookInfo(MsoConfig.FACEBOOK_CLIENTSECRET, mso);        
        try {
            URL url = new URL(urlBase);
            String params = "client_id=" + clientId +
                            "&client_secret=" + secret +
                            "&grant_type=fb_exchange_token" +
                            "&fb_exchange_token=" + shortLivedAccessToken;
            log.info("FACEBOOK: (oauth) params:" + params);
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
    
    public static String getDialogOAuthPath(String referrer, String fbLoginUri, Mso mso) {
        MsoConfigManager configMngr = new MsoConfigManager();
        String clientId = configMngr.getFacebookInfo(MsoConfig.FACEBOOK_CLIENTID, mso);

        String url = "http://www.facebook.com/dialog/oauth?" +
                     "client_id=" + clientId +
                     "&scope=user_likes,user_location,user_interests,email,user_birthday" +
                     "&state=" + FacebookLib.generateState();
        try {
            String modifiedRedirectUri = fbLoginUri + "?uri=" + URLEncoder.encode(referrer, "ascii");
            url += "&redirect_uri=" + URLEncoder.encode(modifiedRedirectUri, "ascii");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("url = " + url);
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
        //log.info("------------the encoding handle by now is : "+connection.getContentEncoding()+" ------------");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        //connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        //log.info("------------the encoding handle by now is : "+connection.getContentEncoding()+" ------------");
        log.info("------------ready for post to facebook------------");
        
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(post);
        writer.close();
        
        //log.info("------------the encoding handle by now is : "+connection.getContentEncoding()+" ------------");
        
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
    
    
    static public FacebookResponse populatePageList(String fbUserId, String accessToken) {
        
        log.info("fbUserId = " + fbUserId + ", accessToken = " + accessToken);
        
        if (fbUserId == null || accessToken == null) {
            return null;
        }
        
        //List<FacebookPage> pages = null;
        FacebookResponse response = null;
        
        try {
            String fullpath = 
                    "https://graph.facebook.com/" + fbUserId + 
                    "/accounts?type=page&access_token=" + URLEncoder.encode(accessToken, "US-ASCII");
            log.info(fullpath);
            URL url = new URL(fullpath);
            ObjectMapper mapper = new ObjectMapper();
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if (connection.getResponseCode() >= 400) {
                response = mapper.readValue(connection.getErrorStream(), FacebookResponse.class);
            } else {
                response = mapper.readValue(connection.getInputStream(), FacebookResponse.class);
            }
            /*
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
            */
        } catch (MalformedURLException e) {
            logException(e);
        } catch (UnsupportedEncodingException e) {
            logException(e);
        } catch (IOException e) {
            logException(e);
        }
        
        return response;
    }
    
    static private void logException(Exception e) {
        log.warning(e.getClass().getCanonicalName());
        NnLogUtil.logException(e);
    }
}
