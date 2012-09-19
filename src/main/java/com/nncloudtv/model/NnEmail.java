package com.nncloudtv.model;

/** 
 * email object
 */
public class NnEmail {

    private String toEmail;
    
    private String toName;
    
    public static String SEND_EMAIL_SHARE = "share@9x9.tv";
    private String senderEmail;
    
    private String senderName;
    
    private String replyToEmail;
    
    private String subject;
    
    private String body;

    public NnEmail(String toEmail, String toName, String 
    		       senderEmail, String senderName, 
    		       String replyToEmail, String subject, String body) {
        this.toEmail = toEmail;
        this.toName = toName;
        this.senderEmail = senderEmail;
        this.senderName = senderName;        
        this.replyToEmail = replyToEmail;
        this.subject = subject;
        this.body = body;
    }
    
    public NnEmail(String subject, String body) {
        this.senderEmail = "nncloudtv@gmail.com";
        this.senderName = "nncloudtv";
        this.toEmail = "nncloudtv@gmail.com";
        this.toName = "nncloudtv";        
    }
    
    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getSendEmail() {
        return senderEmail;
    }

    public void setSendEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSendName() {
        return senderName;
    }

    public void setSendName(String senderName) {
        this.senderName = senderName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReplyToEmail() {
        return replyToEmail;
    }

    public void setReplyToEmail(String replyToEmail) {
        this.replyToEmail = replyToEmail;
    }    
    
}
