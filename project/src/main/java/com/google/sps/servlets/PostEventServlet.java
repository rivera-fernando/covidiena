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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.Date;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.classes.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.text.SimpleDateFormat;
import java.text.ParseException;
 
/** Servlet that posts an event*/
@WebServlet("/post-event")
public class PostEventServlet extends HttpServlet {
 
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the input from the form.
    String name = request.getParameter("name");
    String location = request.getParameter("location");
    String date = request.getParameter("date");
    String time = request.getParameter("time");
    // Format: hh:mm XM
    String hours = time.substring(0, 2);
    String minutes = time.substring(3, 5);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
    long dateTimestamp = 0;
    try {
      Date formattedDate = formatter.parse(date.substring(4));
      long milliseconds = Long.parseLong(hours)*3600000 + Long.parseLong(minutes)*60000;
      dateTimestamp = formattedDate.getTime() + milliseconds;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    String type = request.getParameter("event-type");
    String attendance = request.getParameter("event-attendance");
    String description = request.getParameter("description");
    long timestamp = System.currentTimeMillis();
 
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 
    if (userService.isUserLoggedIn()) {
      String email = userService.getCurrentUser().getEmail().toLowerCase();
      if (userService.isUserAdmin()) {
        Entity approvedEventEntity = new Entity("ApprovedEvent");
        approvedEventEntity.setProperty("name", name);
        approvedEventEntity.setProperty("location", location);
        approvedEventEntity.setProperty("date", date);
        approvedEventEntity.setProperty("time", time);
        approvedEventEntity.setProperty("type", type);
        approvedEventEntity.setProperty("attendance", attendance);
        approvedEventEntity.setProperty("description", description);
        approvedEventEntity.setProperty("timestamp", timestamp);
        approvedEventEntity.setProperty("email", email);
        approvedEventEntity.setProperty("admin-email", email);
        Collection<String> attendees = new HashSet<String>();
        // Add the central/mock user for cohesion
        attendees.add("mock@mock.edu");
        attendees.add(email);
        approvedEventEntity.setProperty("attendees", attendees);
        approvedEventEntity.setProperty("dateTimestamp", dateTimestamp);
 
        datastore.put(approvedEventEntity);
      } else {
        Entity unapprovedEventEntity = new Entity("UnapprovedEvent");
      
        unapprovedEventEntity.setProperty("name", name);
        unapprovedEventEntity.setProperty("location", location);
        unapprovedEventEntity.setProperty("date", date);
        unapprovedEventEntity.setProperty("time", time);
        unapprovedEventEntity.setProperty("type", type);
        unapprovedEventEntity.setProperty("attendance", attendance);
        unapprovedEventEntity.setProperty("description", description);
        unapprovedEventEntity.setProperty("timestamp", timestamp);
        unapprovedEventEntity.setProperty("email", email);
        unapprovedEventEntity.setProperty("dateTimestamp", dateTimestamp);
 
        datastore.put(unapprovedEventEntity);
      }
 
      response.sendRedirect("/events.html");
    } else {
      response.sendRedirect("/login.html");
    }
  }
}
