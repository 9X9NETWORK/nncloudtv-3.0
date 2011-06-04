package com.nncloudtv.lib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class QueueMessage {
	protected static final Logger log = Logger.getLogger(QueueMessage.class.getName());
	
	public final static String BRAND_COUNTER = "brand_counter";
	public final static String CATEGORY_CREATE = "category_create";
	public final static String CHANNEL_CREATE_RELATED = "channel_create_related";	
	public final static String TRANSCODING_SUBMIT = "transcoding_submit";
	
	public static void send(String host, String queue, String message) throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		Connection connection = factory.newConnection();
		
		//send message
		Channel channel = connection.createChannel();
		channel.queueDeclare(queue, false, false, false, null);
	    channel.basicPublish("", queue, null, message.getBytes());
	    log.info(" [x] Sent '" + message + "'");		 
	    channel.close();
	    connection.close();		    
	}

	public byte[] toByteArray (Object obj)
	{
	   byte[] bytes = null;
	   ByteArrayOutputStream bos = new ByteArrayOutputStream();
	   try {
	      ObjectOutputStream oos = new ObjectOutputStream(bos); 
	      oos.writeObject(obj);
	      oos.flush(); 
	      oos.close(); 
	      bos.close();
	      bytes = bos.toByteArray ();
	   } catch (IOException ex) {	   
	   }
	  return bytes;
	}
	    
	public Object toObject (byte[] bytes)
	{
	    Object obj = null;
	    try {
	       ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
	       ObjectInputStream ois = new ObjectInputStream (bis);
	       obj = ois.readObject();
	    } catch (IOException ex) {	    
	    } catch (ClassNotFoundException ex) {
	    }
	    return obj;
	}	
	
	//!!!
	public void fanout(String host, String exchange_name, Object msg) {
	   ConnectionFactory factory = new ConnectionFactory();
       factory.setHost("localhost");
       Connection connection;
       Channel channel;
		try {
		   connection = factory.newConnection();
	       channel = connection.createChannel();
	       channel.exchangeDeclare(exchange_name, "fanout");
	       channel.basicPublish(exchange_name, "", null, this.toByteArray(msg));	       
		   channel.close(); //finally
		   connection.close();
	       //System.out.println(" [x] Sent '" + message + "'");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	public void fanout(String host, String exchange_name, String msg) throws IOException {
		   ConnectionFactory factory = new ConnectionFactory();
	       factory.setHost("localhost");
	       Connection connection = factory.newConnection();
	       Channel channel = connection.createChannel();
	       channel.exchangeDeclare(exchange_name, "fanout");
	       channel.basicPublish(exchange_name, "", null, msg.getBytes());	       
	       //System.out.println(" [x] Sent '" + message + "'");
	       channel.close();
	       connection.close();		
	}
	
}
