package com.nncloudtv.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nncloudtv.dao.NnProgramDao;
import com.nncloudtv.model.NnChannel;
import com.nncloudtv.model.NnProgram;
import com.nncloudtv.model.NnUser;

@Service
public class NnProgramManager {
	
	protected static final Logger log = Logger.getLogger(NnProgramManager.class.getName());
	
	private NnProgramDao programDao = new NnProgramDao();
	
	@Transactional
	public void create(NnChannel channel, NnProgram program) {		
		Date now = new Date();
		program.setCreateDate(now);
		program.setUpdateDate(now);
		if (program.getPubDate() == null) {
			program.setPubDate(now);
		}
		program.setChannelId(channel.getId());
		programDao.save(program);
		
		//set channel count
		int count = channel.getProgramCount() + 1;
		channel.setProgramCount(count);
		NnChannelManager channelMngr = new NnChannelManager();
		channelMngr.save(channel);

		//if the channel's original programCount is zero, its count will not be in the category, adding it now.
		if (count == 1) {
			CategoryManager categoryMngr = new CategoryManager();
			System.out.println("mso program manager, channel create, addChannelCount");
			categoryMngr.addChannelCounter(channel);
		}		
	} 

	public NnProgram save(NnProgram program) {		
		program.setUpdateDate(new Date()); // NOTE: a trying to modify program update time (from admin) will be omitted by this
		program = programDao.save(program);
		return program;
	}

	public void delete(NnProgram program) {
		programDao.delete(program);
	}
	
	public List<NnProgram> findGoodProgramsByChannelId(long channelId) {
		List<NnProgram> programs = new ArrayList<NnProgram>();
		programs = programDao.findGoodProgramsByChannelId(channelId);
		return programs;
	}
	
	public List<NnProgram> findGoodProgramsByChannelIds(List<Long>channelIds) {
		log.info("requested channelIds size:" + channelIds.size());
		List<NnProgram> programs = new ArrayList<NnProgram>();
		log.info("remaining channel size not in the cache:" + channelIds.size());
		if (channelIds.size() > 0) {
			List<NnProgram> list = programDao.findGoodProgramsByChannelIds(channelIds);
			programs.addAll(list);
		}

		return programs;
	}

	public NnProgram findOldestByChannelId(long channelId) {
		NnProgram oldest = programDao.findOldestByChannelId(channelId); 
		log.info("find the oldest program:" + oldest.getId() + ";" + oldest.getName() + ";" + oldest.getStorageId() + ";" + oldest.getPubDate());		
		return oldest;
	}

	public NnProgram findByStorageId(String storageId) {
		return programDao.findByStorageId(storageId);
	}

	public NnProgram findById(long id) {
		NnProgram program = programDao.findById(id);
		return program;
	}
	
	public List<NnProgram> findSubscribedPrograms(NnUser user) {
		SubscriptionManager subService = new SubscriptionManager();			
		List<NnChannel> channels = subService.findSubscribedChannels(user, null);
		List<NnProgram> programs = new ArrayList<NnProgram>();
		List<Long> channelIds = new ArrayList<Long>();
		for (NnChannel c : channels) {
			channelIds.add(c.getId());
		}
		programs = this.findGoodProgramsByChannelIds(channelIds);
		return programs;
	}	
}