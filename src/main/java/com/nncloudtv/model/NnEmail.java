package com.nncloudtv.model;

/** 
 * email object
 */
public class NnEmail {

    private String toEmail;
    
    private String toName;
    
    public static String SEND_EMAIL_SHARE = "share@9x9.tv";
    private String sendEmail;
    
    private String sendName;
    
    private String replyToEmail;
    
    private String subject;
    
    private String body;

    public NnEmail(String toEmail, String toName, String sendEmail, String sendName, String replyToEmail, String subject, String body) {
        this.toEmail = toEmail;
        this.toName = toName;
        this.sendEmail = sendEmail;
        this.replyToEmail = replyToEmail;
        this.sendName = sendName;
        this.subject = subject;
        this.body = body;
    }
    
    public NnEmail(String subject, String body) {
        this.sendEmail = "nncloudtv@gmail.com";
        this.sendName = "nncloudtv";
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
        return sendEmail;
    }

    public void setSendEmail(String sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
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
