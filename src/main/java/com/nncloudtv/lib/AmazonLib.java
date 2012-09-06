package com.nncloudtv.lib;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.api.client.util.Base64;

public class AmazonLib {
	
	protected static final Logger log = Logger.getLogger(AmazonLib.class.getName());
	
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static final String AWS_KEY = "fdXkONXak8YC8TylX7fVSte5nvhFJ0RB7KnXGhpl";
	
	public static final String AWS_ID = "AKIAIUZXV6X5RKSG3QRQ";
	public static final String AWS_TOKEN = "QWJvdXQtTWU=";
	public static final String S3_TOKEN = "YWJvdXR5b3U=";
	public static final String S3_CONTEXT_CODE = "aHR0cDovL2dvby5nbC9lVTNtWg==";
	public static final String S3_EXT = "WW91IEFyZSBOb3QgQWxvbmU=";
	
	/**
	* Computes RFC 2104-compliant HMAC signature.
	* * @param data
	* The data to be signed.
	* @return
	* The Base64-encoded RFC 2104-compliant HMAC signature.
	* @throws
	* java.security.SignatureException when signature generation fails
	*/
	public static String calculateRFC2104HMAC(String data) throws java.security.SignatureException {
		
		String result = null;
		
		try {
			
			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(AWS_KEY.getBytes(), HMAC_SHA1_ALGORITHM);
			
			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			
			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());
			
			// base64-encode the hmac			
			log.info(result);
			byte[] policy;
			try {
				policy = Base64.encode(rawHmac);
				result = new String(policy, "UTF8");			
			} catch (UnsupportedEncodingException e) {
				log.info("unsupported encoding:" + e.getMessage());		
			}		        						
			
		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}
	
	public static String buildS3Policy(String bucket, String acl, String contentType, long size) {
		
		String result = "";
		result += "{ 'expiration': '" + AmazonLib.getFormattedExpirationDate() + "',";
		result += "'conditions': [";
		result += "{ 'bucket': '" + bucket + "' },";
		result += "[ 'starts-with', '$key', ''],";
		result += "{ 'acl': '" + acl + "' },";
		result += "[ 'starts-with', '$Content-Type', '" + contentType + "' ],";
		result += "{ 'success_action_status': '201' },";
		result += "[ 'starts-with', '$Filename', '' ],";
		result += "[ 'content-length-range', 0, " + size + " ],";
		result += "]";
		result += "}";
		
		log.info(result);
		byte[] policy;
		String roundTrip = "";
		try {
			policy = Base64.encode(result.getBytes("UTF8"));
			roundTrip = new String(policy, "UTF8");
		} catch (UnsupportedEncodingException e) {
			log.info("unsupported encoding:" + e.getMessage());
		}
		return roundTrip;
	}
	
	public static String buildS3Policy(String buket, String acl, String contentType) {
		
		String result = "";
		result += "{ 'expiration': '" + AmazonLib.getFormattedExpirationDate() + "',";
		result += "'conditions': [";
		result += "{ 'bucket': '" + buket + "' },";
		result += "[ 'starts-with', '$key', ''],";
		result += "{ 'acl': '" + acl + "' },";
		result += "[ 'starts-with', '$Content-Type', '" + contentType + "' ],";
		result += "{ 'success_action_status': '201' },";
		result += "[ 'starts-with', '$Filename', '' ],";
		result += "[ 'content-length-range', 0, 1073741824 ],"; // 1 GB
		result += "]";
		result += "}";
		
		log.info(result);
		byte[] policy;
		String roundTrip = "";
		try {
			policy = Base64.encode(result.getBytes("UTF8"));
			roundTrip = new String(policy, "UTF8");			
		} catch (UnsupportedEncodingException e) {
			log.info("unsupported encoding:" + e.getMessage());		
		}		        
		return roundTrip;
	}
	
	public static String decodeS3Token(String token, Date timestamp, byte[] salt, long rand) throws IOException {
		
		byte[] encrypt = AuthLib.encryptPassword(token, salt);
		String pattern = timestamp.toString() + String.valueOf(rand);
		int radius = pattern.length();
		String perimeter = pattern.substring(radius) + token;
		int len1 = encrypt.length, len2 = salt.length;
		if (len1 < len2) {
			encrypt[(len2 % len1)] = salt[len1];
		} else {
			salt[(len1 % len2)] = encrypt[len2 - 1];
		}
		log.info("length = " + perimeter.length());
		
		return org.datanucleus.util.Base64.decodeString(perimeter);
	}
	
	public static String getFormattedExpirationDate() {
		Date now = new Date();
		Date oneHourFromNow = new Date(now.getTime() + 3600 * 1000);
		TimeZone tz = TimeZone.getTimeZone( "UTC" );
		SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dfm.setTimeZone(tz);
		String formattedExpirationDate = dfm.format(oneHourFromNow);
		return formattedExpirationDate;
	}
	
}
