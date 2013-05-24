package com.nncloudtv.lib;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class NotifyLib {

    protected static final Logger log = Logger.getLogger(NotifyLib.class.getName());
    protected static String GCM_SENDER_KEY = "AIzaSyBm6bSOwftRRCX3i47gqPLPKalE3E0x6UI";
    
	public void gcmSend(String regId, String msg) {
    	log.info("gcm send:" + regId + ";msg:" + msg);
        String output = "";
        try {
            Sender sender = new Sender(GCM_SENDER_KEY);            
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Calendar cal = Calendar.getInstance();
            Message message = new Message.Builder().addData(
            		msg, dateFormat.format(cal.getTime())).build();
            output += "deviceToken " + regId + "\n";
            Result result = sender.send(message, regId, 1);
            
            /*
            Message message = new Message.Builder().build();
            Result result = sender.send(message, regId, 5);
            output += "Sent message to one device: " + result;            
            */
            
            output += result.toString();
            log.info("gcm output result:" + output);
        } catch (Exception e) {
            e.printStackTrace();
        }		
	}
}
