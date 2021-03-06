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

/** Servlet that allows user to remove an RSVP to an event*/
@WebServlet("/remove-event-rsvp")
public class RemoveEventRSVPServlet extends HttpServlet {
    
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    //Find the appropriate event using a key
    long requestId = Long.parseLong(request.getParameter("id"));
    Key eventKey = KeyFactory.createKey("ApprovedEvent", requestId);
    
    try { 
      Entity approvedEvent = datastore.get(eventKey);

      HttpSession session = request.getSession(false);
      String email = ((String) session.getAttribute("email")).toLowerCase();
      
      @SuppressWarnings("unchecked") // Cast can't verify generic type.
      Collection<String> attendees = (Collection<String>) approvedEvent.getProperty("attendees");
      attendees.remove(email);
      approvedEvent.setProperty("attendees", attendees);

      datastore.put(approvedEvent);

      response.sendRedirect("/events.html");
    } catch(EntityNotFoundException e) {
      e.printStackTrace();
      return;
    }
  }
}
