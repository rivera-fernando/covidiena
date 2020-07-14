// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
 
package com.google.sps.classes;
 
/**
 * Class representing an individual event.
 *
 */
public final class Event {
 
  private final long id;
  private final String name;
  private final String location;
  private final String date;
  private final String time;
  private final String description;
  private final String type;
  private final String attendance;
  private final long timeStamp;
  private final boolean isMine;
  private final String entityType;
  private final String imageKey;
  private final int day;
  private final int numAttendees;
  private final int maxCapacity;
 
  public Event(long id, String name, String location, String date, String time, String description, String attendance, String type, long timeStamp, boolean isMine, String entityType, String imageKey, int day, int numAttendees, int maxCapacity) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.date = date;
    this.time = time;
    this.description = description;
    this.type = type;
    this.attendance = attendance;
    this.timeStamp = timeStamp;
    this.isMine = isMine;
    this.entityType = entityType;
    this.imageKey = imageKey;
    this.day = day;
    this.numAttendees = numAttendees;
    this.maxCapacity = maxCapacity;
  }
}
