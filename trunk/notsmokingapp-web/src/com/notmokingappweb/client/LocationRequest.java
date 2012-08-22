package com.notmokingappweb.client;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class LocationRequest {

		@PrimaryKey
		@Persistent
		private String id;
		@Persistent
		private int good_rate;
		@Persistent
		private int bad_rate;
		@Persistent
		private String reference;
		@Persistent
		private String name;
		@Persistent
		private String address;
		@Persistent
		private double latitude;
		@Persistent
		private double longitude;
		
		
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
