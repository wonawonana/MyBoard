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
    private ArrayList<String> tagGenre;
    private String recommendGame;

    public User(){}

    public User(String email,String nickname,ArrayList<String> tagGenre,String recommendGame){
        //this.LoveGame=LoveGame;
        //this.TodayGame=TodayGame;
        this.email=email;
        this.nickname=nickname;
        this.tagGenre=tagGenre;
        this.recommendGame=recommendGame;
    }

    public User(String email,String nickname,String recommendGame){
        //this.LoveGame=LoveGame;
        //this.TodayGame=TodayGame;
        this.email=email;
        this.nickname=nickname;
        this.recommendGame=recommendGame;
        this.tagGenre=new ArrayList<>();
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
    public ArrayList<String> getTagGenre(){return tagGenre; }
    public String getRecommendGame(){return recommendGame;}

}
