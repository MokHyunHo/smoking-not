package com.facebook.android;


public class LocationRequest {


	private String id;
	private String reference;
	private String name;
	private String address;
	private double latitude;
	private double longitude;
	private int good_rate;
	private int bad_rate;
	
	public LocationRequest(String id,String reference,String name,String address,double latitude,double longitude, int good_rate, int bad_rate){
		this.id=id;
		this.reference=reference;
		this.name=name;
		this.address=address;
		this.latitude=latitude;
		this.longitude=longitude;
		this.good_rate=good_rate;
		this.bad_rate=bad_rate;
	}
	
	public String getReference(){
		return reference;
	}
	public String getId(){
		return id;
	}
	public String getAddress(){
		return address;
	}
	public String getName(){
		return name;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
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
