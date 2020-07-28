package com.google.sps.servlets;

import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.classes.Location;
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
@WebServlet("/load-location")
public class PastLocationServlet extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session.getAttribute("email") != null) {
            Query locationQuery = new Query("LocationLog");
            locationQuery.addSort("date", SortDirection.DESCENDING);
            locationQuery.addFilter("email", FilterOperator.EQUAL, (String) session.getAttribute("email"));
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            PreparedQuery results = datastore.prepare(locationQuery);

            List<Location> locations = new ArrayList<>();
            for(Entity entity:results.asIterable()){
                long locationId = (long) entity.getKey().getId();
                String name = (String) entity.getProperty("name");
                Date dateFormatted = new Date((long) entity.getProperty("date"));
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
                String date = formatter.format(dateFormatted);
                String longitude = (String) entity.getProperty("lng");
                String latitude = (String) entity.getProperty("lat");
                String state = (String) entity.getProperty("state");
                String email = (String) entity.getProperty("email");

                Location location = new Location(locationId, name, email, date, state, longitude, latitude);
                locations.add(location);

            }
            Gson gson = new Gson();
            response.setContentType("application/json;");
            response.getWriter().println(gson.toJson(locations));

        } else {
            response.sendRedirect("/login.html");
        }
    }
}