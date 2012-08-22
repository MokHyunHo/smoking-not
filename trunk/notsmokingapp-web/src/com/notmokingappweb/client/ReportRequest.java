package com.notmokingappweb.client;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class ReportRequest {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String email;
	@Persistent
	private String locationid;
	@Persistent
	private String reportkind;
	@Persistent
	private String date;
	@Persistent
	private int[] reasons;
	@Persistent
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
	
	public String getReportdate(){
		return date;
	}
	
	public String getReportkind(){
		return reportkind;
	}

	public String getComment() {
		return comment;
	}
	
	public int[] getReasons() {
		return reasons;
	}
	
	public Long getReportId() {
		return id;
	}


}