package com.nncloudtv.web.json.transcodingservice;

import java.io.Serializable;
import java.util.List;

import com.nncloudtv.model.NnProgram;
import com.nncloudtv.web.api.NnStatusCode;

public class ProgramInfo implements Serializable {
    private static final long serialVersionUID = 1281119999721089935L;
    
    private String errorCode;
    private String errorReason;
    private String callback;
    private String channelId;
    private List<NnProgram> programs; 

    public ProgramInfo(String channelId) {
        this.setErrorCode(String.valueOf(NnStatusCode.ERROR));
        this.setErrorReason("Error");
        this.channelId = channelId;
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
    public String getChannelId() {
        return channelId;
    }
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    public List<NnProgram> getPrograms() {
        return programs;
    }
    public void setPrograms(List<NnProgram> programs) {
        this.programs = programs;
    }
    public String getCallback() {
        return callback;
    }
    public void setCallback(String callback) {
        this.callback = callback;
    }
      
}
