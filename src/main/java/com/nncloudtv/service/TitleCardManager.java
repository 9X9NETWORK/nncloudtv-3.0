package com.nncloudtv.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.TitleCardDao;
import com.nncloudtv.model.TitleCard;

@Service
public class TitleCardManager {

    protected static final Logger log = Logger.getLogger(TitleCardManager.class.getName());
    
    private TitleCardDao dao = new TitleCardDao();
    
    public TitleCard save(TitleCard card) {
        if (card==null) {
            return null;
        }
        Date now = new Date();
        card.setUpdateDate(now);
        card.setPlayerSyntax(this.generatePlayerSyntax(card));
        return dao.save(card);
    }
    
    public void delete(TitleCard card) {
        if (card == null) {
            return;
        }
        dao.delete(card);
    }
    
    public List<TitleCard> findByProgramId(long programId) {
        return dao.findByProgramId(programId);
    }
    
    public TitleCard findById(long id) {
        return dao.findById(id);
    }
    
    // TODO: findByEpisode()
    
    private String generatePlayerSyntax(TitleCard card) {
        if (card == null) return null;
        if (card.getMessage() == null) 
            return null;
        String syntax = "";
        String encoding = "UTF-8";
        try {
            String breakEncoding = URLEncoder.encode("\n", encoding);
            syntax += "message: " + card.getMessage() + breakEncoding;
            if (card.getType() == TitleCard.TYPE_BEGIN)
                syntax += "type: begin" + breakEncoding;
            else
                syntax += "type: end" + breakEncoding;
            if (card.getDuration() != null)
                syntax += "duration: " + card.getDuration() + breakEncoding;
            if (card.getStyle() != null)
                syntax += "style: " + card.getStyle() + breakEncoding;
            if (card.getColor() != null)
                syntax += "color: " + card.getColor() + breakEncoding;
            if (card.getBgColor() != null)
                syntax += "bgcolor: " + card.getColor() + breakEncoding;
            log.info("syntax:" + syntax);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return syntax;
    }
    
    public TitleCard findByProgramIdAndType(long programId, short type) {
    
        return dao.findByProgramIdAndType(programId, type);
    }
}
