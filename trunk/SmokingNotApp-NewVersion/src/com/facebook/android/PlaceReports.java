package com.facebook.android;

import java.util.List;


public class PlaceReports {

	private List<ReportRequest> lst;
	
	public PlaceReports(List<ReportRequest> lst) {
		this.lst = lst;
	}
	
	public List<ReportRequest> getLst() {
		return lst;
	}


	
}
