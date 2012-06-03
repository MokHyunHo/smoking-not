package com.facebook.android;

public class ReportRequest {

	private String email;
	private String locationid;
	private String reportkind;
	private String date;
	private String[] reasons;
	private String comment;
	



	public ReportRequest(String email, String locationid,String reportkind, String date,String [] reasons,String comment){
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
	
	public String[] getReasons(){
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