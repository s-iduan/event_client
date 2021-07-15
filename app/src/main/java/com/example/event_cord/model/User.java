package com.example.event_cord.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("level")
    private int mLevel;

    public User(
            int id,
            String name,
            String email,
            String password,
            int level
    ) {
        mId = id;
        mName = name;
        mEmail = email;
        mPassword = password;
        mLevel = level;
    }


    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public int getLevel() {
        return mLevel;
    }
}
