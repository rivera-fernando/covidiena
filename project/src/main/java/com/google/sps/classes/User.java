package com.google.sps.classes;

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
  private Boolean is_admin;
  private String imageKey;
  private String state;
  private String diagnosed;

  /*User class is instatiated when a user logins so all their info is accesible without the datatsore being queried each time*/
  public User(long userId, String name, String email, String password, String birthdate, long studentId, 
              String sex, String school, String phone, Boolean is_admin, String imageKey, String state, String diagnosed) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.password = password;
    this.birthdate = birthdate;
    this.studentId = studentId;
    this.sex = sex;
    this.school = school;
    this.phone = phone;
    this.is_admin = is_admin;
    this.imageKey = imageKey;
    this.state = state;
    this.diagnosed = diagnosed;
  }

  public long getUserId() {
    return userId;
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

  public Boolean getIsAdmin(){
      return is_admin;
  }

  public String getSchool(){
      return school;
  }
  
  public String getPassword(){
      return password;
  }

  public String getImageKey(){
      return imageKey;
  }

  public String getState(){
      return state;
  }
} 
