package com.nncloudtv.web.json.transcodingservice;


/**
 * Receive Podcast Program information from Transcoding Service 
 */
public class RtnProgram {
    private String action;
    private String key; //channel id
    private String errorCode;
    private String errorReason;
    private RtnProgramItem[] items;
    
    public static String ACTION_UPDATE_ITEM = "updateItem";
    public static String ACTION_UPDATE_ENCLOSURE = "updateEnclosure";    
        
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }    
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorReason() {
        return errorReason;
    }
    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }
    public RtnProgramItem[] getItems() {
        return items;
    }
    public void setItems(RtnProgramItem[] items) {
        this.items = items;
    }
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("key: " + key + ";");
        buffer.append("errorCode: " + errorCode + ";");
        buffer.append("errorReason: " + errorReason + ";");
        return buffer.toString();
    }     
}
