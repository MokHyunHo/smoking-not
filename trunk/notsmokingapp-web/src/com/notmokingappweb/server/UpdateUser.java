package com.notmokingappweb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.notmokingappweb.client.LocationRequest;
import com.notmokingappweb.client.PMF;
import com.notmokingappweb.client.ReportRequest;
import com.notmokingappweb.client.UserRequest;

public class UpdateUser extends HttpServlet {
	private static final Logger log = Logger.getLogger(UpdateUser.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("application/json;charset=UTF-8");
		log.warning("version 99993");
		int flag=0;
		UserRequest ur = null,user_found = null;
		ReportRequest rr=null;
		LocationRequest loc=null,loc_found=null;
		String str;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		BufferedReader reader = req.getReader();
		StringBuilder sb = new StringBuilder();
		String line = reader.readLine();
		Gson gson = new Gson();
		
		while (line != null) {
			sb.append(line + "\n");
			line = reader.readLine();
		}
		reader.close();
		String data = sb.toString();
		JSONObject json = null;
		log.warning("data recieved is"+data);
		
		try 
		{
			pm.flush();
			json = new JSONObject(data);
			String action=(String)json.get("action");
			
			if (action.compareTo("update_ur")==0)
			{
				
				str=(String)json.getString("user_request");
				ur=gson.fromJson(str, UserRequest.class);
				log.warning("user's score accepted is"+Integer.toString(ur.GetScore()));
				log.warning("before getobjectid");
				log.warning(ur.GetEmail());

				//find user in database
				user_found=pm.getObjectById(UserRequest.class, ur.GetEmail());				
				//user in database
				if (user_found!=null){
					
					
					try {
					//check if the user reported twice on the same place
					Query query = pm.newQuery("SELECT FROM " + ReportRequest.class.getName());
					List<ReportRequest> result = (List<ReportRequest>) query.execute();				
					
					SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
					Date current_date= formatter.parse(ur.GetLastReportDate());
					Date d=null;;
					
					for (ReportRequest r : result) {
						if (r.getReportemail() != null && r.getReportdate()!=null && r.getLocationId()!=null && r.getLocationId().equals(ur.GetLastPlace()) && r.getReportemail().equals(ur.GetEmail())) {
							log.warning("the user that was found is"+r.getReportemail());

							d= formatter.parse(r.getReportdate());
							log.warning("his current date is"+current_date.toString());
							log.warning("his last date is"+d.toString());
							if (d.compareTo(current_date)==0) {
								flag=-1;
							}
						}
					}
					
					
					} catch (Exception e) {
						log.warning("exception was thrown because of a search"+e);
					}
					if (flag==0)
					{
						user_found.SetLastplace("empty");
						user_found.IncreaseScore(ur.GetScore());
						user_found.SetLastplace(ur.GetLastPlace());
						log.warning("the user place is"+user_found.GetLastPlace());
						ur=user_found;
					}
					if (flag==-1)
					{
						user_found.SetLastplace("Report Exsits");
						ur=user_found;
					}
				}
				else 				
					log.warning("getobjectbyid failed, need to create new user in database");
			}
			if (action.compareTo("update_location")==0)
			{
				flag=1;
				str=(String)json.getString("location_request");
				loc=gson.fromJson(str, LocationRequest.class);
				log.warning("the id of the place is"+loc.getId());
				log.warning("increase good rate of the place by "+Integer.toString(loc.getGoodRate()));
				log.warning("before getobjectid");

				//find location in database
				loc_found=pm.getObjectById(LocationRequest.class, loc.getId());	
				//location is in database
				if (loc_found!=null){
					loc_found.increaseBadRate(loc.getBadRate());
					loc_found.increaseGoodRate(loc.getGoodRate());
					
					log.warning("the  bad rate of the place is"+loc.getBadRate());
					loc=loc_found;
				}
				else 				
					log.warning("getobjectbyid failed, need to create new location in database");
			}
			
			if (action.compareTo("update_report")==0)
			{
				flag=2;
				str=(String)json.getString("report_request");
				rr=gson.fromJson(str, ReportRequest.class);
				pm.flush();
				user_found=pm.getObjectById(UserRequest.class, rr.getReportemail());
				log.warning("the id of the place is"+rr.getLocationId());
			}
			
			
			}catch(Exception e)
			{
				//insert new user in to the database
				log.warning("need to create new user in database");
				//log.warning("the user we tried to find is"+ur.GetEmail());
					
			}
			finally 
			{
				if ( (flag==0) || (flag==-1) ) {
					pm.makePersistent(ur);
					log.warning("this is usr= " + ur.GetEmail());
				}	
				if (flag==1) {
					pm.makePersistent(loc);
					log.warning("this is the name of the place= " + loc.getName());
				}
				else
					pm.makePersistent(rr);
				pm.flush();
				pm.close();
			}
	}

}
