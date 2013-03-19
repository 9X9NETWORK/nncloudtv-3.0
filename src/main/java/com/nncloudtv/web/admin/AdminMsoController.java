package com.nncloudtv.web.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.JqgridHelper;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.service.MsoManager;
import com.nncloudtv.service.NnUserManager;

@Controller
@RequestMapping("admin/mso")
public class AdminMsoController {

    protected static final Logger log = Logger.getLogger(AdminMsoController.class.getName());

    public final MsoManager  msoMngr;
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exception(Exception e) {
        return NnNetUtil.textReturn("exception");
    }

    @Autowired
    public AdminMsoController(MsoManager msoMngr) {
        this.msoMngr = msoMngr;        
    }    
        
    /**
     * Mso creation
     * 
     * @param name mso name
     * @param contactEmail contact email
     * @param password password
     * @param logoUrl logo image url
     * @param type mso type
     * @return status in text
     */
    @RequestMapping(value = "create", params = {"name", "contactEmail", "password", "logoUrl", "type"})
    public @ResponseBody String create(@RequestParam String name,
                                       @RequestParam String contactEmail,
                                       @RequestParam String password,
                                       @RequestParam String logoUrl,
                                       @RequestParam Short  type,
                                       HttpServletRequest req) {
        log.info("name = " + name);
        log.info("contactEmail = " + contactEmail);
        log.info("password = " + password);
        log.info("logoUrl = " + logoUrl);
        log.info("type = " + type);
        
        NnUserManager userMngr = new NnUserManager();
        MsoManager msoMngr = new MsoManager();
        
        Mso found = msoMngr.findByName(name);
        if (found != null) {
            return "Name In Used";
        }
        short userType;
        if (type == Mso.TYPE_3X3) {
            userType = NnUser.TYPE_3X3;
        } else if (type == Mso.TYPE_ENTERPRISE) {
            userType = NnUser.TYPE_ENTERPRISE;
        } else {
            return "Only Type 3x3 Is Applicable";
        }
        
        Mso mso = new Mso(name, name, contactEmail, type);
        mso.setTitle(name);
        mso.setLang(LangTable.LANG_EN);
        mso.setLogoUrl(logoUrl);
        msoMngr.save(mso);
        
        NnUser user = new NnUser(contactEmail, password, name, userType, mso.getId());
        userMngr.create(user, req, NnUser.SHARD_DEFAULT);
                
        return "OK";
    }

    /**
     * Mso listing
     * 
     * @param currentPage current page
     * @param rowsPerPage rows per page
     * @param sortIndex sort field
     * @param sortDirection asc or desc
     * @param searchField search field name
     * @param searchOper search condition
     * @param searchString search string
     */
    @RequestMapping(value = "list", params = {"page", "rows", "sidx", "sord"})
    public void list(@RequestParam(value = "page")   Integer      currentPage,
                     @RequestParam(value = "rows")   Integer      rowsPerPage,
                     @RequestParam(value = "sidx")   String       sortIndex,
                     @RequestParam(value = "sord")   String       sortDirection,
                     @RequestParam(required = false) String       searchField,
                     @RequestParam(required = false) String       searchOper,
                     @RequestParam(required = false) String       searchString,
                                                     OutputStream out) {
        
        NnUserManager userMngr = new NnUserManager();
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
        
        String filter = "";
        if (searchField != null && searchOper != null && searchString != null && !searchString.isEmpty()) {            
            Map<String, String> opMap = JqgridHelper.getOpMap();
            if (opMap.containsKey(searchOper)) {
                filter = searchField + " " + opMap.get(searchOper) + " " + searchString;
                log.info("filter: " + filter);
                sortIndex = "updateDate";
                sortDirection = "desc";
            }
        }
        
        int totalRecords = msoMngr.total(filter);
        int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
        if (currentPage > totalPages)
            currentPage = totalPages;
        
        List<Mso> results = msoMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
        
        for (Mso mso : results) {
            
            Map<String, Object> map = new HashMap<String, Object>();
            List<Object> cell = new ArrayList<Object>();
            
            cell.add(mso.getLogoUrl());
            cell.add(mso.getId());
            cell.add(mso.getName());
            cell.add(mso.getTitle());
            cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(mso.getUpdateDate()));
            cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(mso.getCreateDate()));
            cell.add(mso.getJingleUrl());
            cell.add(mso.getType());
            cell.add(mso.getLang());
            cell.add(mso.getContactEmail());
            cell.add("********");
            cell.add(mso.getIntro());
            cell.add(userMngr.total("msoId == " + mso.getId() + " && email != '" + NnUser.GUEST_EMAIL + "'"));
            
            map.put("id", mso.getId());
            map.put("cell", cell);
            dataRows.add(map);
        }
        
        try {
            mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }
    
    @RequestMapping("msoHtmlSelectOptions")
    public void msoHtmlSelectOptions(HttpServletResponse response, OutputStream out) {        
        response.setContentType("text/html;charset=utf-8");
        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(out, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return;
        }
        List<Mso> msoList = msoMngr.findAll();
        try {
            writer.write("<select>");
            writer.write("<option selected=\"selected\" value=\"0\">None</option>");
            for (Mso mso : msoList) {
                writer.write("<option value=\"" + mso.getId() + "\">" + mso.getName() + "</option>");
            }
            writer.write("</select>");
            writer.close();
        } catch (IOException e) {
            return;
        }
    }
    
    /**
     * Mso editing
     * 
     * @param id mso id
     * @param name mso name
     * @param title mso title
     * @param contactEmail contact email
     * @param intro mso description
     * @param lang mso default language, en or zh
     * @param logoUrl mso logo image url
     * @param jingleUrl mso jingle url
     * @return
     */
    @RequestMapping("modify")
    public @ResponseBody String modify(@RequestParam(required=true)  Long   id,
                                       @RequestParam(required=false) String name,
                                       @RequestParam(required=false) String title,
                                       @RequestParam(required=false) String contactEmail,
                                       @RequestParam(required=false) String intro,
                                       @RequestParam(required=false) String lang,
                                       @RequestParam(required=false) String logoUrl,
                                       @RequestParam(required=false) String jingleUrl) {
        log.info("msoId = " + id);
        Mso mso = msoMngr.findById(id);
        if (mso == null) {
            String error = "MSO Not Found";
            log.warning(error);
            return error;
        }
        
        if (name != null) {
            log.info("name = " + name);
            mso.setName(name);
        }
        if (name != null) {
            log.info("title = " + title);
            mso.setTitle(title);
        }
        if (contactEmail != null) {
            log.info("contactEmail = " + contactEmail);
            mso.setContactEmail(contactEmail);
        }
        if (intro != null) {
            log.info("intro = " + intro);
            mso.setIntro(intro);
        }
        if (lang != null) {
            log.info("preferredLangCode = " + lang);
            mso.setLang(lang);
        }
        if (logoUrl != null) {
            log.info("logoUrl = " + logoUrl);
            mso.setLogoUrl(logoUrl);
        }
        if (jingleUrl != null) {
            log.info("jingleUrl = " + jingleUrl);
            mso.setJingleUrl(jingleUrl);
        }
        
        msoMngr.save(mso);
        return "OK";
    }

    /*
    @RequestMapping("create")
    public @ResponseBody String create(@RequestParam(value="name")String name, @RequestParam(value="intro")String intro,
                                       @RequestParam(value="contactEmail")String contactEmail) {
        System.out.println("contact:" + contactEmail);
        Mso mso = new Mso(name, intro, contactEmail, Mso.TYPE_MSO);        
        msoMngr.save(mso);
        return "OK";
    }
    */
    
}
