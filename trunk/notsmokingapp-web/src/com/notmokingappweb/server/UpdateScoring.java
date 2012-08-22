package com.notmokingappweb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.notmokingappweb.client.LocationRequest;
import com.notmokingappweb.client.PMF;
import com.notmokingappweb.client.ReportRequest;
import com.notmokingappweb.client.UserRequest;

public class UpdateScoring extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(UpdateScoring.class.getName());

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
		log.warning("version 2545454");
		UserRequest ur,user_found = null;
		ReportRequest new_report=null;
		LocationRequest loc_found=null;
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
		
		try {
			pm.flush();
			json = new JSONObject(data);
			String action=(String)json.get("action");
			if (action.compareTo("update_score")==0)
			{
				
				str=(String)json.getString("report_request");
				new_report=gson.fromJson(str, ReportRequest.class);
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				Date current_date= formatter.parse(new_report.getReportdate());
				Date report_date;
				Query query = pm.newQuery("SELECT FROM " + ReportRequest.class.getName());
				List<ReportRequest> result = (List<ReportRequest>) query.execute();
				log.warning("the id of the place is"+new_report.getLocationId());
				for (ReportRequest report : result) {
					if (report.getLocationId().compareTo(new_report.getLocationId())==0) {
						log.warning("ok1111");
						if (report.getReportemail().compareTo(new_report.getReportemail())!=0){
							log.warning("the email of the user is "+new_report.getReportemail());
							log.warning("the email of the user that was found id "+report.getReportemail());
						//	report_date=formatter.parse(report.getReportemail());
							//pm.flush();
							if (report.getReportkind().compareTo(new_report.getReportkind())==0){
							//if (report_date.getMonth()==current_date.getMonth()) {
								log.warning("reports kind match");
								user_found=pm.getObjectById(UserRequest.class, report.getReportemail());
								user_found.IncreaseScore(1);
								log.warning("user found is "+report.getReportemail());
								loc_found=pm.getObjectById(LocationRequest.class,report.getLocationId());
								log.warning("location found is "+loc_found.getName());
								user_found.AddNotification(loc_found.getName());
								log.warning(user_found.GetMessage());
								//ur=user_found;
								//pm.flush();
								pm.makePersistent(user_found);
								}
								
							}
						}
					}
				}
			
			
				
				if (action.compareTo("clear")==0){
					str=(String)json.getString("user_request");
					user_found=gson.fromJson(str, UserRequest.class);
					user_found.SetMessage("empty");
					//ur=user_found;
					pm.makePersistent(user_found);
				}
		}catch (Exception e){
			e.printStackTrace();
		}
		finally {
			pm.close();
		}
				
		
	}
}
