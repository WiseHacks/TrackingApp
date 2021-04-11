package com.example.trackingapp;

import android.content.Intent;

public class User {
    public Integer role;
    public String fullname,email,phone;
//    public String Password;

    public User() {
    }

    public User(Integer role, String fullname, String email, String phone) {
        this.role = role;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
    }
}
