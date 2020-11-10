package org.mbg.myboard2;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BoardCafe implements Parcelable{
    //API에서 받아온 값
    String address_name;
    String phone;
    String place_name;
    String place_url;
    String road_address_name;
    double x;
    double y;
    String id;
    //별점_입력한 값(db에 쓰기)
    float star_num_game=(float)0;
    float star_clean=(float)0;
    float star_service=(float)0;
    //db에서 받아온 별점
    float avg_num_game;
    float avg_clean;
    float avg_service;
    //게임 이름_입력한 값(db에 쓰기)
    String input_game_name="";
    //db에서 받아온 게임 이름
    String db_game_name="";



    //외부에서 사용하는 생성자
    public BoardCafe(String id,String address_name, String phone, String place_name, String place_url, String road_address_name, double x, double y) {
        this.id=id;
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