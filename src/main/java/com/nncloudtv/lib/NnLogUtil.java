package com.nncloudtv.lib;

import java.util.logging.Logger;

public class NnLogUtil {
    
    protected final static Logger log = Logger.getLogger(NnLogUtil.class.getName());
            
    public static void logException(Exception e) {
        
        if (e == null) {
            return;
        }
        
        String detail = "";
        StackTraceElement[] elements = e.getStackTrace();
        for (StackTraceElement elm:elements ) {
            detail = detail + elm.toString() + "\n";            
        }
        log.severe("exception:" + e.toString());
        log.severe("exception stacktrace:\n" + detail);        
    }

    public static void logThrowable(Throwable t) {
        String detail = "";
        StackTraceElement[] elements = t.getStackTrace();
        for (StackTraceElement elm:elements ) {
            detail = detail + elm.toString() + "\n";            
        }
        log.severe("exception:" + t.toString());
        log.severe("exception stacktrace:\n" + detail);        
    }
    
}
