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

import com.google.sps.classes.Event;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
 
/**
 * Class representing an individual day with its constituent events.
 */
public final class Day {
 
  private final String name;
  private final String date;
  private final List<Event> events;
 
  public Day(String name, String date, List<Event> events) {
    this.name = name;
    this.date = date;
    this.events = events;
  }
}
