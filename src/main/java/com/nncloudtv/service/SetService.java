package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.SysTag;
import com.nncloudtv.model.SysTagDisplay;
import com.nncloudtv.web.json.cms.Set;

@Service
public class SetService {
    
    protected static final Logger log = Logger.getLogger(SetService.class.getName());
    
    private SysTagManager sysTagMngr;
    private SysTagDisplayManager sysTagDisplayMngr;
    
    @Autowired
    public SetService(SysTagManager sysTagMngr, SysTagDisplayManager sysTagDisplayMngr) {
        this.sysTagMngr = sysTagMngr;
        this.sysTagDisplayMngr = sysTagDisplayMngr;
    }
    
    /** build Set from SysTag and SysTagDisplay */
    private Set setResponse(SysTag set, SysTagDisplay setMeta) {
        
        Set setResp = new Set();
        setResp.setId(set.getId());
        setResp.setMsoId(set.getMsoId());
        setResp.setDisplayId(setMeta.getId());
        setResp.setChannelCnt(setMeta.getCntChannel());
        setResp.setLang(setMeta.getLang());
        setResp.setSeq(set.getSeq());
        setResp.setTag(setMeta.getPopularTag());
        setResp.setName(NnStringUtil.revertHtml(setMeta.getName()));
        setResp.setSortingType(set.getSorting());
        
        return setResp;
    }
    
    /** find sets that owned by mso with specify display language */
    public List<Set> findByMsoIdAndLang(Long msoId, String lang) { // TODO : move, delete
        
        List<Set> results = new ArrayList<Set>();
        Set result = null;
        
        if (msoId == null) {
            return new ArrayList<Set>();
        }
        
        //List<SysTag> results = dao.findByMsoIdAndType(msoId, SysTag.TYPE_SET);
        List<SysTag> sets = sysTagMngr.findByMsoIdAndType(msoId, SysTag.TYPE_SET);
        if (sets == null || sets.size() == 0) {
            return new ArrayList<Set>();
        }
        
        SysTagDisplay setMeta = null;
        for (SysTag set : sets) {
            
            if (lang != null) {
                setMeta = sysTagDisplayMngr.findBySysTagIdAndLang(set.getId(), lang);
            } else {
                setMeta = sysTagDisplayMngr.findBySysTagId(set.getId());
            }
            
            if (setMeta != null) {
                result = setResponse(set, setMeta);
                results.add(result);
            }
        }
        
        return results;
    }
    
    /** find sets that owned by mso */
    public List<Set> findByMsoId(Long msoId) {
        
        if (msoId == null) {
            return new ArrayList<Set>();
        }
        
        return findByMsoIdAndLang(msoId, null);
    }

}
