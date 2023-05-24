package com.eliteworks.eliteworks.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String phoneNo;
    private String gender;
    private Date dob;
    private String address;
    private String photo;
    private boolean isProfileCompleted;

}
