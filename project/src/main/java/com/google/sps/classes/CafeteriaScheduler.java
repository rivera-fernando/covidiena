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

    int numStudents = students.size();

    Schedule schedule = new Schedule(lunch, dinner, mealTime);

    if (students.isEmpty()) {
      return schedule;
    }

    List<Block> lunchBlocks = schedule.getLunchBlocks();
    List<Block> dinnerBlocks = schedule.getDinnerBlocks();

    // Order students by whoever's preference ends first

    // Make deep copy of students list so we don't edit the existing one
    List<Student> studentsLunchCopy = new ArrayList<Student>(students);
    List<Student> studentsDinnerCopy = new ArrayList<Student>(students);

    Collections.sort(studentsLunchCopy, Student.ORDER_LUNCH_END);
    Collections.sort(studentsDinnerCopy, Student.ORDER_DINNER_END);

    // Handle lunch
    List<Student> unhappyLunchStudents = new ArrayList<Student>();
    for (Student student : studentsLunchCopy) {
      boolean assigned = false;
      // While the student is unassigned
      for (Block block : lunchBlocks) {
        if ((block.getTime().overlaps(student.getLunchPref())) && (block.getCapacity() < maxCapacity)) {
          block.addStudent(student);
          schedule.incrementHappiness();
          assigned = true;
          break;
        }
      }
      if (!assigned) {
        unhappyLunchStudents.add(student);
      }
    }

    while (!unhappyLunchStudents.isEmpty()) {
      for (Block block : lunchBlocks) {
        while (block.getCapacity() < maxCapacity && !unhappyLunchStudents.isEmpty()) {
          Student student = unhappyLunchStudents.get(0);
          block.addStudent(student);
          unhappyLunchStudents.remove(student);
        }
      }
    }

    // Handle dinner
    List<Student> unhappyDinnerStudents = new ArrayList<Student>();
    for (Student student : studentsDinnerCopy) {
      boolean assigned = false;
      // While the student is unassigned
      for (Block block : dinnerBlocks) {
        if ((block.getTime().overlaps(student.getDinnerPref())) && (block.getCapacity() < maxCapacity)) {
          block.addStudent(student);
          assigned = true;
          break;
        }
      }
      if (!assigned) {
        unhappyDinnerStudents.add(student);
      }
    }

    while (!unhappyDinnerStudents.isEmpty()) {
      for (Block block : dinnerBlocks) {
        while (block.getCapacity() < maxCapacity && !unhappyDinnerStudents.isEmpty()) {
          Student student = unhappyDinnerStudents.get(0);
          block.addStudent(student);
          unhappyDinnerStudents.remove(student);
        }
      }
    }

    return schedule;
  }
}

