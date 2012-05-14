package com.facebook.android;



public class UserRequest {

	private String email;
	private int score;
	private String lastplaceId;
	
	public UserRequest (String email, int score, String place){
		this.email=email;
		this.score=score;
		this.lastplaceId=place;
	}
	
	public String GetEmail(){
		return this.email;
	}
	

	public String GetLastPalace(){
		return lastplaceId;
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
