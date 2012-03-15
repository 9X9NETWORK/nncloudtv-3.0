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
	
	public final static String HELLO = "hello";
	public final static String VISITOR_COUNTER = "visitor_counter";
	public final static String CATEGORY_CUD_RELATED = "category_cud_related";
	public final static String CHANNEL_CUD_RELATED = "channel_cud_related";	
	public final static String SET_CUD_RELATED = "set_cud_related";	
	
	public static void send(String host, String queue, String message) throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		Connection connection = factory.newConnection(host);
		
		//send message
		Channel channel = connection.createChannel();		
		channel.queueDeclare(queue, false, false, false, false, null);
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
	
	public void fanout(String host, String exchange_name, Object msg) {
	   ConnectionFactory factory = new ConnectionFactory();
       Connection connection;
       Channel channel;
		try {
		   connection = factory.newConnection(host);
	       channel = connection.createChannel();
	       channel.exchangeDeclare(exchange_name, "fanout");
	       channel.basicPublish(exchange_name, "", null, this.toByteArray(msg));	       
		   channel.close(); //finally
		   connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	public void fanout(String host, String exchange_name, String msg) throws IOException {
	   ConnectionFactory factory = new ConnectionFactory();
	   Connection connection = factory.newConnection(host);
       Channel channel = connection.createChannel();
       channel.exchangeDeclare(exchange_name, "fanout");
       String message = msg;
       if (msg == null)
    	   message = "";
       channel.basicPublish(exchange_name, "", null, message.getBytes());	       
       channel.close();
       connection.close();		              
	}
	
}