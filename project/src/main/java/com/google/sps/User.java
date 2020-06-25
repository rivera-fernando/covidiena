package com.google.sps.data;

public final class User {

  private String name;
  private final long userId;
  private final String email;
  private final String birthdate;
  private final long studentID;
  private final String sex;
  private final String school;
  private String phone;
  private String metric;
  private Boolean admin;


  public User(long userId, String name, String email, String birthdate, long studentID, String sex, String school, String phone) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.birthdate = birthdate;
    this.studentID = studentID;
    this.sex = sex;
    this.school = school;
    this.phone = phone;
    metric = "f";
    admin = false;
  }
}