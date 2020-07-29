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
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** Servlet that posts and loads announcemnts*/
@WebServlet("/exposure")
public class Exposure extends HttpServlet {
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  UserService userService = UserServiceFactory.getUserService();
  GsonBuilder gsonBuilder = new GsonBuilder();
  Gson gson = gsonBuilder.create();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      HttpSession session = request.getSession(false); 
      String email = (String) session.getAttribute("email");

      Filter emailFilter =
        new FilterPredicate("email", FilterOperator.EQUAL, email);
      Query query = new Query("LocationLog").setFilter(emailFilter);
      PreparedQuery results = datastore.prepare(query);
      PreparedQuery all;
      Map<String, String> emails = new HashMap<String, String>();
      for (Entity entity : results.asIterable()) {
          Filter dateFilter = 
            new FilterPredicate("date", FilterOperator.EQUAL, entity.getProperty("date"));
          Filter latFilter = 
            new FilterPredicate("lat", FilterOperator.EQUAL, entity.getProperty("lat"));
          Filter lngFilter =
            new FilterPredicate("lng", FilterOperator.EQUAL, entity.getProperty("lng"));
          Filter timeAndPlaceFilter =
            CompositeFilterOperator.and(dateFilter, latFilter, lngFilter);
          //somedthing

          query = new Query("LocationLog").setFilter(timeAndPlaceFilter);
          all = datastore.prepare(query);
          for (Entity new_entity : all.asIterable()) {
              if (!((String) new_entity.getProperty("email")).equals(email)) {
                  Entity exposedEntity = new Entity("ExposedEmails");
                  exposedEntity.setProperty("email", (String) new_entity.getProperty("email"));
                  exposedEntity.setProperty("place", (String) new_entity.getProperty("name"));
                  exposedEntity.setProperty("lat", (String) new_entity.getProperty("lat"));
                  exposedEntity.setProperty("lng", (String) new_entity.getProperty("lng"));
                  datastore.put(exposedEntity);
              }
          }
      }
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      HttpSession session = request.getSession(false); 
      ArrayList<Object> data = new ArrayList<Object>();
      Query query = new Query("ExposedEmails");
      PreparedQuery results = datastore.prepare(query);
      for(Entity entity:results.asIterable()){
        ArrayList<Object> personAndPlace = new ArrayList<Object>();
        personAndPlace.add((String) entity.getProperty("email"));
        personAndPlace.add((String) entity.getProperty("place"));
        personAndPlace.add((String) entity.getProperty("lat"));
        personAndPlace.add((String) entity.getProperty("lng"));


        data.add(personAndPlace);
      }
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(data));
  }
}

