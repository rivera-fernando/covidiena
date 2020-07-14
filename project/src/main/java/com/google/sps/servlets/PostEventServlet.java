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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.sps.classes.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import java.util.Map;
import com.google.gson.Gson;
import javax.servlet.http.HttpSession;
 
/** Servlet that posts an event*/
@WebServlet("/post-event")
public class PostEventServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    List<Boolean> log = new ArrayList<>();
 
    HttpSession session = request.getSession(false); 
    String name = (String) session.getAttribute("person"); 
 
    boolean found = false;
    
    if (!name.equals("null")) {
      found = true;
    }
    
    log.add(found);
    
    Gson gson = new Gson();
 
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(log));
  }
 
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
 
    // Get the input from the form.
    String name = request.getParameter("name");
    String location = request.getParameter("location");
    String date = request.getParameter("date");
    String time = request.getParameter("time");
    // Format: hh:mm XM
    String hours = time.substring(0, 2);
    String minutes = time.substring(3, 5);
    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd, yyyy");
    long dateTimestamp = 0;
    try {
      Date formattedDate = formatter.parse(date);
      long milliseconds = Long.parseLong(hours)*3600000 + Long.parseLong(minutes)*60000;
      dateTimestamp = formattedDate.getTime() + milliseconds;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    String type = request.getParameter("event-type");
    String attendance = request.getParameter("event-attendance");
    String description = request.getParameter("description");
    String imageKey = getUploadedFileUrl(request, "image");
    long timestamp = System.currentTimeMillis();
 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 
    HttpSession session = request.getSession(false);
    boolean found = false;
    if (session.getAttribute("name") != null) {
        found = true;
    }

    if (found) {
      String email = ((String) session.getAttribute("email")).toLowerCase();
      boolean isAdmin = (boolean) session.getAttribute("admin");

      if (isAdmin) {
        Entity approvedEventEntity = new Entity("ApprovedEvent");
        approvedEventEntity.setProperty("name", name);
        approvedEventEntity.setProperty("location", location);
        approvedEventEntity.setProperty("date", date);
        approvedEventEntity.setProperty("time", time);
        approvedEventEntity.setProperty("type", type);
        approvedEventEntity.setProperty("attendance", attendance);
        approvedEventEntity.setProperty("description", description);
        approvedEventEntity.setProperty("timestamp", timestamp);
        approvedEventEntity.setProperty("email", email);
        approvedEventEntity.setProperty("admin-email", email);
        Collection<String> attendees = new HashSet<String>();
        // Add the central/mock user for cohesion
        attendees.add("mock@mock.edu");
        attendees.add(email);
        approvedEventEntity.setProperty("attendees", attendees);
        approvedEventEntity.setProperty("dateTimestamp", dateTimestamp);
        approvedEventEntity.setProperty("imageKey", imageKey);
 
        datastore.put(approvedEventEntity);
      } else {
        Entity unapprovedEventEntity = new Entity("UnapprovedEvent");
 
        unapprovedEventEntity.setProperty("name", name);
        unapprovedEventEntity.setProperty("location", location);
        unapprovedEventEntity.setProperty("date", date);
        unapprovedEventEntity.setProperty("time", time);
        unapprovedEventEntity.setProperty("type", type);
        unapprovedEventEntity.setProperty("attendance", attendance);
        unapprovedEventEntity.setProperty("description", description);
        unapprovedEventEntity.setProperty("timestamp", timestamp);
        unapprovedEventEntity.setProperty("email", email);
        unapprovedEventEntity.setProperty("dateTimestamp", dateTimestamp);
        unapprovedEventEntity.setProperty("imageKey", imageKey);
 
        datastore.put(unapprovedEventEntity);
      }
 
      response.sendRedirect("/events.html");
    } else {
      response.sendRedirect("/login.html");
    }
  }
 
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);
 
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }
 
    BlobKey blobKey = blobKeys.get(0);
 
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    } else {
      return blobKey.getKeyString();
    }
  }
}
