package com.nncloudtv.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.dao.NnProgramDao;
import com.nncloudtv.dao.TitleCardDao;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.TitleCard;

@Service
public class TitleCardManager {

    protected static final Logger log = Logger.getLogger(TitleCardManager.class.getName());
    
    private TitleCardDao dao = new TitleCardDao();
    private NnProgramDao programDao = new NnProgramDao();
    
    public TitleCard save(TitleCard card) {
        if (card==null) {
            return null;
        }
        Date now = new Date();
        card.setUpdateDate(now);
        card.setPlayerSyntax(this.generatePlayerSyntax(card));
        return dao.save(card);
    }

    //TODO, going to remove
    public TitleCard create(TitleCard card) {
        return this.save(card);
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
    
    public List<TitleCard> findByEpisodeId(long episodeId) {
        List<NnProgram> programs = programDao.findProgramsByEpisode(episodeId);
        List<TitleCard> titleCardsFromEpisode = new ArrayList<TitleCard>();
        List<TitleCard> titleCardsFromProgram;
        for (int i=0; i<programs.size(); i++) {
            titleCardsFromProgram = findByProgramId(programs.get(i).getId());
            titleCardsFromEpisode.addAll(titleCardsFromProgram);
        }
        return titleCardsFromEpisode;
    }
    
    public TitleCard findById(long id) {
        return dao.findById(id);
    }
    
    // TODO: findByEpisode()
    
    //IMPORTANT: subepisode: (number) is moved out from here and can only retrieved from NnProgram, 
    //           since the seq could be changed often
    private String generatePlayerSyntax(TitleCard card) {
        if (card == null) return null;
        if (card.getMessage() == null) 
            return null;
        String syntax = "";
        //TODO move to player to assemble
        //syntax += "subepisode: " + Long.parseLong(card.getSubSeq());
        syntax += "message: " + card.getMessage() + "\n";
        if (card.getDuration() != null)
            syntax += "duration: " + card.getDuration() + "\n";
        if (card.getStyle() != null)
            syntax += "style: " + card.getStyle() + "\n";
        if (card.getColor() != null)
            syntax += "color: " + card.getColor() + "\n";
        if (card.getBgColor() != null)
            syntax += "bgcolor: " + card.getColor() + "\n";        
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
