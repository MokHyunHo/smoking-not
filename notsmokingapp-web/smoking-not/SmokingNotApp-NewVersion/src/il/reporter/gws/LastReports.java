package il.reporter.gws;

import java.util.List;

public class LastReports {

	private List<ReportDetails> lst;


	public LastReports(List<ReportDetails> results) {
		this.lst = results;
	}

	public List<ReportDetails> getLst() {
	return lst;
	}

	
}
