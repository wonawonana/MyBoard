package org.mbg.myboard2;

public class cafeDB {

    private String cafeName;
    private float starClean;
    private float starNumGame;
    private float starService;
    private int count;

    public cafeDB(){}
    //db에 없는 id 일때 올리는 class
    public cafeDB(String cafeName,float starClean,float starNumGame,float starService,int count){
        this.cafeName=cafeName;
        this.starClean=starClean;
        this.starNumGame=starNumGame;
        this.starService=starService;
        this.count=count;
    }
    public float getStarClean(){ return starClean;}
    public float getStarService(){return  starService;}
    public float getStarNumGame(){return starNumGame;}
    public int getCount(){return count;}
    public String getCafeName(){return  cafeName;}

}