package org.mbg.myboard2;

public class LikeData {
    private String gnameKOR;
    private String memo;

    public LikeData(String gnameKOR,String memo){
        this.gnameKOR=gnameKOR;
        this.memo=memo;
    }

    public String getGnameKOR() {
        return gnameKOR;
    }

    public String getMemo() {
        return memo;
    }
}
