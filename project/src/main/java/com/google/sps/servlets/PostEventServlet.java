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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.data.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that posts an event*/
@WebServlet("/post-event")
public class PostEventServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the input from the form.
    String name = request.getParameter("name");
    String date = request.getParameter("date");
    String type = request.getParameter("event-type");
    String attendance = request.getParameter("event-attendance");
    String description = request.getParameter("description");
    long timestamp = System.currentTimeMillis();

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail().toLowerCase();
      Entity unapprovedEventEntity = new Entity("UnapprovedEvent");
      
      unapprovedEventEntity.setProperty("name", name);
      unapprovedEventEntity.setProperty("date", date);
      unapprovedEventEntity.setProperty("type", type);
      unapprovedEventEntity.setProperty("attendance", attendance);
      unapprovedEventEntity.setProperty("description", description);
      unapprovedEventEntity.setProperty("timestamp", timestamp);
      unapprovedEventEntity.setProperty("email", userEmail);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(unapprovedEventEntity);

      response.sendRedirect("/events.html");
    } else {
      response.sendRedirect("/login.html");
    }
  }
}
