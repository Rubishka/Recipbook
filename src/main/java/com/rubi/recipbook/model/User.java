package com.rubi.recipbook.model;

/**
 * Created by rubi on 21/08/2017.
 */

public class User {

    public String name;
    public String userID;
    public String Email;

    public User(){}

    public User(String name,String UID, String Email){
        this.name=name;
        this.userID=UID;
        this.Email=Email;
    }
}
