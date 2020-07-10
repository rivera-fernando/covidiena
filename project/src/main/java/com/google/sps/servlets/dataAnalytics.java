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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

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

@WebServlet("/dataAnalytics")
public class dataAnalytics extends HttpServlet {
  UserService userService = UserServiceFactory.getUserService();
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  GsonBuilder gsonBuilder = new GsonBuilder();
  Gson gson = gsonBuilder.create();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      HttpSession session = request.getSession(false); 
      String person = (String) session.getAttribute("person");
      String school = person.substring(person.indexOf("school\":")+9, person.indexOf("\",\"phone"));
      DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
      String day = dateFormat.format(new java.util.Date());
      
      Filter schoolFilter = 
        new FilterPredicate("school", FilterOperator.EQUAL, school);
      Query query = new Query("user_temp").setFilter(schoolFilter);
      PreparedQuery results = datastore.prepare(query);
      double total = 0;
      double num_temps = 0;
      int fevers = 0;
      for (Entity entity:results.asIterable()){
          if (entity.getKey().getName().equals(day)) {
            num_temps += 1;
            total += (double) entity.getProperty("temp");
          }
          if ((double) entity.getProperty("temp") > 99.0) {
              fevers += 1;
          }
      }

      Entity commentEntity = new Entity("daily_temp", day);
      commentEntity.setProperty("temp", total/num_temps);
      commentEntity.setProperty("school", person.substring(person.indexOf("school\":")+9, person.indexOf("\",\"phone")));
      datastore.put(commentEntity);
      String val = Double.toString(total/num_temps);
      ArrayList<String> data = new ArrayList<String>();
      data.add(val);
      data.add(Integer.toString(fevers));
      if (person.substring(person.indexOf("admin\":")+7, person.indexOf("}")).equals("true")) {
          response.setContentType("application/json;");
          response.getWriter().println(gson.toJson(data));
      }

  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      HttpSession session = request.getSession(false); 
      String person = (String) session.getAttribute("person");
      String school = person.substring(person.indexOf("school\":")+9, person.indexOf("\",\"phone"));
      Filter schoolFilter = 
        new FilterPredicate("school", FilterOperator.EQUAL, school);
      Query query = new Query("daily_temp").setFilter(schoolFilter).addSort(Entity.KEY_RESERVED_PROPERTY, SortDirection.ASCENDING);
      PreparedQuery results = datastore.prepare(query);
      ArrayList<Object> data = new ArrayList<Object>();
      for(Entity entity:results.asIterable()){
            ArrayList<Object> pair = new ArrayList<Object>();
            pair.add(entity.getKey().getName());
            pair.add(entity.getProperty("temp"));
            pair.add(96);
            pair.add(99);
            data.add(pair);
      }
      if (person.substring(person.indexOf("admin\":")+7, person.indexOf("}")).equals("true")) {
          response.setContentType("application/json;");
          response.getWriter().println(gson.toJson(data));
      }
  }
}
