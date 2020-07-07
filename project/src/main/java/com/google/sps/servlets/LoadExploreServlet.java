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
import com.google.sps.classes.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that loads pending events*/
@WebServlet("/load-explore")
public class LoadExploreServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("ApprovedEvent");
    query.addSort("dateTimestamp", SortDirection.DESCENDING);

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);
      List<Entity> resultsList = results.asList(FetchOptions.Builder.withDefaults());
      List<Event> exploreEvents = new ArrayList<>();

      for (int i = 0; i < resultsList.size(); i++) {

        Entity entity = resultsList.get(i);
        long id = entity.getKey().getId();
        String name = (String) entity.getProperty("name");
        String date = (String) entity.getProperty("date");
        String description = (String) entity.getProperty("description");
        String type = (String) entity.getProperty("attendance");
        String attendance = (String) entity.getProperty("type");
        long timestamp = (long) entity.getProperty("timestamp");

        Event event = new Event(id, name, date, description, type, attendance, timestamp, false, false);
        exploreEvents.add(event);
      }

      Gson gson = new Gson();

      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(exploreEvents));
    } else {
      response.sendRedirect("/login.html");
    }
  }
}
