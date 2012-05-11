package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.model.NnUser;
import com.nncloudtv.model.NnUserChannelSorting;


public class NnUserChannelSortingDao extends GenericDao<NnUserChannelSorting>{

	public NnUserChannelSortingDao() {
		super(NnUserChannelSorting.class);
	}

	public NnUserChannelSorting save(NnUser user, NnUserChannelSorting sorting) {
		if (user == null || sorting == null) {return null;}		
		PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
		try {
			pm.makePersistent(sorting);
			sorting = pm.detachCopy(sorting);
		} finally {
			pm.close();
		}
		return sorting;
	}
	
	public NnUserChannelSorting findByUserAndChannel(NnUser user, long channelId) {
		PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
		
		NnUserChannelSorting s = null;
		try {
			Query q = pm.newQuery(NnUserChannelSorting.class);
			q.setFilter("userId == userIdParam && channelId== channelIdParam");
			q.declareParameters("long userIdParam, long channelIdParam");
			@SuppressWarnings("unchecked")
			List<NnUserChannelSorting> sorting = (List<NnUserChannelSorting>)q.execute(user.getId(), channelId);
			if (sorting.size() > 0) {
				s = sorting.get(0);
				s = pm.detachCopy(s);
			}
			System.out.println("sorting found???" + sorting.size());
		} finally {
			pm.close();
		}
		System.out.println("sorting found???" + s);
		return s;		
	}
	
	public List<NnUserChannelSorting> findByUser(NnUser user) {
		PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
		List<NnUserChannelSorting> s = new ArrayList<NnUserChannelSorting>();
		try {
			Query q = pm.newQuery(NnUserChannelSorting.class);
			q.setFilter("userId == userIdParam");
			q.declareParameters("long userIdParam");
			@SuppressWarnings("unchecked")
			List<NnUserChannelSorting> sorting = (List<NnUserChannelSorting>)q.execute(user.getId());
			s = (List<NnUserChannelSorting>) pm.detachCopyAll(sorting);
		} finally {
			pm.close();
		}
		return s;		
	}
	
}
