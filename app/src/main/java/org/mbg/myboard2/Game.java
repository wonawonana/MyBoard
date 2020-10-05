package org.mbg.myboard2;

public class Game {

    private String name;    //이름
    private String genre;   //장르
    private String time;    //게임 시간
    private String personnel;   //게임 인원
    //private String rest;    //내용
    //게임 이미지 나중에 추가

   public Game(String name,String genre, String time, String personnel){
        this.name=name;
        this.genre=genre;
        this.time=time;
        this.personnel=personnel;
        //this.rest=rest;
   }

   public String getName(){ return name; }
    public String getGenre(){ return genre; }
    public String getTime(){ return time; }
    public String getPersonnel(){ return personnel; }



}
