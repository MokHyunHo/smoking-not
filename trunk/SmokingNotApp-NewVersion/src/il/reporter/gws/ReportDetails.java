package il.reporter.gws;

public class ReportDetails {
	
	private String name;
	private String user_rank;
	private String address;
	private String reportkind;
	private String date;
	private String locationid;
	private String comment;
	private int[] reasons;
	
	
	public ReportDetails(String name, String address, String reportkind, String date, String locationid, String comment, int[] reasons) {
		this.name=name;
		this.address=address;
		this.reportkind=reportkind;
		this.date=date;
		this.locationid=locationid;
		this.comment=comment;
		this.reasons=reasons;
	}
	
	public String getPlaceName(){
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getLocationId() {
		return locationid;
	}
	
	public String getReportKind() {
		return reportkind;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getComment() {
		return comment;
	}
	
	public int[] getReasons() {
		return reasons;
	}
	
	public void setUserRank(String r)
	{
		user_rank=r;
	}
	
	public String getUserRank() 
	{
		return user_rank;
	}
	
	
	

}
