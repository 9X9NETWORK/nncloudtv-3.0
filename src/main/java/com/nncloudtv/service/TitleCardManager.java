package com.nncloudtv.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.TitleCardDao;
import com.nncloudtv.model.NnEpisode;
import com.nncloudtv.model.NnProgram;
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
        //card.setPlayerSyntax(this.generatePlayerSyntax(card));
        card = dao.save(card);
        new NnProgramManager().resetCache(card.getChannelId());        
        return card;
    }
    
    public void delete(TitleCard card) {
        if (card == null) {
            return;
        }
        dao.delete(card);
        new NnProgramManager().resetCache(card.getChannelId());
    }
    
    public void delete(List<TitleCard> titlecards) {
        
        if (titlecards == null || titlecards.size() == 0) {
            return;
        }
        
        List<Long> channelIds = new ArrayList<Long>();
        
        for (TitleCard titlecard : titlecards) {
            
            if (channelIds.indexOf(titlecard.getChannelId()) < 0) {
                channelIds.add(titlecard.getChannelId());
            }
        }
        
        dao.deleteAll(titlecards);
        
        NnProgramManager programMngr = new NnProgramManager();
        
        log.info("channel count = " + channelIds.size());
        for (Long channelId : channelIds) {
            programMngr.resetCache(channelId);
        }
    }
    
    public List<TitleCard> findByProgramId(long programId) {
        return dao.findByProgramId(programId);
    }
    
    public TitleCard findById(long id) {
        return dao.findById(id);
    }
    
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
    
    public List<TitleCard> findByEpisodeId(long episodeId) {
        
        NnProgramManager programMngr = new NnProgramManager();
        List<NnProgram> programs = programMngr.findByEpisodeId(episodeId);
        if (programs.size() == 0) {
            return new ArrayList<TitleCard>();
        }
        HashMap<Long, NnProgram> programMap = new HashMap<Long, NnProgram>();
        for (NnProgram program : programs) {
            programMap.put(program.getId(), program);
        }
        
        NnEpisodeManager episodeMngr = new NnEpisodeManager();
        NnEpisode episode = episodeMngr.findById(episodeId);
        if (episode == null) {
            return new ArrayList<TitleCard>();
        }
        
        List<TitleCard> titleCardsFromEpisode = new ArrayList<TitleCard>();
        List<TitleCard> titleCardsFromChannel = dao.findByChannel(episode.getChannelId());
        for (TitleCard titleCard : titleCardsFromChannel) {
            if (programMap.containsKey(titleCard.getProgramId())) {
                titleCardsFromEpisode.add(titleCard);
            }
        }
        
        return titleCardsFromEpisode;
    }
}
