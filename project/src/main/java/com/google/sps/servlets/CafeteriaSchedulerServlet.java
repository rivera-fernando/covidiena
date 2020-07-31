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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.sps.classes.Update;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.nio.charset.*;
import org.json.JSONObject;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.sps.classes.CafeteriaScheduler;
import com.google.sps.classes.Schedule;
import com.google.sps.classes.Student;
import com.google.sps.classes.TimeRange;
import com.google.sps.classes.Block;

@WebServlet("/cafeteria-scheduler")
public class CafeteriaSchedulerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    boolean isAdmin = (boolean) session.getAttribute("is_admin");

    if (isAdmin) {
      String school = (String) session.getAttribute("school");
      String cafeteria = (String) request.getParameter("cafeteria");
      
      Key key = KeyFactory.stringToKey((String) request.getParameter("key"));

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

      try {
        Entity entity = datastore.get(key);

        String cafeteriaName = (String) entity.getProperty("cafeteriaName");
        int maxCapacity = Integer.parseInt((String) entity.getProperty("maxCapacity"));
        int mealTime = Integer.parseInt((String) entity.getProperty("mealTime"));

        Query studentQuery = new Query("StudentPref");
        PreparedQuery studentResults = datastore.prepare(studentQuery);
        List<Entity> studentResultsList = studentResults.asList(FetchOptions.Builder.withDefaults());
        List<Student> students = new ArrayList<>();

        long lunch_start = (long) entity.getProperty("lunch_start");
        long lunch_end = (long) entity.getProperty("lunch_end");
        long dinner_start = (long) entity.getProperty("dinner_start");
        long dinner_end = (long) entity.getProperty("dinner_end");

        TimeRange lunch_time = TimeRange.fromStartEnd(Math.toIntExact(lunch_start), Math.toIntExact(lunch_end), false);
        TimeRange dinner_time = TimeRange.fromStartEnd(Math.toIntExact(dinner_start), Math.toIntExact(dinner_end), false);

        for (int index = 0; index < studentResultsList.size(); index++) {
          Entity studentEntity = studentResultsList.get(index);
          if (studentEntity.getProperty("cafeteria").equals(cafeteriaName)) {
            long lunch_start_pref = (long) studentEntity.getProperty("lunch_start");
            long lunch_end_pref = (long) studentEntity.getProperty("lunch_end");
            long dinner_start_pref = (long) studentEntity.getProperty("dinner_start");
            long dinner_end_pref = (long) studentEntity.getProperty("dinner_end");
            String name = (String) studentEntity.getProperty("name");

            TimeRange lunch = TimeRange.fromStartEnd(Math.toIntExact(lunch_start_pref), Math.toIntExact(lunch_end_pref), false);
            TimeRange dinner = TimeRange.fromStartEnd(Math.toIntExact(dinner_start_pref), Math.toIntExact(dinner_end_pref), false);

            Student student = new Student(name, lunch, dinner);
            students.add(student);
          }
        }

        CafeteriaScheduler scheduler = new CafeteriaScheduler();
        Schedule schedule = scheduler.schedule(lunch_time, dinner_time, mealTime, maxCapacity, students);
        List<Student> assignedStudents = scheduler.getAssignedStudents();

        // Update studentPref entities
        int studentIndex = 0;
        for (Student assignedStudent : assignedStudents) {
          for (int index = 0; index < studentResultsList.size(); index++) {
            Entity studentEntity = studentResultsList.get(index);
            String name = (String) studentEntity.getProperty("name");
            if (name.equals(assignedStudent.getName())) {
              studentEntity.setProperty("lunch_start_assigned", assignedStudent.getReceived("lunch").start());
              studentEntity.setProperty("lunch_end_assigned", assignedStudent.getReceived("lunch").start() + mealTime);
              studentEntity.setProperty("dinner_start_assigned", assignedStudent.getReceived("dinner").start());
              studentEntity.setProperty("dinner_end_assigned", assignedStudent.getReceived("dinner").start() + mealTime);
              studentEntity.setProperty("cafeteria_received", cafeteriaName);
              datastore.put(studentEntity);
              break;
            }
          }
        }

        EmbeddedEntity scheduleEntity = new EmbeddedEntity();

        setMealEntity(schedule, scheduleEntity, schedule.getLunchBlocks(), "lunch_blocks");
        setMealEntity(schedule, scheduleEntity, schedule.getDinnerBlocks(), "dinner_blocks");

        entity.setProperty("schedule", scheduleEntity);
        entity.setProperty("is_scheduled", true);
        datastore.put(entity);
      } catch(EntityNotFoundException e) {
        e.printStackTrace();
      }
    }

    response.sendRedirect("/cafeteria.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    String cafeteriaKeyString = request.getParameter("cafeteria");
    Key key = KeyFactory.stringToKey(cafeteriaKeyString);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    try {
      Entity cafeteria = datastore.get(key);
      EmbeddedEntity scheduleEntity = (EmbeddedEntity) cafeteria.getProperty("schedule");
      int mealTime = Integer.parseInt((String) cafeteria.getProperty("mealTime"));

      List<Block> lunchBlocksList = getMealData(cafeteria, scheduleEntity, mealTime, "lunch");
      List<Block> dinnerBlocksList = getMealData(cafeteria, scheduleEntity, mealTime, "dinner");

      Gson gson = new Gson();
      response.setContentType("application/json;");
      String lunch = gson.toJson(lunchBlocksList);
      String dinner = gson.toJson(dinnerBlocksList);
      String both = "[" + lunch + "," + dinner + "]";
      response.getWriter().println(both);
    } catch(EntityNotFoundException e) {
      e.printStackTrace();
    }
  }

  public List<Block> getMealData(Entity cafeteria, EmbeddedEntity scheduleEntity, int mealTime, String meal) {
    EmbeddedEntity blocksEntity = (EmbeddedEntity) scheduleEntity.getProperty("lunch_blocks");
    List<Block> blockList = new ArrayList<Block>();
    String blockStr = "block";

    int meal_start = Math.toIntExact((long) cafeteria.getProperty(meal.concat("_start")));
    int meal_end = Math.toIntExact((long) cafeteria.getProperty(meal.concat("_end")));

    int numBlocks = (meal_end-meal_start)/mealTime;
    for (int index = 0; index < numBlocks; index++) {
      String blockName = blockStr.concat(Integer.toString(index));
      EmbeddedEntity blockEntity = (EmbeddedEntity) blocksEntity.getProperty(blockName);
      int start = Math.toIntExact((long) blockEntity.getProperty("start"));
      int end = Math.toIntExact((long) blockEntity.getProperty("end"));
      TimeRange time = TimeRange.fromStartEnd(start, end, false);
      List<String> students = (List<String>) blockEntity.getProperty("students");
      Block blockToAdd = new Block(students, time, true);
      blockList.add(blockToAdd);
    }

    return blockList;
  }

  public void setMealEntity(Schedule schedule, EmbeddedEntity scheduleEntity, List<Block> blocks, String meal) {
    EmbeddedEntity blocksEntity = new EmbeddedEntity();
    String blockName = "block";
    int blockIndex = 0;
    for (Block block : blocks) {
      EmbeddedEntity blockEntity = new EmbeddedEntity();
      blockEntity.setProperty("start", block.getTime().start());
      blockEntity.setProperty("end", block.getTime().end());
      List<String> blockStudents = new ArrayList<>();
      for (Student blockStudent : block.getStudents()) {
        blockStudents.add(blockStudent.getName());
      }
      blockEntity.setProperty("students", blockStudents);
      blocksEntity.setProperty(blockName.concat(Integer.toString(blockIndex)), blockEntity);
      blockIndex++;
    }
    scheduleEntity.setProperty(meal, blocksEntity);
  }
}

