package com.google.sps.data;

public final class User {

  private String name;
  private final long userId;
  private final String email;
  private String password;
  private final String birthdate;
  private final long studentId;
  private final String sex;
  private final String school;
  private String phone;
  private String metric;
  private Boolean admin;


  public User(long userId, String name, String email, String password, String birthdate, long studentId, String sex, String school, String phone, String metric, Boolean admin) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.password = password;
    this.birthdate = birthdate;
    this.studentId = studentId;
    this.sex = sex;
    this.school = school;
    this.phone = phone;
    this.metric = metric;
    this.admin = admin;
  }

  public String getPhone(){
      return phone;
  }

  public String getName(){
      return name;
  }

  public String getEmail(){
      return email;
  }

  public String getBirthdate(){
      return birthdate;
  }

  public long getStudentId(){
      return studentId;
  }

  public String getSex(){
      return sex;
  }

  public Boolean getAdmin(){
      return admin;
  }

  public String getSchool(){
      return school;
  }

  public String getMetric(){
      return metric;
  }

  public String getPassword(){
      return password;
  }
} 
