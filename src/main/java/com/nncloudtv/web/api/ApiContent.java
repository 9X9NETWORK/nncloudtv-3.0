package com.nncloudtv.web.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nncloudtv.lib.NnStringUtil;
import com.nncloudtv.model.Category;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnChannelPref;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.model.TitleCard;
import com.nncloudtv.service.CategoryManager;
import com.nncloudtv.service.NnChannelManager;
import com.nncloudtv.service.NnChannelPrefManager;
import com.nncloudtv.service.NnProgramManager;
import com.nncloudtv.service.TitleCardManager;

@Controller
@RequestMapping("api")
public class ApiContent extends ApiGeneric {
	
	protected static Logger log = Logger.getLogger(ApiContent.class.getName());
	
	@RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.DELETE)
	public @ResponseBody
	String facebookAutosharingDelete(HttpServletRequest req,
	        HttpServletResponse resp,
	        @PathVariable("channelId") String channelIdStr) {
		
		Long channelId = null;
		try {
			channelId = Long.valueOf(channelIdStr);
		} catch (NumberFormatException e) {
		}
		if (channelId == null) {
			notFound(resp, INVALID_PATH_PARAMETER);
			return null;
		}
		
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel channel = channelMngr.findById(channelId);
		if (channel == null) {
			notFound(resp, "Channel Not Found");
			return null;
		}
		
		NnChannelPref pref = null;
		NnChannelPrefManager prefMngr = new NnChannelPrefManager();
		
		// fbUserId
		pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_USER_ID);
		if (pref != null) {
			prefMngr.delete(pref);
		}
		
		// accessToken
		pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_TOKEN);
		if (pref != null) {
			prefMngr.delete(pref);
		}
		
		return "OK";
	}
	
	@RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.POST)
	public @ResponseBody
	String facebookAutosharingCreate(HttpServletRequest req,
	        HttpServletResponse resp,
	        @PathVariable("channelId") String channelIdStr) {
		
		Long channelId = null;
		try {
			channelId = Long.valueOf(channelIdStr);
		} catch (NumberFormatException e) {
		}
		if (channelId == null) {
			notFound(resp, INVALID_PATH_PARAMETER);
			return null;
		}
		
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel channel = channelMngr.findById(channelId);
		if (channel == null) {
			notFound(resp, "Channel Not Found");
			return null;
		}
		
		String fbUserId = req.getParameter("userId");
		String accessToken = req.getParameter("accessToken");
		if (fbUserId == null || accessToken == null) {
			
			badRequest(resp, MISSING_PARAMETER);
			return null;
		}
		
		NnChannelPref pref = null;
		NnChannelPrefManager prefMngr = new NnChannelPrefManager();
		
		// fbUserId
		pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_USER_ID);
		if (pref != null) {
			pref.setValue(fbUserId);
		} else {
			pref = new NnChannelPref(channel, NnUserPref.FB_USER_ID, fbUserId);
		}
		prefMngr.save(pref);
		
		// accessToken
		pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_TOKEN);
		if (pref != null) {
			pref.setValue(accessToken);
		} else {
			pref = new NnChannelPref(channel, NnUserPref.FB_TOKEN, accessToken);
		}
		prefMngr.save(pref);
		
		return "OK";
	}
	
	@RequestMapping(value = "channels/{channelId}/autosharing/facebook", method = RequestMethod.GET)
	public @ResponseBody
	Map<String, Object> facebookAutosharing(HttpServletRequest req, HttpServletResponse resp,
	        @PathVariable("channelId") String channelIdStr) {
		
		Long channelId = null;
		try {
			channelId = Long.valueOf(channelIdStr);
		} catch (NumberFormatException e) {
		}
		if (channelId == null) {
			notFound(resp, INVALID_PATH_PARAMETER);
			return null;
		}
		
		NnChannelManager channelMngr = new NnChannelManager();
		NnChannel channel = channelMngr.findById(channelId);
		if (channel == null) {
			notFound(resp, "Channel Not Found");
			return null;
		}
		
		NnChannelPrefManager prefMngr = new NnChannelPrefManager();
		NnChannelPref pref = null;
		Map<String, Object> result = new TreeMap<String, Object>();
		String fbUserId = null;
		String accessToken = null;
		
		// fbUserId
		pref = prefMngr.findByChannelIdAndItem(channelId,NnUserPref.FB_USER_ID);
		if (pref != null) {
			fbUserId = pref.getValue();
			result.put("userId", fbUserId);
		}
		
		// accessToken
		pref = prefMngr.findByChannelIdAndItem(channelId, NnUserPref.FB_TOKEN);
		if (pref != null) {
			accessToken = pref.getValue();
			result.put("accessToken", accessToken);
		}
		
		if (accessToken != null && fbUserId != null) {
			return result;
		}
		
		return null;
	}
	
    @RequestMapping(value = "programs/{programId}", method = RequestMethod.GET)
    public @ResponseBody
    NnProgram getProgram(@PathVariable("programId") String programIdStr,
            HttpServletRequest req, HttpServletResponse resp) {

        Long programId = null;
        try {
            programId = Long.valueOf(programIdStr);
        } catch (NumberFormatException e) {
        }
        if (programId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }

        NnProgramManager programMngr = new NnProgramManager();
        NnProgram program = programMngr.findById(programId);
        if (program == null) {
            notFound(resp, "the resource program not exist");
            return null;
        }

        /*
         * program.setName(NnStringUtil.revertHtml(program.getName()));
         * program.setIntro(NnStringUtil.revertHtml(program.getIntro()));
         * program.setComment(NnStringUtil.revertHtml(program.getComment()));
         */

        return program;
    }

    @RequestMapping(value = "programs/{programId}", method = RequestMethod.PUT)
    public @ResponseBody
    NnProgram updateProgram(@PathVariable("programId") String programIdStr,
            HttpServletRequest req, HttpServletResponse resp) {

        Long programId = null;
        try {
            programId = Long.valueOf(programIdStr);
        } catch (NumberFormatException e) {
        }
        if (programId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }

        NnProgramManager programMngr = new NnProgramManager();
        NnProgram program = programMngr.findById(programId);
        if (program == null) {
            notFound(resp, "the resource program not exist");
            return null;
        }

        String name = req.getParameter("name");
        program.setName(NnStringUtil.htmlSafeChars(name));
        String intro = req.getParameter("intro");
        program.setIntro(NnStringUtil.htmlSafeChars(intro));
        String comment = req.getParameter("comment");
        program.setComment(NnStringUtil.htmlSafeChars(comment));
        String imageUrl = req.getParameter("imageUrl"); // format check ?
        program.setImageUrl(imageUrl);

        String seq = req.getParameter("seq");
        Short seqInt = null;
        try {
            seqInt = Short.valueOf(seq);
        } catch (NumberFormatException e) {
        }
        if (seqInt == null) {
            badRequest(resp, BAD_PARAMETER);
            return null;
        } else {
            String seqStr = seqInt.toString();
            program.setSeq(String.format("%08d", seqStr));
        }

        String subSeq = req.getParameter("subSeq");
        Short subSeqInt = null;
        try {
            subSeqInt = Short.valueOf(subSeq);
        } catch (NumberFormatException e) {
        }
        if (subSeqInt == null) {
            badRequest(resp, BAD_PARAMETER);
            return null;
        } else {
            String subSeqStr = subSeqInt.toString();
            program.setSubSeq(String.format("%08d", subSeqStr));
        }

        String startTime = req.getParameter("startTime"); // format check ?
        program.setStartTime(startTime);
        String endTime = req.getParameter("endTime"); // format check ?
        program.setEndTime(endTime);

        programMngr.save(program);

        program = programMngr.findById(programId);

        return program;
    }

    @RequestMapping(value = "channels/{channelId}", method = RequestMethod.GET)
    public @ResponseBody
    NnChannel getChannel(@PathVariable("channelId") String channelIdStr,
            HttpServletRequest req, HttpServletResponse resp) {

        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
        }
        if (channelId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }

        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel result = channelMngr.findById(channelId);
        if (result == null) {
            notFound(resp, "the resource channel not exist");
            return null;
        }

        return result;
    }

    @RequestMapping(value = "channels/{channelId}", method = RequestMethod.PUT)
    public @ResponseBody
    NnChannel updateChannel(@PathVariable("channelId") String channelIdStr,
            HttpServletRequest req, HttpServletResponse resp) {

        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
        }
        if (channelId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }

        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            notFound(resp, "the resource channel not exist");
            return null;
        }

        String name = req.getParameter("name");
        channel.setName(name);
        String intro = req.getParameter("intro");
        channel.setIntro(intro);
        String lang = req.getParameter("lang"); // enum check ?
        channel.setLang(lang);
        String isPublic = req.getParameter("isPublic");
        if (isPublic.equals("true")) {
            channel.setPublic(true);
        } else if (isPublic.equals("false")) {
            channel.setPublic(false);
        } else {
            // report invalid ??
        }
        String tag = req.getParameter("tag");
        channel.setTag(tag);

        channelMngr.save(channel);
        channel = channelMngr.findById(channelId);

        return channel;
    }

    @RequestMapping(value = "channels/{channelId}/programs", method = RequestMethod.GET)
    public @ResponseBody
    List<NnProgram> getProgramList(
            @PathVariable("channelId") String channelIdStr,
            HttpServletRequest req, HttpServletResponse resp) {

        class NnProgramSeqComparator implements Comparator<NnProgram> {
            public int compare(NnProgram program1, NnProgram program2) {
                int seq1 = (program1.getSeq() == null) ? 0 : Integer
                        .valueOf(program1.getSeq());
                int seq2 = (program2.getSeq() == null) ? 0 : Integer
                        .valueOf(program2.getSeq());
                if ((seq1 - seq2) == 0) {
                    int subSeq1 = (program1.getSubSeq() == null) ? 0 : Integer
                            .valueOf(program1.getSubSeq());
                    int subSeq2 = (program2.getSubSeq() == null) ? 0 : Integer
                            .valueOf(program2.getSubSeq());
                    return (subSeq1 - subSeq2);
                } else {
                    return (seq1 - seq2);
                }
            }
        }

        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
        }
        if (channelId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }

        NnProgramManager programMngr = new NnProgramManager();
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(channelId);

        if (channel == null) {
            notFound(resp, "the resource channel not exist");
            return null;
        }

        List<NnProgram> results = programMngr.findByChannel(channelId);
        Collections.sort(results, new NnProgramSeqComparator());

        return results;
    }

    @RequestMapping(value = "categories", method = RequestMethod.GET)
    public @ResponseBody
    List<Category> getCategories(HttpServletRequest req,
            HttpServletResponse resp) {

        class CategoryComparator implements Comparator<Category> {
            public int compare(Category category1, Category category2) {
                int seq1 = category1.getSeq();
                if (category1.getLang() != null
                        && category1.getLang().equalsIgnoreCase("en")) {
                    seq1 -= 100;
                }
                int seq2 = category2.getSeq();
                if (category2.getLang() != null
                        && category2.getLang().equalsIgnoreCase("en")) {
                    seq2 -= 100;
                }
                return (seq1 - seq2);
            }
        }

        String lang = req.getParameter("lang"); // enum check ?
        CategoryManager catMngr = new CategoryManager();
        List<Category> categories;

        if (lang != null) {
            categories = catMngr.findByLang(lang);
        } else {
            categories = catMngr.findAll();
        }

        Collections.sort(categories, new CategoryComparator());

        return categories;
    }

    @RequestMapping(value = "channels/{channelId}/programs/{seq}", method = RequestMethod.GET)
    public @ResponseBody
    List<NnProgram> getProgramListBySeq(
            @PathVariable("channelId") String channelIdStr,
            @PathVariable("seq") String seq, HttpServletRequest req,
            HttpServletResponse resp) {

        class NnProgramSeqComparator implements Comparator<NnProgram> {
            public int compare(NnProgram program1, NnProgram program2) {
                int subSeq1 = (program1.getSubSeq() == null) ? 0 : Integer
                        .valueOf(program1.getSubSeq());
                int subSeq2 = (program2.getSubSeq() == null) ? 0 : Integer
                        .valueOf(program2.getSubSeq());
                return (subSeq1 - subSeq2);
            }
        }

        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
        }
        if (channelId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }

        Short seqInt = null;
        try {
            seqInt = Short.valueOf(seq);
        } catch (NumberFormatException e) {
        }
        if (seqInt == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        String seqStr = seqInt.toString();
        seqStr = String.format("%08d", seqStr);

        NnProgramManager programMngr = new NnProgramManager();
        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(channelId);
        List<NnProgram> results;

        if (channel == null) {
            notFound(resp, "the resource channel not exist");
            return null;
        } else {
            results = programMngr.findByChannelAndSeq(channelId, seqStr);
            // edit note : as empty array return
            /*
             * if(results.isEmpty()) { notFound(resp,
             * "the resource channel exist but it's sequence you requested not exist"
             * ); return null; }
             */
        }

        Collections.sort(results, new NnProgramSeqComparator());

        return results;
    }

    @RequestMapping(value = "channels/{channelId}/programs/{seq}", method = RequestMethod.POST)
    public @ResponseBody
    NnProgram addProgram(@PathVariable("channelId") String channelIdStr,
            @PathVariable("seq") String seq, HttpServletRequest req,
            HttpServletResponse resp) {

        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
        }
        if (channelId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }

        Short seqInt = null;
        try {
            seqInt = Short.valueOf(seq);
        } catch (NumberFormatException e) {
        }
        if (seqInt == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        String seqStr = seqInt.toString();
        seqStr = String.format("%08d", seqStr);

        String subSeq = req.getParameter("subSeq");
        Short subSeqInt = null;
        try {
            subSeqInt = Short.valueOf(subSeq);
        } catch (NumberFormatException e) {
        }
        if (subSeqInt == null) {
            badRequest(resp, BAD_PARAMETER);
            return null;
        }
        String subSeqStr = subSeqInt.toString();
        subSeqStr = String.format("%08d", subSeqStr);

        NnChannelManager channelMngr = new NnChannelManager();
        NnChannel channel = channelMngr.findById(channelId);
        if (channel == null) {
            notFound(resp, "the resource channel not exist");
            return null;
        }

        String name = req.getParameter("name");
        String intro = req.getParameter("intro");
        String imageUrl = req.getParameter("imageUrl"); // format check ?

        NnProgram program = new NnProgram(channelId,
                NnStringUtil.htmlSafeChars(name),
                NnStringUtil.htmlSafeChars(intro), imageUrl);

        program.setSeq(seqStr);
        program.setSubSeq(subSeqStr);

        String comment = req.getParameter("comment");
        program.setComment(NnStringUtil.htmlSafeChars(comment));
        String startTime = req.getParameter("startTime"); // format check ?
        program.setStartTime(startTime);
        String endTime = req.getParameter("endTime"); // format check ?
        program.setEndTime(endTime);

        NnProgramManager programMngr = new NnProgramManager();
        programMngr.create(channel, program);

        // how to get newly created program at this moment

        return program;
    }

    @RequestMapping(value = "title_cards/{channelId}/{seq}", method = RequestMethod.GET)
    public @ResponseBody
    List<TitleCard> getTitleCards(
            @PathVariable("channelId") String channelIdStr,
            @PathVariable("seq") String seq, HttpServletRequest req,
            HttpServletResponse resp) {

        Long channelId = null;
        try {
            channelId = Long.valueOf(channelIdStr);
        } catch (NumberFormatException e) {
        }
        if (channelId == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }

        Short seqInt = null;
        try {
            seqInt = Short.valueOf(seq);
        } catch (NumberFormatException e) {
        }
        if (seqInt == null) {
            notFound(resp, INVALID_PATH_PARAMETER);
            return null;
        }
        String seqStr = seqInt.toString();
        seqStr = String.format("%08d", seqStr);

        TitleCardManager titleCardMngr = new TitleCardManager();
        List<TitleCard> results = titleCardMngr.findByChannelAndSeq(channelId,
                seqStr);

        return results;
    }

}
// program.setSeq(String.format("%08d", seq));
