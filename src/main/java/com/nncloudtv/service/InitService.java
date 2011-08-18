package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.nncloudtv.model.Category;
import com.nncloudtv.model.CategoryChannel;
import com.nncloudtv.model.ContentOwnership;
import com.nncloudtv.model.Ipg;
import com.nncloudtv.model.LangTable;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.MsoConfig;
import com.nncloudtv.model.MsoIpg;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnSet;
import com.nncloudtv.model.NnSetChannel;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserPref;
import com.nncloudtv.model.PdrRaw;
import com.nncloudtv.model.Subscription;
import com.nncloudtv.model.SubscriptionGroup;
import com.nncloudtv.model.SubscriptionLog;
import com.nncloudtv.model.ViewLog;

/**
 * for testing, works only for small set of data
 */	
@Service
public class InitService {
	protected static final Logger log = Logger.getLogger(InitService.class.getName());		

	private Mso msoOne;
	private Mso msoTwo;
	private NnUser userOne;
	private NnUser userTwo;
	private static String NNEMAIL = "mso@9x9.tv";
	private static String FFEMAIL = "mso@5f.tv";
	
	/** 
	 * @param trans whether to turn on transcoding service for channel creation
	 */
	public void initAll() {		
		deleteAll();
		initMsos();
		initCategories();
		initMso1Channels();
		initSets();
		initMso1Ipgs();
	}
	
	public void initMso1Ipgs() {
		MsoIpgManager msoIpgMngr = new MsoIpgManager();
		NnChannelManager channelMngr = new NnChannelManager();
		//msoOne
		NnChannel channel = channelMngr.findBySourceUrlSearch("http://9x9pod.s3.amazonaws.com/default.mp4");
		MsoIpg msoIpg = new MsoIpg(msoOne.getId(), channel.getId(), 81, MsoIpg.TYPE_READONLY);			
		msoIpgMngr.create(msoIpg);			
	}
	
	public void initMsos() {
		//------- default mso ---------
		MsoManager msoMngr = new MsoManager();
		msoOne = new Mso("9x9", "9x9", NNEMAIL, Mso.TYPE_NN);
		msoOne.setTitle("9x9.tv");
		msoOne.setLangCode(LangTable.LANG_EN);
		msoOne.setJingleUrl("http://s3.amazonaws.com/9x9ui/videos/opening.swf");
		msoOne.setLogoUrl("http://s3.amazonaws.com/9x9ui/images/logo_9x9.png");
		msoOne.setShard(1);
		msoMngr.create(msoOne);
				
		//config
		MsoConfigManager configMngr = new MsoConfigManager();
		MsoConfig configOneCdn = new MsoConfig(msoOne.getId(), MsoConfig.CDN, MsoConfig.CDN_AKAMAI);
		configMngr.create(configOneCdn);
		MsoConfig configOneDebug = new MsoConfig(msoOne.getId(), MsoConfig.DEBUG, "1");
		configMngr.create(configOneDebug);
		
		//a default MSO user
		NnUserManager userMngr = new NnUserManager();
		userOne = new NnUser(NNEMAIL, "9x9mso", "9x9 mso", NnUser.TYPE_NN);
		userOne.setMsoId(msoOne.getId());
		userMngr.create(userOne, null, (short)1);
				
		//------- second mso ---------
		msoTwo = new Mso("5f", "5f", FFEMAIL, Mso.TYPE_MSO);
		msoTwo.setTitle("5f.tv");		
		msoTwo.setLangCode(LangTable.LANG_ZH);
		msoTwo.setJingleUrl("http://s3.amazonaws.com/9x9ui/videos/opening.swf ");
		msoTwo.setLogoUrl("http://s3.amazonaws.com/9x9ui/images/5floor-logo.png");
		msoTwo.setShard(2);
		msoMngr.create(msoTwo);
		
		//config
		MsoConfig configTwoCdn = new MsoConfig(msoTwo.getId(), MsoConfig.CDN, MsoConfig.CDN_AKAMAI);
		configMngr.create(configTwoCdn);
		MsoConfig configTwoDebug = new MsoConfig(msoTwo.getId(), MsoConfig.DEBUG, "1");
		configMngr.create(configTwoDebug);
		
		//a default MSO user		
		userTwo = new NnUser(FFEMAIL, "5ffmso", "5f mso", NnUser.TYPE_TBC);		
		userTwo.setMsoId(msoTwo.getId());
		userMngr.create(userTwo, null, (short)2);
	}
	
	public void initCategories() {
		//mso1
		String[] categoryOneStr = {
				"News", "Ambient music", "Friends & Family", 
				"Sports", "Movie trailer", "My Youtube", "Lifestyle", "Food & Wine", "Brands", 			
				"Religion", "Travel", "Others",
			};		
		
		CategoryManager categoryMngr = new CategoryManager();
		int i=1;
		for (String name : categoryOneStr) {			
			Category c = new Category(name, true, msoOne.getId());
			if (!c.getName().equals("Others") && !c.getName().equals("Religion") && !c.getName().equals("Travel")) {
				i++;
				c.setSeq((short)i);
				c.setIpg(true);
				c.setType(Category.TYPE_RESTRICTED);
			}
			if (c.getName().equals("Friends & Family")) {
				c.setType(Category.TYPE_PERSONAL);
			}	
			if (c.getName().equals("My Youtube")) {
				c.setType(Category.TYPE_YOUTUBE);
			}
			categoryMngr.create(c);
		}				
	}
	
	public void initSets() {
		//mso1
		NnSetManager channelSetMngr = new NnSetManager();
		NnSet set1 = new NnSet(msoOne.getId(), "Set One", "set one", true);
		set1.setDefaultUrl("one"); 
		set1.setBeautifulUrl("one");
		//related channels
		NnChannelManager channelMngr = new NnChannelManager();
		List<NnChannel> channelsOne = new ArrayList<NnChannel>();
		NnChannel c1 = channelMngr.findBySourceUrlSearch("http://www.youtube.com/user/machinima");
		NnChannel c2 = channelMngr.findBySourceUrlSearch("http://9x9pod.s3.amazonaws.com/default.mp4");
		c1.setSeq(1);
		c2.setSeq(2);
		channelsOne.add(c1);
		channelsOne.add(c2);		
		channelSetMngr.create(set1, channelsOne);								
		//channelSet ownership
		ContentOwnershipManager ownershipMngr = new ContentOwnershipManager();
		ownershipMngr.create(new ContentOwnership(), msoOne, set1);
		
		//mso2
		NnSet set2 = new NnSet(msoOne.getId(), "Set Two", "set two", true);
		set2.setDefaultUrl("two"); 
		set2.setBeautifulUrl("two");
		//related channels
		List<NnChannel> channelsTwo = new ArrayList<NnChannel>();
		NnChannel c3 = channelMngr.findBySourceUrlSearch("http://www.youtube.com/user/machinima");
		NnChannel c4 = channelMngr.findBySourceUrlSearch("http://9x9pod.s3.amazonaws.com/default.mp4");
		c1.setSeq(1);
		c2.setSeq(2);
		channelsTwo.add(c3);
		channelsTwo.add(c4);		
		channelSetMngr.create(set2, channelsTwo);
		//channelSet ownership
		ownershipMngr.create(new ContentOwnership(), msoTwo, set2);	
	}
	
	public void initMso1Channels() {
		CategoryManager categoryMngr = new CategoryManager();
		NnChannelManager channelMngr = new NnChannelManager();
		NnProgramManager programMngr = new NnProgramManager();

		List<Category> categories = new ArrayList<Category>();
		Category category = categoryMngr.list(1, 1, "", "").get(0);
		System.out.println("<<<<<< category >>>> " + category.getName());
		categories.add(category);
		
		NnChannel channel1 = new NnChannel("Etsy", "Etsy.com", "http://s3.amazonaws.com/9x9chthumb/54e2967caf4e60fe9bc19ef1920997977eae1578.gif", userOne.getId());
		channel1.setFauxName("Etsy Faux Name");
		channel1.setFauxIntro("Etsy Faux Intro");
		channel1.setSourceUrl("http://feeds.feedburner.com/etsyetsyetsy");
		channel1.setContentType(NnChannel.CONTENTTYPE_PODCAST);
		channel1.setPublic(true);
		channel1.setProgramCount(2);
		channelMngr.create(channel1, categories);

		NnProgram program1 = new NnProgram("Handmade Confessional: Eli Dlugach", "Eli Dlugach gives a testimonial on why he loves handmade", "http://s3.amazonaws.com/9x9cache/005a69b4431d521e39534431254d81a211ebefc7_1227739497_thumbnail.jpg", NnProgram.TYPE_VIDEO);
		program1.setImageLargeUrl("http://s3.amazonaws.com/9x9cache/005a69b4431d521e39534431254d81a211ebefc7_1227739497_thumbLarge.jpg");
		program1.setMpeg4FileUrl("http://s3.amazonaws.com/9x9pod/005a69b4431d521e39534431254d81a211ebefc7_1227739497.m4v");
		program1.setWebMFileUrl("http://s3.amazonaws.com/9x9cache/005a69b4431d521e39534431254d81a211ebefc7_1227739497.webm");	
		program1.setPublic(true);		
		programMngr.create(channel1, program1);
		
		NnProgram program2 = new NnProgram("How-Tuesday: Needle Felted Eyeballs", "Read the full Etsy blog post", "http://s3.amazonaws.com/9x9cache/5a14e5502fd5ab6b26e7f11f2a38ee718bc06eea_1288043514_thumbnail.jpg", NnProgram.TYPE_VIDEO);
		program2.setImageLargeUrl("http://s3.amazonaws.com/9x9cache/5a14e5502fd5ab6b26e7f11f2a38ee718bc06eea_1288043514_thumbLarge.jpg");
		program2.setMpeg4FileUrl("http://s3.amazonaws.com/9x9pod/5a14e5502fd5ab6b26e7f11f2a38ee718bc06eea_1288043514.m4v");	
		program2.setPublic(true);
		programMngr.create(channel1, program2);

		NnChannel channel2 = new NnChannel("TEDTalks (hd)", "TED", "http://s3.amazonaws.com/9x9chthumb/f14a9bb972adfefab1c9c4f0ec44f251686d655a.jpg", userOne.getId());		
		channel2.setSourceUrl("http://feeds.feedburner.com/tedtalksHD");
		channel2.setContentType(NnChannel.CONTENTTYPE_PODCAST);
		channel2.setPublic(true);		
		channel2.setProgramCount(3);
		channelMngr.create(channel2, categories);

		NnProgram program3 = new NnProgram("TEDTalks : Beverly + Dereck", "Beverly + Dereck Joubert live in the bush", "http://s3.amazonaws.com/9x9cache/8ad69b8dcbd0edd516c4f6bd530390d9f640de45_1292858280_thumbnail.jpg", NnProgram.TYPE_VIDEO);
		program3.setImageLargeUrl("http://s3.amazonaws.com/9x9cache/8ad69b8dcbd0edd516c4f6bd530390d9f640de45_1292858280_thumbLarge.jpg");
		program3.setMpeg4FileUrl("http://s3.amazonaws.com/9x9pod/8ad69b8dcbd0edd516c4f6bd530390d9f640de45_1292858280.mp4");	
		program3.setPublic(true);
		programMngr.create(channel2, program3);

		NnProgram program4 = new NnProgram("TEDTalks : Peter Molyneux", "Peter Molyneux demos Milo", "http://s3.amazonaws.com/9x9cache/5716619074068502b91f5f9668cf906a6702078b_1282119180_thumbnail.jpg", NnProgram.TYPE_VIDEO);
		program4.setImageLargeUrl("http://s3.amazonaws.com/9x9cache/5716619074068502b91f5f9668cf906a6702078b_1282119180_thumbLarge.jpg");
		program4.setMpeg4FileUrl("http://s3.amazonaws.com/9x9pod/5716619074068502b91f5f9668cf906a6702078b_1282119180.mp4");	
		program4.setPublic(true);
		programMngr.create(channel2, program4);

		NnProgram program5 = new NnProgram("TEDTalks : Hans Rosling", "Hans Rosling reframes 10 years of UN", "http://s3.amazonaws.com/9x9cache/5ee1ea7ea93d6703c90fb4dc00188f4e5619ee1f_1286442720_thumbnail.jpg", NnProgram.TYPE_VIDEO);
		program5.setImageLargeUrl("http://s3.amazonaws.com/9x9cache/5ee1ea7ea93d6703c90fb4dc00188f4e5619ee1f_1286442720_thumbLarge.jpg");
		program5.setMpeg4FileUrl("http://s3.amazonaws.com/9x9pod/5ee1ea7ea93d6703c90fb4dc00188f4e5619ee1f_1286442720.mp4");	
		program5.setPublic(true);
		programMngr.create(channel2, program5);

		NnProgram program6 = new NnProgram("TEDTalks : Zainab Salbi: Women", "In war we often see only the frontline stories of soldiers and combat", "http://s3.amazonaws.com/9x9cache/6e0c1a48b2a5b8b9253b25885a2cce9586564366_1290523500_thumbnail.jpg", NnProgram.TYPE_VIDEO);
		program6.setImageLargeUrl("http://s3.amazonaws.com/9x9cache/6e0c1a48b2a5b8b9253b25885a2cce9586564366_1290523500_thumbLarge.jpg");
		program6.setMpeg4FileUrl("http://s3.amazonaws.com/9x9pod/6e0c1a48b2a5b8b9253b25885a2cce9586564366_1290523500.mp4");
		program6.setWebMFileUrl("http://s3.amazonaws.com/9x9cache/6e0c1a48b2a5b8b9253b25885a2cce9586564366_1290523500.webm");
		program6.setPublic(true);
		programMngr.create(channel2, program6);
	
		//create channel				
		NnChannel channel3 = new NnChannel("System Channel", "System Channel", "https://s3.amazonaws.com/9x9pod/system.png", userOne.getId());
		channel3.setPublic(true);
		channel3.setSourceUrl("http://9x9pod.s3.amazonaws.com/default.mp4");
		channel3.setContentType(NnChannel.CONTENTTYPE_SYSTEM);
		channel3.setProgramCount(1);
		channelMngr.create(channel3, categories);

		NnProgram program7 = new NnProgram("System Program", "", "https://s3.amazonaws.com/9x9pod/system.png", NnProgram.TYPE_VIDEO);
		program7.setPublic(true);
		program7.setMpeg4FileUrl("http://9x9pod.s3.amazonaws.com/default.mp4");
		program7.setWebMFileUrl("http://9x9pod.s3.amazonaws.com/default.mp4");	
		programMngr.create(channel3, program7);
				
		//create a channel, but status set to error
		NnChannel channel4 = new NnChannel("Vegan A Go-Go", "A simple vegan cooking show.", "http://s3.amazonaws.com/9x9chthumb/6bb992aafe18c3054ca30035d7e5fe7cc9394d37.jpg", userOne.getId());		
		channel4.setSourceUrl("http://feeds.feedburner.com/veganagogo");
		channel4.setStatus(NnChannel.STATUS_ERROR);
		channel4.setContentType(NnChannel.CONTENTTYPE_PODCAST);
		channel4.setPublic(true);
		channel4.setProgramCount(2);
		channelMngr.create(channel4, categories);

		NnProgram program8 = new NnProgram("EP 40: Caramelized Rosemary Pears", "Caramelized Rosemary Pears Serves: 6-8", "http://s3.amazonaws.com/9x9cache/a023cd7cafa3b9d18d3e48274b5994c3cbacd759_1218075566_thumbnail.jpg", NnProgram.TYPE_VIDEO);
		program8.setImageLargeUrl("	http://s3.amazonaws.com/9x9cache/a023cd7cafa3b9d18d3e48274b5994c3cbacd759_1218075566_thumbLarge.jpg");
		program8.setMpeg4FileUrl("http://s3.amazonaws.com/9x9pod/a023cd7cafa3b9d18d3e48274b5994c3cbacd759_1218075566.mp4");
		program8.setWebMFileUrl("http://s3.amazonaws.com/9x9cache/a023cd7cafa3b9d18d3e48274b5994c3cbacd759_1218075566.webm");
		program8.setPublic(true);
		programMngr.create(channel4, program8);

		NnProgram program9 = new NnProgram("EP 55: Herbed Fruit Salad", "Herbed Fruit Salad Serves: 10 Difficulty", "http://s3.amazonaws.com/9x9cache/0047ece77a9dcdce929d69be889d3f4258e98b38_1245713603_thumbnail.jpg", NnProgram.TYPE_VIDEO);
		program9.setImageLargeUrl("http://s3.amazonaws.com/9x9cache/0047ece77a9dcdce929d69be889d3f4258e98b38_1245713603_thumbLarge.jpg");
		program9.setMpeg4FileUrl("http://s3.amazonaws.com/9x9pod/0047ece77a9dcdce929d69be889d3f4258e98b38_1245713603.mp4");
		program9.setWebMFileUrl("http://s3.amazonaws.com/9x9cache/0047ece77a9dcdce929d69be889d3f4258e98b38_1245713603.webm");
		program9.setPublic(true);
		programMngr.create(channel4, program9);

		//create a channel, but no programs
		NnChannel channel5 = new NnChannel("Comedy Central's Jokes.com", "", "http://s3.amazonaws.com/9x9cache/1b2885a8ba30ee692b56fd0e9c9128995473367e_1199163600_thumbnail.jpg", userOne.getId());		
		channel5.setSourceUrl("http://feeds.feedburner.com/comedycentral/standup");
		channel5.setPublic(true);
		channel5.setContentType(NnChannel.CONTENTTYPE_PODCAST);
		channelMngr.create(channel5, categories);
		
		//Youtube channel
		NnChannel channel6 = new NnChannel("Machinima channel", null, "http://i4.ytimg.com/i/cMTZY1rFXO3Rj44D5VMyiw/1.jpg?v=6729ba", userOne.getId());
		channel6.setSourceUrl("http://www.youtube.com/user/machinima");
		channel6.setPublic(true);
		channel6.setContentType(NnChannel.CONTENTTYPE_YOUTUBE_CHANNEL);
		channel6.setProgramCount(2);
		channelMngr.create(channel6, categories);
				
		NnProgram program10 = new NnProgram("Join the Heroes! -- INTERACTIVE CHOICE - Sanity Not Included - Complicated Complications", "www.youtube.com Click here to watch the previous episode of Sanity Not Included! Join the Heroes!", "http://i.ytimg.com/vi/xdgt4yI96IY/default.jpg", NnProgram.TYPE_VIDEO);
		program10.setImageLargeUrl("http://i.ytimg.com/vi/xdgt4yI96IY/hqdefault.jpg");
		program10.setOtherFileUrl("http://www.youtube.com/watch?v=xdgt4yI96IY");
		program10.setPublic(true);
		programMngr.create(channel6, program10);
	
		NnProgram program11 = new NnProgram("Halo Reach: Last Man Standing Gameshow Season 2 Episode 17! FAIL CHOICE (Episode 31)", "www.youtube.comClick here to watch Halo Reach: Last Man Standing Gameshow Season 2 Episode 16!", "http://i.ytimg.com/vi/qa2TZq7_TeA/default.jpg", NnProgram.TYPE_VIDEO);
		program11.setImageLargeUrl("http://i.ytimg.com/vi/qa2TZq7_TeA/hqdefault.jpg");	
		program11.setPublic(true);
		programMngr.create(channel1, program11);
		
		//FB channel
		NnChannel channel7 = new NnChannel("BMW", null, "http://profile.ak.fbcdn.net/hprofile-ak-snc4/211055_22893372268_38816_s.jpg", userOne.getId());
		channel7.setSourceUrl("http://www.facebook.com/bmw");
		channel7.setPublic(true);
		channel7.setContentType(NnChannel.CONTENTTYPE_FACEBOOK);
		channel7.setProgramCount(0);
		channelMngr.create(channel7, categories);			
	}
	
	public void deleteAll() {
		DbDumper dumper = new DbDumper();
		@SuppressWarnings("rawtypes")
		List list = dumper.findAll(Category.class, "createDate", 0);
		dumper.deleteAll(Category.class, list, 0);
		
		list = dumper.findAll(CategoryChannel.class, "createDate", 0);
		dumper.deleteAll(CategoryChannel.class, list, 0);

		list = dumper.findAll(ContentOwnership.class, "createDate", 0);
		dumper.deleteAll(ContentOwnership.class, list, 0);
		
		list = dumper.findAll(Ipg.class, "createDate", 0);
		dumper.deleteAll(Ipg.class, list, 0);

		list = dumper.findAll(Mso.class, "createDate", 0);
		dumper.deleteAll(Mso.class, list, 0);	

		list = dumper.findAll(MsoConfig.class, "createDate", 0);
		dumper.deleteAll(MsoConfig.class, list, 0);
		
		list = dumper.findAll(MsoIpg.class, "createDate", 0);
		dumper.deleteAll(MsoIpg.class, list, 0);		

		list = dumper.findAll(NnChannel.class, "createDate", 0);
		dumper.deleteAll(NnChannel.class, list, 0);		

		list = dumper.findAll(NnProgram.class, "createDate", 0);
		dumper.deleteAll(NnProgram.class, list, 0);		
		
		list = dumper.findAll(NnSet.class, "createDate", 0);
		dumper.deleteAll(NnSet.class, list, 0);		

		list = dumper.findAll(NnSetChannel.class, "createDate", 1);
		dumper.deleteAll(NnSetChannel.class, list, 1);

		list = dumper.findAll(NnUser.class, "createDate", 1);
		dumper.deleteAll(NnUser.class, list, 1);
		list = dumper.findAll(NnUser.class, "createDate", 2);
		dumper.deleteAll(NnUser.class, list, 2);
		
		list = dumper.findAll(NnUserPref.class, "createDate", 1);
		dumper.deleteAll(NnUserPref.class, list, 1);
		list = dumper.findAll(NnUserPref.class, "createDate", 2);
		dumper.deleteAll(NnUserPref.class, list, 2);
		
		list = dumper.findAll(PdrRaw.class, "createDate", 0);
		dumper.deleteAll(PdrRaw.class, list, 0);

		list = dumper.findAll(NnProgram.class, "createDate", 0);
		dumper.deleteAll(NnProgram.class, list, 0);		
		
		list = dumper.findAll(Subscription.class, "createDate", 1);
		dumper.deleteAll(Subscription.class, list, 1);
		list = dumper.findAll(Subscription.class, "createDate", 2);
		dumper.deleteAll(Subscription.class, list, 2);		
		
		list = dumper.findAll(SubscriptionLog.class, "createDate", 0);
		dumper.deleteAll(SubscriptionLog.class, list, 0);

		list = dumper.findAll(SubscriptionGroup.class, "createDate", 0);
		dumper.deleteAll(SubscriptionGroup.class, list, 0);
		
		list = dumper.findAll(ViewLog.class, "createDate", 0);
		dumper.deleteAll(ViewLog.class, list, 0);		
	}		
}
