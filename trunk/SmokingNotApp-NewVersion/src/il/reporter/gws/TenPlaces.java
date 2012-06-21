package il.reporter.gws;


public class TenPlaces {
	public static final int num_places=10;
	private LocationRequest places[];
	int count;

	public TenPlaces() {
		this.places = new LocationRequest[num_places];
		count=0;
	}

	public LocationRequest[] getTenPlaces() {
		return places;
	}
	
	public void AddLocation( LocationRequest lr) {
		places[count]=lr;
		count++;
	}

}
