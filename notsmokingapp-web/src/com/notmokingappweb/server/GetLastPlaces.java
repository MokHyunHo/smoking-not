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
import com.notmokingappweb.client.LastUsersReports;
import com.notmokingappweb.client.LocationRequest;
import com.notmokingappweb.client.PMF;
import com.notmokingappweb.client.ReportDetails;
import com.notmokingappweb.client.ReportRequest;
import com.notmokingappweb.client.UserRequest;

public class GetLastPlaces extends HttpServlet {
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
			//log.warning("user str in SendJson is="+UserStr); 
			
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
			
				Query query = pm.newQuery("SELECT FROM " + ReportRequest.class.getName());
				List<ReportRequest> result = (List<ReportRequest>) query.execute();
				JSONObject json;
				if(result.size() == 0)
				{
					json = new JSONObject();
					try {
						json.put("report_request", "report list is empty!");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						log.warning("can't create json object"+e.toString());
					}
				}
				List<ReportDetails> rr = new ArrayList<ReportDetails>();
				ReportDetails rd;
				log.warning(mail);
				try
				{
				for (ReportRequest r : result) {
					if (r.getReportemail() != null && r.getReportemail().equals(mail)) 
					{
						
						LocationRequest loc=pm.getObjectById(LocationRequest.class,r.getLocationId());
						if (loc!=null)
							rd=new ReportDetails(loc.getName(),loc.getAddress(),r.getReportkind(),r.getReportdate(),
								r.getLocationId(),r.getComment(),r.getReasons());
						else
							rd= new ReportDetails("no place","no address",r.getReportkind(),r.getReportdate(),
									r.getLocationId(),r.getComment(),r.getReasons());
						rr.add(rd);
						log.warning(r.getLocationId());
					}
				}
				}catch(Exception e)
				{
					rd=new ReportDetails("no place","no address","good","2012",
							"bla","lala",null);
					log.warning("couldn't create reportdetail");
					log.warning(e.toString());
					return;
				}
				finally {
				LastUsersReports places= new LastUsersReports(rr);
			
				
				// send list to app
				Gson gson = new Gson();
				String ReportStr=gson.toJson(places);
				 json = new JSONObject();
				 
				try {
					json.put("report_request", ReportStr);
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
