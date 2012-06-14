package il.reporter.gws;

public class ReportRequest {

	private String email;
	private String locationid;
	private String reportkind;
	private String date;
	private int[] reasons;
	private String comment;
	



	public ReportRequest(String email, String locationid,String reportkind, String date,int [] reasons,String comment){
		this.email=email;
		this.locationid=locationid;
		this.date=date;
		this.reportkind=reportkind;
		this.reasons=reasons;
		this.comment=comment;
	}

	public String getLocationId() {
		return locationid;
	}
	
	public String getReportemail(){
		return email;
	}
	
	public int[] getReasons(){
		return reasons;
	}
	
	public String getComment() {
		return comment;
	}
	
	public String getReportdate(){
		return date;
	}
	
	public String getReportkind(){
		return reportkind;
	}







}