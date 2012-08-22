package com.notmokingappweb.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.notmokingappweb.client.LocationRequest;
import com.notmokingappweb.client.PMF;
import com.notmokingappweb.client.UserRequest;

public class GetLocation extends HttpServlet {
	private static final Logger log = Logger.getLogger(GetLocation.class.getName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, JDOObjectNotFoundException
	{
			
			resp.setContentType("application/json;charset=UTF-8");
			log.warning("version 7");
			LocationRequest loc=null;
			String locid =(String) req.getParameter("locationid");
			PersistenceManager pm = PMF.get().getPersistenceManager();
			
			//try to get location from database
			try {
				loc=pm.getObjectById(LocationRequest.class, locid);
			}catch (Exception e)
				{
				log.warning("place has no rating");
				
				}
			finally 
			{
				// send user to app
				Gson gson = new Gson();
				String LocationStr;
				if (loc==null)
					LocationStr="NotinDataBase";
				else
					LocationStr=gson.toJson(loc);
				JSONObject json = new JSONObject();
				try {
					json.put("location_req", LocationStr);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
