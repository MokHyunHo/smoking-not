package com.notmokingappweb.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.notmokingappweb.client.PMF;
import com.notmokingappweb.client.UserRequest;

public class CreateNewUser extends HttpServlet {

	private static final Logger log = Logger.getLogger(GetUser.class.getName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, JDOObjectNotFoundException
	{
			
			resp.setContentType("application/json;charset=UTF-8");
			log.warning("version 5777777");
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
				log.warning("the user that was added is " +mail);
				ur=new UserRequest(mail,0,null);
				pm.makePersistent(ur);
				}

			
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException, JDOObjectNotFoundException {
		doGet(req,resp);
	}
}
