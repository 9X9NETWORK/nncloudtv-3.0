package com.nncloudtv.service;

public class NnStatusCode {

	// General info
	public static final int SUCCESS = 0;
	public static final int INFO = 1;
	public static final int WARNING = 2;
	public static final int FATAL = 3;
	public static final int ERROR = 4;

	// 100 input error
	public static final int INPUT_ERROR = 100;
	public static final int INPUT_MISSING = 101;
	public static final int INPUT_BAD = 102;

	// 150 output error
	public static final int OUTPUT_NO_MSG_DEFINED = 151;

	// 200 account related
	public static final int USER_ERROR = 200;
	public static final int USER_LOGIN_FAILED = 201;
	public static final int USER_EMAIL_TAKEN = 202;
	public static final int USER_INVALID = 203;
	public static final int USER_TOKEN_TAKEN = 204; 
	public static final int USER_PERMISSION_ERROR = 205;

	// 250 mso related
	public static final int MSO_ERROR = 250;
	public static final int MSO_INVALID = 251;

	// 300 channel related
	public static final int CHANNEL_ERROR = 300;
	public static final int CHANNEL_URL_INVALID = 301;
	public static final int CHANNEL_INVALID = 302;
	public static final int CHANNEL_OR_USER_INVALID = 303;
	public static final int CHANNEL_STATUS_ERROR = 304;
	public static final int CHANNEL_MAXSIZE_EXCEEDED = 305;

	// 350 subscription related
	public static final int SUBSCRIPTION_ERROR = 350;
	public static final int SUBSCRIPTION_DUPLICATE_CHANNEL = 351;
	public static final int SUBSCRIPTION_SET_OCCUPIED = 352;
	public static final int SUBSCRIPTION_RO_CHANNEL = 353;

	// 400 category related
	public static final int CATEGORY_ERROR = 400;
	public static final int CATEGORY_INVALID = 401;

	// 450 ipg related
	public static final int IPG_ERROR = 450;
	public static final int IPG_INVALID = 451;

	// 500 set related
	public static final int SET_ERROR = 460;
	public static final int SET_INVALID = 461;

	// 700 GAE error
	public static final int GAE_ERROR = 700;
	public static final int GAE_TIMEOUT = 701;

	// 800 database error
	public static final int DATABASE_ERROR = 900;
	public static final int DATABASE_TIMEOUT = 901;
	public static final int DATABASE_NEED_INDEX = 902;

}
