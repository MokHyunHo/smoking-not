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
	<form action="search.jsp" method="post">
		<tr align="center">
			<td colspan="4">הכנס שם מקום:
			<input type="text" name="search" />
			<input type="submit" value="חפש" /></td>
		</tr>
	</form>

<%
	PersistenceManager pm = PMF.get().getPersistenceManager();
	Logger log = Logger.getLogger(LocationRequest.class.getName());
	log.warning("version 66777266");
	boolean flag = false;
	request.setCharacterEncoding("UTF-8");
	String place = request.getParameter("search");

	Query query = pm.newQuery("SELECT FROM "
			+ LocationRequest.class.getName());
	@SuppressWarnings("unchecked")
	List<LocationRequest> entries = (List<LocationRequest>) query
			.execute();
%>
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
		<td align="center"><%=lr.getName()%></td>
		<td align="center"><%=lr.getAddress()%></td>
		<td align="center"><%=lr.getGoodRate()%></td>
		<td align="center"><%=lr.getBadRate()%></td>
	</tr>

	<%
		}
			}
		}

		if (!flag && place != null) {
	%>
	<tr align="center">
		<td colspan=4>המקום לא נמצא</td>
	</tr>
	<%
		}
	%>
</table>
<%
	pm.close();
%>
<%@include file="footer.html"%>