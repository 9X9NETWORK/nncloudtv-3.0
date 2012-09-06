package com.nncloudtv.web.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.JqgridHelper;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.Category;
import com.nncloudtv.service.CategoryManager;

@Controller
@RequestMapping("admin/category")
public class AdminCategoryController {
    
    protected static final Logger logger = Logger.getLogger(AdminCategoryController.class.getName());
    
    private final CategoryManager categoryMngr;    
    
    @Autowired
    public AdminCategoryController(CategoryManager categoryMngr) {
        this.categoryMngr = categoryMngr;
    }

    @ExceptionHandler(Exception.class)
    public String exception(Exception e) {
        NnLogUtil.logException(e);
        return "error/exception";                
    }
    
    /**
     * Add the set to the category
     * 
     * @param category category id
     * @param set set id
     * @return status in text
     */
    @RequestMapping(value="addSet")
    public @ResponseBody String addCh(
                         @RequestParam(required = false) long category,
                         @RequestParam(required = false) long ch,
                         OutputStream out) {
        return "OK";        
    }

    /**
     * Delete the set from the category
     * 
     * @param category
     * @param id
     * @return status in text
     */
    @RequestMapping(value="deleteCh")
    public @ResponseBody String deleteCh(
                         @RequestParam(required = false) long category,
                         @RequestParam(required = false) long id,
                         OutputStream out) {
        return "OK";
    }
    
    /**
     * List sets of the category
     * 
     * @param categoryId category id
     * @param currentPage current page
     * @param rowsPerPage rows per page
     * @param sortIndex sorting field
     * @param sortDirection asc or desc
     */
    //list every channel under a category    
    @RequestMapping(value = "listSet", params = {"category", "page", "rows", "sidx", "sord"})
    public void listSet(@RequestParam(value = "category") Long         categoryId,
                        @RequestParam(value = "page")     Integer      currentPage,
                        @RequestParam(value = "rows")     Integer      rowsPerPage,
                        @RequestParam(value = "sidx")     String       sortIndex,
                        @RequestParam(value = "sord")     String       sortDirection,
                        OutputStream out) {
        System.out.println("category:" + categoryId + ";page:" + currentPage + ";rows:" + 
                rowsPerPage + ";sidex:" + sortIndex + ";sortDirection:sortDirection");
        
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
        
        Category category = categoryMngr.findById(categoryId);
        if (category == null) {
            try {
                mapper.writeValue(out, JqgridHelper.composeJqgridResponse(1, 1, 0, new ArrayList<Map<String, Object>>()));
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
            return;
        }
        
        //String filter = "categoryId == " + categoryId;
        //int totalRecords = categoryMngr.totalCatToSet(filter);
        int totalRecords = 10;
        int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
        if (currentPage > totalPages)
            currentPage = totalPages;
        
        try {
            mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }
    
    /**
     * Category listing 
     * 
     * @param currentPage current page
     * @param rowsPerPage rows per page
     * @param sortIndex sort field
     * @param sortDirection asc or desc
     * @param searchField search field
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
        logger.info("==== category listing ====");
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
        
        String filter = "";
        if (searchField != null && searchOper != null && searchString != null && !searchString.isEmpty()) {
            
            if (searchField.equals("lang")) {
                searchString = NnStringUtil.escapedQuote(searchString);
            }
            
            Map<String, String> opMap = JqgridHelper.getOpMap();
            if (opMap.containsKey(searchOper)) {
                filter = searchField + " " + opMap.get(searchOper) + " " + searchString;
                logger.info("filter: " + filter);
            }
        }
        
        int totalRecords = categoryMngr.total(filter);
        int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
        if (currentPage > totalPages)
            currentPage = totalPages;
        
        List<Category> results = categoryMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
        
        for (Category category : results) {
            
            Map<String, Object> map = new HashMap<String, Object>();
            List<Object> cell = new ArrayList<Object>();
            
            //cell.add(category.getMsoId());
            cell.add(category.getId());
            //cell.add(category.getParentId());
            cell.add(category.getName());
            cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(category.getUpdateDate()));
            //cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(category.getCreateDate()));
            cell.add(category.getLang());
            cell.add(category.isPublic());
            cell.add(category.getSeq());
            cell.add(category.getChannelCnt());
            
            map.put("id", category.getId());
            map.put("cell", cell);
            dataRows.add(map);
        }
        
        try {
            mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }
    
    /**
     * Category create
     * 
     * @param name category nama
     * @param lang category language, en or zh
     * @param seq category sequence in the directory
     * @param isPublic to show in directory or not
     * @return status in text
     */
    @RequestMapping("create")     
    public @ResponseBody String create(@RequestParam(required=true) String name,
                                       @RequestParam(required=true) String lang,
                                       @RequestParam short seq,
                                       @RequestParam(required=false) String isPublic) {
        System.out.println("category create name=" + name);
        Category category = new Category(name, Boolean.parseBoolean(isPublic));
        category.setLang(lang);
        category.setPublic(Boolean.parseBoolean(isPublic));
        category = categoryMngr.save(category);
        this.adjustSeq(category, seq);
        return "OK";
    }
    
    /**
     * Category deletion
     * 
     * @param id category id
     * @return status in text
     */
    @RequestMapping("delete")
    public @ResponseBody String delete(
                    @RequestParam(required=true) Long id) {            
        Category category = categoryMngr.findById(id);
        categoryMngr.delete(category);
        List<Category> toBeSaved = new ArrayList<Category>();
        List<Category> categories = categoryMngr.findByLang(category.getLang());
        for (int i=0; i<categories.size(); i++) {
            Category c = categories.get(i);
            int seq = i+1;
            if (c.getSeq() != seq) {                
                c.setSeq((short)seq);
                toBeSaved.add(c);
            }
        }
        categoryMngr.saveAll(toBeSaved);
        return "OK";
    }
    
    private void adjustSeq(Category category, short seq) {
        Category exist = categoryMngr.findByLangAndSeq(category.getLang(), seq);
        if (exist == null) {
            category.setSeq(seq);
            categoryMngr.save(category);
            return;
        }
        List<Category> toBeSaved = new ArrayList<Category>();        
        List<Category> categories = categoryMngr.findByLang(category.getLang());
        for (int i=seq; i<categories.size(); i++) {
            Category c = categories.get(i);
            c.setSeq((short)(i+1));
            toBeSaved.add(c);
        }
        category.setSeq(seq);
        toBeSaved.add(category);
        categoryMngr.saveAll(toBeSaved);
    }
    
    /**
     * Category edit
     * 
     * @param id category id
     * @param name catetory name
     * @param isPublic to be shown in directory or not
     * @param seq sequence in the directory
     * @param channelCnt channel count
     * @param lang language, zh or en
     * @return status in text
     */
    @RequestMapping("edit")
    public @ResponseBody String edit(
            @RequestParam(required=true)  Long    id,
            @RequestParam(required=false) String  name,            
            @RequestParam(required=false) Boolean isPublic,
            @RequestParam(required=false) short  seq,
            @RequestParam(required=false) int  channelCnt,
            @RequestParam(required=false) String  lang) {        
        
        Category category = categoryMngr.findById(id);
        if (category == null) {
            String error = "Category Not Found";
            logger.warning(error);
            return error;
        }        
        if (name != null) {
            logger.info("name = " + name);
            category.setName(name);
        }
        if (isPublic != null) {
            logger.info("isPublic = " + isPublic);
            category.setPublic(isPublic);
        }
        if (lang != null) {
            logger.info("lang = " + lang);
            category.setLang(lang);
        }
        category.setChannelCnt(channelCnt);
        categoryMngr.save(category);

        Category c2 = categoryMngr.findByLangAndSeq(category.getLang(), seq);
        if (category != null && c2 != null) {
            short seq1 = category.getSeq();
            category.setSeq(seq);
            c2.setSeq(seq1);
            categoryMngr.save(category);
            categoryMngr.save(c2);
        }
        if (category != null && c2 == null) {
            category.setSeq(seq);            
            categoryMngr.save(category);
        }
        
        return "OK";
    }
    
    /*
    @RequestMapping("categoriesHtmlSelectOptions")
    public void categoriesHtmlSelectOptions(HttpServletResponse response, OutputStream out) {
        
        response.setContentType("text/html;charset=utf-8");
        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(out, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return;
        }
        List<Category> categories = categoryMngr.findAll();
        try {
            writer.write("<select>");
            for (Category category : categories)
                writer.write("<option value=\"" + category.getId() + "\">" + category.getName() + "</option>");
            writer.write("</select>");
            writer.close();
        } catch (IOException e) {
            return;
        }
    }
    */
}
