package org.mbg.myboard2;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BoardCafe implements Parcelable{
    //API에서 받아온 값
    String id;
    String address_name;
    String phone;
    String place_name;
    double x;
    double y;


    //외부에서 사용하는 생성자
    public BoardCafe(String id,String address_name, String phone, String place_name, double x, double y) {
        this.id=id;
        this.address_name=address_name;
        this.phone=phone;
        this.place_name=place_name;
        this.x=x;
        this.y=y;

    }

    public String getId(){
        return id;
    }
    public String getAddress_name(){
        return  address_name;
    }
    public String getPhone(){
        return phone;
    }
    public String getPlace_name(){
        return place_name;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }



    //Creator가 사용하는 생성자
    protected BoardCafe(Parcel in) {
        address_name = in.readString();
        phone = in.readString();
        place_name = in.readString();
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
        parcel.writeDouble(x);
        parcel.writeDouble(y);
    }

    public interface AsyncResponse {
        void processFinish(ArrayList<BoardCafe> output);
    }
}