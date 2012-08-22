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
import com.notmokingappweb.client.PMF;
import com.notmokingappweb.client.UserRequest;

public class GetUser extends HttpServlet {
	private static final Logger log = Logger.getLogger(GetUser.class.getName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, JDOObjectNotFoundException
	{
			
			resp.setContentType("application/json;charset=UTF-8");
			log.warning("version 53434343");
			UserRequest ur=null;
			String mail =(String) req.getParameter("mail");
			PersistenceManager pm = PMF.get().getPersistenceManager();
			//log.warning("user str in SendJson is="+UserStr); 
			
			//try to get user from database
			try {
				ur=pm.getObjectById(UserRequest.class, mail);
			}catch (Exception e)
				{
				log.warning("Error: user wasn't found in database"+ e.toString());
				ur=new UserRequest(mail,0,null);
				pm.makePersistent(ur);
				}
			finally 
			{
				// send user to app
				Gson gson = new Gson();
				String UserStr=gson.toJson(ur);
				JSONObject json = new JSONObject();
				try {
					json.put("user_req", UserStr);
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
