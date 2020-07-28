package com.google.sps.servlets;

import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.classes.PasswordHash;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/*user logs locations they have gone off campus for tracing*/
@WebServlet("/logLocation")
public class LogLocation extends HttpServlet {
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false); 
        String name = request.getParameter("location");
        String longitude = request.getParameter("longitude");
        String latitude = request.getParameter("latitude");
        String date = request.getParameter("date");
        String email = (String) session.getAttribute("email");
        String state = (String) session.getAttribute("state");
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        long dateStamp = 0;
        try {
            Date formattedDate = formatter.parse(date);
            dateStamp = formattedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity locationEntity = new Entity("LocationLog");
        locationEntity.setProperty("name", name);
        locationEntity.setProperty("state", state);
        locationEntity.setProperty("lng", longitude);
        locationEntity.setProperty("lat", latitude);
        locationEntity.setProperty("email", email);
        locationEntity.setProperty("date", dateStamp);

        datastore.put(locationEntity);
        response.sendRedirect("/dashboard");

    }
}
