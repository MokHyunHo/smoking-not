package com.facebook.android;


public class LocationRequest {


	private String id;
	private int rate;
	
	public LocationRequest(String id, int rate){
		this.id=id;
		this.rate=rate;
	}
	
	public String getId(){
		return id;
	}
	
	public int getRate() {
		return rate;
	}
	
	public void increaseRate(int new_rate) {
		rate=new_rate+rate;
	}
	
	public void setId(String new_Id){
		id=new_Id;
	}
	
}
