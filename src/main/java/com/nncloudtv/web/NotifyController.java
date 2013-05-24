package com.nncloudtv.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NotifyLib;
import com.nncloudtv.model.EndPoint;
import com.nncloudtv.service.EndPointManager;

@Controller
@RequestMapping("notify") //@RequestMapping("gcm")
public class NotifyController {

    protected static final Logger log = Logger.getLogger(NotifyController.class.getName());

    @RequestMapping(value="send")
    public @ResponseBody String send (
    		@RequestParam(value="device", required = false) String deviceToken,
    		@RequestParam(value="msg", required = false) String msg,
            @RequestParam(value="vendor", required=false) String vendor) {
    	log.info("send:<device>" + deviceToken + " <msg>" + msg + " <vendor>" + vendor);
    	EndPointManager endpointMngr = new EndPointManager();
    	NotifyLib lib = new NotifyLib();
    	if (endpointMngr.getVendorType(vendor) == EndPoint.VENDOR_GCM) {
    		lib.gcmSend(deviceToken, msg);
     	} else if (endpointMngr.getVendorType(vendor) == EndPoint.VENDOR_APNS) {
     		
     	}
        return "OK";    	
    }
    
    //gcm testing
    @RequestMapping(value="index/unregister")
    public ResponseEntity<String> unregister(HttpServletRequest req,
    		@RequestParam(value="regId", required = false) String regId) {            
    	log.info("gcm unregister: " + regId);
        return NnNetUtil.textReturn("OK");
    }
        
    //gcm testing 
    @RequestMapping(value="index/register")
    public ResponseEntity<String> register(HttpServletRequest req,
    		@RequestParam(value="regId", required = false) String regId) {            
    	log.info("gcm register: " + regId);
        String output = "";
        try {
            String SENDER_KEY = "AIzaSyBm6bSOwftRRCX3i47gqPLPKalE3E0x6UI";
            Sender sender = new Sender(SENDER_KEY);
            
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Calendar cal = Calendar.getInstance();
            Message message = new Message.Builder().addData(
            		"message, hello world, ", dateFormat.format(cal.getTime())).build();
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
        return NnNetUtil.textReturn(output);
    }    

}