package il.reporter.gws;

import java.util.List;

public class LastUserHazards {

	private List<HazardRequest> lst;


	public LastUserHazards(List<HazardRequest> results) {
		this.lst = results;
	}

	public List<HazardRequest> getLst() {
	return lst;
	}

	
}
