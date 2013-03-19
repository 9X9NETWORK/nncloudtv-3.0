package com.nncloudtv.web;

import java.util.Comparator;
import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nncloudtv.lib.NnLogUtil;
import com.nncloudtv.model.NnProgram;

@Controller
@RequestMapping("CMSAPI")
public class CmsApiController {
    protected static final Logger log = Logger.getLogger(CmsApiController.class.getName());
    
    class NnProgramSeqComparator implements Comparator<NnProgram> {
        public int compare(NnProgram program1, NnProgram program2) {
            int seq1 = (program1.getSubSeq() == null) ? 0 : Integer.valueOf(program1.getSubSeq());
            int seq2 = (program2.getSubSeq() == null) ? 0 : Integer.valueOf(program2.getSubSeq());
            return (seq1 - seq2);
        }
    }
    
    @ExceptionHandler(Exception.class)
    public String exception(Exception e) {
        NnLogUtil.logException(e);
        return "error/blank";
    }
    
    //////////////////// NnSet Management ////////////////////
    /**
     * Search channel 
     * 
     * @param text search text
     * @return list of channel objects
     */
//    @RequestMapping("searchChannel")
//    public @ResponseBody List<NnChannel> searchChannel(@RequestParam String text) {
//        log.info("search: " + text);
//        if (text == null || text.length() == 0) {
//            log.warning("no query string");
//            return new ArrayList<NnChannel>();
//        }
//        return NnChannelManager.search(text, true);
//    }
//    
//    /**
//     * Retrieve sets owned by the mso 
//     * 
//     * @param msoId
//     * @return list of set objects
//     */
//    
//    @RequestMapping("defaultNnSetInfo")
//    public @ResponseBody NnSet defaultNnSetInfo(@RequestParam Long msoId) {
//        CmsApiService cmsService = new CmsApiService();
//        return cmsService.getDefaultNnSet(msoId);
//    }
//    
//    /**
//     * Which system category is default channel set in
//     * 
//     * @param msoId
//     * @return Category or null (if more than one categories found, return the first one)
//     */
//    @RequestMapping("defaultNnSetCategory")
//    public @ResponseBody Category defaultNnSetCategory(@RequestParam Long msoId) {
//        CmsApiService cmsService = new CmsApiService();
//        NnSet set = cmsService.getDefaultNnSet(msoId);
//        if (set == null)
//            return null;
//        Category category = cmsService.whichSystemCategoryContainingTheSet(set.getId());
//        
//        return category;
//    }
//
//    /**
//     * List all channel in mso default channel set
//     * 
//     * @param msoId mso id
//     * @param isGood find success channel or not
//     * @param setId set id
//     * @return list of set objects
//     */
//    @RequestMapping("defaultNnSetChannels")
//    public @ResponseBody List<NnChannel> defaultNnSetChannels(
//            @RequestParam(required=false) Long msoId,
//            @RequestParam(required=false) Boolean isGood,
//            @RequestParam(required=false) Long setId) {
//        CmsApiService cmsService = new CmsApiService();
//        NnSetManager setMngr = new NnSetManager();
//        NnSet channelSet = null;
//        if (msoId != null) {
//            channelSet = cmsService.getDefaultNnSet(msoId);
//        } else if (setId != null) {
//            channelSet = setMngr.findById(setId);
//        }
//        if (channelSet == null)
//            return new ArrayList<NnChannel>();
//        List<NnChannel> cadidate = cmsService.findChannelsBySet(channelSet.getId());
//        List<NnChannel> results = new ArrayList<NnChannel>();
//        CntSubscribeManager cntMngr = new CntSubscribeManager();
//        for (NnChannel channel : cadidate) {
//            if (isGood == null || !isGood || channel.getStatus() == NnChannel.STATUS_SUCCESS) {
//                channel.setSubscriptionCnt(cntMngr.findTotalCountByChannel(channel.getId()));
//                results.add(channel);
//            }
//        }
//        return results;
//    }
//    
//    /**
//     * Save set information
//     * 
//     * @param req
//     * @param setId set id
//     * @param channelIds channel ids
//     * @param imageUrl image url
//     * @param name name
//     * @param intro description
//     * @param tag tag
//     * @param lang language, en or zh
//     * @param categoryId category id it belongs to
//     * @return status in text
//     */
//    @RequestMapping("saveChannelSet")
//    public @ResponseBody String saveNnSet(HttpServletRequest req,
//                                               @RequestParam Long setId,
//                                               @RequestParam(required = false) String channelIds,
//                                               @RequestParam(required = false) String imageUrl,
//                                               @RequestParam String name,
//                                               @RequestParam String intro,
//                                               @RequestParam String tag,
//                                               @RequestParam String lang,
//                                               @RequestParam Long categoryId) {
//        
//        log.info("setId = " + setId);
//        log.info("channelIds = " + channelIds);
//        log.info("imageUrl = " + imageUrl);
//        log.info("name = " + name);
//        log.info("intro = " + intro);
//        log.info("tag = " + tag);
//        log.info("lang = " + lang);
//        log.info("categoryId = " + categoryId);
//        
//        CmsApiService cmsApiService = new CmsApiService();
//        NnSetManager channelSetMngr = new NnSetManager();
//        NnSet set = channelSetMngr.findById(setId);
//        CategoryToNnSetDao cToSDao = new CategoryToNnSetDao();
//        
//        if (set == null)
//            return "Invalid NnSetId";
//        
//        set.setName(name);
//        set.setTag(tag);
//        set.setLang(lang);
//        if (imageUrl != null) {
//            set.setImageUrl(imageUrl);
//            // TODO: channel set also needs to be processed
//        }
//        set.setIntro(intro);
//        channelSetMngr.save(set);
//        
//        List<CategoryToNnSet> cToSs = cmsApiService.whichCToSContainingTheSet(setId);
//        List<CategoryToNnSet> removable = new ArrayList<CategoryToNnSet>();
//        
//        // NOTE: channel set can only in one system category
//        for (CategoryToNnSet ccs : cToSs) {
//            if (ccs.getCategoryId() != categoryId) {
//                removable.add(ccs);
//            }
//        }
//        for (CategoryToNnSet cToS : removable) {
//            cToSDao.delete(cToS);
//            cToSs.remove(cToS);
//        }
//        
//        log.info("ccss size = " + cToSs.size());        
//        if (cToSs.isEmpty()) {
//            // create a new CategoryNnSet
//            CategoryToNnSet cToS = new CategoryToNnSet(categoryId, setId);
//            cToSDao.save(cToS);
//            // TODO: dealing with channelCount
//            log.info("create new CategoryNnSet setId = " + setId + ", categoryId = " + categoryId);
//        }
//        
//        if (channelIds != null) {
//            NnSetToNnChannelManager cscMngr = new NnSetToNnChannelManager();
//            NnChannelManager channelMngr = new NnChannelManager();
//            List<NnSetToNnChannel> list = cscMngr.findBySet(setId);
//            for (NnSetToNnChannel sToC : list) {
//                cscMngr.delete(sToC);
//            }
//            String[] split = channelIds.split(",");
//            for (int i = 0; i < split.length; i++) {
//                NnChannel channel = channelMngr.findById(Long.valueOf(split[i]));
//                if (channel == null) {
//                    log.warning("channel id does not exist: " + split[i]);
//                    continue;
//                }
//                cscMngr.create(new NnSetToNnChannel(setId, channel.getId(), (short) (i + 1)));
//            }
//        }
//        // piwik
//        PiwikLib.createPiwikSite(setId, 0);
//        return "OK";
//    }
//
//    //////////////////// Channel/Program Management ////////////////////
//    /**
//     * Retrieve podcast information
//     *   
//     * @param url
//     * @return Map<String, String> Key includes title, description, thumbnail
//     * @throws IllegalArgumentException
//     * @throws FeedException
//     * @throws IOException
//     */
//    @RequestMapping("getPodcastInfo")
//    public @ResponseBody Map<String, String> getPodcastInfo(@RequestParam String url) throws IllegalArgumentException, FeedException, IOException {
//        //URL feedUrl = new URL(url);
//        Map<String, String> result = new HashMap<String, String>();
//        SyndFeedInput input = new SyndFeedInput();
//        SyndFeed feed = input.build(new XmlReader(new URL(url)));
//        String title, description, thumbnail;
//        title = feed.getTitle();
//        description = feed.getDescription();
//        thumbnail = (feed.getImage() != null) ? feed.getImage().getUrl() : null;
//        if (title != null) {
//            result.put("title", title);
//            log.info("title = " + title);
//        }
//        if (description != null) {
//            result.put("description", description);
//            log.info("description = " + description);
//        }
//        if (thumbnail != null) {
//            result.put("thumbnail", thumbnail);
//            log.info("thumbnail = " + thumbnail);
//        } else {
//            //List<SyndEntry> entries = feed.getEntries();
//        }
//        return result;
//    }
//
//    /**
//     * List all channels owned by mso
//     * 
//     * @param msoId mso id
//     * @return list of channel objects
//     */
//    @RequestMapping("listOwnedChannels")
//    public @ResponseBody List<NnChannel> listOwnedChannels(@RequestParam Long msoId) {
//        
//        ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
//        List<NnChannel> results = new ArrayList<NnChannel>();
//        
//        log.info("msoId = " + msoId);
//        
//        class NnChannelComparator implements Comparator<NnChannel> {  // yes, I know, its a little dirty
//            public int compare(NnChannel channel1, NnChannel channel2) {
//                Date date1 = channel1.getUpdateDate();
//                Date date2 = channel2.getUpdateDate();
//                return date2.compareTo(date1);
//            }
//        }
//        
//        results = ownershipMngr.findOwnedChannelsByMsoId(msoId);
//        Collections.sort(results, new NnChannelComparator());
//        CntSubscribeManager cntMngr = new CntSubscribeManager();
//        for (NnChannel channel : results) {
//            channel.setSubscriptionCnt(cntMngr.findTotalCountByChannel(channel.getId()));
//        }
//        return results;
//    }
//
//    /**
//     * List all channel sets owned by mso. If msoId is missing, list system sets instead 
//     * @param msoId mso id
//     * @param sortby sort field
//     * @return list of set objects
//     */
//    @SuppressWarnings("unchecked")
//    @RequestMapping("listOwnedChannelSets")
//    public @ResponseBody List<NnSet> listOwnedChannelSets(
//            HttpServletResponse response,
//            @RequestParam(required=false) Long msoId,
//            @RequestParam(required=false) String sortby) {
//        
//        Long expires = Long.valueOf(24 * 60 * 60);
//        response.addHeader("Cache-Control", "private, max-age=" + expires);
//        response.addDateHeader("Expires", System.currentTimeMillis() + (expires * 1000));
//        
//        ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
//        List<NnSet> results = new ArrayList<NnSet>();
//        MsoManager msoMngr = new MsoManager();
//        CmsApiService cmsService = new CmsApiService();
//        CntSubscribeManager cntMngr = new CntSubscribeManager(); 
//        Mso nn = msoMngr.findNNMso();
//        String cacheIdString = "System.NnSets(sortby=lang)";
//        
//        if (msoId == null) {
//            
//            log.info("system channel sets");
//            msoId = nn.getId();
//            
//            if (sortby != null) {
//                if (sortby.equalsIgnoreCase("lang")) {
//                    // get from cache
//                    results = (List<NnSet>)CacheFactory.get(cacheIdString);
//                    if (results != null) {
//                        log.info("get from cache");
//                        return results;
//                    }
//                } else if (sortby.equalsIgnoreCase("reset")) {
//                    // hack
//                    log.info("remove from cache");
//                    CacheFactory.delete(cacheIdString);
//                }
//            }
//        }
//        log.info("msoId = " + msoId);
//        results = ownershipMngr.findOwnedSetsByMso(msoId);
//        for (NnSet set : results) {
//            set.setSubscriptionCnt(cntMngr.findTotalCountBySet(set.getId()));
//            Category category = cmsService.whichSystemCategoryContainingTheSet(set.getId());
//            if (category != null) {
//                log.info("found category = " + category.getId());
//                set.setLang(category.getLang());
//            }
//            // remove some unused field to reduce cached size
//            set.setIntro("");
//        }
//        class NnSetComparator implements Comparator<NnSet> {  // yes, I know, its a little dirty
//            public int compare(NnSet set1, NnSet set2) {
//                String lang1 = set1.getLang();
//                String lang2 = set2.getLang();
//                if (lang1.equalsIgnoreCase(lang2))
//                    return 0;
//                if (lang1.equalsIgnoreCase("en"))
//                    return -1;
//                else
//                    return 1;
//            }
//        }
//        if (sortby != null && sortby.equalsIgnoreCase("lang")) {
//            Collections.sort(results, new NnSetComparator());
//            if (msoId == nn.getId()) {
//                // put to cache
//                log.info("put to cache");
//                CacheFactory.set(cacheIdString, results);
//            }
//        }
//        // size() is not always > 0
//        // log.info("<<<<<<<<<< results size:" + results.size() + ";" + results.get(0).getName());
//        return results;
//    }
//
//    /**
//     * Change program public attribute 
//     * 
//     * @param programId program id
//     * @return true or false of a program's publicity
//     */
//    @RequestMapping("switchProgramPublicity")
//    public @ResponseBody Boolean switchProgramPublicity(@RequestParam Long programId) {
//        NnProgramManager programMngr = new NnProgramManager();
//        NnProgram program = programMngr.findById(programId);
//        if (program.isPublic())
//            program.setPublic(false);
//        else
//            program.setPublic(true);
//        programMngr.save(program);
//        return program.isPublic();
//    }
//    
//    /**
//     * Change channel public attribute 
//     * 
//     * @param channelId channel id
//     * @return true or false of a program's publicity
//     */    
//    @RequestMapping("switchChannelPublicity")
//    public @ResponseBody Boolean switchChannelPublicity(@RequestParam Long channelId) {
//        NnChannelManager channelMngr = new NnChannelManager();
//        NnChannel channel = channelMngr.findById(channelId);
//        if (channel.isPublic())
//            channel.setPublic(false);
//        else
//            channel.setPublic(true);
//        channelMngr.save(channel);
//        return channel.isPublic();
//    }
//    
//    /**
//     * Remove a program
//     * 
//     * @param programId program id
//     */
//    @RequestMapping("removeProgram")
//    public @ResponseBody void removeProgram(@RequestParam Long programId) {
//        log.info("programId = " + programId);
//        NnProgramManager programMngr = new NnProgramManager();
//        NnProgram program = programMngr.findById(programId);
//        if (program != null) {
//            long channelId = program.getChannelId();
//            programMngr.delete(program);
//            log.info("program deleted, reorder program sequence");
//            updateAllProgramsSeq(channelId);
//        }
//    }
//    
//    /**
//     * Remove channel's ownership of a mso, but channel itself is kept 
//     * 
//     * @param channelId channel id
//     * @param msoId mso id
//     */
//    @RequestMapping("removeChannelFromList")
//    public @ResponseBody void removeChannelFromList(@RequestParam Long channelId, @RequestParam Long msoId) {
//        
//        log.info("msoId = " + msoId + ", channelId = " + channelId);
//        ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
//        ContentOwnership ownership = ownershipMngr.findByMsoIdAndChannelId(msoId, channelId);
//        if (ownership != null) {
//            ownershipMngr.delete(ownership);
//            log.info("remove ownership");
//        }
//        NnSetToNnChannelManager cscMngr = new NnSetToNnChannelManager();
//        // remove channels in set
//        List<NnSet> channelSets = ownershipMngr.findOwnedSetsByMso(msoId);
//        for (NnSet channelSet : channelSets) {
//            List<NnSetToNnChannel> list = cscMngr.findBySet(channelSet.getId());
//            for (NnSetToNnChannel sToC : list) {
//                if (sToC.getChannelId() == channelId) {
//                    cscMngr.removeChannel(channelSet.getId(), sToC.getSeq());
//                }
//            }
//        }
//    }
//    
//    /**
//     * Retrieve program info
//     * 
//     * @param programId program id
//     * @return program object
//     */
//    @RequestMapping("programInfo")
//    public @ResponseBody NnProgram programInfo(@RequestParam Long programId) {
//        NnProgramManager programMngr = new NnProgramManager();
//        NnProgram program = programMngr.findById(programId);
//        program.setName(NnStringUtil.revertHtml(program.getName()));
//        program.setIntro(NnStringUtil.revertHtml(program.getIntro()));
//        program.setComment(NnStringUtil.revertHtml(program.getComment()));
//        return program;
//    }
//    
//    /**
//     * Retrieve channel info
//     * 
//     * @param channelId channel id
//     * @return channel object
//     */
//    @RequestMapping("channelInfo")
//    public @ResponseBody NnChannel channelInfo(@RequestParam Long channelId) {
//        NnChannelManager channelMngr = new NnChannelManager();
//        return channelMngr.findById(channelId);
//    }
//    
//    /**
//     * Create a channel by url
//     * 
//     * @param req
//     * @param sourceUrl
//     * @return channel object
//     */
//    @RequestMapping("importChannelByUrl")
//    public @ResponseBody NnChannel importChannelByUrl(HttpServletRequest req, @RequestParam String sourceUrl) {
//        
//        NnChannelManager channelMngr = new NnChannelManager();
//        sourceUrl = sourceUrl.trim();
//        log.info("import " + sourceUrl);
//        sourceUrl = channelMngr.verifyUrl(sourceUrl);
//        if (sourceUrl == null) {
//            log.warning("invalid source url");
//            return null;
//        }
//        log.info("normalized " + sourceUrl);
//        NnChannel channel = channelMngr.findBySourceUrl(sourceUrl);
//        if (channel == null) {
//            log.info("new source url");
//            channel = channelMngr.create(sourceUrl, null, req);
//            if (channel == null) {
//                log.warning("invalid source url");
//                return null;
//            }
//            channel = channelMngr.save(channel);
//            if (channel != null && channel.getContentType() != NnChannel.CONTENTTYPE_FACEBOOK) { //!!!
//                DepotService tranService = new DepotService();
//                tranService.submitToTranscodingService(channel.getId(), sourceUrl, req);
//                // piwik
//                PiwikLib.createPiwikSite(0, channel.getId());
//            }
//            channel.setTag("NEW_CHANNEL");
//        } else {
//            // piwik
//            PiwikLib.createPiwikSite(0, channel.getId());
//        }
//        
//        return channel;
//    }
//    
//    /**
//     * Create a 9x9 channel
//     * 
//     * @param sourceUrl source url
//     * @param imageUrl image url
//     * @param name name
//     * @param intro description
//     * @param tag tag
//     * @param lang language
//     * @param msoId mso id
//     * @return status in text
//     * @throws NoSuchAlgorithmException
//     */
//    @RequestMapping("addChannelByUrl")
//    public @ResponseBody String addChannelByUrl(HttpServletRequest req,
//                                                @RequestParam String sourceUrl,
//                                                @RequestParam(required = false) String imageUrl,
//                                                @RequestParam String name,
//                                                @RequestParam String intro,
//                                                @RequestParam String tag,
//                                                @RequestParam String lang,
//                                                @RequestParam Long msoId) throws NoSuchAlgorithmException {
//        
//        log.info("sourceUrl = " + sourceUrl);
//        log.info("imageUrl = " + imageUrl);
//        log.info("name = " + name);
//        log.info("intro = " + intro);
//        log.info("tag = " + tag);
//        log.info("lang = " + lang);
//        log.info("msoId = " + msoId);
//        
//        NnChannelManager channelMngr = new NnChannelManager();
//        NnChannel channel = channelMngr.findBySourceUrl(sourceUrl);
//        ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
//        MsoManager msoMngr = new MsoManager();
//        
//        Mso mso = msoMngr.findById(msoId);
//        
//        if (channel == null)
//            return "Invalid Source Url";
//        if (mso == null)
//            return "Invalid msoId";
//        
//        channel.setName(name);
//        channel.setTag(tag);
//        if (imageUrl != null) {
//            ContentWorkerService workerService = new ContentWorkerService();
//            Long timestamp = System.currentTimeMillis() / 1000L;
//            
//            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
//            String sudoChannelSource = "http://9x9.tv/channel/" + channel.getId();
//            sha1.update(sudoChannelSource.getBytes());
//            String prefix = NnStringUtil.bytesToHex(sha1.digest()) + "_" + timestamp + "_";
//            
//            log.info("prefix = " + prefix);
//            
//            channel.setImageUrl(imageUrl);
//            workerService.channelLogoProcess(channel.getId(), imageUrl, prefix, req);
//        }
//        channel.setIntro(intro);
//        // channel.setStatus(NnChannel.STATUS_SUCCESS); // default import status
//        channelMngr.save(channel);
//        
//        // submit to transcoding server again
//        if (channel != null && channel.getContentType() != NnChannel.CONTENTTYPE_FACEBOOK) { //!!!
//            DepotService tranService = new DepotService();
//            tranService.submitToTranscodingService(channel.getId(), sourceUrl, req);
//        }
//        
//        // create ownership
//        ContentOwnership ownership = ownershipMngr.findByMsoIdAndChannelId(msoId, channel.getId());
//        if (ownership == null)
//            ownershipMngr.create(new ContentOwnership(), mso, channel);
//        
//        return "OK";
//    }
//
//    /**
//     * Create program
//     * 
//     * @param programId program id
//     * @param channelId channel id
//     * @param sourceUrl source url
//     * @param imageUrl image url
//     * @param name name
//     * @param comment curator comment
//     * @param intro description
//     * @return status in text
//     * @throws NoSuchAlgorithmException
//     */
//    @RequestMapping("saveNewProgram")
//    public @ResponseBody String saveNewProgram(HttpServletRequest req,
//                                               @RequestParam Long programId,
//                                               @RequestParam Long channelId,
//                                               @RequestParam String sourceUrl,
//                                               @RequestParam(required = false) String imageUrl,
//                                               @RequestParam(required = false) String name,
//                                               @RequestParam(required = false) String comment,
//                                               @RequestParam(required = false) String intro) throws NoSuchAlgorithmException {
//        
//        log.info("programId = " + programId);
//        log.info("channelId = " + channelId);
//        log.info("sourceUrl = " + sourceUrl);
//        log.info("imageUrl = " + imageUrl);
//        log.info("name = " + name);
//        log.info("intro = " + intro);
//        log.info("comment = " + comment);
//        
//        NnProgramManager programMngr = new NnProgramManager();
//        NnChannelManager channelMngr = new NnChannelManager();
//        ContentWorkerService workerService = new ContentWorkerService();
//        
//        NnProgram program = programMngr.findById(programId);
//        if (program == null) {
//            return "Invalid programId";
//        }
//        NnChannel channel = channelMngr.findById(channelId);
//        if (channel == null) {
//            return "Invalid channelId";
//        }        
//        Long timestamp = System.currentTimeMillis() / 1000L;
//        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
//        sha1.update(sourceUrl.getBytes());
//        String prefix = NnStringUtil.bytesToHex(sha1.digest()) + "_" + timestamp + "_";
//        log.info("prefix = " + prefix);
//        
//        if (program.getContentType() == NnProgram.CONTENTTYPE_RADIO) {
//            program.setAudioFileUrl(sourceUrl);
//        } else {
//            program.setFileUrl(sourceUrl);
//        }
//        // TODO: ProgramManager.getContentTypeByUrl()
//        if (program.getContentType() == NnProgram.CONTENTTYPE_RADIO) {
//            program.setType(NnProgram.TYPE_AUDIO);
//            log.info("audio link");
//        } else if (sourceUrl.indexOf("youtube.com") == -1) {
//            // TODO: check source url is valid
//            boolean autoGeneratedLogo = (imageUrl == null) ? true : false;
//            workerService.programVideoProcess(programId, sourceUrl, prefix, autoGeneratedLogo, req);
//            program.setContentType(NnProgram.CONTENTTYPE_DIRECTLINK);
//            log.info("direct link");
//        } else {
//            // TODO: check if youtube url is valid
//            program.setContentType(NnProgram.CONTENTTYPE_YOUTUBE);
//            log.info("youtube link");
//        }
//        if (imageUrl != null) {
//            program.setImageUrl(imageUrl);
//            program.setImageLargeUrl(imageUrl);
//            workerService.programLogoProcess(program.getId(), imageUrl, prefix, req);
//        }
//        if (name != null) {
//            program.setName(NnStringUtil.htmlSafeAndTrucated(name));
//        }
//        if (intro != null) {
//            program.setIntro(NnStringUtil.htmlSafeAndTrucated(intro));
//        }
//        if (comment != null) {
//            program.setComment(NnStringUtil.htmlSafeAndTrucated(comment));
//        }
//        program.setPublic(true);
//        programMngr.create(channel, program);
//        
//        updateAllProgramsSeq(channelId);
//        
//        return "OK";
//    }
//    
//    /**
//     * Edit program
//     * 
//     * @param programId program id
//     * @param imageUrl image url
//     * @param name name 
//     * @param intro description
//     * @param comment curator comment
//     * @return status intext
//     * @throws NoSuchAlgorithmException
//     */
//    @RequestMapping("saveProgram")
//    public @ResponseBody String saveProgram(HttpServletRequest req,
//                                            @RequestParam Long programId,
//                                            @RequestParam(required = false) String imageUrl,
//                                            @RequestParam(required = false) String name,
//                                            @RequestParam(required = false) String intro,
//                                            @RequestParam(required = false) String comment) throws NoSuchAlgorithmException {
//        log.info("programId = " + programId);
//        log.info("imageUrl = " + imageUrl);
//        log.info("name = " + name);
//        log.info("intro = " + intro);
//        log.info("comment = " + comment);
//        
//        NnProgramManager programMngr = new NnProgramManager();
//        NnProgram program = programMngr.findById(programId);
//        if (program == null) {
//            return "Invalid programId";
//        }
//        if (name != null)
//            program.setName(NnStringUtil.htmlSafeAndTrucated(name));
//        if (intro != null)
//            program.setIntro(NnStringUtil.htmlSafeAndTrucated(intro));
//        if (comment != null)
//            program.setComment(NnStringUtil.htmlSafeAndTrucated(comment));
//        if (imageUrl != null) {
//            ContentWorkerService workerService = new ContentWorkerService();
//            Long timestamp = System.currentTimeMillis() / 1000L;
//            
//            String sourceUrl;
//            if (program.getFileUrl() != null)
//                sourceUrl = program.getFileUrl();
//            else if (program.getAudioFileUrl() != null)
//                sourceUrl = program.getAudioFileUrl();
//            else
//                sourceUrl = "http://9x9.tv/episode/" + program.getId();
//            
//            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
//            sha1.update(sourceUrl.getBytes());
//            String prefix = NnStringUtil.bytesToHex(sha1.digest()) + "_" + timestamp + "_";
//            
//            log.info("prefix = " + prefix);
//            
//            program.setImageUrl(imageUrl);
//            program.setImageLargeUrl(imageUrl);
//            workerService.programLogoProcess(program.getId(), imageUrl, prefix, req);
//        }
//        programMngr.save(program);
//        
//        return "OK";
//    }
//
//    /**
//     * Channel edit
//     * 
//     * @param channelId channel id
//     * @param msoId mso id
//     * @param imageUrl image url
//     * @param name name
//     * @param intro description
//     * @param tag tag
//     * @param lang language
//     * @return status in text
//     * @throws NoSuchAlgorithmException
//     */
//    @RequestMapping("saveChannel")
//    public @ResponseBody String saveChannel(HttpServletRequest req,
//                                            @RequestParam Long channelId,
//                                            @RequestParam(required = false) Long   msoId,
//                                            @RequestParam(required = false) String imageUrl,
//                                            @RequestParam(required = false) String name,
//                                            @RequestParam(required = false) String intro,
//                                            @RequestParam(required = false) String tag,
//                                            @RequestParam(required = false) String lang) throws NoSuchAlgorithmException {
//        
//        log.info("channelId = " + channelId);
//        log.info("imageUrl = " + imageUrl);
//        log.info("name = " + name);
//        log.info("intro = " + intro);
//        log.info("tag = " + tag);
//        log.info("lang = " + lang);
//        log.info("msoId = " + msoId);
//        
//        NnChannelManager channelMngr = new NnChannelManager();
//        NnChannel channel = channelMngr.findById(channelId);
//        
//        if (channel == null)
//            return "Invalid ChannelId";
//        
//        if (tag != null)
//            channel.setTag(tag);
//        if (imageUrl != null) {
//            ContentWorkerService workerService = new ContentWorkerService();
//            Long timestamp = System.currentTimeMillis() / 1000L;
//            
//            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
//            String sudoChannelSource = "http://9x9.tv/channel/" + channel.getId();
//            sha1.update(sudoChannelSource.getBytes());
//            String prefix = NnStringUtil.bytesToHex(sha1.digest()) + "_" + timestamp + "_";
//            
//            log.info("prefix = " + prefix);
//            
//            channel.setImageUrl(imageUrl);
//            workerService.channelLogoProcess(channelId, imageUrl, prefix, req);
//        }
//        
//        if (name != null)
//            channel.setName(name);
//        if (intro != null)
//            channel.setIntro(intro);
//        if (lang != null) {
//            channel.setLang(lang);
//        }
//        if (msoId != null) { // first time the channel be saved
//            channel.setPublic(true);
//        }
//        channel.setUpdateDate(new Date());
//        // channelMngr.calibrateProgramCnt(channel);
//        channelMngr.save(channel);
//        
//        //channel1 ownership
//        if (msoId != null) {
//            MsoManager msoMngr = new MsoManager();
//            Mso mso = msoMngr.findById(msoId);
//            if (mso != null) {
//                ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
//                if (ownershipMngr.findByMsoIdAndChannelId(msoId, channelId) == null) {
//                    log.info("create ownership");
//                    ownershipMngr.create(new ContentOwnership(), mso, channel);
//                }
//            } else {
//                log.warning("invalid msoId");
//            }
//        }
//        // piwik
//        PiwikLib.createPiwikSite(0, channelId);
//        return "OK";
//    }
//    
//    // this is a wrapper of updateProgramListSeq
//    private void updateAllProgramsSeq(long channelId) {
//        
//        NnProgramManager programMngr = new NnProgramManager();
//        
//        // update all episode sequence
//        List<NnProgram> programList = programMngr.findByChannel(channelId);
//        Collections.sort(programList, new NnProgramSeqComparator());
//        List<Long> programIdList = new ArrayList<Long>();
//        for (NnProgram program : programList) {
//            programIdList.add(program.getId());
//        }
//        String programListStr = Joiner.on(",").join(programIdList);
//        updateProgramListSeq(channelId, programListStr);
//        
//    }
//
//    /**
//     * Adjust program list
//     * 
//     * @param channelId channel id
//     * @param programIdList program list, separed with comma
//     * @return status in text
//     */
//    @RequestMapping("updateProgramListSeq")
//    public @ResponseBody String updateProgramListSeq(@RequestParam Long channelId, @RequestParam String programIdList) {
//        log.info("channelId: " + channelId);
//        log.info("programIdList" + programIdList);
//        
//        NnProgramManager programMngr = new NnProgramManager();
//        
//        List<Long> programIds = new ArrayList<Long>();
//        String[] splitted = programIdList.split(",");
//        for (int i = 0; i < splitted.length; i++) {
//            programIds.add(Long.valueOf(splitted[i]));
//        }
//        
//        List<Long> origProgramIds = new ArrayList<Long>();
//        List<NnProgram> origProgramList = programMngr.findByChannel(channelId);
//        if (origProgramList.size() != programIds.size()) {
//            return "SIZE_NOT_MATCH";
//        }
//        for (int i = 0; i < origProgramList.size(); i++) {
//            origProgramIds.add(origProgramList.get(i).getId());
//        }
//        if (!programIds.containsAll(origProgramIds)) {
//            return "NOT_MATCH";
//        }
//        
//        for (NnProgram program : origProgramList) {
//            int seq = programIds.indexOf(program.getId());
//            program.setSeq(String.format("%08d", seq + 1));
//        }
//        programMngr.save(origProgramList);
//        
//        // clean cache (though, programMngr.save() do it once before)
//        String cacheKey = "nnprogram(" + channelId + ")";
//        log.info("remove cached programInfo data");
//        CacheFactory.delete(cacheKey);
//        
//        return "OK";
//    }
//    /**
//     * Which system set contains this channel
//     * 
//     * @param channelId channel id
//     * @return set object
//     */ 
//    @RequestMapping("channelSystemNnSet")
//    public @ResponseBody NnSet channelSystemNnSet(@RequestParam Long channelId) {
//        CmsApiService cmsService = new CmsApiService();
//        if (channelId == null)
//            return null;
//        List<NnSet> channelSetList = cmsService.whichSystemNnSetsContainingThisChannel(channelId);
//        if (channelSetList.size() > 0)
//            return channelSetList.get(0);
//        else
//            return null;
//    }
//    
//    /**
//     * Create program with default values
//     * 
//     * @param contentType content type
//     * @return program id
//     */
//    @RequestMapping("createProgramSkeleton")
//    public @ResponseBody Long createProgramSkeleton(@RequestParam(required=false) Short contentType) {
//        NnProgramManager programMngr = new NnProgramManager();
//        
//        log.info("create program skeleton");
//        log.info("contentType: " + contentType);
//        
//        NnProgram program;        
//        if (contentType != null && contentType == NnProgram.CONTENTTYPE_RADIO) {            
//            program = new NnProgram("New Program", "New Program", NnChannel.IMAGE_RADIO_URL, NnProgram.TYPE_AUDIO);
//            program.setPublic(false);
//            program.setType(NnProgram.TYPE_AUDIO);
//            program.setContentType(NnProgram.CONTENTTYPE_RADIO);
//            programMngr.save(program);
//            
//        } else {            
//            program = new NnProgram("New Program", "New Program", NnChannel.IMAGE_WATERMARK_URL, NnProgram.TYPE_VIDEO);
//            program.setPublic(false);
//            program.setType(NnProgram.TYPE_VIDEO);
//            programMngr.save(program);
//            
//        }
//        
//        return program.getId();
//    }
//
//    /**
//     * Create channel with default values 
//     * @return channel id
//     */
//    @RequestMapping("createChannelSkeleton")
//    public @ResponseBody Long createChannelSkeleton() {
//        
//        NnChannelManager channelMngr = new NnChannelManager();        
//        NnChannel channel = new NnChannel("New Channel", "New Channel", NnChannel.IMAGE_WATERMARK_URL);
//        channel.setPublic(false);
//        channel.setStatus(NnChannel.STATUS_WAIT_FOR_APPROVAL);
//        channel.setContentType(NnChannel.CONTENTTYPE_MIXED); // a channel type in podcast does not allow user to add program in it, so change to mixed type
//        channelMngr.save(channel);
//        
//        return channel.getId();
//    }
//    
//    /**
//     * Program listing
//     * 
//     * @param channelId
//     * @return list of program objects
//     */
//    @RequestMapping("programList")
//    public @ResponseBody List<NnProgram> programList(Long channelId) {
//        
//        NnProgramManager programMngr = new NnProgramManager();
//        List<NnProgram> results = programMngr.findByChannel(channelId);
//        Collections.sort(results, new NnProgramSeqComparator());
//        
//        return results;
//    }
//    
//    ////////////////////Directory Management ////////////////////    
//    /**
//     * List all the sets under the category  
//     * 
//     * @param categoryId category id
//     * @param isPublic public sets or not
//     * @return
//     */
//    @RequestMapping("listCategoryNnSets")
//    public @ResponseBody List<NnSet> listCategoryNnSets(@RequestParam Long categoryId, @RequestParam(required=false) Boolean isPublic) {
//        CategoryManager catMngr = new CategoryManager();
//        if (isPublic != null && isPublic) {
//            return catMngr.findSetsByCategory(categoryId, true);
//        } else {
//            return catMngr.findSetsByCategory(categoryId, false);
//        }
//    }
//
//    /**
//     * List all system categories (mso in TYPE_NN)
//     * 
//     * @param parentId category parent id, default is 0
//     * @param lang language, en or zh
//     * @return list of category objects
//     */
//    @RequestMapping("systemCategories")
//    public @ResponseBody List<Category> systemCategories(@RequestParam(required=false) Long parentId,
//                                                          @RequestParam(required=false) String lang,
//                                                         HttpServletRequest request,
//                                                         HttpServletResponse response) {
//        response.addDateHeader("Expires", System.currentTimeMillis() + 3600000);
//        CategoryManager catMngr = new CategoryManager();
//        List<Category> categories = catMngr.findPublicCategories(true);
//        List<Category> results = new ArrayList<Category>();
//        long parentIdValue;
//        if (parentId == null) {
//            parentIdValue = 0;
//        } else {
//            parentIdValue = parentId.longValue();
//        }
//        for (Category category : categories) {
//            if (category.getParentId() == parentIdValue) {
//                if (lang == null)
//                    results.add(category);
//                else if (lang != null && lang.equalsIgnoreCase(category.getLang())) {
//                    results.add(category);
//                }
//            }
//        }
//        class CategoryComparator implements Comparator<Category> {
//            public int compare(Category category1, Category category2) {
//                int seq1 = category1.getSeq();
//                if (category1.getLang() != null && category1.getLang().equalsIgnoreCase("en")) {
//                    seq1 -= 100;
//                }
//                int seq2 = category2.getSeq();
//                if (category2.getLang() != null && category2.getLang().equalsIgnoreCase("en")) {
//                    seq2 -= 100;
//                }
//                return (seq1 - seq2);
//            }
//        }
//        Collections.sort(results, new CategoryComparator());
//        return results;
//    }
//    
//    ////////////////////Promotion Tools ////////////////////
//    /**
//     * Enable or disable sharing promotion information
//     * 
//     * @param msoId mso id
//     * @param type mso type
//     * @param enabled enablie sharing features
//     * @return status in text
//     */
//    @RequestMapping("setSnsAuth")
//    public @ResponseBody String setSnsAuth(@RequestParam Long msoId,
//                                            @RequestParam Short type,
//                                            @RequestParam Boolean enabled) {
//        SnsAuthManager snsMngr = new SnsAuthManager();
//        SnsAuth snsAuth = snsMngr.findMsoIdAndType(msoId, type);
//        if (snsAuth != null) {
//            snsAuth.setEnabled(enabled);
//            snsMngr.save(snsAuth);
//            return "OK";
//        } else {
//            return "NotFound";
//        }
//    }
//    
//    /**
//     * Remove sharing promotion information
//     * 
//     * @param msoId mso id 
//     * @param type mso type
//     * @return status in text
//     */
//    @RequestMapping("removeSnsAuth")
//    public @ResponseBody String removeSnsAuth(@RequestParam Long msoId,
//                                              @RequestParam Short type) {
//        SnsAuthManager snsMngr = new SnsAuthManager();
//        AutosharingService shareService = new AutosharingService();
//        
//        SnsAuth snsAuth = snsMngr.findMsoIdAndType(msoId, type);
//        if (snsAuth != null) {
//            snsMngr.delete(snsAuth);
//        }
//        List<NnChannelAutosharing> autoshareList = shareService.findChannelsByMsoAndType(msoId, type);
//        for (NnChannelAutosharing autoshare : autoshareList) {
//            shareService.delete(autoshare);
//        }
//        return "OK";
//    }
//    
//    /**
//     * Create sharing promotion information
//     * 
//     * @param msoId mso id
//     * @param type mso type
//     * @param token sns token
//     * @param secrete sns secret 
//     * @return
//     */
//    @RequestMapping("createSnsAuth")
//    public @ResponseBody String createSnsAuth(@RequestParam Long msoId,
//                                              @RequestParam Short type,
//                                              @RequestParam String token,
//                                              @RequestParam(required=false) String secrete) {
//        
//        SnsAuthManager snsMngr = new SnsAuthManager();
//        SnsAuth snsAuth = snsMngr.findMsoIdAndType(msoId, type);
//        if (snsAuth == null) {
//            snsAuth = new SnsAuth(msoId, type, token);
//            if (secrete != null)
//                snsAuth.setSecret(secrete);
//            snsAuth.setEnabled(true);
//            snsMngr.create(snsAuth);
//        } else {
//            snsAuth.setToken(token);
//            if (secrete != null)
//                snsAuth.setSecret(secrete);
//            snsAuth.setEnabled(true);
//            snsMngr.save(snsAuth);
//        }
//        return "OK";
//    }
//    
//    /**
//     * List sharing promotion information
//     * 
//     * @param msoId mso id
//     * @return list of SnsAuth objects
//     */
//    @RequestMapping("listSnsAuth")
//    public @ResponseBody List<SnsAuth> listSnsAuth(@RequestParam Long msoId) {
//        log.info("msoId = " + msoId);
//        SnsAuthManager snsMngr = new SnsAuthManager();
//        List<SnsAuth> list = snsMngr.findByMso(msoId);
//        for (SnsAuth sns : list) {
//            if (sns.getType() == SnsAuth.TYPE_FACEBOOK) {
//                FacebookLib.populatePageList(sns);
//            }
//        }
//        return list;
//    }
//
//    /**
//     * List channel auto sharing information
//     * 
//     * @param msoId mso id
//     * @param channelId channel id
//     * @return list of channel auto sharing objects
//     */
//    @RequestMapping("listChannelAutosharing")
//    public @ResponseBody List<NnChannelAutosharing> listChannelAutosharing(@RequestParam Long msoId, @RequestParam Long channelId) {
//        log.info("msoId = " + msoId);
//        log.info("channelId = " + channelId);
//        AutosharingService shareService = new AutosharingService();
//        return shareService.findByChannelAndMso(channelId, msoId);
//    }
//    
//    /**
//     * List set auto sharing information
//     * 
//     * @param msoId mso id
//     * @param setId set id
//     * @return list of set auto sharing objects
//     */
//    @RequestMapping("listChannelSetAutosharing")
//    public @ResponseBody List<NnSetAutosharing> listNnSetAutosharing(@RequestParam Long msoId, @RequestParam Long setId) {
//        log.info("msoId = " + msoId);
//        log.info("setId = " + setId);
//        AutosharingService shareService = new AutosharingService();
//        return shareService.findBySetAndMso(setId, msoId);
//    }
//    
//    /**
//     * Create channel auto sharing
//     * 
//     * @param msoId mso id
//     * @param channelId channel id
//     * @param parameter parameters
//     * @param target target
//     * @param type type
//     * @return status in text
//     */
//    @RequestMapping("createChannelAutosharing")
//    public @ResponseBody String createChannelAutosharing(@RequestParam Long msoId,
//                                                       @RequestParam Long channelId,
//                                                       @RequestParam(required=false) String parameter,
//                                                       @RequestParam(required=false) String target,
//                                                       @RequestParam Short type) {
//        log.info("msoId = " + msoId);
//        log.info("channelId = " + channelId);
//        log.info("type = " + type);
//        log.info("parameter = " + parameter);
//        log.info("target = " + target);
//        
//        AutosharingService shareService = new AutosharingService();
//        NnChannelAutosharing autosharing = shareService.findChannelAutosharing(msoId, channelId, type);
//        if (autosharing == null) {
//            autosharing = new NnChannelAutosharing(msoId, channelId, type);
//            if (parameter != null) {
//                autosharing.setParameter(parameter);
//            }
//            if (target != null) {
//                autosharing.setTarget(target);
//            }
//            shareService.create(autosharing);
//        } else {
//            autosharing.setParameter(parameter);
//            autosharing.setTarget(target);
//            shareService.save(autosharing);
//        }
//        return "OK";
//    }
//    
//    /**
//     * Create set auto sharing
//     * @param msoId mso id
//     * @param setId set id
//     * @param type type
//     */
//    @RequestMapping("createChannelSetAutosharing")
//    public @ResponseBody void createNnSetAutosharing(@RequestParam Long msoId,
//                                                          @RequestParam Long setId,
//                                                          @RequestParam Short type) {
//        log.info("msoId == " + msoId);
//        log.info("setId = " + setId);
//        log.info("type = " + type);
//        
//        AutosharingService shareService = new AutosharingService();
//        if (shareService.findSetAutosharing(msoId, setId, type) == null) {
//            shareService.create(new NnSetAutosharing(msoId, setId, type));
//        }
//    }
//
//    /**
//     * Remove a channel's auto sharing feature
//     * 
//     * @param msoId mso id
//     * @param channelId channel id
//     * @param type type
//     * @return status in text
//     */
//    @RequestMapping("removeChannelAutosharing")
//    public @ResponseBody String removeChannelAutosharing(@RequestParam Long msoId,
//                                                       @RequestParam Long channelId,
//                                                       @RequestParam Short type) {
//        log.info("msoId = " + msoId);
//        log.info("channelId = " + channelId);
//        log.info("type = " + type);
//        
//        AutosharingService shareService = new AutosharingService();
//        NnChannelAutosharing autosharing = shareService.findChannelAutosharing(msoId, channelId, type);
//        if (autosharing != null) {
//            shareService.delete(autosharing);
//        }
//        return "OK";
//    }
//    
//    /**
//     * Remove a set's auto sharing feature
//     * 
//     * @param msoId mso id
//     * @param setId set id
//     * @param type type
//     */
//    @RequestMapping("removeNnSetAutosharing")
//    public @ResponseBody void removeNnSetAutosharing(@RequestParam Long msoId,
//                                                     @RequestParam Long setId,
//                                                     @RequestParam Short type) {
//        log.info("msoId = " + msoId);
//        log.info("setId = " + setId);
//        log.info("type = " + type);
//        
//        AutosharingService shareService = new AutosharingService();
//        NnSetAutosharing autosharing = shareService.findSetAutosharing(msoId, setId, type);
//        if (autosharing != null) {
//            shareService.delete(autosharing);
//        }
//    }
//
//    /**
//     * Post the FBPost object to facebook
//     * 
//     * @param fbPost FBPost object
//     */
//    @RequestMapping("postToFacebook")
//    public @ResponseBody void postToFacebook(@RequestBody FBPost fbPost, HttpServletRequest req) {
//        try {
//            log.info("server name: " + req.getServerName());
//            fbPost.setLink(fbPost.getLink().replaceFirst("localhost", req.getServerName()));
//            log.info(fbPost.toString());
//            FacebookLib.postToFacebook(fbPost);
//        } catch (IOException e) {
//            exception(e);
//        }
//    }
//    
//    /**
//     * Post a FBPost object to twitter
//     * 
//     * @param fbPost FBPost object
//     */
//    @RequestMapping("postToTwitter")
//    public @ResponseBody void postToTwitter(@RequestBody FBPost fbPost, HttpServletRequest req) {
//        try {
//            fbPost.setLink(fbPost.getLink().replaceFirst("localhost", req.getServerName()));
//            log.info(fbPost.toString());
//            FacebookLib.postToTwitter(fbPost);
//        } catch (IOException e) {
//            exception(e);
//        } catch (TwitterException e) {
//            log.info("post to twitter operation terminated : "+e.getErrorMessage());
//        }
//    }
//
//    //////////////////// statistics ////////////////////
//    
//    //////////////////// others ////////////////////        
//    /**
//     * Change password
//     * 
//     * @param msoId mso id
//     * @param newPassword new password
//     * @return status in text
//     */
//    @RequestMapping("changePassword")
//    public @ResponseBody String changePassword(@RequestParam Long msoId, @RequestParam String newPassword, HttpServletRequest req) {
//        
//        MsoManager msoMngr = new MsoManager();
//        NnUserManager userMngr = new NnUserManager();
//        Mso mso = msoMngr.findById(msoId);
//        
//        NnUser user = null;
//        switch (mso.getType()) {
//        case Mso.TYPE_NN:
//        case Mso.TYPE_3X3:
//        case Mso.TYPE_ENTERPRISE:
//        case Mso.TYPE_MSO:
//            user = userMngr.findMsoUser(mso);
//            break;
//        case Mso.TYPE_TCO:
//            user = userMngr.findTcoUser(mso, NnUserManager.getShardByLocale(req));
//            break;
//        default:
//        }
//        if (user == null)
//            return "Invalid msoType";
//        user.setPassword(newPassword);
//        userMngr.resetPassword(user);
//        
//        return "OK";
//    }
//    
//    /**
//     * Send email
//     * 
//     * @param from from email address
//     * @param to to email address
//     * @param subject email subject
//     * @param msgBody email body
//     * @return status in text
//     */
//    @RequestMapping(value="sendEmail", params = {"from", "to", "subject", "msgBody"})
//    public @ResponseBody String sendEmail(
//                    @RequestParam(value = "from") String from,
//                    @RequestParam(value = "to") String to,
//                    @RequestParam(value = "subject") String subject,
//                    @RequestParam(value = "msgBody") String msgBody) {
//        
//        log.info("sender: " + from);
//        log.info("subject:" + subject);
//        log.info("content:" + msgBody);
//        
//        EmailService emailService = new EmailService();
//        msgBody = "from: "+from+" , "+msgBody;
//        NnEmail email = new NnEmail(subject, msgBody);
//        email.setToEmail("flipr@9x9cloud.tv");
//        email.setToName("flipr");
//        email.setReplyToEmail(from);
//        emailService.sendEmail(email);        
//        return "OK";
//    }
//    
}

