package com.nncloudtv.web;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.service.ContentWorkerService;
import com.nncloudtv.web.json.transcodingservice.ContentWorker;
import com.nncloudtv.web.json.transcodingservice.PostResponse;

@Controller
@RequestMapping("content_worker")
public class ContentWorkerController {
    protected static final Logger log = Logger.getLogger(ContentWorkerController.class.getName());
    
    private ContentWorkerService workerService;
    
    @Autowired
    public ContentWorkerController(ContentWorkerService workerService) {
        this.workerService = workerService;
    }
    
    @ExceptionHandler(Exception.class)
    public String exception(Exception e) {
        NnLogUtil.logException(e);
        return "error/blank";
    }
            
    @RequestMapping("channel_logo_update")
    public @ResponseBody PostResponse channelLogoUpdate(@RequestBody ContentWorker content, HttpServletRequest req) {
        log.info(content.toString());
        PostResponse resp = workerService.channelLogoUpdate(content);
        return resp;
    }
    
    @RequestMapping("program_logo_update")
    public @ResponseBody PostResponse programLogoUpdate(@RequestBody ContentWorker content, HttpServletRequest req) {
        log.info(content.toString());        
        PostResponse resp = workerService.programLogoUpdate(content);
        return resp;
    }

    @RequestMapping("program_video_update")
    public @ResponseBody PostResponse programVideoUpdate(@RequestBody ContentWorker content, HttpServletRequest req) {    
        log.info(content.toString());
        PostResponse resp = workerService.programVideoUpdate(content);
        return resp;
    }

}
