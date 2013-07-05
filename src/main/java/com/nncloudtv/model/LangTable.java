package com.nncloudtv.model;

import java.util.Arrays;
import java.util.Locale;

/**
 * Extend some day when needed. Id is always a good idea, country code etc  
 */
public class LangTable {

    public static final String LANG_ZH = "zh";
    public static final String LANG_EN = "en";
    public static final String OTHER = "other";

    public static final String EN_PROPERTIE_FILE = "messages_en_US.properties";
    public static final String ZH_PROPERTIE_FILE = "messages_zh_TW.properties";
    
    public static final String[] supportedLanguages = { LANG_ZH, LANG_EN, OTHER };
    
    public static boolean isValidLanguage(String lang) {
        
        if (lang == null)
            return false;
        
        return Arrays.asList(supportedLanguages).contains(lang);
    }
    
    public static Locale getLocale(String lang) {
        
        if (lang.equals(LANG_ZH))
            return Locale.TRADITIONAL_CHINESE;
        
        return Locale.ENGLISH;
    }
}
