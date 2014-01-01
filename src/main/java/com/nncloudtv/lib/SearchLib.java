package com.nncloudtv.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nncloudtv.model.NnChannel;
import com.nncloudtv.service.MsoConfigManager;

public class SearchLib {
	protected static final Logger log = Logger.getLogger(SearchLib.class.getName());
	
	/**
	 * @param keyword search keyword
	 * @param content null or "youtube" or "store-only" 
	 * @param extra null or example "sphere:(zh OR other)"
	 * @param all true or false. false for store. true for cms
	 * @param start start from 0
	 * @param limit count
	 * @return list of channel ids
	 */
	public static List<Long> search(String keyword, String content, String extra, boolean all, int start, int limit) {
        String host = MsoConfigManager.getSearchServer();
		SolrServer server = new HttpSolrServer(host);
		SolrQuery query = new SolrQuery();
		String queryStr = "\"" + keyword + "\"";
		query.set("defType", "edismax");
		query.setQuery(queryStr);		
		query.setFields("id, name");
		query.addSort("updateDate", SolrQuery.ORDER.desc);
                query.set("qf", "name^5 intro");
		if (!all) {
			query.setFilterQueries("isPublic:true");
			query.setFilterQueries("status:(" + NnChannel.STATUS_SUCCESS + " OR " + NnChannel.STATUS_WAIT_FOR_APPROVAL + ")");
		}		
        if (content != null) {
            if (content.equals("youtube")) {
    			query.setFilterQueries("contentType:3");
            } else if (content.equals("store_only")) {
            	query.setFilterQueries("status:0");
            }
        }
        if (extra != null) {
        	//example: extra = "sphere:(zh or other)" 
        	//query.setFilterQueries(extra);
        }
		query.setStart(start);
		query.setRows(limit);
		List<Long> ids = new ArrayList<Long>();
		try {
			QueryResponse response = server.query(query);
			SolrDocumentList docList= response.getResults();
			JSONArray jArray =new JSONArray();
			docList = response.getResults();
			System.out.println("status code:" + response.getStatus());
			for (int i = 0; i < docList.size(); i++) {
			     JSONObject json = new JSONObject(docList.get(i));
			     jArray.put(json);  
			     System.out.println("json:" + json);
			     ids.add(json.getLong("id"));
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ids;		
	}
}
