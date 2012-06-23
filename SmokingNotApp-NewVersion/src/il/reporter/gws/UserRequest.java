package il.reporter.gws;

import android.content.Context;

public class UserRequest {

	private String email;
	private int score;
	private String lastplaceId;
	private String date;
	private String rank = "";
	private String nextRank = "";
	private int nextRankScore = 0;
	private String message;

	public UserRequest(String email, int score, String place, String date) {
		this.email = email;
		this.score = score;
		this.lastplaceId = place;
		this.date = date;
		message = "empty";

	}

	public String GetEmail() {
		return this.email;
	}

	public String GetLastPlace() {
		return lastplaceId;
	}

	public void SetLastplace(String l) {
		lastplaceId = l;
	}

	public String GetLastReportDate() {
		return date;
	}

	public void SetLastReportDate(String last_date) {
		date = last_date;
	}

	public int GetScore() {
		return this.score;
	}

	public void SetMessage(String m) {
		message = m;
	}

	public String GetMessage() {
		return message;
	}

	public void IncreaseScore(int sum) {
		score = sum + score;
	}

	public void handleRanks(Context context) {

		String user_ranks[] = context.getResources().getStringArray(
				R.array.user_ranks);
		int user_rank_scores[] = context.getResources().getIntArray(
				R.array.user_ranks_scores);

		int i;
		for (i = 0; i < user_rank_scores.length; i++) {
			if (score < user_rank_scores[i])
				break;
		}

		rank = user_ranks[i];
		nextRankScore = user_rank_scores[i];
		nextRank = (i < user_rank_scores.length - 1 ? user_ranks[i + 1] : "");

	}

	public String GetRank() {

		return rank;
	}
	
	public String GetNextRank() {

		return nextRank;
	}
	
	public int GetNextRankScore() {

		return nextRankScore;
	}

	public void AddNotification(String loc) {
		if (message.compareTo("empty") == 0)
			message = loc;
		else
			message = message + "#" + loc;
	}

}
