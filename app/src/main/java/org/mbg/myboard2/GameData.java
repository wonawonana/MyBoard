package org.mbg.myboard2;

import java.util.ArrayList;
import java.util.List;

public class GameData {

    private String genre;
    private String genres;
    private String gnameENG;
    private String gnameKOR;
    private ArrayList<Integer> gnum;
    private String gnumbystring;
    private boolean gtime30_60;
    private boolean gtime60_90;
    private boolean gtime90_120;
    private boolean gtimeless30;
    private boolean gtimemore120;

    private String gtimebystring;
    private String system;
    private String imgUrl;

    public GameData(){}

    public GameData(String genre, String genres, String gnameENG, String gnameKOR, ArrayList<Integer> gnum, String gnumbystring,
                    boolean gtime30_60, boolean gtime60_90, boolean gtime90_120, String gtimebystring,
                    boolean gtimeless30, boolean gtimemore120, String system,String imgUrl){
        this.genre=genre;
        this.genres=genres;
        this.gnameENG=gnameENG;
        this.gnameKOR=gnameKOR;
        this.gnum=gnum;
        this.gnumbystring=gnumbystring;
        this.gtime30_60=gtime30_60;
        this.gtime60_90=gtime60_90;
        this.gtime90_120=gtime90_120;
        this.gtimebystring=gtimebystring;
        this.gtimeless30=gtimeless30;
        this.gtimemore120=gtimemore120;
        this.system=system;
        this.imgUrl=imgUrl;
    }


    public String getGenre(){return genre;}
    public String getGenres(){return genres;}
    public String getGnameENG(){return gnameENG;}
    public String getGnameKOR(){return gnameKOR;}
    public ArrayList getGnum(){return gnum;}
    public String getGnumbystring(){return gnumbystring;}
    public boolean getGTime30_60(){return gtime30_60;}
    public boolean getGTime60_90(){return gtime60_90;}
    public boolean getGTime90_120(){return gtime90_120;}
    public String getGtimebystring(){return gtimebystring;}
    public boolean getGtimeless30(){return gtimeless30;}
    public boolean getGtimemore120(){return gtimemore120;}
    public String getSystem(){return system;}
    public String getImgUrl(){return imgUrl;}



}
