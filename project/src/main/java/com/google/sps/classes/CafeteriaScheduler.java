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
import com.google.sps.classes.Block;

public final class CafeteriaScheduler {

  // Return schedule
  public Schedule schedule(TimeRange lunch, TimeRange dinner, 
    int mealTime, int maxCapacity, List<Student> students) {

    Schedule schedule = new Schedule(lunch, dinner, mealTime);

    if (students.isEmpty()) {
      return schedule;
    }

    // Populate meal blocks using deep copies of student lists
    handleMeal(new ArrayList<Student>(students), schedule.getLunchBlocks(), maxCapacity, "lunch");
    handleMeal(new ArrayList<Student>(students), schedule.getDinnerBlocks(), maxCapacity, "dinner");

    return schedule;
  }

  public void handleMeal(List<Student> students, List<Block> blocks, int maxCapacity, String meal) {
    // Order students by whoever's preference ends first
    Collections.sort(students, Student.ORDER_LUNCH_END);

    // Create list to handle students who don't eat at their preferences
    List<Student> unhappyStudents = new ArrayList<Student>();

    for (Student student : students) {
      boolean assigned = false;
      // While the student is unassigned
      for (Block block : blocks) {
        if ((block.getTime().overlaps(student.getPref(meal))) && (block.getCapacity() < maxCapacity)) {
          block.addStudent(student);
          assigned = true;
          break;
        }
      }
      if (!assigned) {
        unhappyStudents.add(student);
      }
    }

    while (!unhappyStudents.isEmpty()) {
      for (Block block : blocks) {
        while (block.getCapacity() < maxCapacity && !unhappyStudents.isEmpty()) {
          Student student = unhappyStudents.get(0);
          block.addStudent(student);
          unhappyStudents.remove(student);
        }
      }
    }
  }
}

