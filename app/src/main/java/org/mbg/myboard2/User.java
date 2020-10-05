package org.mbg.myboard2;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {

    //현재 폰을 들고 있는 유저의 정보

    //private List<Map<String,String>> LoveGame;
    //private String TodayGame;
    private String email;
    private String nickname;
    private ArrayList tagGenre;

    public User(){}

    public User(String email,String nickname,ArrayList tagGenre){
        //this.LoveGame=LoveGame;
        //this.TodayGame=TodayGame;
        this.email=email;
        this.nickname=nickname;
        this.tagGenre=tagGenre;
    }

    public User(String email,String nickname){
        //this.LoveGame=LoveGame;
        //this.TodayGame=TodayGame;
        this.email=email;
        this.nickname=nickname;
        //this.tagGenre=tagGenre;
    }

    /*
    public List<Map<String,String>> getLoveGame(){
        return LoveGame;
    }
    public String getTodayGame(){
        return TodayGame;
    }
    */
    public String getEmail(){ return email; }
    public String getNickname(){ return nickname; }
    public ArrayList getTagGenre(){return tagGenre; }

}
