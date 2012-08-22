package com.notmokingappweb.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.notmokingappweb.client.HazardDateComparator;
import com.notmokingappweb.client.HazardDetails;
import com.notmokingappweb.client.HazardDetailsLst;
import com.notmokingappweb.client.HazardRequest;
import com.notmokingappweb.client.LastUsersReports;
import com.notmokingappweb.client.LocationRequest;
import com.notmokingappweb.client.PMF;
import com.notmokingappweb.client.ReportDateComparator;
import com.notmokingappweb.client.ReportDetails;
import com.notmokingappweb.client.ReportRequest;
import com.notmokingappweb.client.UserRequest;

public class GetLastHazardReports extends HttpServlet {
	
	public final int num_of_reports=10;
	private static final Logger log = Logger.getLogger(GetLastHazardReports.class.getName());
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, JDOObjectNotFoundException
	{
			resp.setContentType("application/json;charset=UTF-8");
			log.warning("version 89999888");
			UserRequest ur=null;
			LocationRequest loc=null;
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query query = pm.newQuery("SELECT FROM " + HazardRequest.class.getName());
			List<HazardRequest> result = (List<HazardRequest>) query.execute();
			Collections.sort(result,new HazardDateComparator());
			JSONObject json;
				if(result.size() == 0)
				{
					json = new JSONObject();
					try {
						json.put("hazard_request", "hazard list is empty!");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						log.warning("can't create json object"+e.toString());
					}
				}
			List<HazardDetails> hr = new ArrayList<HazardDetails>();
			HazardDetails hd;
			String user_rank;
			int count=0;
			try
			{
			for (HazardRequest r : result) {
				if (count== num_of_reports) 
					break;
				
						//get location details
						//get user rank
						ur=pm.getObjectById(UserRequest.class,r.getEmail());
						if (ur!=null)
							user_rank=ur.GetRank();
						else
							user_rank="Begginer";
		
						hd = new HazardDetails(user_rank,r.getDate(),r.getAddress(),r.getComment());
						
						hr.add(hd);
						count++;
					
				}
				}catch(Exception e)
				{
					hd=new HazardDetails("no rank","no date","no address","no comment");		
					log.warning("couldn't create reportdetail");
					log.warning(e.toString());
					return;
				}
				finally {
					HazardDetailsLst places= new HazardDetailsLst(hr);
			
				
				// send list to app
				Gson gson = new Gson();
				String HazardStr=gson.toJson(places);
				 json = new JSONObject();
				 
				try {
					json.put("report_request", HazardStr);
				} catch (JSONException e) {
					log.warning("can't create json object"+e.toString());
				}
				
				
				resp.getWriter().write(json.toString());
				resp.getWriter().flush();
				}
			
			
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, JDOObjectNotFoundException {
		doGet(req,resp);
	}


}
