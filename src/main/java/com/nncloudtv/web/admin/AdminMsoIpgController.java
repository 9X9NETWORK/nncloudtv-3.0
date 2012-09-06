package com.nncloudtv.web.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.codehaus.jackson.map.ObjectMapper;

import com.nncloudtv.lib.JqgridHelper;
import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.lib.NnNetUtil;
import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.Mso;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.MsoIpgManager;
import com.nncloudtv.service.MsoManager;

@Controller
@RequestMapping("admin/msoIpg")
public class AdminMsoIpgController {
    protected static final Logger  logger = Logger.getLogger(AdminMsoIpgController.class.getName());
    public final MsoIpgManager     ipgMngr;
    public final NnChannelManager channelMngr;
    public final MsoManager        msoMngr;
//    public final UserService       userService;
    
    @ExceptionHandler(Exception.class)
    public String exception(Exception e) {
        NnLogUtil.logException(e);
        return "error/exception";                
    }
    
    @Autowired
    public AdminMsoIpgController(MsoIpgManager ipgMngr,
                                 NnChannelManager channelMngr,
                                 MsoManager msoMngr) {
        this.ipgMngr     = ipgMngr;        
        this.channelMngr = channelMngr;
        this.msoMngr     = msoMngr;
//        this.userService = UserServiceFactory.getUserService();
    }    
    
    @RequestMapping("list")
    public ResponseEntity<String> list(@RequestParam String mso) {        
        List<MsoIpg> ipg = (List<MsoIpg>)ipgMngr.findAllByMsoId(Long.parseLong(mso));
        NnChannelManager channelMngr = new NnChannelManager();
        List<NnChannel> channels = new ArrayList<NnChannel>();
        if (ipg != null) {
            for (MsoIpg i : ipg) {
                NnChannel c = channelMngr.findById(i.getChannelId()); 
                c.setSeq(i.getSeq());
                c.setType(i.getType());
                if (c!= null) {channels.add(c);};
            }
        }
        String output = "channel_id\tchannel key\tseq\ttype\tname (type, 1=general; 2=readonly)\n";
        for (NnChannel c : channels) {
            String[] result = {
                    String.valueOf(output + c.getId()),
//                    NnStringUtil.getKeyStr(c.getKey()),
                    String.valueOf(c.getSeq()),
                    String.valueOf(c.getType()),                                        
                    c.getName()
            };
            output = NnStringUtil.getDelimitedStr(result) + "\n";
        }
        return NnNetUtil.textReturn(output);
    }
    
    @RequestMapping(value = "list", params = {"mso", "page", "rows", "sidx", "sord"})
    public void list(@RequestParam(value = "mso")  Long         msoId,
                     @RequestParam(value = "page") Integer      currentPage,
                     @RequestParam(value = "rows") Integer      rowsPerPage,
                     @RequestParam(value = "sidx") String       sortIndex,
                     @RequestParam(value = "sord") String       sortDirection,
                                                   OutputStream out) {
        
        NnChannelManager channelMngr = new NnChannelManager();
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> dataRows = new ArrayList<Map<String, Object>>();
        
        String filter = "msoId == " + msoId;
        int totalRecords = ipgMngr.total(filter);
        int totalPages = (int)Math.ceil((double)totalRecords / rowsPerPage);
        if (currentPage > totalPages)
            currentPage = totalPages;
        
        List<MsoIpg> results = ipgMngr.list(currentPage, rowsPerPage, sortIndex, sortDirection, filter);
        
        for (MsoIpg ipg : results) {
            
            Map<String, Object> map = new HashMap<String, Object>();
            List<Object> cell = new ArrayList<Object>();
            
            NnChannel channel = channelMngr.findById(ipg.getChannelId());
            
            cell.add(ipg.getChannelId());
            cell.add(channel.getName());
            cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ipg.getUpdateDate()));
            cell.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ipg.getCreateDate()));
            cell.add(ipg.getSeq());
            cell.add(ipg.getType());
            
            map.put("id", ipg.getId());
            map.put("cell", cell);
            dataRows.add(map);
        }
        
        try {
            mapper.writeValue(out, JqgridHelper.composeJqgridResponse(currentPage, totalPages, totalRecords, dataRows));
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }
    
    @RequestMapping(value="delete", params = {"id"})
    public @ResponseBody String delete(@RequestParam(value="id") Long msoIpgId) {        
        
//        logger.info("admin = " + userService.getCurrentUser().getEmail());
        
        logger.info("msoIpgId = " + msoIpgId);
        ipgMngr.deleteMsoIpg(msoIpgId);
        return "OK";
    }
    
    @RequestMapping(value="delete", params = {"mso", "channel"})
    public @ResponseBody String delete(@RequestParam(value="mso")     Long msoId,
                                       @RequestParam(value="channel") Long channelId) {        
        
//        logger.info("admin = " + userService.getCurrentUser().getEmail());
        
        logger.info("msoId = " + msoId);
        logger.info("channelId = " + channelId);
        ipgMngr.deleteMsoIpg(msoId, channelId);
        return "OK";
    }
    
    @RequestMapping(value="add")
    public @ResponseBody String add(@RequestParam(required=true, value="mso")     Long    msoId,
                                    @RequestParam(required=true, value="channel") Long    channelId,
                                    @RequestParam(required=true)                  Integer seq,
                                    @RequestParam(required=true)                  Short   type) {
        
//        logger.info("admin = " + userService.getCurrentUser().getEmail());
        
        logger.info("type = " + type);
        if (type != 1 && type != 2) {
            String error = "Invalid Type";
            logger.warning(error);
            return error;
        }
        logger.info("channelId = " + channelId);
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            String error = "Channel Does Not Exist";
            logger.warning(error);
            return error;
        }
        logger.info("msoId = " + msoId);
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            String error = "Mso Does Not Exist";
            logger.warning(error);
            return error;
        }
        if (ipgMngr.findByMsoIdAndChannelId(msoId, channelId) != null) {
            String error = "Channel Is Already Subscribed";
            logger.warning(error);
            return error;
        }
        logger.info("seq = " + seq);
        if (ipgMngr.findByMsoIdAndSeq(msoId, seq.shortValue()) != null) {
            String error = "Seq Is Already In Used";
            logger.warning(error);
            return error;
        }
        
        ipgMngr.create(new MsoIpg(msoId, channelId, seq.shortValue(), type));
        return "OK";
    }
    
    @RequestMapping(value="modify")
    public @ResponseBody String modify(@RequestParam(required=true, value="mso")     Long    msoId,
                                       @RequestParam(required=true, value="channel") Long    channelId,
                                       @RequestParam(required=false)                 Integer seq,
                                       @RequestParam(required=false)                 Short   type) {
        
//        logger.info("admin = " + userService.getCurrentUser().getEmail());
        
        logger.info("channelId = " + channelId);
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            String error = "Channel Does Not Exist";
            logger.warning(error);
            return error;
        }
        logger.info("msoId = " + msoId);
        Mso mso = msoMngr.findById(msoId);
        if (mso == null) {
            String error = "MSO Does Not Exist";
            logger.warning(error);
            return error;
        }
        MsoIpg msoIpg = ipgMngr.findByMsoIdAndChannelId(msoId, channelId);
        if (msoIpg == null) {
            String error = "Subscription Does Not Exist";
            logger.warning(error);
            return error;
        }
        if (seq != null) {
            logger.info("seq = " + seq);
            MsoIpg msoIpgMatched = ipgMngr.findByMsoIdAndSeq(msoId, seq.shortValue());
            if (msoIpgMatched != null && msoIpgMatched.getId() != msoIpg.getId()) {
                String error = "Seq Is Already In Used";
                logger.warning(error);
                return error;
            }
            msoIpg.setSeq(seq.shortValue());
        }
        if (type != null) {
            logger.info("type = " + type);
            if (type != 1 && type != 2) {
                String error = "Invalid Type";
                logger.warning(error);
                return error;
            }
            msoIpg.setType(type);
        }
        ipgMngr.save(msoIpg);
        return "OK";
    }
}
