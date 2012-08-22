package com.notmokingappweb.client;

import java.util.List;

public class LastHazardsReports {

	
	private List<HazardRequest> lst;


	public LastHazardsReports(List<HazardRequest> results) {
		this.lst = results;
	}

	public List<HazardRequest> getLst() {
		return lst;
	}
}
