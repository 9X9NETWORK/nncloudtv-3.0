package com.nncloudtv.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nncloudtv.dao.MsoDao;
import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.model.Mso;

@Service
public class MsoManager {

	protected static final Logger log = Logger.getLogger(MsoManager.class.getName());
	
	private MsoDao msoDao = new MsoDao();	
	
	@Transactional
	public Mso test(Mso mso) {
		this.create(mso);
		return mso;
	}
	
	public int addMsoInfoVisitCounter(String msoName) {
		String counterName = msoName + "BrandInfo";
		CounterFactory factory = new CounterFactory();
		ShardedCounter counter = factory.getOrCreateCounter(counterName);
		counter.increment();			
		return counter.getCount();
	}

	public void create(Mso mso) {
		if (this.findByName(mso.getName()) == null) {
			mso.setCreateDate(new Date());
			this.save(mso);			
		}
	}
	
	public Mso save(Mso mso) {
		Date now = new Date();
		mso.setUpdateDate(now);
		return msoDao.save(mso);
	}
	
	public List<Mso> findByType(short type) {
		return msoDao.findByType(type);
	}
	
	public Mso findNNMso() {
		List<Mso> msos = this.findByType(Mso.TYPE_NN);
		if (msos.size() > 0) { return msos.get(0); }
		return null;
	}

	public Mso findByName(String name) {
		if (name == null) {return null;}
		Mso mso = msoDao.findByName(name);
		return mso;
	}	
		
	public Mso findMsoViaHttpReq(HttpServletRequest req) {
		String msoName = req.getParameter("mso"); //?mso=		
		Mso mso = this.findByName(msoName);
		if (mso == null) {
			log.info("determine mso failed. use default mso");
			mso = this.findNNMso();
		}
		return mso;
	}
	
	public Mso findById(long id) {
		return msoDao.findById(id);
	}
	
	public List<Mso> findAll() {
		return msoDao.findAll();
	}

	//example: mso(1)category(123), returns category
	public static String getCacheKey(String name) {
		return "mso(" + name + ")";
	}
	
}
