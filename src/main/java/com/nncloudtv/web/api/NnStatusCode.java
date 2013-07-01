package com.nncloudtv.web.api;

public class NnStatusCode {

    //General info
    public static final int SUCCESS = 0;
    public static final int INFO = 1;
    public static final int WARNING = 2;
    public static final int FATAL = 3;
    public static final int ERROR = 4;
    
    public static final int API_DEPRECATED = 50;
    public static final int API_UNDER_CONSTRUCTION = 51;
    public static final int API_FORCE_UPGRADE = 52;
    
    //100 input error
    public static final int INPUT_ERROR = 100;
    public static final int INPUT_MISSING = 101;
    public static final int INPUT_BAD = 102;
    public static final int CAPTCHA_FAILED = 110;
    public static final int CAPTCHA_EXPIRED = 111;
    public static final int CAPTCHA_TOOMANY_TRIES = 112;
    public static final int CAPTCHA_INVALID = 113;
    public static final int CAPTCHA_ERROR = 114;
    
    public static final int PIWIK_INVALID = 120;
    public static final int PIWIK_ERROR = 121;
    
    public static final int POI_INVALID = 125;
    public static final int POI_DUPLICATED = 126;
    
    public static final int DEVICE_INVALID = 130;
    
    //150 output error
    public static final int OUTPUT_NO_MSG_DEFINED = 151;

    //180 general data error
    public static final int DATA_ERROR = 180;
    
    //200 account related    
    public static final int USER_ERROR = 200;
    public static final int USER_LOGIN_FAILED = 201;
    public static final int USER_EMAIL_TAKEN = 202;
    public static final int USER_INVALID = 203;
    public static final int USER_TOKEN_TAKEN = 204; //token has associated with an account, to sign up an account, log out and singup another one. 
    public static final int USER_PERMISSION_ERROR = 205; 
    public static final int ACCOUNT_INVALID = 206;
    public static final int INVITE_INVALID = 207;
    
    //250 mso related
    public static final int MSO_ERROR = 250;
    public static final int MSO_INVALID = 251;
    
    //300 channel related
    public static final int CHANNEL_ERROR = 300;
    public static final int CHANNEL_URL_INVALID = 301;
    public static final int CHANNEL_INVALID = 302;
    public static final int CHANNEL_OR_USER_INVALID = 303;
    public static final int CHANNEL_STATUS_ERROR = 304;
    public static final int CHANNEL_MAXSIZE_EXCEEDED = 305;
    public static final int CHANNEL_NOT_FOUND = 306;
    public static final int CHANNEL_YOUTUBE_NOT_AVAILABLE = 30;
    
    public static final int PROGRAM_ERROR = 320;
    public static final int PROGRAM_INVALID = 321;
    
    //350 subscription related
    public static final int SUBSCRIPTION_ERROR = 350;
    public static final int SUBSCRIPTION_DUPLICATE_CHANNEL = 351;
    public static final int SUBSCRIPTION_SET_OCCUPIED = 352;
    public static final int SUBSCRIPTION_DUPLICATE_SET = 353;
    public static final int SUBSCRIPTION_RO_SET = 354;
    public static final int SUBSCRIPTION_POS_OCCUPIED = 355;
    
    //400 category related
    public static final int CATEGORY_ERROR = 400;
    public static final int CATEGORY_INVALID = 401;
    
    //450 ipg related
    public static final int IPG_ERROR = 450;
    public static final int IPG_INVALID =451;
    
    //500 set related
    public static final int SET_ERROR = 500;
    public static final int SET_INVALID =501;

    public static final int TAG_ERROR = 510;
    public static final int TAG_INVALID =511;
    
    //700 server error
    public static final int SERVER_ERROR = 700;
    public static final int SERVER_TIMEOUT = 701;
    
    //900 database error
    public static final int DATABASE_ERROR = 900;
    public static final int DATABASE_TIMEOUT = 901;
    public static final int DATABASE_NEED_INDEX = 902;
    public static final int DATABASE_READONLY = 903;

}
