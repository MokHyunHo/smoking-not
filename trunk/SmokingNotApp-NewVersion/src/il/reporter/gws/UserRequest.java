package il.reporter.gws;

public class UserRequest {

	
	private String email;	
	private int score;	
	private String lastplaceId;	
	private String date;
	private String rank;
	private String message;
	
	public UserRequest (String email, int score, String place,String date){
		this.email=email;
		this.score=score;
		this.lastplaceId=place;
		this.date=date;
		message="empty";
		
	}
	
	public String GetEmail(){
		return this.email;
	}	
	
	public String GetLastPlace(){
		return lastplaceId;
	}
	
	public void SetLastplace (String l) 
	{
		lastplaceId=l;
	}
	
	public String GetLastReportDate() {
		return date;
	}
	
	public void SetLastReportDate(String last_date) {
		date=last_date;
	}
	
	public int GetScore() {
		return this.score;
	}
	
	public void SetMessage(String m){
		message=m;
	}
	
	public String GetMessage(){
		return message;
	}
	
	public void IncreaseScore(int sum){
		score=sum+score;
	}
	
	
	public String GetRank() {
		if ((score >= 0) && (score < 45))
			rank="Beginner";
		if ((score >= 45) && (score < 135))
			rank="Active";
		if ((score >= 135) && (score < 270))
			rank="Advanced";
		if ((score >= 270) && (score < 405))
			rank="Expert";
		if (score >= 405)
			rank="Supervisor";
		return rank;
	}
	
	
	public void AddNotification(String loc){
		if (message.compareTo("empty")==0)
			message=loc;
		else
			message=message+"#"+loc;
	}
	

	
}
