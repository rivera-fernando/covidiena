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

/** Servlet that loads upcoming events*/
@WebServlet("/approve-event")
public class ApproveEventServlet extends HttpServlet {
    
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();

    long requestId = Long.parseLong(request.getParameter("id"));
    Key eventKey = KeyFactory.createKey("UnapprovedEvent", requestId);
    
    try { 
      Entity unapprovedEvent = datastore.get(eventKey);
      datastore.delete(eventKey);

      Entity approvedEventEntity = new Entity("ApprovedEvent");
      approvedEventEntity.setProperty("name", unapprovedEvent.getProperty("name"));
      approvedEventEntity.setProperty("date", unapprovedEvent.getProperty("date"));
      approvedEventEntity.setProperty("type", unapprovedEvent.getProperty("type"));
      approvedEventEntity.setProperty("attendance", unapprovedEvent.getProperty("attendance"));
      approvedEventEntity.setProperty("description", unapprovedEvent.getProperty("description"));
      approvedEventEntity.setProperty("timestamp", unapprovedEvent.getProperty("timestamp"));
      approvedEventEntity.setProperty("dateTimestamp", unapprovedEvent.getProperty("dateTimestamp"));
      approvedEventEntity.setProperty("email", unapprovedEvent.getProperty("email"));
      approvedEventEntity.setProperty("admin-email", userService.getCurrentUser().getEmail().toLowerCase());
      Collection<String> attendees = new HashSet<String>();
      String email = (String) unapprovedEvent.getProperty("email");
      // Add the central/mock user to all the events for cohesion
      attendees.add("mock@mock.edu");
      attendees.add(email);
      approvedEventEntity.setProperty("attendees", attendees);

      datastore.put(approvedEventEntity);
      response.sendRedirect("/events.html");
    } catch(EntityNotFoundException e) {
      return;
    }
  }
}
