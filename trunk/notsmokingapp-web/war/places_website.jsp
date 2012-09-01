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
<%@include file="header.html" %>
<table width="880" align="center" style="font-family:Arial, Helvetica, sans-serif;">
		<tr style="font-weight:bold;color:#06C;">
			<td align="center">שם</td>
			<td align="center">כתובת</td>
			<td align="center">דירוג חיובי</td>
			<td align="center">דירוג שלילי</td>
		</tr>
				<%
					PersistenceManager pm = PMF.get().getPersistenceManager();
					Query bestq = pm.newQuery("SELECT FROM "
							+ LocationRequest.class.getName()
							+ " ORDER BY good_rate DESC");
					
					@SuppressWarnings("unchecked")
					List<LocationRequest> result = (List<LocationRequest>) bestq
							.execute();
					Collections.sort(result, new PlacesRatingComparator());
					int count = 0;
					for (LocationRequest pl : result) {
						if (count == 5)
							break;
						%>
                        		<tr>
			<td align="center"><%=pl.getName()%></td>
			<td align="center"><%=pl.getAddress()%></td>
			<td align="center"><%=pl.getGoodRate()%></td>
			<td align="center"><%=pl.getBadRate()%></td>
		</tr>
                         <%
 						count++;
 	}
 %></table>
 	<%
		pm.close();%>
<%@include file="footer.html"%>