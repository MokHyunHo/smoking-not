package com.notmokingappweb.client;



import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable(detachable = "true")
public class UserRequest {

	@PrimaryKey
	@Persistent
	private String email;
	@Persistent
	private int score;
	@Persistent
	private String lastplaceId;
	@Persistent
	private String date;
	@Persistent
	private String message;
	
	
	private String rank;

	
	public UserRequest (String email, int score, String place){
		this.email=email;
		this.score=score;
		this.lastplaceId=place;
		rank="Beginner";
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
	

	

	
	public void AddNotification(String m){
		if ((message.compareTo("empty")==0))
			message=m;
		else
			message=message+"#"+m;
	}
	

	
}
