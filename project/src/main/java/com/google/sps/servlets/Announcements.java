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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** Servlet that posts and loads announcemnts*/
@WebServlet("/announcement")
public class Announcements extends HttpServlet {
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  UserService userService = UserServiceFactory.getUserService();
  GsonBuilder gsonBuilder = new GsonBuilder();
  Gson gson = gsonBuilder.create();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String name = "";
      String school = "";
      Query query = new Query("User");
      PreparedQuery results = datastore.prepare(query);
      for(Entity entity:results.asIterable()){
          if (((String) entity.getProperty("email")).toLowerCase().equals(userService.getCurrentUser().getEmail().toLowerCase())) {
              name = (String) entity.getProperty("name");
              school = (String) entity.getProperty("school");
          }
      }
      String title = request.getParameter("title");
      String content = request.getParameter("content");
      Entity commentEntity = new Entity("Announcements");
      java.util.Date now=new java.util.Date();
      commentEntity.setProperty("from", name);
      commentEntity.setProperty("title", title);
      commentEntity.setProperty("content", content);
      commentEntity.setProperty("when", now);
      commentEntity.setProperty("school", school);
      datastore.put(commentEntity);
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      ArrayList<Object> data = new ArrayList<Object>();
      String school = "";
      Query query = new Query("User");
      PreparedQuery results = datastore.prepare(query);
      for(Entity entity:results.asIterable()){
          if (((String) entity.getProperty("email")).toLowerCase().equals(userService.getCurrentUser().getEmail().toLowerCase())) {
              school = (String) entity.getProperty("school");
          }
      }
      query = new Query("Announcements").addSort("when", SortDirection.DESCENDING);
      results = datastore.prepare(query);
      for(Entity entity:results.asIterable()){
          if (((String) entity.getProperty("school")).toLowerCase().equals(school.toLowerCase())) {
              ArrayList<Object> pair = new ArrayList<Object>();
              pair.add((String) entity.getProperty("from"));
              pair.add((String) entity.getProperty("title"));
              pair.add((String) entity.getProperty("content"));
              data.add(pair);
          }
      }
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(data));
      //this will get all announcements entities, with school = current user school
  }
}
