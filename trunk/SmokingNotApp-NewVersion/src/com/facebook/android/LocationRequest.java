package com.facebook.android;


public class LocationRequest {


	private String id;
	private int good_rate;
	private int bad_rate;
	
	public LocationRequest(String id, int good_rate, int bad_rate){
		this.id=id;
		this.good_rate=good_rate;
		this.bad_rate=bad_rate;
	}
	
	public String getId(){
		return id;
	}
	
	public int getGoodRate() {
		return good_rate;
	}
	
	public int getBadRate() {
		return bad_rate;
	}
	
	public void increaseGoodRate(int new_rate) {
		good_rate=new_rate+good_rate;
	}
	
	public void increaseBadRate(int new_rate) {
		bad_rate=new_rate+bad_rate;
	}
	
	public void setId(String new_Id){
		id=new_Id;
	}
	
}
