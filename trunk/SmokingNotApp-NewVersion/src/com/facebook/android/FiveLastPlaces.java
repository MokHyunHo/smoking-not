package com.facebook.android;

import java.util.List;

public class FiveLastPlaces {

	private List<ReportDetails> lst;


	public FiveLastPlaces(List<ReportDetails> results) {
		this.lst = results;
	}

	public List<ReportDetails> getLst() {
	return lst;
	}

	
}
