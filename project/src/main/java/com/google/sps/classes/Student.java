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
import java.util.Comparator;
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

  public Student(String name) {
    this.name = name;
    this.lunchPref = null;
    this.dinnerPref = null;
  }

  public void setLunchPref(TimeRange lunchPref) {
    this.lunchPref = lunchPref;
  }

  public void setDinnerPref(TimeRange dinnerPref) {
    this.dinnerPref = dinnerPref;
  }

  public TimeRange getPref(String meal) {
    if (meal.equals("lunch")) {
      return this.lunchPref;
    } else if (meal.equals("dinner")) { 
      return this.dinnerPref;
    } else {
      return null;
    }
  }

  public String getName() {return this.name;}

  public TimeRange getLunch() {return this.lunchPref;}

  public TimeRange getDinner() {return this.dinnerPref;}

  @Override
  public String toString() {return String.format(this.name);}

  /**
   * A comparator for sorting ranges by their start time in ascending order.
   */
  public static final Comparator<Student> ORDER_LUNCH_START = new Comparator<Student>() {
    @Override
    public int compare(Student a, Student b) {
      return Long.compare(a.getPref("lunch").start(), b.getPref("lunch").start());
    }
  };

  /**
   * A comparator for sorting ranges by their start time in ascending order.
   */
  public static final Comparator<Student> ORDER_LUNCH_END = new Comparator<Student>() {
    @Override
    public int compare(Student a, Student b) {
      return Long.compare(a.getPref("lunch").end(), b.getPref("lunch").end());
    }
  };

  /**
   * A comparator for sorting ranges by their start time in ascending order.
   */
  public static final Comparator<Student> ORDER_DINNER_START = new Comparator<Student>() {
    @Override
    public int compare(Student a, Student b) {
      return Long.compare(a.getPref("dinner").start(), b.getPref("dinner").start());
    }
  };

  /**
   * A comparator for sorting ranges by their start time in ascending order.
   */
  public static final Comparator<Student> ORDER_DINNER_END = new Comparator<Student>() {
    @Override
    public int compare(Student a, Student b) {
      return Long.compare(a.getPref("dinner").end(), b.getPref("dinner").end());
    }
  };
}

