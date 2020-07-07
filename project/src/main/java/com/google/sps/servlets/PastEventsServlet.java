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
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.Locale;
import java.text.NumberFormat;
import java.text.ParseException;

/** Servlet that updates past events*/
@WebServlet("/update-past-events")
public class PastEventsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query approvedQuery = new Query("ApprovedEvent");
    Query unapprovedQuery = new Query("UnapprovedEvent");
    Query pastQuery = new Query("PastEvent");

    cleanDatastore(approvedQuery, true, false);
    cleanDatastore(unapprovedQuery, false, false);
    cleanDatastore(pastQuery, false, true);

    response.setStatus(200);
  }

  private void cleanDatastore(Query query, boolean isApproved, boolean isPast) {
    query.addSort("dateTimestamp", SortDirection.ASCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Entity> resultsList = results.asList(FetchOptions.Builder.withDefaults());

    long currentTime = System.currentTimeMillis();

    for (int i = 0; i < resultsList.size(); i++) {
      Entity eventEntity = resultsList.get(i);
      long dateTimestamp = (long) eventEntity.getProperty("dateTimestamp");
      if (isPast) {
        if ((currentTime - dateTimestamp) >= 1209600000) {
          // Delete the event entity 
          Key key = eventEntity.getKey();
          datastore.delete(key);
        } else {
          // If the current entity has not passed, as we are sorted in ascending order
          // we know none of the future entities will be passed either.
          break;
        }
      } else {
        if (dateTimestamp <= currentTime) {
          // If the event is passed and approved, create a new PastEvent.
          if (isApproved) {
            Entity pastEventEntity = new Entity("PastEvent");
            pastEventEntity.setPropertiesFrom(eventEntity);
            datastore.put(pastEventEntity);
          }
          // Delete the event entity 
          Key key = eventEntity.getKey();
          datastore.delete(key);
        } else {
          // If the current entity has not passed, as we are sorted in ascending order
          // we know none of the future entities will be passed either.
          break;
        }
      }
    }

  }
}