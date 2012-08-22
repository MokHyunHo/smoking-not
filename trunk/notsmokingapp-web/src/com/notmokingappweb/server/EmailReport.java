package com.notmokingappweb.server;


import java.io.BufferedReader;
import java.io.IOException;


import java.util.Properties;
import java.util.logging.Logger;
 
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;



import com.google.gson.Gson;
import com.notmokingappweb.client.EmailDetails;



 
@SuppressWarnings("serial")
public class EmailReport extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(EmailReport.class.getName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
		
			 
			@Override
			public void doPost(HttpServletRequest req, HttpServletResponse resp)
					throws ServletException, IOException {
				//doGet(req, resp);
				
				resp.setContentType("application/json;charset=UTF-8");
				log.warning("version 88888886444444444");
				byte [] bmp;
				BufferedReader reader = req.getReader();
				StringBuilder sb = new StringBuilder();
				String line = reader.readLine();
				String ed_str;
				Gson gson = new Gson();
				EmailDetails ed= null;
				String strCallResult = "";
				
				while (line != null) {
					sb.append(line + "\n");
					line = reader.readLine();
				}
				reader.close();
				String data = sb.toString();
				JSONObject json = null;
				log.warning("data recieved is"+data);
				String strTo = "smokingnot2012@gmail.com";
				String strSubject = "New Report";
				String title = "This report was sent by TheReporter App. \n";
				String strBody = null;
				String strUsercomments;
				try 
				{
					
					json = new JSONObject(data);
					String action=(String)json.get("action");
					if (action.equals("send_hazardemail")){
						ed_str= (String)json.get("hazard_email");
						ed=gson.fromJson(ed_str, EmailDetails.class);
						if (ed.getComment()==null)
							strUsercomments="Non.";
						else
							strUsercomments=ed.getComment();
						//fill in the mail
						strBody=title+"The location that was reported is "+ ed.getLocationName()+"\n"+
								"This report was sent by "+ ed.getUserName()+ ".\n"+
								"His details are: \n"+
								"email= "+ed.getUserMail()+ ";\n"+
								"phone number= "+ed.getUserPhone()+ ";\n"+
								"address= "+ed.getUserAddress()+";\n"+
								"Comment: "+strUsercomments;
								 
					}
					
					if (action.equals("send_reportemail")){
						ed_str= (String)json.get("report_email");
						ed=gson.fromJson(ed_str, EmailDetails.class);
						if (ed.getComment()==null)
							strUsercomments="Non.";
						else
							strUsercomments=ed.getComment();
						//fill in the mail
						strBody=title+"The location that was reported is "+ ed.getLocationName()+"\n"+
								"The reasons he pointed are :"+ed.getText()+".\n"+
								"This report was sent by "+ ed.getUserName()+ ".\n"+
								"His details are: \n"+
								"email= "+ed.getUserMail()+ ";\n"+
								"phone number= "+ed.getUserPhone()+ ";\n"+
								"address= "+ed.getUserAddress()+";\n"+
								"Comment: "+strUsercomments;
								 
					}
					//Call the GAEJ Email Service
					Properties props = new Properties();
					Session session = Session.getDefaultInstance(props, null);
					Multipart mp = new MimeMultipart();
					Message msg = new MimeMessage(session);
					msg.setFrom(new InternetAddress("smokingnot2012@gmail.com","Smoking Not application"));
					msg.addRecipient(Message.RecipientType.TO,new InternetAddress(strTo));
					
					msg.setSubject(strSubject);
					msg.setText(strBody);
					bmp=ed.getPicture();
					
					MimeBodyPart htmlPart = new MimeBodyPart();
					htmlPart.setContent(strBody,"text/html");
					mp.addBodyPart(htmlPart);
					if (bmp!=null) 
					{
						
						MimeBodyPart attachment = new MimeBodyPart();
					    attachment.setFileName("picture.png");
					    ByteArrayDataSource dataSource= new ByteArrayDataSource(bmp, "image/png");
					    attachment.setDataHandler(new DataHandler (dataSource));
					    attachment.setHeader("Content-Id", "<image>");
					  //  attachment.setContent(bmp, "application/png");
					    mp.addBodyPart(attachment);
				        msg.setContent(mp);
					} 
					
					Transport.send(msg);
					strCallResult = "Success: " + "Email has been delivered.";
					log.warning(strCallResult);
					
					}
					catch (Exception ex) {
						strCallResult = "Fail: " + ex.getMessage();
						log.warning(strCallResult);
						log.warning(ex.getStackTrace().toString());
						
						}
					

						
							
						
			}
			 
			}