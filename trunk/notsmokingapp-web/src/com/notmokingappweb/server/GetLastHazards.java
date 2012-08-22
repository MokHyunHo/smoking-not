package com.notmokingappweb.server;

import java.io.IOException;
import java.util.ArrayList;
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
import com.notmokingappweb.client.HazardRequest;
import com.notmokingappweb.client.LastHazardsReports;
import com.notmokingappweb.client.PMF;
import com.notmokingappweb.client.UserRequest;

public class GetLastHazards extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(GetUser.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, JDOObjectNotFoundException
	{
			resp.setContentType("application/json;charset=UTF-8");
			log.warning("version 89999888");
			UserRequest ur=null;
			String mail =(String) req.getParameter("mail");
			PersistenceManager pm = PMF.get().getPersistenceManager();
			
			
			//try to get user from database
			try {
				ur=pm.getObjectById(UserRequest.class, mail);
			}catch (Exception e)
				{
				log.warning("add new user into database");
				ur=new UserRequest(mail,0,null);
				pm.makePersistent(ur);
				}
			finally 
			{
			
				Query query = pm.newQuery("SELECT FROM " + HazardRequest.class.getName());
				List<HazardRequest> result = (List<HazardRequest>) query.execute();
				JSONObject json;
				if(result.size() == 0)
				{
					json = new JSONObject();
					try {
						json.put("hazard_request", "report list is empty!");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						log.warning("can't create json object"+e.toString());
					}
				}
				List<HazardRequest> rr = new ArrayList<HazardRequest>();
				log.warning(mail);
				try
				{
				for (HazardRequest r : result) {
					if (r.getEmail() != null && r.getEmail().equals(mail)) 
					{
						rr.add(r);
					}
				}
				}catch(Exception e)
				{
					log.warning(e.toString());
					return;
				}
				finally {
					LastHazardsReports places= new LastHazardsReports(rr);
			
				
				// send list to app
				Gson gson = new Gson();
				String ReportStr=gson.toJson(places);
				 json = new JSONObject();
				 
				try {
					json.put("hazard_request", ReportStr);
				} catch (JSONException e) {
					log.warning("can't create json object"+e.toString());
				}
				
				
				resp.getWriter().write(json.toString());
				resp.getWriter().flush();
				}
			}
			
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, JDOObjectNotFoundException {
		doGet(req,resp);
	}

	
}
