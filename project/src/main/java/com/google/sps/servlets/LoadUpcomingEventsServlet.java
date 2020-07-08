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
import com.google.gson.Gson;
import com.google.sps.classes.Event;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.classes.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
 
/** Servlet that loads upcoming events*/
@WebServlet("/load-upcoming")
public class LoadUpcomingEventsServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("ApprovedEvent");
    query.addSort("dateTimestamp", SortDirection.ASCENDING);
 
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String email = userService.getCurrentUser().getEmail().toLowerCase();
 
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);
      List<Entity> resultsList = results.asList(FetchOptions.Builder.withDefaults());
      List<Event> upcomingEvents = new ArrayList<>();
 
      for (int i = 0; i < resultsList.size(); i++) {
 
        Entity entity = resultsList.get(i);
        // Load the event only if this user's email matches the email of an attendee
        @SuppressWarnings("unchecked") // Cast can't verify generic type.
        Collection<String> attendees = (Collection<String>) entity.getProperty("attendees");
        if (!attendees.isEmpty()) {
          if (attendees.contains(email)) {
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("name");
            String location = (String) entity.getProperty("location");
            String date = (String) entity.getProperty("date");
            String time = (String) entity.getProperty("time");
            String description = (String) entity.getProperty("description");
            String type = (String) entity.getProperty("attendance");
            String attendance = (String) entity.getProperty("type");
            long timestamp = (long) entity.getProperty("timestamp");
 
            Event event = new Event(id, name, location, date, time, description, type, attendance, timestamp, entity.getProperty("email").equals(email), "ApprovedEvent");
            upcomingEvents.add(event);
          }
        }
      }
 
      Gson gson = new Gson();
 
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(upcomingEvents));
    }
    else {
      response.sendRedirect("/login.html");
    }
  }
}
