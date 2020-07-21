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

import java.util.List;
import java.util.ArrayList;
import com.google.sps.classes.TimeRange;
 
/**
 * Class representing an individual student.
 *
 */
public final class Student {

  private final String name;
  private TimeRange lunchPref;
  private TimeRange dinnerPref;

  public Student(String name, TimeRange lunchPref, TimeRange dinnerPref) {

    this.name = name;
    this.lunchPref = lunchPref;
    this.dinnerPref = dinnerPref;

  }

  public void setLunchPref(TimeRange lunchPref) {

    this.lunchPref = lunchPref;
    
  }

  public void setDinnerPref(TimeRange dinnerPref) {

    this.dinnerPref = dinnerPref;
    
  }

  public static TimeRange getLunchPref(Student student) {

    return student.lunchPref;

  }

  public static TimeRange getDinnerPref(Student student) {

    return student.dinnerPref;

  }

  public String getName() {

    return this.name;

  }
}