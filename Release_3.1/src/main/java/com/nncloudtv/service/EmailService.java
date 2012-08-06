package com.nncloudtv.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.model.NnEmail;

/**
 *  This service is for potential future use, not well structured.
 *
 */
@Service
public class EmailService {
		
	public void sendEmail(NnEmail email) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "ec2-50-112-96-199.us-west-2.compute.amazonaws.com"); 
        Session session = Session.getDefaultInstance(props, null);
        
        try {
        	Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(email.getSendEmail(), email.getSendName()));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getToEmail(), email.getToName()));                             
        	Address addr = new InternetAddress(email.getReplyToEmail(), email.getSendName()); 
        	Address addrs[] = {addr};
        	msg.setReplyTo(addrs);
            msg.setSubject(email.getSubject());
            msg.setText(email.getBody());
            Transport.send(msg);
        } catch (Exception e) {
        	NnLogUtil.logException(e);
		}					
	}	
			

}
