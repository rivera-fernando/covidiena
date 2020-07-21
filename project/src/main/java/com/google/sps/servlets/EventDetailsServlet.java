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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.sps.classes.User;
import com.google.sps.classes.Event;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import javax.servlet.http.HttpSession;
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.util.Collection;
 
/** Servlet that posts an event*/
@WebServlet("/event-details")
public class EventDetailsServlet extends HttpServlet {
 
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession(false);
    long eventId = Long.parseLong((String) request.getParameter("eventId"));
    String eventType = (String) request.getParameter("eventType");
    session.setAttribute("eventId", eventId);
    session.setAttribute("eventType", eventType);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    HttpSession session = request.getSession(false);
    long eventId = (long) session.getAttribute("eventId");
    String eventType = (String) session.getAttribute("eventType");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key eventKey = KeyFactory.createKey(eventType, eventId);
    try { 
      Entity entity = datastore.get(eventKey);

      List<Event> events = new ArrayList<>();

      String email = (String) session.getAttribute("email");

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
      List<String> changes = (List<String>) entity.getProperty("changes");
      String adminEmail = (String) entity.getProperty("admin-email");
      boolean edited = (boolean) entity.getProperty("edited");
      List<String> comments = (List<String>) entity.getProperty("comments");
      @SuppressWarnings("unchecked") // Cast can't verify generic type.
      Collection<String> attendees = (Collection<String>) entity.getProperty("attendees");

      Event event = new Event(id, name, location, date, time, description, type, attendance, 
        timestamp, true, eventType, imageKey, -1, attendees.size(), capacity, rejected, changes, 
        adminEmail, edited, "", comments);

      events.add(event);
      Gson gson = new Gson();
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(events));
    } catch(EntityNotFoundException e) {
      return;
    }
  }
}
