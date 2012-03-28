package com.nncloudtv.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.spy.memcached.MemcachedClient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.jdbc.CommunicationsException;
import com.nncloudtv.dao.NnChannelDao;
import com.nncloudtv.lib.CacheFactory;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.lib.YouTubeLib;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnEmail;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.Pdr;
import com.nncloudtv.service.EmailService;
import com.nncloudtv.service.MsoConfigManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnSetManager;
import com.nncloudtv.service.PdrManager;
import com.nncloudtv.service.PlayerApiService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
 
@Controller
@RequestMapping("hello")
public class HelloController {
	
	//basic test
    @RequestMapping("world")
    public ModelAndView world(HttpServletRequest req) throws Exception {
		HttpSession session = req.getSession();
		session.setMaxInactiveInterval(1);
		for (long i=0; i<500000000; i++) {
			System.out.println(i);
			for (long j=0; j<500000000; j++) {			
			}			
		}
        String message = "Hello NnCloudTv";
        return new ModelAndView("hello", "message", message);
    }    

    /*
    @RequestMapping("root")
    public ModelAndView root() { 
        String message = "Hello NnCloudTv";
        return new ModelAndView("hello", "message", message);
    } 
    */   
    
    @RequestMapping("locale")
    public ModelAndView locale(HttpServletRequest req) {
    	String message = req.getLocalName() + ";" + req.getLocalAddr() + req.getLocale().getLanguage();
        return new ModelAndView("hello", "message", message);
    }            
    
    @RequestMapping("search")
    public @ResponseBody String search(
    		@RequestParam String text,
    		HttpServletRequest req) {
    	List<NnChannel> channels = NnChannelDao.searchChannelEntries(text);
    	String result = "size:" + channels.size();
    	for (NnChannel c : channels) {
    		result += c.getId() + ";" + c.getName() + "<br/>";
    	}
		return result;
    }            
    
    //test email service
    @RequestMapping("email")
    public @ResponseBody String email(
    		@RequestParam String toEmail, 
    		@RequestParam String toName, 
    		HttpServletRequest req) {
		EmailService service = new EmailService();
		NnEmail mail = new NnEmail(toEmail, toName, NnEmail.SEND_EMAIL_SHARE, "share 9x9", NnEmail.SEND_EMAIL_SHARE, "hello", "world");
		service.sendEmail(mail);
		return "email sent";
    }            
    
    //db test
    @RequestMapping("pdr")
    public @ResponseBody String pdr() throws CommunicationsException {
    	try {
			PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
			try {
				Pdr raw = new Pdr(1, "session1", "test");
				pm.makePersistent(raw);
			} finally {
				pm.close();
			}
    	} catch (Exception e){
    		throw new CommunicationsException(null, 0, 0, e);
    	}
        return "OK";
    }    

    //db test through manager
    @RequestMapping("pdr_mngt")
    public @ResponseBody String pdr_mngt() { 
		PdrManager rawMngr = new PdrManager();
		Pdr raw = new Pdr(1, "session1", "test");
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
	
	@RequestMapping("queue_visitor") 
	public ResponseEntity<String> queue_db() {
		boolean status = MsoConfigManager.isQueueEnabled(true);
		PlayerApiService service = new PlayerApiService();
		int cnt = service.addMsoInfoVisitCounter(false);
		return NnNetUtil.textReturn(String.valueOf(status) + ";" + cnt);
	}

	@RequestMapping("queue_addChToSet") 
	public ResponseEntity<String> queue_addChToSet(
			@RequestParam String sid,
			@RequestParam String cid) {
		boolean status = MsoConfigManager.isQueueEnabled(true);
		NnSetManager setMngr = new NnSetManager();
		NnSet set = setMngr.findById(Long.parseLong(sid));
		List<NnChannel> channels = new ArrayList<NnChannel>();
		channels.add(new NnChannelManager().findById(Long.parseLong(cid)));
		setMngr.addChannels(set, channels);
		return NnNetUtil.textReturn(String.valueOf(status));
	}
	        
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
 
    @RequestMapping("youtube")
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