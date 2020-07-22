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
import com.google.sps.classes.Block;
 
/**
 * Class representing an individual schedule.
 *
 */
public final class Schedule {

  private final TimeRange lunch;
  private final TimeRange dinner;
  private final int mealTime;
  private List<Block> lunchBlocks; 
  private List<Block> dinnerBlocks;
  private int happy; 

  public Schedule(TimeRange lunch, TimeRange dinner, int mealTime) {

    this.lunch = lunch;
    this.dinner = dinner;
    this.mealTime = mealTime;
    
    // Create lunch blocks
    int lunchNumBlocks = lunch.duration() / mealTime;
    this.lunchBlocks = new ArrayList<Block>();
    for (int blockIndex = 0; blockIndex < lunchNumBlocks; blockIndex++) {
      List<Student> students = new ArrayList<Student>();
      TimeRange time = TimeRange.fromStartDuration(lunch.start() + mealTime * blockIndex, mealTime);

      Block newBlock = new Block(students, time);
      this.lunchBlocks.add(newBlock);
    }

    // Create dinner blocks
    int dinnerNumBlocks = dinner.duration() / mealTime;
    this.dinnerBlocks = new ArrayList<Block>();
    for (int blockIndex = 0; blockIndex < dinnerNumBlocks; blockIndex++) {
      List<Student> students = new ArrayList<Student>();
      TimeRange time = TimeRange.fromStartDuration(dinner.start() + mealTime * blockIndex, mealTime);

      Block newBlock = new Block(students, time);
      this.dinnerBlocks.add(newBlock);
    }

  }

  public List<Block> getLunchBlocks() {

    return this.lunchBlocks;

  }

  public List<Block> getDinnerBlocks() {

    return this.dinnerBlocks;
    
  }

  public void incrementHappiness() {
   
    this.happy++;
  
  }

  public int getHappiness() {

    return this.happy;
    
  }
}
