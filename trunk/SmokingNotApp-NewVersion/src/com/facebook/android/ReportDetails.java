package com.facebook.android;

public class ReportDetails {
	
	private String name;
	private String address;
	private String reportkind;
	private String date;
	private String locationid;
	private String comment;
	private String[] reasons;
	
	
	public ReportDetails(String name, String address, String reportkind, String date, String locationid, String comment, String[] reasons) {
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
	
	public String[] getReasons() {
		return reasons;
	}
	

}
