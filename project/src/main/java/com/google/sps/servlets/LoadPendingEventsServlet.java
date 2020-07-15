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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpSession;
 
/** Servlet that loads pending events*/
@WebServlet("/load-pending")
public class LoadPendingEventsServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("UnapprovedEvent");
    query.addSort("dateTimestamp", SortDirection.ASCENDING);
 
    HttpSession session = request.getSession(false);
    boolean found = false;
    if (session.getAttribute("name") != null) {
      found = true;
    }
    if (found) {
      String email = ((String) session.getAttribute("email")).toLowerCase();
 
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);
      List<Entity> resultsList = results.asList(FetchOptions.Builder.withDefaults());
      List<Event> pendingEvents = new ArrayList<>();
 
      // If the user is an admin, load ALL pending. Otherwise, just the ones that the student posted.
      boolean isAdmin = (boolean) session.getAttribute("admin");
      if (isAdmin) {
        // Should probably find a better way to send this metadata like Lian told me to
        Event metadata = new Event(0, "Admin", "", "", "", "", "", "", 0, false, "", "", -1, 0, 0, false, "", "", false, "");
        pendingEvents.add(metadata);
        for (int i = 0; i < resultsList.size(); i++) {
          Entity entity = resultsList.get(i);
          long id = entity.getKey().getId();
          String name = (String) entity.getProperty("name");
          String location = (String) entity.getProperty("location");
          String date = (String) entity.getProperty("date");
          String time = (String) entity.getProperty("time");
          String description = (String) entity.getProperty("description");
          String type = (String) entity.getProperty("attendance");
          String attendance = (String) entity.getProperty("type");
          long timestamp = (long) entity.getProperty("timestamp");
          boolean isMine =  entity.getProperty("email").equals(email);
          String imageKey = (String) entity.getProperty("imageKey");
          long capacity = (long) entity.getProperty("capacity");
          boolean rejected = (boolean) entity.getProperty("rejected");
          boolean edited = (boolean) entity.getProperty("edited");
          String changes = (String) entity.getProperty("changes");
          String adminEmail = (String) entity.getProperty("admin-email");

          if (!rejected || edited) {
            Event event = new Event(id, name, location, date, time, description, type, attendance,
              timestamp, isMine, "unapproved", imageKey, -1, 0, 0, rejected, changes, adminEmail, 
              edited, "Pending");
            pendingEvents.add(event);
          }
        }
      } else {
        for (int i = 0; i < resultsList.size(); i++) {
 
          Entity entity = resultsList.get(i);
          // Load the event only if this user's email matches the email of 
          // the person that posted the event.
          if (entity.getProperty("email").equals(email)) {
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("name");
            String location = (String) entity.getProperty("location");
            String date = (String) entity.getProperty("date");
            String time = (String) entity.getProperty("time");
            String description = (String) entity.getProperty("description");
            String type = (String) entity.getProperty("attendance");
            String attendance = (String) entity.getProperty("type");
            long timestamp = (long) entity.getProperty("timestamp");
            boolean isMine =  entity.getProperty("email").equals(email);
            String imageKey = (String) entity.getProperty("imageKey");
            long capacity = (long) entity.getProperty("capacity");
            boolean rejected = (boolean) entity.getProperty("rejected");
            String changes = (String) entity.getProperty("changes");
            String adminEmail = (String) entity.getProperty("admin-email");
            boolean edited = (boolean) entity.getProperty("edited");
 
            Event event = new Event(id, name, location, date, time, description, type, attendance, 
              timestamp, true, "UnapprovedEvent", imageKey, -1, 0, capacity, rejected, changes, 
              adminEmail, edited, "Pending");
                
            pendingEvents.add(event);
          }
        }
      }
 
      Gson gson = new Gson();
 
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(pendingEvents));
    } else {
      response.sendRedirect("/login.html");
    }
  }
}
