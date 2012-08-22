package il.reporter.gws;

public class HazardRequest {
	
	
	private String email;
	private String date;
	private String address;
	private double latitude;
	private double longitude;
	private String comment;
	
	
	public HazardRequest(String email,String date, String address, double latitude, double longitude, String comment)
	{
		this.email=email;
		this.date=date;
		this.address=address;
		this.latitude=latitude;
		this.longitude=longitude;
		this.comment=comment;
	}
	
	public String getEmail() 
	{
		return email;
	}
	
	public String getDate()
	{
		return date;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude() 
	{
		return longitude;
	}
	
	public String getAddress() 
	{
		return address;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	

	
}
