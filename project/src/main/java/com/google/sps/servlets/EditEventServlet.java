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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
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
import com.google.sps.data.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/** Servlet that loads upcoming events*/
@WebServlet("/edit-event")
public class EditEventServlet extends HttpServlet {
    
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    long entityId = Long.parseLong(request.getParameter("event-id"));
    String entityType = request.getParameter("event-status");
    Key eventKey = KeyFactory.createKey(entityType, entityId);
    
    try { 
      Entity event = datastore.get(eventKey);

      // Get the input from the form.
      String name = request.getParameter("edit-name");
      String location = request.getParameter("edit-location");
      String date = request.getParameter("edit-date");
      String time = request.getParameter("edit-time");
      // Format: hh:mm XM
      String hours = time.substring(0, 2);
      String minutes = time.substring(3, 5);
      SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
      long dateTimestamp = 0;
      try {
        Date formattedDate = formatter.parse(date);
        long milliseconds = Long.parseLong(hours)*3600000 + Long.parseLong(minutes)*60000;
        dateTimestamp = formattedDate.getTime() + milliseconds;
      } catch (ParseException e) {
        e.printStackTrace();
      }
      String type = request.getParameter("edit-event-type");
      String attendance = request.getParameter("edit-event-attendance");
      String description = request.getParameter("edit-description");


      event.setProperty("name", name);
      event.setProperty("location", location);
      event.setProperty("date", date);
      event.setProperty("time", time);
      event.setProperty("type", type);
      event.setProperty("attendance", attendance);
      event.setProperty("description", description);
      event.setProperty("dateTimestamp", dateTimestamp);

      datastore.put(event);
      response.sendRedirect("/events.html");
    } catch(EntityNotFoundException e) {
      response.sendRedirect("/events.html");
      return;
    }
  }
}
