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

	public class GetHistoryPlaces extends HttpServlet {
		
		private static final Logger log = Logger.getLogger(GetHistoryPlaces.class.getName());
		@SuppressWarnings("unchecked")
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, JDOObjectNotFoundException
		{
				
				resp.setContentType("application/json;charset=UTF-8");
				log.warning("version 7777773");
				LocationRequest loc=null;
				UserRequest ur =null;
				String locid =(String) req.getParameter("locationid");
				PersistenceManager pm = PMF.get().getPersistenceManager();
				List<ReportDetails> rr = new ArrayList<ReportDetails>();
				JSONObject json = new JSONObject();
				
				//try to get user from database
				try {
					loc=pm.getObjectById(LocationRequest.class, locid);
					Query query = pm.newQuery("SELECT FROM " + ReportRequest.class.getName());
					List<ReportRequest> result = (List<ReportRequest>) query.execute();
					
					if(result.size() == 0)
					{
						
						try {
							json.put("report_request", "report list is empty!");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							log.warning("can't create json object"+e.toString());
						}
					}
					
					
					
					try
					{
					for (ReportRequest r : result) {
						if (r.getLocationId() != null && r.getLocationId().equals(locid)) 
						{
							ur=pm.getObjectById(UserRequest.class,r.getReportemail());
							
							ReportDetails rd=new ReportDetails(loc.getName(),loc.getAddress(),r.getReportkind(),r.getReportdate(),
									r.getLocationId(),r.getComment(),r.getReasons());
							if (ur!=null)
								rd.setUserRank(ur.GetRank());
							else
								rd.setUserRank("Begginer");
							rr.add(rd);
							log.warning(r.getLocationId());
						}
					}
					}catch(Exception e)
					{
						log.warning(e.toString());
						return;
					}
				}catch (Exception e)
				{
					log.warning("location doesn't exist in database");
			
				}
					LastUsersReports places= new LastUsersReports(rr);
				
					
					// send list to app
					Gson gson = new Gson();
					String ReportStr=gson.toJson(places);
					 json = new JSONObject();
					try {
						json.put("report_request", ReportStr);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						log.warning("can't create json object"+e.toString());
					}
					
					
					resp.getWriter().write(json.toString());
					resp.getWriter().flush();

				
				
			
				

				
		}
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException, JDOObjectNotFoundException {
			doGet(req,resp);
		}

	}

	
	
