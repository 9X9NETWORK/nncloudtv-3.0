package com.nncloudtv.web.admin;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.NnContent;
import com.nncloudtv.service.NnContentManager;


@Controller
@RequestMapping("admin/content")
public class AdminContentController {

    protected static final Logger logger = Logger.getLogger(AdminContentController.class.getName());        
    
    private final NnContentManager contentMngr;
    
    @Autowired
    public AdminContentController(NnContentManager contentMngr) {
        this.contentMngr = contentMngr;
    }

    @ExceptionHandler(Exception.class)
    public String exception(Exception e) {
        NnLogUtil.logException(e);
        return "error/exception";                
    }    
    
    @RequestMapping(value="form", method=RequestMethod.GET)
    public ModelAndView formEdit(
            @RequestParam(value="id", required=false)String id,
            HttpServletRequest req) {    
        ModelAndView mv = new ModelAndView("admin/static");
        if (id != null) {
            NnContent content = contentMngr.findById(Long.parseLong(id));
            mv.addObject("key", content.getItem());
            mv.addObject("lang", content.getLang());
            String str = content.getValue(); 
            str = NnStringUtil.htmlSafeChars(str);
            mv.addObject("text", str);
        }
        return mv;
    }

    @RequestMapping(value="form", method=RequestMethod.POST)
    public String formPost(HttpServletRequest req) {            
        String key = req.getParameter("key");
        String lang = req.getParameter("lang");
        String text = req.getParameter("text");
        NnContent content = contentMngr.findByItemAndLang(key, lang);
        if (content == null) {
            content = new NnContent(key, text, lang);
        }
        if (lang == null)
            lang = LangTable.LANG_EN;
        if (!lang.equals(LangTable.LANG_EN) && !lang.equals(LangTable.LANG_ZH))
            lang = LangTable.LANG_EN;    
        
        content.setValue(text);
        content.setLang(lang);
        contentMngr.save(content);
        return "redirect:/admin/content/list";
    }
        
    @RequestMapping("list")
    public ResponseEntity<String> list() {
        //find all programs, including the not public ones
        List<NnContent> list = contentMngr.findAll();                
        String output = "<p><INPUT TYPE='button' onClick=\"parent.location='form'\" value='create'></p>";
        for (NnContent c : list) {
            output += "<p>" + "<a href='form?id=" + c.getId() + "'>" +
                      c.getId() + "<a>\t" + c.getItem() + "\t" +
                      c.getLang() + "</p>";
        }
        return NnNetUtil.htmlReturn(output);
    }    
    
}
