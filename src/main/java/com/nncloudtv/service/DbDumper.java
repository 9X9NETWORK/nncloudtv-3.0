package com.nncloudtv.service;

import java.util.Collection;
import java.util.List;
import javax.jdo.PersistenceManager;

import org.springframework.stereotype.Service;

import com.nncloudtv.lib.PMF;

@Service
public class DbDumper {
	
	public void save(Object o) {
		PersistenceManager pm = PMF.get(o.getClass()).getPersistenceManager();
		pm.makePersistent(o);
		pm.close();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void save(Collection objs) {
		PersistenceManager pm = PMF.get(objs.getClass()).getPersistenceManager();
		pm.makePersistentAll(objs);
		pm.close();
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List findAll(Class c, String order, int sharding) {
		PersistenceManager pm = PMF.get(c).getPersistenceManager();
		if (sharding == 1){
		   pm = PMF.getNnUser1().getPersistenceManager();
		} else if (sharding == 2) {
		   pm = PMF.getNnUser2().getPersistenceManager();
		}		  
		String query = "select from " + c.getName();
		List list = (List) pm.newQuery(query).execute();
		List detached = (List) pm.detachCopyAll(list);
		pm.close();
		return detached;	
	}
	
	@SuppressWarnings({ "rawtypes"})
	public void deleteAll(Class c, Collection list, int sharding) {
		PersistenceManager pm = PMF.get(c).getPersistenceManager();;
		if (sharding == 1){
			pm = PMF.getNnUser1().getPersistenceManager();
		} else if (sharding == 2) {
			pm = PMF.getNnUser2().getPersistenceManager();
		}
	    pm.deletePersistentAll(list);
	    pm.close();
	}
}

