package il.reporter.gws;



public class UserRequest {

	private String email;
	private int score;
	private String lastplaceId;
	private String date;
	private String message="empty";
	
	public UserRequest (String email, int score, String place, String date){
		this.email=email;
		this.score=score;
		this.lastplaceId=place;
		this.date=date;
	}
	
	public String GetEmail(){
		return this.email;
	}
	

	public String GetLastPalace(){
		return lastplaceId;
	}
	
	public String GetLastReportDate() {
		return date;
	}
	
	public void SetLastReportDate(String last_date) {
		date=last_date;
	}
	public void SetMessage(String m){
		message=m;
	}
	
	public String GetMessage(){
		return message;
	}
	
	public void SetLastplace (String l) 
	{
		lastplaceId=l;
	}
	
	public int GetScore() {
		return this.score;
	}
	public void IncreaseScore(int sum){
		score=sum+score;
	}
	

	
}
