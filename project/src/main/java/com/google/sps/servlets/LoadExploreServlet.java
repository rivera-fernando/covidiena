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
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpSession;
 
/** Servlet that loads pending events*/
@WebServlet("/load-explore")
public class LoadExploreServlet extends HttpServlet {
 
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Query approvedQuery = new Query("ApprovedEvent");
    Query pastQuery = new Query("PastEvent");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    PreparedQuery approvedResults = datastore.prepare(approvedQuery);
    PreparedQuery pastResults = datastore.prepare(pastQuery);

    List<Entity> approvedResultsList = approvedResults.asList(FetchOptions.Builder.withDefaults());
    List<Entity> pastResultsList = pastResults.asList(FetchOptions.Builder.withDefaults());

    List<Entity> resultsList = new ArrayList<>();

    resultsList.addAll(pastResultsList);
    resultsList.addAll(approvedResultsList);

    int numWeeks = Integer.parseInt(request.getParameter("week"));
    long firstDayOfWeek = getFirstDayOfWeek();
    firstDayOfWeek += 604800000*(numWeeks);

    HttpSession session = request.getSession(false);
    boolean found = false;
    if (session.getAttribute("name") != null) {
        found = true;
    }

    if (found) {
      String email = ((String) session.getAttribute("email")).toLowerCase();
      
      List<Day> week = (List<Day>) addEvents(resultsList, firstDayOfWeek, email);
      
      Gson gson = new Gson();

      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(week));
    } else {
      response.sendRedirect("/login.html");
    }
  }

  private long getFirstDayOfWeek() {
    long today = System.currentTimeMillis();
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.setTimeInMillis(today);
    while (calendar.get(Calendar.DAY_OF_WEEK) > calendar.getFirstDayOfWeek()) {
        calendar.add(Calendar.DATE, -1); // Substract 1 day until first day of week.
    }
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    long firstDayOfWeekTimestamp = calendar.getTimeInMillis();

    return firstDayOfWeekTimestamp;
  }

  private List<Day> addEvents(List<Entity> resultsList, long firstDayOfWeek, String email) {

    List<Day> week = new ArrayList<>();
    List<Event> sundayEvents = new ArrayList<>();
    List<Event> mondayEvents = new ArrayList<>();
    List<Event> tuesdayEvents = new ArrayList<>();
    List<Event> wednesdayEvents = new ArrayList<>();
    List<Event> thursdayEvents = new ArrayList<>();
    List<Event> fridayEvents = new ArrayList<>();
    List<Event> saturdayEvents = new ArrayList<>();

    for (int i = 0; i < resultsList.size(); i++) {

      Entity entity = resultsList.get(i);
      // Load the event only if this user's email matches the email of an attendee
      @SuppressWarnings("unchecked") // Cast can't verify generic type.
      Collection<String> attendees = (Collection<String>) entity.getProperty("attendees");
      if (!attendees.isEmpty()) {
        long id = entity.getKey().getId();
        String name = (String) entity.getProperty("name");
        String location = (String) entity.getProperty("location");
        String date = (String) entity.getProperty("date");
        String time = (String) entity.getProperty("time");
        String description = (String) entity.getProperty("description");
        String type = (String) entity.getProperty("attendance");
        String attendance = (String) entity.getProperty("type");
        String entityType = (String) entity.getProperty("entityType");
        long timestamp = (long) entity.getProperty("timestamp");
        String imageKey = (String) entity.getProperty("imageKey");
        long capacity = (long) entity.getProperty("capacity");
        boolean edited = (boolean) entity.getProperty("edited");
        long dateTimestamp = (long) entity.getProperty("dateTimestamp");
        if (dateTimestamp < firstDayOfWeek - 60000) {
          continue;
        } else if (dateTimestamp >= firstDayOfWeek + 604740000) {
          continue;
        }
        String category = "";
        if ((entityType != null) && (entityType.equals("PastEvent"))) {
          category = "Past";
        } else if (attendees.contains(email)) {
          category = "Upcoming";
        } else {
          category = "Explore";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(dateTimestamp);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Event event = new Event(id, name, location, date, time, description, type, attendance, 
          timestamp, entity.getProperty("email").equals(email), "ApprovedEvent", imageKey, 
          day, attendees.size(), capacity, false, new ArrayList<String>(), "", edited, category,
          new ArrayList<String>());
        
        if (day == 1) {
            sundayEvents.add(event);
        } else if (day == 2) {
            mondayEvents.add(event);
        } else if (day == 3) {
            tuesdayEvents.add(event);
        } else if (day == 4) {
            wednesdayEvents.add(event);
        } else if (day == 5) {
            thursdayEvents.add(event);
        } else if (day == 6) {
            fridayEvents.add(event);
        } else if (day == 7) {
            saturdayEvents.add(event);
        }
      }
    }

    Date saturdayDate = new Date(firstDayOfWeek + 86400000*6);
    Date fridayDate = new Date(firstDayOfWeek + 86400000*5);
    Date thursdayDate = new Date(firstDayOfWeek + 86400000*4);
    Date wednesdayDate = new Date(firstDayOfWeek + 86400000*3);
    Date tuesdayDate = new Date(firstDayOfWeek + 86400000*2);
    Date mondayDate = new Date(firstDayOfWeek + 86400000);
    Date sundayDate = new Date(firstDayOfWeek);

    SimpleDateFormat formatted = new SimpleDateFormat("dd MMM YYYY");

    String saturdayDateString = formatted.format(saturdayDate);
    String fridayDateString = formatted.format(fridayDate);
    String thursdayDateString = formatted.format(thursdayDate);
    String wednesdayDateString = formatted.format(wednesdayDate);
    String tuesdayDateString = formatted.format(tuesdayDate);
    String mondayDateString = formatted.format(mondayDate);
    String sundayDateString = formatted.format(sundayDate);

    Day sunday = new Day("sunday", sundayDateString, sundayEvents);
    Day monday = new Day("monday", mondayDateString, mondayEvents);
    Day tuesday = new Day("tuesday", tuesdayDateString, tuesdayEvents);
    Day wednesday = new Day("wednesday", wednesdayDateString, wednesdayEvents);
    Day thursday = new Day("thursday", thursdayDateString, thursdayEvents);
    Day friday = new Day("friday", fridayDateString, fridayEvents);
    Day saturday = new Day("saturday", saturdayDateString, saturdayEvents);

    week.add(sunday);
    week.add(monday);
    week.add(tuesday);
    week.add(wednesday);
    week.add(thursday);
    week.add(friday);
    week.add(saturday);

    return week;
  }
}
