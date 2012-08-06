package com.nncloudtv.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Captcha;

public class CaptchaDao extends GenericDao<Captcha> {
	protected static final Logger log = Logger.getLogger(CaptchaDao.class.getName());
	
	public CaptchaDao() {
		super(Captcha.class);
	}
	
	public void saveAll(List<Captcha> list) {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		try {
			pm.makePersistentAll(list);
		} finally {
			pm.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Captcha getRandom() {
		PersistenceManager pm = PMF.getContent().getPersistenceManager();
		Captcha c = null;
		double random = Math.random();
		try {
			Query q = pm.newQuery(Captcha.class);
			q.setFilter("random > randomParam");
			q.declareParameters("double randomParam");
			q.setRange(1, 2);
			q.setOrdering("random");
			List<Captcha> list = (List<Captcha>) q.execute(random);
			System.out.println("random:" + random);
			if (list.size() == 0) {
				q.setFilter("random < randomParam");
				list = (List<Captcha>) q.execute(random);
			}
			if (list.size() == 0) {
				System.out.println("gotta enter here");
				q = pm.newQuery(Captcha.class);
				//q.setRange(0,1);
				list = (List<Captcha>) q.execute();
				System.out.println("list.size:" + list.size());				
			}
			if (list.size() > 0)
				c = pm.detachCopy(list.get(0));			
		} finally {
			pm.close();
		}
		return c;
	}
	
}
