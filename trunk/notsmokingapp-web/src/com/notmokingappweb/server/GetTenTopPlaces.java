package com.notmokingappweb.server;



import java.io.IOException;
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
import com.notmokingappweb.client.LocationRequest;
import com.notmokingappweb.client.PMF;
import com.notmokingappweb.client.PlacesBadRaiting;
import com.notmokingappweb.client.PlacesRatingComparator;
import com.notmokingappweb.client.TenPlaces;


public class GetTenTopPlaces extends HttpServlet {
	private static final Logger log = Logger.getLogger(GetUser.class.getName());
	public static final int num_of_places =10;
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, JDOObjectNotFoundException
	{
			
			resp.setContentType("application/json;charset=UTF-8");
			log.warning("version7777");
			PersistenceManager pm = PMF.get().getPersistenceManager();
			String action =(String) req.getParameter("action");
			Query query = pm.newQuery("SELECT FROM "+ LocationRequest.class.getName() );
			List<LocationRequest> result = (List<LocationRequest>) query.execute();
			if (action.compareTo("top_ten")==0)
				Collections.sort(result, new PlacesRatingComparator());
			if (action.compareTo("bad_top_ten")==0)
				Collections.sort(result, new PlacesBadRaiting());
			int count = 0;
			TenPlaces topten = new TenPlaces();
			for (LocationRequest pl : result) {
				if (count == num_of_places)
					break;
				count++;
				topten.AddLocation(pl);
			}
			
			// send toptenplaces to app
			Gson gson = new Gson();
			JSONObject json;
			String TopTenStr=gson.toJson(topten);
			json = new JSONObject();
			 
			try {
				json.put("Top_Ten", TopTenStr);
			} catch (JSONException e) {
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
