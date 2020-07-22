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

    List<Block> lunchBlocks = schedule.getLunchBlocks();
    List<Block> dinnerBlocks = schedule.getDinnerBlocks();

    // Order students by whoever's preference ends first

    // Make deep copy of students list so we don't edit the existing one
    List<Student> studentsLunchCopy = new ArrayList<Student>(students);
    List<Student> studentsDinnerCopy = new ArrayList<Student>(students);

    Collections.sort(studentsLunchCopy, Student.ORDER_LUNCH_END);
    Collections.sort(studentsDinnerCopy, Student.ORDER_DINNER_END);

    // Handle lunch
    int blockIndex = 0;
    while (!studentsLunchCopy.isEmpty()) {
      Student student = studentsLunchCopy.get(0);
      TimeRange lunchPref = student.getLunchPref();
      Block block = lunchBlocks.get(blockIndex);
      // If the block is full, or if there are not students
      // who want to eat at this block's time, go to next block.
      if ((lunchPref.start() > block.getTime().start())
        || (block.getCapacity() >= maxCapacity)) {
        blockIndex++;
        continue;
      } else {
        block.addStudent(student);
        studentsLunchCopy.remove(student);
      }
    }

    // Handle dinner
    blockIndex = 0;
    while (!studentsDinnerCopy.isEmpty()) {
      Student student = studentsDinnerCopy.get(0);
      TimeRange dinnerPref = student.getDinnerPref();
      Block block = dinnerBlocks.get(blockIndex);
      // If the block is full, or if there are not students
      // who want to eat at this block's time, go to next block.
      if ((dinnerPref.start() > block.getTime().start())
        || (block.getCapacity() >= maxCapacity)) {
        blockIndex++;
        continue;
      } else {
        block.addStudent(student);
        studentsDinnerCopy.remove(student);
      }
    }

    return schedule;
  }
}
