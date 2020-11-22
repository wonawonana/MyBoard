package org.mbg.myboard2;

import java.util.ArrayList;

public class cafeDB {

    private String cafeName;
    private float starClean;
    private float starNumGame;
    private float starService;
    private int count;
    private ArrayList<String> cafeGameList;
    private String businessHour;
    private String price;

    private String address_name;
    private String phone;



    public cafeDB(){}
    //db에 없는 id 일때 올리는 class
    public cafeDB(String cafeName,float starClean,float starNumGame,float starService,int count, ArrayList<String> cafeGameList, String businessHour, String price
            ,String address_name,String phone){
        this.cafeName=cafeName;
        this.starClean=starClean;
        this.starNumGame=starNumGame;
        this.starService=starService;
        this.count=count;
        this.cafeGameList=cafeGameList;
        this.businessHour=businessHour;
        this.price=price;
        this.address_name=address_name;
        this.phone=phone;
    }
    public cafeDB(String cafeName,float starClean,float starNumGame,float starService,int count, ArrayList<String> cafeGameList, String businessHour, String price){
        this.cafeName=cafeName;
        this.starClean=starClean;
        this.starNumGame=starNumGame;
        this.starService=starService;
        this.count=count;
        this.cafeGameList=cafeGameList;
        this.businessHour=businessHour;
        this.price=price;
        this.address_name="";
        this.phone="";
    }
    public float getStarClean(){ return starClean;}
    public float getStarService(){return  starService;}
    public float getStarNumGame(){return starNumGame;}
    public int getCount(){return count;}
    public String getCafeName(){return  cafeName;}
    public ArrayList<String> getCafeGameList(){return cafeGameList;}
    public String getBusinessHour(){return businessHour;}
    public String getPrice(){return price;}
    public String getAddress_name(){return address_name;}
    public String getPhone(){return phone;}

}