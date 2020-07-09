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
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** Servlet that posts and loads announcemnts*/
@WebServlet("/temp")
public class Temperature extends HttpServlet {
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  UserService userService = UserServiceFactory.getUserService();
  GsonBuilder gsonBuilder = new GsonBuilder();
  Gson gson = gsonBuilder.create();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      HttpSession session=request.getSession(false); 
      String n=(String)session.getAttribute("person");
      DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
      String day = dateFormat.format(new Date());
      //this part will create the db entries
      String temp = request.getParameter("temp");
      Entity commentEntity = new Entity("user_temp", day);
      commentEntity.setProperty("email", n.substring(n.indexOf("email\":")+8, n.indexOf("\",\"password")));
      commentEntity.setProperty("school", n.substring(n.indexOf("school\":")+9, n.indexOf("\",\"phone")));
      commentEntity.setProperty("temp", Double.parseDouble(temp));
      datastore.put(commentEntity);
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      HttpSession session=request.getSession(false); 
      String n=(String)session.getAttribute("person");
      Query query = new Query("user_temp").addSort(Entity.KEY_RESERVED_PROPERTY, SortDirection.ASCENDING);
      PreparedQuery results = datastore.prepare(query);
      ArrayList<Object> data = new ArrayList<Object>();
      for(Entity entity:results.asIterable()){
          if (((String) entity.getProperty("email")).toLowerCase().equals(n.substring(n.indexOf("email\":")+8, n.indexOf("\",\"password")))) {
              ArrayList<Object> pair = new ArrayList<Object>();
              pair.add(entity.getKey().getName());
              pair.add(entity.getProperty("temp"));
              pair.add(96);
              pair.add(99);
              data.add(pair);
          }
      }
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(data));
  }
}
