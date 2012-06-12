package com.facebook.android;



public class EmailDetails {
	
	 private String name,phone,add,mail;
	 private byte[] pic;
	 private String text;
	 private String loc_name;
	// private String loc_address;
	 
	 public EmailDetails(String name, String phone, String add, String mail,String text, String loc_name)
	 {
		 this.name=name;
		 this.phone=phone;
		 this.add=add;
		 this.mail=mail;
		 this.text=text;
		 this.loc_name=loc_name;
		// this.loc_address=loc_address;
		 
		 
	 }
	 
	 public void setPicture(byte[] barr)
	 {
		 this.pic=barr;
	 }
	 
	 public String getText() {
		 return text;
	 }
	 public String getUserName() {
		 return name;
	 }
	 
	 public String getUserAddress() {
		 return add;
	 }
	 public String getUserPhone() {
		 return phone;
	 }
	 public String getUserMail() {
		 return mail;
	 }
	 public String getLocationName() {
		 return loc_name;
	 }
	 
	 public byte[] getPicture(){
		 return pic;
	 }
	 
	 
	 
	 
	

}
