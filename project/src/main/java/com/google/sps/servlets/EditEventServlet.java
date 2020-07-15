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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
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
import com.google.sps.classes.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;
 
/** Servlet that posts an event*/
@WebServlet("/edit-event")
public class EditEventServlet extends HttpServlet {
 
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the input from the form.
    String name = request.getParameter("edit-name");
    String location = request.getParameter("edit-location");
    String date = request.getParameter("edit-date");
    String time = request.getParameter("edit-time");
    // Format: hh:mm XM
    String hours = time.substring(0, 2);
    String minutes = time.substring(3, 5);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
    long dateTimestamp = 0;
    try {
      Date formattedDate = formatter.parse(date);
      long milliseconds = Long.parseLong(hours)*3600000 + Long.parseLong(minutes)*60000;
      dateTimestamp = formattedDate.getTime() + milliseconds;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    String type = request.getParameter("edit-event-type");
    String attendance = request.getParameter("edit-event-attendance");
    String description = request.getParameter("edit-description");
    String imageKey = getUploadedFileUrl(request, "edit-image");
 
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    String entityType = request.getParameter("event-status");
    long entityId = Long.parseLong(request.getParameter("event-id"));

    Key eventKey = KeyFactory.createKey(entityType, entityId);
    
    try { 
      Entity event = datastore.get(eventKey);

      event.setProperty("name", name);
      event.setProperty("location", location);
      event.setProperty("date", date);
      event.setProperty("time", time);
      event.setProperty("type", type);
      event.setProperty("attendance", attendance);
      event.setProperty("description", description);
      event.setProperty("dateTimestamp", dateTimestamp);
      event.setProperty("edited", true);
      if (imageKey != null) {
        event.setProperty("imageKey", imageKey);
      }
 
      datastore.put(event);
      response.sendRedirect("/events.html");
    } catch(EntityNotFoundException e) {
      return;
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