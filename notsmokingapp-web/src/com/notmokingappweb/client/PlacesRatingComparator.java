package com.notmokingappweb.client;


import java.util.Comparator;


public class PlacesRatingComparator implements Comparator<LocationRequest> {

	public int compare(LocationRequest loc1, LocationRequest loc2)  {
		//report1.getDate().
		//Date d1 = new Date()
		try {
			
			int good_rate1= loc1.getGoodRate();
			int good_rate2=loc2.getGoodRate();
			int bad_rate1=loc1.getBadRate();
			int bad_rate2=loc2.getBadRate();
			return (good_rate2-bad_rate2)-(good_rate1-bad_rate1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}  
		
		return 0;
	}
	
}
