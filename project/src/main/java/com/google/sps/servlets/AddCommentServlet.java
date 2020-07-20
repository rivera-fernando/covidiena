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
import com.google.gson.Gson;
import com.google.sps.classes.Update;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

/** Servlet that posts and loads announcemnts*/
@WebServlet("/comments")
public class AddCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    HttpSession session = request.getSession(false);
    long eventId = (long) session.getAttribute("eventId");
    String eventType = (String) session.getAttribute("eventType");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key eventKey = KeyFactory.createKey(eventType, eventId);
    
    String comment = (String) request.getParameter("comment-body");
    long timestamp = System.currentTimeMillis();

    try { 
      Entity entity = datastore.get(eventKey);

      @SuppressWarnings("unchecked") // Cast can't verify generic type.
        Collection<String> comments = (Collection<String>) entity.getProperty("comments");
      if (comments == null) {
        comments = new ArrayList<String>();
      }
      comments.add(comment);
      entity.setProperty("comments", comments);

      datastore.put(entity);
    } catch(EntityNotFoundException e) {
      return;
    }
  }
}
