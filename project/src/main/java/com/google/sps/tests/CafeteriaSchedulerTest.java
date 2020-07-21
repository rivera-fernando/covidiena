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

package com.google.sps.tests;

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

/** */
@RunWith(JUnit4.class)
public final class CafeteriaSchedulerTest {

  private CafeteriaScheduler scheduler;

  private static final Collection <Student> NO_STUDENTS = Collections.emptySet();

  // Some collections of students for our tests.
  private static final Student STUDENT_A = new Student("Student A");
  private static final Student STUDENT_B = new Student("Student B");
  private static final Student STUDENT_C = new Student("Student C");

  // All dates are the first day of the year 2020.
  private static final int TIME_0800AM = TimeRange.getTimeInMinutes(8, 0);
  private static final int TIME_0830AM = TimeRange.getTimeInMinutes(8, 30);
  private static final int TIME_0845AM = TimeRange.getTimeInMinutes(8, 45);
  private static final int TIME_0900AM = TimeRange.getTimeInMinutes(9, 0);
  private static final int TIME_0930AM = TimeRange.getTimeInMinutes(9, 30);
  private static final int TIME_1000AM = TimeRange.getTimeInMinutes(10, 0);
  private static final int TIME_1100AM = TimeRange.getTimeInMinutes(11, 00);

  private static final int DURATION_30_MINUTES = 30;
  private static final int DURATION_60_MINUTES = 60;
  private static final int DURATION_90_MINUTES = 90;
  private static final int DURATION_1_HOUR = 60;
  private static final int DURATION_2_HOUR = 120;

  @Before
  public void setUp() {
    scheduler = new CafeteriaScheduler();
  }

  @Test
  public void noStudents() {
    
    int duration = TimeRange.WHOLE_DAY.duration() + 1;
    MeetingRequest request = new MeetingRequest(Arrays.asList(PERSON_A), duration);

    Collection < TimeRange > actual = query.query(NO_EVENTS, request);
    Collection < TimeRange > expected = Arrays.asList();

    Assert.assertEquals(expected, actual);
  }
}
