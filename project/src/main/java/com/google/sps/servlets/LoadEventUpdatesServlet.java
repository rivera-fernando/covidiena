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
import com.google.sps.classes.EventUpdate;
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
@WebServlet("/load-event-updates")
public class LoadEventUpdatesServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("EventUpdate");

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
      List<EventUpdate> updates = new ArrayList<>();
 
      for (int i = 0; i < resultsList.size(); i++) {
 
        Entity entity = resultsList.get(i);
        String ownerEmail = (String) entity.getProperty("owner");

        if (ownerEmail.equals(email)) {
          long id = (long) entity.getProperty("id");
          String name = (String) entity.getProperty("name");
          String adminEmail = (String) entity.getProperty("adminEmail");
          boolean isRejected = (boolean) entity.getProperty("isRejected");
          String changeRequested = (String) entity.getProperty("changeRequested");

          EventUpdate update = new EventUpdate(id, name, email, adminEmail, isRejected, changeRequested);
          updates.add(update);
        }
      }
 
      Gson gson = new Gson();
 
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(updates));
    } else {
      response.sendRedirect("/login.html");
    }
  }
}
