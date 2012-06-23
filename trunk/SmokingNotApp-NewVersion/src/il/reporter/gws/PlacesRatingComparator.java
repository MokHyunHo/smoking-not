package il.reporter.gws;

import java.util.Comparator;

public class PlacesRatingComparator implements Comparator<GooglePlace> {

	public int compare(GooglePlace loc1, GooglePlace loc2)  {

		try {
			
			int good_rate1= loc1.goodRate;
			int good_rate2=loc2.goodRate;
			int bad_rate1=loc1.badRate;
			int bad_rate2=loc2.badRate;
			
			return (good_rate2-bad_rate2)-(good_rate1-bad_rate1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}  
		
		return 0;
	}
}