package com.nncloudtv.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.jdo.JDOFatalDataStoreException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.FacebookLib;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.lib.QueueFactory;
import com.nncloudtv.model.NnEmail;
import com.nncloudtv.model.Pdr;
import com.nncloudtv.service.EmailService;
import com.nncloudtv.service.PdrManager;
import com.nncloudtv.web.json.facebook.FBPost;
import com.nncloudtv.web.json.facebook.FacebookError;
import com.nncloudtv.web.json.facebook.FacebookMe;
import com.nncloudtv.web.json.transcodingservice.ChannelInfo;
 
@Controller
@RequestMapping("hello")
public class HelloController {

    //protected static final Logger log = Logger.getLogger(HelloController.class.getName());
    protected static final Logger log = Logger.getLogger(HelloController.class);

    //basic test
    @RequestMapping("world")
    public ModelAndView world(HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        session.setMaxInactiveInterval(1);
        String message = "Hello NnCloudTv";
        return new ModelAndView("hello/hello", "message", message);
    }    

    //log test
    @RequestMapping("log")
    public ModelAndView log()  {
        for (int i=0; i<100; i++) {
            int img = new Random().nextInt(10);
            System.out.println("img:" + img);
        }
        log.info("----- hello log -----");
        log.warn("----- hello warning -----");
        log.fatal("----- hello severe -----");
        System.out.println("----- hello console -----");
        return new ModelAndView("hello", "message", "log");
    }    
        
    //email service test
    @RequestMapping("email")
    public @ResponseBody String email(
            @RequestParam String toEmail, 
            @RequestParam String toName, 
            HttpServletRequest req) {
        EmailService service = new EmailService();
        NnEmail mail = new NnEmail(toEmail, toName, NnEmail.SEND_EMAIL_SHARE, "share 9x9", NnEmail.SEND_EMAIL_SHARE, "hello", "world");
        mail.setBody("<h1>hello</h1>");
        mail.setHtml(true);
        service.sendEmail(mail, null, null);
        
        return "email sent";
    }
        
    //db test
    @RequestMapping("pdr")
    public @ResponseBody String pdr() throws Exception{
        try {
            PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
            try {
                Pdr raw = new Pdr(1, "session1", "test");
                pm.makePersistent(raw);
            }finally {
                pm.close();
            }
        } catch (JDOFatalDataStoreException e){
            log.info("Fatal Exception");
        } catch (Throwable t) {            
            if (t.getCause() instanceof JDOFatalDataStoreException) {
                log.info("");
            }    
        }
        return "OK";
    }    

    //db test through manager
    @RequestMapping("pdr_mngt")
    public @ResponseBody String pdr_mngt() { 
        PdrManager pdrMngr = new PdrManager();
        Pdr pdr = new Pdr(1, "session1", "test");
        pdrMngr.create(pdr);
        return "OK";
    }                
    
    //cache test
    @RequestMapping("cache")
    public ResponseEntity<String> cache() {
        String output = "No Cache";
        
        String cacheValue = (String)CacheFactory.get("hello");
        output = "original: " + cacheValue + "\n";

        if (CacheFactory.isRunning) {
            output += "it's running"  + "\n";
            if (cacheValue == null) {
               CacheFactory.set("hello", "9x9");
               cacheValue = (String)CacheFactory.get("hello");
               output += "after set cache: " + cacheValue;
            }
        } else {
            output += "cache is not running";
        }
        
        return NnNetUtil.textReturn(output);
    }        
        
    /**
     * MQ / MQCallback - tiny MQ loopback test
     * 
     * http://localhost:8888/hello/MQ?msg=HelloWorld
     * 
     * @param req
     * @param msg
     * @return
     */    
    @RequestMapping("MQ")
    public @ResponseBody String MQ(HttpServletRequest req, @RequestParam(required=false) String msg) {        
        FacebookError json = new FacebookError(); // none of FB business though
        json.setType("MQ Test");
        if (msg != null) {
            json.setMessage(msg);
            log.info("your message is: " + msg);
        } else {
            json.setMessage("none");
            log.info("you didn't specify message to carry");
        }
        
        QueueFactory.add("/hello/MQCallback", json);
        
        return "OK";
    }
    
    @RequestMapping("MQCallback")
    public @ResponseBody void MQCallback(@RequestBody FacebookError err, HttpServletRequest req) {        
        log.info("MQCallback received message: " + err.getMessage());
        
    }
    
    //queue test
    @RequestMapping("queue")
    public @ResponseBody String queue(HttpServletRequest req, @RequestParam String msg) throws IOException {
        ChannelInfo info = new ChannelInfo();
        info.setErrorCode("lalala");
        System.out.println(info.toString());
        QueueFactory.add(msg, null);
        
        /*
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();        
        channel.queueDeclare(QueueMessage.QUEUE_NNCLOUDTV, true, false, false, null);        
        channel.basicPublish( "", QueueMessage.QUEUE_NNCLOUDTV, 
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    msg.getBytes());
        System.out.println(" [x] Sent '" + msg + "'");
        
        channel.close();
        connection.close();
        */
        return "OK";
    }

    //test ip behind proxy, aka load balancer
    @RequestMapping("getIp")
    public ResponseEntity<String> getIp(HttpServletRequest req) {
        String oriIp = req.getRemoteAddr();
        String ip = NnNetUtil.getIp(req);
        String output = "ori ip:" + oriIp + "\n" + ";process ip:" + ip;
        return NnNetUtil.textReturn(output);
    }
    
    //fb test    
    @RequestMapping("FB")
    public ResponseEntity<String> FB(HttpServletRequest req) {        
        String now = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).format(new Date()).toString();
        
        FBPost fbPost = new FBPost(now, "FB/MQ loopback test", "http://www.iteye.com/upload/logo/user/76967/bf3e420a-8e22-36b8-84e2-1b31c23407f1.jpg");
        fbPost.setLink("http://eternal1025.iteye.com/blog/344360");
        fbPost.setMessage("test");
        fbPost.setCaption("999999");
        fbPost.setFacebookId("197930870280133");
        fbPost.setAccessToken("AAABk0M5owJgBANNPKAVzYjaDktNjivXAP2Y2HSZAIZCq4OdnLm92vgr22Or72LUDSUtnCH3VV8ZAsAKCjzamvL15R31RZCeUZCZB8KFhCAMgZDZD");
        
        QueueFactory.add("/CMSAPI/postToFacebook", fbPost);
        
        return NnNetUtil.textReturn("OK");
    }
    
    @RequestMapping("fbMe")
    public ResponseEntity<String> fbMe(HttpServletRequest req) {
        FacebookLib lib = new FacebookLib();
        String accessToken = "AAAF7oZCnYjt4BAArGAWMX5GUOuXooF60R3L8ZAqAfFHZAUAq9CAZBXDmCjEip1g6Ok7llSU3cg84ARGUeFoilJx3HMeHpNRlBcRKXpz71gZDZD";
        //String accessToken = "AAAF7oZCnYjt4BAArGAWMX5GUOuXooF60R3L8ZAqAfFHZAUAq9CAZBXDmC";
        FacebookMe me = lib.getFbMe(accessToken);
        String output = me.getEmail();
        output += "\n" + me.getId(); //get from fb
        output += "\n" + me.getName();
        output += "\n" + me.getGender();
        output += "\n" + me.getLocale();
        output += "\n" + me.getBirthday();
        output += "\n" + me.getStatus();
        return NnNetUtil.textReturn(output);
    }

    @RequestMapping("fbPic")
    public String fbPic(HttpServletRequest req) {
        return "hello/hello";
//        FacebookLib lib = new FacebookLib();
//        String output = lib.getProfilePic("ywntseng");
//        return NnNetUtil.textReturn(output);
    }
    
}
    
