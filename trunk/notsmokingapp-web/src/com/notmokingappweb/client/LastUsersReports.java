package com.notmokingappweb.client;


import java.util.List;

public class LastUsersReports {

	private List<ReportDetails> lst;


	public LastUsersReports(List<ReportDetails> results) {
		this.lst = results;
	}

	public List<ReportDetails> getLst() {
	return lst;
	}

}
