package il.reporter.gws;
import java.util.ArrayList;
import java.util.List;

public class TenPlaces {
	
	
	private List<LocationRequest> places;
	int count=0;


	public TenPlaces() {
		this.places = new ArrayList<LocationRequest>();
		count=0;
	}

	public List<LocationRequest>  getTenPlaces() {
		return places;
	}
	
	public  void AddLocation (LocationRequest lr) {
		places.add(lr);
	}
	
	

}
