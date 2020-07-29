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

import com.google.appengine.api.datastore.Key;

/**
 * Class representing an individual event.
 *
 */
public final class Cafeteria {
 
  private final String school;
  private final String name;
  private final int maxCapacity;
  private final int mealTime;
  private final long lunch_start;
  private final long lunch_end;
  private final long dinner_start;
  private final long dinner_end;
  private final boolean is_scheduled;
  private final String key;

  public Cafeteria(String school, String name, int maxCapacity, 
    int mealTime, long lunch_start, long lunch_end, long dinner_start, 
    long dinner_end, boolean is_scheduled, String key) {

    this.school = school;
    this.name = name;
    this.maxCapacity = maxCapacity;
    this.mealTime = mealTime;
    this.lunch_start = lunch_start;
    this.lunch_end = lunch_end;
    this.dinner_start = dinner_start;
    this.dinner_end = dinner_end;
    this.is_scheduled = is_scheduled;
    this.key = key;
  }
}
