package com.nncloudtv.lib;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NnStringUtil {
	public static String capitalize(String str) {
		if (str == null) {return null;}
		str = str.toLowerCase();
		String firstLetter = str.substring(0,1);
        String remainder   = str.substring(1);
        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();
        return capitalized;
	}

	public static String getDateString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm a");
        String result = dateFormat.format(date);
        System.out.println("getUpdateDate() : UDate = " + result);
		return result;
	}

	public static String getDelimitedStr(String[] ori) {
		StringBuilder result = new StringBuilder();
		String delimiter = "\t";
		if (ori.length > 0) {
			result.append(ori[0]);
		    for (int i=1; i<ori.length; i++) {
		       result.append(delimiter);
		       result.append(ori[i]);
		    }
		}
		return result.toString();
	}
	
	public static String escapedQuote(String str) {
		
		return "'" + str.replaceAll("'", "''") + "'";
	}
	
	public static String bytesToHex(byte[] src){
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Translate string to html safe characters
	 * 
	 * @param str
	 * @return
	 */
	public static String htmlSafeChars(String str) {
		if (str == null)
			return null;
		return str.replaceAll("\n", " ")
		          .replaceAll("\t", " ")
		          .replaceAll("&", "&amp;")
		          .replaceAll("<", "&lt;")
		          .replaceAll(">", "&gt;")
		          .replaceAll("\"", "&quot;");
	}
	
}
