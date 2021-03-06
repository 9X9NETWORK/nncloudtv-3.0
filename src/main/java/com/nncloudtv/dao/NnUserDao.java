package com.nncloudtv.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.nncloudtv.lib.AuthLib;
import com.nncloudtv.lib.PMF;
import com.nncloudtv.model.Mso;
import com.nncloudtv.model.NnUser;
import com.nncloudtv.service.MsoManager;

public class NnUserDao extends GenericDao<NnUser> {

    protected static final Logger log = Logger.getLogger(NnUserDao.class.getName());
    
    public NnUserDao() {
        super(NnUser.class);
    }

    //generic = true is for regular search
    //generic = false means for experimental ios feature
    @SuppressWarnings("unchecked")
    public List<NnUser> search(String email, String name, String generic, long msoId) {
        List<NnUser> detached = new ArrayList<NnUser>();        
        PersistenceManager pm = PMF.getNnUser1().getPersistenceManager();
        try {
            String sql = "";
            if (generic != null) {
            	sql = "select * from nnuser a1 " + 
            			" inner join (" + 
            			  "select distinct u.id " +
            			   " from nnuser_profile p, nnuser u " +
            			  " where p.userId = u.id " +
            			    " and (lower(p.name) like lower(\"%" + generic + "%\") " + 
            			          " or lower(p.intro) like lower(\"%" + generic + "%\")) " + 
            			  ") a2 on a1.id=a2.id";
            	/*
                sql = "select * from nnuser " + 
                       "where id in (select userId from nnuser_profile " +
                                    " where msoId = " + msoId +  
                                    "   and (lower(name) like lower(\"%" + generic + "%\") " +  
                                    "   or lower(intro) like lower(\"%" + generic + "%\")))";
                                    */                              
            } else {
               sql = "select * from nnuser " + "where ";                      
               if (email != null) {
                   sql += " email = '" + email + "'";
               }
               /*
               } else if (name != null) { 
                   sql += " lower(name) like lower('%" + name + "%')";
               */
            }
            log.info("Sql=" + sql);        
            pm = PMF.getNnUser1().getPersistenceManager();
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnUser.class);
            List<NnUser> results = (List<NnUser>) q.execute();
            detached = (List<NnUser>)pm.detachCopyAll(results);
            //user2
            pm = PMF.getNnUser2().getPersistenceManager();
            q= pm.newQuery("javax.jdo.query.SQL", sql);
            results = (List<NnUser>) q.execute();
            detached.addAll((List<NnUser>)pm.detachCopyAll(results));
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();                
        }        
        return detached;
    }
    
    @Override // looks both two shard DB
    public NnUser findById(long id) {
        
        NnUser user = null;
        user = findById(id, (short) 1);
        if (user == null) {
            return findById(id, (short) 2);
        } else {
            return user;
        }
    }
    
    public NnUser findById(long id, short shard) {
        
        NnUser user = null;
        NnUser detached = null;
        PersistenceManager pm = null;
        if (shard == 0) {
            return findById(id);
        } else if (shard > 1) {
            pm = PMF.getNnUser2().getPersistenceManager();
        } else {
            pm = PMF.getNnUser1().getPersistenceManager();
        }
        
        try {            
            user = pm.getObjectById(NnUser.class, id);
            detached = (NnUser)pm.detachCopy(user);            
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return detached;
        //return user;
    }
    
    /**
    public NnUser findById(long id, short shard) {
        PersistenceManager pm = NnUserDao.getPersistenceManager((short) shard, null);
        NnUser detached = null;
        try {
            NnUser user = (NnUser)pm.getObjectById(NnUser.class, id);
            if (user == null)
                pm = NnUserDao.getPersistenceManager((short)2, null);
            else
                detached = user;
            //detached = (NnUser)pm.detachCopy(user); // Louis: this line cause internal error
        } catch (JDOObjectNotFoundException e) {
        } finally {
            pm.close();
        }
        return detached;        
    }    
    **/
    
    //use either shard or token to determine partition, default shard 1 if nothing
    public static PersistenceManager getPersistenceManager(short shard, String token) {
        if (shard != 0) {
            if (shard != 1) {
                return PMF.getNnUser2().getPersistenceManager();
            } else {
                return PMF.getNnUser1().getPersistenceManager();
            }
        }
        if (token != null) {
            if (token.contains("2-")) {
                return PMF.getNnUser2().getPersistenceManager();
            } else {
                return PMF.getNnUser1().getPersistenceManager();
            }
        }        
        return PMF.getNnUser1().getPersistenceManager();
    }
                
    public NnUser save(NnUser user) {
        if (user == null) {return null;}
        PersistenceManager pm = NnUserDao.getPersistenceManager(user.getShard(), user.getToken());
        try {
            pm.makePersistent(user);
            user = pm.detachCopy(user);
        } finally {
            pm.close();
        }
        return user;
    }
    
    // return MSO only
    @SuppressWarnings("unchecked")
    public NnUser findAuthenticatedMsoUser(String email, String password, long msoId) {
        NnUser user = null;
        PersistenceManager pm = NnUserDao.getPersistenceManager(NnUser.SHARD_DEFAULT, null);
        MsoManager msoMngr = new MsoManager();
        long nnId = msoMngr.findNNMso().getId();
        try {
            Query query = pm.newQuery(NnUser.class);
            query.setFilter("email == emailParam && msoId == msoIdParam && type == typeParam");
            query.declareParameters("String emailParam, long msoIdParam, short typeParam");
            List<NnUser> results = (List<NnUser>) query.execute(email, msoId, NnUser.TYPE_3X3);
            if (results.size() > 0) {
                user = results.get(0);        
            } else {
                // make 9x9 and 5F login as possible
                results = (List<NnUser>) query.execute(email, nnId, NnUser.TYPE_NN);  // super account !!!
                if (results.size() > 0) {
                    user = results.get(0);
                } else {
                    results = (List<NnUser>) query.execute(email, msoId, NnUser.TYPE_TBC);
                    if (results.size() > 0) {
                        user = results.get(0);
                    } else {
                        results = (List<NnUser>) query.execute(email, msoId, NnUser.TYPE_ENTERPRISE);
                        if (results.size() > 0) {
                            user = results.get(0);
                        }
                    }
                }
            }
            if (user != null) {
                byte[] proposedDigest = AuthLib.passwordDigest(password, user.getSalt());
                if (!Arrays.equals(user.getCryptedPassword(), proposedDigest)) {                
                    user = null;
                } else {
                    user = pm.detachCopy(user);
                }
            }
        } finally {
            pm.close();
        }
        return user;        
    }
    
    public NnUser findAuthenticatedUser(String email, String password, short shard) {
        NnUser user = null;
        PersistenceManager pm = NnUserDao.getPersistenceManager(shard, null);
        try {
            Query query = pm.newQuery(NnUser.class);
            query.setFilter("email == emailParam");
            query.declareParameters("String emailParam");                
            @SuppressWarnings("unchecked")
            List<NnUser> results = (List<NnUser>) query.execute(email);
            if (results.size() > 0) {
                user = results.get(0);        
                byte[] proposedDigest = AuthLib.passwordDigest(password, user.getSalt());
                if (!Arrays.equals(user.getCryptedPassword(), proposedDigest)) {                
                    user = null;
                }
            }
            user = pm.detachCopy(user);
        } finally {
            pm.close();
        }
        return user;        
    }
    
    public List<NnUser> findByType(short type) {
        List<NnUser> detached = new ArrayList<NnUser>();
        PersistenceManager pm = NnUserDao.getPersistenceManager(NnUser.SHARD_DEFAULT, null);
        try {
            Query query = pm.newQuery(NnUser.class);
            query.setFilter("type == " + type);    
            @SuppressWarnings("unchecked")
            List<NnUser> users = (List<NnUser>) query.execute(type);
            detached = (List<NnUser>)pm.detachCopyAll(users);
        } finally {
            pm.close();
        }
        return detached;        
    }
    
    public NnUser findByToken(String token) {
        NnUser user = null;
        log.info("token = " + token);
        PersistenceManager pm = NnUserDao.getPersistenceManager((short)0, token);
        try {
            Query query = pm.newQuery(NnUser.class);
            query.setFilter("token == tokenParam");
            query.declareParameters("String tokenParam");        
            @SuppressWarnings("unchecked")
            List<NnUser> results = (List<NnUser>) query.execute(token);
            if (results.size() > 0) {
                user = results.get(0);            
            }
            user = pm.detachCopy(user);
        } finally {
            pm.close();
        }
        return user;                
    }

    public NnUser findNNUser() {
        List<NnUser> detached = new ArrayList<NnUser>();
        PersistenceManager pm = NnUserDao.getPersistenceManager((short)1, null);
        try {
            Query query = pm.newQuery(NnUser.class);
            query.setFilter("type == " + NnUser.TYPE_NN);    
            @SuppressWarnings("unchecked")
            List<NnUser> users = (List<NnUser>) query.execute(NnUser.TYPE_NN);
            detached = (List<NnUser>)pm.detachCopyAll(users);
        } finally {
            pm.close();
        }
        if (detached != null)
            return detached.get(0);
        return null;        
    }
    
    public NnUser findByEmail(String email, short shard) {
        NnUser user = null;
        PersistenceManager pm = NnUserDao.getPersistenceManager(shard, null);
        try {
            Query query = pm.newQuery(NnUser.class);
            query.setFilter("email == emailParam");
            query.declareParameters("String emailParam");        
            @SuppressWarnings("unchecked")
            List<NnUser> results = (List<NnUser>) query.execute(email);
            if (results.size() > 0) {
                user = results.get(0);            
            }
            user = pm.detachCopy(user);
        } finally {
            pm.close();
        }
        return user;                
    }    
    
    public NnUser findByProfileUrl(String profileUrl) {
        if (profileUrl == null) return null;
        profileUrl = profileUrl.toLowerCase();
        NnUser user = null;
        PersistenceManager pm = PMF.getNnUser1().getPersistenceManager();        
        try {
            for (int i=0;i<2;i++) {
                String sql = "select * from nnuser n " +
                		     " where exists (" +
                		         " select userId " +
                                   "from nnuser_profile p " +
                                 " where p.userId = n.id " +
                                   " and lower(profileUrl) = '" + profileUrl + "')";                
                log.info("sql:" + sql);
                Query query = pm.newQuery("javax.jdo.query.SQL", sql);
                query.setClass(NnUser.class);
                @SuppressWarnings("unchecked")
                List<NnUser> results = (List<NnUser>) query.execute();
                if (results.size() > 0) {
                    user = results.get(0);
                    i = 2;
                } else {
                    pm = PMF.getNnUser2().getPersistenceManager();
                }
            }
            user = pm.detachCopy(user);
        } finally {
            pm.close();
        }
        return user;                
    }    

    public NnUser findByFbId(String fbId) {
        NnUser user = null;
        PersistenceManager pm = PMF.getNnUser1().getPersistenceManager();        
        try {
            for (int i=0;i<2;i++) {
                Query query = pm.newQuery(NnUser.class);
                query.setFilter("fbId == fbIdParam");
                query.declareParameters("String fbIdParam");        
                @SuppressWarnings("unchecked")
                List<NnUser> results = (List<NnUser>) query.execute(fbId);
                if (results.size() > 0) {
                    user = results.get(0);
                    i = 2;
                } else {
                    pm = PMF.getNnUser2().getPersistenceManager();
                }
            }
            user = pm.detachCopy(user);
        } finally {
            pm.close();
        }
        return user;                
    }    
    
    public List<NnUser> findByTypeAndMso(Short type, Long msoId) {
        List<NnUser> detached = new ArrayList<NnUser>();
        PersistenceManager pm = NnUserDao.getPersistenceManager(NnUser.SHARD_DEFAULT, null);
        try {
            Query query = pm.newQuery(NnUser.class);
            query.setFilter("type == typeParam && msoId == msoIdParam");
            query.declareParameters("short typeParam, long msoIdParam");
            @SuppressWarnings("unchecked")
            List<NnUser> results = (List<NnUser>) query.execute(type, msoId);
            if (results.size() > 0) {
                detached = (List<NnUser>) pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        return detached;
    }
    
    public List<NnUser> findTcoUser(Mso mso, short shard) {
        List<NnUser> detached = new ArrayList<NnUser>();
        PersistenceManager pm = NnUserDao.getPersistenceManager(shard, null);
        try {
            Query query = pm.newQuery(NnUser.class);
            query.setFilter("type == typeParam && msoId == msoIdParam");
            query.declareParameters("short typeParam, long msoIdParam");
            @SuppressWarnings("unchecked")
            List<NnUser> results = (List<NnUser>) query.execute(NnUser.TYPE_TCO, mso.getId());
            if (results.size() > 0) {
                detached = (List<NnUser>) pm.detachCopyAll(results);
            }
        } finally {
            pm.close();
        }
        return detached;
    }

    //TODO merge one and two
    public List<NnUser> findFeatured(long msoId) {
        PersistenceManager pm = PMF.getNnUser1().getPersistenceManager(); 
        List<NnUser> detached = new ArrayList<NnUser>(); 
        try {
            String sql = "select * " +
                          " from nnuser n " + 
                         " where exists " +
                           " (select userId from nnuser_profile p " +
                               " where p.userId = n.id " + 
                                 " and p.msoId = " + msoId +
                                 " and p.featured = true " +
                               " order by rand())" + 
                                       " limit 9";                                    
            log.info("sql:" + sql);
            Query q= pm.newQuery("javax.jdo.query.SQL", sql);
            q.setClass(NnUser.class);
            @SuppressWarnings("unchecked")
            List<NnUser> users = (List<NnUser>) q.execute();
            detached = (List<NnUser>)pm.detachCopyAll(users);
        } finally {
            pm.close();
        }
        return detached;
    }        
}
