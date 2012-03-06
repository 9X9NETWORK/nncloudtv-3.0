package com.nncloudtv.lib;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AuthLib {
	private static int ITERATION_NUMBER = 10;
	
	public static byte[] encryptPassword(String password, byte[] bSalt) {
		byte[] bDigest = passwordDigest(password, bSalt);
		return bDigest;
	}
	
	public static byte[] generateSalt() {
		SecureRandom random = null;
		byte[] bSalt = null;				
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		bSalt = new byte[8];
		random.nextBytes(bSalt);
		return bSalt;
	}
	
	public static byte[] passwordDigest(String password, byte[] salt) {
		byte[] input = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			digest.update(salt);
			input = digest.digest(password.getBytes("UTF-8"));
			for (int i=0; i<ITERATION_NUMBER; i++) {
				digest.reset();
				input = digest.digest(input);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return input;
	}


}
