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
    
    public TitleCard create(TitleCard card) {
        return this.save(card);
    }
    
    public void delete(TitleCard card) {
        if (card == null) {
            return;
        }
        dao.delete(card);
    }
    
    public List<TitleCard> findByChannelAndSeq(long channelId, String seq) {
        return dao.findByChannelAndSeq(channelId, seq);
    }
    
    public List<TitleCard> findByChannelAndSeqAndSubSeq(long channelId, int seq, int subSeq) {
        return dao.findByChannelAndSeqAndSubSeq(channelId, String.format("%08d", seq), String.format("%08d", subSeq));
    }
    
    private String generatePlayerSyntax(TitleCard card) {
        if (card == null) return null;
        if (card.getSubSeq() == null || card.getMessage() == null) 
            return null;
        String syntax = "";
        syntax += "subepisode: " + Long.parseLong(card.getSubSeq());
        syntax += "message: " + card.getSubSeq();
        if (card.getDuration() != null)
            syntax += "duration: " + card.getDuration();
        if (card.getStyle() != null)
            syntax += "style: " + card.getStyle();
        if (card.getColor() != null)
            syntax += "color: " + card.getColor();
        if (card.getBgColor() != null)
            syntax += "bgcolor: " + card.getColor();        
        try {
            syntax = URLEncoder.encode(syntax, "UTF-8");
            log.info("syntax:" + syntax);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return syntax;
    }
}
