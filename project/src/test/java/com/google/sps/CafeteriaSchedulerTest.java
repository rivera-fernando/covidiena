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

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.classes.CafeteriaScheduler;
import com.google.sps.classes.Schedule;
import com.google.sps.classes.Student;
import com.google.sps.classes.TimeRange;
import com.google.sps.classes.Block;

/** */
@RunWith(JUnit4.class)
public final class CafeteriaSchedulerTest {

  private static final List<Student> NO_STUDENTS = new ArrayList<Student>();

  // Sample times
  private static final int TIME_1100 = TimeRange.getTimeInMinutes(11, 00);
  private static final int TIME_1130 = TimeRange.getTimeInMinutes(11, 30);
  private static final int TIME_1200 = TimeRange.getTimeInMinutes(12, 00);
  private static final int TIME_1230 = TimeRange.getTimeInMinutes(12, 30);
  private static final int TIME_1300 = TimeRange.getTimeInMinutes(13, 00);
  private static final int TIME_1330 = TimeRange.getTimeInMinutes(13, 30);
  private static final int TIME_1400 = TimeRange.getTimeInMinutes(14, 00);
  private static final int TIME_1430 = TimeRange.getTimeInMinutes(14, 30);
  private static final int TIME_1500 = TimeRange.getTimeInMinutes(15, 00);
  // Break
  private static final int TIME_1700 = TimeRange.getTimeInMinutes(17, 00);
  private static final int TIME_1730 = TimeRange.getTimeInMinutes(17, 30);
  private static final int TIME_1800 = TimeRange.getTimeInMinutes(18, 00);
  private static final int TIME_1830 = TimeRange.getTimeInMinutes(18, 30);
  private static final int TIME_1900 = TimeRange.getTimeInMinutes(19, 00);
  private static final int TIME_1930 = TimeRange.getTimeInMinutes(19, 30);
  private static final int TIME_2000 = TimeRange.getTimeInMinutes(20, 00);
  private static final int TIME_2030 = TimeRange.getTimeInMinutes(20, 30);
  private static final int TIME_2100 = TimeRange.getTimeInMinutes(21, 00);

  // Sample Lunch Blocks
  private static final TimeRange LUNCH_A = TimeRange.fromStartEnd(TIME_1100, TIME_1300, false);
  private static final TimeRange LUNCH_B = TimeRange.fromStartEnd(TIME_1200, TIME_1500, false);
  private static final TimeRange LUNCH_C = TimeRange.fromStartEnd(TIME_1100, TIME_1500, false);

  // Sample Dinner Blocks
  private static final TimeRange DINNER_A = TimeRange.fromStartEnd(TIME_1700, TIME_1900, false);
  private static final TimeRange DINNER_B = TimeRange.fromStartEnd(TIME_1800, TIME_2100, false);
  private static final TimeRange DINNER_C = TimeRange.fromStartEnd(TIME_1700, TIME_2100, false);

  // Sample meal durations
  private static final int DURATION_30_MINUTES = 30;
  private static final int DURATION_45_MINUTES = 45;
  private static final int DURATION_60_MINUTES = 60;

  // Sample capacities
  private static final int CAPACITY_1 = 1;
  private static final int CAPACITY_3 = 3;
  private static final int CAPACITY_5 = 5;
  private static final int CAPACITY_10 = 10;
  private static final int CAPACITY_125 = 125;

  // Sample Lunch Preferences
  private static final TimeRange PREF_1100 = TimeRange.fromStartDuration(TIME_1100, DURATION_60_MINUTES);
  private static final TimeRange PREF_1130 = TimeRange.fromStartDuration(TIME_1130, DURATION_60_MINUTES);
  private static final TimeRange PREF_1200 = TimeRange.fromStartDuration(TIME_1200, DURATION_60_MINUTES);
  private static final TimeRange PREF_1230 = TimeRange.fromStartDuration(TIME_1230, DURATION_60_MINUTES);
  private static final TimeRange PREF_1300 = TimeRange.fromStartDuration(TIME_1300, DURATION_60_MINUTES);
  private static final TimeRange PREF_1330 = TimeRange.fromStartDuration(TIME_1330, DURATION_60_MINUTES);
  private static final TimeRange PREF_1400 = TimeRange.fromStartDuration(TIME_1400, DURATION_60_MINUTES);

    // Sample Dinner Preferences
  private static final TimeRange PREF_1700 = TimeRange.fromStartDuration(TIME_1700, DURATION_60_MINUTES);
  private static final TimeRange PREF_1730 = TimeRange.fromStartDuration(TIME_1730, DURATION_60_MINUTES);
  private static final TimeRange PREF_1800 = TimeRange.fromStartDuration(TIME_1800, DURATION_60_MINUTES);
  private static final TimeRange PREF_1830 = TimeRange.fromStartDuration(TIME_1830, DURATION_60_MINUTES);
  private static final TimeRange PREF_1900 = TimeRange.fromStartDuration(TIME_1900, DURATION_60_MINUTES);
  private static final TimeRange PREF_1930 = TimeRange.fromStartDuration(TIME_1930, DURATION_60_MINUTES);
  private static final TimeRange PREF_2000 = TimeRange.fromStartDuration(TIME_2000, DURATION_60_MINUTES);

  // Some sample students
  private static final Student STUDENT_A = new Student("Student A", PREF_1100, PREF_1700);
  private static final Student STUDENT_B = new Student("Student B", PREF_1130, PREF_1730);
  private static final Student STUDENT_C = new Student("Student C", PREF_1200, PREF_1800);
  private static final Student STUDENT_D = new Student("Student D", PREF_1230, PREF_1830);
  private static final Student STUDENT_E = new Student("Student E", PREF_1300, PREF_1900);
  private static final Student STUDENT_F = new Student("Student F", PREF_1330, PREF_1930);
  private static final Student STUDENT_G = new Student("Student G", PREF_1400, PREF_2000);
  private static final Student STUDENT_H = new Student("Student H", PREF_1100, PREF_1800);
  private static final Student STUDENT_I = new Student("Student I", PREF_1130, PREF_1930);
  private static final Student STUDENT_J = new Student("Student J", PREF_1200, PREF_1200);

  private CafeteriaScheduler scheduler;
  private List<Student> studentsPool = new ArrayList<Student>();

  @Before
  public void setUp() {
    scheduler = new CafeteriaScheduler();
    studentsPool.add(STUDENT_A);
    studentsPool.add(STUDENT_B);
    studentsPool.add(STUDENT_C);
    studentsPool.add(STUDENT_D);
    studentsPool.add(STUDENT_E);
    studentsPool.add(STUDENT_F);
    studentsPool.add(STUDENT_G);
    studentsPool.add(STUDENT_H);
    studentsPool.add(STUDENT_I);
    studentsPool.add(STUDENT_J);
  }

  @Test
  public void noStudents() {
    Schedule actual = scheduler.schedule(LUNCH_A, DINNER_A, DURATION_30_MINUTES, CAPACITY_1, NO_STUDENTS);
    
    List<Block> lunchExpected = new ArrayList<Block>();
    lunchExpected.add(new Block(TIME_1100, DURATION_30_MINUTES));
    lunchExpected.add(new Block(TIME_1130, DURATION_30_MINUTES));
    lunchExpected.add(new Block(TIME_1200, DURATION_30_MINUTES));
    lunchExpected.add(new Block(TIME_1230, DURATION_30_MINUTES));
    
    List<Block> dinnerExpected = new ArrayList<Block>();
    dinnerExpected.add(new Block(TIME_1700, DURATION_30_MINUTES));
    dinnerExpected.add(new Block(TIME_1730, DURATION_30_MINUTES));
    dinnerExpected.add(new Block(TIME_1800, DURATION_30_MINUTES));
    dinnerExpected.add(new Block(TIME_1830, DURATION_30_MINUTES));

    Assert.assertEquals(lunchExpected, actual.getLunchBlocks());
    Assert.assertEquals(dinnerExpected, actual.getDinnerBlocks());
  }

  @Test
  public void oneStudent() {
    List<Student> students = new ArrayList<Student>();
    students.add(STUDENT_C);
    Schedule actual = scheduler.schedule(LUNCH_A, DINNER_A, DURATION_30_MINUTES, CAPACITY_1, students);
    
    List<Block> lunchExpected = new ArrayList<Block>();
    lunchExpected.add(new Block(TIME_1100, DURATION_30_MINUTES));
    lunchExpected.add(new Block(TIME_1130, DURATION_30_MINUTES));
    Block student_C_Lunch = new Block(TIME_1200, DURATION_30_MINUTES);
    student_C_Lunch.addStudent(STUDENT_C);
    lunchExpected.add(student_C_Lunch);
    lunchExpected.add(new Block(TIME_1230, DURATION_30_MINUTES));
    
    List<Block> dinnerExpected = new ArrayList<Block>();
    dinnerExpected.add(new Block(TIME_1700, DURATION_30_MINUTES));
    dinnerExpected.add(new Block(TIME_1730, DURATION_30_MINUTES));
    Block student_C_Dinner = new Block(TIME_1800, DURATION_30_MINUTES);
    student_C_Dinner.addStudent(STUDENT_C);
    dinnerExpected.add(student_C_Dinner);
    dinnerExpected.add(new Block(TIME_1830, DURATION_30_MINUTES));

    Assert.assertEquals(lunchExpected, actual.getLunchBlocks());
    Assert.assertEquals(dinnerExpected, actual.getDinnerBlocks());
  }

  @Test
  public void twoStudents() {
    List<Student> students = new ArrayList<Student>();
    // Full overlap for lunch, no overlap for dinner
    students.add(STUDENT_A);
    students.add(STUDENT_H);
    Schedule actual = scheduler.schedule(LUNCH_A, DINNER_A, DURATION_30_MINUTES, CAPACITY_1, students);
    
    List<Block> lunchExpected = new ArrayList<Block>();
    Block student_A_Lunch = new Block(TIME_1100, DURATION_30_MINUTES);
    student_A_Lunch.addStudent(STUDENT_A);
    lunchExpected.add(student_A_Lunch);
    Block student_H_Lunch = new Block(TIME_1130, DURATION_30_MINUTES);
    student_H_Lunch.addStudent(STUDENT_H);
    lunchExpected.add(student_H_Lunch);
    lunchExpected.add(new Block(TIME_1200, DURATION_30_MINUTES));
    lunchExpected.add(new Block(TIME_1230, DURATION_30_MINUTES));
    
    List<Block> dinnerExpected = new ArrayList<Block>();
    Block student_A_Dinner = new Block(TIME_1700, DURATION_30_MINUTES);
    student_A_Dinner.addStudent(STUDENT_A);
    dinnerExpected.add(student_A_Dinner);
    dinnerExpected.add(new Block(TIME_1730, DURATION_30_MINUTES));
    Block student_H_Dinner = new Block(TIME_1800, DURATION_30_MINUTES);
    student_H_Dinner.addStudent(STUDENT_H);
    dinnerExpected.add(student_H_Dinner);
    dinnerExpected.add(new Block(TIME_1830, DURATION_30_MINUTES));

    Assert.assertEquals(lunchExpected, actual.getLunchBlocks());
    Assert.assertEquals(dinnerExpected, actual.getDinnerBlocks());
  }

  @Test
  public void twoStudentsSameBlock() {
    List<Student> students = new ArrayList<Student>();
    // Full overlap for lunch, no overlap for dinner
    students.add(STUDENT_A);
    students.add(STUDENT_A);
    Schedule actual = scheduler.schedule(LUNCH_A, DINNER_A, DURATION_30_MINUTES, CAPACITY_3, students);
    
    List<Block> lunchExpected = new ArrayList<Block>();
    Block block1_lunch = new Block(TIME_1100, DURATION_30_MINUTES);
    block1_lunch.addStudents(students);
    lunchExpected.add(block1_lunch);
    lunchExpected.add(new Block(TIME_1130, DURATION_30_MINUTES));
    lunchExpected.add(new Block(TIME_1200, DURATION_30_MINUTES));
    lunchExpected.add(new Block(TIME_1230, DURATION_30_MINUTES));
    
    List<Block> dinnerExpected = new ArrayList<Block>();
    Block block1_dinner = new Block(TIME_1700, DURATION_30_MINUTES);
    block1_dinner.addStudents(students);
    dinnerExpected.add(block1_dinner);
    dinnerExpected.add(new Block(TIME_1730, DURATION_30_MINUTES));
    dinnerExpected.add(new Block(TIME_1800, DURATION_30_MINUTES));
    dinnerExpected.add(new Block(TIME_1830, DURATION_30_MINUTES));

    Assert.assertEquals(lunchExpected, actual.getLunchBlocks());
    Assert.assertEquals(dinnerExpected, actual.getDinnerBlocks());
  }

  @Test
  public void noOptimalSolution() {
    List<Student> students = new ArrayList<Student>();
    // Full overlap for lunch, no overlap for dinner
    students.add(STUDENT_A);
    students.add(STUDENT_A);
    students.add(STUDENT_A);
    Schedule actual = scheduler.schedule(LUNCH_A, DINNER_A, DURATION_30_MINUTES, CAPACITY_1, students);
    
    List<Block> lunchExpected = new ArrayList<Block>();
    Block block1_lunch = new Block(TIME_1100, DURATION_30_MINUTES);
    block1_lunch.addStudent(STUDENT_A);
    lunchExpected.add(block1_lunch);    
    Block block2_lunch = new Block(TIME_1130, DURATION_30_MINUTES);
    block2_lunch.addStudent(STUDENT_A);
    lunchExpected.add(block2_lunch);
    Block block3_lunch = new Block(TIME_1200, DURATION_30_MINUTES);
    block3_lunch.addStudent(STUDENT_A);
    lunchExpected.add(block3_lunch);
    lunchExpected.add(new Block(TIME_1230, DURATION_30_MINUTES));
    
    List<Block> dinnerExpected = new ArrayList<Block>();
    Block block1_dinner = new Block(TIME_1700, DURATION_30_MINUTES);
    block1_dinner.addStudent(STUDENT_A);
    dinnerExpected.add(block1_dinner);    
    Block block2_dinner = new Block(TIME_1730, DURATION_30_MINUTES);
    block2_dinner.addStudent(STUDENT_A);
    dinnerExpected.add(block2_dinner);
    Block block3_dinner = new Block(TIME_1800, DURATION_30_MINUTES);
    block3_dinner.addStudent(STUDENT_A);
    dinnerExpected.add(block3_dinner);
    dinnerExpected.add(new Block(TIME_1830, DURATION_30_MINUTES));

    Assert.assertEquals(lunchExpected, actual.getLunchBlocks());
    Assert.assertEquals(dinnerExpected, actual.getDinnerBlocks());
  }
  
  @Test
  public void lotsOfStudents() {
    // Create a pool of 1000 students
    List<Student> students = new ArrayList<Student>();
    for (int index = 0; index < 100; index++) {
      students.addAll(studentsPool);
    }

    Schedule actual = scheduler.schedule(LUNCH_C, DINNER_C, DURATION_30_MINUTES, CAPACITY_125, students);

    Assert.assertEquals(CAPACITY_125, actual.getLunchBlocks().get(0).getCapacity());
    Assert.assertEquals(CAPACITY_125, actual.getLunchBlocks().get(1).getCapacity());
    Assert.assertEquals(CAPACITY_125, actual.getLunchBlocks().get(2).getCapacity());
    Assert.assertEquals(CAPACITY_125, actual.getLunchBlocks().get(3).getCapacity());
    Assert.assertEquals(CAPACITY_125, actual.getDinnerBlocks().get(0).getCapacity());
    Assert.assertEquals(CAPACITY_125, actual.getDinnerBlocks().get(1).getCapacity());
    Assert.assertEquals(CAPACITY_125, actual.getDinnerBlocks().get(2).getCapacity());
    Assert.assertEquals(CAPACITY_125, actual.getDinnerBlocks().get(3).getCapacity());


    System.out.println(actual.getHappiness());

    Assert.assertTrue(actual.getLunchBlocks().get(0).contains(63, "Student A"));
    Assert.assertTrue(actual.getLunchBlocks().get(0).contains(62, "Student H"));
    Assert.assertTrue(actual.getLunchBlocks().get(1).contains(37, "Student A"));
    Assert.assertTrue(actual.getLunchBlocks().get(1).contains(38, "Student H"));
    Assert.assertTrue(actual.getLunchBlocks().get(1).contains(25, "Student B"));
    Assert.assertTrue(actual.getLunchBlocks().get(1).contains(25, "Student I"));
    Assert.assertTrue(actual.getLunchBlocks().get(2).contains(63, "Student B"));
    Assert.assertTrue(actual.getLunchBlocks().get(2).contains(62, "Student I"));
    Assert.assertTrue(actual.getLunchBlocks().get(3).contains(63, "Student C"));
    Assert.assertTrue(actual.getLunchBlocks().get(3).contains(62, "Student J"));
    Assert.assertTrue(actual.getLunchBlocks().get(4).contains(100, "Student D"));
    Assert.assertTrue(actual.getLunchBlocks().get(4).contains(25, "Student E"));
    Assert.assertTrue(actual.getLunchBlocks().get(5).contains(75, "Student E"));    
    Assert.assertTrue(actual.getLunchBlocks().get(5).contains(50, "Student F"));    
    Assert.assertTrue(actual.getLunchBlocks().get(6).contains(50, "Student F"));    
    Assert.assertTrue(actual.getLunchBlocks().get(6).contains(75, "Student G"));    
    Assert.assertTrue(actual.getLunchBlocks().get(7).contains(25, "Student G")); 
    Assert.assertTrue(actual.getLunchBlocks().get(7).contains(12, "Student B")); 
    Assert.assertTrue(actual.getLunchBlocks().get(7).contains(13, "Student I")); 
    Assert.assertTrue(actual.getLunchBlocks().get(7).contains(37, "Student C")); 
    Assert.assertTrue(actual.getLunchBlocks().get(7).contains(38, "Student J")); 
  }
}

