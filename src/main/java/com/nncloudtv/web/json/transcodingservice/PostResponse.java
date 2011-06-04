package com.nncloudtv.web.json.transcodingservice;

public class PostResponse {

	private String errorCode;
	private String errorReason;
	
	public static String ERROR_CODE_SUCCESS = "0";
	public static String ERROR_CODE_FAIL = "1";

	public static String ERROR_MSG_SUCCESS = "Success";
	public static String ERROR_MSG_FAIL = "Fail";

	public PostResponse() {}
	
	public PostResponse(String errorCode, String errorReason) {
		this.errorCode = errorCode;
		this.errorReason = errorReason;
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
}
