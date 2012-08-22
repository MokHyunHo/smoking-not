package com.notmokingappweb.client;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable(detachable = "true")
public class EmailDetails {
	
	 private String name,phone,add,mail;
	 private byte[] pic;
	 private String text;
	 private String loc_name;
	 private String comment;
	// private String loc_address;
	 
	 public EmailDetails() {
		 this.name="Non";
		 this.phone="Non";
		 this.add="Non";
		 this.mail="Non";
		 this.text="Non";
		 this.loc_name="Non";
		 this.comment="Non";
	 }
	 public EmailDetails(String name, String phone, String add, String mail,String text, String loc_name,String comment)
	 {
		 this.name=name;
		 this.phone=phone;
		 this.add=add;
		 this.mail=mail;
		 this.text=text;
		 this.loc_name=loc_name;
		 this.comment=comment;
		 
		 
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
	 
	 public String getComment() {
		 return comment;
	 }
	 
	 
	 
	

}
