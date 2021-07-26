package com.example.project05122017;


class Message {
    String login;
    String loginme;
    String message;
    String date;
    String time;
    int status;
    Message(String login,String loginme,String message,String time,String date,int status){
        this.login=login;
        this.status=status;
        this.loginme=loginme;
        this.message=message;
        this.date=date;
        this.time=time;
    }
}
