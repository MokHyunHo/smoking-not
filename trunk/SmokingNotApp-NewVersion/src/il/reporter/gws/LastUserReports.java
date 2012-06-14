package il.reporter.gws;

import java.util.List;

public class LastUserReports {

	private List<ReportDetails> lst;


	public LastUserReports(List<ReportDetails> results) {
		this.lst = results;
	}

	public List<ReportDetails> getLst() {
	return lst;
	}

	
}
