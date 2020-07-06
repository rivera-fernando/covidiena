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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.classes.Update;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that posts and loads announcemnts*/
@WebServlet("/updates")
public class UpdatesServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserAdmin()) {
      // Get the input from the form.
      String title = request.getParameter("update-title");
      String description = request.getParameter("update-description");
      long timestamp = System.currentTimeMillis();
      String author = userService.getCurrentUser().getNickname();

      Entity updateEntity = new Entity("Update");
      updateEntity.setProperty("title", title);
      updateEntity.setProperty("description", description);
      updateEntity.setProperty("author", author);
      updateEntity.setProperty("timestamp", timestamp);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(updateEntity);
    }

    response.sendRedirect("/index.html");
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Update");
    query.addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Entity> resultsList = results.asList(FetchOptions.Builder.withDefaults());
    List<Update> updates = new ArrayList<>();

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserAdmin()) {
      Update update = new Update(0, "Admin", "", "", 0);
      updates.add(update);
    }

    for (int i = 0; i < resultsList.size(); i++) {

      Entity entity = resultsList.get(i);
      long id = entity.getKey().getId();
      String title = (String) entity.getProperty("title");
      String description = (String) entity.getProperty("description");
      String author = (String) entity.getProperty("author");
      long timestamp = (long) entity.getProperty("timestamp");

      Update update = new Update(id, title, description, author, timestamp);
      updates.add(update);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(updates));
  }
}
