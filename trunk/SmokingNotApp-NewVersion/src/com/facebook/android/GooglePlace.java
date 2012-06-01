package com.facebook.android;

import android.location.Location;

public class GooglePlace {
	public String id;
	public String name;
	public String vicinity;
	public Location location;
	public double distance;
	public int goodRate;
	public int badRate;
	
	public GooglePlace() {
		
	}
	
	public String toString() {
		return "ID: " + id + " Name" + name + " Vicinity: " + vicinity;
	}
/*
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(id);
		out.writeString(name);
		out.writeString(vicinity);
		out.writeInt(goodRate);
		out.writeInt(badRate);
		out.writeDouble(distance);
	}
	

	public static final Parcelable.Creator<GooglePlace> CREATOR = new Parcelable.Creator<GooglePlace>() {
		public GooglePlace createFromParcel(Parcel in) {
			return new GooglePlace(in);
		}

		public GooglePlace[] newArray(int size) {
			return new GooglePlace[size];
		}
	};

	private GooglePlace(Parcel in) {
		mData = in.readInt();
	}*/
}