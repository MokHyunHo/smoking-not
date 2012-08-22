package il.reporter.gws;



public class HazardDetails {


	private String rank;
	private String date;
	private String address;
	private String comment;
	private String status;
	
	
	public HazardDetails(String rank,String date, String address, String comment)
	{
		this.rank=rank;
		this.date=date;
		this.address=address;
		
		this.comment=comment;
	}
	
	public String getRank() 
	{
		return rank;
	}
	
	public String getDate()
	{
		return date;
	}
	
	
	public String getAddress() 
	{
		return address;
	}
	
	public String getStatus() 
	{
		return status;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public void SetStatus(String s){
		this.status=s;
	}
	
	

}
