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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.sps.classes.CafeteriaScheduler;
import com.google.sps.classes.Schedule;
import com.google.sps.classes.TimeRange;
import com.google.sps.classes.Student;

@WebServlet("/cafeteria-preferences")
public class PreferencesServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    HttpSession session = request.getSession(false);

    // Get data from form to create timeranges
    String lunch_start = request.getParameter("lunch-pref-start");
    int lunch_start_hour = Integer.parseInt(lunch_start.substring(0, 2));
    int lunch_start_minute = Integer.parseInt(lunch_start.substring(3));
    int lunch_start_int = lunch_start_hour * 60 + lunch_start_minute; 

    String lunch_end = request.getParameter("lunch-pref-end");
    int lunch_end_hour = Integer.parseInt(lunch_end.substring(0, 2));
    int lunch_end_minute = Integer.parseInt(lunch_end.substring(3));
    int lunch_end_int = lunch_end_hour * 60 + lunch_end_minute; 

    String dinner_start = request.getParameter("dinner-pref-start");
    int dinner_start_hour = Integer.parseInt(dinner_start.substring(0, 2));
    int dinner_start_minute = Integer.parseInt(dinner_start.substring(3));
    int dinner_start_int = dinner_start_hour * 60 + dinner_start_minute; 

    String dinner_end = request.getParameter("dinner-pref-end");
    int dinner_end_hour = Integer.parseInt(dinner_end.substring(0, 2));
    int dinner_end_minute = Integer.parseInt(dinner_end.substring(3));
    int dinner_end_int = dinner_end_hour * 60 + dinner_end_minute; 
    
    String cafeteria = (String) request.getParameter("cafeteria-field");
    String name = (String) session.getAttribute("name");
    String school = (String) session.getAttribute("school");

    Entity studentPrefEntity = new Entity("StudentPref");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    studentPrefEntity.setProperty("lunch_start", lunch_start_int);
    studentPrefEntity.setProperty("lunch_end", lunch_end_int);
    studentPrefEntity.setProperty("dinner_start", dinner_start_int);
    studentPrefEntity.setProperty("dinner_end", dinner_end_int);
    studentPrefEntity.setProperty("school", school);
    studentPrefEntity.setProperty("name", name);
    studentPrefEntity.setProperty("cafeteria", cafeteria);
    studentPrefEntity.setProperty("email", ((String) session.getAttribute("email")).toLowerCase());

    datastore.put(studentPrefEntity);
    response.sendRedirect("/cafeteria.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession();
    String email = ((String) session.getAttribute("email")).toLowerCase();

    Query query = new Query("StudentPref");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Entity> resultsList = results.asList(FetchOptions.Builder.withDefaults());
    List<Student> student = new ArrayList<>();

    for (int i = 0; i < resultsList.size(); i++) {
      Entity entity = resultsList.get(i);
      String studentEmail = ((String) entity.getProperty("email")).toLowerCase();

      if (email.equals(studentEmail)) {
        String name = (String) entity.getProperty("name");
        long lunch_start = (long) entity.getProperty("lunch_start");
        long lunch_end = (long) entity.getProperty("lunch_end");
        long dinner_start = (long) entity.getProperty("dinner_start");
        long dinner_end = (long) entity.getProperty("dinner_end");

        TimeRange lunch = TimeRange.fromStartEnd(Math.toIntExact(lunch_start), Math.toIntExact(lunch_end), false);
        TimeRange dinner = TimeRange.fromStartEnd(Math.toIntExact(dinner_start), Math.toIntExact(dinner_end), false);

        Student newStudent = new Student(name, lunch, dinner);
        
        student.add(newStudent);
      }
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(student));
  }
}

