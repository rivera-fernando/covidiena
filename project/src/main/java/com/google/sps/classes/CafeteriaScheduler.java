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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import com.google.sps.classes.Schedule;
import com.google.sps.classes.Student;
import com.google.sps.classes.TimeRange;

public final class CafeteriaScheduler {
  // Variables

  // Constructor
  public CafeteriaScheduler() {

  }

  // Return schedule
  public Schedule schedule(Collection<Event> events, MeetingRequest request) {
    Schedule schedule = new Schedule();
    return schedule;
  }
}