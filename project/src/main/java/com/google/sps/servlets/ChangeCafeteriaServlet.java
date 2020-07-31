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
import java.util.Objects;
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

@WebServlet("/change-cafeteria")
public class ChangeCafeteriaServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    String name = (String) session.getAttribute("name");

    String targetCafeteriaKeyString = request.getParameter("cafeteriaKey");
    String targetCafeteriaName = request.getParameter("targetCafeteriaName");
    String targetMeal = request.getParameter("meal");
    String targetBlockName = "block";
    long targetBlockStart = Long.parseLong((String) request.getParameter("blockStart"));
    long mealDuration = Long.parseLong((String) request.getParameter("mealDuration"));

    String currCafeteria = request.getParameter("currCafeteria");
    long currMealStart = Long.parseLong((String) request.getParameter("currTimeRangeStart"));
    long currMealEnd = Long.parseLong((String) request.getParameter("currTimeRangeEnd"));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Add to new cafeteria
    Key key = KeyFactory.stringToKey(targetCafeteriaKeyString);
    try {
      Entity targetCafeteria = datastore.get(key);
      EmbeddedEntity schedule = (EmbeddedEntity) targetCafeteria.getProperty("schedule");
      EmbeddedEntity blocks = (EmbeddedEntity) schedule.getProperty(targetMeal.concat("_blocks"));
      long targetBlockNum = (targetBlockStart - ((long) targetCafeteria.getProperty(targetMeal.concat("_start")))) 
        / mealDuration;
      EmbeddedEntity targetBlock = (EmbeddedEntity) blocks.getProperty(targetBlockName.concat(Long.toString(targetBlockNum)));
      List<String> students = (List<String>) targetBlock.getProperty("students");
      if (Objects.isNull(students)) {
        students = new ArrayList<String>();
      }
      students.add(name);
      targetBlock.setProperty("students", students);
      blocks.setProperty(targetBlockName.concat(Long.toString(targetBlockNum)), targetBlock);
      schedule.setProperty(targetMeal.concat("_blocks"), blocks);
      targetCafeteria.setProperty("schedule", schedule);
      datastore.put(targetCafeteria);
    } catch(EntityNotFoundException e) {
      e.printStackTrace();
    }

    // Remove from current block
    Query cafeteriaQuery = new Query("CafeteriaEntity");
    PreparedQuery cafeteriaResults = datastore.prepare(cafeteriaQuery);
    List<Entity> cafeteriaResultsList = cafeteriaResults.asList(FetchOptions.Builder.withDefaults());
    for (int i = 0; i < cafeteriaResultsList.size(); i++) {
      Entity oldCafeteria = cafeteriaResultsList.get(i);
      if (((String) oldCafeteria.getProperty("school")).equals((String) session.getAttribute("school"))
        && ((String) oldCafeteria.getProperty("cafeteriaName")).equals(currCafeteria)) {
        EmbeddedEntity schedule = (EmbeddedEntity) oldCafeteria.getProperty("schedule");
        EmbeddedEntity blocks = (EmbeddedEntity) schedule.getProperty(targetMeal.concat("_blocks"));
        long blockNum = (currMealStart - ((long) oldCafeteria.getProperty(targetMeal.concat("_start")))) 
          / (Long.parseLong((String) oldCafeteria.getProperty("mealTime")));
        EmbeddedEntity targetBlock = (EmbeddedEntity) blocks.getProperty(targetBlockName.concat(Long.toString(blockNum)));
        List<Student> students = (List<Student>) targetBlock.getProperty("students");
        students.remove(name);
        targetBlock.setProperty("students", students);
        blocks.setProperty(targetBlockName.concat(Long.toString(blockNum)), targetBlock);
        schedule.setProperty(targetMeal.concat("_blocks"), blocks);
        oldCafeteria.setProperty("schedule", schedule);
        datastore.put(oldCafeteria);
      }
    }

    // Change studentPref
    Query studentPrefQuery = new Query("StudentPref");
    PreparedQuery studentPrefResults = datastore.prepare(studentPrefQuery);
    List<Entity> studentPrefResultsList = studentPrefResults.asList(FetchOptions.Builder.withDefaults());
    for (int i = 0; i < studentPrefResultsList.size(); i++) {
      Entity studentPrefEntity = studentPrefResultsList.get(i);
      if (((String) studentPrefEntity.getProperty("email")).toLowerCase().equals(((String) session.getAttribute("email")).toLowerCase())) {
        studentPrefEntity.setProperty("cafeteria_received", targetCafeteriaName);
        studentPrefEntity.setProperty(targetMeal.concat("_start_assigned"), targetBlockStart);
        studentPrefEntity.setProperty(targetMeal.concat("_end_assigned"), targetBlockStart + mealDuration);
        datastore.put(studentPrefEntity);
      }
    }

    response.sendRedirect("/cafeteria.html");
  }
}

