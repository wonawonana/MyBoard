package org.mbg.myboard2;

public class Item {

    //이 변수가 필드명이랑 같아야 함
    private int like;
    private String nickname;
    private String text;

    public Item(){}

    public Item(int like,String text,String nickname) {
        this.like = like;
        this.nickname=nickname;
        this.text=text;

    }

    public int getLike() {
        return like;
    }
    public String getNickname() {
        return nickname;
    }
    public String getText() {
        return text;
    }

}
