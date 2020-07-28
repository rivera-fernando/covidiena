package com.google.sps.classes;

public final class Location {
 
  private final long locationId;
  private final String name;
  private final String email;
  private final String date;
  private final String state;
  private final String longitude;
  private final String latitude;

  /*Location class is instatiated when the past locations are requested in the student dashboard*/
  public Location(long locationId, String name, String email, String date, String state, String longitude, String latitude) {
    this.locationId = locationId;
    this.name = name;
    this.email = email;
    this.date = date;
    this.state = state;
    this.longitude = longitude;
    this.latitude = latitude;
  }
}
