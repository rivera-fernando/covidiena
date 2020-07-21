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

public final class Block {
  
  private List<Student> students;
  private final TimeRange time;
  private final int duration;

  public Block(int start, int duration) {

    this.students = new ArrayList<Student>();
    this.time = TimeRange.fromStartDuration(start, duration);
    this.duration = duration;

  }

  public Block(List<Student> students, TimeRange time) {
    
    this.students = students;
    this.time = time;
    this.duration = time.duration();

  }

  public void addStudent(Student student) {

    this.students.add(student);

  }

  public Student removeStudent(Student student) {

    this.students.remove(student);

    return student;
  }

  public List<Student> getStudents() {

    return this.students;

  }

  private static boolean equalStudents(Block a, Block b) {
    if (a.students.size() == b.students.size()) {
      for (int i = 0; i < a.students.size(); i++) {
        if (a.students.get(i).getName() != b.students.get(i).getName()) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public String toString() {

    return String.format("Block: [" + time.start() + ", " + time.end() + "] " + "Students: " + students);
  
  }

  private static boolean equals(Block a, Block b) {
    if (equalStudents(a, b)) {
      return a.time.start() == b.time.start() && 
        a.time.duration() == b.time.duration();
    }
    return false;
  }

  @Override
  public boolean equals(Object other) {

    return other instanceof Block && equals(this, (Block) other);
  
  }

}