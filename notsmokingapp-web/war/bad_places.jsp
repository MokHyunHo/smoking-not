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
			int count2 = 0;
			Collections.sort(result, new PlacesBadRaiting());
			
			for (LocationRequest pla : result) {
				if (count2 == 5)
					break;
		%>                        		<tr>
			<td><%=pla.getName()%></td>
			<td><%=pla.getAddress()%></td>
			<td><%=pla.getGoodRate()%></td>
			<td><%=pla.getBadRate()%></td>
		</tr> <%
				count2++;
}
%></table>
	<%
		pm.close();%>
<%@include file="footer.html"%>