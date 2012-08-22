<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="javax.jdo.PersistenceManager"%>
<%@page import="javax.jdo.Query"%>
<%@page import="java.util.List"%>
<%@page import="com.notmokingappweb.client.PMF"%>
<%@page import="com.notmokingappweb.client.LocationRequest"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.notmokingappweb.client.PlacesRatingComparator"%>
<%@page import="java.util.Collections"%>
<%@page import="com.notmokingappweb.client.PlacesBadRaiting"%>
<%@include file="header.html"%>

<table width="880" align="center"
	style="font-family: Arial, Helvetica, sans-serif;">
	<form action="places_website.jsp" method="post">
		<tr>
			<td>הכנס שם מקום:
			<td>
			<td><input type="text" name="search" /></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" value="חפש" /></td>
		</tr>
	</form>
</table>

<%
	PersistenceManager pm = PMF.get().getPersistenceManager();
	Logger log = Logger.getLogger(LocationRequest.class.getName());
	log.warning("version 66666");
	boolean flag = false;
	request.setCharacterEncoding("UTF-8");
	String place = request.getParameter("search");

	Query query = pm.newQuery("SELECT FROM "
			+ LocationRequest.class.getName());
	@SuppressWarnings("unchecked")
	List<LocationRequest> entries = (List<LocationRequest>) query
			.execute();
%>
<table width="880" align="center"
	style="font-family: Arial, Helvetica, sans-serif;">
	<tr style="font-weight: bold; color: #06C;">
		<td align="center">שם</td>
		<td align="center">כתובת</td>
		<td align="center">דירוג חיובי</td>
		<td align="center">דירוג שלילי</td>
	</tr>
	<%
		for (LocationRequest lr : entries) {

			//if (lr.getName().toLowerCase().contains(place.toLowerCase()) ) 
			if (lr.getName() != null && place != null) {
				log.warning("lr name is " + lr.getName());
				if ((lr.getName().indexOf(place)) != -1) {
					flag = true;
	%>
	<tr>
		<td><%=lr.getName()%></td>
		<td><%=lr.getAddress()%></td>
		<td><%=lr.getGoodRate()%></td>
		<td><%=lr.getBadRate()%></td>
	</tr>

	<%
		}
			}
		}

		if (!flag && place != null) {
	%>
	<tr>
		<td colspan=4>No Rating</td>
	</tr>
	<%
		}
	%>
</table>
<%
	pm.close();
%>
<%@include file="footer.html"%>