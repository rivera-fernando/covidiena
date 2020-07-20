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
import com.google.sps.classes.Day;
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
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
 
/** Servlet that loads pending events*/
@WebServlet("/load-search")
public class SearchEventsServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query approvedQuery = new Query("ApprovedEvent");
    approvedQuery.addSort("dateTimestamp", SortDirection.ASCENDING);
    Query pastQuery = new Query("PastEvent");
    pastQuery.addSort("dateTimestamp", SortDirection.ASCENDING);

    HttpSession session = request.getSession(false);
    boolean found = false;
    if (session.getAttribute("name") != null) {
      found = true;
    }

    if (found) {
      String email = ((String) session.getAttribute("email")).toLowerCase();
 
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery approvedResults = datastore.prepare(approvedQuery);
      List<Entity> approvedResultsList = approvedResults.asList(FetchOptions.Builder.withDefaults());
      PreparedQuery pastResults = datastore.prepare(pastQuery);
      List<Entity> pastResultsList = pastResults.asList(FetchOptions.Builder.withDefaults());
      
      List<Entity> resultsList = new ArrayList<>();
      resultsList.addAll(pastResultsList);
      resultsList.addAll(approvedResultsList);

      long today = System.currentTimeMillis();
      
      List<Event> events = new ArrayList<>();
 
      for (int i = 0; i < resultsList.size(); i++) {
 
        Entity entity = resultsList.get(i);
        // Load the event only if this user's email matches the email of an attendee
        @SuppressWarnings("unchecked") // Cast can't verify generic type.
        Collection<String> attendees = (Collection<String>) entity.getProperty("attendees");
        if (!attendees.isEmpty()) {
          long id = entity.getKey().getId();
          String name = (String) entity.getProperty("name");
          String location = (String) entity.getProperty("location");
          String date = (String) entity.getProperty("date");
          String time = (String) entity.getProperty("time");
          String description = (String) entity.getProperty("description");
          String type = (String) entity.getProperty("attendance");
          String attendance = (String) entity.getProperty("type");
          long timestamp = (long) entity.getProperty("timestamp");
          String imageKey = (String) entity.getProperty("imageKey");
          long dateTimestamp = (long) entity.getProperty("dateTimestamp");
          long capacity = (long) entity.getProperty("capacity");
          boolean edited = (boolean) entity.getProperty("edited");

          Calendar calendar = Calendar.getInstance();
          calendar.clear();
          calendar.setTimeInMillis(dateTimestamp);
          int day = calendar.get(Calendar.DAY_OF_WEEK);
        
          String category = "";
          if (dateTimestamp < today) {
            category = "Past";
          } else if (attendees.contains(email)) {
            category = "Upcoming";
          } else {
            category = "Explore";
          }
          
          Event event = new Event(id, name, location, date, time, description, type, 
            attendance, timestamp, entity.getProperty("email").equals(email), "ApprovedEvent", 
            imageKey, day, attendees.size(), capacity, false, new ArrayList<String>(), "", 
            edited, category, new ArrayList<String>());

          events.add(event);
        }
      }
 
      Gson gson = new Gson();
 
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(events));
    } else {
      response.sendRedirect("/login.html");
    }
  }
}
