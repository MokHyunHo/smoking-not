package com.notmokingappweb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.notmokingappweb.client.HazardRequest;
import com.notmokingappweb.client.PMF;
import com.notmokingappweb.client.UserRequest;

public class UpdateHazard extends HttpServlet {
	
	
	private static final Logger log = Logger.getLogger(UpdateUser.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("application/json;charset=UTF-8");
		log.warning("version 666666666");
		int flag=0;
		UserRequest ur = null,user_found = null;
		HazardRequest hr=null;
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
				log.warning(ur.GetEmail());

				//find user in database
				user_found=pm.getObjectById(UserRequest.class, ur.GetEmail());				
				//user in database
				if (user_found!=null){
					flag=1;
					user_found.IncreaseScore(ur.GetScore());
					ur=user_found;	
					}
					
				else 				
					log.warning("getobjectbyid failed, need to create new user in database");
			}
			
			if (action.compareTo("update_hazard")==0)
			{
				flag=2;
				str=(String)json.getString("hazard_request");
				hr=gson.fromJson(str, HazardRequest.class);
			}
			
			
			}catch(Exception e)
			{
				//insert new user in to the database
				
				log.warning(e.getMessage());
					
			}
			finally 
			{
				if ( (flag==1) ) {
					pm.makePersistent(ur);
				}	
				if (flag==2) {
					pm.makePersistent(hr);
				
				}
				
				pm.flush();
				pm.close();
			}
	}

}
