package org.mbg.myboard2;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BoardCafe implements Parcelable{
    String address_name;
    String phone;
    String place_name;
    String place_url;
    String road_address_name;
    double x;
    double y;


    //외부에서 사용하는 생성자
    public BoardCafe(String address_name, String phone, String place_name, String place_url, String road_address_name, double x, double y){
        this.address_name=address_name;
        this.phone=phone;
        this.place_name=place_name;
        this.place_url=place_url;
        this.road_address_name=road_address_name;
        this.x=x;
        this.y=y;
    }

    //Creator가 사용하는 생성자
    protected BoardCafe(Parcel in) {
        address_name = in.readString();
        phone = in.readString();
        place_name = in.readString();
        place_url = in.readString();
        road_address_name = in.readString();
        x = in.readDouble();//longitude 12@.
        y = in.readDouble();//latitude 3@.
    }

    public static final Creator<BoardCafe> CREATOR = new Creator<BoardCafe>() {
        @Override
        public BoardCafe createFromParcel(Parcel in) {
            return new BoardCafe(in);
        }

        @Override
        public BoardCafe[] newArray(int size) {
            return new BoardCafe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address_name);
        parcel.writeString(phone);
        parcel.writeString(place_name);
        parcel.writeString(place_url);
        parcel.writeString(road_address_name);
        parcel.writeDouble(x);
        parcel.writeDouble(y);
    }

    public interface AsyncResponse {
        void processFinish(ArrayList<BoardCafe> output);
    }
}