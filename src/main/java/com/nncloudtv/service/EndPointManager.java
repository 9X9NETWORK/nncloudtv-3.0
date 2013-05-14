package com.nncloudtv.service;

import java.util.Date;

import com.nncloudtv.dao.EndPointDao;
import com.nncloudtv.model.EndPoint;

public class EndPointManager {

    EndPointDao dao = new EndPointDao();
    
    public EndPoint save(EndPoint endpoint) {        
        if (endpoint == null) {
            return null;
        }
        Date now = new Date();
        endpoint.setUpdateDate(now);
        if (endpoint.getCreateDate() == null) {
            endpoint.setCreateDate(now);
        }
        endpoint = dao.save(endpoint);        
        return endpoint;
    }

    public short getVendorType(String vendor) {
        if (vendor == null) 
            return EndPoint.VENDOR_UNDEFINED;
        vendor = vendor.toLowerCase();
        if (vendor.equals("gcm"))
            return EndPoint.VENDOR_GCM;
        if (vendor.equals("apns"))
            return EndPoint.VENDOR_APNS;
        if (vendor.equals("sms"))
            return EndPoint.VENDOR_SMS;
        return EndPoint.VENDOR_UNDEFINED;
    }
    
    public EndPoint findByEndPoint(long userId, long msoId, short vendor) {
        if (vendor == 0)
            return null;
        return dao.findByEndPoint(userId, msoId, vendor);                
    }
    
    public void delete(EndPoint endpoint) {
        dao.delete(endpoint);
    }        
    
}
