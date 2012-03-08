package com.nncloudtv.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;

import net.spy.memcached.MemcachedClient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.NnEmail;
import com.nncloudtv.model.PdrRaw;
import com.nncloudtv.service.EmailService;
import com.nncloudtv.service.PdrRawManager;
import com.nncloudtv.service.PlayerApiService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
 
@Controller
@RequestMapping("hello")
public class HelloController {
	
	//basic test
    @RequestMapping("/world")
    public ModelAndView helloWorld() { 
        String message = "Hello NnCloudTv";
        return new ModelAndView("hello", "message", message);
    }    

    @RequestMapping("/locale")
    public ModelAndView locale(HttpServletRequest req) {
    	String message = req.getLocalName() + ";" + req.getLocalAddr() + req.getLocale().getLanguage();
        return new ModelAndView("hello", "message", message);
    }            

    //test email service
    @RequestMapping("/email")
    public @ResponseBody String email(HttpServletRequest req) {
		EmailService service = new EmailService();
		NnEmail mail = new NnEmail("yiwen@teltel.com", "yiwen", NnEmail.SEND_EMAIL_SHARE, "share 9x9", NnEmail.SEND_EMAIL_SHARE, "hello", "world");
		service.sendEmail(mail);
		return "email sent";
    }            
    
    //db test
    @RequestMapping("/pdr")
    public @ResponseBody String pdr() { 
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			PdrRaw raw = new PdrRaw(1, "session1", "test");
			pm.makePersistent(raw);
		} finally {
			pm.close();
		}        
        return "OK";
    }    

    //db test through manager
    @RequestMapping("/pdr_mngt")
    public @ResponseBody String pdr_mngt() { 
		PdrRawManager rawMngr = new PdrRawManager();
		PdrRaw raw = new PdrRaw(1, "session1", "test");
		rawMngr.create(raw);
        return "OK";
    }    
        
    //cache test, set a cache, use with cache_delete
	@RequestMapping("cache_set")
	public ResponseEntity<String> cache_set() {
		String output = "No Cache";
		MemcachedClient c;
		try {
			c = new MemcachedClient(new InetSocketAddress("localhost", CacheFactory.PORT_DEFAULT));
			output = "original: " + (String)c.get("hello") + "\n";			
			c.set("hello", CacheFactory.EXP_DEFAULT, "9x9");
			output += "after set cache: " + (String)c.get("hello");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NnNetUtil.textReturn(output);
	}
	
	@RequestMapping("queue_db") 
	public ResponseEntity<String> queue_db() {
		PlayerApiService service = new PlayerApiService();
		service.addMsoInfoVisitCounter("9x9");
		return NnNetUtil.textReturn("hello");
	}
	
    @RequestMapping("/truncate")
    public @ResponseBody String truncate() { 
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			Query query = pm.newQuery("javax.jdo.query.SQL","alter table pdr_raw auto_increment=1");
			query.execute();
		} finally {
			pm.close();
		}        
        return "OK";
    }    
    
    /*
    @RequestMapping("/tx")
    public ModelAndView tx() { 
        MsoManager mngr = new MsoManager();         
        mngr.test(new Mso("meow", "meow", "a@a.com", Mso.TYPE_NN));        
        String message = "hello transaction";
        return new ModelAndView("hello", "message", message);
    } 
    */   
        
    @RequestMapping("fanout")
    public @ResponseBody String fanout(@RequestParam String exchange_name) throws IOException {
 	   ConnectionFactory factory = new ConnectionFactory();
	   Connection connection = factory.newConnection("localhost");
       Channel channel = connection.createChannel();

       channel.exchangeDeclare(exchange_name, "fanout");

       String message = "hello superman";

       channel.basicPublish(exchange_name, "", null, message.getBytes());
       System.out.println(" [x] Sent '" + message + "'");

       channel.close();
       connection.close();
       return "OK";
    }
 
    @RequestMapping("/youtube")
    public ModelAndView youtube() { 
    	Map<String, String> maps = YouTubeLib.getYouTubeEntry("nike", true);
        String msg = "thumbnail:" + maps.get("thumbnail") + "<br/>";
        msg += "title:" + maps.get("title") + "<br/>";
        msg += "description:" + maps.get("description") + "<br/>";
        msg += "status:" + maps.get("status") + "<br/><br/><br/><br/>";

    	maps = YouTubeLib.getYouTubeEntry("android", true);
        msg += "thumbnail:" + maps.get("thumbnail") + "<br/>";
        msg += "title:" + maps.get("title") + "<br/>";
        msg += "description:" + maps.get("description") + "<br/>";
        msg += "status:" + maps.get("status") + "<br/><br/><br/><br/>";
        
    	maps = YouTubeLib.getYouTubeEntry("98145fdd67deb31d", false);
        msg += "thumbnail:" + maps.get("thumbnail") + "<br/>";
        msg += "title:" + maps.get("title") + "<br/>";
        msg += "description:" + maps.get("description") + "<br/>";
        msg += "status:" + maps.get("status") + "<br/><br/><br/><br/>";

    	maps = YouTubeLib.getYouTubeEntry("98145fdd67d", false);
        msg += "thumbnail:" + maps.get("thumbnail") + "<br/>";
        msg += "title:" + maps.get("title") + "<br/>";
        msg += "description:" + maps.get("description") + "<br/>";
        msg += "status:" + maps.get("status") + "<br/>";
                
        return new ModelAndView("hello", "message", msg);
    }    
    
    //rabbitmqctl list_queues
    @RequestMapping("receive")
    public @ResponseBody String receive() throws IOException, InterruptedException {
    	String queue_name = "hello";
    	ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("localhost");
        //Connection connection = factory.newConnection();
    	Connection connection = factory.newConnection("localhost");
        Channel channel = connection.createChannel();

        //channel.queueDeclare(queue_name, false, false, false, null);
        channel.queueDeclare(queue_name, false, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queue_name, true, consumer);
        
        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
        String message = new String(delivery.getBody());
        System.out.println(" [x] Received '" + message + "'");

    	return "OK";
    }

}