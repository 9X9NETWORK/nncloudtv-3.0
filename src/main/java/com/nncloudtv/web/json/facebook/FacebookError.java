package com.nncloudtv.web.json.facebook;

import java.io.Serializable;

public class FacebookError implements Serializable {
    private static final long serialVersionUID = -2144615533057273573L;
    
    private String message;
    private String type;
    private int code;
    private int error_subcode;
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
    public int getError_subcode() {
        return error_subcode;
    }
    public void setError_subcode(int error_subcode) {
        this.error_subcode = error_subcode;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
}
