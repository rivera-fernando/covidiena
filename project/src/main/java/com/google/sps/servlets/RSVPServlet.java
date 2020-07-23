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
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.classes.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that allows user to RSVP to an event*/
@WebServlet("/RSVP-event")
public class RSVPServlet extends HttpServlet {
    
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();

    long requestId = Long.parseLong(request.getParameter("id"));
    Key eventKey = KeyFactory.createKey("ApprovedEvent", requestId);
    
    try { 
      Entity approvedEvent = datastore.get(eventKey);

      HttpSession session = request.getSession(false);
      String email = ((String) session.getAttribute("email")).toLowerCase();
      
      @SuppressWarnings("unchecked") // Cast can't verify generic type.
      Collection<String> attendees = (Collection<String>) approvedEvent.getProperty("attendees");
      if (!attendees.contains(email)) {
        attendees.add(email);
      }
      approvedEvent.setProperty("attendees", attendees);

      datastore.put(approvedEvent);
    } catch(EntityNotFoundException e) {
      e.printStackTrace();
      return;
    }
  }
}
