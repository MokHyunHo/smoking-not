package com.facebook.android;

public class ReportRequest {

	private String email;
	private String locationid;
	private String reportkind;
	private String date;



	public ReportRequest(String email, String locationid,String reportkind, String date){
		this.email=email;
		this.locationid=locationid;
		this.date=date;
		this.reportkind=reportkind;
	}

	public String getLocationId() {
		return locationid;
	}
	
	public String getReportemail(){
		return email;
	}
	
	public String getReportdate(){
		return date;
	}
	
	public String getReportkind(){
		return reportkind;
	}







}