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
import java.util.HashSet;
 
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
      HashSet<String> emails = new HashSet();
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
              emails.add((String) new_entity.getProperty("email"));
          }
      }
      Iterator<String> itr = emails.iterator();  
      while(itr.hasNext()){  
        Entity exposedEntity = new Entity("ExposedEmails");
        exposedEntity.setProperty("email", itr.next());
        datastore.put(exposedEntity);
      }     
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      HttpSession session = request.getSession(false); 
      //example school: "University of Florida", phone: 111-111-1111
      String school = (String) session.getAttribute("school");
      ArrayList<Object> data = new ArrayList<Object>();
      Query query = new Query("Announcements").addSort("when", SortDirection.DESCENDING);
      PreparedQuery results = datastore.prepare(query);
      for(Entity entity:results.asIterable()){
          if (((String) entity.getProperty("school")).toLowerCase().equals(school.toLowerCase())) {
              ArrayList<Object> pair = new ArrayList<Object>();
              pair.add((String) entity.getProperty("from"));
              pair.add((String) entity.getProperty("title"));
              pair.add((String) entity.getProperty("importance"));
              pair.add((String) entity.getProperty("content"));
              data.add(pair);
          }
      }
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(data));
  }
}
