package com.nncloudtv.web.json.facebook;

import java.io.Serializable;

public class FacebookError implements Serializable {
    private static final long serialVersionUID = -2144615533057273573L;
    
    private String message;
    private String type;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
