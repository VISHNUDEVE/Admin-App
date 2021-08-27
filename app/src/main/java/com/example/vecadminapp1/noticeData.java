package com.example.vecadminapp1;

public class noticeData {
    String date, title, key, length, Image,time;

    public noticeData() {

    }

    public noticeData(String title, String Image, String date, String length, String Key) {
        this.title = title;
        this.Image = Image;
        this.date = date;
        this.key = Key;
        this.length = length;
        this.time = time;
    }


    public String getTitle() {
        return title;
    }
    public String getImage(){ return Image;}
    public String getKey(){return key;}
    public String getDate(){return date;}
    public String getLength(){return length;}
    public String getTime(){return time;}
    }
