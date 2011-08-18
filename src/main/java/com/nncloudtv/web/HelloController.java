package com.nncloudtv.web;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.PdrRaw;
import com.nncloudtv.service.MsoManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
 
@Controller
public class HelloController {
 
    @RequestMapping("/hello")
    public ModelAndView helloWorld() { 
        String message = "Hello NnCloudTv";
        return new ModelAndView("hello", "message", message);
    }    

    @RequestMapping("/tx")
    public ModelAndView tx() { 
        MsoManager mngr = new MsoManager();         
        mngr.test(new Mso("meow", "meow", "a@a.com", Mso.TYPE_NN));        
        String message = "hello transaction";
        return new ModelAndView("hello", "message", message);
    }    
    
    @RequestMapping("/pdr")
    public @ResponseBody String pdr() { 
		PersistenceManager pm = PMF.getAnalytics().getPersistenceManager();
		try {
			PdrRaw raw = new PdrRaw(1, null, 1, "w", "a");
			pm.makePersistent(raw);
		} finally {
			pm.close();
		}        
        return "OK";
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
    
    
    @RequestMapping("/mso")
    public @ResponseBody String mso() { 
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			Mso mso = new Mso("a", "a", "a@a.com", Mso.TYPE_MSO);
			pm.makePersistent(mso);
		} finally {
			pm.close();
		}        
        return "OK";
    }    
    
    @RequestMapping("fanout")
    public @ResponseBody String fanout(@RequestParam String exchange_name) throws IOException {
 	   ConnectionFactory factory = new ConnectionFactory();
       //factory.setHost("localhost");
       //Connection connection = factory.newConnection();
	   Connection connection = factory.newConnection("localhost");
       Channel channel = connection.createChannel();

       channel.exchangeDeclare(exchange_name, "fanout");

       String message = "hello";

       channel.basicPublish(exchange_name, "", null, message.getBytes());
       System.out.println(" [x] Sent '" + message + "'");

       channel.close();
       connection.close();
       return "OK";
    }
    
    @RequestMapping("send")
    public @ResponseBody String send() throws IOException {
    	String queue_name = "hello";
    	//create a connection
    	ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("localhost");
        //Connection connection = factory.newConnection();    	
        Connection connection = factory.newConnection("localhost");
        Channel channel = connection.createChannel();
        //declare a queue and publish the message
        //channel.queueDeclare(queue_name, false, false, false, null);
        channel.queueDeclare(queue_name, false, false, false, false, null);
        String message = "Hello World!";
        channel.basicPublish("", queue_name, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        //close
        channel.close();
        connection.close();
    	return "OK";
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