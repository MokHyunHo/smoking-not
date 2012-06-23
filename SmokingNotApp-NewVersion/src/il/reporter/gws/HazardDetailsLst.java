package il.reporter.gws;

import java.util.List;

public class HazardDetailsLst {

	private List<HazardDetails> lst;


	public HazardDetailsLst(List<HazardDetails> results) {
		this.lst = results;
	}

	public List<HazardDetails> getLst() {
		return lst;
	}
}
