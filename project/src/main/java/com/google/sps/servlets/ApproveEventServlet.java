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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.sps.classes.EventUpdate;


/** Servlet that loads upcoming events*/
@WebServlet("/approve-event")
public class ApproveEventServlet extends HttpServlet {
    
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 
    long requestId = Long.parseLong(request.getParameter("event-changes-id"));
    boolean approved = (boolean) Boolean.parseBoolean(request.getParameter("approved"));
    Key eventKey = KeyFactory.createKey("UnapprovedEvent", requestId);

    HttpSession session = request.getSession(false);
    
    if (approved) {
      try { 
        Entity unapprovedEvent = datastore.get(eventKey);
        datastore.delete(eventKey);
  
        Entity approvedEventEntity = new Entity("ApprovedEvent");
        approvedEventEntity.setProperty("name", unapprovedEvent.getProperty("name"));
        approvedEventEntity.setProperty("location", unapprovedEvent.getProperty("location"));
        approvedEventEntity.setProperty("date", unapprovedEvent.getProperty("date"));
        approvedEventEntity.setProperty("time", unapprovedEvent.getProperty("time"));
        approvedEventEntity.setProperty("type", unapprovedEvent.getProperty("type"));
        approvedEventEntity.setProperty("attendance", unapprovedEvent.getProperty("attendance"));
        approvedEventEntity.setProperty("description", unapprovedEvent.getProperty("description"));
        approvedEventEntity.setProperty("timestamp", unapprovedEvent.getProperty("timestamp"));
        approvedEventEntity.setProperty("dateTimestamp", unapprovedEvent.getProperty("dateTimestamp"));
        approvedEventEntity.setProperty("email", unapprovedEvent.getProperty("email"));
        approvedEventEntity.setProperty("admin-email", ((String) session.getAttribute("email")).toLowerCase());
        approvedEventEntity.setProperty("imageKey", unapprovedEvent.getProperty("imageKey"));
        approvedEventEntity.setProperty("capacity", unapprovedEvent.getProperty("capacity"));
        approvedEventEntity.setProperty("edited", unapprovedEvent.getProperty("edited"));
        Collection<String> attendees = new HashSet<String>();
        String email = (String) unapprovedEvent.getProperty("email");
        // Add the central/mock user to all the events for cohesion
        attendees.add("mock@mock.edu");
        approvedEventEntity.setProperty("attendees", attendees);
  
        datastore.put(approvedEventEntity);

        // Handle event update
        Entity updateEntity = new Entity("EventUpdate");
        updateEntity.setProperty("id", requestId);
        updateEntity.setProperty("name", unapprovedEvent.getProperty("name"));
        updateEntity.setProperty("owner", unapprovedEvent.getProperty("email"));
        updateEntity.setProperty("adminEmail", session.getAttribute("email"));
        updateEntity.setProperty("isRejected", false);
        updateEntity.setProperty("changeRequested", null);

        datastore.put(updateEntity);

        response.sendRedirect("/events.html");
      } catch(EntityNotFoundException e) {
        e.printStackTrace();
        return;
      }
    } else {
      try {
        Entity unapprovedEvent = datastore.get(eventKey);

        String requestedChange = request.getParameter("changes");

        @SuppressWarnings("unchecked") // Cast can't verify generic type.
        Collection<String> changes = (Collection<String>) unapprovedEvent.getProperty("changes");
        if (changes == null) {
          changes = new ArrayList<String>();
        }
        changes.add(requestedChange);

        unapprovedEvent.setProperty("rejected", true);
        unapprovedEvent.setProperty("changes", changes);
        unapprovedEvent.setProperty("edited", false);
        unapprovedEvent.setProperty("admin-email", ((String) session.getAttribute("email")).toLowerCase());

        datastore.put(unapprovedEvent);  

        Entity updateEntity = new Entity("EventUpdate");
        updateEntity.setProperty("id", requestId);
        updateEntity.setProperty("name", unapprovedEvent.getProperty("name"));
        updateEntity.setProperty("owner", unapprovedEvent.getProperty("email"));
        updateEntity.setProperty("adminEmail", session.getAttribute("email"));
        updateEntity.setProperty("isRejected", true);
        updateEntity.setProperty("changeRequested", requestedChange);

        datastore.put(updateEntity);

        response.sendRedirect("/events.html"); 
      } catch(EntityNotFoundException e) {
        e.printStackTrace();
        return;
      }
    }
  }
}
