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
import com.google.sps.classes.Student;
import com.google.sps.classes.TimeRange;
 
/**
 * Class representing an individual schedule.
 *
 */
public final class Schedule {

  TimeRange lunch;
  TimeRange dinner;
  int mealTime;
  List<Block> blocks; 

  public Schedule(TimeRange lunch, TimeRange dinner, int mealTime) {

    this.lunch = lunch;
    this.dinner = dinner;
    this.mealTime = mealTime;
    this.blocks = new ArrayList<Block>();

  }

  public static Block createBlock(List<Student> students, TimeRange time) {
    Block block = new Block(students, time, time.duration());
    return block;
  }

  protected final class Block {
    
    List<Student> students;
    TimeRange time;
    int duration;

    public Block(List<Student> students, TimeRange time, int Duration) {
      
      this.students = students;
      this.time = time;
      this.duration = duration;

    }

    public void addStudent(Student student) {

      this.students.add(student);
      
    }
  }
}
