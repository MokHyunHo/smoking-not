package com.notmokingappweb.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class HazardDateComparator implements Comparator<HazardRequest> {
	
	public int compare(HazardRequest report1, HazardRequest report2) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date date1 = (Date)df.parse(report1.getDate());
			Date date2 = (Date)df.parse(report2.getDate());
			
			return date2.compareTo(date1);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		
		return 0;
	}

}
