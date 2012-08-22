package il.reporter.gws;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class HazardDateComparator implements Comparator<HazardRequest> {

	public int compare(HazardRequest hazard1, HazardRequest hazard2) {
		//report1.getDate().
		//Date d1 = new Date()
		try {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date date1 = (Date)df.parse(hazard1.getDate());
			Date date2 = (Date)df.parse(hazard2.getDate());
			
			return date2.compareTo(date1);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		
		return 0;
	}
}