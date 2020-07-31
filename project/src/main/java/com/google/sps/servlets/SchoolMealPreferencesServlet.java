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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

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

import com.google.sps.classes.Cafeteria;

@WebServlet("/school-meal-preferences")
public class SchoolMealPreferencesServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    HttpSession session = request.getSession(false);
    boolean isAdmin = (boolean) session.getAttribute("is_admin");

    if (isAdmin) {
      String lunch_start = request.getParameter("lunch-start");
      int lunch_start_hour = Integer.parseInt(lunch_start.substring(0, 2));
      int lunch_start_minute = Integer.parseInt(lunch_start.substring(3));
      int lunch_start_int = lunch_start_hour * 60 + lunch_start_minute; 

      String lunch_end = request.getParameter("lunch-end");
      int lunch_end_hour = Integer.parseInt(lunch_end.substring(0, 2));
      int lunch_end_minute = Integer.parseInt(lunch_end.substring(3));
      int lunch_end_int = lunch_end_hour * 60 + lunch_end_minute; 

      String dinner_start = request.getParameter("dinner-start");
      int dinner_start_hour = Integer.parseInt(dinner_start.substring(0, 2));
      int dinner_start_minute = Integer.parseInt(dinner_start.substring(3));
      int dinner_start_int = dinner_start_hour * 60 + dinner_start_minute; 

      String dinner_end = request.getParameter("dinner-end");
      int dinner_end_hour = Integer.parseInt(dinner_end.substring(0, 2));
      int dinner_end_minute = Integer.parseInt(dinner_end.substring(3));
      int dinner_end_int = dinner_end_hour * 60 + dinner_end_minute; 

      String mealTime = request.getParameter("meal-time");
      String maxCapacity = request.getParameter("max-capacity");
      String cafeteriaName = request.getParameter("cafeteria-name");

      String school = (String) session.getAttribute("school");

      Entity cafeteriaEntity = new Entity("CafeteriaEntity");

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      
      cafeteriaEntity.setProperty("lunch_start", lunch_start_int);
      cafeteriaEntity.setProperty("lunch_end", lunch_end_int);
      cafeteriaEntity.setProperty("dinner_start", dinner_start_int);
      cafeteriaEntity.setProperty("dinner_end", dinner_end_int);
      cafeteriaEntity.setProperty("school", school);
      cafeteriaEntity.setProperty("mealTime", mealTime);
      cafeteriaEntity.setProperty("maxCapacity", maxCapacity);
      cafeteriaEntity.setProperty("cafeteriaName", cafeteriaName);
      cafeteriaEntity.setProperty("is_scheduled", false);

      datastore.put(cafeteriaEntity);
    }
    response.sendRedirect("/cafeteria.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession(false);
    String userSchool = (String) session.getAttribute("school");

    Query query = new Query("CafeteriaEntity");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Entity> resultsList = results.asList(FetchOptions.Builder.withDefaults());
    List<Cafeteria> cafeteriaList = new ArrayList<>();

    for (int i = 0; i < resultsList.size(); i++) {
      Entity entity = resultsList.get(i);
      String school = (String) entity.getProperty("school");

      if (school.equals(userSchool)) {
        String cafeteriaName = (String) entity.getProperty("cafeteriaName");
        int maxCapacity = Integer.parseInt((String) entity.getProperty("maxCapacity"));
        int mealTime = Integer.parseInt((String) entity.getProperty("mealTime"));
        long lunch_start = (long) entity.getProperty("lunch_start");
        long lunch_end = (long) entity.getProperty("lunch_end");
        long dinner_start = (long) entity.getProperty("dinner_start");
        long dinner_end = (long) entity.getProperty("dinner_end");
        boolean is_scheduled = (boolean) entity.getProperty("is_scheduled");
        Key key = entity.getKey();
        String keyString = KeyFactory.keyToString(key);

        Cafeteria cafeteria = new Cafeteria(school, cafeteriaName, maxCapacity, mealTime, 
          lunch_start, lunch_end, dinner_start, dinner_end, is_scheduled, keyString);
        
        cafeteriaList.add(cafeteria);
      }
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(cafeteriaList));
  }
}

